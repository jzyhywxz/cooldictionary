package com.zzw.zwm.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zzw.zwm.database.MyDataBaseHelper;

/**
 * �������࣬�����ṩ��ע�ᡢ��¼����ѯ���û�/���ޣ������ޡ�������Щ���ܡ�
 * Գ�ǿ��Բ��մ����д�Լ��Ĵ�������
 * @author zzw
 * @version 1.0
 */
public class MyProcesser {
	private MyDevelopmentKit mdk;			// �������߰�
	private MyDataBaseHelper dbHelper;		// ���ݿ�����
	private SimpleDateFormat mSDF;			// ���ڸ�ʽ
	
	private static final int REGISTER=0;	// ע��
	private static final int SIGN_IN=1;		// ��¼
	private static final int QUERY_COUNT=2;	// ��ѯ������
	private static final int QUERY_USER=3;	// ��ѯ�û�
	private static final int UPDATE_COUNT=4;// ����
	private static final int SHARE_ITEM=5;	// �������
	
	public MyProcesser(MyDevelopmentKit kit){
		mdk=kit;
		dbHelper=new MyDataBaseHelper();
		dbHelper.createUserTable();
		dbHelper.createEnjoyTable();
		dbHelper.createEmailboxTable();
		mSDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public interface MyDevelopmentKit {
		String read(SelectionKey key);
		void write(SelectionKey key, String msg);
	}

	private String getDate(){
		return mSDF.format(new Date());
	}

	public void log(String tag, String msg){
		System.out.println("["+tag+"]["+getDate()+"] "+msg);
	}
	
	public void execute(String sql) throws SQLException{
		dbHelper.execute(sql);
	}
	
	/**
	 * ���û�������д����û��������״̬�����Ϣ�壬
	 * ״̬����û�������б�ʶ�����磺ע�ᡢ��¼�����ޡ���ѯ�û��ȵȣ�
	 * ��Ϣ�����û�����ľ������ݣ����磺ע�ᡢ��¼��Ҫ�û��������룬
	 * ������Ҫ���ʵȵȡ�״̬�����Ϣ��֮����"#"�ָ�����Ϣ֮����"&"�ָ���
	 * @param require
	 * @throws RequireException 
	 * @throws IOException 
	 */
	public void process(SelectionKey key, String require){
		Matcher m=Pattern.compile("#").matcher(require);
		if(!m.find()){
			mdk.write(key, "0#fail");
			return;
		}
		String stateCode=require.substring(0, m.start());
		String msgBody=require.substring(m.end());
		switch(Integer.parseInt(stateCode)){
		case REGISTER:
			register(key, msgBody);
			break;
		case SIGN_IN:
			signIn(key, msgBody);
			break;
		case QUERY_COUNT:
			queryCount(key, msgBody);
			break;
		case QUERY_USER:
			queryUser(key, msgBody);
			break;
		case UPDATE_COUNT:
			updateCount(key, msgBody);
			break;
		case SHARE_ITEM:
			shareItem(key, msgBody);
			break;
		default:break;
		}
	}
	
	/**
	 * ע�����̣����Ȳ�ѯ���ݿ����Ƿ������ͬ�û��������ڣ���ֱ�ӷ���"0#fail"��
	 * �����û��������ݿ⣬������"1#pass"��
	 * @param key
	 * @param msgBody
	 */
	private void register(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select name from user where name='"+info[0]+"';");
			if(!result.next()){
				dbHelper.execute("insert into user values(null,'"+info[0]+"','"+
						info[1]+"',true,'"+getDate()+"');");
				mdk.write(key, "1#pass");
				key.attach(info[0]);
			}
			else{
				mdk.write(key, "0#fail");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��¼���̣����Ȳ�ѯ�û��Ƿ���ڣ��������ڣ���ֱ�ӷ���"0#null"��
	 * ���������룬����ƥ�䣬��ֱ�ӷ���"0#fail"�������������״̬�͵�¼ʱ�䣬
	 * ������"1#pass"��
	 * @param key
	 * @param msgBody
	 */
	private void signIn(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select password,online,date from user "+
							"where name='"+info[0]+"';");
			if(!result.next()){
				mdk.write(key, "0#null");
			}
			else if(info[1].equals(result.getString(1))){
				dbHelper.execute("update user set online=true,date='"+
						getDate()+"' where name='"+info[0]+"';");
				mdk.write(key, "1#pass");
				key.attach(info[0]);
			}
			else{
				mdk.write(key, "0#fail");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ѯ�����������Ȳ�ѯ�����Ƿ���ڣ���������ֱ�ӷ���"1#0&0&0"��
	 * ����ֱ�ӷ���"1#number&number&number"��
	 * @param key
	 * @param msgBody
	 */
	private void queryCount(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select * from enjoy where word='"+info[0]+"';");
			if(!result.next()){
				mdk.write(key, "1#0&0&0");
			}
			else{
				mdk.write(key, "1#"+result.getString(2)+"&"+
						result.getString(3)+"&"+result.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * ��ѯ�û�����Ϊ�����ѯ���ض���ѯ�������ѯ�����û��ṩ����������в�ѯ��
	 * �ض���ѯ�����û��ṩ���û������в�ѯ�������ѯʱ�����ɹ��򷵻�
	 * "1#seed&name,online&...&name,online"�����򷵻�"0#null"��"0#fail"��
	 * �ض���ѯʱ�����ɹ��򷵻�"1#online"�����򷵻�"0#null"��
	 * @param key
	 * @param msgBody
	 */
	private void queryUser(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			if(info[0].equals("id")){
				ResultSet result=dbHelper.executeReturnResult(
						"select max(id) from user;");
				if(!result.next()){
					mdk.write(key, "0#null");
					return;
				}
				int next=new Random(Integer.parseInt(info[1])).
						nextInt(result.getInt(1))+1;
				result=dbHelper.executeReturnResult("select name,online from user "+
						"where id between "+next+" and "+(next+10)+";");
				if(!result.next()){
					mdk.write(key, "0#fail");
				}
				else{
					StringBuilder sb=new StringBuilder("1#");
					sb.append(next);
					do{
						sb.append("&").append(result.getString(1)).
						append(",").append(result.getString(2));
					}while(result.next());
					mdk.write(key, sb.toString());
				}
			}
			else if(info[0].equals("name")){
				ResultSet result=dbHelper.executeReturnResult(
						"select online from user where name='"+info[1]+"';");
				if(!result.next()){
					mdk.write(key, "0#null");
				}
				else{
					mdk.write(key, "1#"+result.getString(1));
				}
			}
			else{
				mdk.write(key, "0#fail");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �������̣����Ȳ�ѯ�����Ƿ���ڣ�����������£�����������ݿ⡣
	 * �ɹ�����"1#pass"��ʧ�ܷ���"0#fail"��
	 * @param key
	 * @param msgBody
	 */
	private void updateCount(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select word from enjoy where word='"+info[0]+"';");
			if(!result.next()){
				dbHelper.execute("insert into enjoy values('"+
						info[0]+"',"+info[1]+","+info[2]+","+info[3]+");");
			}
			else{
				dbHelper.execute("update enjoy set youdao=youdao+"+info[1]+
						",bing=bing+"+info[2]+",baidu=baidu+"+info[3]+
						" where word='"+info[0]+"';");
			}
			mdk.write(key, "1#pass");
		} catch (SQLException e) {
			e.printStackTrace();
			mdk.write(key, "0#fail");
		}
	}
	
	/**
	 * ������������Ƚ������������ݿ⣬���´�selectʱ�����ÿ���û��Ƿ��д����յĴ�����
	 * �������ͣ����������������ͳɹ������ѷ����߷��ͳɹ���ʧ���򷵻�"0#fail&error"��
	 * @param key
	 * @param msgBody
	 */
	private void shareItem(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			dbHelper.execute("insert into emailbox values('"+info[0]+"','"+info[1]+
					"','"+info[2]+"','"+getDate()+"');");
		} catch (SQLException e) {
			e.printStackTrace();
			mdk.write(key, "0#fail&"+e.getMessage());
		}
	}
	
	/**
	 * �Դ������зַ������ȶ������û�����ʶ�������/��Ϊ�����ߣ�
	 * ������/�����ʹ�����������Ϻ�����ݿ���ɾ�������ѷ��͵Ĵ�����
	 * @param selector
	 */
	public void dispatchItem(Selector selector){
		Set<SelectionKey> set=selector.keys();
		if(set==null || set.size()<=0)
			return;
		for(SelectionKey sk : set){
			if(!sk.isValid())
				continue;
			Object o=sk.attachment();
			if(o==null || o.toString().contains("."))
				continue;
			try {
				ResultSet res=dbHelper.executeReturnResult(
						"select send,word,date from emailbox "+
								"where receive='"+o+"';");
				if(res.next()){
					StringBuilder sb=new StringBuilder("1#mail");
					do{
						sb.append("&").append(res.getString(1)).
						append(",").append(res.getString(2)).
						append(",").append(res.getString(3));
					}while(res.next());
					mdk.write(sk, sb.toString());
					dbHelper.execute("delete from emailbox "+
							"where receive='"+o+"';");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

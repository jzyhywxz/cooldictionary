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
	private static final int RECEIVE_ITEM=6;// �յ�����
	
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
	 * ���û�������д����û�����������������Ϣ�壬
	 * ��������û�������б�ʶ�����磺ע�ᡢ��¼�����ޡ���ѯ�ȣ�
	 * ��Ϣ������û��ľ����������磺ע�ᡢ��¼��Ҫ�û���������ȡ�
	 * ���������Ϣ��֮����"#"�ָ�����Ϣ֮����"&"�ָ���
	 * ��������󣬷����������û�����Ӧ���ģ����������롢״̬���
	 * ��Ϣ�壬�������Ӧ�û�����״̬���ʶ�Ƿ�ɹ�����Ϣ�����
	 * ��������ݡ������롢״̬�����Ϣ��֮����"#"�ָ�����Ϣ֮��
	 * ��"&"�ָ���
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
	 * ע������
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
				mdk.write(key, REGISTER+"#1#ע��ɹ�&"+info[0]);
				key.attach(info[0]);
			}
			else{
				mdk.write(key, REGISTER+"#0#�û��Ѵ���");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��¼����
	 * @param key
	 * @param msgBody
	 */
	private void signIn(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select password from user "+
							"where name='"+info[0]+"';");
			if(!result.next()){
				mdk.write(key, SIGN_IN+"#0#�û�������");
			}
			else if(info[1].equals(result.getString(1))){
				dbHelper.execute("update user set online=true,date='"+
						getDate()+"' where name='"+info[0]+"';");
				mdk.write(key, SIGN_IN+"#1#��¼�ɹ�&"+info[0]);
				key.attach(info[0]);
			}
			else{
				mdk.write(key, SIGN_IN+"#0#�������");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ѯ������
	 * @param key
	 * @param msgBody
	 */
	private void queryCount(SelectionKey key, String msgBody){
		String[] info=msgBody.split("&");
		try {
			ResultSet result=dbHelper.executeReturnResult(
					"select * from enjoy where word='"+info[0]+"';");
			if(!result.next()){
				mdk.write(key, QUERY_COUNT+"#1#0&0&0");
			}
			else{
				mdk.write(key, QUERY_COUNT+"#1#"+result.getString(2)+"&"+
						result.getString(3)+"&"+result.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * ��ѯ�û�
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
					mdk.write(key, QUERY_USER+"#0#�û�������");
					return;
				}
				int next=new Random(Integer.parseInt(info[1])).
						nextInt(result.getInt(1))+1;
				result=dbHelper.executeReturnResult("select name,online from user "+
						"where id between "+next+" and "+(next+10)+";");
				if(!result.next()){
					mdk.write(key, QUERY_USER+"#0#��ѯʧ��");
				}
				else{
					StringBuilder sb=new StringBuilder();
					sb.append(QUERY_USER).append("#1#").append(next);
					do{
						sb.append("&").append(result.getString(1)).
						append(",").append(result.getString(2));
					}while(result.next());
					mdk.write(key, sb.toString());
				}
			}
			else if(info[0].equals("all")){
				ResultSet result=dbHelper.executeReturnResult(
						"select name,online from user;");
				if(!result.next()){
					mdk.write(key, QUERY_USER+"#0#�û�������");
				}
				else{
					StringBuilder sb=new StringBuilder();
					sb.append(QUERY_USER).append("#1#");
					do{
						sb.append(result.getString(1)).append(",").
						append(result.getString(2)).append("&");
					}while(result.next());
					sb.deleteCharAt(sb.length()-1);
					mdk.write(key, sb.toString());
				}
			}
			else if(info[0].equals("name")){
				ResultSet result=dbHelper.executeReturnResult(
						"select online from user where name='"+info[1]+"';");
				if(!result.next()){
					mdk.write(key, QUERY_USER+"#0#�û�������");
				}
				else{
					mdk.write(key, QUERY_USER+"#1#"+result.getString(1));
				}
			}
			else{
				mdk.write(key, QUERY_USER+"#0#��ѯʧ��");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������
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
						",bing=bing+"+info[2]+",jinshan=jinshan+"+info[3]+
						" where word='"+info[0]+"';");
			}
			mdk.write(key, UPDATE_COUNT+"#1#���޳ɹ�");
		} catch (SQLException e) {
			e.printStackTrace();
			mdk.write(key, UPDATE_COUNT+"#0#����ʧ��");
		}
	}
	
	/**
	 * �������
	 * @param key
	 * @param msgBody
	 */
	private void shareItem(SelectionKey key, String msgBody){
		// info[0] ����������
		// info[1] ����ĵ���
		// info[2] ���ʵķ���
		// info[3] �����߼���
		String[] info=msgBody.split("&");
		String[] rece=info[3].split(",");
		String date=getDate();
		try {
			for(String re:rece)
				dbHelper.execute("insert into emailbox values('"+
						info[0]+"','"+re+"','"+info[1]+"','"+info[2]+"','"+date+"');");
			mdk.write(key, SHARE_ITEM+"#1#����ɹ�");
		} catch (SQLException e) {
			e.printStackTrace();
			mdk.write(key, SHARE_ITEM+"#0#����ʧ��");
		}
	}
	
	/**
	 * �Դ������зַ�
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
						"select send,word,content,date from emailbox "+
								"where receive='"+o+"';");
				if(res.next()){
					StringBuilder sb=new StringBuilder();
					sb.append(RECEIVE_ITEM).append("#1#");
					do{
						sb.append(res.getString(1)).append("^").
						append(res.getString(2)).append("^").
						append(res.getString(3)).append("^").
						append(res.getString(4)).append("&");
					}while(res.next());
					sb.deleteCharAt(sb.length()-1);
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

package com.zzw.zwm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * ������������NIO��ʽʵ�ֵĵ��̶߳�ͻ��ķ������ˣ�
 * �����˿�����ά�����л��̵߳Ķ��⿪��������ʵ����
 * ������ͨ�ź�I/O��
 * @author zzw
 * @version 1.0
 */
public class MyServer implements MyProcesser.MyDevelopmentKit {
	private Selector mSelector;				// ѡ����
	private ServerSocketChannel mChannel;	// ͨ��
	private MyProcesser mProcesser;			// ������
	private static MyServer mInstance;		// ��̬ʵ��
	
	public static MyServer getInstance() throws IOException {
		if(mInstance==null){
			synchronized(MyServer.class){
				if(mInstance==null)
					mInstance=new MyServer();
			}
		}
		return mInstance;
	}
	
	/**
	 * MyServer��Ĺ��췽�����ڴ˷����д�ѡ���Ӻ�ͨ����
	 * ����ͨ�����óɷ�����̬���󶨶˿ں�ע��ACCEPT�¼���
	 * ���⻹��Դ��������г�ʼ����
	 * @throws IOException
	 */
	private MyServer() throws IOException {
		mSelector=Selector.open();
		mChannel=ServerSocketChannel.open();
		mChannel.socket().setReuseAddress(true);
		mChannel.bind(new InetSocketAddress(7999));
		mChannel.configureBlocking(false);
		mChannel.register(mSelector, SelectionKey.OP_ACCEPT);
		mProcesser=new MyProcesser(this);
	}
	
	/**
	 * ��ʼ�����ڴ˹����У����������ϼ������Կͻ������󣬲�����������
	 * ���������д�����ÿ��ѭ����ʼ�׶Σ��������ַ����п��Էַ��Ĵ�����
	 */
	public void start(){
		SelectionKey key=null;
		try {
			while(true){
				mProcesser.dispatchItem(mSelector);
				if(mSelector.select()<=0)
					continue;
				Iterator<SelectionKey> iterator=mSelector.
						selectedKeys().iterator();
				while(iterator.hasNext()){
					key=iterator.next();
					iterator.remove();
					// �������ӽ���
					if(key.isAcceptable()){
						accept(key);
					}
					// �û����󵽴�
					else if(key.isReadable()){
						String require=read(key);
						/**
						 * �������������û������û����󱻳�ȡ�������ɴ���������
						 * ��ͬ�Ĵ���������C/S���������ݸ�ʽ���ṩ��ͬ�Ĵ������̣�
						 * ���еĴ���ϸ�ڶ�����װ�ڴ������У�������ֻ�����ṩ
						 * process()������
						 */
						if(require!=null)
							mProcesser.process(key, require);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				mSelector.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * �������ӽ������˽׶λ�δ���������û������󣬶�ֻ�ǽ�����TCP���ӣ�
	 * ��Ҫ��ȡͨ��ͨ������������Ϊ������̬�������Ҫ�ڴ�ͨ����ע��READ
	 * �¼��������û�����
	 * @param key
	 */
	public void accept(SelectionKey key){
		SocketChannel sc=null;
		try {
			sc=((ServerSocketChannel)(key.channel())).accept();
			String remoteAddress=sc.getRemoteAddress().toString();
			mProcesser.log("CONNECT", remoteAddress);
			sc.configureBlocking(false);
			sc.register(mSelector, SelectionKey.OP_READ).attach(remoteAddress);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			try {
				if(sc!=null)
					sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			key.cancel();
		}
	}
	
	/**
	 * �ӿͻ��˶�ȡ����
	 */
	synchronized public String read(SelectionKey key){
		SocketChannel sc=(SocketChannel)key.channel();
		ByteBuffer bb=ByteBuffer.allocate(10240);
		try {
			sc.read(bb);
			bb.flip();
			CharBuffer cb=Charset.forName("UTF-8").decode(bb);
			//System.out.println("*"+cb.toString().trim()+"*");
			return cb.toString().trim();
			//return new String(bb.array()).trim();
		} catch (IOException e) {
			try {
				if(!key.attachment().toString().contains(".")){
					mProcesser.execute("update user set online=false where name='"+
							key.attachment()+"';");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			mProcesser.log(" BREAK ", key.attachment().toString());
			key.cancel();
			return null;
		}
	}
	
	/**
	 * ��ͻ��˷�������
	 */
	public synchronized void write(SelectionKey key, String msg){
		SocketChannel sc=(SocketChannel)key.channel();
		try {
			sc.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
		} catch (IOException e) {
			try {
				if(!key.attachment().toString().contains(".")){
					mProcesser.execute("update user set online=false where name='"+
							key.attachment()+"';");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			mProcesser.log(" BREAK ", key.attachment().toString());
			key.cancel();
		}
	}
	
	
	public static class Test{
		public static void main(String[] args){
			try {
				MyServer.getInstance().start();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("����������ʧ�ܣ�");
			}
		}
	}

}

package com.zzw.zwm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MyServer {
	private Selector mSelector;				// ѡ����
	private ServerSocketChannel mChannel;	// ͨ��
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
	 * @throws IOException
	 */
	private MyServer() throws IOException {
		mSelector=Selector.open();
		mChannel=ServerSocketChannel.open();
		mChannel.socket().setReuseAddress(true);
		mChannel.bind(new InetSocketAddress(7999));
		mChannel.configureBlocking(false);
		mChannel.register(mSelector, SelectionKey.OP_ACCEPT);
	}
	
	public void start(){
		SelectionKey key=null;
		while(true){
			try {
				if(mSelector.select()<=0)
					continue;
				Iterator<SelectionKey> iterator=mSelector.
						selectedKeys().iterator();
				while(iterator.hasNext()){
					key=iterator.next();
					iterator.remove();
					if(key.isAcceptable()){
						accept(key);
					}
					else if(key.isReadable()){
						String msg=read(key);
						// TODO �����û������ѯ���ݿ�
					}
				}
			} catch (IOException e) {
				System.err.println("[ ERROR ] ѡ���¼�ʱ�������󣬷�������ֹ��");
				e.printStackTrace();
				try {
					mSelector.close();
					Thread.currentThread().interrupt();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void accept(SelectionKey key){
		SocketChannel sc=null;
		try {
			sc=((ServerSocketChannel)(key.channel())).accept();
			String remoteAddress=sc.getRemoteAddress().toString();
			System.out.println("[CONNECT] "+remoteAddress);
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
	
	public String read(SelectionKey key){
		SocketChannel sc=(SocketChannel)key.channel();
		ByteBuffer bb=ByteBuffer.allocate(1024);
		try {
			sc.read(bb);
			String msg=new String(bb.array()).trim();
			System.out.println("[RECEIVE] "+key.attachment()+" "+msg);
			sc.write(ByteBuffer.wrap(("[ECHO] "+msg).getBytes()));
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[ BREAK ] "+key.attachment());
			key.cancel();
			return null;
		}
	}
	
	public static class Test{
		public static void main(String[] args){
			try {
				MyServer.getInstance().start();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("[ ERROR ] ����������ʧ�ܣ�");
			}
		}
	}
}

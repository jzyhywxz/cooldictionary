package com.zzw.zwm.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MyClient {
	// ��������ַ�Ͷ˿�
	private static final String SERVER_ADDRESS="localhost";
	private static final int PORT=7999;
	
	private Selector mSelector=null;	// ѡ����
	private SocketChannel mChannel=null;// ����ͨ��
	private Thread mServerThread=null;	// �����������߳�
	
	private SelectionKey mKey=null;		// ���Ӿ��
	private Semaphore mSemaphore=new Semaphore(0);
	
	public MyClient() throws IOException {
		// TODO ���岢��ʾͼ���û�����
		
		// ����������
		mSelector=Selector.open();
		mChannel=SocketChannel.open();
		mChannel.configureBlocking(false);
		mChannel.register(mSelector, SelectionKey.OP_CONNECT);
		mChannel.connect(new InetSocketAddress(SERVER_ADDRESS, PORT));

		mServerThread=new Thread(){
			@Override
			public void run(){
				try {
					while(!isInterrupted()){
						if(mSelector.select()<=0)
							continue;
						Iterator<SelectionKey> iterator=mSelector.
								selectedKeys().iterator();
						while(iterator.hasNext()){
							SelectionKey key=iterator.next();
							iterator.remove();
							if(key.isConnectable()){
								connect(key);
							}
							else if(key.isReadable()){
								String msg=read(key);
								// TODO ����������Է����������ݣ�
								// ������Щ���ݸ���ͼ���û����档
								if(msg!=null)
									System.out.println(msg);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						mChannel.close();
						mSelector.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					interrupt();
				}
			}
		};
		mServerThread.start();
		
		// TODO �û����룬�ͻ��˺ͷ�����ͨ��
		Scanner in=new Scanner(System.in);
		while(in.hasNext()){
			String msg=in.nextLine();
			if(msg!=null)
				write(msg);
		}
		in.close();
	}
	
	void connect(SelectionKey key){
		SocketChannel sc=(SocketChannel)key.channel();
		try {
			if(sc.isConnectionPending())
				sc.finishConnect();
			sc.configureBlocking(false);
			mKey=sc.register(mSelector, SelectionKey.OP_READ);
			mSemaphore.release();
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			try {
				mChannel.close();
				mSelector.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			mServerThread.interrupt();
		}
	}
	
	String read(SelectionKey key){
		SocketChannel sc=(SocketChannel)key.channel();
		ByteBuffer bb=ByteBuffer.allocate(1024);
		try {
			sc.read(bb);
			bb.flip();
			CharBuffer cb=Charset.forName("UTF-8").decode(bb);
			//System.out.println("*"+cb.toString().trim()+"*");
			return cb.toString().trim();
			//return new String(bb.array()).trim();
		} catch (IOException e) {
			// TODO ֪ͨ�û����ӶϿ�����ͼ���û�������ʾ
			System.err.println("[ BREAK ] ��������������ѶϿ���");
			e.printStackTrace();
			key.cancel();
			try {
				mChannel.close();
				mSelector.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			mServerThread.interrupt();
			return null;
		}
	}
	
	void write(String msg){
		// ��֤�����������������֮ǰ���Ѿ������������֮������ӡ�
		try {
			if(mKey==null)
				mSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SocketChannel sc=(SocketChannel)mKey.channel();
		try {
			sc.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
		} catch (IOException e) {
			// TODO ֪ͨ�û���������������ѶϿ�����ͼ���û�������ʾ
			System.err.println("[ BREAK ] ��������������ѶϿ���");
			e.printStackTrace();
			mKey.cancel();
			try {
				mChannel.close();
				mSelector.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			mServerThread.interrupt();
		}
	}
	
	public static class Test{
		public static void main(String[] args){
			try {
				new MyClient();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("���ӷ�����ʧ�ܣ�");
			}
		}
	}
}

package com.zzw.zwm.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpConnectionHelper {
	private HttpConnectionHelper(){}
	
	/**
	 * this method will invoke the get method of http protocol 
	 * to obtain the translation of some words from Internet.
	 * @param authority	- URL's authority, include "http[s]://"
	 * @param path		- URL's path
	 * @param query		- URL's query
	 * @param encoding	- encoding format
	 * @param cr		- the special content reader, 
	 * 					  which decides what content to read.
	 */
	public static void get(String authority, String path, 
			LinkedHashMap<String,String> query, String encoding, 
			ContentReader cr) {
		// URL
		URL url=null;
		// connection
		HttpURLConnection connection=null;
		// buffered reader
		BufferedReader reader=null;
		
		try {
			url=getURL(authority, path, query);
		} catch (AuthorityNullException e) {
			System.err.println(e.getMessage());
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			connection=(HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			reader=new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));
			String next;
			// -- read content starting --
			/* �������ȡ��������ķ��룬���ڲ�ͬ��վ�ṩ��HTML�ļ����в�ͬ
			 * �ĸ�ʽ�������е�http������ͬ�����ӷ��������ϣ������ｫ��ȡ��
			 * ���巽ʽ��ȡ������װ��ContentReader���У������ÿһ�н���
			 * ��Ӧ���жϣ�������ҪԳ���Լ�ʵ�֣���ֻ�з���Ҫ����вŻᱻ����
			 * ��ȡ��һ����ȡ��ɣ�ContentReader�����isFinished()�᷵
			 * ��true�����´��жϽ�����ѭ��������ο������Ķ����ĵ�Content-
			 * Reader�����࣬������com.zzw.zwm.util.ContentReader-
			 * Factory�����ṩ�����־���ʵ���ࡣ
			 */
			cr.skip(reader);
			while(!cr.isFinished() && 
				  (next=cr.readNext(reader))!=null){
				cr.append(next.trim());
			}
			// -- read content finished --
		} catch (IOException e) {
			System.err.println("http connection IO exception!");
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null)
					reader.close();
				} catch (IOException e) {
					System.err.println("reader close failed!");
					e.printStackTrace();
				}
			if(connection!=null)
				connection.disconnect();
		}
	}

	/**
	 * this method will create a url instance 
	 * with these parameters.
	 * @param auth	- URL's authority, include "http[s]://"
	 * @param path	- URL's path
	 * @param query	- URL's query
	 * @return a special URL instance
	 * @throws AuthorityNullException: throw when authority is null
	 * @throws MalformedURLException : throw by URL construction
	 */
	public static URL getURL(String auth, String path, 
			LinkedHashMap<String,String> query) throws 
	AuthorityNullException, MalformedURLException {
		if(auth==null)
			throw new AuthorityNullException("authority is null!");
		
		StringBuilder sb=new StringBuilder();
		sb.append(auth);
		if(path!=null){
			if(path.startsWith("/")){
				if(sb.charAt(sb.length()-1)=='/')
					sb.append(path.substring(1, path.length()-1));
				else
					sb.append(path);
			}
			else
				sb.append('/').append(path);
		}
		if(query!=null){
			sb.append('?');
			for(Map.Entry<String, String> e:query.entrySet())
				sb.append(e.getKey()).append('=').
				append(e.getValue()).append('&');
			sb.deleteCharAt(sb.length()-1);
		}
			return new URL(sb.toString());
	}
	
	/**
	 * ContentReader will decide what line to append. 
	 * You must override the "append()" method and 
	 * change the "isFinished" field to "true" in this method!
	 * @author zzw
	 */
	public static abstract class ContentReader {
		protected StringBuilder content;	// �ı�����
		protected boolean isFinish;			// �Ƿ��ȡ���
		protected Pattern head;		// HTML��ǿ�ͷ(<a ...>)
		protected Pattern tail;		// HTML��ǽ�β(</a>��.../>)
		
		public ContentReader(){
			content=new StringBuilder();
			isFinish=false;
			head=Pattern.compile("<\\w+");
			tail=Pattern.compile("<?/\\w*>");
		}
		
		public abstract long getCount();
		
		/* ����������������ٱ���������ʱ�������ץȡ����ҳ��ͷ��
		 * ����������Ϣ�����綨�����style������ʱ�����ʵ��skip()
		 * ������������ҳ��ͷ��һ�����ݡ�
		 */
		public abstract long skip(BufferedReader reader);
		
		/* ���ַ������������У������ַ���String����
		 */
		public String readBuffer(BufferedReader reader){
			if(reader==null)
				return null;
			
			CharBuffer cb=CharBuffer.allocate(1024);
			try {
				if(reader.read(cb)>0){
					String str=cb.flip().toString();
					if(str.endsWith("<") || str.endsWith("/")){
						return str+(char)reader.read();
					}
					else
						return str;
				}
				else
					return null;
			} catch (IOException e) {
				System.err.println("read buffer failed!");
				e.printStackTrace();
				return null;
			}
		}
		
		/* ��ȡһ�У������ַ���String����
		 */
		public String readLine(BufferedReader reader){
			if(reader==null)
				return null;
			
			try {
				return reader.readLine();
			} catch (IOException e) {
				System.err.println("read line failed!");
				e.printStackTrace();
				return null;
			}
		}
		
		/* �����������ζ�ȡ�������ṩ�����ֶ�ȡ��ʽ ���� 
		 * readBuffer()��readLine()��ǰ��ÿ�ν��ַ�
		 * ���뻺�����У�����ÿ�ζ�ȡһ�С�
		 */
		public abstract String readNext(BufferedReader reader);
		
		/* �����������ȡʲô����Ҫ���ǻ�Ҫ�����ｫ
		 * isFinished��Ϊtrue��
		 */
		public abstract void append(String s);
		
		public String getContent(){
			return content.toString();
		}
		
		public boolean isFinished(){
			return isFinish;
		}
		
		public void reset(){
			isFinish=false;
			content=new StringBuilder();
		}
	}
	
	/**
	 * this class extends Exception.
	 * @author zzw
	 */
	@SuppressWarnings("serial")
	public static class AuthorityNullException extends Exception {
		public AuthorityNullException(String e){
			super(e);
		}
	}
}

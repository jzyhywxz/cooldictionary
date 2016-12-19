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

/**
 * HTTP连接协助者，此类将协助使用者从网络上抓取相关网页，
 * 并通过相应的内容读取器读取需要的内容。
 * @author zzw
 * @version 1.0
 */
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
			/* 在这里读取来自网络的翻译，由于不同网站提供的HTML文件具有不同
			 * 的格式，但所有的http都有相同的连接方法（见上）。这里将读取的
			 * 具体方式抽取出来封装到ContentReader类中，它会对每一行进行
			 * 相应的判断（这里需要猿们自己实现），只有符合要求的行才会被真正
			 * 读取！一旦读取完成，ContentReader对象的isFinished()会返
			 * 回true，则下次判断将跳出循环。具体参考可以阅读下文的Content-
			 * Reader抽象类，另外在com.zzw.zwm.util.ContentReader-
			 * Factory类中提供了三种具体实现类。
			 */
			cr.skip(reader);
			while(!cr.isFinished() && 
				  (next=cr.readNext(reader))!=null){
				cr.append(next.trim());
			}
			// -- read content finished --
		} catch (IOException e) {
			//System.err.println("http connection IO exception!");
			//e.printStackTrace();
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
	 * @version 1.0
	 */
	public static abstract class ContentReader {
		protected StringBuilder content;	// 文本内容
		protected boolean isFinish;			// 是否读取完成
		protected Pattern head;		// HTML标记开头(<a ...>)
		protected Pattern tail;		// HTML标记结尾(</a>或.../>)
		
		public ContentReader(){
			content=new StringBuilder();
			isFinish=false;
			head=Pattern.compile("<\\w+");
			tail=Pattern.compile("<?/\\w*>");
		}
		
		/* 在这里决定跳过多少比特数。有时候回遇到抓取的网页开头有
		 * 大量冗余信息（比如定义各种style），这时候可以实现skip()
		 * 方法来跳过网页开头的一段内容。
		 */
		public abstract long skip(BufferedReader reader);
		
		/* 将字符读进缓冲区中，返回字符的String对象。
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
		
		/* 读取一行，返回字符的String对象。
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
		
		/* 在这里决定如何读取，这里提供了两种读取方式 —— 
		 * readBuffer()和readLine()，前者每次将字符
		 * 读入缓冲区中，后者每次读取一行。
		 */
		public abstract String readNext(BufferedReader reader);
		
		/* 在这里决定读取什么，不要忘记还要在这里将
		 * isFinished改为true！
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

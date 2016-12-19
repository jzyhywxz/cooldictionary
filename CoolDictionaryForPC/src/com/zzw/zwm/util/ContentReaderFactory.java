package com.zzw.zwm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zzw.zwm.network.HttpConnectionHelper.ContentReader;

/**
 * ���ݶ�ȡ��������ͨ���˹������Եõ������е�����Ӧ���ٶ�����
 * ��ҳ�����ݶ�ȡ������ͨ���ض��Ķ�ȡ������ȡ��Ӧ��ҳ�ķ������ݡ�
 * @author zzw
 * @version 1.0
 */
public class ContentReaderFactory {
	private ContentReaderFactory(){}
	
	// "<h2 class=\"wordbook-js\">"
	public static ContentReader getYoudaoContentReader(
			String tab, int seg){
		return new SimpleContentReader(tab, seg){
			@Override
			public String readNext(BufferedReader reader) {
				return readLine(reader);
			}
			@Override
			public long skip(BufferedReader reader) {
				try {
					return reader.skip(5500);
				} catch (IOException e) {
					System.err.println("reader skip failed!");
					e.printStackTrace();
					return -1;
				}
			}
			@Override
			public String getKeyword() {
				Matcher kwmhead=Pattern.compile("<span class=\"keyword\">").matcher(content);
				Matcher kwmtail=Pattern.compile("</span>").matcher(content);
				if((!kwmhead.find()) || (!kwmtail.find()))
					return "";
				int start=kwmhead.end();
				int end=kwmtail.start();
				return content.substring(start, end);
			}
			@Override
			public String getTranslation() {
				// ��һ��
				Matcher trmhead=Pattern.compile("<ul>").matcher(content);
				Matcher trmtail=Pattern.compile("</ul>").matcher(content);
				if((!trmhead.find()) || (!trmtail.find()))
					return "";
				int start=trmhead.end();
				int end=trmtail.start();
				String trans=content.substring(start, end);
				// �ڶ���
				StringBuilder sb=new StringBuilder();
				trmhead=Pattern.compile("<li>").matcher(trans);
				trmtail=Pattern.compile("</li>").matcher(trans);
				while(trmhead.find() && trmtail.find()){
					sb.append(trans.substring(trmhead.end(), trmtail.start())).
					append("\n");
				}
				if(sb.length()>0)
					sb.deleteCharAt(sb.length()-1);
				return sb.toString();
			}
		};
	}
	
	// "<div class=\"hd_area\">"
	public static ContentReader getBingContentReader(
			String tab, int seg){
		return new SimpleContentReader(tab, seg){
			@Override
			public String readNext(BufferedReader reader) {
				return readLine(reader);
			}
			@Override
			public long skip(BufferedReader reader) {
				try {
					return reader.skip(66000);
				} catch (IOException e) {
					System.err.println("reader skip failed!");
					e.printStackTrace();
					return -1;
				}
			}
			@Override
			public String getKeyword() {
				Matcher kwmhead=Pattern.compile("<strong>").matcher(content);
				Matcher kwmtail=Pattern.compile("</strong>").matcher(content);
				if((!kwmhead.find()) || (!kwmtail.find()))
					return "";
				int start=kwmhead.end();
				int end=kwmtail.start();
				return content.substring(start, end);
			}
			@Override
			public String getTranslation() {
				// ��һ��
				Matcher trmhead=Pattern.compile("<ul>").matcher(content);
				Matcher trmtail=Pattern.compile("</ul>").matcher(content);
				if((!trmhead.find()) || (!trmtail.find()))
					return "";
				int start=trmhead.end();
				int end=trmtail.start();
				String trans=content.substring(start, end).
						replaceAll("</?span[^>]*>", "");
				// �ڶ���
				StringBuilder sb=new StringBuilder();
				trmhead=Pattern.compile("<li>").matcher(trans);
				trmtail=Pattern.compile("</li>").matcher(trans);
				while(trmhead.find() && trmtail.find()){
					sb.append(trans.substring(trmhead.end(), trmtail.start())).
					append("\n");
				}
				if(sb.length()>0)
					sb.deleteCharAt(sb.length()-1);
				return sb.toString();
			}
		};
	}
	
	// "<div id=\"simple_means\""
	public static ContentReader getBaiduContentReader(
			String tab, int seg){
		return new SimpleContentReader(tab, seg){
			@Override
			public String readNext(BufferedReader reader) {
				return readLine(reader);
			}
			@Override
			public long skip(BufferedReader reader) {
				try {
					return reader.skip(3500);
				} catch (IOException e) {
					System.err.println("reader skip failed!");
					e.printStackTrace();
					return -1;
				}
			}
			@Override
			public String getKeyword() {
				return "";
			}
			@Override
			public String getTranslation() {
				// ��һ��
				Matcher trmhead=Pattern.compile("<div>").matcher(content);
				Matcher trmtail=Pattern.compile("</div>").matcher(content);
				if((!trmhead.find()) || (!trmtail.find(trmhead.end())))
					return "";
				int start=trmhead.end();
				int end=trmtail.start();
				String trans=content.substring(start, end).replaceAll("</?s\\w+>", "");
				// �ڶ���
				StringBuilder sb=new StringBuilder();
				trmhead=Pattern.compile("<p>").matcher(trans);
				trmtail=Pattern.compile("</p>").matcher(trans);
				while(trmhead.find() && trmtail.find()){
					sb.append(trans.substring(trmhead.end(), trmtail.start())).
					append("\n");
				}
				if(sb.length()>0)
					sb.deleteCharAt(sb.length()-1);
				return sb.toString();
			}
		};
	}
	
	public static ContentReader getJinshanContentReader(
			String tab, int seg){
		return new SimpleContentReader(tab, seg){
			@Override
			public String getKeyword() {
				Matcher kwmhead=Pattern.compile("<h1 class=\"keyword\">").matcher(content);
				Matcher kwmtail=Pattern.compile("</h1>").matcher(content);
				if((!kwmhead.find()) || (!kwmtail.find()))
					return "";
				int start=kwmhead.end();
				int end=kwmtail.start();
				return content.substring(start, end).trim();
			}
			@Override
			public String getTranslation() {
				// ��һ��
				Matcher trmhead=Pattern.compile("<ul[^>]*>").matcher(content);
				Matcher trmtail=Pattern.compile("</ul>").matcher(content);
				if((!trmhead.find()) || (!trmtail.find()))
					return "";
				int start=trmhead.end();
				int end=trmtail.start();
				String trans=content.substring(start, end).replaceAll("</?s?p[^>]*>", "");
				// �ڶ���
				StringBuilder sb=new StringBuilder();
				trmhead=Pattern.compile("<li[^>]*>").matcher(trans);
				trmtail=Pattern.compile("</li>").matcher(trans);
				while(trmhead.find() && trmtail.find()){
					sb.append(trans.substring(trmhead.end(), trmtail.start())).
					append("\n");
				}
				if(sb.length()>0)
					sb.deleteCharAt(sb.length()-1);
				return sb.toString();
			}
			@Override
			public long skip(BufferedReader reader) {
				return 0;
			}
			@Override
			public String readNext(BufferedReader reader) {
				return readLine(reader);
			}
		};
	}

	/**
	 * ��ContentReader�ļ�ʵ�֣���Ҫʵ���ˣ���ԣ�ͨ�õ�
	 * append������
	 * @author zzw
	 * @version 1.0
	 */
	public abstract static class SimpleContentReader extends ContentReader {
		private boolean isStart=false;	// �Ƿ�ʼ��ȡ
		private int headNum=0;			// HTMLͷ�����
		private int tailNum=0;			// HTMLβ�����
		private int segnNum=0;			// HTML�α����
		private String startTab;		// ��ʼ��ȡ���
		private int segment;			// ��Ҫ��ȡ����
		private long count;
		
		public SimpleContentReader(String tab, int seg){
			startTab=tab;
			segment=seg;
		}
		
		@Override
		public void append(String s) {
			if(s.contains(startTab)){
				Matcher m=Pattern.compile(startTab).matcher(s);
				m.find();
				s=s.substring(m.start());
				isStart=true;
			}
			else
				count+=s.getBytes().length;
			if(isStart){
				Matcher mhead=head.matcher(s);
				Matcher mtail=tail.matcher(s);
				int offset=0;
				
				while(true){
					int hp=-1, tp=-1;
					if(mhead.find(offset)){ hp=mhead.end(); }
					if(mtail.find(offset)){ tp=mtail.end(); }
					
					if(hp>-1 || tp>-1){
						if((tp>hp && hp>-1) || tp==-1){
							++headNum;
							offset=hp;
						}
						else{
							++tailNum;
							offset=tp;
						}
						
						if(headNum==tailNum){
							if(++segnNum>=segment){
								content.append(s.substring(0, offset));
								isFinish=true;
								return;
							}
							headNum=tailNum=0;
						}
					}
					else{
						content.append(s);
						return;
					}
				}
			}
		}
		
		public abstract String getKeyword();
		public abstract String getTranslation();
		
		public long getCount(){
			return count;
		}
		
		@Override
		public void reset(){
			super.reset();
			isStart=false;
			headNum=tailNum=segnNum=0;
			count=0;
		}
	}
}

package com.zzw.zwm.client;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Semaphore;

import com.zzw.zwm.network.HttpConnectionHelper;
import com.zzw.zwm.util.ContentReaderFactory;
import com.zzw.zwm.util.ContentReaderFactory.SimpleContentReader;

class Test {
	// 服务器地址和端口
	private static final String SERVER_ADDRESS="192.168.1.115";
	private static final int PORT=7999;
		
	private Selector mSelector=null;	// 选择器
	private SocketChannel mChannel=null;// 网络通道
	private Thread mServerThread=null;	// 服务器连接线程
	
	private SelectionKey mKey=null;		// 连接句柄
	//private Test FrmClient;
	private Semaphore mSemaphore=new Semaphore(0);
	
	private String userName;
	private String password;

	private JFrame frmDict;
	private boolean loggedin;
	private JTextField textField;
	private JLabel lblHead;
	private JPanel panelMain;
	private JPanel panelLogin;
	private JLabel lblUserName;
	private JButton btnLogin;
	private JButton btnReg;
	private JButton btnCheckOl;
	private JPanel panelSearch;
	private JPanel panelChkBox;
	private JCheckBox chckbxJinShan;
	private JCheckBox chckbxBing;
	private JCheckBox chckbxYoudao;
	private JButton btnSearch;
	private JPanel panelResult;
	private JTabbedPane tabbedPane;
	private ImageIcon headInIcon=new ImageIcon("head_in.png");
	private ImageIcon headOutIcon=new ImageIcon("head_out.png");
	private JPanel panelJinShan;
	private JPanel panelYoudao;
	private JPanel panelBing;
	private JTextArea transJinShan;
	private JLabel likeJinShan;
	private JLabel likeCountJinShan;
	private JTextArea transYoudao;
	private JLabel likeYoudao;
	private JLabel likeCountYoudao;
	private JTextArea transBing;
	private JLabel likeBing;
	private JLabel likeCountBing;
	private ImageIcon likeNopeIcon=new ImageIcon("like_nope.png");
	private ImageIcon likeYepIcon=new ImageIcon("like_yep.png");
	
	private FrmSignUp frmSignUp;
	private FrmSignIn frmSignIn;
	private FrmCheckUser frmCheckUser;
	private JLabel lblWordJinShan;
	private JLabel lblWordYoudao;
	private JLabel lblWordBing;
	
	private String strKey;
	private String[] strShare = new String[3];
	
	private Thread threadSearch=null;
	
	public static void main(String[] args) {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test window = new Test();
					window.frmDict.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		try {
			Test window = new Test();
			window.frmDict.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLogin(String userName, String password) {
		loggedin = true;
		this.userName = userName;
		this.password = password;
		if(loggedin) lblHead.setIcon(headInIcon);
		else lblHead.setIcon(headOutIcon);
		lblUserName.setText(userName);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getKey() {
		return strKey;
	}

	public String getStrShare() {
		StringBuilder sb=new StringBuilder();
		if(strShare[0].equals("JinShan")) {
			sb.append("JinShan").append("^").append(transJinShan.getText()).append("^").append(""+(likeJinShan.getIcon()==likeYepIcon)).append("^").append(likeCountJinShan.getText()).append("^");
			if(strShare[1].equals("Youdao")) {
				sb.append("Youdao").append("^").append(transYoudao.getText()).append("^").append(""+(likeYoudao.getIcon()==likeYepIcon)).append("^").append(likeCountYoudao.getText()).append("^");
				sb.append("Bing").append("^").append(transBing.getText()).append("^").append(""+(likeBing.getIcon()==likeYepIcon)).append("^").append(likeCountBing.getText()).append("&");
			}
			else {
				sb.append("Bing").append("^").append(transBing.getText()).append("^").append(""+(likeBing.getIcon()==likeYepIcon)).append("^").append(likeCountBing.getText()).append("^");
				sb.append("Youdao").append("^").append(transYoudao.getText()).append("^").append(""+(likeYoudao.getIcon()==likeYepIcon)).append("^").append(likeCountYoudao.getText()).append("&");
			}
		}
		else if(strShare[0].equals("Youdao")) {
			sb.append("Youdao").append("^").append(transYoudao.getText()).append("^").append(""+(likeYoudao.getIcon()==likeYepIcon)).append("^").append(likeCountYoudao.getText()).append("^");
			if(strShare[1].equals("JinShan")) {
				sb.append("JinShan").append("^").append(transJinShan.getText()).append("^").append(""+(likeJinShan.getIcon()==likeYepIcon)).append("^").append(likeCountJinShan.getText()).append("^");
				sb.append("Bing").append("^").append(transBing.getText()).append("^").append(""+(likeBing.getIcon()==likeYepIcon)).append("^").append(likeCountBing.getText()).append("&");
			}
			else {
				sb.append("Bing").append("^").append(transBing.getText()).append("^").append(""+(likeBing.getIcon()==likeYepIcon)).append("^").append(likeCountBing.getText()).append("^");
				sb.append("JinShan").append("^").append(transJinShan.getText()).append("^").append(""+(likeJinShan.getIcon()==likeYepIcon)).append("^").append(likeCountJinShan.getText()).append("&");
			}
		}
		else {
			sb.append("Bing").append("^").append(transBing.getText()).append("^").append(""+(likeBing.getIcon()==likeYepIcon)).append("^").append(likeCountBing.getText()).append("^");
			if(strShare[1].equals("JinShan")) {
				sb.append("JinShan").append("^").append(transJinShan.getText()).append("^").append(""+(likeJinShan.getIcon()==likeYepIcon)).append("^").append(likeCountJinShan.getText()).append("^");
				sb.append("Youdao").append("^").append(transYoudao.getText()).append("^").append(""+(likeYoudao.getIcon()==likeYepIcon)).append("^").append(likeCountYoudao.getText()).append("&");
			}
			else {
				sb.append("Youdao").append("^").append(transYoudao.getText()).append("^").append(""+(likeYoudao.getIcon()==likeYepIcon)).append("^").append(likeCountYoudao.getText()).append("^");
				sb.append("JinShan").append("^").append(transJinShan.getText()).append("^").append(""+(likeJinShan.getIcon()==likeYepIcon)).append("^").append(likeCountJinShan.getText()).append("&");
			}
		}
		
		return sb.toString();
	}
	
	public Test() throws IOException {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		initialize();
		// 服务器连接
		
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
								// TODO 这里接收来自服务器的数据，
								// 根据这些数据更新图形用户界面。
								String[] info=msg.split("#");
								int requireCode=Integer.parseInt(info[0]);
								int respondCode=Integer.parseInt(info[1]);
								switch(requireCode){
								case 0:		// 用户注册
									frmSignUp.respondReg(respondCode, info[2]);
									break;
								case 1:		// 用户登录
									frmSignIn.respondLogin(respondCode, info[2]);
									break;
								case 2:		// 查询点赞
									//respondQueryCount(respondCode, info[2]);
									if(respondCode==1){
										String[] num=info[2].split("&");
										int youdaoNum=Integer.parseInt(num[0]);
										int bingNum=Integer.parseInt(num[1]);
										int jinshanNum=Integer.parseInt(num[2]);
										likeCountYoudao.setText(""+youdaoNum);
										likeCountBing.setText(""+bingNum);
										likeCountJinShan.setText(""+jinshanNum);
										tabbedPane.removeAll();
										
										if(jinshanNum>=youdaoNum && jinshanNum>=bingNum){
											if(chckbxJinShan.isSelected()) {
												tabbedPane.addTab("JinShan", panelJinShan);
											}
											strShare[0]="JinShan";
											if(youdaoNum>=bingNum){
												if(chckbxYoudao.isSelected()) {
													tabbedPane.addTab("Youdao", panelYoudao);
												}
												if(chckbxBing.isSelected()) {
													tabbedPane.addTab("Bing", panelBing);
												}
												strShare[1]="Youdao";
												strShare[2]="Bing";
											}
											else{
												if(chckbxBing.isSelected()) {
													tabbedPane.addTab("Bing", panelBing);
												}
												if(chckbxYoudao.isSelected()) {
													tabbedPane.addTab("Youdao", panelYoudao);
												}
												strShare[2]="Youdao";
												strShare[1]="Bing";
											}
										}
										else if(youdaoNum>=bingNum && youdaoNum>=jinshanNum){
											if(chckbxYoudao.isSelected()) {
												tabbedPane.addTab("Youdao", panelYoudao);
											}
											strShare[0]="Youdao";
											if(bingNum>=jinshanNum){
												if(chckbxBing.isSelected()) {
													tabbedPane.addTab("Bing", panelBing);
												}
												if(chckbxJinShan.isSelected()) {
													tabbedPane.addTab("JinShan", panelJinShan);
												}
												strShare[1]="Bing";
												strShare[2]="JinShan";
											}
											else{
												if(chckbxJinShan.isSelected()) {
													tabbedPane.addTab("JinShan", panelJinShan);
												}
												if(chckbxBing.isSelected()) {
													tabbedPane.addTab("Bing", panelBing);
												}
												strShare[2]="Bing";
												strShare[1]="JinShan";
											}
										}
										else {
											if(chckbxBing.isSelected()) {
												tabbedPane.addTab("Bing", panelBing);
											}
											strShare[0]="Bing";
											if(youdaoNum>=jinshanNum){
												if(chckbxYoudao.isSelected()) {
													tabbedPane.addTab("Youdao", panelYoudao);
												}
												if(chckbxJinShan.isSelected()) {
													tabbedPane.addTab("JinShan", panelJinShan);
												}
												strShare[1]="Youdao";
												strShare[2]="JinShan";
											}
											else{
												if(chckbxJinShan.isSelected()) {
													tabbedPane.addTab("JinShan", panelJinShan);
												}
												if(chckbxYoudao.isSelected()) {
													tabbedPane.addTab("Youdao", panelYoudao);
												}
												strShare[1]="Youdao";
												strShare[2]="JinShan";
											}
										}
										
									}
									break;
								case 3:		// 查询用户
									String[] users=info[2].split("&");
									frmCheckUser.updateList(users.length, users);
									break;
								case 4:		// 用户点赞
									//respondUpdateCount(respondCode, info[2]);
									break;
								case 5:		// 分享词条
									//respondShareItem(respondCode, info[2]);
									break;
								case 6:		// 接收词条
									String[] items=info[2].split("&");
									for(int i=0;i<items.length;i++) {
										frmCheckUser.addMsg(items[i]);
									}
									frmCheckUser.updateMsg();
									break;
								default:break;
								}
								//if(msg!=null)
									//System.out.println(msg);
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
	}

	private void initialize() {
		frmDict = new JFrame();
		frmDict.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDict.setSize(406, 619);
		frmDict.setResizable(false);
		frmDict.setLocationRelativeTo(null);
		frmDict.getContentPane().setLayout(null);
		
		panelMain = new JPanel();
		panelMain.setBackground(new Color(240, 248, 255));
		panelMain.setBounds(0, 0, 400, 590);
		frmDict.getContentPane().add(panelMain);
		panelMain.setLayout(null);
		
		panelLogin = new JPanel();
		panelLogin.setBackground(new Color(135, 206, 250));
		panelLogin.setBounds(10, 10, 380, 90);
		panelMain.add(panelLogin);
		panelLogin.setLayout(null);
		
		
		if(loggedin) lblHead = new JLabel(headInIcon);
		else lblHead = new JLabel(headOutIcon);
		lblHead.setBackground(Color.WHITE);
		lblHead.setForeground(Color.BLACK);
		lblHead.setHorizontalAlignment(SwingConstants.CENTER);
		lblHead.setBounds(10, 0, 80, 90);
		panelLogin.add(lblHead);
		
		loggedin = false;
		btnLogin = new JButton("Sign In/Out");
		frmSignIn =new FrmSignIn("Sign In", this);
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(loggedin) {
					loggedin = false;
					lblHead.setIcon(headOutIcon);
					lblUserName.setText("OFFLINE");
					likeJinShan.setIcon(likeNopeIcon);
					likeYoudao.setIcon(likeNopeIcon);
					likeBing.setIcon(likeNopeIcon);
				}
				else {
					frmSignIn.setLocationRelativeTo(btnLogin);
					frmSignIn.setVisible(true);
				}
			}
		});
		btnLogin.setFont(new Font("微软雅黑", Font.BOLD, 10));
		btnLogin.setBounds(116, 50, 93, 30);
		panelLogin.add(btnLogin);
		
		
		btnReg = new JButton("Sign Up");
		frmSignUp = new FrmSignUp("Sign Up", this);
		btnReg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frmSignUp.setLocationRelativeTo(btnReg);
				frmSignUp.setVisible(true);
			}
		});
		btnReg.setFont(new Font("微软雅黑", Font.BOLD, 10));
		btnReg.setBounds(258, 10, 93, 30);
		panelLogin.add(btnReg);
		
		btnCheckOl = new JButton("Check User");
		frmCheckUser = new FrmCheckUser("Check User", this);
		btnCheckOl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(loggedin) {
					frmCheckUser.setLocationRelativeTo(btnCheckOl);
					frmCheckUser.setVisible(true);
					frmCheckUser.removeUsers();
					write("3#all");
				}
			}
		});
		btnCheckOl.setFont(new Font("微软雅黑", Font.BOLD, 10));
		btnCheckOl.setBounds(258, 50, 93, 30);
		panelLogin.add(btnCheckOl);
		
		lblUserName = new JLabel("OFFLINE");
		lblUserName.setToolTipText("User Name");
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(116, 10, 93, 30);
		panelLogin.add(lblUserName);
		
		panelSearch = new JPanel();
		panelSearch.setBackground(new Color(135, 206, 250));
		panelSearch.setBounds(10, 110, 380, 90);
		panelMain.add(panelSearch);
		panelSearch.setLayout(null);
		
		textField = new JTextField();
		textField.setToolTipText("Input Here");
		textField.setBounds(10, 10, 200, 34);
		panelSearch.add(textField);
		textField.setColumns(10);
		
		panelChkBox = new JPanel();
		panelChkBox.setBounds(10, 54, 360, 26);
		panelSearch.add(panelChkBox);
		panelChkBox.setLayout(null);
		
		chckbxJinShan = new JCheckBox("JinShan");
		chckbxJinShan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxJinShan.isSelected()) {
					if(tabbedPane.indexOfTab("JinShan") == -1) {
						tabbedPane.addTab("JinShan", panelJinShan);
					}
				}
				else {
					int t = tabbedPane.indexOfTab("JinShan");
					tabbedPane.remove(t);
				}
			}
		});
		chckbxJinShan.setSelected(true);
		chckbxJinShan.setBounds(6, 0, 80, 26);
		panelChkBox.add(chckbxJinShan);
		
		chckbxYoudao = new JCheckBox("Youdao");
		chckbxYoudao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxYoudao.isSelected()) {
					if(tabbedPane.indexOfTab("Youdao") == -1) {
						tabbedPane.addTab("Youdao", panelYoudao);
					}
				}
				else {
					int t = tabbedPane.indexOfTab("Youdao");
					tabbedPane.remove(t);
				}
			}
		});
		chckbxYoudao.setSelected(true);
		chckbxYoudao.setBounds(140, 0, 80, 26);
		panelChkBox.add(chckbxYoudao);

		chckbxBing = new JCheckBox("Bing");
		chckbxBing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxBing.isSelected()) {
					if(tabbedPane.indexOfTab("Bing") == -1) {
						tabbedPane.addTab("Bing", panelBing);
					}
				}
				else {
					int t = tabbedPane.indexOfTab("Bing");
					tabbedPane.remove(t);
				}
			}
		});
		chckbxBing.setSelected(true);
		chckbxBing.setBounds(274, 0, 80, 26);
		panelChkBox.add(chckbxBing);
		
		btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				threadSearch = new Thread() {
					public void run() {
						likeJinShan.setIcon(likeNopeIcon);
						likeYoudao.setIcon(likeNopeIcon);
						likeBing.setIcon(likeNopeIcon);
						likeCountJinShan.setText("");
						likeCountYoudao.setText("");
						likeCountBing.setText("");
						LinkedHashMap<String,String> query=
								new LinkedHashMap<String,String>();
						String word = textField.getText().replaceAll(" ", "+");
						
						
						SimpleContentReader jinshancr=(SimpleContentReader)ContentReaderFactory.
								getJinshanContentReader("<div class=\"in-base-top clearfix\" class=\"clearfix\">", 2);
						jinshancr.reset();
						HttpConnectionHelper.get("http://www.iciba.com", word, null, "UTF-8", jinshancr);
						lblWordJinShan.setText(jinshancr.getKeyword());
						transJinShan.setText(jinshancr.getTranslation());
						
						SimpleContentReader youdaocr=(SimpleContentReader)ContentReaderFactory.
								getYoudaoContentReader("<h2 class=\"wordbook-js\">", 2);
						query.clear();
						query.put("q", word);
						youdaocr.reset();
						HttpConnectionHelper.get("http://dict.youdao.com", "/search", query, "UTF-8", youdaocr);
						lblWordYoudao.setText(youdaocr.getKeyword());
						transYoudao.setText(youdaocr.getTranslation());
						
						SimpleContentReader bingcr=(SimpleContentReader)ContentReaderFactory.
								getBingContentReader("<div class=\"hd_area\">", 2);
						query.clear();
						query.put("q", word);
						bingcr.reset();
						HttpConnectionHelper.get("http://cn.bing.com", "/dict/search", query, "UTF-8", bingcr);
						lblWordBing.setText(bingcr.getKeyword());
						transBing.setText(bingcr.getTranslation());
						
						strKey=youdaocr.getKeyword();
						
						if(loggedin) {
							write("2#"+strKey);
						}
						/*tabbedPane.removeAll();
						if(chckbxJinShan.isSelected()) {
							tabbedPane.addTab("JinShan", panelJinShan);
						}
						if(chckbxYoudao.isSelected()) {
							tabbedPane.addTab("Youdao", panelYoudao);
						}
						if(chckbxBing.isSelected()) {
							tabbedPane.addTab("Bing", panelBing);
						}*/
					}
				};
				threadSearch.start();
			}
		});
		btnSearch.setBounds(253, 10, 100, 34);
		panelSearch.add(btnSearch);
		
		panelResult = new JPanel();
		panelResult.setBackground(new Color(135, 206, 250));
		panelResult.setBounds(10, 210, 380, 370);
		panelMain.add(panelResult);
		panelResult.setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 10, 360, 350);
		panelJinShan = new JPanel();
		panelJinShan.setBackground(new Color(240, 248, 255));
		tabbedPane.addTab("JinShan", panelJinShan);
		panelJinShan.setLayout(null);
		
		transJinShan = new JTextArea();
		transJinShan.setFont(new Font("Monospaced", Font.PLAIN, 14));
		transJinShan.setEditable(false);
		transJinShan.setLineWrap(true);
		transJinShan.setWrapStyleWord(true);
		transJinShan.setBackground(new Color(240, 248, 255));
		transJinShan.setBounds(10, 60, 285, 251);
		panelJinShan.add(transJinShan);
		
		likeJinShan = new JLabel();
		likeJinShan.setToolTipText("Like");
		likeJinShan.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(loggedin) {
					if(likeJinShan.getIcon()==likeNopeIcon) {
						likeJinShan.setIcon(likeYepIcon);
						write("4#"+strKey+"&0&0&1");
						likeCountJinShan.setText(""+(Integer.parseInt(likeCountJinShan.getText())+1));
					}
					else {
						likeJinShan.setIcon(likeNopeIcon);
						write("4#"+strKey+"&0&0&-1");
						likeCountJinShan.setText(""+(Integer.parseInt(likeCountJinShan.getText())-1));
					}
				}
			}
		});
		likeJinShan.setIcon(likeNopeIcon);
		likeJinShan.setBounds(305, 10, 40, 40);
		panelJinShan.add(likeJinShan);
		
		likeCountJinShan = new JLabel("");
		likeCountJinShan.setHorizontalAlignment(JLabel.CENTER);
		likeCountJinShan.setBounds(305, 60, 40, 15);
		panelJinShan.add(likeCountJinShan);
		
		lblWordJinShan = new JLabel("");
		lblWordJinShan.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblWordJinShan.setBounds(10, 10, 285, 40);
		panelJinShan.add(lblWordJinShan);
		
		panelYoudao = new JPanel();
		panelYoudao.setBackground(new Color(240, 248, 255));
		tabbedPane.addTab("Youdao", panelYoudao);
		panelYoudao.setLayout(null);
		
		transYoudao = new JTextArea();
		transYoudao.setFont(new Font("Monospaced", Font.PLAIN, 14));
		transYoudao.setWrapStyleWord(true);
		transYoudao.setLineWrap(true);
		transYoudao.setEditable(false);
		transYoudao.setBackground(new Color(240, 248, 255));
		transYoudao.setBounds(10, 60, 285, 251);
		panelYoudao.add(transYoudao);
		
		likeYoudao = new JLabel();
		likeYoudao.setToolTipText("Like");
		likeYoudao.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(loggedin) {
					if(likeYoudao.getIcon()==likeNopeIcon) {
						likeYoudao.setIcon(likeYepIcon);
						write("4#"+strKey+"&1&0&0");
						likeCountYoudao.setText(""+(Integer.parseInt(likeCountYoudao.getText())+1));
					}
					else {
						likeYoudao.setIcon(likeNopeIcon);
						write("4#"+strKey+"&-1&0&0");
						likeCountYoudao.setText(""+(Integer.parseInt(likeCountYoudao.getText())-1));
					}
				}
			}
		});
		likeYoudao.setIcon(likeNopeIcon);
		likeYoudao.setBounds(305, 10, 40, 40);
		panelYoudao.add(likeYoudao);
		
		likeCountYoudao = new JLabel("");
		likeCountYoudao.setHorizontalAlignment(JLabel.CENTER);
		likeCountYoudao.setBounds(305, 60, 40, 15);
		panelYoudao.add(likeCountYoudao);
		
		lblWordYoudao = new JLabel("");
		lblWordYoudao.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblWordYoudao.setBounds(10, 10, 285, 40);
		panelYoudao.add(lblWordYoudao);
		
		panelBing = new JPanel();
		panelBing.setBackground(new Color(240, 248, 255));
		tabbedPane.addTab("Bing", panelBing);
		panelBing.setLayout(null);
		
		transBing = new JTextArea();
		transBing.setFont(new Font("Monospaced", Font.PLAIN, 14));
		transBing.setWrapStyleWord(true);
		transBing.setLineWrap(true);
		transBing.setEditable(false);
		transBing.setBackground(new Color(240, 248, 255));
		transBing.setBounds(10, 60, 285, 251);
		panelBing.add(transBing);
		
		likeBing = new JLabel();
		likeBing.setToolTipText("Like");
		likeBing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(loggedin) {
					if(likeBing.getIcon()==likeNopeIcon) {
						likeBing.setIcon(likeYepIcon);
						write("4#"+strKey+"&1&0&0");
						likeCountBing.setText(""+(Integer.parseInt(likeCountBing.getText())+1));
					}
					else {
						likeBing.setIcon(likeNopeIcon);
						write("4#"+strKey+"&-1&0&0");
						likeCountBing.setText(""+(Integer.parseInt(likeCountBing.getText())-1));
					}
				}
			}
		});
		likeBing.setIcon(likeNopeIcon);
		likeBing.setBounds(305, 10, 40, 40);
		panelBing.add(likeBing);
		
		likeCountBing = new JLabel("");
		likeCountBing.setHorizontalAlignment(JLabel.CENTER);
		likeCountBing.setBounds(305, 60, 40, 15);
		panelBing.add(likeCountBing);
		
		lblWordBing = new JLabel("");
		lblWordBing.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblWordBing.setBounds(10, 10, 285, 40);
		panelBing.add(lblWordBing);
		panelResult.add(tabbedPane);
		
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
			// TODO 通知用户连接断开，在图形用户界面显示
			System.err.println("[ BREAK ] 与服务器的连接已断开！");
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
		// 保证在向服务器传递数据之前，已经建立与服务器之间的连接。
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
			// TODO 通知用户与服务器的连接已断开，在图形用户界面显示
			System.err.println("[ BREAK ] 与服务器的连接已断开！");
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
}

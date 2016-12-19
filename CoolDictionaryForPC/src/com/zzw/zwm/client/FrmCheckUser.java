package com.zzw.zwm.client;
import java.awt.*;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FrmCheckUser extends JFrame {

	private JPanel contentPane;
	private JPanel panelMain;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPaneOl;
	private JScrollPane scrollPaneAll;
	private JList<String> listOl;
	private JList<String> listAll;
	private Test father;
	private Vector<String> usersAll = new Vector<String>();
	private Vector<String> usersOl = new Vector<String>();
	private JButton btnRefresh;
	private JButton btnMailbox;
	private JButton btnShare;
	private ImageIcon iconRefresh = new ImageIcon("./refresh.png");
	private ImageIcon iconMailbox = new ImageIcon("./mailbox.png");
	private ImageIcon iconShare = new ImageIcon("./share.png");
	private FrmMailbox frmMailbox;
	private BufferedImage bi;
	private String imgName;
	
	public void addMsg(String msg) {
		frmMailbox.msg.addElement(msg);
	}
	
	public void updateMsg() {
		frmMailbox.msgBrief.clear();
		for(int i=frmMailbox.msg.size()-1;i>=0;i--) {
			String[] info=frmMailbox.msg.elementAt(i).split("\\^");
			// 发送者
			String sender=info[0];
			// 单词
			String word=info[1];
			String date=info[14];
			String temp=date+", "+sender+" shared the word \""+word+"\" with you.";
			frmMailbox.msgBrief.add(temp);
			frmMailbox.listMsg.setListData(frmMailbox.msgBrief);
		}
	}
	
	class FrmImg extends JFrame {

		private JPanel contentPane;
		private JPanel panelMain;
		private Test father;
		
		public FrmImg(String msg) {
			setVisible(false);
			setResizable(false);
			//this.father = father;
			
			setTitle("Word Card");
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setBounds(100, 100, 426, 489);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			panelMain = new JPanel();
			panelMain.setBackground(new Color(135, 206, 250));
			panelMain.setBounds(10, 10, 400, 440);
			contentPane.add(panelMain);
			panelMain.setLayout(null);
			
			JLabel lblWord = new JLabel("");
			lblWord.setFont(new Font("Times New Roman", Font.BOLD, 16));
			lblWord.setBounds(10, 0, 200, 40);
			panelMain.add(lblWord);
			
			JLabel lblSender = new JLabel("");
			lblSender.setBounds(240, 0, 150, 20);
			panelMain.add(lblSender);
			
			JLabel lblTime = new JLabel("");
			lblTime.setBounds(240, 20, 150, 20);
			panelMain.add(lblTime);
			
			JPanel panel_0 = new JPanel();
			panel_0.setBackground(new Color(240, 248, 255));
			panel_0.setBounds(10, 50, 380, 120);
			panelMain.add(panel_0);
			panel_0.setLayout(null);
			
			JLabel lblLike_0 = new JLabel();
			lblLike_0.setToolTipText("Like");
			lblLike_0.setBounds(330, 10, 40, 40);
			panel_0.add(lblLike_0);
			
			JLabel lblCount_0 = new JLabel("");
			lblCount_0.setHorizontalAlignment(SwingConstants.CENTER);
			lblCount_0.setBounds(330, 60, 40, 15);
			panel_0.add(lblCount_0);
			
			JTextArea textArea_0 = new JTextArea();
			textArea_0.setFont(new Font("Monospaced", Font.PLAIN, 12));
			textArea_0.setLineWrap(true);
			textArea_0.setWrapStyleWord(true);
			textArea_0.setEditable(false);
			textArea_0.setBackground(new Color(135, 206, 250));
			textArea_0.setBounds(10, 10, 310, 100);
			panel_0.add(textArea_0);
			
			JPanel panel_1 = new JPanel();
			panel_1.setBackground(new Color(240, 248, 255));
			panel_1.setBounds(10, 180, 380, 120);
			panelMain.add(panel_1);
			panel_1.setLayout(null);
			
			JLabel lblLike_1 = new JLabel();
			lblLike_1.setToolTipText("Like");
			lblLike_1.setBounds(330, 10, 40, 40);
			panel_1.add(lblLike_1);
			
			JLabel lblCount_1 = new JLabel("");
			lblCount_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblCount_1.setBounds(330, 60, 40, 15);
			panel_1.add(lblCount_1);
			
			JTextArea textArea_1 = new JTextArea();
			textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 12));
			textArea_1.setWrapStyleWord(true);
			textArea_1.setLineWrap(true);
			textArea_1.setEditable(false);
			textArea_1.setBackground(new Color(135, 206, 250));
			textArea_1.setBounds(10, 10, 310, 100);
			panel_1.add(textArea_1);
			
			JPanel panel_2 = new JPanel();
			panel_2.setBackground(new Color(240, 248, 255));
			panel_2.setBounds(10, 310, 380, 120);
			panelMain.add(panel_2);
			panel_2.setLayout(null);
			
			JLabel lblLike_2 = new JLabel();
			lblLike_2.setToolTipText("Like");
			lblLike_2.setBounds(330, 10, 40, 40);
			panel_2.add(lblLike_2);
			
			JLabel lblCount_2 = new JLabel("");
			lblCount_2.setHorizontalAlignment(SwingConstants.CENTER);
			lblCount_2.setBounds(330, 60, 40, 15);
			panel_2.add(lblCount_2);
			
			JTextArea textArea_2 = new JTextArea();
			textArea_2.setFont(new Font("Monospaced", Font.PLAIN, 12));
			textArea_2.setWrapStyleWord(true);
			textArea_2.setLineWrap(true);
			textArea_2.setEditable(false);
			textArea_2.setBackground(new Color(135, 206, 250));
			textArea_2.setBounds(10, 10, 310, 100);
			panel_2.add(textArea_2);
			
			String[] info=msg.split("\\^");
			// 发送者
			lblSender.setText(info[0]);
			// 单词
			lblWord.setText(info[1]);
			// 翻译标题
			String t0 = new String();
			String t1 = new String();
			String t2 = new String();
			t0=info[2]+"\n"+info[3];
			t1=info[6]+"\n"+info[7];
			t2=info[10]+"\n"+info[11];
			textArea_0.setText(t0);
			textArea_1.setText(t1);
			textArea_2.setText(t2);
			// 是否点赞
			if(Boolean.parseBoolean(info[4]))
				lblLike_0.setIcon(new ImageIcon("./like_yep.png"));
			else
				lblLike_0.setIcon(new ImageIcon("./like_nope.png"));
			if(Boolean.parseBoolean(info[8]))
				lblLike_1.setIcon(new ImageIcon("./like_yep.png"));
			else
				lblLike_1.setIcon(new ImageIcon("./like_nope.png"));
			if(Boolean.parseBoolean(info[12]))
				lblLike_2.setIcon(new ImageIcon("./like_yep.png"));
			else
				lblLike_2.setIcon(new ImageIcon("./like_nope.png"));
			// 点赞数
			lblCount_0.setText(info[5]);
			lblCount_1.setText(info[9]);
			lblCount_2.setText(info[13]);
			// 发送时间
			lblTime.setText(info[14]);
			
			bi = new BufferedImage(panelMain.getWidth(),panelMain.getHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bi.createGraphics();
			panelMain.paint(g2d);
			String strTime=info[14].replaceAll("[^0-9]", "");
			imgName = ".\\wordcards\\"+info[0]+info[1]+strTime+".png";
			
			/*try {
				//String filename = ".\\wordcards\\"+info[0]+info[1]+info[14]+".png";
				
				File file=new File(filename);
				if(!file.exists())
					file.createNewFile();
				ImageIO.write(bi, "PNG", new File(filename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}*/
		}
	}
	
	class FrmDisplay extends JFrame {

		private JPanel contentPane;
		private JLabel lblDisplay;
		private Test father;
		
		public FrmDisplay() {
			//setVisible(false);
			setResizable(false);
			//this.father = father;
			
			setTitle("Word Card");
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setBounds(100, 100, 426, 549);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			lblDisplay = new JLabel();
			lblDisplay.setBounds(10, 10, 400, 440);
			lblDisplay.setIcon(new ImageIcon(bi));
			contentPane.add(lblDisplay);
			
			JButton btnSave = new JButton("Save");
			btnSave.setBounds(66, 480, 100, 30);
			btnSave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						//String filename = ".\\wordcards\\"+info[0]+info[1]+info[14]+".png";
						File file=new File(imgName);
						if(!file.exists())
							file.createNewFile();
						ImageIO.write(bi, "PNG", new File(imgName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					setVisible(false);
				}
			});
			contentPane.add(btnSave);
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.setBounds(246, 480, 100, 30);
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
				}
			});
			contentPane.add(btnCancel);
		}
	}
	
	class FrmMailbox extends JFrame {

		private JPanel contentPane;
		private JScrollPane scrollPane;
		private JList<String> listMsg = new JList<String>();
		private Vector<String> msg = new Vector<String>();
		private Vector<String> msgBrief = new Vector<String>();
		private boolean flag;

		public FrmMailbox(String title) {
			flag=false;
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setBounds(100, 100, 450, 300);
			setResizable(false);
			setVisible(false);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			//TODO Get Messages
			
			for(int i=msg.size()-1;i>=0;i--) {
				String[] info=msg.elementAt(i).split("\\^");
				// 发送者
				String sender=info[0];
				// 单词
				String word=info[1];
				String date=info[14];
				String temp=date+", "+sender+" shared the word \""+word+"\" with you.";
				msgBrief.add(temp);
			}
			
			listMsg = new JList<String>(msgBrief);
			//listMsg.addListSelectionListener(new ListSelectionListener() {
				//@Override
				//public void valueChanged(ListSelectionEvent e) {
			listMsg.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					//TODO Generate Image
					//if(!flag) {
						int i = listMsg.getSelectedIndex();
						if(i>=0) {
							FrmImg frm=new FrmImg(msg.elementAt(msg.size()-1-i));
							FrmDisplay dis=new FrmDisplay();
							dis.setLocationRelativeTo(listMsg);
							dis.setVisible(true);
						}
					//}
					//flag=!flag;
				}
			});
			listMsg.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			scrollPane = new JScrollPane(listMsg);
			scrollPane.setBounds(0, 0, 434, 261);
			contentPane.add(scrollPane);
		}
	}
	
	public void updateList(int no, String[] users) {
		for(int i=0;i<no;i++) {
			String[] info=users[i].split(",");
			if(info.length>1) {
				if(info[1].equals("1"))
					usersOl.add(info[0]);
				usersAll.add(info[0]);
			}
		}
		listOl.setListData(usersOl);
		listAll.setListData(usersAll);
	}
	
	public void removeUsers() {
		usersOl.removeAllElements();
		usersAll.removeAllElements();
	}
	
	public FrmCheckUser(String title, Test father) {
		//TODO Get User List
		this.father=father;
		//this.father.write("3#all");
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle(title);
		setBounds(100, 100, 436, 299);
		setVisible(false);
		setResizable(false);
		frmMailbox = new FrmMailbox("Mailbox");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelMain = new JPanel();
		panelMain.setBounds(0, 0, 430, 261);
		contentPane.add(panelMain);
		panelMain.setLayout(null);
		
		listOl=new JList<String>(usersOl);
		listOl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneOl = new JScrollPane(listOl);
		scrollPaneOl.setBounds(0, 0, 387, 236);
		
		listAll = new JList<String>(usersAll);
		listAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneAll = new JScrollPane(listAll);
		scrollPaneAll.setBounds(0, 0, 387, 236);
		
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBounds(10, 10, 364, 241);
		tabbedPane.addTab("<html>O<br>N<br>L<br>I<br>N<br>E", scrollPaneOl);
		tabbedPane.addTab("<html>A<br>L<br>L", scrollPaneAll);
		panelMain.add(tabbedPane);
		
		btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO Get User List
				usersOl.removeAllElements();
				usersAll.removeAllElements();
				father.write("3#all");
			}
		});
		btnRefresh.setBounds(384, 10, 40, 40);
		btnRefresh.setIcon(iconRefresh);
		panelMain.add(btnRefresh);
		
		btnMailbox = new JButton("");
		btnMailbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frmMailbox.setLocationRelativeTo(btnMailbox);
				frmMailbox.setVisible(true);
			}
		});
		btnMailbox.setBounds(384, 70, 40, 40);
		btnMailbox.setIcon(iconMailbox);
		panelMain.add(btnMailbox);
		
		btnShare = new JButton("");
		btnShare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = tabbedPane.getSelectedIndex();
				String receiver;
				if(index == 0) {
					receiver = listOl.getSelectedValue();
					//TODO Send
				}
				else {
					receiver = listAll.getSelectedValue();
				}
				if(receiver!=null && !receiver.equals("")) {
					StringBuilder sb=new StringBuilder("5#");				// 请求号
					sb.append(father.getUserName()).append("&").append(father.getKey()).append("&").append(father.getStrShare());
					sb.append(receiver);
					father.write(sb.toString());
				}
			}
		});
		btnShare.setBounds(384, 130, 40, 40);
		btnShare.setIcon(iconShare);
		panelMain.add(btnShare);
	}
}

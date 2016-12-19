package com.zzw.zwm.client;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

public class FrmSignUp extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldUser;
	private JPasswordField passwordField;
	private JPanel panelMain;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JLabel lblUserName;
	private JLabel lblPassword;
	private String userName;
	private String password;
	private Test father;
	private JLabel lblMsg;
	private Thread send;
	
	public void respondReg(int respondCode, String msg){
		if(respondCode==1){
			textFieldUser.setText("");
			passwordField.setText("");
			lblMsg.setText("");
			//father.setLogin(userName, password);
			setVisible(false);
		}
		else {
			lblMsg.setText(msg);
		}
	}
	
	public FrmSignUp(String title, Test father) {
		setVisible(false);
		setResizable(false);
		this.father = father;
		
		setTitle(title);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 436, 299);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelMain = new JPanel();
		panelMain.setBounds(10, 10, 400, 240);
		contentPane.add(panelMain);
		panelMain.setLayout(null);
		
		btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				send = new Thread() {
					public void run() {
						userName = textFieldUser.getText();
						password = new String(passwordField.getPassword());
						father.write("0#"+userName+"&"+password);
					}
				};
				send.start();
				//System.out.println(userName);
				//System.out.println(password);
				//dispose();
				//setVisible(false);
			}
		});
		btnConfirm.setBounds(66, 180, 100, 25);
		panelMain.add(btnConfirm);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});
		btnCancel.setBounds(234, 180, 100, 25);
		panelMain.add(btnCancel);
		
		lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(50, 35, 80, 25);
		panelMain.add(lblUserName);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(50, 105, 80, 25);
		panelMain.add(lblPassword);
		
		textFieldUser = new JTextField();
		textFieldUser.setBounds(150, 35, 200, 25);
		panelMain.add(textFieldUser);
		textFieldUser.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(150, 105, 200, 25);
		panelMain.add(passwordField);
		
		lblMsg = new JLabel();
		lblMsg.setBounds(66, 215, 268, 25);
		lblMsg.setHorizontalAlignment(JLabel.CENTER);
		panelMain.add(lblMsg);
	}
}

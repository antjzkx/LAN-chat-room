package Client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import Server.Help;

import java.io.*;
import java.net.*;

/*
 * 聊天客户端的主框架类
 * */


public class ChatClient extends JFrame implements ActionListener{
	
	String  ip = "127.0.0.1";//链接到服务端的iP地址
	int port = 8888;//连接到服务端的端口号
	String  userName = "匆匆过客";  //用户名
	int type = 0; // 0 表示未连接，1表示已连接

	//Image icon;  //程序图标
	JComboBox combobox; //选择发送消息的接收者
	JTextArea messageShow;//客户端的信息显示
	JScrollPane messageScrollPane; //信息显示的滚动条
	
	JLabel express,sendToLabel,messageLabel;
	
	JTextField clientMessage; //客户端消息发送
	JCheckBox checkbox; // 悄悄话
	JComboBox actionlist;// 表情选择
	JButton clientMessageButton; //发送消息
	JTextField showStatus; // 显示用户连接状态
	
	Socket socket;
	ObjectOutputStream output; // 网络套接字输出流
	ObjectInputStream input; // 网络套接字输入流
	
	ClientReceive recThread;
	//建立菜单栏
	JMenuBar jMenuBar = new JMenuBar();
	//建立菜单组
	JMenu operateMenu = new JMenu("操作（O）");
	//建立菜单项
	JMenuItem loginItem = new JMenuItem("用户登陆(T)");
	JMenuItem logoffItem = new JMenuItem("用户注销(L)");
	JMenuItem exitItem = new JMenuItem("退出(X)");
	
	JMenu conMenu = new JMenu("设置(C	)");
	JMenuItem userItem = new JMenuItem("用户设置(U)");
	JMenuItem connectItem = new JMenuItem("连接设置(C)");
	
	JMenu helpMenu = new JMenu("帮助(H)");
	JMenuItem helpItem = new JMenuItem("帮助(H)");
	
	//建立工具栏
	JToolBar toolBar = new JToolBar();
	//建立工具栏中的按钮组件
	JButton loginButton; //用户登陆
	JButton logoffButton;//用户注销
	JButton userButton;//用户信息的设置
	JButton connectButton;//连接设置
	JButton exitButton; // 退出按钮
	
	//框架大小
	Dimension faceSize = new Dimension(400,600);
	
	JPanel downPanel;
	GridBagLayout gridBag;
	GridBagConstraints gridBagCon;
	
public ChatClient(){
	init(); //初始化程序
	
	
	// 添加框架的关闭事件处理
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.pack();
	//设置框架的大小
	this.setSize(faceSize);
	//设置运行时窗口的位置
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.setLocation((int)(screenSize.width - faceSize.getWidth())/2, (int)(screenSize.height-faceSize.getHeight())/2);
	this.setResizable(false);
	this.setTitle("聊天室客户端"); //设置标题
	show();

}

/*
 *程序初始化函数
 * */
	
public void init(){
	
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());
	
	//添加菜单栏

	operateMenu.add(loginItem);
	operateMenu.add(logoffItem);
	operateMenu.add(exitItem);
    jMenuBar.add(operateMenu);
    conMenu.add(userItem);
    conMenu.add(connectItem);
    jMenuBar.add(conMenu);
    helpMenu.add(helpItem);
    jMenuBar.add(helpMenu);
    setJMenuBar(jMenuBar);
    
    //初始化按钮
    loginButton = new JButton("登陆");
    logoffButton = new JButton("注销");
    userButton = new JButton("用户设置");
    connectButton = new JButton("连接设置");
    exitButton = new JButton ("退出");
    
    //当鼠标放上 显示信息
    
    loginButton.setToolTipText("连接到指定的服务器");
    logoffButton.setToolTipText("与服务器断开连接");
    userButton.setToolTipText("设置用户信息");
    connectButton.setToolTipText("设置所要连接到的服务器信息");
    
    //按钮添加打工具栏
   
    toolBar.add(userButton);
    toolBar.add(connectButton);
    toolBar.addSeparator();
    toolBar.add(loginButton);
    toolBar.add(logoffButton);
    toolBar.addSeparator();
    toolBar.add(exitButton);
    
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    checkbox = new JCheckBox("悄悄话");
    checkbox.setSelected(false);
    
    actionlist = new JComboBox();
    actionlist.addItem("微笑地");
    actionlist.addItem("高兴地");
    actionlist.addItem("轻轻地");
    actionlist.addItem("生气地");
    actionlist.addItem("小心地");
    actionlist.addItem("静静地");
    actionlist.setSelectedIndex(0);
    
    //初始时
    
    loginButton.setEnabled(true);
    logoffButton.setEnabled(false);
    
    //为菜单栏添加事件监听
    loginItem.addActionListener(this);
    logoffItem.addActionListener(this);
    exitItem.addActionListener(this);
    userItem.addActionListener(this);
    connectItem.addActionListener(this);
    helpItem.addActionListener(this);
    
    
    // 添加按钮地事件侦听
    
    loginButton.addActionListener(this);
    logoffButton.addActionListener(this);
    userButton.addActionListener(this);
    connectButton.addActionListener(this);
    exitButton.addActionListener(this);
    combobox = new JComboBox();
    
    combobox.insertItemAt("所有人", 0);
    combobox.setSelectedIndex(0);
    
    messageShow = new JTextArea();
    messageShow.setEditable(false);
    
    //添加滚动条
    messageScrollPane = new JScrollPane(messageShow,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    messageScrollPane.setPreferredSize(new Dimension(400,400));
    messageScrollPane.revalidate();
    
    clientMessage = new JTextField(18);
    clientMessage.setEnabled(false);
    clientMessageButton = new JButton();
    clientMessageButton.setText("发送");
    
    //添加系统消息地事件监听
    
    clientMessage.addActionListener(this);
    clientMessageButton.addActionListener(this);
    
    sendToLabel = new JLabel("发送至");
    express = new JLabel("        表情 ：");
    messageLabel = new JLabel("发送消息");
    downPanel = new JPanel();
    gridBag = new GridBagLayout();
    downPanel.setLayout(gridBag);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 0;
    gridBagCon.gridy = 0;
    gridBagCon.gridwidth = 5;
    gridBagCon.gridheight = 2;
    gridBagCon.ipadx = 5;
    gridBagCon.ipady = 5;
    
    JLabel none = new JLabel("	");
    gridBag.setConstraints(none, gridBagCon);
    downPanel.add(none);
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 0;
    gridBagCon.gridy = 2;
    gridBagCon.insets = new Insets(1,0,0,0);
    gridBag.setConstraints(sendToLabel,gridBagCon);
    downPanel.add(sendToLabel);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 1;
    gridBagCon.gridy = 2;
    gridBagCon.anchor = GridBagConstraints.LINE_START;
    gridBag.setConstraints(combobox, gridBagCon);
    downPanel.add(combobox);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 2;
    gridBagCon.gridy = 2;
    gridBagCon.anchor = GridBagConstraints.LINE_END;
    gridBag.setConstraints(express, gridBagCon);
    downPanel.add(express);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 3;
    gridBagCon.gridy = 2;
    gridBagCon.anchor = GridBagConstraints.LINE_START;
    gridBag.setConstraints(actionlist, gridBagCon);
    downPanel.add(actionlist);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 4;
    gridBagCon.gridy = 2;
    gridBagCon.insets = new Insets(1,0,0,0);
    gridBag.setConstraints(checkbox, gridBagCon);
    downPanel.add(checkbox);
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 0;
    gridBagCon.gridy = 3;
    gridBag.setConstraints(messageLabel, gridBagCon);
    downPanel.add(messageLabel);
    
    gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 1;
	 gridBagCon.gridy = 3;
	 gridBagCon.gridwidth = 3;
	 gridBagCon.gridheight = 1;
	 gridBag.setConstraints(clientMessage,gridBagCon);
	 downPanel.add(clientMessage);
    
    
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 4;
    gridBagCon.gridy = 3;
    gridBag.setConstraints(clientMessageButton, gridBagCon);
    downPanel.add(clientMessageButton);
    
    showStatus = new JTextField(8);
    showStatus.setEditable(false);
    gridBagCon = new GridBagConstraints();
    gridBagCon.gridx = 0;
    gridBagCon.gridy = 5;
    gridBag.setConstraints(showStatus,gridBagCon);
    downPanel.add(showStatus);
     
    
    
    
    contentPane.add(messageScrollPane, BorderLayout.CENTER);
    contentPane.add(downPanel, BorderLayout.SOUTH);
    
    // 关闭程序时地操作
    
    this.addWindowListener(
    		 new WindowAdapter(){
    			 public void windowClosing(WindowEvent e){
    				 if(type == 1){
    					 DisConnect();
    					 
    				 }
    				 System.exit(0);
    			 }
    		 }
    		);
    
    
}

/*
 * 事件处理
 * */
public void actionPerformed(ActionEvent e){
	Object obj = e.getSource();
	
	if(obj == userItem || obj == userButton){
		//用户信息设置
		//调出用户信息设置对话框
		UserConf userConf = new UserConf(this,userName);
		userConf.setVisible(true); 
		userName = userConf.userInputName;
	}
	else if( obj == connectItem || obj == connectButton){
		//连接服务器设置
		//调出连接设置对话框
		ConnectConf connectConf = new ConnectConf(this, ip, port);
        connectConf.setVisible(true);
        ip = connectConf.userInputIp;
        port = connectConf.userInputPort;
        System.out.println(port);
	}
	else if( obj == loginItem || obj == loginButton){
		//登陆
		Connect();
		
	}
	else if(  obj ==logoffItem || obj ==logoffButton)
	{
		//注销
		DisConnect();
		//showStatus.setText("");
	}
	else if ( obj == clientMessage || obj == clientMessageButton){
		//发送消息
		
		SendMessage();
		clientMessage.setText("");
	}
	else if(obj == exitItem || obj== exitButton){
		//退出
		int j = JOptionPane.showConfirmDialog(this,"真的要退出么？","退出",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		if( j ==JOptionPane.YES_OPTION){
			if(type == 1){
				DisConnect();
				
			}
			System.exit(0);
		}
	}
	else if(obj == helpItem ){
		//菜单栏中地帮助
	  // 调出帮助对话框
		Help helpDialog = new Help(this);
		helpDialog.show();
	}
}

public void Connect(){
	try{
		socket = new Socket(ip,port);
	}
	catch(Exception e) {
		  JOptionPane.showConfirmDialog(this,"不能连接到指定服务器。 \n 请确定连接设置是否正确","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
	    return ;
	}
	try{
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());
		output.flush();
		output.writeObject(userName);
		output.flush();
		
		recThread = new ClientReceive(socket,output,input,combobox,messageShow,showStatus);
		recThread.start();
		
		
		loginButton.setEnabled(false);
		loginItem.setEnabled(false);
		userButton.setEnabled(false);
		userItem.setEnabled(false);
		connectButton.setEnabled(false);
		connectItem.setEnabled(false);
		logoffButton.setEnabled(true);
		logoffItem.setEnabled(true);
		
	    clientMessage.setEnabled(true);
	    messageShow.append("连接服务器"+ip+":"+port+"成功...\n");
	    type = 1;
	    
	} 
	catch( Exception e)
	{
		System.out.println(e);
	    return ;	
	}
	
}

public void DisConnect(){
	loginButton.setEnabled(true);
	loginItem.setEnabled(true);
	userButton.setEnabled(true);
	userItem.setEnabled(true);
	connectButton.setEnabled(true);
	connectItem.setEnabled(true);
	logoffButton.setEnabled(false);
	clientMessage.setEnabled(false);
	
	
	if(socket.isClosed()){
		return;
	}
	
	try{
		output.writeObject("用户下线");
		input.close();
		output.flush();
		output.close();
		socket.close();
		messageShow.append("已经与服务器断开连接... \n");
		type = 0;// 表示未连接
	}catch(Exception e){
		
	}
	
}


public void SendMessage(){
	String toSomebody = combobox.getSelectedItem().toString();
	String status ="";
	if(checkbox.isSelected()){
		 status = "悄悄话";
	}
	String action = actionlist.getSelectedItem().toString();
	String message = clientMessage.getText();
	
	
	if(socket.isClosed()){
		return;
	}

	try{
		output.writeObject("聊天信息");
		output.flush();
		output.writeObject(toSomebody);
		output.flush();
		output.writeObject(status);
		output.flush();
		output.writeObject(action);
		output.flush();
		output.writeObject(message);	
		output.flush();
		
	}
	
	catch(Exception e){
		
	}
	
}



public static void main(String args[]){
	ChatClient app= new ChatClient();

	
}


















}



















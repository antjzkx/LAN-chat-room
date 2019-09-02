package Server;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
/**
   *聊天室服务器的主框框架;
   */
public class ChatServer extends JFrame implements ActionListener{
	 
	public static int port = 8888;  //服务器的窃听窗口
	
	ServerSocket serverSocket;//服务端的Socket
	//Image icon; //程序图标
	JComboBox combobox;//选择发送消息的接收者
	JTextArea messageShow; //服务端的消息显示
	JScrollPane messageScrollPane;//信息显示的滚动条
	JTextField showStatus; //显示用户链接状态
	JLabel sendToLabel,messageLabel;
	JTextField sysMessage;//服务端消息的发送
	JButton sysMessageButton; //服务端消息的发送按钮
	UserLinkList userLinkList;//用户链表
	
	//建立菜单栏
	JMenuBar jMenuBar = new JMenuBar();
	//建立菜单组 
	JMenu serviceMenu = new JMenu("服务(V)");
	//建立菜单项
	JMenuItem portItem = new JMenuItem("端口设置(P)");
	JMenuItem startItem = new JMenuItem("启动服务(S)");
	JMenuItem stopItem = new JMenuItem("停止服务(T)");
	JMenuItem exitItem = new JMenuItem("退出(X)");
	
	JMenu helpMenu = new JMenu("帮助（H）");
	JMenuItem helpItem =new JMenuItem("帮助(H)");
	
	//建立工具栏
	JToolBar toolBar = new JToolBar();
	
	//建立工具栏里面的按键组件
	JButton portSet; //启动服务器端口监听
	JButton startServer; //启动服务端监听
	JButton stopServer; // 关闭服务端监听
	JButton exitButton;// 推出按钮
	
	// 模板的大小
	Dimension faceSize =  new Dimension(400,600);
	
	ServerListen listenThread;
	
    JPanel downPanel;
    GridBagLayout gridBag;
    GridBagConstraints gridBagCon;
 /*
  * 服务器构建函数
  * */

    public ChatServer(){
    	init();  //初始化程序
    	
    	//添加框架的关闭事件处理
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.pack();
    	//设置框架大小
    	this.setSize(faceSize);
    	
    	
    	//设置运行时窗口的位置
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	this.setLocation((int)(screenSize.width - faceSize.getWidth())/2, (int)(screenSize.height - faceSize.getHeight())/2);
    	this.setResizable(false);
    	this.setTitle("聊天室服务端"); //设置标题
    	
    	
    
    	show();
    	
    	
   
    }
    
   /*
    * 程序初始化函数
    * */
  public void init(){
	  Container contentPane = getContentPane();
	  contentPane.setLayout(new BorderLayout());
	  
	  //添加菜单栏
	  
	  serviceMenu.add(portItem);
	  serviceMenu.add(startItem);
	  serviceMenu.add(stopItem);
	  serviceMenu.add(exitItem);
	  jMenuBar.add(serviceMenu);
	  helpMenu.add(helpItem);
	  jMenuBar.add(helpMenu);
	  setJMenuBar(jMenuBar);
	  
	  //初始化按钮
	  
	  
	  portSet =  new JButton("端口设置");
	  startServer = new JButton("启动服务");
	  stopServer = new JButton("停止服务");
	  exitButton = new JButton("退出");
	  
	  
	  //将按钮添加到工具栏
	  toolBar.add(portSet);
	  toolBar.addSeparator();
	  toolBar.add(startServer);
	  toolBar.add(stopServer);
	  toolBar.addSeparator();
	  toolBar.add(exitButton);
	  
	  contentPane.add(toolBar, BorderLayout.NORTH);
	  
	  //初始时，令停止服务按钮不可用
	  stopServer.setEnabled(false);
	  stopItem.setEnabled(false);
	  
	  
	  //为菜单栏添加事件监听
	  portItem.addActionListener(this);
	  startItem.addActionListener(this);
	  stopItem.addActionListener(this);
	  exitItem.addActionListener(this);
	  helpItem.addActionListener(this);
	  
	  //添加按钮的事件监听
	  
	 portSet.addActionListener(this);
	 startServer.addActionListener(this); 
	 stopServer.addActionListener(this);
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
	 
	 showStatus = new JTextField(31);
	 showStatus.setEditable(false);
	 
	 sysMessage = new JTextField(24);
	 sysMessage.setEnabled(false);
	 sysMessageButton = new JButton();
	 sysMessageButton.setText("发送");
	 
	 //添加系统消息的事件监听
	 sysMessage.addActionListener(this);
	 sysMessageButton.addActionListener(this);
	 
	 sendToLabel = new JLabel("发送至");
	 messageLabel = new JLabel("发送消息：");
	 downPanel = new JPanel();
	 gridBag = new GridBagLayout();
	 downPanel.setLayout(gridBag);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 0;
	 gridBagCon.gridy = 0;
	 gridBagCon.gridwidth = 0;
	 gridBagCon.gridheight = 2;
	 gridBagCon.ipadx = 5;
	 gridBagCon.ipady = 5;
	 
	 JLabel none =  new JLabel("	");
	 gridBag.setConstraints(none,gridBagCon);
	 downPanel.add(none);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 0;
	 gridBagCon.gridy = 2;
	 gridBagCon.insets = new Insets(1,0,0,0);
   	 gridBagCon.ipadx = 5;
	 gridBagCon.ipady = 5;  
	 gridBag.setConstraints(sendToLabel,gridBagCon);
	 downPanel.add(sendToLabel);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 1;
	 gridBagCon.gridy =2;
	 
	 gridBagCon.anchor = GridBagConstraints.LINE_START;
	 gridBag.setConstraints(messageLabel,gridBagCon);
	 downPanel.add(combobox);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 0;
	 gridBagCon.gridy = 3;
	 gridBag.setConstraints(messageLabel,gridBagCon);
	 downPanel.add(messageLabel);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 1;
	 gridBagCon.gridy = 3;
	 gridBag.setConstraints(sysMessage,gridBagCon);
	 downPanel.add(sysMessage);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 2;
	 gridBagCon.gridy = 3;
	 gridBag.setConstraints(sysMessageButton,gridBagCon);
	 downPanel.add(sysMessageButton);
	 
	 gridBagCon = new GridBagConstraints();
	 gridBagCon.gridx = 0;
	 gridBagCon.gridy = 4;
	 gridBagCon.gridwidth = 3;
	 gridBag.setConstraints(showStatus,gridBagCon);
	 downPanel.add(showStatus); 
	 
	 contentPane.add(messageScrollPane, BorderLayout.CENTER);
	 contentPane.add(downPanel, BorderLayout.SOUTH);
	 
	 //关闭程序时的操作
	 
	    this.addWindowListener(
			  new WindowAdapter(){
				  public void windowClosing(WindowEvent e){
					  stopService();
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
	   if( obj == startServer || obj == startItem){
                 //启动服务端
		       startService();
	   }
	   else if(obj == stopServer || obj == stopItem){ //停止服务器
		   int j = JOptionPane.showConfirmDialog(this, "真的停止服务么？","停止服务",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		    
		   if( j == JOptionPane.YES_OPTION){
			   stopService();
		   }
	   }
	   else if ( obj == portSet || obj == portItem) {
		     //端口设置   跳出端口设置对话框
		   PortConf portConf = new PortConf(this);
		   portConf.setVisible(true);
	   }
	   else if( obj == exitButton || obj == exitItem){
		   //退出函数
		   int j = JOptionPane.showConfirmDialog(this, "真的要退出么？","退出",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		   if( j == JOptionPane.YES_OPTION){
			    stopService();
			   
			    System.exit(0);
		   }
	   }
	   else if( obj == helpItem){
		   //菜单栏里的帮助  调出帮助对话框
		   Help  helpDialog = new Help(this);
		   helpDialog.setVisible(true);
		   
	   }
	   else if( obj == sysMessage || obj == sysMessageButton){
		   //发送系统消息
		   sendSystemMessage();
	   }
   }
	   /*
	    * 启动服务器
	    * */
 public void startService(){
	  try{
		  serverSocket = new ServerSocket(port, 10);
		  messageShow.append("服务端已经启动,在"+port+"在端口监听...... \n");
		  startServer.setEnabled(false);
		  startItem.setEnabled(false);
		  portSet.setEnabled(false);
		  portItem.setEnabled(false);
		  
		  stopServer.setEnabled(true);
		  stopItem.setEnabled(true);
		  
		  sysMessage.setEnabled(true);
		  sysMessageButton.setEnabled(true);
		  
	  }
	  catch(Exception e){
		  //System.out.println(e);
	  }
	  
	  userLinkList = new UserLinkList();
	 
	  listenThread = new ServerListen(serverSocket,combobox,messageShow,showStatus,userLinkList);
	  
	  listenThread.start();
	   
 }
	   /*
	    * 关闭服务qi
	    * */
 
 
 
 public void stopService(){
      try{
    	  //向所有发送服务器关闭的消息
    	  sendStopToAll();
    	  
    	  listenThread.isStop = true;
    	  serverSocket.close();
    	  int count = userLinkList.getCount();
    	  int i = 0;
    	  while(i < 0){
    		  Node node = userLinkList.findUser(i);
    		  node.input.close();
    		  node.output.close();
    		  node.socket.close();
    		  i++;
    	  }
    	 
    	  stopServer.setEnabled(false);
    	  stopItem.setEnabled(false);
    	  startServer.setEnabled(true);
    	  startItem.setEnabled(true);
    	  portSet.setEnabled(true);
    	  portItem.setEnabled(true);
    	  sysMessage.setEnabled(false);
    	  messageShow.append("服务端已经关闭\n");
    	  
    	  combobox.removeAllItems();
    	  combobox.addItem("所有人");
    	;
    	  
      }
      catch(Exception e){
    	  //System.out.println("");
    	  
      }
	}
 
 /*
  * 向所有人发送服务器关闭的消息
  * */
 
 public void sendStopToAll(){
	 
	 int count = userLinkList.getCount();
	  
	 int i = 0;
	 while(i< count) {
		 Node node = userLinkList.findUser(i);
		 if(node == null){
			 i++;
		     continue;
		 }
		 
		 try{
			 node.output.writeObject("服务关闭");
			 node.output.flush();
		 }catch(Exception e){
			 // System.out.printlb("123123");
		 }
		i++;
		 
	 }
 }
 
 /*
  * 向所有人发送消息 
  * 
  * 
  * 
  * */
 
 public void sendMsgToAll(String msg){
	 int count = userLinkList.getCount();
	 int i = 0;
	 
	 while(i < count){
		 Node  node = userLinkList.findUser(i);
		 if(node == null){
			 i++;
			 continue;
		 }
		 try{
			 node.output.writeObject("系统信息");
			 node.output.flush();
			 node.output.writeObject(msg);
			 node.output.flush();
		 }catch(Exception e){
			 //System.out.println("@@@" +e);
		 }
		 i++;
		
	 }
	 sysMessage.setText("");
	 
 }
 
 
 

 /*
  * 向用户客户端发送消息
  * */
 
 public void sendSystemMessage(){
	 String toSomebody = combobox.getSelectedItem().toString();
	 String message = sysMessage.getText() + "\n";
	 
	 messageShow.append(message);
	 //向所有人发送消息
	 if(toSomebody.equalsIgnoreCase("所有人")){
		 sendMsgToAll(message);
	 }
	 else {
		 
		 Node node = userLinkList.findUser(toSomebody);
		 
		 try{
			 node.output.writeObject("系统信息");
			 node.output.flush();
			 node.output.writeObject(message);
			 node.output.flush();
		 }
		 catch(Exception e){
			 //System.oot.printlb("");
			 
		 }
		 
		 
	 }
	 sysMessage.setText("");
	  
 }
 /*
  *  通过给定的文件名获得图像
  * 
  */

 public static void main(String args[]){
	 ChatServer app = new ChatServer();
 }
   
	   
	   
}



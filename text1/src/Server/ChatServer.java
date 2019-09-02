package Server;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
/**
   *�����ҷ�������������;
   */
public class ChatServer extends JFrame implements ActionListener{
	 
	public static int port = 8888;  //����������������
	
	ServerSocket serverSocket;//����˵�Socket
	//Image icon; //����ͼ��
	JComboBox combobox;//ѡ������Ϣ�Ľ�����
	JTextArea messageShow; //����˵���Ϣ��ʾ
	JScrollPane messageScrollPane;//��Ϣ��ʾ�Ĺ�����
	JTextField showStatus; //��ʾ�û�����״̬
	JLabel sendToLabel,messageLabel;
	JTextField sysMessage;//�������Ϣ�ķ���
	JButton sysMessageButton; //�������Ϣ�ķ��Ͱ�ť
	UserLinkList userLinkList;//�û�����
	
	//�����˵���
	JMenuBar jMenuBar = new JMenuBar();
	//�����˵��� 
	JMenu serviceMenu = new JMenu("����(V)");
	//�����˵���
	JMenuItem portItem = new JMenuItem("�˿�����(P)");
	JMenuItem startItem = new JMenuItem("��������(S)");
	JMenuItem stopItem = new JMenuItem("ֹͣ����(T)");
	JMenuItem exitItem = new JMenuItem("�˳�(X)");
	
	JMenu helpMenu = new JMenu("������H��");
	JMenuItem helpItem =new JMenuItem("����(H)");
	
	//����������
	JToolBar toolBar = new JToolBar();
	
	//��������������İ������
	JButton portSet; //�����������˿ڼ���
	JButton startServer; //��������˼���
	JButton stopServer; // �رշ���˼���
	JButton exitButton;// �Ƴ���ť
	
	// ģ��Ĵ�С
	Dimension faceSize =  new Dimension(400,600);
	
	ServerListen listenThread;
	
    JPanel downPanel;
    GridBagLayout gridBag;
    GridBagConstraints gridBagCon;
 /*
  * ��������������
  * */

    public ChatServer(){
    	init();  //��ʼ������
    	
    	//��ӿ�ܵĹر��¼�����
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.pack();
    	//���ÿ�ܴ�С
    	this.setSize(faceSize);
    	
    	
    	//��������ʱ���ڵ�λ��
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	this.setLocation((int)(screenSize.width - faceSize.getWidth())/2, (int)(screenSize.height - faceSize.getHeight())/2);
    	this.setResizable(false);
    	this.setTitle("�����ҷ����"); //���ñ���
    	
    	
    
    	show();
    	
    	
   
    }
    
   /*
    * �����ʼ������
    * */
  public void init(){
	  Container contentPane = getContentPane();
	  contentPane.setLayout(new BorderLayout());
	  
	  //��Ӳ˵���
	  
	  serviceMenu.add(portItem);
	  serviceMenu.add(startItem);
	  serviceMenu.add(stopItem);
	  serviceMenu.add(exitItem);
	  jMenuBar.add(serviceMenu);
	  helpMenu.add(helpItem);
	  jMenuBar.add(helpMenu);
	  setJMenuBar(jMenuBar);
	  
	  //��ʼ����ť
	  
	  
	  portSet =  new JButton("�˿�����");
	  startServer = new JButton("��������");
	  stopServer = new JButton("ֹͣ����");
	  exitButton = new JButton("�˳�");
	  
	  
	  //����ť��ӵ�������
	  toolBar.add(portSet);
	  toolBar.addSeparator();
	  toolBar.add(startServer);
	  toolBar.add(stopServer);
	  toolBar.addSeparator();
	  toolBar.add(exitButton);
	  
	  contentPane.add(toolBar, BorderLayout.NORTH);
	  
	  //��ʼʱ����ֹͣ����ť������
	  stopServer.setEnabled(false);
	  stopItem.setEnabled(false);
	  
	  
	  //Ϊ�˵�������¼�����
	  portItem.addActionListener(this);
	  startItem.addActionListener(this);
	  stopItem.addActionListener(this);
	  exitItem.addActionListener(this);
	  helpItem.addActionListener(this);
	  
	  //��Ӱ�ť���¼�����
	  
	 portSet.addActionListener(this);
	 startServer.addActionListener(this); 
	 stopServer.addActionListener(this);
	 exitButton.addActionListener(this);
	 
	 combobox = new JComboBox();
	 combobox.insertItemAt("������", 0);
	 combobox.setSelectedIndex(0);
	  
	 messageShow = new JTextArea();
	 messageShow.setEditable(false);
	 
	 //��ӹ�����
	  
	 messageScrollPane = new JScrollPane(messageShow,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	 messageScrollPane.setPreferredSize(new Dimension(400,400));
	 messageScrollPane.revalidate();
	 
	 showStatus = new JTextField(31);
	 showStatus.setEditable(false);
	 
	 sysMessage = new JTextField(24);
	 sysMessage.setEnabled(false);
	 sysMessageButton = new JButton();
	 sysMessageButton.setText("����");
	 
	 //���ϵͳ��Ϣ���¼�����
	 sysMessage.addActionListener(this);
	 sysMessageButton.addActionListener(this);
	 
	 sendToLabel = new JLabel("������");
	 messageLabel = new JLabel("������Ϣ��");
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
	 
	 //�رճ���ʱ�Ĳ���
	 
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
      * �¼�����
      * */  
   public void actionPerformed(ActionEvent e){
	   Object obj = e.getSource();
	   if( obj == startServer || obj == startItem){
                 //���������
		       startService();
	   }
	   else if(obj == stopServer || obj == stopItem){ //ֹͣ������
		   int j = JOptionPane.showConfirmDialog(this, "���ֹͣ����ô��","ֹͣ����",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		    
		   if( j == JOptionPane.YES_OPTION){
			   stopService();
		   }
	   }
	   else if ( obj == portSet || obj == portItem) {
		     //�˿�����   �����˿����öԻ���
		   PortConf portConf = new PortConf(this);
		   portConf.setVisible(true);
	   }
	   else if( obj == exitButton || obj == exitItem){
		   //�˳�����
		   int j = JOptionPane.showConfirmDialog(this, "���Ҫ�˳�ô��","�˳�",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		   if( j == JOptionPane.YES_OPTION){
			    stopService();
			   
			    System.exit(0);
		   }
	   }
	   else if( obj == helpItem){
		   //�˵�����İ���  ���������Ի���
		   Help  helpDialog = new Help(this);
		   helpDialog.setVisible(true);
		   
	   }
	   else if( obj == sysMessage || obj == sysMessageButton){
		   //����ϵͳ��Ϣ
		   sendSystemMessage();
	   }
   }
	   /*
	    * ����������
	    * */
 public void startService(){
	  try{
		  serverSocket = new ServerSocket(port, 10);
		  messageShow.append("������Ѿ�����,��"+port+"�ڶ˿ڼ���...... \n");
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
	    * �رշ���qi
	    * */
 
 
 
 public void stopService(){
      try{
    	  //�����з��ͷ������رյ���Ϣ
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
    	  messageShow.append("������Ѿ��ر�\n");
    	  
    	  combobox.removeAllItems();
    	  combobox.addItem("������");
    	;
    	  
      }
      catch(Exception e){
    	  //System.out.println("");
    	  
      }
	}
 
 /*
  * �������˷��ͷ������رյ���Ϣ
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
			 node.output.writeObject("����ر�");
			 node.output.flush();
		 }catch(Exception e){
			 // System.out.printlb("123123");
		 }
		i++;
		 
	 }
 }
 
 /*
  * �������˷�����Ϣ 
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
			 node.output.writeObject("ϵͳ��Ϣ");
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
  * ���û��ͻ��˷�����Ϣ
  * */
 
 public void sendSystemMessage(){
	 String toSomebody = combobox.getSelectedItem().toString();
	 String message = sysMessage.getText() + "\n";
	 
	 messageShow.append(message);
	 //�������˷�����Ϣ
	 if(toSomebody.equalsIgnoreCase("������")){
		 sendMsgToAll(message);
	 }
	 else {
		 
		 Node node = userLinkList.findUser(toSomebody);
		 
		 try{
			 node.output.writeObject("ϵͳ��Ϣ");
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
  *  ͨ���������ļ������ͼ��
  * 
  */

 public static void main(String args[]){
	 ChatServer app = new ChatServer();
 }
   
	   
	   
}



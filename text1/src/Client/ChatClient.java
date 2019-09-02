package Client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import Server.Help;

import java.io.*;
import java.net.*;

/*
 * ����ͻ��˵��������
 * */


public class ChatClient extends JFrame implements ActionListener{
	
	String  ip = "127.0.0.1";//���ӵ�����˵�iP��ַ
	int port = 8888;//���ӵ�����˵Ķ˿ں�
	String  userName = "�Ҵҹ���";  //�û���
	int type = 0; // 0 ��ʾδ���ӣ�1��ʾ������

	//Image icon;  //����ͼ��
	JComboBox combobox; //ѡ������Ϣ�Ľ�����
	JTextArea messageShow;//�ͻ��˵���Ϣ��ʾ
	JScrollPane messageScrollPane; //��Ϣ��ʾ�Ĺ�����
	
	JLabel express,sendToLabel,messageLabel;
	
	JTextField clientMessage; //�ͻ�����Ϣ����
	JCheckBox checkbox; // ���Ļ�
	JComboBox actionlist;// ����ѡ��
	JButton clientMessageButton; //������Ϣ
	JTextField showStatus; // ��ʾ�û�����״̬
	
	Socket socket;
	ObjectOutputStream output; // �����׽��������
	ObjectInputStream input; // �����׽���������
	
	ClientReceive recThread;
	//�����˵���
	JMenuBar jMenuBar = new JMenuBar();
	//�����˵���
	JMenu operateMenu = new JMenu("������O��");
	//�����˵���
	JMenuItem loginItem = new JMenuItem("�û���½(T)");
	JMenuItem logoffItem = new JMenuItem("�û�ע��(L)");
	JMenuItem exitItem = new JMenuItem("�˳�(X)");
	
	JMenu conMenu = new JMenu("����(C	)");
	JMenuItem userItem = new JMenuItem("�û�����(U)");
	JMenuItem connectItem = new JMenuItem("��������(C)");
	
	JMenu helpMenu = new JMenu("����(H)");
	JMenuItem helpItem = new JMenuItem("����(H)");
	
	//����������
	JToolBar toolBar = new JToolBar();
	//�����������еİ�ť���
	JButton loginButton; //�û���½
	JButton logoffButton;//�û�ע��
	JButton userButton;//�û���Ϣ������
	JButton connectButton;//��������
	JButton exitButton; // �˳���ť
	
	//��ܴ�С
	Dimension faceSize = new Dimension(400,600);
	
	JPanel downPanel;
	GridBagLayout gridBag;
	GridBagConstraints gridBagCon;
	
public ChatClient(){
	init(); //��ʼ������
	
	
	// ��ӿ�ܵĹر��¼�����
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.pack();
	//���ÿ�ܵĴ�С
	this.setSize(faceSize);
	//��������ʱ���ڵ�λ��
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.setLocation((int)(screenSize.width - faceSize.getWidth())/2, (int)(screenSize.height-faceSize.getHeight())/2);
	this.setResizable(false);
	this.setTitle("�����ҿͻ���"); //���ñ���
	show();

}

/*
 *�����ʼ������
 * */
	
public void init(){
	
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());
	
	//��Ӳ˵���

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
    
    //��ʼ����ť
    loginButton = new JButton("��½");
    logoffButton = new JButton("ע��");
    userButton = new JButton("�û�����");
    connectButton = new JButton("��������");
    exitButton = new JButton ("�˳�");
    
    //�������� ��ʾ��Ϣ
    
    loginButton.setToolTipText("���ӵ�ָ���ķ�����");
    logoffButton.setToolTipText("��������Ͽ�����");
    userButton.setToolTipText("�����û���Ϣ");
    connectButton.setToolTipText("������Ҫ���ӵ��ķ�������Ϣ");
    
    //��ť��Ӵ򹤾���
   
    toolBar.add(userButton);
    toolBar.add(connectButton);
    toolBar.addSeparator();
    toolBar.add(loginButton);
    toolBar.add(logoffButton);
    toolBar.addSeparator();
    toolBar.add(exitButton);
    
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    checkbox = new JCheckBox("���Ļ�");
    checkbox.setSelected(false);
    
    actionlist = new JComboBox();
    actionlist.addItem("΢Ц��");
    actionlist.addItem("���˵�");
    actionlist.addItem("�����");
    actionlist.addItem("������");
    actionlist.addItem("С�ĵ�");
    actionlist.addItem("������");
    actionlist.setSelectedIndex(0);
    
    //��ʼʱ
    
    loginButton.setEnabled(true);
    logoffButton.setEnabled(false);
    
    //Ϊ�˵�������¼�����
    loginItem.addActionListener(this);
    logoffItem.addActionListener(this);
    exitItem.addActionListener(this);
    userItem.addActionListener(this);
    connectItem.addActionListener(this);
    helpItem.addActionListener(this);
    
    
    // ��Ӱ�ť���¼�����
    
    loginButton.addActionListener(this);
    logoffButton.addActionListener(this);
    userButton.addActionListener(this);
    connectButton.addActionListener(this);
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
    
    clientMessage = new JTextField(18);
    clientMessage.setEnabled(false);
    clientMessageButton = new JButton();
    clientMessageButton.setText("����");
    
    //���ϵͳ��Ϣ���¼�����
    
    clientMessage.addActionListener(this);
    clientMessageButton.addActionListener(this);
    
    sendToLabel = new JLabel("������");
    express = new JLabel("        ���� ��");
    messageLabel = new JLabel("������Ϣ");
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
    
    // �رճ���ʱ�ز���
    
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
 * �¼�����
 * */
public void actionPerformed(ActionEvent e){
	Object obj = e.getSource();
	
	if(obj == userItem || obj == userButton){
		//�û���Ϣ����
		//�����û���Ϣ���öԻ���
		UserConf userConf = new UserConf(this,userName);
		userConf.setVisible(true); 
		userName = userConf.userInputName;
	}
	else if( obj == connectItem || obj == connectButton){
		//���ӷ���������
		//�����������öԻ���
		ConnectConf connectConf = new ConnectConf(this, ip, port);
        connectConf.setVisible(true);
        ip = connectConf.userInputIp;
        port = connectConf.userInputPort;
        System.out.println(port);
	}
	else if( obj == loginItem || obj == loginButton){
		//��½
		Connect();
		
	}
	else if(  obj ==logoffItem || obj ==logoffButton)
	{
		//ע��
		DisConnect();
		//showStatus.setText("");
	}
	else if ( obj == clientMessage || obj == clientMessageButton){
		//������Ϣ
		
		SendMessage();
		clientMessage.setText("");
	}
	else if(obj == exitItem || obj== exitButton){
		//�˳�
		int j = JOptionPane.showConfirmDialog(this,"���Ҫ�˳�ô��","�˳�",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		if( j ==JOptionPane.YES_OPTION){
			if(type == 1){
				DisConnect();
				
			}
			System.exit(0);
		}
	}
	else if(obj == helpItem ){
		//�˵����еذ���
	  // ���������Ի���
		Help helpDialog = new Help(this);
		helpDialog.show();
	}
}

public void Connect(){
	try{
		socket = new Socket(ip,port);
	}
	catch(Exception e) {
		  JOptionPane.showConfirmDialog(this,"�������ӵ�ָ���������� \n ��ȷ�����������Ƿ���ȷ","��ʾ",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
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
	    messageShow.append("���ӷ�����"+ip+":"+port+"�ɹ�...\n");
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
		output.writeObject("�û�����");
		input.close();
		output.flush();
		output.close();
		socket.close();
		messageShow.append("�Ѿ���������Ͽ�����... \n");
		type = 0;// ��ʾδ����
	}catch(Exception e){
		
	}
	
}


public void SendMessage(){
	String toSomebody = combobox.getSelectedItem().toString();
	String status ="";
	if(checkbox.isSelected()){
		 status = "���Ļ�";
	}
	String action = actionlist.getSelectedItem().toString();
	String message = clientMessage.getText();
	
	
	if(socket.isClosed()){
		return;
	}

	try{
		output.writeObject("������Ϣ");
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



















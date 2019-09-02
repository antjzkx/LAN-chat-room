package Server;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;


public class ServerListen extends Thread{
     
	ServerSocket server;
	JComboBox combobox;
	JTextField textfield;
	JTextArea textarea;
	UserLinkList userLinkList; //用户列表
	
	Node client;
	ServerReceive recvThread;
	
	public boolean isStop;
	/*
	 * 聊天服务器的用户上线与下线监听
	 * */
    
	
public ServerListen(ServerSocket server,JComboBox combobox,JTextArea textarea,JTextField textfield,UserLinkList userLinkList){
	
	      this.server = server;
	      this.combobox = combobox;
	      this.textarea = textarea;
	      this.textfield = textfield;
	      this.userLinkList = userLinkList;
	      
	      isStop = false;
	      
	      
	    		  
	}
	
public void run(){
	
	while(!isStop && !server.isClosed()){
		 try{
			 client = new Node();
			 client.socket = server.accept();
			 client.output = new ObjectOutputStream(client.socket.getOutputStream());
			 client.output.flush();
			 client.input = new ObjectInputStream(client.socket.getInputStream());
			 client.username = (String) client.input.readObject();
			 //显示提示消息
			 combobox.addItem(client.username);
			 userLinkList.addUser(client);
			 textarea.append("用户"+ client.username+"上线"+"\n");
			 textfield.setText("在线用户"+userLinkList.getCount()+"人\n");
			 recvThread = new ServerReceive(textarea,textfield,combobox,client,userLinkList);
			  recvThread.start();
			  
		 }
		 catch(Exception e) {}
	}
	
	
	
	
	
}	
}

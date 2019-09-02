package Client;
import javax.swing.*;
import java.io.*;
import java.net.*;



public class ClientReceive extends Thread {
    private JComboBox combobox;
    private JTextArea textarea;
    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;
    
    JTextField showStatus;
    
 
ClientReceive(Socket socket,ObjectOutputStream output,ObjectInputStream input,JComboBox combobox,JTextArea textarea,JTextField showStatus){
	 
	    this.socket = socket;
	    this.output = output;
	    this.input = input;
	    this.combobox = combobox;
	    this.textarea = textarea;
	    this.showStatus = showStatus;
	
}
    
public void run(){
	while(!socket.isClosed()){
		try{
			
			String type = (String)input.readObject();
			
			if(type.equalsIgnoreCase("ϵͳ��Ϣ")){
				String sysmsg = (String)input.readObject();
				textarea.append("ϵͳ��Ϣ"+sysmsg);
			}
			else if(type.equalsIgnoreCase("����ر�")){
				output.close();
				input.close();
				socket.close();
				textarea.append("�������Ѿ��رգ� \n");
				break;
			}
			else if(type.equalsIgnoreCase("������Ϣ")){
				String messag = (String)input.readObject();
				textarea.append(messag);
            } 
			else if(type.equalsIgnoreCase("�û��б�")){
				String userlist = (String) input.readObject();
				String usernames[] =  userlist.split("\n");
				combobox.removeAllItems();
				
				int i = 0;
				combobox.addItem("������");
				while(i < usernames.length){
					combobox.addItem(usernames[i]);
					i ++;
				}
				combobox.setSelectedIndex(0);
				showStatus.setText("�����û�"+usernames.length+"��");
				
			}
		
		}catch(Exception e){
			System.out.println(e);
		}
	 
	
	}
}   
    
}
package servers;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import records.Record;
import thread.UdpListener2;

public class Server2 implements CenterServer{
	private HashMap<Character,ArrayList<Record>> DDOServer2;
	private HashMap<Character,ArrayList<Record>> MTLServer2;
	private HashMap<Character,ArrayList<Record>> LVLServer2;
    private File loggingFileDDO = new File("DDOServer2.txt");
    private File loggingFileMTL = new File("MTLServer2.txt");
    private File loggingFileLVL = new File("LVLServer2.txt");
    private String message;
    
    public Server2() {
		DDOServer2 = new HashMap<>();
		MTLServer2 = new HashMap<>();
		LVLServer2 = new HashMap<>();
		this.message = "";
	}
    
    public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
    public static void main(String[] args) {
    	int port=5002;
//    	byte[] reply = new byte[1000];
//    	boolean flag;
//    	String replyMessage = null;
    	
    	Server2 server2 = new Server2();
    	new UdpListener2(port,server2).start();
    	
		while(true){
			// get message from the UdpListener
			if(server2.getMessage().equals("")){// it is a backup 
				multicast(server2.getMessage(),server2);
			}
			
    	
		}
	}

    public static void operation(String message,Server2 server2){
		String[] strings = message.split(",");
    	switch(strings[0]){
    		case "1":
    			server2.createTRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
    			break;
    		case "2":
    			server2.createSRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]);
    			break;
    		case "3":
    			server2.getRecordCounts(strings[1]);
    			break;
    		case "4":
    			server2.editRecord(strings[1], strings[2], strings[3], strings[4]);
    			break;
    		case "5":
    			server2.transferRecord(strings[1], strings[2], strings[3]);
    			break;
    		case "7":
    			server2.getRecordInfo(strings[1],strings[2]);
    			break;
    		default:
    			System.out.println("error!");
    	}
	}

	public static void multicast(String message,Server2 server2){//as a backup
    	
    	//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
//        	System.setProperty("java.net.preferIPv4Stack", "true");
        	InetAddress group = InetAddress.getByName("228.5.6.7");
        	socket = new MulticastSocket(6789);
        	socket.joinGroup(group);
        	byte[] m = "Server2 finish".getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	
        	byte[] buffer = new byte[1000];
//        	for(int i=0;i<=2;i++){ // get messages from others in group
        		System.out.println("receiving...");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve: "+ new String(messageIn.getData()));
        		
        		operation(new String(messageIn.getData()), server2);
        		socket.send(messageOut);
//        	}
        	System.out.println("Server2");
        	socket.leaveGroup(group);
        }catch(SocketException e){
        	System.out.println("Socket: " + e.getMessage());
        	e.printStackTrace();
        }catch (IOException e) {
        	System.out.println("IO: " + e.getMessage());
		}
        finally {
        	if(socket != null) 
        		socket.close();
        }
	}

	public void multicast2(String message){// as a primary replica
		//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
        	System.setProperty("java.net.preferIPv4Stack", "true");
        	InetAddress group = InetAddress.getByName("228.5.6.7");
        	socket = new MulticastSocket(6789);
        	socket.joinGroup(group);
        	byte[] m = "Server2 nihao".getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	socket.send(messageOut);
        	byte[] buffer = new byte[1000];
        	for(int i=0;i<2;i++){  // get messages from others in group
        		System.out.println("receiving");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve:"+ new String(messageIn.getData()));
        	}
        	System.out.println("Server2");
        	socket.leaveGroup(group);
        }catch(SocketException e){
        	System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
        	System.out.println("IO: " + e.getMessage());
		}
        finally {
        	if(socket != null) 
        		socket.close();
		}
	}

	@Override
	public boolean createTRecord(String managerId, String firstName, String lastName, String address, String phone,
			String specialization, String location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered,
			String status, String date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRecordCounts(String managerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean editRecord(String managerId, String recordID, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean transferRecord(String managerId, String recordID, String remoteCenterServerName) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getRecordInfo(String managerId, String recordID) {
		// TODO Auto-generated method stub
		return null;
	}

}

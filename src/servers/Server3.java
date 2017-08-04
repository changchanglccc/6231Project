package servers;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import records.Record;
import sun.misc.Queue;

public class Server3 implements CenterServer{
	private HashMap<Character,ArrayList<Record>> DDOServer3;
	private HashMap<Character,ArrayList<Record>> MTLServer3;
	private HashMap<Character,ArrayList<Record>> LVLServer3;
	private LinkedList<String> queue;
    private File loggingFile;
    private File loggingFileMTL;
    private File loggingFileLVL;
    
    public static void main(String[] args) {
    	multicast();
    }
    
	

	public static void multicast(){
    	
    	//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
        	System.setProperty("java.net.preferIPv4Stack", "true");
        	InetAddress group = InetAddress.getByName("228.5.6.7");
        	socket = new MulticastSocket(6789);
        	socket.joinGroup(group);
        	byte[] m = "Server3 nihao".getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	
        	byte[] buffer = new byte[1000];
        	for(int i=0;i<=2;i++){ // get messages from others in group
        		System.out.println("receiving");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve:"+ new String(messageIn.getData()));
        		socket.send(messageOut);
        	}
        	System.out.println("Server3");
//        	socket.leaveGroup(group);
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
	public String getRecordInfo(String recordID) {
		// TODO Auto-generated method stub
		return null;
	}

}

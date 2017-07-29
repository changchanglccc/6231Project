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
import sun.misc.Queue;

public class Server3 {
	private HashMap<Character,ArrayList<Record>> DDOServer3;
	private HashMap<Character,ArrayList<Record>> MTLServer3;
	private HashMap<Character,ArrayList<Record>> LVLServer3;
	private Queue<String> queue;
    private File loggingFile;
    
public static void main(String[] args) {
    	
    	//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
        	InetAddress group = InetAddress.getByName(args[1]);
        	socket = new MulticastSocket(6791);
        	socket.joinGroup(group);
        	byte[] m = args[0].getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	socket.send(messageOut);
        	byte[] buffer = new byte[1000];
        	for(int i=0;i<3;i++){  // get messages from others in group
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve:"+ new String(messageIn.getData()));
        	}
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

}

package thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import servers.CommonServer;
import servers.Server2;

public class UdpListener2 extends Thread{

    private String message;
    private int port;
    private CommonServer commonServer;
    private Server2 server2;
    private boolean flag;
	private String replyMessage;

    public UdpListener2(CommonServer server, int portNumber,Server2 server2){
        this.port=portNumber;
        this.commonServer = server;
        this.server2 = server2;
        this.message = "";
        this.flag = false;
        this.replyMessage = "";
    }


    public String getMessage() {
		return message;
	}


	@Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {
            //create belonging socket
            datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            byte[] reply = new byte[1000];
//            System.out.println(centerServerImp.centerName+"is ready to listen UDP requests between servers");
            //listening
            while(true){
//            	message="";
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(request);
                message=new String(request.getData());
                System.out.println("updListener: "+getMessage());
                commonServer.setMessage(message);

                
                if(!message.equals("")){
    		    	String[] strings = message.split(",");
    		    	switch(strings[0]){
    		    		case "1":
    		    			flag = server2.createTRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
    		    			server2.multicast(message);
    		    			reply = String.valueOf(flag).getBytes();
    		    			break;
    		    		case "2":
    		    			flag = server2.createSRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]);
    		    			server2.multicast(message);
    		    			reply = String.valueOf(flag).getBytes();
    		    			break;
    		    		case "3":
    		    			replyMessage = server2.getRecordCounts(strings[1]);
    		    			reply = replyMessage.getBytes();
    		    			break;
    		    		case "4":
    		    			flag = server2.editRecord(strings[1], strings[2], strings[3], strings[4]);
    		    			server2.multicast(message);
    		    			reply = String.valueOf(flag).getBytes();
    		    			break;
    		    		case "5":
    		    			flag = server2.transferRecord(strings[1], strings[2], strings[3]);
    		    			server2.multicast(message);
    		    			reply = String.valueOf(flag).getBytes();
    		    			break;
    		    		case "7":
    		    			replyMessage = server2.getRecordInfo(strings[1],strings[2]);
    		    			reply = replyMessage.getBytes();
    		    			break;
    		    		default:
    		    			System.out.println("error!");
    		    	}
//    		    	message="";
    		    	
    				try {
    					DatagramPacket replyPacket = new DatagramPacket(reply, reply.length, request.getAddress(), request.getPort());
    					datagramSocket.send(replyPacket);
    					
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
    }
}

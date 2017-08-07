package thread;



import servers.Server2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpHandler extends Thread{

    private InetAddress address;
    private int port;
    private DatagramSocket datagramSocket;
    private Server2[] replica;
    private String message;

    public UdpHandler(InetAddress address, int port, DatagramSocket datagramSocket, Server2[] replica, String operation){
        this.address = address;
        this.port = port;
        this.datagramSocket = datagramSocket;
        this.replica = replica;
        this.message=operation;
    }

    @Override
    public void run() {
        if(!message.equals("")){
            byte[] reply=null;

            String[] strings = message.split(",");

            //decide which center
            int index=-1;
            String recordID=strings[1].trim();
            if(recordID.startsWith("MTL"))//"MTL", "LVL", "DDO"
                index=0;
            else if(recordID.startsWith("LVL"))
                index=1;
            else if(recordID.startsWith("DDO"))
                index=2;
            if(index==-1){
                System.out.println("ERROR: Server2 - invalid recordID");
            }

            Server2 server=replica[index];

            switch(strings[0]){
                case "1":
                    boolean flag = server.createTRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
                    reply = String.valueOf(flag).getBytes();

                    break;
                case "2":
                    flag = server.createSRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]);
                    reply = String.valueOf(flag).getBytes();

                    break;
                case "3":
                    String replyMessage = server.getRecordCounts(strings[1]);
                    reply = replyMessage.getBytes();
                    break;
                case "4":
                    flag = server.editRecord(strings[1], strings[2], strings[3], strings[4]);
                    reply = String.valueOf(flag).getBytes();
                    break;
                case "5":
                    flag = server.transferRecord(strings[1], strings[2], strings[3]);
                    reply = String.valueOf(flag).getBytes();
                    break;
                case "7":
                    replyMessage = server.getRecordInfo(strings[1],strings[2]);
                    reply = replyMessage.getBytes();
                    break;
                default:
                    System.out.println("Server - receive invalid message");
            }
            try {
                DatagramPacket replyPacket = new DatagramPacket(reply, reply.length, address, port);
                datagramSocket.send(replyPacket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

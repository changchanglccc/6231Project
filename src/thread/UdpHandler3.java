package thread;


import helper.PortDefinition;
import helper.Timeout;
import servers.Server3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class UdpHandler3 extends Thread{

    //for whole sever3
    private DatagramSocket myDatagramSocket;
    private DatagramSocket acknowSocket;
    private InetAddress myinetAddress;
    //for one message
    private Server3 server;
    private DatagramPacket datagramPacket;
    //for server5
    private static ArrayList<Integer> messageIDs = new ArrayList<Integer>();
    private static ArrayList<String> results = new ArrayList<String>();
    private static Queue<String> messageQueue = new LinkedList<String>();


    public UdpHandler3(InetAddress inetAddress, DatagramSocket datagramSocket, DatagramSocket acknowSocket, Server3 replica, DatagramPacket datagramPacket) {
        this.myDatagramSocket = datagramSocket;
        this.server = replica;
        this.datagramPacket = datagramPacket;
        this.myinetAddress = inetAddress;
        this.acknowSocket=acknowSocket;
    }

    @Override
    public void run(){
        if (datagramPacket.getPort() != PortDefinition.FE_OPEARION_PORT) {      // not from FE,means backup
            String message = new String(datagramPacket.getData());
            operating(message);
        }
        else {   //form FE, means primary
            System.out.println("Receive request from FE");
            String message = new String(datagramPacket.getData());
            messageQueue.offer(message);
            //if many threads, It may not the message added above
            String messagePeek = messageQueue.peek();
            notifyExec(messagePeek, 5001);
            notifyExec(messagePeek, 5002);
            Timeout timeout = new Timeout(500);
            boolean flag5001 = false;
            boolean flag5002 = false;

            String result = operating(messagePeek);
            sentMessageForReply(result);

            //listening 1s
            timeout.startUp();
            while (timeout.flag){
                try {
                    byte[] buffer = new byte[300];
                    DatagramPacket acknow = new DatagramPacket(buffer, buffer.length);
                    acknowSocket.receive(acknow);

                    if (acknow.getPort() == 5001)
                        flag5001 = true;
                    if (acknow.getPort() == 5002)
                        flag5002 = true;

                    if (flag5001 && flag5002)
                        break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!flag5001) {
                notifyExec(messagePeek, 5001);
                System.out.println("Server2: [primary] I did not receive acknowledge from 5002,send again");
            }
            if (!flag5002) {
                notifyExec(messagePeek, 5002);
                System.out.println("Server2: [primary] I did not receive acknowledge from 5003,send again");
            }
            messageQueue.poll();
        }
    }


    public String operating(String message) {
        if (!message.equals("")) {
            byte[] reply = null;
            String[] strings = message.split(",");
            int messageId = Integer.parseInt(strings[0]);

            if (messageIDs.contains(messageId)) {    //already done
                int index = messageIDs.indexOf(messageId);
                String result = results.get(index);
                return result;

            }
            else {    //new message
                messageIDs.add(messageId);


                String result = null;
                switch (strings[1]) {
                    case "1":
                        boolean flag = server.createTRecord(strings[2], strings[3], strings[4], strings[5], strings[6], strings[7], strings[8]);
                        result = castBoolean2Return(flag);
                        break;
                    case "2":
                        flag = server.createSRecord(strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
                        result = castBoolean2Return(flag);
                        break;
                    case "3":
                        result = server.getRecordCounts(strings[2]);
                        break;
                    case "4":
                        flag = server.editRecord(strings[2], strings[3], strings[4], strings[5]);
                        result = castBoolean2Return(flag);
                        break;
                    case "5":
                        flag = server.transferRecord(strings[2], strings[3], strings[4]);
                        result = castBoolean2Return(flag);
                        break;
                    case "6":
                        result = server.getRecordInfo(strings[2], strings[3]);
                        break;
                    default:
                        System.out.println("Server - receive invalid message");
                }

                results.add(result);
                return result;
            }
        }
        return "UDP Handler - ERROR ";
    }


    public void sentMessageForReply(String content) {

        try {
        	byte[] message = new byte[2000]; 
            message = content.getBytes();
            DatagramPacket replyPacket = new DatagramPacket(message, message.length, datagramPacket.getAddress(), datagramPacket.getPort());
            myDatagramSocket.send(replyPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyExec(String content, int port) {
        try {
            byte[] message = content.getBytes();
            DatagramPacket request = new DatagramPacket(message, message.length, myinetAddress, port);
            myDatagramSocket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String castBoolean2Return(boolean b){
        if(b)
            return "SUCCESS";
        else
            return "FAIL";
    }


}

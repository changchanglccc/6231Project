package helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueActions {

    public static void main(String[] args) throws IOException {
        DatagramSocket server = new DatagramSocket(8888);
        byte[] recvBuf = new byte[100];
        Queue<String> queue = new LinkedBlockingQueue<String>();
        while (true) {
            DatagramPacket recvPacket
                    = new DatagramPacket(recvBuf, recvBuf.length);
            server.receive(recvPacket);
            String recvStr = (new String(recvPacket.getData(), 0, recvPacket.getLength())).trim();
            System.out.println(recvStr);

            if (null == recvStr || recvStr.equals("")) {
                continue;
            }

            if (recvStr.equals("200")) {
                if (!queue.isEmpty()) {
                    int port = recvPacket.getPort();
                    InetAddress addr = recvPacket.getAddress();
                    String sendStr = "done: " + queue.remove() + "\n\r";
                    byte[] sendBuf;
                    sendBuf = sendStr.getBytes();
                    DatagramPacket sendPacket
                            = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
                    server.send(sendPacket);
                }
            } else {
                queue.add(recvStr);
                if (!queue.isEmpty()) {
                    int port = recvPacket.getPort();
                    InetAddress addr = recvPacket.getAddress();
                    String sendStr = "data: " + queue.peek() + "\n\r";
                    byte[] sendBuf;
                    sendBuf = sendStr.getBytes();
                    DatagramPacket sendPacket
                            = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
                    server.send(sendPacket);
                }
            }
        }
    }

}

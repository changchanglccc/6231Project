package helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

// this is a test class to make the settimeout fifo queue work
public class QueueActions {

    public static boolean canSend = true;
    public static int delay = 0;
    public static int interval = 1000;
    public static int portNbr = 8888;

    public static void main(String[] args) throws IOException {
        final DatagramSocket server = new DatagramSocket(portNbr);
        byte[] recvBuf = new byte[100];
        Queue<String> queue = new LinkedBlockingQueue<String>();

        Timer timer = new Timer();

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

                    // clear the last timer
                    timer.cancel(); // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge(); // Removes all cancelled tasks from this timer's task queue.
                    canSend = true; // now we can send the peek

                    // prepare the job that will be send
                    int port = recvPacket.getPort();
                    InetAddress addr = recvPacket.getAddress();
                    String sendStr = "done: " + queue.remove() + "\n\r";

                    byte[] sendBuf;
                    sendBuf = sendStr.getBytes();

                    final DatagramPacket sendPacket
                            = new DatagramPacket(sendBuf, sendBuf.length, addr, port);

                    // send job to RM every 1 second till we get 200
                    canSend = false;
                    timer.schedule(new TimerTask() {
                                       @Override
                                       public void run() {
                                           try {
                                               server.send(sendPacket);
                                           } catch (IOException ioe) {
                                               System.out.println(ioe);
                                           }
                                       }
                                   },
                            delay,
                            interval
                    );

                }
            } else {
                // if we get job, we enqueue
                queue.add(recvStr);

                if (!queue.isEmpty() && canSend) {
                    int port = recvPacket.getPort();
                    InetAddress addr = recvPacket.getAddress();
                    String sendStr = "data: " + queue.peek() + "\n\r";
                    byte[] sendBuf;
                    sendBuf = sendStr.getBytes();
                    final DatagramPacket sendPacket
                            = new DatagramPacket(sendBuf, sendBuf.length, addr, port);

                    // TODO: is this right?
                    canSend = false;
                    timer.schedule(new TimerTask() {
                                       @Override
                                       public void run() {
                                           try {
                                               server.send(sendPacket);
                                           } catch (IOException ioe) {
                                               System.out.println(ioe);
                                           }
                                       }
                                   },
                            delay,
                            interval
                    );
                }
            }

        }


    }

}

package fifo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FifoUdpListener extends Thread {

    private static Lock lock;
    public static int PORT_NUMBER;
    public static DatagramSocket server = null;
    public static byte[] recvBuf = null;
    public static Queue<String> queue = null;
    public static Queue<Thread> threadList = null;
    public static TimerTaskRun timerTaskRun = null;

    public FifoUdpListener(int portNbr) {
        lock = new ReentrantLock();
        PORT_NUMBER = portNbr;
        try {
            server = new DatagramSocket(PORT_NUMBER);
        } catch (SocketException se) {
            System.err.println(se);
        }
        recvBuf = new byte[100];
        queue = new LinkedBlockingQueue<String>();
        threadList = new LinkedBlockingQueue<Thread>();
    }

    @Override
    public void run() {

        while (true) {
            final DatagramPacket recvPacket
                    = new DatagramPacket(recvBuf, recvBuf.length);
            try {
                server.receive(recvPacket);
            } catch (IOException ie) {
                System.err.println(ie);
            }

            String recvStr = (new String(recvPacket.getData(), 0, recvPacket.getLength())).trim();
            System.out.println(recvStr);
            if (null == recvStr || recvStr.equals("")) {
                continue;
            }

            // 200 means all RMs processed the task successfully
            if (recvStr.equals("200")) {

                // clear the clock
                Thread thread2 = threadList.remove();
                thread2.stop();
                System.out.println("Killed");

                // send the next task if any
                if (queue.size() != 0) {
                    queue.remove();

                    if (queue.size() != 0) {
                        // get the next message
                        final String head = queue.peek();

                        timerTaskRun = new TimerTaskRun(server, head, recvPacket);
                        Thread thread = new Thread(timerTaskRun);
                        threadList.add(thread);
                        thread.start();
                    }
                }
            } else {
                // if it's a task message
                lock.lock();
                queue.add(recvStr);
                if (queue.size() == 1) {
                    lock.unlock();
                    // if the queue was empty, we send the task straight
                    final String head = queue.peek();
                    timerTaskRun = new TimerTaskRun(server, head, recvPacket);
                    Thread thread = new Thread(timerTaskRun);
                    threadList.add(thread);
                    thread.start();
                } else {
                    lock.unlock();
                }
            }

        }
    }

}

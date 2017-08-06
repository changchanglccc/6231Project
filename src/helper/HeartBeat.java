package helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.Timer;


public class HeartBeat extends Thread implements ActionListener{

    private static int failureDetectorPort=5000;
    private Timer timer;
    private int replicaNo;


    public HeartBeat(int portNo){
        this.timer= new Timer(2000,this);
        this.replicaNo=portNo;
    }

    public void startUp(){
        this.timer.start();
    }

    public void showDown(){
        this.timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.start();
    }

    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {

            datagramSocket = new DatagramSocket();
            byte[] message = String.valueOf(replicaNo).getBytes();
            InetAddress host = InetAddress.getByName("localhost");

            DatagramPacket heartBeatPacket = new DatagramPacket(message, message.length,host,failureDetectorPort);
            datagramSocket.send(heartBeatPacket);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
    }
}

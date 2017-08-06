package Helper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class LeaderElection {

    private boolean isLeaderFlag = false;
    private boolean isElectingFlag = false;
    public String serverName ;
    public String serverIP;
    public int serverID;
    public int serverPort;
    public int leaderElectionPort ;

    public ServerListener rl = new ServerListener(serverName,leaderElectionPort);

    int FE_port = 4000;
    String FE_IP = "127.0.0.1";
    ServerInfo[] gm = new ServerInfo[3];

    public LeaderElection(String serverName,String serverIP,
                          int serverID,int serverPort,int leaderElectionPort){
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.serverID = serverID;
        this.serverPort = serverPort;
        this.leaderElectionPort = leaderElectionPort;

        gm[0] = new ServerInfo("rep1", "127.0.0.1", 5000, 3, 7001);
        gm[1] = new ServerInfo("rep2", "127.0.0.1", 5001, 2, 7002);
        gm[2] = new ServerInfo("rep3", "127.0.0.1", 5002, 1, 7003);
        new Thread(rl).start();
    }

    public int initialElection() {
        DatagramSocket socket = null;
        try {
            String msg = "Is there a leader?";
            socket = new DatagramSocket();
            InetAddress FE_addr = InetAddress.getByName(FE_IP);
            byte[] sendData = msg.getBytes();
            byte[] receiveData = new byte[4096];
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, FE_addr, FE_port);
            socket.send(sendPacket);
            System.out.println(serverName + " is Sending message to FE: " + msg);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String out = new String(receivePacket.getData()).trim();
            System.out.println(serverName + " received message from FE: " + out);
            if (!Boolean.parseBoolean(out)){
                sendData = (Integer.toString(serverPort).getBytes());
                sendPacket = new DatagramPacket(sendData, sendData.length, FE_addr, FE_port);
                socket.send(sendPacket);
                System.out.println(serverName + " is sending local port to FE: " + Integer.toString(serverPort));
                socket.receive(receivePacket);
                out = new String(receivePacket.getData()).trim();
                System.out.println(out);
                if (out.equalsIgnoreCase("succeed"))
                    this.isLeaderFlag = true;
                else
                    System.out.println("Error in leader election!");
                return serverPort;
            }
            else {
                sendData = "leader?".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, FE_addr, FE_port);
                socket.send(sendPacket);
                System.out.println(serverName + " is questing leader port from FE.");
                socket.receive(receivePacket);
                out = new String(receivePacket.getData()).trim();
                System.out.println(serverName + " received leader port: " + out);
                return Integer.parseInt(out);
            }

        }catch (Exception e) {
            return -1;
        }finally{
            if(socket != null) socket.close();
        }
    }

    public int ElectLeader(){
        int newLeaderPort = -1;
        try {
            this.setElectingFlag(true);
            System.out.println(serverName + " is holding an election.");
            if (this.isLeaderFlag){
                System.out.println(serverName + "is still leader.");
                newLeaderPort = serverPort;
                notifyNewLeader(FE_IP ,FE_port);
                for (int i = 0; i < gm.length; i++){
                    if (gm[i].getElection_port() > leaderElectionPort){
                        notifyNewLeader(gm[i].getIP(), gm[i].getElection_port());
                    }
                }
            }
            else{
                if(!sendMessage())
                {//elect self as co-ordinator
                    newLeaderPort = serverPort;
                    notifyNewLeader(FE_IP ,FE_port);
                    for (int i = 0; i < gm.length; i++){
                        if (gm[i].getElection_port() > leaderElectionPort){
                            notifyNewLeader(gm[i].getIP(), gm[i].getElection_port());
                        }
                    }
                }
                else
                    newLeaderPort = receiveLeader(serverIP, leaderElectionPort);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return newLeaderPort;

    }

    private void notifyNewLeader(String addr, int port) {
        String msg = Integer.toString(serverPort);
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket();
            InetAddress IP_addr = InetAddress.getByName(addr);
            byte[] sendData = msg.getBytes();
            byte[] receiveData = new byte[4096];
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP_addr, port);
            socket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String out = new String(receivePacket.getData()).trim();
            if (out.equalsIgnoreCase("succeed")){
                System.out.println("Leader port is updated in " + addr + ": " + port);
                this.isLeaderFlag = true;
            }
            else
                System.out.println("Failed to notify leader port to: " + addr + ": " + port);
        }
        catch(Exception e){
            System.out.println(e);
        }finally{
            if(socket != null) socket.close();
        }

    }

    private int receiveLeader(String addr, int port) {
        int out = -1;
        DatagramSocket socket = null;
        String msg = "success";
        try{
            socket = new DatagramSocket(port, InetAddress.getByName(addr));
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            out = Integer.parseInt(new String(receivePacket.getData()).trim());
            byte[] sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(sendPacket);
        }
        catch(Exception e){
            System.out.println("Fail to get the leader port.");
        }finally{
            if(socket != null) socket.close();
        }
        return out;
    }

    private boolean sendMessage() {
        boolean result = false;
        for(int i = serverID; i < gm.length; i++){
            try {
                Socket electionMessage = new Socket(InetAddress.getByName(gm[2-i].getIP()), gm[2-i].getElection_port());
                System.out.println( serverName+ " -> " + gm[2-i].getName() + ": respond successfully!");
                electionMessage.close();
                result = true;
            } catch (Exception e) {
                System.out.println(serverName + " -> " + gm[2-i].getName() + ": no respond!");
            }
        }
        return result;
    }

    public boolean isLeaderFlag() {
        return isLeaderFlag;
    }

    public void setLeaderFlag(boolean isLeaderFlag) {
        this.isLeaderFlag = isLeaderFlag;
    }

    public boolean isElectingFlag() {
        return isElectingFlag;
    }

    public void setElectingFlag(boolean isElectingFlag) {
        this.isElectingFlag = isElectingFlag;
    }


}

package frontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;


public class FailureDetector extends Thread {
    private ArrayList<Integer> replicasList;
    private ArrayList<Integer> heartBeatRecords;
    private int port;

    public FailureDetector(int portNo){
        this.port=portNo;
        this.replicasList=new ArrayList<Integer>();
        this.heartBeatRecords=new ArrayList<Integer>();
    }

    public void addServer(int portNo){
        replicasList.add(portNo);
        heartBeatRecords.add(2); //if continuous 2 times fail to receive.
    }

    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {
            //create belonging socket
            datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[200];

            //listening heatBeat
            while(true){
                DatagramPacket heartBeat = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(heartBeat);
                if(heartBeat.getLength()!=0){
                    String source=new String(heartBeat.getData());
                    recording(source);
                }
                if(heartBeatRecords.contains(0)){  //means 2 runs heartbeats
                    if(!heartBeatRecords.contains(2)){
                        for(int i=0;i<heartBeatRecords.size();i++){    //restore
                            heartBeatRecords.set(i,2);
                        }
                    }else{
                        int failReplica=heartBeatRecords.indexOf(2);   //someone fail
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


    public void recording(String source)throws Exception{
        int sourceReplicaNo=Integer.parseInt(source.trim());
        int index=-1;
        for(int i=0;i<replicasList.size();i++){
            if(replicasList.get(i)==sourceReplicaNo){
                index=i;
                break;
            }
        }
        if(index!=-1)
            heartBeatRecords.set(index,heartBeatRecords.get(index)-1);
        else
            System.out.println("FailureDetector: receive invalid heartBeat package");
    }
    
}

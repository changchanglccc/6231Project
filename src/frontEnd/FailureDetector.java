package frontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;


public class FailureDetector extends Thread {
    private ArrayList<Integer> replicasList;
    private ArrayList<Integer> heartBeatRecords;
    private int port=4999;      //failureDetector special port
    private int runsTolerant=2;

    public FailureDetector(){
        this.replicasList=new ArrayList<Integer>();
        this.heartBeatRecords=new ArrayList<Integer>();
    }

    public void addServer(int portNo){
        replicasList.add(portNo);
        heartBeatRecords.add(runsTolerant+1);
    }

    private void removeServer(int index){
        replicasList.remove(index);
        heartBeatRecords.remove(index);
    }

    @Override
    public void run() {
        DatagramSocket datagramSocket = null;

        try {
            //create belonging socket
            datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[500];

            //listening heatBeat
            while(true){
                DatagramPacket heartBeat = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(heartBeat);
                String source=new String(heartBeat.getData());
                System.out.println("FailureDetector: [ "+source.trim()+" ] is alive");

                recording(source);

                if(heartBeatRecords.contains(0)){
                    if(!heartBeatRecords.contains(runsTolerant+1)){  //everyone is ok
                        for(int i=0;i<heartBeatRecords.size();i++){
                            heartBeatRecords.set(i,runsTolerant+1);
                        }
                    }else{
                        int failReplicaIndex=heartBeatRecords.indexOf(runsTolerant+1);   //someone fail
                        System.out.println("FailureDetector: [ "+replicasList.get(failReplicaIndex)+" ] is crashed !!!");
                        removeServer(failReplicaIndex);




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


    public void sentMessageForElection(){

    }

}

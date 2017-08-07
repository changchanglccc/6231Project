package fifo;



import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;


public class MessageQueue extends Thread{

   private DatagramSocket datagramSocket;

   private Queue<String> queue;

   public MessageQueue(DatagramSocket datagramSocket){
      queue= new LinkedList<String>();
      this.datagramSocket=datagramSocket;
   }

   public void enque(String message){
      queue.offer(message);
   }

   public void deque(){
      queue.poll();
   }


   @Override
   public void run() {
      while (true){

      }
   }
}

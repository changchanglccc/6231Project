package frontEnd;


import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FailureDetector extends Thread implements ActionListener{
    private ArrayList<Integer> replicasList;
    private Timer timer;

    public FailureDetector(){
        replicasList=new ArrayList<Integer>();
        timer=new Timer(2000,this);
    }

    public void addServer(int portNo){
        replicasList.add(portNo);
    }

    public void startUp(){
        this.timer.start();
    }

    public void showDown(){
        this.timer.stop();
    }

    @Override
    public void run() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.startUp();
    }


}

package thread;

import servers.Server1;

public class UdpListener extends Thread{

    private String message;
    private int port;
    private Server1 server1;
    private boolean flag;
	private String replyMessage;

    public UdpListener(int portNumber,Server1 server1){
        this.port=portNumber;
        this.server1 = server1;
        this.message = "";
        this.flag = false;
        this.replyMessage = "";
    }


    public String getMessage() {
		return message;
	}


	@Override
    public void run() {
       
    }
}

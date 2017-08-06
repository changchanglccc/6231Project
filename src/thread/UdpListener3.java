package thread;

import servers.Server3;

public class UdpListener3 extends Thread{

    private String message;
    private int port;
    private Server3 server3;
    private boolean flag;
	private String replyMessage;

    public UdpListener3(int portNumber,Server3 server3){
        this.port=portNumber;
        this.server3 = server3;
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

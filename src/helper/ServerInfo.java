package helper;

/**
 * Created by chongli on 2017-08-05.
 */

public class ServerInfo {
    private String name;
    private String IP;
    private int local_port;
    private int ID;
    private int election_port;

    public ServerInfo(String name, String iP, int localPort, int iD, int electionPort) {
        super();
        this.name = name;
        IP = iP;
        this.local_port = localPort;
        ID = iD;
        this.election_port = electionPort;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public int getElection_port() {
        return election_port;
    }
}

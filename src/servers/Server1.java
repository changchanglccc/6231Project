package servers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import records.Record;
import sun.misc.Queue;

public class Server1 {
	private HashMap<Character,ArrayList<Record>> DDOServer1;
	private HashMap<Character,ArrayList<Record>> MTLServer1;
	private HashMap<Character,ArrayList<Record>> LVLServer1;
	private Queue<String> queue;
    private File loggingFile;

}

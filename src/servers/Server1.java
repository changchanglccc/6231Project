package servers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import records.Record;
import records.StudentRecord;
import records.TeacherRecord;
import thread.UdpListener;

public class Server1 implements CenterServer  {
	private HashMap<Character,ArrayList<Record>> DDOServer1;
	private HashMap<Character,ArrayList<Record>> MTLServer1;
	private HashMap<Character,ArrayList<Record>> LVLServer1;
    private File loggingFileDDO = new File("DDOServer1.txt");
    private File loggingFileMTL = new File("MTLServer1.txt");
    private File loggingFileLVL = new File("LVLServer1.txt");
    private String message;
    
    public Server1() {
		DDOServer1 = new HashMap<>();
		MTLServer1 = new HashMap<>();
		LVLServer1 = new HashMap<>();
		this.message = "";

	}
    
    public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
    
	public static void main(String[] args) {
    	int port=5001;
//    	byte[] reply = new byte[1000];
//    	boolean flag;
//    	String replyMessage = null;
    	
    	Server1 server1 = new Server1();
    	new UdpListener(port,server1).start();
    	
		while(true){
			// get message from the UdpListener
			if(server1.getMessage().equals("")){// it is a backup 
				multicast(server1.getMessage(),server1);
			}
    	
		}
		
	}
    
    
    public static void operation(String message,Server1 server1){
		String[] strings = message.split(",");
    	switch(strings[0]){
    		case "1":
    			server1.createTRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
    			break;
    		case "2":
    			server1.createSRecord(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]);
    			break;
    		case "3":
    			server1.getRecordCounts(strings[1]);
    			break;
    		case "4":
    			server1.editRecord(strings[1], strings[2], strings[3], strings[4]);
    			break;
    		case "5":
    			server1.transferRecord(strings[1], strings[2], strings[3]);
    			break;
    		case "7":
    			server1.getRecordInfo(strings[1],strings[2]);
    			break;
    		default:
    			System.out.println("error!");
    	}
	}

	public static void multicast(String message,Server1 server1){//as a backup
    	
    	//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
//        	System.setProperty("java.net.preferIPv4Stack", "true");
        	InetAddress group = InetAddress.getByName("228.5.6.7");
        	socket = new MulticastSocket(6789);
        	socket.joinGroup(group);
        	byte[] m = "Server1 finish".getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	
        	byte[] buffer = new byte[1000];
//        	for(int i=0;i<=2;i++){ // get messages from others in group
        		System.out.println("receiving...");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve: "+ new String(messageIn.getData()));
        		
        		operation(new String(messageIn.getData()), server1);
        		socket.send(messageOut);
//        	}
        	System.out.println("Server1");
        	socket.leaveGroup(group);
        }catch(SocketException e){
        	System.out.println("Socket: " + e.getMessage());
        	e.printStackTrace();
        }catch (IOException e) {
        	System.out.println("IO: " + e.getMessage());
		}
        finally {
        	if(socket != null) 
        		socket.close();
        }
	}

	public void multicast2(String message){// as a primary replica
		//Multicast
    	// args give message contents & destination multicast group (e.g. "228.5.6.7")
    	MulticastSocket socket = null;
        try{
        	System.setProperty("java.net.preferIPv4Stack", "true");
        	InetAddress group = InetAddress.getByName("228.5.6.7");
        	socket = new MulticastSocket(6789);
        	socket.joinGroup(group);
        	byte[] m = "Server1 nihao".getBytes();
        	DatagramPacket messageOut = new DatagramPacket(m, m.length,group,6789);
        	socket.send(messageOut);
        	byte[] buffer = new byte[1000];
        	for(int i=0;i<2;i++){  // get messages from others in group
        		System.out.println("receiving");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve:"+ new String(messageIn.getData()));
        	}
        	System.out.println("Server1");
        	socket.leaveGroup(group);
        }catch(SocketException e){
        	System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
        	System.out.println("IO: " + e.getMessage());
		}
        finally {
        	if(socket != null) 
        		socket.close();
		}
	}


	@Override
	public boolean createTRecord(String managerId, String firstName, String lastName, String address, String phone,
			String specialization, String location) {
		
		TeacherRecord teacherRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);
        int beforeNum=getLocalRecordsCount(managerId);
        
        //log
        String log=(new Date().toString()+" - "+managerId+" - creating a teacher record - "+teacherRecord.recordID);
        System.out.println(teacherRecord);

        if(managerId.startsWith("MTL")){
        	storingRecord(teacherRecord,MTLServer1);
            writeLog(log,loggingFileMTL);
		}
		else if(managerId.startsWith("DDO")){
			storingRecord(teacherRecord,DDOServer1);
	        writeLog(log,loggingFileDDO);
		}
		else{
			storingRecord(teacherRecord,LVLServer1);
	        writeLog(log,loggingFileLVL);
		}
        
        //
        int afterNum=getLocalRecordsCount(managerId);
       
        return beforeNum+1<=afterNum;
	}


	@Override
	public boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered,
			String status, String date) {
		StudentRecord studentRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, date);
        int beforeNum=getLocalRecordsCount(managerId);
        
        String log=(new Date().toString()+" - "+managerId+" - creating a student record - "+studentRecord.recordID);
        System.out.println(studentRecord);
        
        if(managerId.startsWith("MTL")){
        	storingRecord(studentRecord,MTLServer1);
            writeLog(log,loggingFileMTL);
		}
		else if(managerId.startsWith("DDO")){
			storingRecord(studentRecord,DDOServer1);
	        writeLog(log,loggingFileDDO);
		}
		else{
			storingRecord(studentRecord,LVLServer1);
	        writeLog(log,loggingFileLVL);
		}
        int afterNum=getLocalRecordsCount(managerId);
        
        return beforeNum+1<=afterNum;
	}


	@Override
	public String getRecordCounts(String managerId) {
		 String DDONum = String.valueOf(getLocalRecordsCount("DDO1111"));
		 String LVLNum = String.valueOf(getLocalRecordsCount("LVL1111"));
		 String MTLNum = String.valueOf(getLocalRecordsCount("MTL1111"));
		 
		//log
        String log=(new Date().toString()+" - "+managerId+" - get records number ");
        if(managerId.startsWith("MTL")){
            writeLog(log,loggingFileMTL);
		}
		else if(managerId.startsWith("DDO")){
	        writeLog(log,loggingFileDDO);
		}
		else{
	        writeLog(log,loggingFileLVL);
		}

        return "Records Count: DDO:"+DDONum+" | LVL:"+LVLNum+" | MTL:"+MTLNum;
	}


	@Override
	public boolean editRecord(String managerId, String recordID, String fieldName, String newValue) {
			Record targetRecord=null;
			Collection<ArrayList<Record>> arrayListsSet = null;
			if(managerId.startsWith("MTL")){
				arrayListsSet=MTLServer1.values();
			}
			else if(managerId.startsWith("DDO")){
				 arrayListsSet=DDOServer1.values();
			}
			else{
				 arrayListsSet=LVLServer1.values();
			}
	      
	        for(ArrayList<Record> recordArrayListSet:arrayListsSet){
	            for(Record record:recordArrayListSet){
	                if(record.recordID.equalsIgnoreCase(recordID))
	                    targetRecord=record;
	                    break;
	            }
	        }
	        if(targetRecord!=null){
	            if(targetRecord instanceof TeacherRecord){
	                synchronized (targetRecord) {
	                    ((TeacherRecord) targetRecord).setValue(fieldName, newValue);  //shared resource - synchronized
	                    System.out.println(targetRecord);
	                }
	            }
	            else {
	                synchronized (targetRecord) {
	                    ((StudentRecord) targetRecord).setValue(fieldName, newValue);   //shared resource - synchronized
	                    System.out.println(targetRecord);
	                }
	            }
	            //log
	            String log=(new Date().toString()+" - "+managerId+" - editing the record - "+recordID+" - Success");
	            
	            if(managerId.startsWith("MTL")){
	                writeLog(log,loggingFileMTL);
	    		}
	    		else if(managerId.startsWith("DDO")){
	    	        writeLog(log,loggingFileDDO);
	    		}
	    		else{
	    	        writeLog(log,loggingFileLVL);
	    		}
	            return true;
	        }
	        else{
	            //log
	            String log=(new Date().toString()+" - "+managerId+" - editing the record - "+recordID+"- ERROR:Record not exist");
	            
	            if(managerId.startsWith("MTL")){
	                writeLog(log,loggingFileMTL);
	    		}
	    		else if(managerId.startsWith("DDO")){
	    	        writeLog(log,loggingFileDDO);
	    		}
	    		else{
	    	        writeLog(log,loggingFileLVL);
	    		}
	            return false;
	        }
	}


	@Override
	public boolean transferRecord(String managerId, String recordID, String remoteCenterServerName) {
		Record targetRecord=null;
		Collection<ArrayList<Record>> arrayListsSet = null;
		if(managerId.startsWith("MTL")){
			arrayListsSet=MTLServer1.values();
		}
		else if(managerId.startsWith("DDO")){
			 arrayListsSet=DDOServer1.values();
		}
		else{
			 arrayListsSet=LVLServer1.values();
		}
		
        for(ArrayList<Record> recordArrayListSet : arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
                break;
            }
        }
        if(targetRecord==null){
            //log
            String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
            "Error:record not exist");
            
            if(managerId.startsWith("MTL")){
                writeLog(log,loggingFileMTL);
    		}
    		else if(managerId.startsWith("DDO")){
    	        writeLog(log,loggingFileDDO);
    		}
    		else{
    	        writeLog(log,loggingFileLVL);
    		}
            return false;
        }
        else{
            //remove
        	ArrayList<Record> theArrayList = null; 
        	if(managerId.startsWith("MTL")){
        		theArrayList=MTLServer1.get(targetRecord.lastName.charAt(0));
    		}
    		else if(managerId.startsWith("DDO")){
    			theArrayList=DDOServer1.get(targetRecord.lastName.charAt(0));
    		}
    		else{
    			theArrayList=LVLServer1.get(targetRecord.lastName.charAt(0));
    		}
        	
        	theArrayList.remove(targetRecord);
            
            //add
            boolean flag = true;
            if(remoteCenterServerName.startsWith("DDO"))
            	storingRecord(targetRecord,DDOServer1);
            else if(remoteCenterServerName.startsWith("LVL"))
            	storingRecord(targetRecord,LVLServer1);
            else if(remoteCenterServerName.startsWith("MTL"))
            	storingRecord(targetRecord,MTLServer1);
            else
                flag=false;
            //log
            if(flag){
                String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
                        "Success");
                if(managerId.startsWith("MTL")){
                    writeLog(log,loggingFileMTL);
        		}
        		else if(managerId.startsWith("DDO")){
        	        writeLog(log,loggingFileDDO);
        		}
        		else{
        	        writeLog(log,loggingFileLVL);
        		}
            }
            else{
                String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
                        "Fail");
                if(managerId.startsWith("MTL")){
                    writeLog(log,loggingFileMTL);
        		}
        		else if(managerId.startsWith("DDO")){
        	        writeLog(log,loggingFileDDO);
        		}
        		else{
        	        writeLog(log,loggingFileLVL);
        		}
            }
            return flag;
        }
	}


	@Override
	public String getRecordInfo(String managerId, String recordID) {
		Record targetRecord=null;
		Collection<ArrayList<Record>> arrayListsSet = null;
		if(managerId.startsWith("MTL")){
			arrayListsSet=MTLServer1.values();
		}
		else if(managerId.startsWith("DDO")){
			 arrayListsSet=DDOServer1.values();
		}
		else{
			 arrayListsSet=LVLServer1.values();
		}
		System.out.println("recordID: "+recordID);
        for(ArrayList<Record> recordArrayListSet:arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
                break;
            }
        }
        if(targetRecord!=null)
            return targetRecord.toString();
        else
            return "the record is not exist";
	}
	
	private void storingRecord(Record record,HashMap<Character,ArrayList<Record>> hashMap){
        char cap=record.lastName.charAt(0);
        if(!hashMap.containsKey(cap)){
            ArrayList<Record> newArray=new ArrayList<Record>();
            newArray.add(record);
            hashMap.put(cap,newArray);
        }
        else{
            ArrayList<Record> theArray= hashMap.get(cap);
            theArray.add(record);
        }
    }
	
	 public int getLocalRecordsCount(String managerId){
	        int count=0;
	        
	        Collection<ArrayList<Record>> arrayListsSet = null;
			if(managerId.startsWith("MTL")){
				arrayListsSet=MTLServer1.values();
			}
			else if(managerId.startsWith("DDO")){
				 arrayListsSet=DDOServer1.values();
			}
			else{
				 arrayListsSet=LVLServer1.values();
			}
			
	        for(ArrayList<Record> recordArrayListSet :arrayListsSet){
	            for(Record record:recordArrayListSet){
	                count++;
	            }
	        }
	        return count;
	    }
	
	public void writeLog(String log,File file){
        if(!file.exists())
            return;
        try {
            synchronized (file) {
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(log);
                bufferedWriter.newLine();
                bufferedWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

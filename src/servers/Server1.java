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
import java.util.LinkedList;
import records.Record;
import records.StudentRecord;
import records.TeacherRecord;
import sun.misc.Queue;

public class Server1 implements CenterServer{
	private HashMap<Character,ArrayList<Record>> DDOServer1;
	private HashMap<Character,ArrayList<Record>> MTLServer1;
	private HashMap<Character,ArrayList<Record>> LVLServer1;
	private LinkedList<String> queue;
    private File loggingFileDDO;
    private File loggingFileMTL;
    private File loggingFileLVL;
    
    public static void main(String[] args) {
    	
    	String message = null;
    	multicast(message);
	}
    
    
    public static void multicast(String message){
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
        	for(int i=0;i<3;i++){  // get messages from others in group
        		System.out.println("receiving");
        		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        		socket.receive(messageIn);
        		System.out.println("Recieve:"+ new String(messageIn.getData()));
        	}
        	System.out.println("Server1");
//        	socket.leaveGroup(group);
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
        int beforeNum=getLocalRecordsCount();

        storingRecord(teacherRecord);
        //
        int afterNum=getLocalRecordsCount();
        //log
        String log=(new Date().toString()+" - "+managerId+" - creating a teacher record - "+teacherRecord.recordID);
        System.out.println(teacherRecord);
        writeLog(log);
        return beforeNum+1<=afterNum;
	}


	@Override
	public boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered,
			String status, String date) {
		StudentRecord studentRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, date);
        int beforeNum=getLocalRecordsCount();
        storingRecord(studentRecord);
        int afterNum=getLocalRecordsCount();
        String log=(new Date().toString()+" - "+managerId+" - creating a student record - "+studentRecord.recordID);
        System.out.println(studentRecord);
        writeLog(log);
        return beforeNum+1<=afterNum;
	}


	@Override
	public String getRecordCounts(String managerId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean editRecord(String managerId, String recordID, String fieldName, String newValue) {
		 Record targetRecord=null;

	        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
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
	            writeLog(log);
	            return true;
	        }
	        else{
	            //log
	            String log=(new Date().toString()+" - "+managerId+" - editing the record - "+recordID+"- ERROR:Record not exist");
	            writeLog(log);
	            return false;
	        }
		return false;
	}


	@Override
	public boolean transferRecord(String managerId, String recordID, String remoteCenterServerName) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getRecordInfo(String recordID) {
		Record targetRecord=null;

        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
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
		return null;
	}
	
	private synchronized void storingRecord(Record record){
        char cap=record.lastName.charAt(0);
        if(!storedRecords.containsKey(cap)){
            ArrayList<Record> newArray=new ArrayList<Record>();
            newArray.add(record);
            storedRecords.put(cap,newArray);
        }
        else{
            ArrayList<Record> theArray= storedRecords.get(cap);
            theArray.add(record);
        }
    }
	
	 public int getLocalRecordsCount(){
	        int count=0;
	        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
	        for(ArrayList<Record> recordArrayListSet :arrayListsSet){
	            for(Record record:recordArrayListSet){
	                count++;
	            }
	        }
	        return count;
	    }
	
	public void writeLog(String log){
        if(!loggingFile.exists())
            return;
        try {
            synchronized (loggingFile) {
                FileWriter fileWriter = new FileWriter(loggingFile, true);
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

package client;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Manager{

	private String managerID;
	private static File loggingFile=new File("manager.txt");


	public Manager(String managerID){
		this.managerID = managerID;

	}


	public boolean createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location){

		return false;
	}

	public boolean createSRecord(String firstName, String lastName, String coursesRegistered, String status, String date){

		return false;
	}

	public String getRecordCounts(){

		return "";
	}

	public boolean editRecord(String recordID, String fieldName, String newValue){
		return false;
	}

	public boolean transferRecord(String recordID, String remoteCenterServerName){
		return false;
	}

	public String getRecordInfo(String recordID){
		return "";
	}


	private static void writelog(String log){
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

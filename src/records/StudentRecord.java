package records;

import java.io.Serializable;

public class StudentRecord extends Record implements Serializable{

	private static int idCounter=10000;

	public String coursesRegistered;
	public String status;
	public String date;
	
	public StudentRecord(String firstName, String lastName, String coursesRegistered, String status, String date){
		super(firstName,lastName);
		this.recordID="SR"+String.valueOf(++idCounter);
		this.coursesRegistered = coursesRegistered;
		this.status = status;
		this.date = date;
	}

	public boolean setValue(String fieldName, String value){
		if(fieldName.equalsIgnoreCase("status")){
			if(value.equals("active")||value.equals("inactive")){
				this.status=value;
				return true;
			}
			else
				return false;
		}
		else if(fieldName.equalsIgnoreCase("coursesregistered")){
			this.coursesRegistered=value;
			return true;
		}
		 else if(fieldName.equalsIgnoreCase("date")){
			 this.date=value;
			 return true;
		 }
		 else return false;
	}

	@Override
	public String toString() {
		return "StudentRecord [coursesRegistered=" + coursesRegistered + ", status=" + status + ", date=" + date
				+ ", recordID=" + recordID + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}

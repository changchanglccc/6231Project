package records;

import java.io.Serializable;

public class TeacherRecord extends Record implements Serializable{
	private static int idCounter=10000;
	public String address;
	public String phone;
	public String specialization;
	public String location;

	public TeacherRecord(String firstName, String lastName, String address, String phone, String specialization,
			String location) {
		super(firstName,lastName);
		this.recordID="TR"+String.valueOf(++idCounter);
		this.address = address;
		this.phone = phone;
		this.specialization = specialization;
		this.location = location;
	}
	
	public boolean setValue(String fieldName, String value){
		if(fieldName.equalsIgnoreCase("location")){
			if(value.equals("mtl")||value.equals("lvl")||value.equals("ddo")){
				this.location=value;
				return true;
			}
			else
				return false;
		}
		else if(fieldName.equalsIgnoreCase("address")){
			this.address=value;
			return true;
		}
		else if(fieldName.equalsIgnoreCase("phone")){
			this.phone=value;
			return true;
		}
		else if(fieldName.equalsIgnoreCase("specialization")){
			this.specialization=value;
			return true;
		}
		else return false;
	}

	@Override
	public String toString() {
		return "TeacherRecord [address=" + address + ", phone=" + phone + ", specialization=" + specialization
				+ ", location=" + location + ", recordID=" + recordID + ", firstName=" + firstName + ", lastName="
				+ lastName + "]";
	}
}

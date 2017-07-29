package ClassManageSystem;


/**
* ClassManageSystem/CenterServerOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ClassManageSystem.idl
* Saturday, July 29, 2017 10:07:17 AM EDT
*/

public interface CenterServerOperations 
{
  boolean createTRecord (String managerId, String firstName, String lastName, String address, String phone, String specialization, String location);
  boolean createSRecord (String managerId, String firstName, String lastName, String coursesRegistered, String status, String date);
  String getRecordCounts (String managerId);
  boolean editRecord (String managerId, String recordID, String fieldName, String newValue);
  boolean transferRecord (String managerId, String recordID, String remoteCenterServerName);
  void shutdown ();
  String getRecordInfo (String recordID);
} // interface CenterServerOperations
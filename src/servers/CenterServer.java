package servers;

public interface CenterServer {

    boolean createTRecord(String managerId, String firstName,String lastName,String address, String phone, String specialization, String location);

    boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered, String status, String date);

    String  getRecordCounts(String managerId);

    boolean editRecord(String managerId, String recordID, String fieldName, String newValue);

    boolean transferRecord(String managerId, String recordID, String remoteCenterServerName);

    String getRecordInfo(String managerId,String recordID);
}

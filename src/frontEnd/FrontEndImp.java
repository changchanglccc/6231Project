package frontEnd;


import DCMS.FrontEnd;
import DCMS.FrontEndHelper;
import DCMS.FrontEndPOA;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import servers.CenterServer;

public class FrontEndImp extends FrontEndPOA{

    private static FrontEndImp frontEnd;
    private CenterServer primaryServer;
    private ORB orb;
    private int portNum;

    private FrontEndImp(){}
    //singleton
    public static FrontEndImp getFrontEnd(){
        if(frontEnd==null)
            frontEnd=new FrontEndImp();

        return frontEnd;
    }

    public static void main(String[] args){
        //start up the periodical detecting
        FailureDetector failureDetector=new FailureDetector();
        failureDetector.run();

        //run CORBA and listen requests from clients
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", "-ORBInitialPort", "1050"}, null);
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            FrontEndImp frontEndSurvant=FrontEndImp.getFrontEnd();
            frontEndSurvant.setORB(orb);
            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontEndSurvant);
            FrontEnd href = FrontEndHelper.narrow(ref);
            // NameService invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            String name = "frontEnd";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);
            orb.run();
            System.out.println("------");
        }
        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
    
    public void setORB(ORB orb_val){
        orb = orb_val;
    }

    public void setPrimaryServer(CenterServer primary){
        this.primaryServer=primary;
    }

    @Override
    public boolean createTRecord(String managerId, String firstName, String lastName, String address, String phone, String specialization, String location) {
        return primaryServer.createTRecord(managerId, firstName, lastName, address, phone, specialization, location);
    }

    @Override
    public boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered, String status, String date) {
       return primaryServer.createSRecord(managerId, firstName, lastName, coursesRegistered, status, date);
    }

    @Override
    public String getRecordCounts(String managerId) {
       return primaryServer.getRecordCounts(managerId);
    }

    @Override
    public boolean editRecord(String managerId, String recordID, String fieldName, String newValue) {
       return primaryServer.editRecord(managerId, recordID, fieldName, newValue);
    }

    @Override
    public boolean transferRecord(String managerId, String recordID, String remoteCenterServerName) {
       return primaryServer.transferRecord(managerId, recordID, remoteCenterServerName);
    }

    @Override
    public void shutdown() {
    	this.orb.shutdown(false);
    }

    @Override
    public String getRecordInfo(String recordID) {
      return primaryServer.getRecordInfo(recordID);
    }

}

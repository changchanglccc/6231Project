package frontEnd;


import java.io.File;

import org.omg.CORBA.ORB;

import servers.CenterServer;

public class FrontEnd {

    private static FrontEnd frontEnd;
    private CenterServer primaryServer;
    private int portNum;
    public String centerName;
    private File loggingFile;
    private ORB orb;

    private FrontEnd(){}

    public static FrontEnd getFrontEnd(){
        if(frontEnd==null)
            return new FrontEnd();
        else
            return frontEnd;
    }


    public void setPrimaryServer(CenterServer primary,File loggingFile,String centerName){
        this.primaryServer=primary;
        this.loggingFile=loggingFile;
        this.centerName=centerName;
    }

    //front end 跟 client 用corba交流，so,FE得有CORBA的idl,这个类去实现
    public static void main(String[] args){

    }

}

package frontEnd;


import servers.CenterServer;

public class FrontEnd {

    private static FrontEnd frontEnd;
    private CenterServer primaryServer;
    private int portnum;

    private FrontEnd(){}

    public static FrontEnd getFrontEnd(){
        if(frontEnd==null)
            return new FrontEnd();
        else
            return frontEnd;
    }


    public void setPrimaryServer(CenterServer primary){
        this.primaryServer=primary;
    }

    //front end 跟 client 用corba交流，so,FE得有CORBA的idl,这个类去实现
    public static void main(String[] args){

    }

}

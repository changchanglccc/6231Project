package frontEnd;

import servers.CenterServer;
import java.util.ArrayList;


public class BullySelector {

    private static BullySelector bullySelector;
    private ArrayList<CenterServer> centerServerList;


    private BullySelector(){
        centerServerList=new ArrayList<CenterServer>();
    }

    public static BullySelector getBullySelector(){
        if(bullySelector==null)
            bullySelector=new BullySelector();
        return bullySelector;
    }

    private CenterServer electing(){
        return null;
    }

    public void startUp(){
        CenterServer primary = electing();
        FrontEndImp.getFrontEnd().setPrimaryServer(primary);
    }

}

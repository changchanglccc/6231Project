package frontEnd;

import java.util.ArrayList;


public class BullySelector {

    private static BullySelector bullySelector;
    private ArrayList<Integer> centerServerList;


    private BullySelector(){
        centerServerList=new ArrayList<Integer>();
    }

    public static BullySelector getBullySelector(){
        if(bullySelector==null)
            bullySelector=new BullySelector();
        return bullySelector;
    }

    private int electing(){
        return 0;
    }

    public void startUp(){
        int primaryPortNo = electing();
        FrontEndImp.getFrontEnd().setPrimaryServer(primaryPortNo);
    }

    public void addServer(int newServer){
        centerServerList.add(newServer);
    }

}

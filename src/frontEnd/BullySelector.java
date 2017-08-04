package frontEnd;

import java.util.ArrayList;


public class BullySelector {

    private static BullySelector bullySelector;
    private ArrayList<Integer> serverList;


    private BullySelector(){
        serverList =new ArrayList<Integer>();
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
        serverList.add(newServer);
    }

}

package Model;

import java.io.File;

public class MainModel {

    SQL sql = new SQL();

    public MainModel(){

    }

    public void createNewCampaign(File clickLogPath, File impressionLogPath, File serverLogPath){
        try{
            sql.putData(clickLogPath.getPath(),"click");
            sql.putData(impressionLogPath.getPath(),"impressions");
            sql.putData(serverLogPath.getPath(),"server");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

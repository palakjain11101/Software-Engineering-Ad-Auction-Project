package Model;

import java.io.File;
import java.sql.ResultSet;

public class MainModel {

    SQL sql = new SQL();

    public MainModel(){

    }

    public String createNewCampaign(File clickLogPath, File impressionLogPath, File serverLogPath){
        try{
            sql.connection("test");
            sql.createTable();
            sql.putData(clickLogPath.getPath(),"click");
            sql.putData(impressionLogPath.getPath(),"impressions");
            sql.putData(serverLogPath.getPath(),"server");
        }catch(Exception e){
            return e.getMessage();
        }

        return null;
    }

    public ResultSet getData(String query){
        return sql.getData(query);
    }
}

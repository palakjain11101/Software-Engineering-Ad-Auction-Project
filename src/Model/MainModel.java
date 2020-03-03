package Model;

import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public Double getData(String overallMetricQuery){
        try {
            return sql.getData(overallMetricQuery).getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public ArrayList<GraphPoint> getDataOverTimePoints(String metricOverTimeQuery){
        if(metricOverTimeQuery.equals("")) return null;
        ArrayList<GraphPoint> metricOverTime = new ArrayList<>();
        ResultSet metricOverTimeSet = sql.getData(metricOverTimeQuery);
        int i = 0;
        try {
            while (metricOverTimeSet.next()) {
                metricOverTime.add(new GraphPoint(i, metricOverTimeSet.getDouble(2)));
                i++;
            }
        }
        catch (SQLException e){e.printStackTrace();}
        return metricOverTime;
    }
}

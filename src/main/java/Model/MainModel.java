package Model;

import View.CampaignTab;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainModel {

    private SQL sql = new SQL();
    private int bounceTime;
    private boolean bounceConversion;
    private String chartTypeTime = "DATE(";

    public MainModel(){
        // Set default bounce values
        bounceTime = 30;
        bounceConversion = false;

    }

    public void setBounceAttributes(int time, boolean conversion){
        bounceTime = time;
        bounceConversion = conversion;
    }


    public String createNewCampaign(File clickLogPath, File impressionLogPath, File serverLogPath){
        try{
            sql.connection("test");
            sql.createTable();
            sql.putData(impressionLogPath.getPath(),"impressions");
            sql.putData(clickLogPath.getPath(),"click");
            sql.putData(serverLogPath.getPath(),"server");
        }catch(Exception e){
            return e.getMessage();
        }

        return null;
    }

    public Double getData(String overallMetricQuery){
        try {
            ResultSet set = sql.getData(overallMetricQuery);
            Double value = set.getDouble(1);
            return round(value,2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public ArrayList<GraphPoint> getDataOverTimePoints(String metricOverTimeQuery, boolean shouldGraphAvg, boolean shouldXAxisBeIncrement){
        if(metricOverTimeQuery.equals("")) return null;
        ArrayList<GraphPoint> metricOverTime = new ArrayList<>();
        ResultSet metricOverTimeSet = sql.getData(metricOverTimeQuery);
        int i = 0;
        try {
            while (metricOverTimeSet.next()) {
                if(shouldGraphAvg) {
                    metricOverTime.add(new GraphPoint(shouldXAxisBeIncrement ? i : metricOverTimeSet.getInt(1), metricOverTimeSet.getDouble(2), metricOverTimeSet.getDouble(3)));
                }
                else {
                    metricOverTime.add(new GraphPoint(shouldXAxisBeIncrement ? i : metricOverTimeSet.getInt(1), metricOverTimeSet.getDouble(2)));
                }
                i++;
            }
        }
        catch (SQLException e){e.printStackTrace();}
        return metricOverTime;
    }

    public ArrayList<CampaignTab.CampaignDataPackage> queryCampaign(HashMap<String, List<String>> hashFilters){

        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();

        String cases = convertFiltersToCase(hashFilters);
        System.out.println(cases);

        //Set the conversion variable if needed
        String conversionCase = "";
        if(bounceConversion){
            conversionCase = " AND Conversion = \"Yes\"";
        }

        double impressions = getData("SELECT COUNT(case when " + cases + " then 1 else null end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double clicks = getData("SELECT COUNT(case when " + cases + " then 1 else null end) FROM click INNER JOIN person ON click.id = person.id;");
        double uniques = getData("select count(case when " + cases + " then 1 else null end) from (SELECT distinct click.id,gender,ageRange,income,context from click INNER JOIN person ON click.id = person.id);");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND " + cases + " " + conversionCase + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' AND " + cases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double totalCostClick = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM click INNER JOIN person ON click.id = person.id;");
        double totalCostImpressions = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint>[] impressionsOverTime = getAllPointData("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from impressions INNER JOIN person ON impressions.id = person.id) GROUP BY DATE(date);",false);
        ArrayList<GraphPoint>[] clicksOverTime = getAllPointData("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from click INNER JOIN person ON click.id = person.id)  GROUP BY DATE(date);",false);
        ArrayList<GraphPoint>[] uniquesOverTime = getAllPointData("select d1, IFNULL(c1,0) from (select distinct DATE(date) as d1 from click order by d1 asc) left outer join (select DATE(date) as d2, count(case when " + cases + " then 1 end) as c1 from (SELECT * from click INNER JOIN person ON click.id = person.id group by person.id) GROUP BY DATE(date)) on d1=d2;",false);
        ArrayList<GraphPoint>[] bouncesOverTime = getAllPointData("select DATE(date), COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND "+ cases + " " + conversionCase + " then 1 else null end) from (SELECT * from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint>[] conversionsOverTime = getAllPointData("Select DATE(date), COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) from (SELECT *  from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint>[] totalCostOverTime = getAllPointData("select d1, SUM(c) from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date) UNION ALL SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) group by d1;",false);
        ArrayList<GraphPoint>[] CTROverTime = getAllPointData("SELECT d, SUM(c), SUM(i) from (SELECT DATE(date) as d, count(case when " + cases + " then 1 else null end) as c, 0 as i from click INNER JOIN person ON click.id = person.id group by DATE(date) UNION ALL SELECT DATE(date) as d, 0 as c, count(case when " + cases + " then 1 else null end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) group by d;",true);
        ArrayList<GraphPoint>[] CPAOverTime = getAllPointData("SELECT d1, c2, i from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(case when " + cases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by d1) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint>[] CPCOverTime = getAllPointData("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end), count(case when " + cases + " then 1 else null end) from click INNER JOIN person ON click.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint>[] CPMOverTime = getAllPointData("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end)*1000, count(case when " + cases + " then 1 else null end) from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint>[] bounceRateOverTime = getAllPointData("SELECT d1, i, c from (SELECT DATE(date) as d1, count(case when " + cases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND " + cases + " " + conversionCase + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 GROUP BY d1;",true);

        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Impressions", impressions, impressionsOverTime[0],impressionsOverTime[1],impressionsOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Clicks", clicks, clicksOverTime[0],clicksOverTime[1],clicksOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Uniques", uniques, uniquesOverTime[0],uniquesOverTime[1],uniquesOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Bounces", bounces, bouncesOverTime[0],bouncesOverTime[1],bouncesOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Conversions", conversions, clicksOverTime[0],conversionsOverTime[1],clicksOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Total Cost", totalCost, totalCostOverTime[0],totalCostOverTime[1],totalCostOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CTR", round(clicks/impressions,2), CTROverTime[0],CTROverTime[1],CTROverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPA", round(totalCost/conversions,2), CPAOverTime[0],CPAOverTime[1],CPAOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPC",  round(totalCostClick/clicks,2), CPCOverTime[0],CPCOverTime[1],CPCOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPM", (round(totalCostImpressions/impressions,2))*1000, CPMOverTime[0],CPMOverTime[1],CPMOverTime[2]));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Bounce Rate",round(bounces/clicks,2), bounceRateOverTime[0],bounceRateOverTime[1],bounceRateOverTime[2]));

        return basicMetrics;
    }

    private ArrayList<GraphPoint>[] getAllPointData(String mainStatement, Boolean isAvg){
        String dataPerHourOfDayString = mainStatement.replace("DATE(","strftime('%H',");
        String dataPerDayOfWeekString = mainStatement.replace("DATE(","strftime('%w',");
        ArrayList<GraphPoint> dataOverTimePoints = getDataOverTimePoints(mainStatement,isAvg, true);
        ArrayList<GraphPoint> dataPerHourOfDayPoints = addZeroPoints(getDataOverTimePoints(dataPerHourOfDayString,isAvg, false), 1, 24);
        ArrayList<GraphPoint> dataPerDayOfWeekPoints = addZeroPoints(getDataOverTimePoints(dataPerDayOfWeekString,isAvg, false), 0, 6);
        return (ArrayList<GraphPoint>[]) new ArrayList[] {dataOverTimePoints,dataPerHourOfDayPoints,dataPerDayOfWeekPoints};
    }

    private ArrayList<GraphPoint> addZeroPoints(ArrayList<GraphPoint> points, int min, int max){
        for(int x = min;x<=max;x++){
            if(!doesContainSpecifiedXPoint(points,x)){
                points.add(new GraphPoint(x,0));
            }
        }
        points.sort((p1, p2) -> (int) (p1.getX() - p2.getX()));
        return points;
    }

    private boolean doesContainSpecifiedXPoint(ArrayList<GraphPoint> points, int x){
        for(GraphPoint point : points){
            if(point.getX() == x){
                return true;
            }
        }
        return false;
    }

    private String convertFiltersToCase(HashMap<String, List<String>> hashFilters){
        if(hashFilters.size()==0){
            return "1";
        }
        String holdCase = "1";
        for(String filter : hashFilters.keySet()){
            if(filter == "beforeDate"){
                holdCase += " AND date" + " < \"" + hashFilters.get(0) + "\"";
            }else if (filter == "afterDate"){
                holdCase += " AND date" + " > \"" + hashFilters.get(0) + "\"";
            }else{
                holdCase += " AND (" + convertFilterListToString(filter, hashFilters.get(filter)) + ")";
            }
        }
        System.out.println(holdCase);
        return holdCase;
    }

    private String convertFilterListToString(String filter, List<String> filterList){
        String holdFilters = "0";
        for(String filterValue : filterList){
            holdFilters += " OR " + filter + " = \"" + filterValue + "\"";
        }
        return holdFilters;
    }

    private void setChartTypeStrfTime(String chartTypeTime){
        this.chartTypeTime = chartTypeTime;
    }

    public ArrayList<Double> getAllClickCosts(){
        ArrayList<Double> clickCosts = new ArrayList<>();
        ResultSet resultSet = sql.getData("SELECT cost FROM click;");
        while (true) {
            try {
                clickCosts.add(resultSet.getDouble(1));
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return clickCosts;
    }


    //FOR TESTING PURPOSES
    public void openCurrentDatabase(){
        sql.connection("test");
    }
}

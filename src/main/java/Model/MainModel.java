package Model;

import View.CampaignTab;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainModel {

    SQL sql = new SQL();
    private int bounceTime;
    private boolean bounceConversion;

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
            ResultSet set = sql.getData(overallMetricQuery);
            Double value = set.getDouble(1);
            return value;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public ArrayList<GraphPoint> getDataOverTimePoints(String metricOverTimeQuery, boolean shouldGraphAvg){
        if(metricOverTimeQuery.equals("")) return null;
        ArrayList<GraphPoint> metricOverTime = new ArrayList<>();
        ResultSet metricOverTimeSet = sql.getData(metricOverTimeQuery);
        int i = 0;
        try {
            while (metricOverTimeSet.next()) {
                if(shouldGraphAvg) {
                    metricOverTime.add(new GraphPoint(i, metricOverTimeSet.getDouble(2), metricOverTimeSet.getDouble(3)));
                }
                else {
                    metricOverTime.add(new GraphPoint(i, metricOverTimeSet.getDouble(2)));
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
        double uniques = getData("select count(case when " + cases + " then 1 else null end) from (SELECT distinct click.id,gender,ageRange,income from click INNER JOIN person ON click.id = person.id);");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND " + cases + " " + conversionCase + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' AND " + cases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double totalCostClick = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM click INNER JOIN person ON click.id = person.id;");
        double totalCostImpressions = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint> impressionsOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from impressions INNER JOIN person ON impressions.id = person.id) GROUP BY DATE(date);",false);
        ArrayList<GraphPoint> clicksOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from click INNER JOIN person ON click.id = person.id)  GROUP BY DATE(date);",false);
        ArrayList<GraphPoint> uniquesOverTime = getDataOverTimePoints("select d1, IFNULL(c1,0) from (select distinct Date(date) as d1 from click order by d1 asc) left outer join (select DATE(date) as d2, count(case when " + cases + " then 1 end) as c1 from (SELECT * from click INNER JOIN person ON click.id = person.id group by person.id) GROUP BY DATE(date)) on d1=d2;",false);
        ArrayList<GraphPoint> bouncesOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND "+ cases + " " + conversionCase + " then 1 else null end) from (SELECT * from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint> conversionsOverTime = getDataOverTimePoints("Select DATE(date), COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) from (SELECT *  from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint> totalCostOverTime = getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) INNER JOIN (SELECT DATE(date) as d2, SUM(case when " + cases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by DATE(d1);",false);
        ArrayList<GraphPoint> CTROverTime = getDataOverTimePoints("SELECT d1, c, i from (SELECT date(date) as d1, count(case when " + cases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(case when " + cases + " then 1 else null end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPAOverTime = getDataOverTimePoints("SELECT d1, c2, i from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(case when " + cases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(date) as d2, COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPCOverTime = getDataOverTimePoints("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end), count(case when " + cases + " then 1 else null end) from click INNER JOIN person ON click.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint> CPMOverTime = getDataOverTimePoints("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end)*1000, count(case when " + cases + " then 1 else null end) from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint> bounceRateOverTime = getDataOverTimePoints("SELECT d1, i, c from (SELECT DATE(date) as d1, count(case when " + cases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (strftime('%s',exitDate) - strftime('%s',date) < 30 AND " + cases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE (date)) ON d1=d2 GROUP BY DATE(d1);",true);

        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Impressions", impressions, impressionsOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Clicks", clicks, clicksOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Uniques", uniques, uniquesOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Bounces", bounces, bouncesOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Conversions", conversions, conversionsOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Total Cost", totalCost, totalCostOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CTR", clicks/impressions, CTROverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPA", totalCost/conversions, CPAOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPC",  totalCostClick/clicks, CPCOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("CPM", (totalCostImpressions/impressions)*1000, CPMOverTime));
        basicMetrics.add(new CampaignTab.CampaignDataPackage("Bounce Rate",bounces/clicks, bounceRateOverTime));

        return basicMetrics;
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
}

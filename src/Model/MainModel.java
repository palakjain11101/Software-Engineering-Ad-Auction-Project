package Model;

import View.CampaignTab;

import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<CampaignTab.CampaignDataPackage> loadCampaign(){
        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();

        double impressions = getData("SELECT COUNT(*) FROM impressions;");
        double clicks = getData("SELECT COUNT(*) FROM click;");
        double uniques = getData("SELECT COUNT(DISTINCT id) FROM click;");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < 30 then 1 else null end) FROM server");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' then 1 else null end) FROM server");
        double totalCostClick = getData("SELECT SUM(cost) FROM click");
        double totalCostImpressions = getData("SELECT SUM(cost) FROM impressions");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint> impressionsOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from impressions group by DATE(date);",false);
        ArrayList<GraphPoint> clicksOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from click group by DATE(date);",false);
        ArrayList<GraphPoint> uniquesOverTime = getDataOverTimePoints("SELECT DATE(date), count(distinct id) from click group by DATE(date);",false);
        ArrayList<GraphPoint> bouncesOverTime = getDataOverTimePoints("SELECT DATE(date), count(case when strftime('%s',exitDate) - strftime('%s',date) < 30 then 1 else null end) from server group by DATE (date);",false);
        ArrayList<GraphPoint> conversionsOverTime = getDataOverTimePoints("SELECT DATE(date), count(case when conversion = 'Yes' then 1 else null end) from server group by DATE(date);",false);
        ArrayList<GraphPoint> totalCostOverTime = getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1);",false);
        ArrayList<GraphPoint> CTROverTime = getDataOverTimePoints("SELECT d1, c, i from (SELECT date(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(*) as i from impressions group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPAOverTime = getDataOverTimePoints("SELECT d1, c2, i from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(date) as d2, count(*) as i from server where conversion='Yes' group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPCOverTime = getDataOverTimePoints("SELECT DATE(date), sum(cost), count(*) from click group by DATE(date);",true);
        ArrayList<GraphPoint> CPMOverTime = getDataOverTimePoints("SELECT DATE(date), sum(cost)*1000, count(*) from impressions group by DATE(date);",true);
        ArrayList<GraphPoint> bounceRateOverTime = getDataOverTimePoints("SELECT d1, i, c from (SELECT DATE(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, count(*) as i from server where strftime('%s',exitDate) - strftime('%s',date) < 30 group by DATE (date)) ON d1=d2 GROUP BY DATE(d1);",true);

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

    public ArrayList<CampaignTab.CampaignDataPackage> updateFilters(HashMap<String,String> hashFilters){

        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();


        String whereClause = "";

        HashMap<String,String> hashFiltersClone = (HashMap<String,String>) hashFilters.clone();

        if (hashFiltersClone.size() == 0){
            System.out.println("No filters supplied");
        }else {
            HashMap.Entry<String, String> first = hashFiltersClone.entrySet().stream().findFirst().get();
            String key = first.getKey();
            String value = first.getValue();

            hashFiltersClone.remove(key);

            whereClause = "WHERE " + key + " = \"" + value + "\"";

            for(HashMap.Entry<String,String> entry : hashFiltersClone.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();

                whereClause += " AND " + key + " = \"" + value + "\"";

            }
        }

        String countCases = convertFiltersToCase(hashFilters, true);
        String sumCases = convertFiltersToCase(hashFilters, false);

        double impressions = getData("SELECT COUNT(case when " + countCases + " then 1 else null end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double clicks = getData("SELECT COUNT(case when " + countCases + " then 1 else null end) FROM click INNER JOIN person ON click.id = person.id;");
        double uniques = getData("SELECT COUNT(DISTINCT click.id) FROM click INNER JOIN person ON click.id = person.id;");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < 30 AND " + countCases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' AND " + countCases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double totalCostClick = getData("SELECT SUM(case when " + countCases + " then cost else 0 end) FROM click INNER JOIN person ON click.id = person.id;");
        double totalCostImpressions = getData("SELECT SUM(case when " + countCases + " then cost else 0 end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint> impressionsOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when " + countCases + " then 1 else null end) from (SELECT * from impressions INNER JOIN person ON impressions.id = person.id) GROUP BY DATE(date);",false);
        ArrayList<GraphPoint> clicksOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when " + countCases + " then 1 else null end) from (SELECT * from click INNER JOIN person ON click.id = person.id)  GROUP BY DATE(date);",false);
        ArrayList<GraphPoint> uniquesOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when " + countCases + " then 1 else null end) from (SELECT DISTINCT * from click INNER JOIN person ON click.id = person.id) GROUP BY DATE(date);",false);
        ArrayList<GraphPoint> bouncesOverTime = getDataOverTimePoints("select DATE(date), COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < 30 AND " + countCases + " then 1 else null end) from (SELECT * from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint> conversionsOverTime = getDataOverTimePoints("Select DATE(date), COUNT(case when (conversion = 'Yes' AND " + countCases + ") then 1 else null end) from (SELECT *  from server INNER JOIN person ON server.id = person.id) group by DATE(date);",false);
        ArrayList<GraphPoint> totalCostOverTime = getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(case when " + sumCases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) INNER JOIN (SELECT DATE(date) as d2, SUM(case when " + sumCases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by DATE(d1);",false);
        ArrayList<GraphPoint> CTROverTime = getDataOverTimePoints("SELECT d1, c, i from (SELECT date(date) as d1, count(case when " + countCases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(case when " + countCases + " then 1 else null end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPAOverTime = getDataOverTimePoints("SELECT d1, c2, i from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(case when " + sumCases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(case when " + countCases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(date) as d2, COUNT(case when (conversion = 'Yes' AND " + countCases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 group by d1;",true);
        ArrayList<GraphPoint> CPCOverTime = getDataOverTimePoints("SELECT DATE(date), sum(case when " + sumCases + " then cost else 0 end), count(case when " + countCases + " then 1 else null end) from click INNER JOIN person ON click.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint> CPMOverTime = getDataOverTimePoints("SELECT DATE(date), sum(case when " + sumCases + " then cost else 0 end)*1000, count(case when " + countCases + " then 1 else null end) from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date);",true);
        ArrayList<GraphPoint> bounceRateOverTime = getDataOverTimePoints("SELECT d1, i, c from (SELECT DATE(date) as d1, count(case when " + countCases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (strftime('%s',exitDate) - strftime('%s',date) < 30 AND " + countCases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE (date)) ON d1=d2 GROUP BY DATE(d1);",true);

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

    private String convertFiltersToCase(HashMap<String,String> hashFilters, boolean isCount){
        if(hashFilters.size()==0){
            return isCount ? "*" : "cost";
        }
        String holdCase = "1";
        for(String filter : hashFilters.keySet()){
            if(filter == "beforeDate"){
                holdCase += " AND date" + " < \"" + hashFilters.get(filter) + "\"";
            }else if (filter == "afterDate"){
                holdCase += " AND date" + " > \"" + hashFilters.get(filter) + "\"";
            }else{
                holdCase += " AND " + filter + " = \"" + hashFilters.get(filter) + "\"";
            }
        }
        System.out.println(holdCase);
        return holdCase;
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

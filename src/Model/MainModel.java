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

    public ArrayList<CampaignTab.CampaignDataPackage> loadCampaign(){
        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();

        double impressions = getData("SELECT COUNT(*) FROM impressions;");
        double clicks = getData("SELECT COUNT(*) FROM click;");
        double uniques = getData("SELECT COUNT(DISTINCT id) FROM click;");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) FROM server");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' then 1 else null end) FROM server");
        double totalCostClick = getData("SELECT SUM(cost) FROM click");
        double totalCostImpressions = getData("SELECT SUM(cost) FROM impressions");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint> impressionsOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from impressions group by DATE(date);");
        ArrayList<GraphPoint> clicksOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from click group by DATE(date);");
        ArrayList<GraphPoint> uniquesOverTime = getDataOverTimePoints("SELECT DATE(date), count(distinct id) from click group by DATE(date);");
        ArrayList<GraphPoint> bouncesOverTime = getDataOverTimePoints("SELECT DATE(entryDate), count(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) from server group by DATE (entryDate);");
        ArrayList<GraphPoint> conversionsOverTime = getDataOverTimePoints("SELECT DATE(entryDate), count(case when conversion = 'Yes' then 1 else null end) from server group by DATE(entryDate);");
        ArrayList<GraphPoint> totalCostOverTime = getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1);");
        ArrayList<GraphPoint> CTROverTime = getDataOverTimePoints("SELECT d1, CAST(c as float)/CAST(i as float) from (SELECT date(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(*) as i from impressions group by DATE(date)) ON d1=d2 group by d1;");
        ArrayList<GraphPoint> CPAOverTime = getDataOverTimePoints("SELECT d1, CAST(c2 as float)/CAST(i as float) from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(entryDate) as d2, count(*) as i from server where conversion='Yes' group by DATE(entryDate)) ON d1=d2 group by d1;");
        ArrayList<GraphPoint> CPCOverTime = getDataOverTimePoints("SELECT DATE(date), avg(cost) from click group by DATE(date);");
        ArrayList<GraphPoint> CPMOverTime = getDataOverTimePoints("SELECT DATE(date), avg(cost)*1000 from impressions group by DATE(date);");
        ArrayList<GraphPoint> bounceRateOverTime = getDataOverTimePoints("SELECT d1, CAST(i as float)/CAST(c as float) from (SELECT DATE(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(entryDate) as d2, count(*) as i from server where strftime('%s',exitDate) - strftime('%s',entryDate) < 30 group by DATE (entryDate)) ON d1=d2 GROUP BY DATE(d1);");

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

        if (hashFilters.size() == 0){
            System.out.println("No filters supplied");
        }else {
            HashMap.Entry<String, String> first = hashFilters.entrySet().stream().findFirst().get();
            String key = first.getKey();
            String value = first.getValue();

            hashFilters.remove(key);

            whereClause = "WHERE " + key + " = \"" + value + "\"";

            for(HashMap.Entry<String,String> entry : hashFilters.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();

                whereClause += " AND " + key + " = \"" + value + "\"";

            }
        }

        double impressions = getData("SELECT COUNT(*) FROM impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " ;");
        double clicks = getData("SELECT COUNT(*) FROM click INNER JOIN person ON click.id = person.id " + whereClause + " ;");
        double uniques = getData("SELECT COUNT(DISTINCT click.id) FROM click INNER JOIN person ON click.id = person.id " + whereClause + " ;");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) FROM server INNER JOIN person ON server.id = person.id " + whereClause + " ;");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' then 1 else null end) FROM server INNER JOIN person ON server.id = person.id " + whereClause + " ;");
        double totalCostClick = getData("SELECT SUM(cost) FROM click INNER JOIN person ON click.id = person.id " + whereClause + " ;");
        double totalCostImpressions = getData("SELECT SUM(cost) FROM impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " ;");
        double totalCost = totalCostClick + totalCostImpressions;

        ArrayList<GraphPoint> impressionsOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " group by DATE(date);");
        ArrayList<GraphPoint> clicksOverTime = getDataOverTimePoints("SELECT DATE(date), count(*) from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date);");
        ArrayList<GraphPoint> uniquesOverTime = getDataOverTimePoints("SELECT DATE(date), count(distinct click.id) from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date);");
        ArrayList<GraphPoint> bouncesOverTime = getDataOverTimePoints("SELECT DATE(entryDate), COUNT(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) from server INNER JOIN person ON server.id = person.id " + whereClause + " group by DATE (entryDate);");
        ArrayList<GraphPoint> conversionsOverTime = getDataOverTimePoints("SELECT DATE(entryDate), COUNT(case when conversion = 'Yes' then 1 else null end) from server INNER JOIN person ON server.id = person.id " + whereClause + " group by DATE(entryDate);");
        ArrayList<GraphPoint> totalCostOverTime = getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(cost) as c from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " group by DATE(date)) ON d1=d2 group by DATE(d1);");
        ArrayList<GraphPoint> CTROverTime = getDataOverTimePoints("SELECT d1, CAST(c as float)/CAST(i as float) from (SELECT date(date) as d1, count(*) as c from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(*) as i from impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " group by DATE(date)) ON d1=d2 group by d1;");
        ArrayList<GraphPoint> CPAOverTime = getDataOverTimePoints("SELECT d1, CAST(c2 as float)/CAST(i as float) from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(cost) as c from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(entryDate) as d2, COUNT(case when conversion = 'Yes' then 1 else null end) as i from server INNER JOIN person ON server.id = person.id " + whereClause + " group by DATE(entryDate)) ON d1=d2 group by d1;");
        ArrayList<GraphPoint> CPCOverTime = getDataOverTimePoints("SELECT DATE(date), avg(cost) from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date);");
        ArrayList<GraphPoint> CPMOverTime = getDataOverTimePoints("SELECT DATE(date), avg(cost)*1000 from impressions INNER JOIN person ON impressions.id = person.id " + whereClause + " group by DATE(date);");
        ArrayList<GraphPoint> bounceRateOverTime = getDataOverTimePoints("SELECT d1, CAST(i as float)/CAST(c as float) from (SELECT DATE(date) as d1, count(*) as c from click INNER JOIN person ON click.id = person.id " + whereClause + " group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(entryDate) as d2, COUNT(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) as i from server INNER JOIN person ON server.id = person.id " + whereClause + " group by DATE (entryDate)) ON d1=d2 GROUP BY DATE(d1);");

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
}

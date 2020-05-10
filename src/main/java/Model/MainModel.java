package Model;

import View.CampaignTab;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.sqrt;

public class MainModel {

    private String currentcampaignId = "";

    private SQL sql = new SQL();
    private int bounceTime;
    private int bouncePages;

    private HashMap<String, HashMap<String, List<String>>> allFilters;
    private String graphType;
    //private String chartTypeTime = "DATE(";

    public MainModel() {
        // Set default values
        bounceTime = 30;
        bouncePages = 10;
        graphType = "Standard";
        allFilters = new HashMap<>();
    }

    public void setBounceAttributes(int time, int pages) {
        bounceTime = time;
        bouncePages = pages;
    }


    public String createNewCampaign(File clickLogPath, File impressionLogPath, File serverLogPath, String campaignId) {
        try {
            sql.connection(campaignId);
            sql.createTable(campaignId);
            sql.putData(impressionLogPath.getPath(), "impressions",campaignId);
            sql.putData(clickLogPath.getPath(), "click",campaignId);
            sql.putData(serverLogPath.getPath(), "server",campaignId);
            allFilters.put(campaignId,new HashMap<>());
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    public void deleteCampaign(String campaignID){
        sql.deleteDatabase(campaignID);
        allFilters.remove(campaignID);
        if(currentcampaignId.equals(campaignID)){
            currentcampaignId = "";
        }
    }

    public Double getData(String overallMetricQuery) {
        try {
            ResultSet set = sql.getData(overallMetricQuery, currentcampaignId);
            Double value = set.getDouble(1);
            return round(value, 2);
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

    private double safeDivide(double v1, double v2) {
        return v2 == 0 ? 0 : v1 / v2;
    }

    public ArrayList<GraphPoint> getDataOverTimePoints(String metricOverTimeQuery, boolean shouldGraphAvg, boolean shouldXAxisBeIncrement) {
        if (metricOverTimeQuery.equals("")) return null;
        ArrayList<GraphPoint> metricOverTime = new ArrayList<>();
        ResultSet metricOverTimeSet = sql.getData(metricOverTimeQuery, currentcampaignId);
        int i = 0;
        try {
            while (metricOverTimeSet.next()) {
                if (shouldGraphAvg) {
                    metricOverTime.add(new GraphPoint(shouldXAxisBeIncrement ? i : metricOverTimeSet.getInt(1), metricOverTimeSet.getDouble(2), metricOverTimeSet.getDouble(3)));
                } else {
                    metricOverTime.add(new GraphPoint(shouldXAxisBeIncrement ? i : metricOverTimeSet.getInt(1), metricOverTimeSet.getDouble(2)));
                }
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metricOverTime;
    }

    public void setFilters(HashMap<String, List<String>> filters, String campaignID) {
        allFilters.put(campaignID,filters);
    }

    public HashMap<String, List<String>> getFilters(String campaignID) {
        return allFilters.get(campaignID);
    }

    public void setChartType(String type){
        graphType = type;
    }

    public String getGraphType(){return graphType;}

    public ArrayList<CampaignTab.CampaignDataPackage> queryOverallMetrics(String campaignId) {

        currentcampaignId = campaignId;

        ArrayList<CampaignTab.CampaignDataPackage> mertics = new ArrayList<>();
        String cases = convertFiltersToCase(allFilters.get(campaignId));

        double impressions = getData("SELECT COUNT(case when " + cases + " then 1 else null end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double clicks = getData("SELECT COUNT(case when " + cases + " then 1 else null end) FROM click INNER JOIN person ON click.id = person.id;");
        double uniques = getData("select count(case when " + cases + " then 1 else null end) from (SELECT distinct date,click.id,gender,ageRange,income,context from click INNER JOIN person ON click.id = person.id);");
        double bounces = getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND pagesViewed <= " + bouncePages + " AND " + cases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double conversions = getData("SELECT COUNT(case when conversion = 'Yes' AND " + cases + " then 1 else null end) FROM server INNER JOIN person ON server.id = person.id;");
        double totalCostClick = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM click INNER JOIN person ON click.id = person.id;");
        double totalCostImpressions = getData("SELECT SUM(case when " + cases + " then cost else 0 end) FROM impressions INNER JOIN person ON impressions.id = person.id;");
        double totalCost = totalCostClick + totalCostImpressions;

        mertics.add(new CampaignTab.CampaignDataPackage("Number of Impressions",impressions));
        mertics.add(new CampaignTab.CampaignDataPackage("Number of Clicks",clicks));
        mertics.add(new CampaignTab.CampaignDataPackage("Number of Uniques",uniques));
        mertics.add(new CampaignTab.CampaignDataPackage("Number of Bounces",bounces));
        mertics.add(new CampaignTab.CampaignDataPackage("Number of Conversions",conversions));
        mertics.add(new CampaignTab.CampaignDataPackage("Total Cost",totalCost));
        mertics.add(new CampaignTab.CampaignDataPackage("CTR",round(safeDivide(clicks, impressions), 2)));
        mertics.add(new CampaignTab.CampaignDataPackage("CPA",round(safeDivide(totalCost, conversions), 2)));
        mertics.add(new CampaignTab.CampaignDataPackage("CPC",round(safeDivide(totalCostClick, clicks), 2)));
        mertics.add(new CampaignTab.CampaignDataPackage("CPM",round(safeDivide(totalCostImpressions, impressions) * 100, 2)));
        mertics.add(new CampaignTab.CampaignDataPackage("Bounce Rate",round(safeDivide(bounces, clicks), 2)));

        return mertics;
    }

    public ArrayList<GraphPoint> queryCampaign(String metric, String campaignId) {

        currentcampaignId = campaignId;

        String cases = convertFiltersToCase(allFilters.get(campaignId));

        switch (metric) {
            case "Number of Impressions":
                return getDataPoints("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from impressions INNER JOIN person ON impressions.id = person.id) GROUP BY DATE(date);", false);

            case "Number of Clicks":
                return getDataPoints("select DATE(date), COUNT(case when " + cases + " then 1 else null end) from (SELECT * from click INNER JOIN person ON click.id = person.id)  GROUP BY DATE(date);", false);

            case "Number of Uniques":
                return getDataPoints("select d1, IFNULL(c1,0) from (select distinct DATE(date) as d1 from click order by d1 asc) left outer join (select DATE(date) as d2, count(case when " + cases + " then 1 end) as c1 from (SELECT * from click INNER JOIN person ON click.id = person.id group by person.id) GROUP BY DATE(date)) on d1=d2;", false);

            case "Number of Bounces":
                return getDataPoints("select DATE(date), COUNT(case when strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND pagesViewed <= " + bouncePages + " AND " + cases + " then 1 else null end) from (SELECT * from server INNER JOIN person ON server.id = person.id) group by DATE(date);", false);

            case "Number of Conversions":
                return getDataPoints("Select DATE(date), COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) from (SELECT *  from server INNER JOIN person ON server.id = person.id) group by DATE(date);", false);

            case "Total Cost":
                return getDataPoints("select d1, SUM(c) from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date) UNION ALL SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) group by d1;", false);

            case "CTR":
                return getDataPoints("SELECT d, SUM(c), SUM(i) from (SELECT DATE(date) as d, count(case when " + cases + " then 1 else null end) as c, 0 as i from click INNER JOIN person ON click.id = person.id group by DATE(date) UNION ALL SELECT DATE(date) as d, 0 as c, count(case when " + cases + " then 1 else null end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) group by d;", true);

            case "CPA":
                return getDataPoints("SELECT d1, c2, i from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(case when " + cases + " then cost else 0 end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(case when " + cases + " then cost else 0 end) as i from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date)) ON d1=d2 group by d1) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (conversion = 'Yes' AND " + cases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 group by d1;", true);

            case "CPC":
                return getDataPoints("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end), count(case when " + cases + " then 1 else null end) from click INNER JOIN person ON click.id = person.id group by DATE(date);", true);

            case "CPM":
                return getDataPoints("SELECT DATE(date), sum(case when " + cases + " then cost else 0 end)*1000, count(case when " + cases + " then 1 else null end) from impressions INNER JOIN person ON impressions.id = person.id group by DATE(date);", true);

            case "Bounce Rate":
                return getDataPoints("SELECT d1, i, c from (SELECT DATE(date) as d1, count(case when " + cases + " then 1 else null end) as c from click INNER JOIN person ON click.id = person.id group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, COUNT(case when (strftime('%s',exitDate) - strftime('%s',date) < " + bounceTime + " AND pagesViewed <= " + bouncePages + " AND " + cases + ") then 1 else null end) as i from server INNER JOIN person ON server.id = person.id group by DATE(date)) ON d1=d2 GROUP BY d1;", true);

        }
        return null;
    }

    private ArrayList<GraphPoint> getDataPoints(String query, Boolean isAvg) {
        ArrayList<GraphPoint> dataPoints;
        if (graphType.equals("Standard")) {
            dataPoints = getDataOverTimePoints(query, isAvg, true);
            dataPoints = setOutliers(dataPoints);

        } else if (graphType.equals("Per Hour of Day")) {
            dataPoints = addZeroPoints(getDataOverTimePoints(query.replace("DATE(", "strftime('%H',"), isAvg, false),1,24);

        } else {
            dataPoints = addZeroPoints(getDataOverTimePoints(query.replace("DATE(", "strftime('%w',"), isAvg, false),0,6);

        }
        return dataPoints;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<GraphPoint> setOutliers(ArrayList<GraphPoint> dataPoints) {
        double mean = this.computeMean(dataPoints);
        double stdDev = this.getStdDev(dataPoints , mean);


        for (GraphPoint x : dataPoints) {
            double distance = Math.abs(x.getY() - mean);
            if (distance > (2.0 * stdDev)) {
                //System.out.println("Value " + x.getY() + " is an outlier");
                x.setOutlier();
            }
        }

        return dataPoints;
    }


    public Double getStdDev(ArrayList<GraphPoint> dataPoints, double mean) {
        double sigma = 0.0;

        for (GraphPoint x : dataPoints) {
            sigma = sigma + ((mean - x.getY())*(mean - x.getY()));
        }

        //System.out.printf("Sigma: %f\n", sigma);
        double inv = 1.0 / ((double)dataPoints.size() - 1.0);

        double variance = inv * sigma;
        //System.out.printf("Variance: %f\n", variance);

        return sqrt(variance);
    }

    public Double computeMean(ArrayList<GraphPoint> dataPoints) {

        double mean = 0.0;

        double sum = 0.0;

        for (GraphPoint x : dataPoints) {
            sum = sum + x.getY();
        }

        mean = sum / dataPoints.size();
        //System.out.printf("Mean: %f\n", mean);

        return mean;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private ArrayList<GraphPoint> addZeroPoints(ArrayList<GraphPoint> points, int min, int max) {
        for (int x = min; x <= max; x++) {
            if (!doesContainSpecifiedXPoint(points, x)) {
                points.add(new GraphPoint(x, 0));
            }
        }
        points.sort((p1, p2) -> (int) (p1.getX() - p2.getX()));
        return points;
    }

    private boolean doesContainSpecifiedXPoint(ArrayList<GraphPoint> points, int x) {
        for (GraphPoint point : points) {
            if (point.getX() == x) {
                return true;
            }
        }
        return false;
    }

    private String convertFiltersToCase(HashMap<String, List<String>> hashFilters) {
        if (hashFilters.size() == 0) {
            return "1";
        }
        String holdCase = "1";
        for (String filter : hashFilters.keySet()) {
            if (filter == "dateBefore") {
                holdCase += " AND date" + " < \"" + hashFilters.get(filter).get(0) + "\"";
            } else if (filter == "dateAfter") {
                holdCase += " AND date" + " > \"" + hashFilters.get(filter).get(0) + "\"";
            } else {
                holdCase += " AND (" + convertFilterListToString(filter, hashFilters.get(filter)) + ")";
            }
        }
        System.out.println(holdCase);
        return holdCase;
    }

    private String convertFilterListToString(String filter, List<String> filterList) {
        String holdFilters = "0";
        for (String filterValue : filterList) {
            holdFilters += " OR " + filter + " = \"" + filterValue + "\"";
        }
        return holdFilters;
    }

    public ArrayList<Double> getAllClickCosts(String campaignId) {
        ArrayList<Double> clickCosts = new ArrayList<>();
        ResultSet resultSet = sql.getData("SELECT cost FROM click", campaignId);
        double d;
        while (true) {
            try {
                d = resultSet.getDouble(1);
                if(!(d == 0.0)){
                    clickCosts.add(d);
                }
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return clickCosts;
    }


    //FOR TESTING PURPOSES
    public void openCurrentDatabase() {
        sql.connection("test");
    }


}

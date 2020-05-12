package Model;

import View.CampaignTab;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.junit.runner.RunWith;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

//@RunWith(Arquillian.class)
public class MainModelTest {

    private static final String ID = "test";
    private static final double DELTA = 1e-15;
    private static MainModel model = new MainModel();

//    @Deployment
//    public static JavaArchive createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class)
//                .addClass(MainModel.class)
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//    }

    @BeforeClass
    public static void setUp(){
        File click = new File("testClickLog.csv");
        File impression = new File("testImpressionLog.csv");
        File server = new File("testServerLog.csv");
        model.createNewCampaign(click,impression,server,ID);
    }

    @Test
    public void testBaseMetricValues() {
        model.setFilters(new HashMap<>(),ID);
        ArrayList<CampaignTab.CampaignDataPackage> data = model.queryOverallMetrics(ID);
        Assert.assertEquals(8, data.get(0).getOverallMetric(),DELTA);
        Assert.assertEquals(7, data.get(1).getOverallMetric(),DELTA);
        Assert.assertEquals(7, data.get(2).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(3).getOverallMetric(),DELTA);
        Assert.assertEquals(5, data.get(4).getOverallMetric(),DELTA);
        Assert.assertEquals(8, data.get(5).getOverallMetric(),DELTA);
        Assert.assertEquals(0.88, data.get(6).getOverallMetric(),DELTA);
        Assert.assertEquals(1.6, data.get(7).getOverallMetric(),DELTA);
        Assert.assertEquals(0.29, data.get(8).getOverallMetric(),DELTA);
        Assert.assertEquals(75, data.get(9).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(10).getOverallMetric(),DELTA);
    }

    @Test
    public void testBaseMetricValuesWithDateFilters() {
        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("dateBefore", Arrays.asList("2017-01-06 00:00:00"));
        filters.put("dateAfter",Arrays.asList("2017-01-03 00:00:00"));
        model.setFilters(filters,ID);

        ArrayList<CampaignTab.CampaignDataPackage> data = model.queryOverallMetrics(ID);
        Assert.assertEquals(3, data.get(0).getOverallMetric(),DELTA);
        Assert.assertEquals(3, data.get(1).getOverallMetric(),DELTA);
        Assert.assertEquals(3, data.get(2).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(3).getOverallMetric(),DELTA);
        Assert.assertEquals(2, data.get(4).getOverallMetric(),DELTA);
        Assert.assertEquals(3, data.get(5).getOverallMetric(),DELTA);
        Assert.assertEquals(1, data.get(6).getOverallMetric(),DELTA);
        Assert.assertEquals(1.5, data.get(7).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(8).getOverallMetric(),DELTA);
        Assert.assertEquals(100, data.get(9).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(10).getOverallMetric(),DELTA);
        model.setFilters(new HashMap<>(),ID);
    }

    @Test
    public void testBaseMetricValuesWithOtherFilters(){
        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("gender",Arrays.asList("Male"));
        filters.put("context",Arrays.asList("Blog"));
        filters.put("income",Arrays.asList("Medium"));
        filters.put("ageRange",Arrays.asList("<25"));
        model.setFilters(filters,ID);

        ArrayList<CampaignTab.CampaignDataPackage> data = model.queryOverallMetrics(ID);
        Assert.assertEquals(4, data.get(0).getOverallMetric(),DELTA);
        Assert.assertEquals(3, data.get(1).getOverallMetric(),DELTA);
        Assert.assertEquals(3, data.get(2).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(3).getOverallMetric(),DELTA);
        Assert.assertEquals(2, data.get(4).getOverallMetric(),DELTA);
        Assert.assertEquals(4, data.get(5).getOverallMetric(),DELTA);
        Assert.assertEquals(0.75, data.get(6).getOverallMetric(),DELTA);
        Assert.assertEquals(2, data.get(7).getOverallMetric(),DELTA);
        Assert.assertEquals(0.67, data.get(8).getOverallMetric(),DELTA);
        Assert.assertEquals(50, data.get(9).getOverallMetric(),DELTA);
        Assert.assertEquals(0, data.get(10).getOverallMetric(),DELTA);
        model.setFilters(new HashMap<>(),ID);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGraphPointValues(){
        model.queryCampaign("Number of Impressions",ID);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Impressions",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Clicks",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Uniques",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Bounces",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Conversions",ID)), is(Arrays.asList(1.0,0.0,1.0,0.0,1.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Total Cost",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CTR",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,0.5)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPA",ID)), is(Arrays.asList(1.0,0.0,1.0,0.0,1.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPC",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPM",ID)), is(Arrays.asList(1000.0,1000.0,1000.0,1000.0,1000.0,0.0,500.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Bounce Rate",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGraphPointValuesWithDateFilters(){
        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("dateBefore", Arrays.asList("2017-01-06 00:00:00"));
        filters.put("dateAfter",Arrays.asList("2017-01-03 00:00:00"));
        model.setFilters(filters,ID);

        //Same as normal graph but with the first 2 and last 2 removed
        model.queryCampaign("Number of Impressions",ID);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Impressions",ID)), is(Arrays.asList(0.0,0.0,1.0,1.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Clicks",ID)), is(Arrays.asList(0.0,0.0,1.0,1.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Uniques",ID)), is(Arrays.asList(0.0,0.0,1.0,1.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Bounces",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Conversions",ID)), is(Arrays.asList(0.0,0.0,1.0,0.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Total Cost",ID)), is(Arrays.asList(0.0,0.0,1.0,1.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CTR",ID)), is(Arrays.asList(0.0,0.0,1.0,1.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPA",ID)), is(Arrays.asList(0.0,0.0,1.0,0.0,1.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPC",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPM",ID)), is(Arrays.asList(0.0,0.0,1000.0,1000.0,1000.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Bounce Rate",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        model.setFilters(new HashMap<>(),ID);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGraphPointValuesWithOtherFilters(){
        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("gender",Arrays.asList("Male"));
        filters.put("context",Arrays.asList("Blog"));
        filters.put("income",Arrays.asList("Medium"));
        filters.put("ageRange",Arrays.asList("<25"));
        model.setFilters(filters,ID);

        model.queryCampaign("Number of Impressions",ID);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Impressions",ID)), is(Arrays.asList(0.0,0.0,0.0,1.0,0.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Clicks",ID)), is(Arrays.asList(0.0,0.0,0.0,1.0,0.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Conversions",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Total Cost",ID)), is(Arrays.asList(0.0,0.0,0.0,1.0,0.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPA",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,1.0,2.0)));
        model.setFilters(new HashMap<>(),ID);
    }

    @Test
    public void testGraphPointValuesOnPerHoursOfDayGraph(){
        model.setChartType("Per Hour of Day");

        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Total Cost",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,3.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CTR",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.8,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Clicks",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,3.0,4.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));

    }

    @Test
    public void testGraphPointValuesOnPerDayOfWeekGraph(){
        model.setChartType("Per Day of Week");
        //model.queryCampaign("Number of Impressions",ID);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Impressions",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Conversions",ID)), is(Arrays.asList(1.0,0.0,1.0,0.0,1.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPA",ID)), is(Arrays.asList(1.0,0.0,1.0,0.0,1.0,1.0,2.0)));

    }


    @Test
    public void testGraphPointValuesOnPerDayOfWeekGraphWithFilters(){
        model.setChartType("Per Day of Week");

        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("gender",Arrays.asList("Male"));
        filters.put("income",Arrays.asList("Medium","High"));
        filters.put("ageRange",Arrays.asList("<25"));
        model.setFilters(filters,ID);

        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPA",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,1.0,2.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Uniques",ID)), is(Arrays.asList(0.0,0.0,0.0,1.0,0.0,1.0,1.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPM",ID)), is(Arrays.asList(0.0,0.0,0.0,1000.0,0.0,0.0,500.0)));

        model.setFilters(new HashMap<>(),ID);
    }

    @Test
    public void testGraphPointValuesOnPerHourOfDayGraphWithFilters(){
        model.setChartType("Per Hour of Day");

        HashMap<String, List<String>> filters = new HashMap<>();
        filters.put("gender",Arrays.asList("Male"));
        filters.put("income",Arrays.asList("Low","Medium"));
        filters.put("ageRange",Arrays.asList("<25"));
        model.setFilters(filters,ID);

        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Total Cost",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,2.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Conversions",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,2.0,3.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("CPM",ID)), is(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1000.0,600.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)));

        model.setFilters(new HashMap<>(),ID);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testChangesInBounceRate(){
        model.setFilters(new HashMap<>(),ID);
        model.setBounceAttributes(Integer.MAX_VALUE,3);
        ArrayList<CampaignTab.CampaignDataPackage> data = model.queryOverallMetrics(ID);
        Assert.assertEquals(1, data.get(3).getOverallMetric(),DELTA);
        Assert.assertEquals(0.14, data.get(10).getOverallMetric(),DELTA);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Bounces",ID)), is(Arrays.asList(1.0,0.0,0.0,0.0,0.0,0.0,0.0)));

        model.setBounceAttributes(Integer.MAX_VALUE,Integer.MAX_VALUE);
        data = model.queryOverallMetrics(ID);
        Assert.assertEquals(7, data.get(3).getOverallMetric(),DELTA);
        Assert.assertEquals(1, data.get(10).getOverallMetric(),DELTA);
        Assert.assertThat(convertGraphPointToListOfYPoints(model.queryCampaign("Number of Bounces",ID)), is(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,1.0)));
    }

    @Test
    public void testOutliers(){
        ArrayList<GraphPoint> points = new ArrayList<>();
        ArrayList<GraphPoint> modifiedPoints;

        points.add(new GraphPoint(1,1));
        points.add(new GraphPoint(2,1));
        points.add(new GraphPoint(3,1));
        points.add(new GraphPoint(4,1));
        points.add(new GraphPoint(5,5));

        modifiedPoints = model.setOutliers(points,10);
        assertFalse(modifiedPoints.get(4).getOutlier());
        modifiedPoints = model.setOutliers(points,0.5);
        assertTrue(modifiedPoints.get(4).getOutlier());
    }

    private List<Double> convertGraphPointToListOfYPoints(List<GraphPoint> points){
        List<Double> yPoints = new ArrayList<>();
        for(GraphPoint point : points){
            yPoints.add(point.getY());
        }
        return yPoints;
    }

    @Before
    public void revertFiltersAndBounce(){
        model.setFilters(new HashMap<>(),ID);
        model.setBounceAttributes(30,Integer.MAX_VALUE);
    }

    @AfterClass
    public static void deleteTestCampaign(){
        model.deleteCampaign(ID);
    }
}

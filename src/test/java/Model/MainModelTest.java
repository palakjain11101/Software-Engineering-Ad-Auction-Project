package Model;

import View.CampaignTab;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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

    }

    @Test
    public void testBaseMetricValuesWithOtherFilters(){

    }

    @Test
    public void testGraphPointValues(){

    }

    @Test
    public void testGraphPointValuesWithDateFilters(){

    }

    @Test
    public void testGraphPointValuesWithOtherFilters(){

    }

    @Test
    public void testGraphPointValuesOnPerHoursOfDayGraph(){

    }

    @Test
    public void testGraphPointValuesOnPerDayOfWeekGraph(){

    }

    @Test
    public void testChangesInBounceRate(){

    }

    @Test
    public void testOutliers(){

    }

    @AfterClass
    public static void deleteTestCampaign(){

    }
}

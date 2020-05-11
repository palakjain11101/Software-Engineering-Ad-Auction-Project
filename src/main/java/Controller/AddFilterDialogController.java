package Controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


public class AddFilterDialogController<string> {

    @FXML
    DatePicker dateBefore;
    @FXML
    DatePicker dateAfter;
    @FXML
    private CheckBox lowIncome;
    @FXML
    private CheckBox mediumIncome;
    @FXML
    private CheckBox highIncome;
    @FXML
    private CheckBox newsContext;
    @FXML
    private CheckBox blogContext;
    @FXML
    private CheckBox shoppingContext;
    @FXML
    private CheckBox hobbiesContext;
    @FXML
    private CheckBox socialContext;
    @FXML
    private CheckBox travelContext;
    @FXML
    private CheckBox ageLT25;
    @FXML
    private CheckBox age25To34;
    @FXML
    private CheckBox age35To44;
    @FXML
    private CheckBox age45to54;
    @FXML
    private CheckBox ageGT54;
    @FXML
    private CheckBox genderMale;
    @FXML
    private CheckBox genderFemale;

    private HashMap<String,List<String>> filters;

    private boolean isConfirmPressed = false;

    public void init(HashMap<String,List<String>> filters){
        this.filters = filters;
        if(isDateSet("dateBefore")){
            dateBefore.setValue(getDate("dateBefore"));
        }
        if(isDateSet("dateAfter")){
            dateAfter.setValue(getDate("dateAfter"));
        }
        lowIncome.setSelected(isFilterSelected("income","Low"));
        mediumIncome.setSelected(isFilterSelected("income","Medium"));
        highIncome.setSelected(isFilterSelected("income","High"));
        newsContext.setSelected(isFilterSelected("context","News"));
        blogContext.setSelected(isFilterSelected("context","Blog"));
        shoppingContext.setSelected(isFilterSelected("context","Shopping"));
        hobbiesContext.setSelected(isFilterSelected("context","Hobbies"));
        socialContext.setSelected(isFilterSelected("context","Social Media"));
        travelContext.setSelected(isFilterSelected("context","Travel"));
        ageLT25.setSelected(isFilterSelected("ageRange","<25"));
        age25To34.setSelected(isFilterSelected("ageRange","25-34"));
        age35To44.setSelected(isFilterSelected("ageRange","35-44"));
        age45to54.setSelected(isFilterSelected("ageRange","45-54"));
        ageGT54.setSelected(isFilterSelected("ageRange",">54"));
        genderMale.setSelected(isFilterSelected("gender","Male"));
        genderFemale.setSelected(isFilterSelected("gender","Female"));
    }

    private boolean isFilterSelected(String metric, String filter){
        List<String> filtersForMetric = filters.get(metric);
        if(filtersForMetric == null){
            return false;
        }
        return filtersForMetric.contains(filter);
    }

    private boolean isDateSet(String metric){
        List<String> filtersForMetric = filters.get(metric);
        return !(filtersForMetric == null);
    }

    private LocalDate getDate(String metric){
        List<String> filtersForMetric = filters.get(metric);
        String dateString = filtersForMetric.get(0).split(" ")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    @FXML public void dateBeforeChanged(){
        setDate("dateBefore",dateBefore);
    }
    @FXML public void dateAfterChanged(){
        setDate("dateAfter",dateAfter);
    }
    @FXML public void dateBeforeClear(){
        dateBefore.setValue(null);
        filters.remove("dateBefore");
    }
    @FXML public void dateAfterClear(){
        dateAfter.setValue(null);
        filters.remove("dateAfter");
    }

    private void setDate(String type, DatePicker picker){
        LocalDate date = picker.getValue();
        if(date == null){
            filters.remove(type);
        }
        else {
            List<String> dateList = new ArrayList<>();
            String dateString = date.toString() + " 00:00:00";
            dateList.add(dateString);
            filters.put(type,dateList);
        }
    }

    @FXML public void lowIncomeChanged(){onSelected("income","Low");}
    @FXML public void mediumIncomeChanged(){onSelected("income","Medium");}
    @FXML public void highIncomeChanged(){onSelected("income","High");}
    @FXML public void newsContextChanged(){onSelected("context","News");}
    @FXML public void blogContextChanged(){onSelected("context","Blog");}
    @FXML public void shoppingContextChanged(){onSelected("context","Shopping");}
    @FXML public void hobbiesContextChanged(){onSelected("context","Hobbies");}
    @FXML public void socialContextChanged(){onSelected("context","Social Media");}
    @FXML public void travelContextChanged(){onSelected("context","Travel");}
    @FXML public void ageLT25Changed(){onSelected("ageRange","<25");}
    @FXML public void age25To34Changed(){onSelected("ageRange","25-34");}
    @FXML public void age35To44Changed(){onSelected("ageRange","35-44");}
    @FXML public void age45to54Changed(){onSelected("ageRange","45-54");}
    @FXML public void ageGT54Changed(){onSelected("ageRange",">54");}
    @FXML public void genderMaleChanged(){onSelected("gender","Male");}
    @FXML public void genderFemaleChanged(){onSelected("gender","Female");}

    private void onSelected(String metric, String filter){
        boolean isSelected = isFilterSelected(metric,filter);
        List<String> filterList = filters.get(metric);
        filterList = filterList == null ? new ArrayList<>() : filterList;
        if(isSelected){
            filterList.remove(filter);
        }
        else {
            filterList.add(filter);
        }
        if(filterList.size()==0){
            filters.remove(metric);
        }
        else {
            filters.put(metric, filterList);
        }
    }

    @FXML public void confirmPressed(){
        isConfirmPressed = true;
        Stage stage = (Stage) lowIncome.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmPressed(){
        return isConfirmPressed;
    }

    public HashMap<String,List<String>> getFilters(){
        return filters;
    }

    @FXML public void getHelp(){
        OutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Hello World!"));
        document.close();

    }



}













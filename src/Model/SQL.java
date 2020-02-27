package Model;

import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class SQL {
    private Connection c;
    private Statement stmt;


    public SQL(){
        this.connection();
        this.createTable();
        this.putData("click_log.csv","click");
        this.putData("impression_log.csv","impressions");
        this.putData("server_log.csv","server");
    }

    public void connection(){
        this.c = null;


        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public void createTable(){
        String serverTable = "CREATE TABLE server (\n"
                + " id integer,\n"
                + " entryDate text,\n"
                + " exitDate text,\n"
                + " pagesViewed integer,\n"
                + " conversion text,\n"
                + " PRIMARY KEY (id,entryDate));";

        String impressionsTable = "CREATE TABLE impressions (\n"
                + " id integer,\n"
                + " date text,\n"
                + " gender text,\n"
                + " ageRange text,\n"
                + " income text,\n"
                + " context text,\n"
                + " cost real,\n"
                + " PRIMARY KEY (id,date));";

        String clickTable = "CREATE TABLE click (\n"
                + " id integer,\n"
                + " date text,\n"
                + " cost real,\n"
                + " PRIMARY KEY (id,date));";

        try{
            this.stmt = this.c.createStatement();
            stmt.execute(serverTable);
            stmt.execute(impressionsTable);
            stmt.execute(clickTable);
            System.out.println("WORKED");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void putData(String file, String table){

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                String insertLine;

                if (table == "click" && !data[0].equals("Date") ){
                    insertLine = "INSERT INTO click VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\");";
                    try{
                        stmt.execute(insertLine);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }else if (table == "impressions" && !data[0].equals("Date")){
                    insertLine = "INSERT INTO impressions VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\",\"" + data[3] + "\",\"" + data[4] + "\",\"" + data[5] + "\",\"" + data[6] + "\");";
                    try{
                        stmt.execute(insertLine);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }else if (table == "server" && !data[0].equals("Entry Date")){
                    insertLine = "INSERT INTO server VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\",\"" + data[3] + "\",\"" + data[4] + "\");";
                    try{
                        stmt.execute(insertLine);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }else{
                    insertLine = "";
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ResultSet getData(String query){
        ResultSet rs = stmt.execute(query);
        return rs;
    }

    public static void main( String args[] ) {
        SQL sql = new SQL();
    }
}



// Connection method ////////
// Create table method //////////
// Read information from file method and store in database /////////
// Take query and get data from database method ////////


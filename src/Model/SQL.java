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
        String serverTable = "CREATE TABLE IF NOT EXISTS server (\n"
                + " id integer PRIMARY KEY,\n"
                + " entryDate text NOT NULL,\n"
                + " exitDate text,\n"
                + " pagesViewed integer NOT NULL,\n"
                + " conversion text NOT NULL\n"
                + ");";

        String impressionsTable = "CREATE TABLE IF NOT EXISTS impressions (\n"
                + " id integer PRIMARY KEY,\n"
                + " date text NOT NULL,\n"
                + " gender text NOT NULL,\n"
                + " age integer NOT NULL,\n"
                + " income text NOT NULL,\n"
                + " context text NOT NULL,\n"
                + " cost real NOT NULL\n"
                + ");";

        String clickTable = "CREATE TABLE IF NOT EXISTS click (\n"
                + " id integer PRIMARY KEY,\n"
                + " date text NOT NULL,\n"
                + " cost real NOT NULL\n"
                + ");";

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

            //while ((line = br.readLine()) != null) {
            for (int i = 0; i < 100; i++) {

                // use comma as separator
                line = br.readLine();
                String[] data = line.split(cvsSplitBy);

                String insertLine = "";
                //System.out.println("INSERT INTO click VALUES (" + data[1] + "," + data[0] + "," + data[2] + ");");
                if (table == "click" && data[0] != "Date"){
                    insertLine = "INSERT INTO click VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\");";
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

    public static void main( String args[] ) {
        SQL sql = new SQL();
    }
}



// Connection method ////////
// Create table method //////////
// Read information from file method and store in database
// Take query and get data from database method


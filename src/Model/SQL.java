package Model;

import java.io.File;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;

public class SQL {
    private Connection c;
    private Statement stmt;

    public void connection(String databaseName){
        this.c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+ databaseName + ".db");
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
                + " context text,\n"
                + " cost real,\n"
                + " PRIMARY KEY (id,date));";

        String personTable = "CREATE TABLE person (\n"
                + " id integer,\n"
                + " gender text,\n"
                + " ageRange text,\n"
                + " income text,\n"
                + " PRIMARY KEY (id));";

        String clickTable = "CREATE TABLE click (\n"
                + " id integer,\n"
                + " date text,\n"
                + " cost real,\n"
                + " PRIMARY KEY (id,date));";

        try{
            this.stmt = this.c.createStatement();
            stmt.execute("DROP TABLE IF EXISTS server;");
            stmt.execute("DROP TABLE IF EXISTS click;");
            stmt.execute("DROP TABLE IF EXISTS impressions;");
            stmt.execute("DROP TABLE IF EXISTS person;");
            stmt.execute(serverTable);
            stmt.execute(impressionsTable);
            stmt.execute(personTable);
            stmt.execute(clickTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void putData(String file, String table) throws Exception {

        String line;
        String cvsSplitBy = ",";
        c.setAutoCommit(false);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int x = 1;
            line = br.readLine();

            if(table.equals("click") && line.equals("Date,ID,Click Cost")){
                while ((line = br.readLine()) != null) {
                    x+=1;
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);

                    if(data.length != 3){
                        throw new Exception("Values missing on line " + x + ", \"" + line + "\"");
                    }else{
                        stmt.addBatch("INSERT INTO click VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\");");
                    }

                }
            } else if(table.equals("impressions") && line.equals("Date,ID,Gender,Age,Income,Context,Impression Cost")){
                while ((line = br.readLine()) != null) {
                    x += 1;
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);

                    if (data.length != 7) {
                        throw new Exception("Values missing on line " + x + ", \"" + line + "\"");
                    } else {
                        stmt.addBatch("INSERT INTO impressions VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[5] + "\",\"" + data[6] + "\");");
                        stmt.addBatch("INSERT OR IGNORE INTO person VALUES (" + data[1] + ",\"" + data[2] + "\",\"" + data[3] + "\",\"" + data[4] + "\");");
                    }
                }
            } else if(table.equals("server") && line.equals("Entry Date,ID,Exit Date,Pages Viewed,Conversion")){
                while ((line = br.readLine()) != null) {
                    x += 1;
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);


                    if (data.length != 5) {
                        throw new Exception("Values missing on line " + x + ", \"" + line + "\"");
                    } else {
                        stmt.addBatch("INSERT INTO server VALUES (" + data[1] + ",\"" + data[0] + "\",\"" + data[2] + "\",\"" + data[3] + "\",\"" + data[4] + "\");");
                    }
                }
            } else {
                throw new Exception("Incorrect file format");
            }
        } catch (IOException e) {
            throw new Exception("No file found at given path");
        }
        try{
            stmt.executeBatch();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public ResultSet getData(String query){
        ResultSet rs = null;
        try{
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rs;
    }

    public static void main( String args[] ) {
        SQL sql = new SQL();
    }
}
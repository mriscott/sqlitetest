package com.sdfeu.yane.sqllitetest;
import java.sql.*;
import java.io.*;
import javax.json.*;
import javax.json.stream.*;

public class SQLiteTest 
{
    static boolean DEBUG=false;
    Connection connection;

    // could be overriden if we wanted a GUI
    public void display(String str){
	    System.out.println(str);
    }
    
    public static void main(String [] args){
	SQLiteTest me = new SQLiteTest();
	try{
		me.setup();
		me.loadjson();
		if(args.length==0){
			me.read("");
			me.cleanup();
			System.exit(0);	
		}
		if(args[0].equalsIgnoreCase("SEARCH") && args.length==2){
			me.read(args[1]);
			me.cleanup();
			System.exit(0);	
		}
		if(args[0].equalsIgnoreCase("ADD") && args.length==3){
			try{
				me.addMovie(args[1],args[2]);
			}catch(NumberFormatException e){
				System.out.println("Not a valid year:"+args[2]);
			}
			me.cleanup();
			System.exit(0);	
		}
		if(args[0].equalsIgnoreCase("DEL") && args.length==2){
			me.deleteMovie(args[1]);
			me.cleanup();
			System.exit(0);	
		}
		System.out.println("Bad args");
		System.out.println("Usage:  ");
		System.out.println(" - No args         ...  list all films  ");
		System.out.println(" - SEARCH xxx      ...  list films matching xxx ");
		System.out.println(" - ADD title year  ...  add film ");
		System.out.println(" - DEL xxx         ...  delete films matching xxx ");
	} catch (SQLException e) {
	    e.printStackTrace();
            System.out.println(e.getMessage());  
	}
	finally{
	    try{
		me.cleanup();
	    } catch (SQLException e) {  
		System.out.println(e.getMessage());  
	    }

	}
    }

    // delete movis where title amtches string
    public void deleteMovie(String search) throws SQLException{
	runSql("delete from MOVIES where title like '%"+search+"%';");
    }
    // displays movis where title amtches string
    public void read(String search) throws SQLException{
	ResultSet rs=runQuery("select distinct * from MOVIES where title like '%"+search+"%' order by year;");
	while(rs.next()){
            //Display values
            System.out.println((rs.getString("Title")+ " ("+rs.getInt("Year")+")"));
         }
	rs.close();
    }

    public void setup() throws SQLException{
	createTables();
	addData();
    }

    public void loadjson(){
	try{
	    InputStream is = new FileInputStream("example.json");
	    JsonParser parser = Json.createParser(is);
	    String title=null;
	    while (parser.hasNext()) {
		JsonParser.Event e = parser.next();
		if (e == JsonParser.Event.KEY_NAME) {
		    switch (parser.getString()) {
		    case "title":
			parser.next();
			title=parser.getString();
			System.out.println("Title:"+title);
			break;
		    case "year":
			parser.next();
			String year=parser.getString();
			System.out.println("Adding "+title+" ("+year+")");
			addMovie(title,year);
			break;
		    }
		}
	    }
	    is.close();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }


   
   // close connection at end - not necessary if program ends cleanly
   public  void cleanup() throws SQLException{
	if (connection!=null) connection.close();
	connection=null;
	debug("Database closed");
    }

    
    void createTables() throws SQLException{        
        // SQL statement for creating a new table
	String sql = "CREATE TABLE IF NOT EXISTS movies (\n"
                + "	title varchar2(50) NOT NULL,\n"
                + "	year integer\n"
                + ");";
	runSql(sql);
	debug("Tables created");
    }

    int runCountQuery(String query) throws SQLException{
	ResultSet rs=runQuery(query);
	rs.next();
	int count = rs.getInt(1);
        rs.close();
        return count;
    }

    // add a movie
    void addMovie(String title, int year) throws SQLException{
	int count=runCountQuery("select count(*) from MOVIES where title = '"+title+"' and year="+year+";");
	if (count==1){
		// already there
		return;
	}
	if (count >1) {
		// duplicates - clear them down
		runSql("delete from MOVIES where title = '"+title+"' and year="+year+";");
	}
	runSql("Insert into movies values('"+title+"',"+year+");");
    }

    // takes two strings for ease
    void addMovie(String title, String year) throws SQLException,NumberFormatException{
	   addMovie(title,Integer.parseInt(year)); 
    }

    // so the DB doesn't start empty
    void addData() throws SQLException{
            // first check movie isn't already populated
	    int count=runCountQuery("select count(*) from MOVIES;");
            if (count!=0) return; 
	    addMovie("Citizen Kane",1941);
	    addMovie("The Matrix",1999);
	    addMovie("Bee Movie",2007);
	    addMovie("The Dark Knight",2008);
	    addMovie("The Godfather",1972);
    }

    
    // Runs an SQL Command not expecting output i.e. an update/delete
    void runSql(String sql) throws SQLException
    {
	Statement stmt = getConn().createStatement();
	// create a new table
	debug("Update:"+sql);
	stmt.execute(sql);
	String updates="Update count:"+stmt.getUpdateCount();
	debug(updates);
    }

    // Runs an SQL Command expecting output (i.e. a select)
    ResultSet runQuery(String sql) throws SQLException
    {
	Statement stmt = getConn().createStatement();
	// create a new table
	debug("Query:"+sql);
	return stmt.executeQuery(sql);
    }


    Connection getConn() throws SQLException{  
	if (connection!=null) return connection;
	// db parameters  
	String url = "jdbc:sqlite:movies.db";  
	// create a connection to the database  
	connection = DriverManager.getConnection(url);  
        
	debug("Connection to SQLite has been established.");  

	return connection;
              
    }

    void debug(String str){
	if (DEBUG) System.out.println(str);
    }

}

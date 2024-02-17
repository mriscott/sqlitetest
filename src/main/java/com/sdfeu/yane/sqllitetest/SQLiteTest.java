package com.sdfeu.yane.sqllitetest;
import java.sql.*;

public class SQLiteTest 
{
    static boolean DEBUG=false;
    Connection connection;

    public void display(String str){
	    System.out.println(str);
    }
    
    public static void main(String [] args){
	SQLiteTest me = new SQLiteTest();
	try{
		if(args.length==0){
			me.setup();
			me.read("");
			me.cleanup();
			System.exit(0);	
		}
		if(args[0].equals("SEARCH") && args.length==2){
			me.read(args[1]);
			me.cleanup();
			System.exit(0);	
		}
		if(args[0].equals("ADD") && args.length==3){
			try{
				me.addMovie(args[1],args[2]);
			}catch(NumberFormatException e){
				System.out.println("Not a valid year:"+args[2]);
			}
			me.cleanup();
			System.exit(0);	
		}
		System.out.println("Bad args");
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

    void addMovie(String title, int year) throws SQLException{
	ResultSet rs=runQuery("select count(*) from MOVIES where title = '"+title+"' and year="+year+";");
	rs.next();
	int count = rs.getInt(1);
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

    void addMovie(String title, String year) throws SQLException,NumberFormatException{
	   addMovie(title,Integer.parseInt(year)); 
    }

    void addData() throws SQLException{
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

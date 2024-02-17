package com.sdfeu.yane.sqllitetest;


import org.junit.*;
import java.sql.*;

public class TestSqliteTest {

    private SQLiteTest me; 

    @Before
    public void setUp() throws Exception {
        me = new SQLiteTest ();
	me.setup();
    }

    @Test
    // test that tests work
    public void testMaths() {
        Assert.assertEquals("2x2 should be 4", 2*2, 4);
    }

    
    @Test
    // test setup  
    public void testSetup() throws Exception {
       int c=count("SELECT count (*) from movies");
       Assert.assertEquals("Should be 5 movies",c,5);
    }

    @Test
    // test add  
    public void testAdd() throws Exception {
       int c=count("SELECT count (*) from movies");
       me.addMovie("Star Wars",1978);
       int c2=count("SELECT count (*) from movies");
       Assert.assertEquals("Should have one more movie",c+1,c2);
   } 
    private int count(String query){
       int count =-1;
       try{
           ResultSet rs = me.runQuery("SELECT count (*) from movies");
           rs.next();
           count=rs.getInt(1);
           rs.close();
       }catch(SQLException e) {e.printStackTrace();}
       return count;
    }

    @After
    public void tearDown() throws Exception
    {  try{
       me.runSql("delete from movies");
       me.cleanup();
       } finally{
       new java.io.File("movies.db").delete();
       }
    }
	

}

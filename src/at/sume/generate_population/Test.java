package at.sume.generate_population;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
 
class Test  {
    String url;
    Connection con;
    Statement stmt;
    public static void main(String args[]) {
        Test pc = new Test();
    }
    
    public Test(){
        url = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=E:\\Dokumente\\ABM Daten.mdb;";
        //url = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=D:\\database\\A.mdb;";
        String query = "select sgtId, Stadtgebietstyp from MA18_Stadtgebietstypen";
        
        
        System.out.println("Start of Program\t"+now("ss.SSS"));
        
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch(java.lang.ClassNotFoundException e) {
            System.err.println("Treiber-Klasse " + e + " konnte nicht geladen werden!");
            System.err.println(e.getMessage());
            System.exit(0);
        }
        System.out.println("After Class\t"+now("ss.SSS"));
        try {
            con = DriverManager.getConnection(url);
            
            System.out.println("After Connection\t"+now("ss.SSS"));
            
            stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("After Execute Query\t"+now("ss.SSS"));
            
            while (rs.next()) {
                rs.getString("Stadtgebietstyp");
            }
            
            System.out.println("After check the ResultSet\t"+now("ss.SSS"));
            
            stmt.close();
            con.close();
        } catch(SQLException ex) {
            System.err.println("Datenbank Verbindungsfehler!\n" + ex);
            System.exit(0);
        }
        System.out.println("Close Connection\t"+now("ss.SSS"));
    }
    private String now(String Format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        return sdf.format(cal.getTime());
    }
}

// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import java.util.ListIterator;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.GeoHash;

@WebServlet("/crimeStat")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class HTMLServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();

        // Print an HTML page as the output of the query
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en' dir='ltr'>");
        out.println("<head>");
        out.println("<title>Database Project</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<link rel='stylesheet' href='css/layout.css' type='text/css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='wrapper row1'>");
        out.println("<header id='header' class='clear'>");
        out.println("<div id='hgroup'>");
        out.println("<h1><a href='#'>Crime Prevention in Baltimore</a></h1>");
        out.println("<h2>By Steven Tan and William Li</h2>");
        out.println("</div>");
        out.println("</header>");
        out.println("</div>");
        out.println("<div class='wrapper row2'>");
        out.println("<div id='container' class='clear'>");
        //this is the main image displayed 
        out.println("<section id='slider'><a href='#'><img src='images/demo/960x360.gif' alt=''></a></section>");
        
        //this is one section, should be embedded with mysql queries
        out.println("<div id='homepage'>");

        out.println("<section id='services' class='clear'>");
        out.println("<article class='one_quarter'>");
        out.println("<strong>Historical Results Associated With Your Geo Location</strong>");


        //start the query
        //parameter for query initialization
        double longitude = Double.parseDouble(request.getParameter("long"));
        double latitude = Double.parseDouble(request.getParameter("lat"));
        int r = Integer.parseInt(request.getParameter("radius"));
        String firstLocation ="";
        String secondLocation ="";
        String thirdLocation ="";
      

        try (
            // Step 1: Allocate a database 'Connection' object
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "JDBC_USERNAME", "JDBC_PASSWORD");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
             
        ) {
            //compute our geohash from user input
            String geohash = getGeohash(longitude,latitude);
            List<String> geohash_list = geohashCircleSearch(longitude,latitude,r);
            ListIterator<String> iterator = geohash_list.listIterator(); 

            String sql = "";
            String hash = iterator.next();
            sql += "SELECT CrimeDate, CrimeTime, Location FROM Crime_In CI, Crime C WHERE CI.Geohash like " + "'" + hash
                +"%"+ "'" + " AND CI.CID = C.CID\n";
            ArrayList<String> used_list = new ArrayList<>();
            used_list.add(hash);

            while (iterator.hasNext()) {
                // Show all historical results associated with that geohash
                hash = iterator.next();
                if (used_list.contains(hash)) {
                    continue;
                }
                sql += "UNION \nSELECT CrimeDate, CrimeTime, Location FROM Crime_In CI, Crime C WHERE CI.Geohash like " + "'" + hash
                    +"%"+ "'" + " AND CI.CID = C.CID\n";
                used_list.add(hash);
            }
               
            //out.println("<p>Your SQL statement is: " + sql + "</p>"); // Echo for debugging
                
            ResultSet rset = stmt.executeQuery(sql);  // Send the query to the server
            // Step 4: Process the query result set
            int count = 0;
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>");
            out.println("Crime Date");
            out.println("</th>");
            out.println("<th>");
            out.println("Crime Time");
            out.println("</th>");
            out.println("<th>");
            out.println("Crime Location");
            out.println("</th>");
            out.println("</tr>");

            
            while(rset.next()) {
                firstLocation = rset.getString("Location");
                out.println("<tr>");
                out.println("<td>" + rset.getString("CrimeDate") +"</td>\n"
                    +"<td>" + rset.getString("CrimeTime") +"</td>\n"
                    + "<td>" + rset.getString("Location") + "</td>");
                out.println("</tr>");
                count++;
            }
            out.println("</table>");
            out.println("<p>==== " + count + " historical crime records found =====</p>");
            out.println("</article>");


        } catch (Exception e) {
          out.println(e);
        }

        try (
            // Step 1: Allocate a database 'Connection' object
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "JDBC_USERNAME", "JDBC_PASSWORD");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
        ) {

            //start of second query 
            String sql2 = "SELECT L.District, Count(C.CID) AS COUNT "+
            "FROM Location L, Crime_in CI, Crime C "+
            "WHERE L.Neighborhood <> '' AND L.Neighborhood IS NOT NULL AND L.geohash = CI.geohash AND CI.CID = C.CID "+
            "Group By L.District "+
            "Order By COUNT DESC LIMIT 1";

            ResultSet rset2 = stmt.executeQuery(sql2);
            rset2.next();
            secondLocation = rset2.getString("District");
            out.println("<article class='one_quarter'>");

            out.println("<strong>Most Dangerous District In Your Area</strong>");
            out.println("<p> The most danagerous district is: " + secondLocation + " with " + rset2.getString("COUNT")+ " incidents" + "</p>");
            out.println("</article>");
                    

        } catch (Exception e) {
            out.println(e);
        }

        try (
            // Step 1: Allocate a database 'Connection' object
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "JDBC_USERNAME", "JDBC_PASSWORD");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
        ) {
            //start of the third query
            String sql3 = "SELECT L.Neighborhood, Count(C.CID) AS COUNT "+
            "FROM Location L, Crime_in CI, Crime C "+
            "WHERE L.Neighborhood <> '' AND L.Neighborhood IS NOT NULL AND L.geohash = CI.geohash AND CI.CID = C.CID "+
            "Group By L.Neighborhood "+
            "Order By COUNT DESC LIMIT 1";

            ResultSet rset3 = stmt.executeQuery(sql3);
            rset3.next();
            thirdLocation = rset3.getString("neighborhood");
            out.println("<article class='one_quarter lastbox'>");
            out.println("<strong>Most Dangerou Neighborhood In Your Geo Location</strong>");
            out.println("<p> The most danagerous neighborhood is: " + thirdLocation+ " with " + rset3.getString("COUNT")+ " incidents" + "</p>");
            out.println("</article>");
        } catch (Exception e) {
            out.println(e);
        }


        try (
            // Step 1: Allocate a database 'Connection' object
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "JDBC_USERNAME", "JDBC_PASSWORD");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
        ) {
            String sql4 = "SELECT BI.BID, Count(CI.CID) AS COUNT"
                      +" FROM Crime_in CI, Building_In BI"
                      +" WHERE CI.geohash like CONCAT(LEFT(BI.geohash, 7),'%')"
                      +" Group By BI.BID"
                      +" Order By COUNT DESC LIMIT 1";

            ResultSet rset4 = stmt.executeQuery(sql4);
            rset4.next();
            out.println("<article class='one_quarter lastbox'>");
            out.println("<strong>Most Dangerou Neighborhood In Your Geo Location</strong>");
            out.println("<p> The most danagerous vacant building is: " + rset4.getString("BID") + " with " + rset4.getString("COUNT")+ " incidents" + "</p>");
            out.println("</article>");
        } catch (Exception e) {
            out.println(e);
        }
            out.println("</section>");

            //this is the section for map api
            out.println("<section id='latest'>");
            out.println("<iframe class='one_quarter' src='https://maps.google.com/maps?q=" +firstLocation+ "&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
            + "frameborder='0'style='border:0' allowfullscreen></iframe>");
            out.println("<iframe class='one_quarter' src='https://maps.google.com/maps?q=" +secondLocation+ "Baltimore" + "&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
            + "frameborder='0'style='border:0' allowfullscreen></iframe>");
            out.println("<iframe class='one_quarter lastbox' src='https://maps.google.com/maps?q=" +thirdLocation+ "Baltimore" + "&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
            + "frameborder='0'style='border:0' allowfullscreen></iframe>");
            out.println("</section>");
            out.println("</div>");
    
            out.println("</body>");
            out.println("</html>");
      }

      // returns a geohash string from lon lat and character precision
      public String getGeohash(double lon, double lat) throws Exception {
         String gh =  GeoHash.geoHashStringWithCharacterPrecision(lat, lon, 12);
         return gh;
      }
  
      // Radius is in METERS
      public List<String> geohashCircleSearch(double lon, double lat, int radius) throws Exception {
         WGS84Point center = new WGS84Point(lat, lon);
         GeoHashCircleQuery query = new GeoHashCircleQuery(center, radius);
         List<GeoHash> gh_list = query.getSearchHashes();
         List<String> gh_string_list = new ArrayList<String>();
         ListIterator<GeoHash> iterator = gh_list.listIterator(); 
         while(iterator.hasNext()){
            String temp = iterator.next().toBinaryString();
            temp = temp.substring(0,temp.length() - temp.length()%5);
            gh_string_list.add(GeoHash.fromBinaryString(temp).toBase32());
         }
         // gh_string_list.add("none");
         return gh_string_list;
      }
}

















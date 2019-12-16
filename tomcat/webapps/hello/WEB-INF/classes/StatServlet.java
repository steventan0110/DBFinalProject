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

@WebServlet("/crimeStats")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class StatServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();

      // Print an HTML page as the output of the query
      out.println("<html>");
      out.println("<head><title>Query Response</title></head>");
      out.println("<body>");

      double longitude = Double.parseDouble(request.getParameter("long"));
      double latitude = Double.parseDouble(request.getParameter("lat"));
      int r = Integer.parseInt(request.getParameter("radius"));
      //first we need to compute GEOHASH based on the user input longitude and latitude

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/DB_Final?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "root", "lihaojun10");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
         
         
      ) {
                //compute our geohash from user input
                String geohash = getGeohash(longitude,latitude);
                List<String> geohash_list = geohashCircleSearch(longitude,latitude,r);
                ListIterator<String> iterator = geohash_list.listIterator(); 


                  // Show all historical results associated with that geohash
               String sql= "SELECT CrimeDate, CrimeTime, Location FROM Crime_In CI, Crime C WHERE CI.Geohash like " + "'" +iterator.next()
               +"%"+ "'" + " AND CI.CID = C.CID;";
                  
      
               out.println("<p>Your SQL statement is: " + sql + "</p>"); // Echo for debugging
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
                  // Print a paragraph <p>...</p> for each record
                  out.println("<tr>");
                  out.println("<td>" + rset.getString("CrimeDate") +"</td>\n"
                      +"<td>" + rset.getString("CrimeTime") +"</td>\n"
                      + "<td>" + rset.getString("Location") + "</td>");
                  out.println("</tr>");
                  count++;
               }
               out.println("</table>");
               out.println("<p>==== " + count + " records found =====</p>");

      } catch (Exception e) {
         out.println(e);
      }
      
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





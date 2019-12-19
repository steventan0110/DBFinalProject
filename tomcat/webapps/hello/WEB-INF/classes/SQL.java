// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import ch.hsr.geohash.WGS84Point;

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

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "JDBC_USERNAME", "JDBC_PASSWORD");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
          double longitude = Double.parseDouble(request.getParameter("long"));
          double latitude = Double.parseDouble(request.getParameter("lat"));
          double r = Double.parseDouble(request.getParameter("radius"));

          //first we need to compute GEOHASH based on the user input longitude and latitude
          String geohash;





         // Show all historical results associated with that geohash
         String sql= "SELECT CrimeDate, CrimeTime, Location FROM Crime_In CI AND Crime C WHERE CI.Geohash = " + geohash 
            + "AND CI.CID = C.CID;";

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



          //second query, find the safeset hour in user's area
         String sql1 = "SELECT CrimeTime FROM Crime_In as CI, Crime as C WHERE CI.CID = C.CID"
         + "AND CI.Geohash = " + geohash + "Group by CrimeTime Order By Count(CrimeTime) Limit 1";
         
         ResultSet rset1 = stmt.executeQuery(sql1);
         out.println("<p> The saftest hour is: " + rset1.getString("CrimeTime") + "</p>");

       
          //third query: most dangerous district
          String sql2 = "SELECT L.District, L.Neighborhood, Count(C.CID) AS COUNT "+
          "FROM Location L, Crime_in CI, Crime C "+
          "WHERE L.Neighborhood <> '' AND L.Neighborhood IS NOT NULL AND L.geohash = CI.geohash AND CI.CID = C.CID "+
          "Group By L.District "+
          "Order By COUNT DESC LIMIT 1";

          ResultSet rset2 = stmt.executeQuery(sql2);
          out.println("<p> The most danagerous district is: " + rset2.getString("District") + " with " + rset2.getString("COUNT")+ " incidents" + "</p>");

          //fourth query: most dangerous neighborhood
          String sql3 = "SELECT L.District, L.Neighborhood, Count(C.CID) AS COUNT "+
          "FROM Location L, Crime_in CI, Crime C "+
          "WHERE L.Neighborhood <> '' AND L.Neighborhood IS NOT NULL AND L.geohash = CI.geohash AND CI.CID = C.CID "+
          "Group By L.Neighborhood "+
          "Order By COUNT DESC LIMIT 1";

          ResultSet rset3 = stmt.executeQuery(sql3);
          out.println("<p> The most danagerous neighborhood is: " + rset3.getString("neighborhood") + " with " + rset2.getString("COUNT")+ " incidents" + "</p>");

          //fifth query: most dangerous vacant building
          String sql4 = "SELECT B.Block, B.BuildingAddress, BI.COUNT"
                     +" FROM(SELECT BI.BID, Count(CI.CID) AS COUNT"
                     +" FROM Crime_in CI, Building_In BI"
                     +" WHERE CI.geohash like CONCAT(LEFT(BI.geohash, 7),'%')"
                     +" Group By BI.BID"
                     +" Order By COUNT DESC LIMIT 1) BI,"
                     +" Vacant_Building B"
                     +" WHERE B.BID = BI.BID";

         ResultSet rset4 = stmt.executeQuery(sql3);
         out.println("<p> The most danagerous vacant building is at Block " + rset3.getString("Block") + " Address" + rset3.getString("BuildingAddress") +" with " + rset2.getString("COUNT")+ " incidents" + "</p>");


      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }



}
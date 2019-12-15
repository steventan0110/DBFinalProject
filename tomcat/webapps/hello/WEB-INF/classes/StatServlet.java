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

      try {
         boolean result = testIssue3WithCircleQuery();
         out.println("RESULT="+result);
      } catch (Exception e) {
         out.println(e);
      }
      
    }

//       try (
//          // Step 1: Allocate a database 'Connection' object
//          Connection conn = DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/project?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
//                "root", "twt123456");   // For MySQL
//                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

//          // Step 2: Allocate a 'Statement' object in the Connection
//          Statement stmt = conn.createStatement();
//       ) {
//           //first we need to compute GEOHASH based on the user input longitude and latitude
//           String geohash;





//          // Show all historical results associated with that geohash
//          String sql1 = "SELECT CrimeDate, CrimeTime, Location FROM Crime_In CI AND Crime C WHERE CI.Geohash = " + geohash 
//             + "AND CI.CID = C.CID;";

//          out.println("<p>Your SQL statement is: " + sql1 + "</p>"); // Echo for debugging
//          ResultSet rset = stmt.executeQuery(sql1);  // Send the query to the server

//          // Step 4: Process the query result set
//          int count = 0;
//          out.println("<table>");
//          out.println("<tr>");
//          out.println("<th>");
//          out.println("Crime Date");
//          out.println("</th>");
//          out.println("<th>");
//          out.println("Crime Time");
//          out.println("</th>");
//          out.println("<th>");
//          out.println("Crime Location");
//          out.println("</th>");
//          out.println("</tr>");
//          while(rset.next()) {
//             // Print a paragraph <p>...</p> for each record
//             out.println("<tr>");
//             out.println("<td>" + rset.getString("CrimeDate") +"</td>\n"
//                 +"<td>" + rset.getString("CrimeTime") +"</td>\n"
//                 + "<td>" + rset.getString("Location") + "</td>");
//             out.println("</tr>");
//             count++;
//          }
//          out.println("</table>");
//          out.println("<p>==== " + count + " records found =====</p>");



//           //second query, find the safeset hour in user's area
//          String sql2 = "SELECT FROM Crime_In as CI, Crime as C WHERE CI.CID = C.CID"
//          + "AND CI.Geohash = " + geohash + "Group by CrimeTime Order By Count(CrimeTime) Limit 1";
         
//          ResultSet rset1 = stmt.executeQuery(sql1);
//          while (rset1.next()) {
             
//          }
//           //third query: most dangerous district

//           //fourth query: most dangerous neighborhood

//           //fifth query: most dangerous vacant building


        
//       } catch(Exception ex) {
//          out.println("<p>Error: " + ex.getMessage() + "</p>");
//          out.println("<p>Check Tomcat console for details.</p>");
//          ex.printStackTrace();
//       }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
//       out.println("</body></html>");
//       out.close();
//    }

   //TODO testing!!
	public boolean testIssue3WithCircleQuery() throws Exception {
		WGS84Point center = new WGS84Point(39.86391280373075, 116.37356590048701);
		GeoHashCircleQuery query = new GeoHashCircleQuery(center, 589);

		// the distance between center and test1 is about 430 meters
		WGS84Point test1 = new WGS84Point(39.8648866576058, 116.378465869303);
		// the distance between center and test2 is about 510 meters
		WGS84Point test2 = new WGS84Point(39.8664787092599, 116.378552856158);

      return (query.contains(test1) && query.contains(test2));
	}
}
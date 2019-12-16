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

@WebServlet("/test")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
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
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en' dir='ltr'>");
        out.println("<head>");
        out.println("<title>Database Project</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<link rel='stylesheet' href='css/layout.css' type='text/css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='wrapper row1'>");
        out.println('<header id="header" class="clear">');
        out.println('<div id="hgroup">');
        out.println('<h1><a href="#">Crime Prevention in Baltimore</a></h1>');
        out.println('<h2>By Steven Tan and William Li</h2>');
        out.println('</div>');
        out.println('</header>');
        out.println('</div>');
        out.println('<div class="wrapper row2">');
        out.println('<div id="container" class="clear">');
        //this is the main image displayed 
        out.println('<section id="slider"><a href="#"><img src="images/demo/960x360.gif" alt=""></a></section>');
        
        //this is one section, should be embedded with mysql queries
        out.println('<div id="homepage">');

        out.println('<section id="services" class="clear">');
        out.println('<article class="one_third">');
        out.println('<figure><img src="images/demo/32x32.gif" width="32" height="32" alt=""></figure>');
        out.println('<strong>Historical Results Associated With Your Geo Location</strong>');
        out.println('<p>xxx</p>');
        out.println('</article>');

        out.println('<section id="services" class="clear">');
        out.println('<article class="one_third">');
        out.println('<figure><img src="images/demo/32x32.gif" width="32" height="32" alt=""></figure>');
        out.println('<strong>Historical Results Associated With Your Geo Location</strong>');
        out.println('<p>xxx</p>');
        out.println('</article>');
                
        out.println('<section id="services" class="clear">');
        out.println('<article class="one_third lastbox">');
        out.println('<figure><img src="images/demo/32x32.gif" width="32" height="32" alt=""></figure>');
        out.println('<strong>Historical Results Associated With Your Geo Location</strong>');
        out.println('<p>xxx</p>');
        out.println('</article>');

        out.println('</section>');
        //this is the section for map api
        out.println('<section id="latest">');
        out.println('<iframe class="one_third" src="https://maps.google.com/maps?q=manhatan&t=&z=13&ie=UTF8&iwloc=&output=embed" frameborder="0"
    style="border:0" allowfullscreen></iframe>');
        out.println('</section>');
        out.println('</div>');

        out.println('</body>');
        out.println('</html>');

        // out.println('');
        // out.println('');
    }
}


















































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





}





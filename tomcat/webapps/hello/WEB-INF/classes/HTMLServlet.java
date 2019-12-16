// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import java.util.ListIterator;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;


@WebServlet("/test")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
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
        out.println("<article class='one_third'>");
        out.println("<figure><img src='images/demo/32x32.gif' width='32' height='32' alt=''></figure>");
        out.println("<strong>Historical Results Associated With Your Geo Location</strong>");
        out.println("<p>xxx</p>");
        out.println("</article>");

        out.println("<article class='one_third'>");
        out.println("<figure><img src='images/demo/32x32.gif' width='32' height='32' alt=''></figure>");
        out.println("<strong>Historical Results Associated With Your Geo Location</strong>");
        out.println("<p>xxx</p>");
        out.println("</article>");
                
        out.println("<article class='one_third lastbox'>");
        out.println("<figure><img src='images/demo/32x32.gif' width='32' height='32' alt=''></figure>");
        out.println("<strong>Historical Results Associated With Your Geo Location</strong>");
        out.println("<p>xxx</p>");
        out.println("</article>");

        out.println("</section>");
        //this is the section for map api
        out.println("<section id='latest'>");
        out.println("<iframe class='one_third' src='https://maps.google.com/maps?q=manhatan&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
        + "frameborder='0'style='border:0' allowfullscreen></iframe>");
        out.println("<iframe class='one_third' src='https://maps.google.com/maps?q=manhatan&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
        + "frameborder='0'style='border:0' allowfullscreen></iframe>");
        out.println("<iframe class='one_third last' src='https://maps.google.com/maps?q=manhatan&t=&z=13&ie=UTF8&iwloc=&output=embed'" 
        + "frameborder='0'style='border:0' allowfullscreen></iframe>");
        out.println("</section>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");

        // out.println(");
        // out.println(");
    }
}

















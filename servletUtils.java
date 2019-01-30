// JO 9-Jan-2019
// Utilities for servlets ... shared methods
package quizretakes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Jeff Offutt
 *         Date: January, 2019
 * Used by all servlets
 *
 */

public class servletUtils
{

/**
 * Print the header of the HTML pages
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
*/
static void printHeader (PrintWriter out) throws ServletException, IOException
{
   out.println ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   out.println ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
   out.println ("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
   out.println ("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />");
   out.println ("<head>");
   out.println ("  <title>Quiz retake scheduler</title>");
   out.println ("  <script type='text/javascript'>");
   out.println ("     function setFocusMain()");
   out.println ("     {");
   out.println ("       document.quizSchedule.studentName.focus();");
   out.println ("     }");
   out.println ("     function setFocusID()");
   out.println ("     {");
   out.println ("       document.getCourseID.courseID.focus();");
   out.println ("     }");
   out.println ("</script>");
   out.println ("</head>");
   out.println ("");
}

/**
 * Print the footer of HTML page
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
 */
static void printFooter (PrintWriter out) throws ServletException, IOException
{
   out.println ("<p style=\"font-size:80%;font-family:monospace; color:#888888\">");
   out.println ("Rasika Mohod &amp; Jeff Offutt");
   out.println ("<br/>January 2019");
   out.println ("</p>");
   out.println ("</body>");
   out.println ("</html>");
}

/**
 * Print a form to get the courseID
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
*/
static void printNeedCourseID (PrintWriter out, String thisServlet, String message) throws ServletException, IOException
{
   out.println ("<body onLoad=\"setFocusID()\" bgcolor=\"#DDEEDD\">");
   out.println ("");
   out.println ("<center><h2>GMU quiz retake scheduler</h2></center>");
   out.println ("<hr/>");
   out.println ("");

   // print the form
   out.println ("<form name='getCourseID' method='get' action='" + thisServlet + "' >");
out.print (message);
   out.print   ("  <p>Please enter the course ID given to you by your instructor. ");
   out.print   ("     It is probably the same as the university course ID, with no spaces. ");
   out.println ("  <br/>");

   out.print   ("  <p>courseID: ");
   out.println ("  <input type='text' id='courseID' name='courseID' size='15' />");
   out.println ("  <br/>");
   out.println ("  <br/>");
   out.println ("  <button id='submitRequest' type='submit' style='font-size:large'>Submit</button>");
   out.println ("</form>");
}
} // end servletUtils class

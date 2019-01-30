// JO 3-Jan-2019
package quizretakes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.time.*;
import java.lang.Long;
import java.lang.String;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Jeff Offutt
 *         Date: January, 2019
 *
 * Wiring the pieces together:
 *    quizschedule.java -- Servlet entry point for students to schedule quizzes
 *    quizReader.java -- reads XML file and stores in quizzes.
                             Used by quizschedule.java
 *    quizzes.java -- A list of quizzes from the XML file
 *                    Used by quizschedule.java
 *    quizBean.java -- A simple quiz bean
 *                      Used by quizzes.java and readQuizzesXML.java
 *    retakesReader.java -- reads XML file and stores in retakes.
                             Used by quizschedule.java
 *    retakes.java -- A list of retakes from the XML file
 *                    Used by quizschedule.java
 *    retakeBean.java -- A simple retake bean
 *                      Used by retakes.java and readRetakesXML.java
 *    apptBean.java -- A bean to hold appointments

 *    quizzes.xml -- Data file of when quizzes were given
 *    retakes.xml -- Data file of when retakes are given
 */

public class quizschedule extends HttpServlet
{
   // Data files
   // location maps to /webapps/offutt/WEB-INF/data/ from a terminal window.
   // These names show up in all servlets
   private static final String dataLocation    = "/var/www/CS/webapps/offutt/WEB-INF/data/";
   static private final String separator = ",";
   private static final String courseBase   = "course";
   private static final String quizzesBase = "quiz-orig";
   private static final String retakesBase = "quiz-retakes";
   private static final String apptsBase   = "quiz-appts";

   // Filenames to be built from above and the courseID parameter
   private String courseFileName;
   private String quizzesFileName;
   private String retakesFileName;
   private String apptsFileName;

   // Passed as parameter and stored in course.xml file (format: "swe437")
   private String courseID;
   // Stored in course.xml file, default 14
   // Number of days a retake is offered after the quiz is given
   private int daysAvailable = 14;

   // To be set by getRequestURL()
   private String thisServlet = "";


// doGet() : Prints the form to schedule a retake
@Override
protected void doGet (HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException
{
   response.setContentType ("text/html");
   PrintWriter out = response.getWriter ();
   servletUtils.printHeader (out);

   // Whoami? (Used in form)
   thisServlet = (request.getRequestURL()).toString();
   // CS server has a flaw--requires https & 8443, but puts http & 8080 on the requestURL
   thisServlet = thisServlet.replace("http", "https");
   thisServlet = thisServlet.replace("8080", "8443");

   // CourseID must be a parameter (also in course XML file, but we need to know which course XML file ...)
   courseID = request.getParameter("courseID");
   if (courseID != null && !courseID.isEmpty())
   {  // If not, ask for one.
      courseBean course;
      courseReader cr = new courseReader();
      courseFileName = dataLocation + courseBase + "-" + courseID + ".xml";
      try {
         course = cr.read(courseFileName);
      } catch (Exception e) {
         String message = "<p>Can't find the data files for course ID " + courseID + ". You can try again.";
         servletUtils.printNeedCourseID (out, thisServlet, message);
         servletUtils.printFooter (out);
         return;
      }
      daysAvailable = Integer.parseInt(course.getRetakeDuration());

      // Filenames to be built from above and the courseID
      String quizzesFileName = dataLocation + quizzesBase + "-" + courseID + ".xml";
      String retakesFileName = dataLocation + retakesBase + "-" + courseID + ".xml";
      String apptsFileName   = dataLocation + apptsBase   + "-" + courseID + ".txt";

      // Load the quizzes and the retake times from disk
      quizzes quizList    = new quizzes();
      retakes retakesList = new retakes();
      quizReader    qr = new quizReader();
      retakesReader rr = new retakesReader();

      try { // Read the files and print the form
         quizList    = qr.read (quizzesFileName);
         retakesList = rr.read (retakesFileName);
         printQuizScheduleForm (out, quizList, retakesList, course);
      } catch (Exception e)
      {
         String message = "<p>Can't find the data files for course ID " + courseID + ". You can try again.";
         servletUtils.printNeedCourseID (out, thisServlet, message);
      }
   }
   else
   {
      servletUtils.printNeedCourseID (out, thisServlet, "");
   }
   servletUtils.printFooter (out);
}

// doPost saves an appointment in a file and prints an acknowledgement
@Override
protected void doPost (HttpServletRequest request, HttpServletResponse response)
                      throws ServletException, IOException
{
   // No saving if IOException
   boolean IOerrFlag = false;
   String IOerrMessage = "";

   // Filename to be built from above and the courseID
   courseID = request.getParameter("courseID");
   String apptsFileName   = dataLocation + apptsBase + "-" + courseID + ".txt";

   // Get name and list of retake requests from parameters
   String studentName = request.getParameter ("studentName");
   String[] allIDs    = request.getParameterValues ("retakeReqs");

   response.setContentType ("text/html");
   PrintWriter out = response.getWriter ();
   servletUtils.printHeader (out);
   out.println ("<body bgcolor=\"#DDEEDD\">");

   if(allIDs != null && studentName != null && studentName.length() > 0)
   {
      // Append the new appointment to the file
      try {
         File file = new File(apptsFileName);
         synchronized(file)
         { // Only one student should touch this file at a time.
            if (!file.exists())
            {
               file.createNewFile();
            }
            FileWriter     fw = new FileWriter(file.getAbsoluteFile(), true); //append mode
            BufferedWriter bw = new BufferedWriter(fw);

            for(String oneIDPair : allIDs)
            {
               bw.write(oneIDPair + separator + studentName + "\n");
            }

            bw.flush();
            bw.close();
         } // end synchronize block
      } catch (IOException e) {
         IOerrFlag = true;
         IOerrMessage = "I failed and could not save your appointment." + e;
      }

      // Respond to the student
      if (IOerrFlag)
      {
         out.println ("<p>");
         out.println (IOerrMessage);
      } else {
         out.println ("<p>");
         if (allIDs.length == 1)
            out.println (studentName + ", your appointment has been scheduled.");
         else
            out.println (studentName + ", your appointments have been scheduled.");
         out.println ("<p>Please arrive in time to finish the quiz before the end of the retake period.");
         out.println ("<p>If you cannot make it, please cancel by sending email to your professor.");
      }

   } else { // allIDs == null or name is null
      out.println ("<body bgcolor=\"#DDEEDD\">");
      if(allIDs == null)
         out.println ("<p>You didn't choose any quizzes to retake.");
      if(studentName == null || studentName.length() == 0)
         out.println ("<p>You didn't give a name ... no anonymous quiz retakes.");

      thisServlet = (request.getRequestURL()).toString();
      // CS server has a flaw--requires https & 8443, but puts http & 8080 on the requestURL
      thisServlet = thisServlet.replace("http", "https");
      thisServlet = thisServlet.replace("8080", "8443");
      out.println("<p><a href='" + thisServlet + "?courseID=" + courseID + "'>You can try again if you like.</a>");
   }
   servletUtils.printFooter (out);
}

/**
 * Print the body of HTML
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
*/
private void printQuizScheduleForm (PrintWriter out, quizzes quizList, retakes retakesList, courseBean course)
        throws ServletException, IOException
{
   // Check for a week to skip
   boolean skip = false;
   LocalDate startSkip = course.getStartSkip();
   LocalDate endSkip   = course.getEndSkip();

   boolean retakePrinted = false;

   out.println ("<body onLoad=\"setFocusMain()\" bgcolor=\"#DDEEDD\">");
   out.println ("");
   out.println ("<center><h2>GMU quiz retake scheduler for class " + course.getCourseTitle() + "</h2></center>");
   out.println ("<hr/>");
   out.println ("");

   // print the main form
   out.println ("<form name='quizSchedule' method='post' action='" + thisServlet + "?courseID=" + courseID + "' >");
   out.print   ("  <p>You can sign up for quiz retakes within the next two weeks. ");
   out.print   ("Enter your name (as it appears on the class roster), ");
   out.println ("then select which date, time, and quiz you wish to retake from the following list.");
   out.println ("  <br/>");

   LocalDate today  = LocalDate.now();
   LocalDate endDay = today.plusDays(new Long(daysAvailable));
   LocalDate origEndDay = endDay;
   // if endDay is between startSkip and endSkip, add 7 to endDay
   if (!endDay.isBefore(startSkip) && !endDay.isAfter(endSkip))
   {  // endDay is in a skip week, add 7 to endDay
      endDay = endDay.plusDays(new Long(7));
      skip = true;
   }

   out.print   ("  <p>Today is ");
   out.println ((today.getDayOfWeek()) + ", " + today.getMonth() + " " + today.getDayOfMonth() );
   out.print   ("  <p>Currently scheduling quizzes for the next two weeks, until ");
   out.println ((endDay.getDayOfWeek()) + ", " + endDay.getMonth() + " " + endDay.getDayOfMonth() );
   out.println ("  <br/>");

   out.print   ("  <p>Name: ");
   out.println ("  <input type='text' id='studentName' name='studentName' size='50' />");
   out.println ("  <br/>");
   out.println ("  <br/>");

   out.println ("  <table border=1 style='background-color:#99dd99'><tr><td>"); // outer table for borders
   out.println ("  <tr><td>");
   for(retakeBean r: retakesList)
   {
      LocalDate retakeDay = r.getDate();
      if (!(retakeDay.isBefore (today)) && !(retakeDay.isAfter (endDay)))
      {
         // if skip && retakeDay is after the skip week, print a white bg message
         if (skip && retakeDay.isAfter(origEndDay))
         {  // A "skip" week such as spring break.
            out.println ("    <table border=1 width=100% style='background-color:white'>"); // inner table to format skip week
            out.println ("      <tr><td>Skipping a week, no quiz or retakes.");
            out.println ("    </table>"); // inner table for skip week
            // Just print for the FIRST retake day after the skip week
            skip = false;
         }
         retakePrinted = true;
         out.println ("    <table width=100%>"); // inner table to format one retake
         // format: Friday, January 12, at 10:00am in EB 4430
         out.println ("    <tr><td>" + retakeDay.getDayOfWeek() + ", " +
                      retakeDay.getMonth() + " " +
                      retakeDay.getDayOfMonth() + ", at " +
                      r.timeAsString() + " in " +
                      r.getLocation());

         for(quizBean q: quizList)
         {
            LocalDate quizDay = q.getDate();
            LocalDate lastAvailableDay = quizDay.plusDays(new Long(daysAvailable));
            // To retake a quiz on a given retake day, the retake day must be within two ranges:
            // quizDay <= retakeDay <= lastAvailableDay --> (!quizDay > retakeDay) && !(retakeDay > lastAvailableDay)
            // today <= retakeDay <= endDay --> !(today > retakeDay) && !(retakeDay > endDay)

            if (!quizDay.isAfter(retakeDay) && !retakeDay.isAfter(lastAvailableDay) &&
                !today.isAfter(retakeDay) && !retakeDay.isAfter(endDay))
            {
               out.println ("    <tr><td align='right'><label for='q" + q.getID() + "r" + r.getID() + "'>Quiz " + q.getID() + " from " + quizDay.getDayOfWeek() + ", " + quizDay.getMonth() + " " + quizDay.getDayOfMonth() + ":</label> ");
               // Value is "retakeID:quiziD"
               out.println ("    <td><input type='checkbox' name='retakeReqs'  value='" + r.getID() + separator + q.getID() + "' id='q" + q.getID() + "r" + r.getID() + "'>");
            }
         }
      }
      if (retakePrinted)
      {
         out.println ("  </table>");
         out.println ("  <tr><td>");
         retakePrinted = false;
      }
   }
   out.println ("  <tr><td align='middle'><button id='submitRequest' type='submit' name='submitRequest' style='font-size:large'>Submit request</button>");
   out.println ("  </table>");
   out.println ("</form>");


   out.println ("<br/>");
   out.println ("<br/>");
   out.println ("<br/>");
   out.println ("<br/>");
   out.println ("<table border=1>");
   out.println ("<tr><td align='middle'>All quiz retake opportunities</td></tr>");
   for(retakeBean r: retakesList)
   {
      out.print   ("  <tr><td>");
      out.print   (r);
      out.println ("  </td></td>");
   }
   out.println ("</table>");
}

} // end quizschedule class

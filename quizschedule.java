// JO, Jan 2019
package quizretakes;

// A command line interface (CLI) for the quizschedule servlet
// SWE 437, Assignment 2, Spring 2019
// This class is based on the quizschedule.java servlet
// Removed all the servlet stuff
// New code has the comment /* CLI */

import java.util.Scanner; /* CLI */

//=============================================================================

import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;
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

import java.util.Properties; /* CLI */

/**
 * @author Jeff Offutt
 *         Date: January, 2019
 *
 * Wiring the pieces together:
 *    quizschedule.java -- Servlet entry point for students to schedule quizzes
 *    quizReader.java -- reads XML file and stores in quizzes.
 *                       Used by quizschedule.java
 *    quizzes.java -- A list of quizzes from the XML file
 *                    Used by quizschedule.java
 *    quizBean.java -- A simple quiz bean
 *                     Used by quizzes.java and readQuizzesXML.java
 *    retakesReader.java -- reads XML file and stores in retakes.
 *                          Used by quizschedule.java
 *    retakes.java -- A list of retakes from the XML file
 *                    Used by quizschedule.java
 *    retakeBean.java -- A simple retake bean
 *                       Used by retakes.java and readRetakesXML.java
 *    apptBean.java -- A bean to hold appointments
 *
 *    quizzes.xml -- Data file of when quizzes were given
 *    retakes.xml -- Data file of when retakes are given
 *    course.xml -- Data file with information about the course
 */

public class quizsched
{
   // Used to pass retakeID,quizID pairs from printQuizScheduleForm() to readInputSave()
   private static Properties retakeQuizIDProps = new Properties(); /* CLI */

   // Data files
   /* CLI: All variables changed to static to use in main() */
   private static final String dataLocation = "quizretakes/"; /* CLI */
   private static final String separator    = ",";
   private static final String courseBase   = "course";
   private static final String quizzesBase  = "quiz-orig";
   private static final String retakesBase  = "quiz-retakes";
   private static final String apptsBase    = "quiz-appts";

   // Filenames to be built from above and the courseID
   private static String courseFileName;
   private static String quizzesFileName;
   private static String retakesFileName;
   private static String apptsFileName;

   // Stored in course.xml file, default 14
   // Number of days a retake is offered after the quiz is given
   private static int daysAvailable = 14;

// ===============================================================
// Prints the form to schedule a retake
public static void main(String []argv) /* CLI */
{
   Scanner sc = new Scanner(System.in); /* CLI */

   // Get course ID from user (could be passed as a command line parameter ...)
   String courseID = readCourseID(sc); /* CLI */
   buildFileNames(courseID); /* CLI */

   // Get information about the course
   courseBean course;
   try {
      course = readCourseFile(courseID);
   } catch(Exception e) {
      System.out.println("Can't find the data files for course ID " + courseID + ". You can try again with a different course ID.");
      return;
   }

   daysAvailable = Integer.parseInt(course.getRetakeDuration());

   try { // Read the files and print the form
      quizzes quizList; /* CLI */
      retakes retakesList; /* CLI */
      quizList    =  readQuizzes(courseID); /* CLI */
      retakesList = readRetakes(courseID); /* CLI */
      // Inside try-block so this won't print if files can't be read
      printQuizScheduleForm(quizList, retakesList, course);
   } catch(Exception e) {
      System.out.println("Can't read the data files for course ID " + courseID + ". You can try again with a different courseID.");
      return;
   }

   // This replaces the submit-response (was doPost() )
   readInputSave(sc, courseID); /* CLI */
}  // end main()

// ===============================================================
// Was doPost()
// Called from main to read student's choice and save to file
private static void readInputSave(Scanner sc, String courseID) /* CLI */
{
   // Get name and list of retake requests from user
   System.out.print("What is your name? ");
   String studentName = sc.next();

   System.out.print("Enter a number from the list to schedule a retake: "); /* CLI */
   String retake = sc.next(); /* CLI */
   String retakeQuizID = retakeQuizIDProps.getProperty(retake); /* CLI */

   // Append the new appointment to the file
   try {
      File file = new File(apptsFileName);
      if(retakeQuizID != null) /* CLI */
      {  // user must choose one of the numbers on screen
         if(!file.exists())
         {
            file.createNewFile();
         }
         FileWriter     fw = new FileWriter(file.getAbsoluteFile(), true); //append mode
         BufferedWriter bw = new BufferedWriter(fw);

         bw.write(retakeQuizID + separator + studentName + "\n");

         bw.flush();
         bw.close();

         // CLI: simplified the logic in this method somewhat from the servlet version.
         // Respond to the student
         System.out.println("");
         System.out.println(studentName + ", your appointment has been scheduled.");
         System.out.println("Please arrive in time to finish the quiz before the end of the retake period.");
         System.out.println("If you cannot make it, please cancel by sending email to your professor.");
      } else {
         System.out.println("");
         System.out.println("I don't have a retake time for that number. Please try again.");
      }
   } catch(IOException e) {
      System.out.println("");
      System.out.println("I failed and could not save your appointment.\nException message is: " + e);
   }
}

// ===============================================================
/* CLI: Dropped parameter "out", now we print to screen */
// Print the quiz retake choices (maybe should also change the method name?)
private static void printQuizScheduleForm(quizzes quizList, retakes retakesList, courseBean course)
{
   // Check for a week to skip
   boolean skip = false;
   LocalDate startSkip = course.getStartSkip();
   LocalDate endSkip   = course.getEndSkip();

   System.out.println("");
   System.out.println("");
   System.out.println("******************************************************************************");
   System.out.println("GMU quiz retake scheduler for class " + course.getCourseTitle());
   System.out.println("******************************************************************************");
   System.out.println("");
   System.out.println("");

   // print the main form
   System.out.println("You can sign up for quiz retakes within the next two weeks. ");
   System.out.println("Enter your name (as it appears on the class roster), ");
   System.out.println("then select which date, time, and quiz you wish to retake from the following list.");
   System.out.println("");

   LocalDate today  = LocalDate.now();
   LocalDate endDay = today.plusDays(new Long(daysAvailable));
   LocalDate origEndDay = endDay;
   // if endDay is between startSkip and endSkip, add 7 to endDay
   if(!endDay.isBefore(startSkip) && !endDay.isAfter(endSkip))
   {  // endDay is in a skip week, add 7 to endDay
      endDay = endDay.plusDays(new Long(7));
      skip = true;
   }

   System.out.print  ("Today is ");
   System.out.println((today.getDayOfWeek()) + ", " + today.getMonth() + " " + today.getDayOfMonth() );
   System.out.print  ("Currently scheduling quizzes for the next two weeks, until ");
   System.out.println((endDay.getDayOfWeek()) + ", " + endDay.getMonth() + " " + endDay.getDayOfMonth() );
   System.out.print("");

   // Unique integer for each retake and quiz pair
   int quizRetakeCount = 0; /* CLI */
   for(retakeBean r: retakesList)
   {
      LocalDate retakeDay = r.getDate();
      if(!(retakeDay.isBefore(today)) && !(retakeDay.isAfter(endDay)))
      {
         // if skip && retakeDay is after the skip week, print a message
         if(skip && retakeDay.isAfter(origEndDay))
         {  // A "skip" week such as spring break.
            System.out.println("      Skipping a week, no quiz or retakes.");
            // Just print for the FIRST retake day after the skip week
            skip = false;
         }
         // format: Friday, January 12, at 10:00am in EB 4430
         System.out.println("RETAKE: " + retakeDay.getDayOfWeek() + ", " +
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
            if(!quizDay.isAfter(retakeDay) && !retakeDay.isAfter(lastAvailableDay) &&
                !today.isAfter(retakeDay) && !retakeDay.isAfter(endDay))
            {
               quizRetakeCount++; /* CLI */
               // Put in a properties structure for writing to retake schedule file (CLI)
               retakeQuizIDProps.setProperty(String.valueOf(quizRetakeCount), r.getID() + separator + q.getID()); /* CLI */
               System.out.print  ("    " + quizRetakeCount + ") "); /* CLI */
               System.out.println("Quiz " + q.getID() + " from " + quizDay.getDayOfWeek() + ", " + quizDay.getMonth() + " " + quizDay.getDayOfMonth() );
            }
         }
      }
   }
   System.out.println("");
}

// ===============================================================
// Build the file names in one place to make them easier to change
private static void buildFileNames(String courseID) /* CLI */
{
   courseFileName  = dataLocation + courseBase  + "-" + courseID + ".xml"; /* CLI */
   quizzesFileName = dataLocation + quizzesBase + "-" + courseID + ".xml"; /* CLI */
   retakesFileName = dataLocation + retakesBase + "-" + courseID + ".xml"; /* CLI */
   apptsFileName   = dataLocation + apptsBase   + "-" + courseID + ".txt"; /* CLI */
}

// ===============================================================
// Get the course ID from the user
private static String readCourseID(Scanner sc) /* CLI */
{
   System.out.print("Enter courseID: "); /* CLI */
   return(sc.next()); /* CLI */
}

// ===============================================================
// Read the course file
private static courseBean readCourseFile(String courseID) throws Exception /* CLI */
{
   courseBean course; /* CLI */
   courseReader cr = new courseReader(); /* CLI */
   course          = cr.read(courseFileName);
   return(course); /* CLI */
}

// ===============================================================
// Read the quizzes file
private static quizzes readQuizzes(String courseID) throws Exception /* CLI */
{
   quizzes quizList = new quizzes(); /* CLI */
   quizReader qr    = new quizReader(); /* CLI */
   quizList         = qr.read(quizzesFileName); /* CLI */
   return(quizList); /* CLI */
}

// ===============================================================
// Read the retakes file
private static retakes readRetakes(String courseID) throws Exception /* CLI */
{
   retakes retakesList = new retakes();
   retakesReader rr    = new retakesReader();
   retakesList         = rr.read(retakesFileName);
   return(retakesList); /* CLI */
}

} // end quizschedule class

// JO 3-Jan-2019
package quizretakes;
import java.util.*;
import java.time.*;
import java.lang.Long;
import java.lang.String;

import java.io.*;

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

public class quizschedule
{
   // Data files
   // location maps to /webapps/offutt/WEB-INF/data/ from a terminal window.
   // These names show up in all servlets
   private static final String dataLocation = System.getProperty("user.dir");
   static private final String separator    = ",";
   private static final String courseBase   = "course";
   private static final String quizzesBase  = "quiz-orig";
   private static final String retakesBase  = "quiz-retakes";
   private static final String apptsBase    = "quiz-appts";

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

   //map between QuizId and RetakeID
   //retake is key, quiz is value
   private static HashMap <Integer, ArrayList<Integer>> map = new HashMap<Integer,ArrayList<Integer>>();


// replace doGet with main() because of CLI
public static void main (String args[])
{
   //starting prompts to get data
   System.out.println("GMU quiz retake scheduler");
   System.out.println("Please enter the course ID given to you by your instructor."
            + "It is probably the same as the university course ID, with no spaces."
            + "Please press enter when done.");

   Scanner scan = new Scanner(System.in);
   String courseID = scan.nextLine();

   if ( courseID.equalsIgnoreCase("swe437") ) //make sure accessing the right class
   {
      quizretakes.courseBean course;
      courseReader cr = new courseReader();
      courseFileName = dataLocation + courseBase + "-" + courseID + ".xml";
      try
      {
        course = cr.read(courseFileName);
      } catch (Exception e) {
        System.out.println(e);
        return;
      }
      daysAvailable = Integer.parseInt(course.getRetakeDuration());

      System.out.println("GMU quiz retake scheduler for class Software Testing");
      System.out.println("You can sign up for quiz retakes within the next two weeks."
              + "Enter your name (as it appears on the class roster, first and last name)."
               + "Press enter when done");
      String studentName = scan.nextLine();
      //TODO: need to store student name using write() method... why now? can't we just do that when adding the appt?

      //TODO:print out list of QUIZZES
      // Filenames to be built from above and the courseID
      String quizzesFileName = dataLocation + quizzesBase + "-" + courseID + ".xml";
      String retakesFileName = dataLocation + retakesBase + "-" + courseID + ".xml";

      // Load the quizzes and the retake times from disk
      quizzes quizList    = new quizzes();
      retakes retakesList = new retakes();
      quizReader    qr = new quizReader();
      retakesReader rr = new retakesReader();

      try
      { // Read the files and print the form
        quizList    = qr.read (quizzesFileName);
        retakesList = rr.read (retakesFileName);
        printList(quizList, retakesList, course);
      } catch (Exception e)
      {
        System.out.println(e);
        return;
      }

      System.out.println("Thank you, please select a Quiz Session from the options above."
            + "You can select which date and time you wish to schedule the retake from the following list."
              + "Type the corresponding session number and press enter.");
      int retakeID = scan.nextInt();
      System.out.println("Please select the quiz ID you want to retake, type the corresponding quiz number and press enter");
      int quizID = scan.nextInt();

      Integer retakeINT = retakeID;
      Integer quizINT = quizID;

      if (map.containsKey(retakeINT))
      {
         ArrayList<Integer> t = map.get(retakeID);
         t.add(quizINT);
         map.put(retakeINT, t);
      }
      else
      {
         ArrayList<Integer> i = new ArrayList<Integer>();
         i.add(quizINT);
         map.put(retakeINT, quizINT);
      }

      boolean ifSuccessful = write(studentName, retakeID, quizID, courseID);
      if ( ifSuccessful )
      {
         System.out.println(studentName + ": your appointment has been scheduled."
                 + "Please arrive in time to finish the quiz before the end of the retake period."
                 + "If you cannot make it, please cancel by sending email to your professor.");
      }
      else
      {
         System.out.println("Sorry, I could not schedule your appointment. You can try again");
         //TODO: start over
      }
   }
   else
   {
      System.out.println("Sorry! We do not currently offer Quiz Retakes for that courseID.");
      //TODO: add functionality here to retype courseID / start over
   }
}

// changes doPost() to method  write() to write information to the data file
public static boolean write ( String StudentName, int RetakeID, int QuizID)
{
   return false;
}

public static void printList (quizzes quizList, retakes retakesList, courseBean course)
{
  for(retakeBean r: retakesList)
  {
     System.out.println(r.toString());
        for(quizBean q: quizList)
        {
           System.out.println(q.toString());
        }
  }
}
   public static boolean write (String StudentName, int RetakeID , int QuizID, String courseID)
   {
      // No saving if IOException
      boolean IOerrFlag = false;
      String IOerrMessage = "";

      // Filename to be built from above and the courseID
      String apptsFileName   = dataLocation + apptsBase + "-" + courseID + ".txt"; //?

      // Get name and list of retake requests from parameters
      String studentName = StudentName;

      if(map != null && studentName != null && studentName.length() > 0)
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


               for ( HashMap.Entry<Integer, ArrayList<Integer>> entry: map.entrySet())
               {
                  Integer key = entry.getKey();
                  ArrayList<Integer> val = entry.getValue();
                  for ( Integer i : val)
                  {
                     bw.write( key+ separator + i + "\n");
                  }
               }

               bw.flush();
               bw.close();
            } // end synchronize block
         } catch (IOException e) {
            IOerrFlag = true;
            IOerrMessage = "I failed and could not save your appointment." + e;
            return false;
         }

         // Respond to the student
         if (IOerrFlag)
         {
            System.out.println (IOerrMessage);
         } else {
               System.out.println(studentName + ", your appointment(s) have been scheduled.");
            System.out.println("Please arrive in time to finish the quiz before the end of the retake period.");
            System.out.println("If you cannot make it, please cancel by sending email to your professor.");
         }

      } else { // allIDs == null or name is null
         if(map == null)
            System.out.println ("You didn't choose any quizzes to retake.");
         if(studentName == null || studentName.length() == 0)
            System.out.println ("You didn't give a name ... no anonymous quiz retakes.");

         System.out.println("courseID=" + courseID + ". You can try again if you like.");
         return false;
      }
      return true;
   }

} // end quizschedule class

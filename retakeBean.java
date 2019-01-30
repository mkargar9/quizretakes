package quizretakes;

import java.time.*;

/**
 * This bean holds information about a quiz retake session

 * @author Jeff Offutt
 */
/* *****************************************
<retakes>
  <retake>
    <id>1</id> <!-- Should be unique and in order -->
    <location>Inn 204</location> --String, building & room
    <whenOffered>
      <month>2</month> --01..12
      <day>7</day> --1..31
      <hour>10</hour> --0..23
      <minute>00</minute> --> 0-59
    </whenOffered>
  </retake>
  <retake>
    <id>2</id>
...
</retakes>
***************************************** */

public class retakeBean implements Comparable<retakeBean>
{
   private int retakeID;
   private String location;
   private LocalDate whenOffered;
   private LocalTime timeOffered;

   public retakeBean (int ID, String location, int month, int day, int hour, int minute)
   {
      retakeID      = ID;
      this.location = location;
      int year      = Year.now().getValue();
      whenOffered   = LocalDate.of (year, month, day);
      timeOffered   = LocalTime.of (hour, minute);
   }

   @Override
   public int compareTo (retakeBean quizR)
   {
      return this.retakeID - quizR.retakeID;
   }

   // *** Getters *** //
   public LocalDate getDate()
   {
      return whenOffered;
   }
   public String getLocation()
   {
      return location;
   }
   public int getID()
   {
      return retakeID;
   }
   public String toString()
   {
      return retakeID + ": " +
             location + ": " +
             whenOffered.toString() + ": " +
             whenOffered.getDayOfWeek() + ": " +
             timeOffered.toString();
   }

   // Date methods
   public Month getMonth()
   {
      return whenOffered.getMonth();
   }
   public int getMonthNum()
   {
      return whenOffered.getMonthValue();
   }
   public DayOfWeek getDayOfWeek()
   {
      return whenOffered.getDayOfWeek();
   }
   public String dateAsString ()
   {
      return whenOffered.toString();
   }

   // Time methods
   public String timeAsString ()
   {
      return timeOffered.toString();
   }

/*
   public String getQuizId() {
      return quizId;
   }
*/

}

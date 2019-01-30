//JO, 10-Jan-2019
// Stores course information in a bean
package quizretakes;

import java.time.*;

/**
 * This bean holds information about a course

 * @author Jeff Offutt
 */
/* *****************************************
<course>
  <courseID>swe437</courseID> <!-- Used in file names and displayed -->
  <courseTitle>Software testing</courseTitle> <!-- String to be displayed -->
  <retakeDuration>14</retakeDuration> <!-- int, number of days retake is possible -->
  <startSkipMonth>1</startSkipMonth>
  <startSkipDay>21</startSkipDay>
  <endSkipMonth>1</endSkipMonth>
  <endSkipDay>25</endSkipDay>
  <dataLocation>/var/www/CS/webapps/offutt/WEB-INF/data/</dataLocation>
</course>
***************************************** */

public class courseBean
{
   private String courseID;
   private String courseTitle;
   private String retakeDuration;
   private LocalDate startSkip;
   private LocalDate endSkip;
   private String dataLocation;

   // *** Constructor *** //
   public courseBean (String courseID, String courseTitle, String retakeDuration,
                      LocalDate startSkip, LocalDate endSkip, String dataLocation)
   {
      this.courseID       = courseID;
      this.courseTitle    = courseTitle;
      this.retakeDuration = retakeDuration;
      this.startSkip      = startSkip;
      this.endSkip        = endSkip;
      this.dataLocation   = dataLocation;
   }

   // *** Getters *** //
   public String getCourseID()
   {
      return courseID;
   }
   public String getCourseTitle()
   {
      return courseTitle;
   }
   public String getRetakeDuration()
   {
      return retakeDuration;
   }
   public LocalDate getStartSkip()
   {
      return startSkip;
   }
   public LocalDate getEndSkip()
   {
      return endSkip;
   }
   public String getDataLocation()
   {
      return dataLocation;
   }

   // *** Setters *** //
   public void setCourseID(String courseID)
   {
      this.courseID = courseID;
   }
   public void setCourseTitle(String courseTitle)
   {
      this.courseTitle = courseTitle;
   }
   public void setRetakeDuration(String retakeDuration)
   {
      this.retakeDuration = retakeDuration;
   }
   public void setStartSkip(LocalDate startSkip)
   {
      this.startSkip = startSkip;
   }
   public void setEndSkip(LocalDate endSkip)
   {
      this.endSkip = endSkip;
   }
   public void setDataLocation(String dataLocation)
   {
      this.dataLocation = dataLocation;
   }

}

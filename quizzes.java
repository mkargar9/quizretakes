package quizretakes;

// import java.io.Serializable; ?? Needed?
import java.util.*;

/**
 * This class holds a collection of quizzes

 * @author Jeff Offutt
 */

public class quizzes implements Iterable<quizBean>
{
   private ArrayList<quizBean> quizzes;

   // ***** Constructors //
   public quizzes ()
   {
      quizzes = new ArrayList<quizBean>();
   }

   public quizzes (int quizID, int month, int day, int hour, int minute)
   {  // Adds one quiz to a new list
      quizzes = new ArrayList<quizBean>();
      quizBean qb = new quizBean (quizID, month, day, hour, minute);
      quizzes.add (qb);
   }

   public quizzes (quizBean qb)
   {
      quizzes = new ArrayList<quizBean>();
      quizzes.add (qb);
   }

   // *** sorting and iterating *** //
   public void sort ()
   {
      Collections.sort (quizzes);
   }

   @Override
   public Iterator<quizBean> iterator()
   {
       return quizzes.iterator();
   }

   // ***** setters & getters //
   public void addQuiz (quizBean qb)
   {
      quizzes.add (qb);
   }

   public String toString ()
   {
      return (Arrays.toString(quizzes.toArray()));
   }

}

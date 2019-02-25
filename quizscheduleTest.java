package quizretakes;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class quizscheduleTest 
{
	quizzes quizList;
	retakes retakesList;
	LocalDate startSkip; //for courseBean course
	LocalDate endSkip; //for courseBean course
	courseBean course; 
	
	@Before
	public void setUp() throws Exception
	{
		//sets up data for testNullPointerException()
		startSkip = LocalDate.of(2019, 1, 21); 
		endSkip = LocalDate.of(2019, 1, 25);
		quizList = new quizzes();
		retakesList = new retakes();
		course = new courseBean("swe437", "Software Testing", "14", startSkip, endSkip, "/var/www/CS/webapps/offutt/WEB-INF/data/");

	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	/**
	 * This method tests NullPointerException being thrown if any or all of parameters to quizScheduleForm are null
	 */
	@Test (expected = NullPointerException.class)
	public void testNullPointerException()
	{
		//should return NullPointerException
		quizschedule.printQuizScheduleForm(null, null, null); //check if all 3 inputs are null
		
		quizschedule.printQuizScheduleForm(quizList, null, null); //should return NullPointerException
		
		quizschedule.printQuizScheduleForm(null, retakesList, null);  //should return NullPointerException

		quizschedule.printQuizScheduleForm(null, null, course); //should return NullPointerException
	}

	@Test
	public void testPrintQuizScheduleForm()
	{
		
	}

}

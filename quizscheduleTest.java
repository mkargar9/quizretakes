package quizretakes;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

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
		//resets all the data
		startSkip = null;
		endSkip = null;
		quizList = null;
		retakesList = null;
		course = null;
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

	/**
	 * This method shows how to retrieve the System.out content
	 */
	@Test
	public void testRetrievingOutput()
	{
		// Create a stream to hold the output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		// IMPORTANT: Save the old System.out!
		PrintStream old = System.out;
		// Tell Java to use your special stream
		System.setOut(ps);
		
		// Print some output: goes to your special stream
		System.out.print("Foofoofoo!"); //replace this line with calling printQuizScheduleForm
		
		// Put things back
		System.out.flush();
		System.setOut(old);
		// Show what happened (need to use the toString() method)
		assertEquals(baos.toString(), "Foofoofoo!");
		
		//another way to test a string for containing information
		assertThat(baos.toString(), both(endsWith("!")).and(containsString("Foo")));
	}

}

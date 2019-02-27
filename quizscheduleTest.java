package quizretakes;

import static org.junit.Assert.*;

import java.time.LocalDate;

import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.*;

public class quizscheduleTest 
{
	quizzes quizList;
	retakes retakesList;
	LocalDate startSkip; //for courseBean course
	LocalDate endSkip; //for courseBean course
	courseBean course; 
	LocalDate today  = LocalDate.now();
	LocalDate endDay = today.plusDays(new Long(14));
	String courseID = "swe437";
	   
	@Before
	public void setUp() throws Exception
	{
		//sets up data 
		startSkip = LocalDate.of(2019, 1, 21); 
		endSkip = LocalDate.of(2019, 1, 25);
		quizList = new quizzes();
		retakesList = new retakes();
		course = new courseBean(courseID, "Software Testing", "14", startSkip, endSkip, "/var/www/CS/webapps/offutt/WEB-INF/data/");
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
	
	/** 
	 * This method tests correct printing correct today date and end date 
	 */
	@Test
	public void testRetakeOutput()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		PrintStream old = System.out;
		System.setOut(ps);
		
		quizschedule.printQuizScheduleForm(quizList, retakesList, course);
		
		System.out.flush();
		System.setOut(old);
		
		String output = out.toString();
		
		//test prints out correct today date "Today is ...."
		assertThat(output, CoreMatchers.containsString(today.getDayOfWeek() + ", " + today.getMonth() + " " + today.getDayOfMonth()));
		
		//tests prints out correct end date "scheduling quizzes until ..."
		assertThat(output, CoreMatchers.containsString(endDay.getDayOfWeek() + ", " + endDay.getMonth() + " " + endDay.getDayOfMonth()));
		
	}

	/**
	 * This method tests the header print statements (first print statements)
	 */
	@Test
	public void testHeader()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		PrintStream old = System.out;
		System.setOut(ps);
		
		quizschedule.printQuizScheduleForm(quizList, retakesList, course);
		
		System.out.flush();
		System.setOut(old);
		
		String output = out.toString();
		
		//tests header output print statements 
		assertThat(output, CoreMatchers.containsString("GMU quiz retake scheduler for class " + course.getCourseTitle()));
		assertThat(output, CoreMatchers.containsString("You can sign up for quiz retakes within the next two weeks. "));
		assertThat(output, CoreMatchers.containsString("Enter your name (as it appears on the class roster), "));
		assertThat(output, CoreMatchers.containsString("then select which date, time, and quiz you wish to retake from the following list."));
	}
	
}

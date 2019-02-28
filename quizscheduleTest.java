package quizretakes;

import static org.junit.Assert.*;

import java.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.hamcrest.CoreMatchers;
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
	LocalDate today  = LocalDate.now();
	LocalDate endDay = today.plusDays(new Long(14));
	String courseID = "swe437";
	   
	@Before
	public void setUp() throws Exception
	{
		//sets up data 
		startSkip = LocalDate.of(2019, 1, 21); 
		endSkip = LocalDate.of(2019, 1, 25);
		quizList = new quizzes(); /* CLI */
		quizReader qr = new quizReader(); /* CLI */
		quizList = qr.read(System.getProperty("user.dir") + "\\src\\quizretakes\\quiz-orig-swe437.xml");
		retakesList = new retakes();
		retakesReader rr = new retakesReader();
		retakesList = rr.read(System.getProperty("user.dir") + "\\\\src\\\\quizretakes\\\\quiz-retakes-swe437.xml");
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
		//check if all 3 inputs are null
		quizschedule.printQuizScheduleForm(null, null, null); //Controllability
		
		//should return NullPointerException 
		quizschedule.printQuizScheduleForm(quizList, null, null); //Controllability
		
		//should return NullPointerException
		quizschedule.printQuizScheduleForm(null, retakesList, null); //Controllability

		//should return NullPointerException
		quizschedule.printQuizScheduleForm(null, null, course); //Controllability
		
		 //should return NullPointerException
		quizschedule.printQuizScheduleForm(null, retakesList, course); //Controllability

		//should return NullPointerException
		quizschedule.printQuizScheduleForm(quizList, null, course); //Controllability
		
		//should return NullPointerException
		quizschedule.printQuizScheduleForm(quizList, retakesList, null); //Controllability
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
		assertThat(output, CoreMatchers.containsString("GMU quiz retake scheduler for class " + course.getCourseTitle())); //Observability
		assertThat(output, CoreMatchers.containsString("You can sign up for quiz retakes within the next two weeks. ")); //Observability
		assertThat(output, CoreMatchers.containsString("Enter your name (as it appears on the class roster), ")); //Observability
		assertThat(output, CoreMatchers.containsString("then select which date, time, and quiz you wish to retake from the following list.")); //Observability
	}
	
	/** 
	 * This method tests correct printing correct today date and end date 
	 */
	@Test
	public void testDates()
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
		assertThat(output, CoreMatchers.containsString(today.getDayOfWeek() + ", " + today.getMonth() + " " + today.getDayOfMonth())); //Observability
		
		//tests prints out correct end date "scheduling quizzes until ..."
		assertThat(output, CoreMatchers.containsString(endDay.getDayOfWeek() + ", " + endDay.getMonth() + " " + endDay.getDayOfMonth())); //Observability
	}
	
	/**
	 * This method tests the correct retake statements
	 */
	@Test
	public void testRetakes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		PrintStream old = System.out;
		System.setOut(ps);
		
		quizschedule.printQuizScheduleForm(quizList, retakesList, course);
		
		System.out.flush();
		System.setOut(old);
		
		String output = out.toString();
		
		//tests retake output print statements 
		assertThat(output, CoreMatchers.containsString("RETAKE: THURSDAY, FEBRUARY 28, at 15:30 in EB 4430")); //Observability
		assertThat(output, CoreMatchers.containsString("RETAKE: TUESDAY, MARCH 5, at 16:00 in ???")); //Observability
	}
	
	/**
	 * This method tests the correct quiz statements
	 */
	@Test
	public void testQuizzes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		PrintStream old = System.out;
		System.setOut(ps);
		
		quizschedule.printQuizScheduleForm(quizList, retakesList, course);
		
		System.out.flush();
		System.setOut(old);
		
		String output = out.toString();
		
		//tests quiz output print statements 
		assertThat(output, CoreMatchers.containsString("1) Quiz 1 from TUESDAY, FEBRUARY 19")); //Observability
		assertThat(output, CoreMatchers.containsString("2) Quiz 2 from TUESDAY, FEBRUARY 26")); //Observability
		assertThat(output, CoreMatchers.containsString("3) Quiz 1 from TUESDAY, FEBRUARY 19")); //Observability
		assertThat(output, CoreMatchers.containsString("4) Quiz 2 from TUESDAY, FEBRUARY 26")); //Observability
	}
}

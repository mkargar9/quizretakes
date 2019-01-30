//JO, 10-Jan-2019
// Reads course information from an XML file with a DOM parser
// Stores in a course Bean and returns
// No xsd or validation

package quizretakes;

import java.lang.*;
import java.time.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

// XML parsers are so needy
// package dom; // in the documentation I found
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// These classes read the sample XML file and manage output:
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class courseReader
{

public courseBean read (String filename)
       throws IOException, ParserConfigurationException, SAXException
{
   courseBean course = null;

System.out.println("In course Reader, fileName: " +filename);
   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   DocumentBuilder builder = factory.newDocumentBuilder();
   Document document = builder.parse (new File (filename));

System.out.println("In course Reader");
   // Get all the nodes
   NodeList nodeList = document.getDocumentElement().getChildNodes();
   for (int i = 0; i < nodeList.getLength(); i++)
   {
System.out.println("course Reader, i=" +i);
      // XML structure is simple--6 elements
      // Not validating the data values
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
System.out.println("course Reader, in if");
         Element elem = (Element) node;

         // quiz IDs should be unique
         String courseID = elem.getElementsByTagName("courseID").item(0).getChildNodes().item(0).getNodeValue();
System.out.println("course Reader, courseID: " + courseID);
         String courseTitle = elem.getElementsByTagName("courseTitle").item(0).getChildNodes().item(0).getNodeValue();
         String retakeDuration = elem.getElementsByTagName("retakeDuration").item(0).getChildNodes().item(0).getNodeValue();
         String dataLocation = elem.getElementsByTagName("dataLocation").item(0).getChildNodes().item(0).getNodeValue();

         // startSkipMonth is an integer 1..12
         Integer startSkipMonth = Integer.parseInt(elem.getElementsByTagName("startSkipMonth").item(0).getChildNodes().item(0).getNodeValue());
         // startSkipDay is integer 1..31
         Integer startSkipDay = Integer.parseInt(elem.getElementsByTagName("startSkipDay").item(0).getChildNodes().item(0).getNodeValue());

         // endSkipMonth is an integer 1..12
         Integer endSkipMonth = Integer.parseInt(elem.getElementsByTagName("endSkipMonth").item(0).getChildNodes().item(0).getNodeValue());
         // endSkipDay is integer 1..31
         Integer endSkipDay = Integer.parseInt(elem.getElementsByTagName("endSkipDay").item(0).getChildNodes().item(0).getNodeValue());

         int year = Year.now().getValue();
         LocalDate startSkip = LocalDate.of(year, startSkipMonth, startSkipDay);
         LocalDate endSkip   = LocalDate.of(year, endSkipMonth, endSkipDay);

         course = new courseBean(courseID, courseTitle, retakeDuration, startSkip, endSkip, dataLocation);

      } // end if
   } // end for

   return (course);
} // end read

} // end class

// JO, 3-Jan-2019
// Checking XML and reading with DOM parser
// No xsd or validation as yet

package quizretakes;

import java.lang.*;
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

public class retakesReader
{

public retakes read (String filename)
       throws IOException, ParserConfigurationException, SAXException
{
   retakes retakeList = new retakes();
   retakeBean retake;


   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   DocumentBuilder builder = factory.newDocumentBuilder();
   Document document = builder.parse (new File (filename));

   // Get all the nodes
   NodeList nodeList = document.getDocumentElement().getChildNodes();
   for (int i = 0; i < nodeList.getLength(); i++)
   {
      // XML structure is simple--a bunch of quizzes
      // Not validating the data values
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
         Element elem = (Element) node;

         // retake IDs should be unique
         Integer ID = Integer.parseInt(elem.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue());
         // Location is a string (building and room probably)
         String location = elem.getElementsByTagName("location").item(0).getChildNodes().item(0).getNodeValue();
         // month is an integer 1..12
         Integer month = Integer.parseInt(elem.getElementsByTagName("month").item(0).getChildNodes().item(0).getNodeValue());
         // day is integer 1..31
         Integer day = Integer.parseInt(elem.getElementsByTagName("day").item(0).getChildNodes().item(0).getNodeValue());
         // hour is integer 0..23
         Integer hour = Integer.parseInt(elem.getElementsByTagName("hour").item(0).getChildNodes().item(0).getNodeValue());
         // minute is integer 0..59
         Integer minute = Integer.parseInt(elem.getElementsByTagName("minute").item(0).getChildNodes().item(0).getNodeValue());
         // Put one XML record into a bean and add it to the list
         retake = new retakeBean (ID, location, month, day, hour, minute);
         retakeList.addRetake (retake);

      } // end if
   } // end for

   // XML file may not be sorted
   retakeList.sort();

   return (retakeList);

} // end read


} // end class

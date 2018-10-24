/*
 * CSV2XML.java
 * Project: EATool
 * Created on 28-Jul-2006
 *
 */
package uk.co.alvagem.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * CSV2XML - utility to convert CSV to XML for testing basic stylesheets.
 * 
 * @author rbp28668
 */
public class CSV2XML {

    /**
     * 
     */
    public CSV2XML() {
        super();
    }

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Usage CSV2XML <input> <output>");
            return;
        }
        try {

            File outputFile = new File(args[1]);
            StreamResult result = new StreamResult(new FileOutputStream(outputFile));
            result.setSystemId(outputFile.getAbsolutePath());

            Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            props.setProperty(OutputKeys.INDENT,"yes");
            Serializer serializer = SerializerFactory.getSerializer(props);
            serializer.setOutputStream(result.getOutputStream());

            CSVBasicXMLReader xmlReader = new CSVBasicXMLReader();
            xmlReader.setContentHandler(serializer.asContentHandler());
            
            FileInputStream input = new FileInputStream(args[0]);
            InputSource source = new InputSource(input);
            xmlReader.parse(source);
            
            
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("SAX Exception: " + e.getMessage());
        }
    }
}

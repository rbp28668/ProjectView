/*
 * CSVBasicXMLReader.java
 * Project: EATool
 * Created on 08-Jan-2006
 *
 */
package uk.co.alvagem.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * CSVBasicXMLReader reads in a CSV file presenting it via SAX events as
 * as xml with the elements file, line and value1 through valueN.
 * 
 * @author rbp28668
 */
public class CSVBasicXMLReader extends AbstractXMLReader {
    
    /** an empty attribute for use with SAX */
    private static final Attributes EMPTY_ATTR = new AttributesImpl();
    
    /** Tag to delimit file */
    private static final String FILE_TAG = "file";
    
    /** Tag to delimit line */
    private static final String LINE_TAG = "line";
    
    /** Tag to delimit values */
    private static final String VALUE_TAG = "value";
    
    /**
     *  
     */
    public CSVBasicXMLReader() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource input) throws IOException, SAXException {
        
        // if no handler is registered to receive events, don't bother
        // to parse the CSV file
        ContentHandler ch = getContentHandler( );
        if (ch == null) {
            return;
        }
        
        
        // convert the InputSource into a BufferedReader
        BufferedReader br = getBufferedReader(input);
        
        ch.startDocument( );
        
        // emit <csvFile>
        ch.startElement("","",FILE_TAG,EMPTY_ATTR);
        
        // read each line of the file until EOF is reached
        String line;
        while ((line = br.readLine( )) != null) {
            line = line.trim( );
            if (line.length( ) > 0) {
                // create the <line> element
                ch.startElement("","",LINE_TAG,EMPTY_ATTR);
                
                // output data from this line
                StringBuffer curLine = new StringBuffer(line);
                parseLine(curLine, 0,  ch, 1, br);
                // close the </line> element
                ch.endElement("","",LINE_TAG);
            }
        }
        
        // emit </csvFile>
        ch.endElement("","",FILE_TAG);
        ch.endDocument( );
    }
    
    /**
     * @param input
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     */
    private BufferedReader getBufferedReader(InputSource input) throws MalformedURLException, IOException, SAXException {
        BufferedReader br = null;
        if (input.getCharacterStream() != null) {
            br = new BufferedReader(input.getCharacterStream());
        } else if (input.getByteStream() != null) {
            br = new BufferedReader(new InputStreamReader(input.getByteStream()));
        } else if (input.getSystemId() != null) {
            java.net.URL url = new URL(input.getSystemId());
            br = new BufferedReader(new InputStreamReader(url.openStream()));
        } else {
            throw new SAXException("Invalid InputSource object");
        }
        return br;
    }

    // Break an individual line into tokens.
    // This is a recursive function
    // that extracts the first token, then
    // recursively parses the
    // remainder of the line.
    private void parseLine(StringBuffer curLine, int start, ContentHandler ch, int idx, BufferedReader br)
    throws IOException, SAXException {
        
        String firstToken = null;
        int commaIndex = locateFirstDelimiter(curLine, start, br);
        if (commaIndex > -1) {
            firstToken = curLine.substring(start, commaIndex).trim( );
            start = skipSpace(curLine, commaIndex + 1);
        } else {
            // no commas, so the entire line from start is the token
            firstToken = curLine.substring(start);
            start = curLine.length();
        }
        
        // remove redundant quotes
        firstToken = cleanupQuotes(firstToken);
        
        // emit the <value> element
        String tag = VALUE_TAG + idx;
        ch.startElement("","",tag,EMPTY_ATTR);
        ch.characters(firstToken.toCharArray(), 0, firstToken.length( ));
        ch.endElement("","",tag);
        
        // recursively process the remainder of the line
        if (start < curLine.length()) {
            parseLine(curLine, start, ch, idx+1, br);
        }
    }
    
    /**
     * Finds the first non-space character in the buffer starting at a given position.
     * @param curLine is the buffer to parse.
     * @param start is the start point to look at
     * @return the position of the first non-space char.  Note: returns
     * curLine.length() if buffer only has spaces.
     */
    private int skipSpace(StringBuffer curLine, int start) {
        while(start < curLine.length()){
            if(Character.isSpaceChar(curLine.charAt(start))){
                ++start;
            } else {
                break;
            }
        }
        return start;
    }

    // locate the position of the comma,
    // taking into account that
    // a quoted token may contain ignorable commas.
    private int locateFirstDelimiter(StringBuffer curLine, int start, BufferedReader br) throws IOException {
        if (curLine.charAt(start) == '"') {
            boolean inQuote = true;
            ++start;
            
            // Loop round - normal exit is a return in the first pass but if
            // there was a newline in a quoted string we may need to try again.
            while(true){
	            int numChars = curLine.length( );
	            for (; start<numChars; ++start) {
	                char curChar = curLine.charAt(start);
	                if (curChar == '"') {
	                    inQuote = !inQuote;
	                } else if (curChar == ',' && !inQuote) {
	                    return start;  // normal return for quoted token
	                }
	            }
	            // if still in quote then newline in quoted string - try to
	            // recover by appending next non-blank line.
	            if(inQuote){
	                String line;
	                do {
	                    line = br.readLine();
	                    if(line == null){
	                        break;
	                    }
	                    line = line.trim();
	                } while(line.length() == 0);
	                
	                if(line == null){ 
	                    return -1;  // run off end of file - use whole string.
	                } else {
	                    curLine.append(line);
	                }
	            } else {
	                // Not in quote so whole line is a token
	                return -1; // normal return for no subsequent delimiter
	            }
            }
        } else {
            return curLine.indexOf(",",start); // normal return for unquoted token
        }
    }
    
    // remove quotes around a token, as well as pairs of quotes
    // within a token.
    private String cleanupQuotes(String token) {
        StringBuffer buf = new StringBuffer( );
        int length = token.length( );
        int curIndex = 0;
        
        if (token.startsWith("\"") && token.endsWith("\"")) {
            curIndex = 1;
            length--;
        }
        
        boolean oneQuoteFound = false;
        boolean twoQuotesFound = false;
        
        while (curIndex < length) {
            char curChar = token.charAt(curIndex);
            if (curChar == '"') {
                twoQuotesFound = (oneQuoteFound) ? true : false;
                oneQuoteFound = true;
            } else {
                oneQuoteFound = false;
                twoQuotesFound = false;
            }
            
            if (twoQuotesFound) {
                twoQuotesFound = false;
                oneQuoteFound = false;
                curIndex++;
                continue;
            }
            
            buf.append(curChar);
            curIndex++;
        }
        
        return buf.toString( );
    }
    
    /**
     * Sees if an XML tag is the root file tag.
     * @return true if the tag is a FILE_TAG.
     */
    public boolean isFileTag(String tag) {
        return tag.equals(FILE_TAG);
    }
    
    /**
     * Sees if an XML tag is the tag used to delimit the start of a line.
     * @return true if the tag is a  LINE_TAG.
     */
    public boolean isLineTag(String tag) {
        return tag.equals(LINE_TAG);
    }
    
    /**
     * Sees if an XML tag is the tag for a value on a line.
     * @return  true if the tag is a VALUE_TAG.
     */
    public boolean isValueTag(String tag) {
        return tag.startsWith(VALUE_TAG);
    }
}



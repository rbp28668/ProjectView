/**
 * 
 */
package uk.co.alvagem.projectview.core.msimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XSLTTransform;

/**
 * 
 * For project 2003: http://msdn.microsoft.com/en-us/library/aa679870(office.11).aspx
 * For project 2007: http://msdn.microsoft.com/en-us/library/bb968652.aspx
 * @author bruce.porteous
 *
 */
public class MSProjectXMLImport extends DefaultHandler implements ContentHandler{

    private static final String PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
    private static final String NS = "http://schemas.microsoft.com/project";

    private XMLReader parser;
    private HashMap<String,Handler> handlers = new HashMap<String,Handler>();
    private Locator locator = null;
    private Node top = null;
    private Stack<Node> nodes = new Stack<Node>();
	private StringBuffer text = new StringBuffer();

	private DAOFactory factory;

    /** Creates new MSProjectXMLImport  */
    public MSProjectXMLImport() {
        this(PARSER_NAME);
    }

    /** Creates new XMLLoader.
     * @param parserClass is the name of the class to use for the parser.  
     */
    public MSProjectXMLImport(String parserClass) {
        try {
            parser = (XMLReader)Class.forName(parserClass).newInstance();
            parser.setContentHandler(this);
            parser.setErrorHandler(this);
            parser.setFeature( "http://xml.org/sax/features/namespaces", true );
            
            handlers.put("Project", new ProjectHandler(this));
            handlers.put("Calendar", new CalendarHandler(this));
            handlers.put("Assignment", new AssignmentHandler(this));
            handlers.put("Task", new TaskHandler(this));
        }
        catch(Exception e) {
            throw new UnsupportedOperationException("Unable to setup XML Parser " + parserClass + ":" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Allow handlers to access the dao factory.
     * @return the DAO factory.
     */
    DAOFactory getFactory() {
    	return factory;
    }
    
    /**
     * Walks down the current node hierarchy looking for a given node with an XPath like expression.
     * @param path is the path expression - starting with / as the root node, nodes are selected by name
     * separated by /.
     * @return the text value of the selected node.
     */
    String getValueAt(String path){
    	
    	Node node = top;
    	StringTokenizer toks = new StringTokenizer(path,"/");
    	while(toks.hasMoreTokens()){
    		String tok = toks.nextToken().trim();
    		if(tok.length() == 0){
    			throw new IllegalArgumentException("path can't have empty selectors: " + path);
    		}
    		node = node.getChild(tok);
    		if(node == null) {
    			throw new IllegalArgumentException("no child node corresponding to " + tok);
    		}
    	}
    	return node.getText();
    }
    
    
    /**
     * Parses a stream obtained from a URI.  Events are fired in registered
     * handlers when matching entities are found in the XML.
     * @param uri identifies the data to parse
     */    
    public void parse(String uri) throws SAXException {
        try {
            parser.parse(uri);
        }
        catch(IOException e) {
            throw new InputException("Unable to read xml input ", e);
        }
    }

    /**
     * Parses an InputStream.  Events are fired in registered
     * handlers when matching entities are found in the XML.
     * @param stream provides the data to parse
     */    
    public void parse(InputStream stream) throws SAXException {
        try {
            parser.parse(new InputSource(stream));
        }
        catch(IOException e) {
            throw new InputException("Unable to read xml input ", e);
        }
    }

	private String getText(){
		return text.toString();
	}
	
	private void clearText(){
		text.delete(0,text.length());
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
        this.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
        factory.beginTransaction();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		factory.commit();
		factory = null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		text.append(arg0,arg1,arg2);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String gName,
			Attributes attrs) throws SAXException {
		
		if(uri.equals(NS)){
			Node node = new Node(localName);
			nodes.push(node);
			
			if(top == null){
				top = node;
			}
			
		}
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String gName)
			throws SAXException {

		if(uri.equals(NS)){
			Node node = nodes.pop();
			node.setText(getText());
			clearText();
			
			if(nodes.size() > 0){
				Node parent = nodes.peek();
				parent.addChild(node);
			}
			
			Handler handler = handlers.get(localName);
			if(handler != null){
				try{
					handler.process(node);
				} catch (Exception e){
					throw new SAXException("Unable to process data for " + localName, e);
				}
			}
		}

	}

}


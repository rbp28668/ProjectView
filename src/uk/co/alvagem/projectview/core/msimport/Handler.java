/**
 * 
 */
package uk.co.alvagem.projectview.core.msimport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * See http://msdn.microsoft.com/en-us/library/bb968652.aspx for
 * details of Project 2007 xml export structure.
 * @author bruce.porteous
 *
 */
public abstract class Handler  {

	private MSProjectXMLImport msimport;
	private DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'THH:mm:ss");

	
	protected Handler(MSProjectXMLImport msimport){
		this.msimport = msimport;
	}
	
	protected MSProjectXMLImport getImport(){
		return msimport;
	}
	
	public abstract void process(Node node) throws Exception;
	
//	public boolean shouldPreview(){ return false; }
//	
//	public void preview(Node node){ };
	
	
	/**
	 * @param node
	 * @param string
	 * @return
	 */
	protected String getChildText(Node node, String name) {
		return node.getChild(name).getText();
	}

	protected long getChildDuration(Node node, String name) throws ParseException{
		return parseDuration(getChildText(node,name));
	}
	
	protected Date getChildDate(Node node, String name) throws ParseException{
		return parseDate(getChildText(node,name));
	}
	
	protected int getChildInt(Node node, String name){
		return Integer.parseInt(getChildText(node,name));
	}
	
	/**
	 * Parses a duration value and returns the duration in mS (commensurate
	 * with Date.getTime()).
	 * A duration of time, provided in the format PnYnMnDTnHnMnS where nY 
	 * represents the number of years, nM the number of months, nD the 
	 * number of days, T the date/time separator, nH the number of hours, 
	 * nM the number of minutes, and nS the number of seconds.
	 * For example, to indicate a duration of 1 year, 2 months, 3 days, 
	 * 10 hours, and 30 minutes, you write: P1Y2M3DT10H30M. 
	 * You could also indicate a duration of minus 120 days as -P120D
	 * @param text
	 * @return
	 * @throws ParseException 
	 */
	protected long parseDuration(String text) throws ParseException{
		
		long acc = 0;
		long total = 0;
		boolean isDate = true;
		boolean negate = false;

		for(int idx = 1; idx<text.length(); ++idx){
			if(Character.isDigit(text.charAt(idx))){
				acc *= 10;
				acc += (text.charAt(idx) - '0');
			} else { // not a digit
				switch (text.charAt(idx)) {
				
				case '-':
					negate = true;
					break;
				
				case 'P': // Leading P - ignore.
					break;
					
				case 'Y':
					total += (acc * 365 * 24 * 60 * 60 * 1000L);  // years worth (ignoring leap-years).  
					break;
				
				case 'M':
					if(isDate) {
						total += (acc * 365 * 24 * 60 * 60 * 1000L )/12;  // months worth
					} else {
						total += (acc * 60 * 1000L); // minutes worth
					}
					break;
					
				case 'D':
					total += (acc * 24 * 60 * 60 * 1000L);
					break;
					
				case 'T':
					isDate = false;  // time fields from now on (M is ambiguous without this )
					break;
					
				case 'H':
					total += (acc * 60 * 60 * 1000L);
					break;
					
				default:
					throw new ParseException("Invalid format character in duration: " + text.charAt(idx),0);
				}
				acc = 0; // not a digit so reset value.
			}
		}
		if(negate){
			total = 0-total;
		}
		return total;
	}
	
	
	/**
	 * YYYY-MM-DDTHH:MM:SS
	 * @param text
	 * @return
	 * @throws ParseException 
	 */
	protected Date parseDate(String text) throws ParseException{
		return fmt.parse(text);
	}
}

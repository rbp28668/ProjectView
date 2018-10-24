/**
 * 
 */
package uk.co.alvagem.projectview.core.msimport;

import java.util.LinkedList;
import java.util.List;

/**
 * An element in an XML tree.  Note that the MS Project XML is simple - use of this
 * simple Node structure simplifies downstream processing compared with even
 *  JDom or Dom4J.
 *  Here nodes can have text content and/or child nodes.  
 * @author bruce.porteous
 *
 */
public class Node {
	private String name;
	private String text;
	private List<Node> children = new LinkedList<Node>();
	
	public Node(String name){
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	public void addChild(Node child){
		children.add(child);
	}
	
	public List<Node> getChildren(){
		return children;
	}

	public List<Node> getChildren(String name){
		List<Node> matching = new LinkedList<Node>();
		for(Node child : children){
			if(child.name.equals(name)){
				matching.add(child);
			}
		}
		
		return matching;
	}

	public Node getChild(String name){
		for(Node child : children){
			if(child.name.equals(name)){
				return child;
			}
		}
		return null;
	}
}

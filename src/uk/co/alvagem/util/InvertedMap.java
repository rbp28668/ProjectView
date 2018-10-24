/*
 * InvertedMap.java
 * Created on 31-May-2004
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * InvertedMap is a variant on a HashMap for inverse lookup where
 * keys become values and vice-versa.
 * @author Bruce.Porteous
 *
 */
public class InvertedMap extends HashMap {


	/**
	 * @param m
	 */
	public InvertedMap(Map m) {
		super();
		invertMap(m);
	}
	
	/**
	 * Do the map inversion.
	 * @param source
	 */
	private void invertMap(Map source){
		for(Iterator iter = source.entrySet().iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			put(entry.getValue(), entry.getKey());
		}
	}


}

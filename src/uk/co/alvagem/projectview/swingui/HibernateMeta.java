/*
 * HibernateMeta.java
 * Created on 14-May-2005
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.util.Iterator;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

import uk.co.alvagem.database.HibernateUtil;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

/**
 * HibernateMeta
 * 
 * @author rbp28668
 * Created on 14-May-2005
 */
public class HibernateMeta {

    private Configuration cfg;
    
    /**
     * 
     */
    public HibernateMeta() {
        super();
        cfg = HibernateUtil.getConfig();
    }

    public void update(FormTemplate template){
        if(template == null){
            throw new NullPointerException("Cannot update a null FormTemplate");
        }
        
        for(FieldTemplate field : template.getFields()){
           update(field);
        }
    }
    
    public void update(FieldTemplate field){
 		
        String className = field.getTargetClass().getName();
		PersistentClass pc =  cfg.getClassMapping(className);
		if(pc == null) {
		    System.out.println("Not persistent: " + className);
		    return;
		}
		
		String propertyName = field.getProperty();
		Property property;
		try {
		    property = pc.getProperty(propertyName);
		} catch (MappingException mx) {
		    property = null;
		}

		if(property == null){
		    System.out.println("No property " + propertyName + " on " + className);
		    return;
		}
		
		//System.out.println("Property " + propertyName);
		//System.out.println("Property Column span is " + property.getColumnSpan());
		    
	    field.setRequired(!property.isOptional());
		    
		Column column = getColumn(property);
		if(column != null) {
			int length = column.getLength();
			field.setMaxlen(length);
		}
    }
    
    private Column getColumn(Property property){
		Iterator<Column> iter = property.getColumnIterator();
		Column col = null;
		if(iter.hasNext()){
			col = iter.next();
		}
		if(iter.hasNext()){
		    col = null; // multiple column - can't use for setting template
		}
		return col;
	}

 	

}

/*
 * ConstraintTypes.java
 * Project: ProjectView
 * Created on 28 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConstraintTypes {

    public static final ConstraintType START_NOT_BEFORE = new StartNotBefore();
    public static final ConstraintType START_NOT_AFTER = new StartNotAfter();
    public static final ConstraintType START_AT = new StartAt();
    public static final ConstraintType FINISH_NOT_BEFORE = new FinishNotBefore();
    public static final ConstraintType FINISH_NOT_AFTER = new FinishNotAfter();
    public static final ConstraintType FINISH_AT = new FinishAt();

    private static Map<String,ConstraintType> constraints = new HashMap<String,ConstraintType>();
    static {
        constraints.put(START_NOT_BEFORE.toString(), START_NOT_BEFORE);
        constraints.put(START_NOT_AFTER.toString(), START_NOT_AFTER);
        constraints.put(START_AT.toString(), START_AT);
        constraints.put(FINISH_NOT_BEFORE.toString(), FINISH_NOT_BEFORE);
        constraints.put(FINISH_NOT_AFTER.toString(), FINISH_NOT_AFTER);
        constraints.put(FINISH_AT.toString(), FINISH_AT);
     }
    
    private ConstraintTypes(){}
    
    /**
     * Get a constraint type by name.
     * @param name
     * @return
     */
    public static ConstraintType get(String name){
        ConstraintType type =  constraints.get(name);
        if(type == null){
            throw new IllegalArgumentException("Unknown constraint type " + name);
        }
        return type;
    }
    
    /**
     * Get a collection of available ConstraintType e.g. for drop-downs.
     * @return Collection<ConstraintType> 
     */
    public static Collection<ConstraintType> getTypes(){
        return constraints.values();
    }
    
}

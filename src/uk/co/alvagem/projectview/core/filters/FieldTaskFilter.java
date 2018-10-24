/**
 * 
 */
package uk.co.alvagem.projectview.core.filters;

import java.lang.reflect.InvocationTargetException;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;
import uk.co.alvagem.ui.TypeHandler;

/**
 * @author bruce.porteous
 *
 */
public class FieldTaskFilter extends AbstractTaskFilter implements TaskFilter, Editable {

	public static final String COMPARISON_WIDGET = "FieldTaskFilter.Comparison";
	public static final String FIELD_WIDGET = "FieldTaskFilter.FieldTemplate";
	
    private static FormTemplate template = new FormTemplate();
    
    private static FormTemplate editTemplate = new FormTemplate();
    
    static {
	    template.add(new FieldTemplate(Task.class,"Name","name"));
	    template.add(new FieldTemplate(Task.class,"Description","description"));
	    template.add(new FieldTemplate(Task.class,"Notes","notes"));
	    template.add(new FieldTemplate(Task.class,"Work Package","workPackage"));
	    template.add(new FieldTemplate(Task.class,"Priority","priority"));
	    
	    template.add(new FieldTemplate(Task.class,"Estimated Effort","estimatedEffort"));
	    template.add(new FieldTemplate(Task.class,"Estimate Spread","estimateSpread"));
	    template.add(new FieldTemplate(Task.class,"Fraction Complete","fractionComplete"));
	    template.add(new FieldTemplate(Task.class,"Actual Work","actualWork"));
	    template.add(new FieldTemplate(Task.class,"Effort Driven", "effortDriven"));
	    template.add(new FieldTemplate(Task.class,"Active", "active"));
	    
	    editTemplate.add(new FieldTemplate(FieldTaskFilter.class,"Property","field",FieldTemplate.OBJECT)
	    		.setWidgetType(FIELD_WIDGET));
	    editTemplate.add(new FieldTemplate(FieldTaskFilter.class,"Comparision","comparison",FieldTemplate.OBJECT)
	    		.setWidgetType(COMPARISON_WIDGET));
	    editTemplate.add(new FieldTemplate(FieldTaskFilter.class,"With","value"));
    }
	
    private FieldTemplate field;
    private String value;
    private Comparison comparison;
    
	/**
	 * 
	 */
	public FieldTaskFilter() {
		super();
	}

    public boolean accept(Task task) throws FilterException {
		try {
			Object retval = field.getGetter().invoke(task,(Object[])null);
			
			if(retval instanceof Comparable){
				Comparable comparable = (Comparable)retval;
				TypeHandler handler = field.getHandler();
				return comparison.accept(comparable, handler.parse(value));
			}
			
		} catch (IllegalArgumentException e) {
			throw new FilterException("Can't get field value " + e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new FilterException("Can't get field value " + e.getMessage(),e);
		} catch (InvocationTargetException e) {
			throw new FilterException("Can't get field value " + e.getMessage(),e);
		}
		return false;
    }

    public String toString(){
        return field.getName() + " " + comparison.toString() + " " + value;
    }
    
    public TaskFilter copy(){
        return new ActiveTaskFilter();
    }

	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.core.filters.Editable#getEditTemplate()
	 */
	public FormTemplate getEditTemplate() {
		return editTemplate;
	}

	

	/**
	 * @return Returns the comparison.
	 */
	public Comparison getComparison() {
		return comparison;
	}

	/**
	 * @param comparison The comparison to set.
	 */
	public void setComparison(Comparison comparison) {
		this.comparison = comparison;
	}

	/**
	 * @return Returns the field.
	 */
	public FieldTemplate getField() {
		return field;
	}

	/**
	 * @param field The field to set.
	 */
	public void setField(FieldTemplate field) {
		this.field = field;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public static FormTemplate getTemplate(){
		return template;
	}
	
	
}

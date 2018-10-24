/**
 * 
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.ui.FormTemplate;

/**
 * Interface to show that a task filter is editable.
 * @author bruce.porteous
 *
 */
public interface Editable {
	/**
	 * Gets the FormTemplate needed to edit this object.
	 * @return a FormTemplate to drive the editing.
	 */
	public FormTemplate getEditTemplate();
}

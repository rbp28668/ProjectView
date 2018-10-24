/**
 * 
 */
package uk.co.alvagem.projectview.plugins;

import java.util.Map;

/**
 * Interface that should be implemented by a project-view plugin.
 * @author bruce.porteous
 *
 */
public interface ProjectViewPlugin {

	/**
	 * Gets the name of the plugin which can be used in a menu.
	 * @return the plugin's name.
	 */
	public String getName();
	
	
	/**
	 * Gets a map of the command names and their descriptions.
	 * @return a map of command names (keys) and descriptions (values).
	 */
	public Map<String,String> getCommands();
	
	
	/**
	 * Runs a command on the given database.
	 * @param commandName is one of the command names given by the getCommands() method.
	 * @param context provides the context for the plugin to run in.
	 */
	public void runCommand(String commandName, ProjectViewContext context) throws Exception;

}

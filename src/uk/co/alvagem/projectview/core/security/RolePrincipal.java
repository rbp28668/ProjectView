/**
 * 
 */
package uk.co.alvagem.projectview.core.security;

import java.security.Principal;

/**
 * @author bruce.porteous
 *
 */
public class RolePrincipal implements Principal {

	public final static RolePrincipal ADMINISTRATOR = new RolePrincipal("Administrator");
	
	
	
	private String name;

	private RolePrincipal(){}
	
	private RolePrincipal(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return name;
	}

}

/**
 * 
 */
package uk.co.alvagem.projectview.core.security;

import java.security.Principal;

/**
 * @author bruce.porteous
 *
 */
public class UserPrincipal implements Principal {

	private String name;
	public UserPrincipal(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return name;
	}

}

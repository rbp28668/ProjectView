/**
 * 
 */
package uk.co.alvagem.projectview.model;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import uk.co.alvagem.projectview.core.security.UserPrincipal;

/**
 * @author bruce.porteous
 *
 */
public class ChangeTrackInterceptor implements Interceptor {

	private Principal principal;
	
	public ChangeTrackInterceptor(Subject subject){
		Set<UserPrincipal> principals = subject.getPrincipals(UserPrincipal.class);
		if(principals.size() != 1){
			throw new IllegalStateException("Subject can only have 1 user principal");
		}
		this.principal = principals.iterator().next();
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#afterTransactionBegin(org.hibernate.Transaction)
	 */
	public void afterTransactionBegin(Transaction arg0) {
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#afterTransactionCompletion(org.hibernate.Transaction)
	 */
	public void afterTransactionCompletion(Transaction arg0) {
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#beforeTransactionCompletion(org.hibernate.Transaction)
	 */
	public void beforeTransactionCompletion(Transaction arg0) {
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2,
			Object[] arg3, String[] arg4, Type[] arg5) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#getEntity(java.lang.String, java.io.Serializable)
	 */
	public Object getEntity(String entityName, Serializable id)
			throws CallbackException {
		// Put cache read here!
		//System.out.println("Hibernate.getEntity(" + entityName + "," + id + ")");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#getEntityName(java.lang.Object)
	 */
	public String getEntityName(Object object) throws CallbackException {
		//System.out.println("Hibernate.getEntityName - " + object.toString());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#instantiate(java.lang.String, org.hibernate.EntityMode, java.io.Serializable)
	 */
	public Object instantiate(String arg0, EntityMode arg1, Serializable arg2)
			throws CallbackException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#isTransient(java.lang.Object)
	 */
	public Boolean isTransient(Object arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public void onDelete(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) throws CallbackException {
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
			Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
		
		// Modification
		if(entity instanceof Persistent) {
			for(int i=0; i<propertyNames.length; ++i){
				if(propertyNames[i].equals("timestamp")){
					currentState[i] = new Date();
				}
				if(propertyNames[i].equals("user")){
					currentState[i] = principal.getName();
				}
			}
			return true;
		}
			
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#onLoad(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) throws CallbackException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		// Modification
		if(entity instanceof Persistent) {
			for(int i=0; i<propertyNames.length; ++i){
				if(propertyNames[i].equals("timestamp")){
					state[i] = new Date();
				}
				if(propertyNames[i].equals("user")){
					state[i] = principal.getName();
				}
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#postFlush(java.util.Iterator)
	 */
	public void postFlush(Iterator arg0) throws CallbackException {
	}

	/* (non-Javadoc)
	 * @see org.hibernate.Interceptor#preFlush(java.util.Iterator)
	 */
	public void preFlush(Iterator arg0) throws CallbackException {
	}

}

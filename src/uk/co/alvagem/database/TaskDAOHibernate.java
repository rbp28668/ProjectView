/*
 * TaskDAOHibernate.java
 * Project: ProjectView
 * Created on 30 Dec 2007
 *
 */
package uk.co.alvagem.database;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;

public class TaskDAOHibernate
extends GenericHibernateDAO<Task, Integer>
implements TaskDAO {

    @SuppressWarnings("unchecked")
    public List<Task> findTopLevelTasks() {
        Session session = getSession();
        Query q = session.createQuery("from Task t where t.parent is null");
        return q.list();
    }

	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.dao.TaskDAO#findByExternalKey(java.lang.String)
	 */
	public Task findByExternalKey(String key) {
        Session session = getSession();
        Query q = session.createQuery("from Task t where t.externalId = ?");
        q.setString(0, key);
        return (Task)q.uniqueResult();
	}
    
}


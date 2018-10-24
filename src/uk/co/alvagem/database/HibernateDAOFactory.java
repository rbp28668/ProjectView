/*
 * HibernateDAOFactory.java
 * Project: ProjectView
 * Created on 28 Dec 2007
 *
 */
package uk.co.alvagem.database;


import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.co.alvagem.projectview.dao.AllocationDAO;
import uk.co.alvagem.projectview.dao.CalendarDAO;
import uk.co.alvagem.projectview.dao.CalendarItemDAO;
import uk.co.alvagem.projectview.dao.ConstraintDAO;
import uk.co.alvagem.projectview.dao.DependencyDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.dao.TaskHistoryDAO;
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.CalendarItem;
import uk.co.alvagem.projectview.model.Constraint;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.TaskHistory;

public class HibernateDAOFactory extends DAOFactory {

    private Transaction transaction = null;
    
    private GenericHibernateDAO instantiateDAO(Class daoClass) {
        try {
            GenericHibernateDAO dao = (GenericHibernateDAO)daoClass.newInstance();
            dao.setSession(getCurrentSession());
            return dao;
        } catch (Exception ex) {
            throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
        }
    }

    // You could override this if you don't want HibernateUtil for lookup
    protected Session getCurrentSession() {
        return HibernateUtil.getSession();
    }

    @Override
    public void beginTransaction() {
        transaction = getCurrentSession().beginTransaction();
    }

    @Override
    public void commit() {
        transaction.commit();
        transaction = null;
        HibernateUtil.closeSession();
    }

    @Override
    public void rollback() {
        transaction.rollback();
        transaction = null;
        HibernateUtil.closeSession();
    }
    
    
    // Inline concrete DAO implementations with no business-related data access methods.
    // If we use public static nested classes, we can centralize all of them in one source file.

 
    @Override
    public AllocationDAO getAllocationDAO() {
        return (AllocationDAO)instantiateDAO(AllocationDAOHibernate.class);
    }

	@Override
	public CalendarDAO getCalendarDAO() {
		return (CalendarDAO)instantiateDAO(CalendarDAOHibernate.class);
	}

	@Override
	public CalendarItemDAO getCalendarItemDAO() {
		return (CalendarItemDAO)instantiateDAO(CalendarItemDAOHibernate.class);
	}
	
    @Override
    public ConstraintDAO getConstraintDAO() {
        return (ConstraintDAO)instantiateDAO(ConstraintDAOHibernate.class);
    }

    @Override
    public DependencyDAO getDependencyDAO() {
        return (DependencyDAO)instantiateDAO(DependencyDAOHibernate.class);
    }

    @Override
    public ResourceDAO getResourceDAO() {
        return (ResourceDAO)instantiateDAO(ResourceDAOHibernate.class);
    }

    @Override
    public TaskDAO getTaskDAO() {
        return (TaskDAO)instantiateDAO(TaskDAOHibernate.class);
    }

    @Override
    public TaskHistoryDAO getTaskHistoryDAO() {
        return (TaskHistoryDAO)instantiateDAO(TaskHistoryDAOHibernate.class);
    }

    public static class AllocationDAOHibernate
    extends GenericHibernateDAO<Allocation, Integer>
    implements AllocationDAO {}

    public static class CalendarDAOHibernate
    extends GenericHibernateDAO<Calendar, Integer>
    implements CalendarDAO {}

    public static class CalendarItemDAOHibernate
    extends GenericHibernateDAO<CalendarItem, Integer>
    implements CalendarItemDAO {}

    public static class ConstraintDAOHibernate
    extends GenericHibernateDAO<Constraint, Integer>
    implements ConstraintDAO {}

    public static class DependencyDAOHibernate
    extends GenericHibernateDAO<Dependency, Integer>
    implements DependencyDAO {}

    public static class ResourceDAOHibernate
    extends GenericHibernateDAO<Resource, Integer>
    implements ResourceDAO {}

    public static class TaskHistoryDAOHibernate
    extends GenericHibernateDAO<TaskHistory, Integer>
    implements TaskHistoryDAO {}



   

}
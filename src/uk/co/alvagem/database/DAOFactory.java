/*
 * DAOFactory.java
 * Project: ProjectView
 * Created on 28 Dec 2007
 *
 */
package uk.co.alvagem.database;

import uk.co.alvagem.projectview.dao.AllocationDAO;
import uk.co.alvagem.projectview.dao.CalendarDAO;
import uk.co.alvagem.projectview.dao.CalendarItemDAO;
import uk.co.alvagem.projectview.dao.ConstraintDAO;
import uk.co.alvagem.projectview.dao.DependencyDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.dao.TaskHistoryDAO;

public abstract class DAOFactory {

    /**
     * Creates a standalone DAOFactory that returns unmanaged DAO
     * beans for use in any environment Hibernate has been configured
     * for. Uses HibernateUtil/SessionFactory and Hibernate context
     * propagation (CurrentSessionContext), thread-bound or transaction-bound,
     * and transaction scoped.
     */
    public static final Class HIBERNATE = uk.co.alvagem.database.HibernateDAOFactory.class;

    /**
     * Factory method for instantiation of concrete factories.
     */
    public static DAOFactory instance(Class factory) {
        try {
            return (DAOFactory)factory.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't create DAOFactory: " + factory);
        }
    }

    public abstract void beginTransaction();
    public abstract void commit();
    public abstract void rollback();
    
    // Add your DAO interfaces here
    public abstract AllocationDAO getAllocationDAO();
    public abstract CalendarDAO getCalendarDAO();
    public abstract CalendarItemDAO getCalendarItemDAO();
    public abstract ConstraintDAO getConstraintDAO();
    public abstract DependencyDAO getDependencyDAO();
    public abstract ResourceDAO getResourceDAO();
    public abstract TaskDAO getTaskDAO();
    public abstract TaskHistoryDAO getTaskHistoryDAO();

}

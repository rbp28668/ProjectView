/*
 * HibernateUtil.java
 * Created on 11-May-2005
 *
 */
package uk.co.alvagem.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;



/**
 * HibernateUtil
 * 
 * @author rbp28668
 * Created on 11-May-2005
 */
public class HibernateUtil {

    private static Log log = LogFactory.getLog(HibernateUtil.class);

    private static Configuration cfg;
    private static SessionFactory sessionFactory;

    private static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    /**
     * Initialise the session factory from the given configuration.
     * @param cfg
     */
    public static void setConfig(Configuration cfg){
        try {
            HibernateUtil.cfg = cfg;
            sessionFactory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Set the configuration from hibernate.cfg.xml and build the session factory.
     */
    public static void setConfig(){
        try {
            // Create the Configuration and SessionFactory
            cfg = new Configuration();
            sessionFactory = cfg.configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Get the configuration - useful for checking metadata such as mandatory and
     * field lengths.
     * @return the current config.
     */
    public static Configuration getConfig(){
        return cfg;
    }
    
    public static Session getSession() throws HibernateException {
        Session s = session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            if(sessionFactory == null){
                throw new IllegalStateException("Hibernate not configured");
            }
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
    
    
    public static void createSchema(){
        Connection con = getSession().connection();
        SchemaExport export = new SchemaExport(cfg,con);
        export.create(true,true);	// display and run script.
        for(Iterator<Exception> iter = export.getExceptions().iterator(); iter.hasNext();){
            Exception e = iter.next();
            System.out.println("Error creating tables: " + e.getMessage());
        }
        try{
            SchemaView.showTables(con);
        } catch (SQLException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    public static void dropSchema(){
        Connection con = getSession().connection();
        SchemaExport export = new SchemaExport(cfg,con);
        export.drop(true,true);	// display and run script.
    }

    public static void updateSchema() {
        SchemaUpdate schemaUpdate = new SchemaUpdate(cfg);
        schemaUpdate.execute(true,true); // run and display 
    }
}


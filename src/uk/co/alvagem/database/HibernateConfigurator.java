/*
 * HibernateConfigurator.java
 * Project: ProjectView
 * Created on 31 Dec 2007
 *
 */
package uk.co.alvagem.database;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.SettingsManager.Element;

/**
 * HibernateConfigurator
 * Configures hibernate from settings files.
 * @see http://www.hibernate.org/hib_docs/v3/reference/en/html/session-configuration.html#configuration-programmatic
 * @author rbp28668
 */
public class HibernateConfigurator {

    private Configuration cfg;
    private Properties properties = new Properties();
    private String configName = "LocalServer";

    // The common dialects - not a complete list, see the Hibernate docs.
    public static final String[] DIALECTS = {
        "org.hibernate.dialect.DB2Dialect",
        "org.hibernate.dialect.DerbyDialect",
        "org.hibernate.dialect.FirebirdDialect",
        "org.hibernate.dialect.FrontBaseDialect",
        "org.hibernate.dialect.HSQLDialect", // version 1.8 and higher only
        "org.hibernate.dialect.InformixDialect",
        "org.hibernate.dialect.IngresDialect",
        "org.hibernate.dialect.InterbaseDialect",
        "org.hibernate.dialect.MySQL5Dialect",
        "org.hibernate.dialect.MySQL5InnoDBDialect",
        "org.hibernate.dialect.MySQLDialect",
        "org.hibernate.dialect.MySQLInnoDBDialect",
        "org.hibernate.dialect.MySQLMyISAMDialect",
        "org.hibernate.dialect.Oracle9Dialect",
        "org.hibernate.dialect.OracleDialect",
        "org.hibernate.dialect.PostgreSQLDialect",
        "org.hibernate.dialect.SQLServerDialect",
        "org.hibernate.dialect.SybaseDialect"
        
    };

    // Names of basic properties
    private final static String DRIVER = "hibernate.connection.driver_class";
    private final static String URL = "hibernate.connection.url";
    private final static String USERNAME = "hibernate.connection.username";
    private final static String PASSWORD = "hibernate.connection.password";
    private final static String DIALECT = "hibernate.dialect";

    /**
     * Creates a new configurator with a default configuration.
     */
    public HibernateConfigurator(){
        cfg = new Configuration();
        setDefaultProperties();
    }
    
    /**
     * Get the configured Configuration.  Note that this is a one-hit method - you 
     * configure the Configuration then get it once. This also ensures that the
     * JDBC driver is registered with the driver manager.
     * @return a Configured Configuration.
     * @throws ClassNotFoundException 
     */
    public Configuration getConfig() throws ClassNotFoundException{
    	
    	// Ensure driver is registered with the JDBC driver manager.
    	String driver = properties.getProperty(DRIVER);
    	Class.forName(driver);
    	
    	
        cfg.setProperties(properties);
        Configuration toReturn = cfg;
        cfg = null;
        return toReturn;
    }
    
    public void addClass(Class<?> persistent){
        cfg.addClass(persistent);
    }
    
    public Properties getProperties(){
        return properties;
    }
    
    public DataSource getDataSource(){
        DataSource ds = new DataSource();
        ds.setName(configName);
        ds.setDriverClass(properties.getProperty(DRIVER));
        ds.setConnectionURL(properties.getProperty(URL));
        ds.setUsername(properties.getProperty(USERNAME));
        ds.setPassword(properties.getProperty(PASSWORD));
        ds.setDialect(properties.getProperty(DIALECT));
        return ds;
    }
    
    public void setDataSource(DataSource ds){
        configName = ds.getName();
        properties.setProperty(DRIVER,ds.getDriverClass());
        properties.setProperty(URL,ds.getConnectionURL());
        properties.setProperty(USERNAME,ds.getUsername());
        properties.setProperty(PASSWORD,ds.getPassword());
        properties.setProperty(DIALECT,ds.getDialect());
    }
    
    private void setDefaultProperties(){
        //<!-- Database connection settings -->
    	properties.setProperty(DRIVER,"org.hsqldb.jdbcDriver");
    	properties.setProperty(URL,"jdbc:hsqldb:hsql://localhost/projectview");
    	properties.setProperty(USERNAME,"sa");
    	properties.setProperty(PASSWORD,"");

        //<!-- JDBC connection pool (use the built-in) --,"
    	properties.setProperty("hibernate.connection.pool_size","1");

        //<!-- SQL dialect --,"
    	properties.setProperty(DIALECT,"org.hibernate.dialect.HSQLDialect");

        //<!-- Enable Hibernate's automatic session context management --,"
    	properties.setProperty("hibernate.current_session_context_class","thread");

        //<!-- Disable the second-level cache  --,"
    	properties.setProperty("hibernate.cache.provider_class","org.hibernate.cache.NoCacheProvider");

        //<!-- Echo all executed SQL to stdout if true--,"
    	properties.setProperty("hibernate.show_sql","true");
        //props.setProperty("hibernate.show_sql","false");

        //<!-- For debugging execute SQL one statement at a time -->
        //props.setProperty("hibernate.jdbc.batch_size", "0");

 
    }


    /**
     * Sets up the properties from a configuration in the settings.
     * @param settings is the SettingsManager containing the persistence settings.
     */
    public void configureFrom(SettingsManager settings) {
        Element root = settings.getOrCreateElement("/HibernateConfig");
        String cfgName = root.attribute("name");
        if(cfgName != null){
        	//System.out.println("Configuring from " + cfgName);
            // Name tells us which set of properties to read.
            // Find the Config element with a matching name attribute.
            boolean found = false;
            for(Element props : root.getChildren()){
                if(props.getName().equals("Config")){
                    if(props.attributeRequired("name").equals(cfgName)){
                        found = true;
                        configName = cfgName;
                        //System.out.println("Found configuration " + cfgName);
                        for(Element prop : props.getChildren()){
                            if(prop.getName().equals("Property")){
                                String name = prop.attributeRequired("name");
                                String value = prop.attributeRequired("value");
                                properties.put(name,value);
                                //System.out.println("Set " + name + " to " + value);
                            }
                        }
                    }
                }
            }
            if(!found){
                throw new IllegalStateException("Missing config " + cfgName);
            }

        }
    }
    
    /**
     * Updates the configuration in the settings manager from the current configuration.
     * @param settings
     */
    public void updateConfig(SettingsManager settings) {
        Element root = settings.getOrCreateElement("/HibernateConfig");
        root.setAttribute("name",configName);

        Element config = null;
        for(Element props : root.getChildren()){
            if(props.getName().equals("Config")){
                if(props.attributeRequired("name").equals(configName)){
                    config = props;
                    break;
                }
            }
        }

        if(config == null){
            config = settings.new Element("Config");
            config.setAttribute("name", configName);
            root.addElement(config);
        }
        
        for(Object key : properties.keySet()){
            String name = (String)key;
            String value = properties.getProperty(name);
            Element prop = settings.new Element("Property");
            prop.setAttribute("name", name);
            prop.setAttribute("value", value);
            config.addElement(prop);
        }
    }
    
    /** Selects the given configuration from the settings file.
     * @param settings is the settings file containing the settings.
     * @param name is the name of the configuration to use.
     * @throws IllegalArgumentException if name does not correspond to 
     * any of the configurations in settings.
     */
    public void useConfiguration(SettingsManager settings, String name){

    	// Numpty check....
        List<String> available = getAvailableConfigurations(settings);
        if(!available.contains(name)){
        	throw new IllegalArgumentException(name + " is not a known hibernate configuration");
        }

    	Element root = settings.getOrCreateElement("/HibernateConfig");
        root.setAttribute("name", name);
        
        configureFrom(settings);

    }
    /**
     * Looks at the settings file and gets a list of all the available hibernate 
     * configurations.
     * @param settings is the settings file containing the configurations.
     * @return a list of configuration names, maybe empty, never null.
     */
    public static List<String>getAvailableConfigurations(SettingsManager settings){
    	List<String>configNames = new LinkedList<String>();
    	
        Element root = settings.getOrCreateElement("/HibernateConfig");
        for(Element props : root.getChildren()){
            if(props.getName().equals("Config")){
                configNames.add(props.attributeRequired("name"));
            }
        }
    	return configNames;
    }
    
    /**
     * Defines the data source for the connection.
     * @author bruce.porteous
     *
     */
    public static class DataSource{
        private String name;
        private String driverClass;
        private String connectionURL;
        private String username;
        private String password;
        private String dialect;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * @return the driverClass
         */
        public String getDriverClass() {
            return driverClass;
        }
        /**
         * @param driverClass the driverClass to set
         */
        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }
        /**
         * @return the connectionURL
         */
        public String getConnectionURL() {
            return connectionURL;
        }
        /**
         * @param connectionURL the connectionURL to set
         */
        public void setConnectionURL(String connectionURL) {
            this.connectionURL = connectionURL;
        }
        /**
         * @return the username
         */
        public String getUsername() {
            return username;
        }
        /**
         * @param username the username to set
         */
        public void setUsername(String username) {
            this.username = username;
        }
        /**
         * @return the password
         */
        public String getPassword() {
            return password;
        }
        /**
         * @param password the password to set
         */
        public void setPassword(String password) {
            this.password = password;
        }
        /**
         * @return the dialect
         */
        public String getDialect() {
            return dialect;
        }
        /**
         * @param dialect the dialect to set
         */
        public void setDialect(String dialect) {
            this.dialect = dialect;
        }
    }
    
    
}

/*
 * FilterFactory.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterFactory {

    private static Map<String,Factory> factories = new HashMap<String, Factory>();
    
    static {
        register(new AndFactory());
        register(new OrFactory());
        register(new NotFactory());
        register(new IsActiveFactory());
        register(new IsAbandonedFactory());
        register(new IsCompleteFactory());
        register(new FieldFactory());
        register(new ResourceFactory());
    }
    
    private static void register(Factory factory){
        factories.put(factory.getName(), factory);
    }
    
    public static Set<String> getFilterTypes(){
        return factories.keySet();
    }
    
    public static TaskFilter newFilter(String type){
        Factory factory = factories.get(type);
        if(factory == null){
            throw new IllegalArgumentException("No task filter factory of type " + type);
        }
        return factory.create();
    }
    
    private static abstract class Factory {
        String name;
        
        Factory(String name){
            this.name =name;
        }
        
        String getName() {
            return name;
        }
        
        abstract TaskFilter create();
    }
    
    private static class AndFactory extends Factory{
        AndFactory() { super("And"); }
        TaskFilter create() { return new AndTaskFilter();}
    }
    
    private static class OrFactory extends Factory{
        OrFactory() { super("Or"); }
        TaskFilter create() { return new OrTaskFilter();}
    }

    private static class NotFactory extends Factory{
        NotFactory() { super("Not"); }
        TaskFilter create() { return new NotTaskFilter();}
    }

    private static class IsActiveFactory extends Factory{
        IsActiveFactory() { super("Active"); }
        TaskFilter create() { return new ActiveTaskFilter();}
    }
    
    private static class IsAbandonedFactory extends Factory{
        IsAbandonedFactory() { super("Abandoned"); }
        TaskFilter create() { return new IsAbandonedTaskFilter();}
    }
    
    private static class IsCompleteFactory extends Factory{
        IsCompleteFactory() { super("Complete"); }
        TaskFilter create() { return new IsCompleteTaskFilter();}
    }
    
    private static class FieldFactory extends Factory{
    	FieldFactory() { super("Field value"); }
        TaskFilter create() { return new FieldTaskFilter();}
    }
    
    
    private static class ResourceFactory extends Factory{
        ResourceFactory() { super("Resource"); }
        TaskFilter create() { return new ResourceTaskFilter();}
    }

}

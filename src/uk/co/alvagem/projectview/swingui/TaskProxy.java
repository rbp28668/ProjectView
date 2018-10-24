package uk.co.alvagem.projectview.swingui;

import uk.co.alvagem.projectview.model.Task;

/**
     * Proxy class that allows display of a task to be customised in the tree node.
     * @author bruce.porteous
     *
     */
    class TaskProxy {
    	Task task;
    	
    	TaskProxy(Task task){
    		this.task = task;
    	}
    	
    	Task getTask() {
    		return task;
    	}
    	
    	public String toString(){
    	    StringBuffer buff = new StringBuffer();
    	    boolean complete = task.getFractionComplete() == 1.0f;
    	    boolean active = task.isActive();
    	    //System.out.println(task.getName() + ": " + ((complete)?"complete":"incomplete") + ", " +((active)?"active":"inactive"));
    	    buff.append("<html>");
    	    if(complete){
    	        buff.append("<i>");
    	    }
    	    if(!active){
    	    	buff.append("<span \"style=color:red\">");
    	    }
    	    buff.append(task.getName());
    	    
//    	    buff.append(" (");
//    	    buff.append(Float.toString(getEstimatedEffort()));
//    	    buff.append(")");
    	    
    	    if(!active){
    	    	buff.append("</span>");
    	    }

            if(complete){
                buff.append("</i>");
            }
            buff.append("</html>");
    	    return buff.toString();
    	}
    }
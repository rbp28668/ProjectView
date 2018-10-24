/*
 * TaskDAO.java
 * Project: ProjectView
 * Created on 28 Dec 2007
 *
 */
package uk.co.alvagem.projectview.dao;

import java.util.List;

import uk.co.alvagem.database.GenericDAO;
import uk.co.alvagem.projectview.model.Task;

public interface TaskDAO extends GenericDAO<Task,Integer>{

    List<Task> findTopLevelTasks();

	/**
	 * @param key
	 * @return
	 */
	Task findByExternalKey(String key);
    
}

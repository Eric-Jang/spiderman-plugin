package spiderman.plugin.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.eweb4j.spiderman.plugin.DupRemovalPoint;
import org.eweb4j.spiderman.task.Task;

import spiderman.plugin.duplicate.DocIDServer;

public class DupRemovalPointImpl implements DupRemovalPoint{
	
	private Collection<String> newUrls = null;
	private Task task  = null;
	
	public void init(Task task, Collection<String> newUrls) {
		this.task = task;
		this.newUrls = newUrls;
	}
	
	public Collection<Task> removeDuplicateTask(Collection<Task> tasks){
		Collection<Task> validTasks = new ArrayList<Task>();
		for (String url : newUrls){
			Task newTask = new Task(url, task.site, 10);
			int docId = DocIDServer.getDocId(task.site.getName(), url);
			if (docId < 0){
				docId = DocIDServer.getNewDocID(task.site.getName(), url);
				validTasks.add(newTask);
			}
		}
		
		return validTasks;
	}

}

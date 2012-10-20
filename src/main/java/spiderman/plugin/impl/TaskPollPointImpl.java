package spiderman.plugin.impl;

import org.eweb4j.spiderman.plugin.TaskPollPoint;
import org.eweb4j.spiderman.task.Task;
import org.eweb4j.spiderman.xml.Site;

import spiderman.plugin.queue.TaskQueues;

public class TaskPollPointImpl implements TaskPollPoint{

	public Task pollTask(Site site) throws Exception{
		return TaskQueues.pollTask(site.getName());
	}
}

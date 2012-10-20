package spiderman.plugin.impl;

import java.util.Collection;

import org.eweb4j.spiderman.plugin.TaskSortPoint;
import org.eweb4j.spiderman.task.Task;
import org.eweb4j.spiderman.xml.Target;

import spiderman.plugin.util.Util;

public class TaskSortPointImpl implements TaskSortPoint {

	public Collection<Task> sortTasks(Collection<Task> tasks) throws Exception {
		for (Task task : tasks) {
			// 如果url不符合需求，排序调回0，否则默�?0，Queue按排序从大到小取
			Target tgt = Util.isTargetUrl(task);
			if (tgt == null)
				task.sort = 0;
		}

		return tasks;
	}

}

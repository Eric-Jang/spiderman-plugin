package spiderman.plugin.impl;

import org.eweb4j.spiderman.plugin.BeginPoint;
import org.eweb4j.spiderman.task.Task;
import org.eweb4j.util.CommonUtil;


public class BeginPointImpl implements BeginPoint{

	public Task confirmTask(Task task) throws Exception{
		if (!CommonUtil.isSameHost(task.site.getUrl(), task.url))
			return null;
		
		return task;
	}

}

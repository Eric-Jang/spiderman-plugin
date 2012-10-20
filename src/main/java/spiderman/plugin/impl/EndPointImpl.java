package spiderman.plugin.impl;

import java.util.Map;

import org.eweb4j.spiderman.plugin.EndPoint;
import org.eweb4j.spiderman.spider.Counter;
import org.eweb4j.spiderman.spider.SpiderListener;
import org.eweb4j.spiderman.spider.Spiderman;
import org.eweb4j.spiderman.task.Task;

public class EndPointImpl implements EndPoint{

	private Task task;
	private Map<String, Object> model;
	private SpiderListener listener;
	
	public void init(Task task, Map<String, Object> model, SpiderListener listener) throws Exception {
		this.task = task;
		this.model = model;
		this.listener = listener;
	}
	
	public Map<String, Object> complete(Map<String, Object> dataMap) throws Exception {
		Counter counter = Spiderman.counters.get(task.site.getName());
		counter.plus();//统计
		model.put("url", task.url);
		listener.onParse(Thread.currentThread(), task, model, Spiderman.counters.get(task.site.getName()).getCount());
		
		return dataMap;
	}

}

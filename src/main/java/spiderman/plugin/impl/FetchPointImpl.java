package spiderman.plugin.impl;

import org.eweb4j.spiderman.fetcher.FetchResult;
import org.eweb4j.spiderman.plugin.FetchPoint;
import org.eweb4j.spiderman.spider.SpiderListener;
import org.eweb4j.spiderman.task.Task;

import spiderman.plugin.util.HttpFetcher;

public class FetchPointImpl implements FetchPoint{

	private SpiderListener listener = null;
	private Task task = null;
	
	public void init(Task task, SpiderListener listener) throws Exception {
		this.task = task;
		this.listener = listener;
	}
	
	public FetchResult fetch(FetchResult result) throws Exception {
		HttpFetcher fetcher = new HttpFetcher(listener);
		
		return fetcher.fetch(task.url);
	}

}

package spiderman.plugin.impl;

import java.util.Collection;
import java.util.HashSet;

import org.eweb4j.spiderman.fetcher.FetchResult;
import org.eweb4j.spiderman.plugin.DigPoint;
import org.eweb4j.spiderman.spider.SpiderListener;
import org.eweb4j.spiderman.task.Task;

import spiderman.plugin.util.Util;

public class DigPointImpl implements DigPoint{

	private FetchResult result = null;
	private Task task = null;
	
	public void init(FetchResult result, Task task, SpiderListener listener) throws Exception {
		this.result = result;
		this.task = task;
	}
	
	public Collection<String> digNewUrls(Collection<String> urls) throws Exception {
		return this.digNewUrls(result);
	}

	private Collection<String> digNewUrls(FetchResult result) throws Exception{
		if (result == null)
			return null;
		
		Collection<String> urls = new HashSet<String>();
		String moveUrl = result.getMovedToUrl();
		
		if (moveUrl != null){
			if (!moveUrl.equals(task.url))
				urls.add(moveUrl);
		}else {
			if (result.getPage() == null) return null;
			String html = result.getPage().getContent();
			if (html == null) return null;
			
			urls.addAll(Util.findAllLinkHref(html, task.site.getUrl()));
		}
		
		return urls;
	}
	
}

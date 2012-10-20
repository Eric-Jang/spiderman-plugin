package spiderman.plugin.impl;

import java.util.Map;

import org.eweb4j.spiderman.fetcher.Page;
import org.eweb4j.spiderman.plugin.ParsePoint;
import org.eweb4j.spiderman.spider.SpiderListener;
import org.eweb4j.spiderman.xml.Model;
import org.eweb4j.spiderman.xml.Target;

import spiderman.plugin.util.ModelParser;

public class ParsePointImpl implements ParsePoint{

	private SpiderListener listener;
	private Target target ;
	private Page page;
	
	public void init(Target target, Page page, SpiderListener listener) throws Exception{
		this.target = target;
		this.page = page;
		this.listener = listener;
	}
	
	public Map<String, Object> parse(Map<String, Object> model) throws Exception {
		return parseTargetModelByXpathAndRegex();
	}

	private Map<String,Object> parseTargetModelByXpathAndRegex() throws Exception {
		Model model = target.getModel();
		Class<?> modelClass = null;
		if (model.getClazz() == null || model.getClazz().trim().length() == 0)
			modelClass = Map.class;
		else 
			modelClass = Class.forName(model.getClazz());
		
		ModelParser parser = new ModelParser(target, listener);
		Map<String, Object> map = parser.parse(page.getContent());
		
		return map;
	}
}

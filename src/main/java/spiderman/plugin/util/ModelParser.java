package spiderman.plugin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eweb4j.spiderman.spider.SpiderListener;
import org.eweb4j.spiderman.xml.Field;
import org.eweb4j.spiderman.xml.Target;
import org.eweb4j.util.CommonUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class ModelParser{

	private Target target = null;
	private SpiderListener listener = null;
	
	public ModelParser(Target target, SpiderListener listener) {
		this.target = target;
		this.listener = listener;
	}
	
	public Map<String,Object> parse(String html) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		final List<Field> fields = target.getModel().getField();
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode rootNode = cleaner.clean(html);
		for (Field field : fields){
			String key = field.getName();
			String xpath = field.getParser().getXpath();
			String attribute = field.getParser().getAttribute();
			String regex = field.getParser().getRegex();
			String isArray = field.getIsArray();
			Object[] nodeVals;
			try {
				nodeVals = rootNode.evaluateXPath(xpath);
				if (nodeVals == null || nodeVals.length == 0)
					continue;
				
				Collection<Object> value = new ArrayList<Object>();
				if (attribute != null && attribute.trim().length() > 0){
					for (Object nodeVal : nodeVals){
						TagNode node = (TagNode)nodeVal;
						value.add(node.getAttributeByName(attribute));
					}
				}else 
					value.addAll(Arrays.asList(nodeVals));
				
				if (regex != null && regex.trim().length() > 0){
					Collection<String> inputs = new ArrayList<String>(value.size());
					for (Object obj : value){
						String input = String.valueOf(obj);
						String val = CommonUtil.findOneByRegex(input, regex);
						if (val != null)
							input = val;
						inputs.add(input);
					}
					
					if (!inputs.isEmpty()){
						value.clear();
						value.addAll(inputs);
					}
				}
				
				if ("1".equals(isArray))
					map.put(key, value);
				else
					map.put(key, new ArrayList<Object>(value).get(0));
				
			} catch (XPatherException e) {
				listener.onError(Thread.currentThread(), e.toString(), e);
				continue;
			}
		}
		
		return map;
	}

}

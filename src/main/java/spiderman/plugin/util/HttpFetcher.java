package spiderman.plugin.util;

import org.eweb4j.spiderman.fetcher.FetchResult;
import org.eweb4j.spiderman.spider.Settings;
import org.eweb4j.spiderman.spider.SpiderListener;


public class HttpFetcher {

	private SpiderListener listener = null;
	
	public HttpFetcher(SpiderListener listener) {
		this.listener = listener;
	}
	
	public FetchResult fetch(String url) throws Exception{
		if (url == null)
			throw new Exception("fetcher url required ");
		final int maxRetryTimes = Settings.http_fetch_retry();
		int retryTimes = 0;
		while (true) {
			try {
				return HttpUtil.fetch(url);
			} catch (Throwable e) {
				if (retryTimes > maxRetryTimes)
					return null;
				retryTimes++;
				listener.onInfo(Thread.currentThread(), "retry -> " + retryTimes + "/"+maxRetryTimes + " url -> " + url);
				Thread.sleep(Settings.http_fetch_timeout() * 1000);
			}
		}
	}

}

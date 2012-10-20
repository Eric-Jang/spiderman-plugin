package spiderman.plugin.util;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eweb4j.spiderman.fetcher.FetchResult;
import org.eweb4j.spiderman.fetcher.Page;


public class HttpUtil {

	public static FetchResult fetch(String url) throws Exception{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
		paramsBean.setVersion(HttpVersion.HTTP_1_1);
		paramsBean.setContentCharset("UTF-8");
		paramsBean.setUseExpectContinue(false);
		params.setBooleanParameter("http.protocol.handle-redirects", false);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				Header contentEncoding = entity.getContentEncoding();
				if (contentEncoding != null) {
					HeaderElement[] codecs = contentEncoding.getElements();
					for (HeaderElement codec : codecs) {
						if (codec.getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}

		});

		final FetchResult fetchResult = new FetchResult();
		HttpEntity entity = null;
		HttpGet get = null;
		try {
			get = new HttpGet(url);
			get.addHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(get);
			entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header header = response.getFirstHeader("Location");
				if (header == null)
					throw new Exception("can not found any location to move case status code is 30X");
				
				String movedToUrl = header.getValue();
				movedToUrl = URLCanonicalizer.getCanonicalURL(movedToUrl, url);
				fetchResult.setMovedToUrl(movedToUrl);
				fetchResult.setStatusCode(statusCode);
				
				Page page = new Page(fetchResult.getFetchedUrl());
				load(page, entity);
				fetchResult.setPage(page);
				
				return fetchResult;
			}
				
			fetchResult.setFetchedUrl(url);
			String uri = get.getURI().toString();
			if (!uri.equals(url) && !URLCanonicalizer.getCanonicalURL(uri).equals(url)) 
					fetchResult.setFetchedUrl(uri);

			if (entity != null) {
				long size = entity.getContentLength();
				if (size == -1) {
					Header length = response.getLastHeader("Content-Length");
					if (length == null) {
						length = response.getLastHeader("Content-length");
					}
					if (length != null) {
						size = Integer.parseInt(length.getValue());
					} else {
						size = -1;
					}
				}

				fetchResult.setStatusCode(HttpStatus.SC_OK);
				
				Page page = new Page(fetchResult.getFetchedUrl());
				load(page, entity);
				fetchResult.setPage(page);
				
				return fetchResult;

			} else {
				get.abort();
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (entity == null && get != null) get.abort();
				
			} catch (Exception e) {
			}
			
			get.releaseConnection();
		}
		
		Page page = new Page(fetchResult.getFetchedUrl());
		load(page, entity);
		fetchResult.setPage(page);
		
		return fetchResult;
	}

	public static void load(Page page, HttpEntity entity) throws Exception {
		if (entity == null || page == null)
			return ;
		
		Header type = entity.getContentType();
		if (type != null) {
			page.setContentType(type.getValue());
		}

		Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			page.setContentEncoding(encoding.getValue());
		}

		page.setContentCharset(EntityUtils.getContentCharSet(entity));
//		page.setContentData(EntityUtils.toByteArray(entity));
		page.SetContent(EntityUtils.toString(entity));
	}
	
}

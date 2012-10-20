package spiderman.plugin.duplicate;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eweb4j.ioc.IOC;
import org.eweb4j.util.CommonUtil;
import org.eweb4j.util.FileUtil;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;

public class DocIDServer {

	protected static final Log logger = LogFactory.getLog(DocIDServer.class);

	protected static Map<String, Database> dbs = new Hashtable<String, Database>();

	protected static final Object mutex = new Object();

	protected static int lastDocID = 0;
	
	static {
		File dbEnv = IOC.getBean("file");
		if (!dbEnv.exists())
			throw new RuntimeException("dbEnv folder -> " + dbEnv.getAbsolutePath() + " not found !");
		
		for (File f : dbEnv.listFiles()){
			boolean flag = FileUtil.deleteFile(f);
			if (!flag)
				throw new RuntimeException("file -> " + f.getAbsolutePath() + " can not delete !");
			
		}
	}
	
	private static Database getDb(String dbId) {
		if (!dbs.containsKey(dbId)){
			
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			Environment env = IOC.getBean("env");
			dbs.put(dbId, env.openDatabase(null, dbId, dbConfig));
			lastDocID = 0;
		}
		
		return dbs.get(dbId);
	}

	/**
	 * Returns the docid of an already seen url.
	 * 
	 * @param url
	 *            the URL for which the docid is returned.
	 * @return the docid of the url if it is seen before. Otherwise -1 is
	 *         returned.
	 */
	public static int getDocId(String dbId, String url) {
		synchronized (mutex) {
			OperationStatus result;
			DatabaseEntry value = new DatabaseEntry();
			try {
				DatabaseEntry key = new DatabaseEntry(url.getBytes());
				result = getDb(dbId).get(null, key, value, null);

				if (result == OperationStatus.SUCCESS
						&& value.getData().length > 0) {
					return CommonUtil.byteArray2Int(value.getData());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}
	}

	public static int getNewDocID(String dbId, String url) {
		synchronized (mutex) {
			try {
				// Make sure that we have not already assigned a docid for this
				// URL
				int docid = getDocId(dbId, url);
				if (docid > 0) {
					return docid;
				}

				lastDocID++;
				getDb(dbId).put(null, new DatabaseEntry(url.getBytes()), new DatabaseEntry(CommonUtil.int2ByteArray(lastDocID)));
				return lastDocID;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}
	}

	public static void addUrlAndDocId(String dbId, String url, int docId) throws Exception {
		synchronized (mutex) {
			if (docId <= lastDocID) {
				throw new Exception("Requested doc id: " + docId + " is not larger than: " + lastDocID);
			}
			
			// Make sure that we have not already assigned a docid for this URL
			int prevDocid = getDocId(dbId, url);
			if (prevDocid > 0) {
				if (prevDocid == docId) {
					return;
				}
				throw new Exception("Doc id: " + prevDocid + " is already assigned to URL: " + url);
			}
			
			getDb(dbId).put(null, new DatabaseEntry(url.getBytes()), new DatabaseEntry(CommonUtil.int2ByteArray(docId)));
			lastDocID = docId;
		}
	}
	
	public static boolean isSeenBefore(String dbId, String url) {
		return getDocId(dbId, url) != -1;
	}

	public static int getDocCount(String dbId) {
		try {
			return (int) getDb(dbId).count();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void sync(String dbId) {
		try {
			getDb(dbId).sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public static void close(String dbId) {
		try {
			getDb(dbId).close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}

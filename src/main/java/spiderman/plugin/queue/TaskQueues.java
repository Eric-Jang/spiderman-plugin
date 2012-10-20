package spiderman.plugin.queue;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.PriorityBlockingQueue;

import org.eweb4j.spiderman.task.Task;
import org.eweb4j.spiderman.url.UrlRuleChecker;


public class TaskQueues {

	private static Hashtable<String, PriorityBlockingQueue<Task>> queueList = new Hashtable<String,PriorityBlockingQueue<Task>>();
	
	public static Task pollTask(String name) throws Exception{
		return getQueue(name).poll();
	}
	
	public static boolean pushTask(Task task){
		synchronized (queueList) {
			if (task == null)
				return false;
			
			// �ж���ЧURL
			if (null == task.url || task.url.trim().length() == 0)
				return false;
			
			//�ж�URL�Ƿ��ܽ������
			if (!UrlRuleChecker.check(task.url, task.site.getQueueRules().getRule()))
				return false;
			
			return getQueue(task.site.getName()).add(task);
		}
	}
	
	
	private static PriorityBlockingQueue<Task> getQueue(String name){
		synchronized (queueList) {
			if (!queueList.containsKey(name)){
				PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<Task>(11, order);
				queueList.put(name, queue);
			}
			
			return queueList.get(name);
		}
	}
	
	private static Comparator<Task> order =  new Comparator<Task>(){
		public int compare(Task t1, Task t2) {
			//ע�����������ȼ���1 < 0 < -1 
			if (t1.sort == t2.sort) return 0;
			return t1.sort < t2.sort ? 1 : -1;
		}
	};
}

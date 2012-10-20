package spiderman.plugin;

import org.eweb4j.spiderman.plugin.BeginPoint;
import org.eweb4j.spiderman.task.Task;

public class Hello111 implements BeginPoint{

	public Task confirmTask(Task task) {
		System.out.println("Hello, 我是 hello111 我拿到的 task 是 -> " + task);
		task.sort = -5;
		System.out.println("我将要返回 task ->" + task);
		return task;
	}

}

package spiderman.plugin;

import org.eweb4j.spiderman.plugin.BeginPoint;
import org.eweb4j.spiderman.task.Task;

public class Hello222 implements BeginPoint{

	public Task confirmTask(Task task) {
		System.out.println("Hello, 我是 hello222 我拿到的task是 -> " + task);
		return task;
	}

}

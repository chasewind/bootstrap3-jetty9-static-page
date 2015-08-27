package com.belief.test.task;

import java.util.concurrent.Callable;

public class SinaTaskhandler extends Taskhandler<MicroUrl> implements Callable<String> {

	public SinaTaskhandler(MicroUrl target) {
		super(target);
	}

	@Override
	public String call() throws Exception {
		return null;
	}

}

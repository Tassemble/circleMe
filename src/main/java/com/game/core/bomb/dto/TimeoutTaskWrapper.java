package com.game.core.bomb.dto;

import java.util.concurrent.Future;

import com.game.core.bomb.play.dto.FastJoinTimeoutCallback;

/**
 * @author CHQ
 * @date 2014年2月3日
 * @since 1.0
 */
public class TimeoutTaskWrapper {
	Future<?>	future;
	FastJoinTimeoutCallback		thread;
	
	

	public TimeoutTaskWrapper(Future<?> future, FastJoinTimeoutCallback thread) {
		super();
		this.future = future;
		this.thread = thread;
	}

	public Future<?> getFuture() {
		return future;
	}

	public void setFuture(Future<?> future) {
		this.future = future;
	}


	public Long getThreadId() {
		if (thread == null) {
			return -1L;
		}
		return thread.getCurrentThreadId();
	}
	
	public boolean halt() {
		if (future == null) {
			return false;
		}
		if (future.isDone() || future.isCancelled()) {
			return false;
		}
		
		return future.cancel(true);
	}

}

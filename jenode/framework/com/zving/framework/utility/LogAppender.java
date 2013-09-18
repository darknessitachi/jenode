package com.zving.framework.utility;

import com.zving.framework.collection.Queuex;

public class LogAppender {
	private static long id = 0L;
	private static Queuex<LogMessage> queue = new Queuex(200);

	public void add(String message) {
		id += 1L;
		LogMessage lm = new LogMessage();
		lm.id = id;
		lm.message = (message + "\n");
		queue.push(lm);
	}

	public static StringBuffer getLog(long id) {
		StringBuffer msg = new StringBuffer();
		for (int i = 0; i < queue.size(); i++) {
			LogMessage lm = (LogMessage) queue.get(i);
			if (lm.id > id) {
				msg.append(lm.message);
			}
		}
		return msg;
	}

	public static long getMaxId() {
		return id;
	}

	private class LogMessage {
		long id;
		String message;

		private LogMessage() {
		}
	}
}
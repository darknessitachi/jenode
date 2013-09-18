package com.zving.framework.utility;

import com.zving.framework.Current;
import java.util.ArrayList;

public class Errorx {
	protected ArrayList<Message> list = new ArrayList();

	protected boolean ErrorFlag = false;

	protected boolean ErrorDealedFlag = true;

	public static void addMessage(String message) {
		add(message, false);
	}

	public static void addError(String message) {
		add(message, true);
	}

	private static void add(String message, boolean isError) {
		Message msg = new Message();
		msg.isError = isError;
		msg.Message = message;
		if (isError) {
			getCurrent().ErrorFlag = true;
			getCurrent().ErrorDealedFlag = false;
			StackTraceElement[] stack = new Throwable().getStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("Errorx : ");
			sb.append(message);
			sb.append("\n");
			for (int i = 2; i < stack.length; i++) {
				StackTraceElement ste = stack[i];
				if (ste.getClassName().indexOf("DBConnPool") == -1) {
					sb.append("\tat ");
					sb.append(ste.getClassName());
					sb.append(".");
					sb.append(ste.getMethodName());
					sb.append("(");
					sb.append(ste.getFileName());
					sb.append(":");
					sb.append(ste.getLineNumber());
					sb.append(")\n");
				}
			}
			msg.StackTrace = sb.toString();
		}
		getCurrent().list.add(msg);
	}

	public static ArrayList<Message> getErrors() {
		return getCurrent().list;
	}

	public static boolean hasError() {
		return getCurrent().ErrorFlag;
	}

	public static boolean hasDealed() {
		return getCurrent().ErrorDealedFlag;
	}

	public static void clear() {
		getCurrent().list.clear();
		getCurrent().ErrorFlag = false;
		getCurrent().ErrorDealedFlag = true;
	}

	public static String printString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (int j = 1; i < getCurrent().list.size(); i++) {
			Message msg = (Message) getCurrent().list.get(i);
			if (msg.isError) {
				sb.append("Error:");
				sb.append(msg.Message);
				sb.append("<br>\n");
				j++;
			}
		}
		for (i = 0; i < getCurrent().list.size(); i++) {
			Message msg = (Message) getCurrent().list.get(i);
			if (!msg.isError) {
				sb.append("Warning:");
				sb.append(msg.Message);
				sb.append("\n");
			}
		}
		getCurrent().ErrorDealedFlag = true;
		return sb.toString();
	}

	public static String[] getMessages() {
		String[] arr = new String[getCurrent().list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ((Message) getCurrent().list.get(i)).Message;
		}
		clear();
		return arr;
	}

	public static String getAllMessage() {
		FastStringBuilder sb = new FastStringBuilder();
		int index = 1;
		for (int i = 0; i < getCurrent().list.size(); i++) {
			Message msg = (Message) getCurrent().list.get(i);
			if (msg.isError) {
				sb.append("\n").append(index).append(". Error: ")
						.append(msg.Message);
				index++;
			} else {
				sb.append("\n").append(index).append(". Warning: ")
						.append(msg.Message);
				index++;
			}
		}
		clear();
		return sb.toStringAndClose();
	}

	public static Errorx getCurrent() {
		return Current.getErrorx();
	}

	public static class Message {
		public boolean isError;
		public String Message;
		public String StackTrace;
	}
}
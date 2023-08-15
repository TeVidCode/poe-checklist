package org.tevid.todo_list.log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class LoggingService {

	private static LoggingService instance;

	public static LoggingService getInstance() {
		if (instance == null)
			instance = new LoggingService();
		return instance;
	}

	private LinkedList<String> logs = new LinkedList<String>();
	private LinkedList<LogChangeListener> listeners = new LinkedList<LoggingService.LogChangeListener>();

	private LoggingService() {
	}

	public void addLogChangeListener(LogChangeListener listener) {
		listeners.add(listener);
	}

	public void removeLogChangeListener(LogChangeListener listener) {
		listeners.remove(listener);
	}

	public void logInfo(String text) {
		log(LogLevel.INFO, text);
	}

	public void logWarning(String text) {
		log(LogLevel.WARNING, text);
	}

	public void logError(String text) {
		log(LogLevel.INFO, text);
	}

	private void log(LogLevel level, String text) {
		StringBuilder builder = new StringBuilder();

		builder.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		builder.append(" ");
		builder.append(level.name());
		builder.append(" ");
		builder.append(text);

		logs.add(builder.toString());

		for (LogChangeListener listener : listeners) {
			listener.onLogAdded(builder.toString());
		}
	}

	public static interface LogChangeListener {
		public void onLogAdded(String text);

	}

	public static enum LogLevel {
		INFO, WARNING, ERROR
	}
}

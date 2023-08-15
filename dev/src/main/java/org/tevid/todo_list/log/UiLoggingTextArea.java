package org.tevid.todo_list.log;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class UiLoggingTextArea extends JScrollPane implements LoggingService.LogChangeListener {

	private static final long serialVersionUID = 1L;
	@Getter
	private JTextArea textArea = new JTextArea();

	public UiLoggingTextArea() {
		setViewportView(textArea);
		textArea.setEditable(false);
		LoggingService.getInstance().addLogChangeListener(this);
	}

	@Override
	public void onLogAdded(String text) {
		textArea.append(text + System.lineSeparator());
	}

}

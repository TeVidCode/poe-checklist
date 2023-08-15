package org.tevid.todo_list.web;

import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import org.tevid.todo_list.key_listener.UiShortcutPanel;
import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.utils.ConfigService;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class UiWebPanel extends JPanel {

	private ConfigService config = ConfigService.getInstance();
	private WebService webService = WebService.getInstance();
	private LoggingService loggingService = LoggingService.getInstance();
	
	private static final long serialVersionUID = 1L;
	private JButton btnStartStop;
	private JButton open;
	private JFormattedTextField port;	

	public UiWebPanel() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		this.add(new JLabel("Port:"));
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		
		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of focus lost
		formatter.setCommitsOnValidEdit(true);
		
		
		port = new JFormattedTextField(formatter);
		port.setMaximumSize(new Dimension(75, port.getPreferredSize().height));		
		port.setText(String.valueOf(config.getPort()));
		
		this.add(port);
		
		this.add(Box.createRigidArea(new Dimension(20, 0)));		
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(e -> startStop());
		this.add(btnStartStop);	
	
		this.add(Box.createRigidArea(new Dimension(5, 0)));		
		open = new JButton("Open Todos");
		open.addActionListener(e -> webService.open());
		open.setEnabled(false);
		this.add(open);
		
		this.add(Box.createGlue());
		this.add(new UiShortcutPanel());
		

		
	}
	
	private void startStop() {
		if(webService.hasStarted()) {
			btnStartStop.setText("Start");
			port.setEditable(true);
			open.setEnabled(false);
			webService.stop();
			loggingService.logInfo("Webserver stopped");
		}else {
			String newPort = port.getText();
			config.setPort(Integer.parseInt(newPort));
			
			btnStartStop.setText("Stop");
			port.setEditable(false);
			open.setEnabled(true);
			webService.start();
			loggingService.logInfo("Webserver started");
		}
		
		
		
	}

}

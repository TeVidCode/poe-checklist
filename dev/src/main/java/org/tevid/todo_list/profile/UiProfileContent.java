package org.tevid.todo_list.profile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.todo.ProgressService;
import org.tevid.todo_list.todo.TodoService;
import org.tevid.todo_list.utils.ConfigService;

public class UiProfileContent extends JPanel implements ProfileChangeListener {
	private static final long serialVersionUID = 1L;

	private TodoService todoService = TodoService.getInstance();
	private LoggingService loggingService = LoggingService.getInstance();
	private ProgressService progressService = ProgressService.getInstance();
	private ConfigService configService = ConfigService.getInstance();
	private ProfileService profileService = ProfileService.getInstance();
	private Profile profile = null;

	private List<ProfileChangeListener> listeners = new LinkedList<ProfileChangeListener>();

	public UiProfileContent() {
		setLayout(new BorderLayout());
		JPanel actions = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbl.setConstraints(actions, gbc);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(5, 0, 5, 5);

		actions.setLayout(gbl);
		this.add(actions, BorderLayout.PAGE_START);

		Component space = Box.createRigidArea(new Dimension(10, 40));
		gbc.gridx = 0;
		gbc.gridy = 0;
		actions.add(space, gbc);

		JButton setActive = createButton(gbc, 1, "Set Active");
		setActive.addActionListener(e -> setActive());
		actions.add(setActive, gbc);

		JButton resetProgress = createButton(gbc, 2, "Reset");
		resetProgress.addActionListener(e -> reset());
		actions.add(resetProgress, gbc);

		JButton editTodos = createButton(gbc, 3, "Edit Todos");
		editTodos.addActionListener(e -> todoService.edit(profile));
		actions.add(editTodos, gbc);

		addLabel(gbc, actions, 1, "Sets the profile to active. They will show up in the web view.");
		addLabel(gbc, actions, 2,
				"Removes the todo list by removing all progress. Also reloads todos when the todo list changed");
		addLabel(gbc, actions, 3, "Edit the todo list");

		this.setVisible(false);
		if (configService.getActiveProfile() != null) {
			List<Profile> profiles = profileService.getProfiles();
			for (Profile profile : profiles) {
				if (profile.getName().equals(configService.getActiveProfile())) {
					progressService.load(profile);
					this.setVisible(true);
					this.profile = profile;
					loggingService.logInfo("Loaded last active profile " + profile.getName());
				}
			}
		}
	}

	private JButton createButton(GridBagConstraints gbc, int y, String name) {
		JButton button = new JButton(name);
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.weightx = 0.1; // Allow horizontal growth
		gbc.weighty = 0.0; // Allow vertical growth
		gbc.fill = GridBagConstraints.NONE; // Fill both horizontally and vertically
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(5, 5, 5, 0);

		return button;
	}

	private void addLabel(GridBagConstraints gbc, JPanel parent, int y, String text) {

		JTextArea ta = new JTextArea(text);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setOpaque(false); // Make the background transparent

		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.weightx = 1.0; // Allow horizontal growth
		gbc.weighty = 0; // Allow vertical growth
		gbc.fill = GridBagConstraints.HORIZONTAL; // Fill both horizontally and vertically
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 0, 5, 5);
		parent.add(ta, gbc);
	}

	private void reset() {
		int ret = JOptionPane.showConfirmDialog(this,
				"<html>Are you sure you want to rest all the todo progress?</html>", "Reset Progress?",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (ret == JOptionPane.YES_OPTION) {
			progressService.reset(profile);
			loggingService.logInfo("Reset progress of profile " + profile.getName());
		}
	}

	private void setActive() {
		if (profile == null)
			return;
		configService.setActiveProfile(profile.getName());
		progressService.load(profile);
		for (ProfileChangeListener listener : listeners)
			listener.onProfileSetActive(profile);
		loggingService.logInfo("Set profile " + profile.getName() + " to active");
	}

	@Override
	public void onProfileSelected(Profile profile) {
		if (profile == null) {
			this.setVisible(false);
			this.profile = null;
		} else {
			this.profile = profile;
			this.setVisible(true);
		}
	}

	@Override
	public void onProfileSetActive(Profile profile) {

	}

	public void addProfileChangeListener(ProfileChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeProfileChangeListener(ProfileChangeListener listener) {
		this.listeners.remove(listener);
	}

}

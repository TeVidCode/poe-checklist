package org.tevid.todo_list.profile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.profile.ProfileService.DuplicateProfileException;
import org.tevid.todo_list.profile.ProfileService.InvalidProfileNameException;
import org.tevid.todo_list.utils.ConfigService;
import org.tevid.todo_list.utils.UiIcons;

public class UiProfileList extends JPanel implements ProfileChangeListener {

	private static final long serialVersionUID = 1L;

	private ProfileService profileService = ProfileService.getInstance();
	private LoggingService loggingService = LoggingService.getInstance();
	private ConfigService configService = ConfigService.getInstance();

	private JList<Profile> profileList = new JList<Profile>();
	private List<ProfileChangeListener> listeners = new LinkedList<ProfileChangeListener>();

	public UiProfileList() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(controls, BorderLayout.PAGE_START);

		JButton add = new JButton(UiIcons.add);
		add.addActionListener(e -> add());
		controls.add(add);

		JButton rename = new JButton(UiIcons.edit);
		rename.addActionListener(e -> rename());
		controls.add(rename);

		JButton refresh = new JButton(UiIcons.refresh);
		refresh.addActionListener(e -> refresh(null));
		controls.add(refresh);

		JButton delete = new JButton(UiIcons.delete);
		controls.add(delete);
		delete.addActionListener(e -> delete());

		DefaultListModel<Profile> listModel = new DefaultListModel<Profile>();
		listModel.addAll(profileService.getProfiles());
		profileList.setModel(listModel);
		profileList.setCellRenderer(new ProfileListRenderer());
		profileList.setAlignmentX(Component.LEFT_ALIGNMENT);
		profileList.setBorder(new EmptyBorder(5, 2, 5, 2));
		profileList.addListSelectionListener(e -> {
			if (profileList.getSelectedValue() != null) {
				for (ProfileChangeListener listener : listeners) {
					listener.onProfileSelected(profileList.getSelectedValue());
				}
			}
		});

		Profile selectedProfile = null;
		if (configService.getActiveProfile() != null) {
			for (int i = 0; i < listModel.size(); i++) {
				Profile profile = listModel.get(i);
				if (profile.getName().equals(configService.getActiveProfile())) {
					selectedProfile = profile;
				}
			}
		}	

		profileList.setSelectedValue(selectedProfile, true);

		JScrollPane scrollPane = new JScrollPane(profileList);

		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void delete() {
		Profile profile = profileList.getSelectedValue();
		if (profile == null)
			return;
		int ret = JOptionPane.showConfirmDialog(this,
				"<html>Are you sure you want to delete the profile:&nbsp;<b>" + profile.getName() + "</b>?</html>",
				"Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (ret == JOptionPane.YES_OPTION) {
			try {
				profileService.deleteProfile(profile);
				loggingService.logInfo("Deleted profile " + profile.getName());
				refresh(null);
			} catch (IOException e) {
				loggingService.logError("Error deleting profile: " + profile.getName());
			}
		}
	}

	private void refresh(String selectedName) {
		SwingUtilities.invokeLater(() -> {
			DefaultListModel<Profile> listModel = new DefaultListModel<Profile>();
			listModel.addAll(profileService.getProfiles());
			profileList.setModel(listModel);

			boolean setSelected = false;
			if (selectedName != null) {
				
				for (int i = 0; i < profileList.getModel().getSize(); i++) {
					Profile profile = profileList.getModel().getElementAt(i);
					if (profile.getName().equals(selectedName)) {
						profileList.setSelectedIndex(i);
						for (ProfileChangeListener listener : listeners) {						
							listener.onProfileSelected(profile);
						}
						setSelected =true;
						break;
					}
				}
			} 
			if(setSelected == false){
				for (ProfileChangeListener listener : listeners) {
					listener.onProfileSelected(null);
				}
			}
		});
	}

	private void add() {
		String name = JOptionPane.showInputDialog(this, "Enter a name for this profile:", "Profile Name",
				JOptionPane.PLAIN_MESSAGE);
		if (name != null) {
			try {
				profileService.createProfile(name);
				refresh(name);
			} catch (InvalidProfileNameException e) {
				loggingService.logError("The profile name must only contains letters, numbers, underscores and dashes");
			} catch (IOException e) {
				loggingService.logError("An unknown error occured");

			} catch (DuplicateProfileException e) {
				loggingService.logError("A profile with this name already exists");
			}
		}

	}

	private void rename() {
		Profile profile = profileList.getSelectedValue();
		if (profile == null)
			return;

		String name = JOptionPane.showInputDialog(this,
				"<html>Enter a new name for the profile:&nbsp;<b>" + profile.getName() + "</b></html>",
				"Rename profile", JOptionPane.PLAIN_MESSAGE);
		if (name != null) {
			try {
				profileService.updateName(profile, name);
				refresh(name);
			} catch (InvalidProfileNameException e) {
				loggingService.logError("The profile name must only contains letters, numbers, underscores and dashes");
			} catch (DuplicateProfileException e) {
				loggingService.logError("A profile with this name already exists");
			}
		}
	}

	public void addProfileChangeListener(ProfileChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeProfileChangeListener(ProfileChangeListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onProfileSelected(Profile profile) {

	}

	@Override
	public void onProfileSetActive(Profile profile) {
		refresh(profile.getName());
	}

}

package org.tevid.todo_list.profile;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.tevid.todo_list.utils.ConfigService;

public class ProfileListRenderer extends JLabel implements ListCellRenderer<Profile> {

	private static final long serialVersionUID = 1L;

	private ConfigService config = ConfigService.getInstance();

	public ProfileListRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Profile> list, Profile value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		Font f = getFont();
		if (value.getName().equals(config.getActiveProfile())) {
			f = f.deriveFont(f.getStyle() | Font.BOLD);
			f = f.deriveFont(f.getStyle() & ~Font.ITALIC);
			
		}else {
			f = f.deriveFont(f.getStyle() & ~Font.BOLD);
			f = f.deriveFont(f.getStyle() | Font.ITALIC);
		}
		setFont(f);
		setText(value.getName());
		return this;
	}
}

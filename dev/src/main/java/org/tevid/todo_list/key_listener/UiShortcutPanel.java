package org.tevid.todo_list.key_listener;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.tevid.todo_list.utils.ConfigService;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class UiShortcutPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private ConfigService configService = ConfigService.getInstance();

	public UiShortcutPanel() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.add(new JLabel("next: "));		
		JButton nextKey = new JButton(NativeKeyEvent.getKeyText(configService.getNextkey()));
		nextKey.addActionListener(e -> {
			int keyCode = getKeyDialog(configService.getNextkey());
			configService.setNextKey(keyCode);
			nextKey.setText(NativeKeyEvent.getKeyText(keyCode));
		});
		this.add(nextKey);
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		
		this.add(new JLabel("previous: "));		
		JButton previousKey = new JButton(NativeKeyEvent.getKeyText(configService.getPreviousKey()));
		previousKey.addActionListener(e -> {
			int keyCode = getKeyDialog(configService.getNextkey());			
			configService.setPreviousKey(keyCode);
			previousKey.setText(NativeKeyEvent.getKeyText(keyCode));
		});
		this.add(previousKey);
	}
	
	public int getKeyDialog(int keyCode) {
		SetKeyButton[] buttons = new SetKeyButton[1];
		buttons[0] = new SetKeyButton(keyCode);				
		int confirm = JOptionPane.showConfirmDialog(this, buttons, "Set shortcut", JOptionPane.YES_NO_OPTION);
		buttons[0].removeKeyListener(); // just to make sure nothing is listening anymore
		System.out.println(buttons[0].getKeyCode());
		if(confirm == JOptionPane.YES_OPTION)
			return buttons[0].getKeyCode();
		else
			return keyCode;
				
	}	
}

package org.tevid.todo_list.key_listener;

import javax.swing.JButton;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import lombok.Getter;

public class SetKeyButton extends JButton implements NativeKeyListener {
	
	private static final long serialVersionUID = 1L;
	private boolean isListening = false;
	@Getter
	private int keyCode;
	
	public SetKeyButton(int key) {
		this.addActionListener(e -> onClick());
		this.keyCode = key;
		onClick();
	}
	
	
	public void onClick() {
		
		if(isListening == false) {
			this.setText("Waiting for key press");
			GlobalScreen.addNativeKeyListener(this);
			isListening = true;
		}else {
			isListening = false;
			removeKeyListener();
			this.setText(NativeKeyEvent.getKeyText(keyCode));
			
		}		
	}
	
	public void nativeKeyReleased(NativeKeyEvent e) {		
		this.keyCode = e.getKeyCode();
		this.setText(NativeKeyEvent.getKeyText(keyCode));
		removeKeyListener();
		isListening = false;
	}
	
	public void removeKeyListener() {
		GlobalScreen.removeNativeKeyListener(this);		
	}
}

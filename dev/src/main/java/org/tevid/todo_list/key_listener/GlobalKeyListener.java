package org.tevid.todo_list.key_listener;

import org.tevid.todo_list.todo.ProgressService;
import org.tevid.todo_list.utils.ConfigService;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

	private ProgressService progressService = ProgressService.getInstance();
	private ConfigService configService = ConfigService.getInstance();

	
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == configService.getNextkey()) {
			progressService.markDone();

		} else if (e.getKeyCode() == configService.getPreviousKey()) {
			progressService.markUnDone();

		}
	}


}

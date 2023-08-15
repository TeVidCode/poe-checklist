package org.tevid.todo_list;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.LogManager;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.tevid.todo_list.key_listener.GlobalKeyListener;
import org.tevid.todo_list.log.UiLoggingTextArea;
import org.tevid.todo_list.profile.ProfileService;
import org.tevid.todo_list.profile.UiProfileContent;
import org.tevid.todo_list.profile.UiProfileList;
import org.tevid.todo_list.utils.PathService;
import org.tevid.todo_list.web.UiWebPanel;

import com.beust.jcommander.JCommander;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;


public class Program {
	

	public static void main(String[] rawArgs) throws SecurityException, IOException {
		
		
		ProgramArgs args= new ProgramArgs();
		JCommander commander = JCommander.newBuilder() //
				.addObject(args) //
				.build();		
		commander.parse(rawArgs);
		
		PathService.getInstance().setRootDir(args.getRootDir());
		
		Path logConfigFile = PathService.getInstance().getRootDir().resolve("logging.properties");
	
		LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile.toFile()));

		FlatLightLaf.setup();
		
		JFrame jFrame = new JFrame();
		jFrame.setSize(800, 600);		
		
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		jFrame.add(container);
		
		UiWebPanel webControls = new UiWebPanel();
		container.add(webControls, BorderLayout.PAGE_START);
		
		
		UiProfileList profileList = new UiProfileList();
		profileList.setPreferredSize(new Dimension(200, 0));
		container.add(profileList, BorderLayout.LINE_START);	
		
		UiLoggingTextArea textArea = new UiLoggingTextArea();
		textArea.setPreferredSize(new Dimension(0, 100));
		container.add(textArea, BorderLayout.PAGE_END);

		
		UiProfileContent profileContent = new UiProfileContent();
		profileList.addProfileChangeListener(profileContent);
		profileContent.addProfileChangeListener(profileList);
		container.add(profileContent, BorderLayout.CENTER);
		
		jFrame.setVisible(true);	
	
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
		
		
		ProfileService.getInstance();
		
	}

}

package org.tevid.todo_list.web;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.util.http.fileupload.ProgressListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.tevid.todo_list.todo.ProgressService;
import org.tevid.todo_list.todo.ProgressService.TodoChangeListener;

import lombok.extern.java.Log;

@Component
@Log
public class SocketHandler extends TextWebSocketHandler implements TodoChangeListener {

	private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	
	public SocketHandler() {
		ProgressService.getInstance().addChangeListener(this);
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		
		log.info("received from session " + session.getId() + " message: " + message.getPayload());		
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//the messages will be broadcasted to all users.
		sessions.add(session);
	}

	@Override
	public void onChange() {
		for(WebSocketSession session: sessions) {			
			try {
				if(session.isOpen() == false)
					continue;
				session.sendMessage(new TextMessage("CHANGE"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

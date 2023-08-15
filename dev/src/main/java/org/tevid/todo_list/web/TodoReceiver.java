package org.tevid.todo_list.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tevid.todo_list.profile.ProfileService;
import org.tevid.todo_list.todo.ProgressService;
import org.tevid.todo_list.todo.Todo;

import jdk.internal.org.jline.utils.Log;

@RestController()
public class TodoReceiver {

	private ProgressService progressService = ProgressService.getInstance();
	private ProfileService profileService = ProfileService.getInstance();

	@GetMapping("/todos")
	public List<Todo> getUndone() {
		return progressService.getTodos();
	}

	@GetMapping("/current_section")
	public String getCurrent() {
		return progressService.getCurrentSection();
	}
	
	@GetMapping("/img/{image}")
	public byte[] getImage(@PathVariable(required = true) String image) throws IOException {
		InputStream is = profileService.getImageAsStream(progressService.getCurrentProfile(), image);
		byte[] bytes = StreamUtils.copyToByteArray(is);
		return bytes;
	}
}

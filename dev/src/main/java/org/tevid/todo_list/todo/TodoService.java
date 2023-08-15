package org.tevid.todo_list.todo;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.profile.Profile;

import lombok.extern.java.Log;

@Log
public class TodoService {

	private static TodoService instance;

	public static TodoService getInstance() {
		if (instance == null)
			instance = new TodoService();
		return instance;
	}

	private LoggingService loggingService = LoggingService.getInstance();
	private LinkedHashMap<String, List<Todo>> todos = new LinkedHashMap<String, List<Todo>>();

	private TodoService() {
		List<Todo> act1 = Arrays.asList(Todo.builder().section("Act1").caption("Act1 todo 1").isOptional(false).build(),
				Todo.builder().section("Act1").caption("Act1 todo 3 (Optional").isOptional(true).build(),
				Todo.builder().section("Act1").caption("Act1 todo 2").isOptional(false).build());
		todos.put("Act1", act1);

		List<Todo> act2 = Arrays.asList(Todo.builder().section("Act2").caption("Act1 todo 1").isOptional(false).build(),
				Todo.builder().section("Act2").caption("Act2 todo 2 (Optional").isOptional(true).build());
		todos.put("Act2", act2);
	}

	public LinkedList<String> getSections() {
		return new LinkedList<String>(todos.keySet());
	}

	public List<Todo> getTodos(String section) {
		return todos.get(section);
	}

	public void edit(Profile profile) {
		Path todoFile = profile.getPath().resolve("todos.txt");
		if (todoFile.toFile().exists() == false) {
			loggingService.logError("Todo file for profile " + profile.getName() + " doesn't exist in location "
					+ todoFile.toAbsolutePath().toString());
			return;
		}
		try {
			Desktop.getDesktop().open(todoFile.toFile());
		} catch (IOException e) {
			loggingService
					.logError("Error opening todo file with default editor: " + todoFile.toAbsolutePath().toString());
			return;
		}
	}

	public LinkedHashMap<String, List<Todo>> load(Profile profile) {

		LinkedList<Todo> todos = new LinkedList<Todo>();

		Path todoFile = profile.getPath().resolve("todos.txt");
		if (todoFile.toFile().exists() == false) {
			loggingService.logError("Todo file for profile " + profile.getName() + " doesn't exist in location "
					+ todoFile.toAbsolutePath().toString());
			return new LinkedHashMap<String, List<Todo>>();
		}

		Todo.TodoBuilder todoBuilder = Todo.builder();
		boolean shouldAdd = false;
		String section = "";
		try (Stream<String> stream = Files.lines(todoFile)) {

			Iterator<String> iterator = stream.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next().replace("\n", "").replace("\r", "").trim();
				if (line.isEmpty())
					continue;

				if (line.startsWith("#")) {
					if (shouldAdd)
						todos.add(todoBuilder.build());
					section = line.substring(1).trim();
					todoBuilder = Todo.builder().section(section);
					shouldAdd = false;
				} else if (line.startsWith("-")) {
					if (shouldAdd)
						todos.add(todoBuilder.build());
					String caption = line.substring(1).trim();
					todoBuilder = Todo.builder().caption(caption).section(section);
					shouldAdd = true;
				} else if (line.startsWith("*")) {
					if (shouldAdd)
						todos.add(todoBuilder.build());
					String caption = line.substring(1).trim();
					todoBuilder = Todo.builder().caption("(Optional): " + caption).isOptional(true).section(section);
					shouldAdd = true;
				} else if (line.startsWith("//")) {
					// do nothing it's a comment
				} else {
					if (shouldAdd == true) {
						TodoContent content = TodoContent.buildText(line.trim());
						todoBuilder = todoBuilder.addContent(content);
					}
				}
			}
			if (shouldAdd) {
				todos.add(todoBuilder.build());
			}

		} catch (IOException e) {
			loggingService.logError("Error reading todo.txt for profile " + profile.getName());
			log.severe("Error reading todo.txt for profile " + profile.getName() + ". error=" + e.toString());
		}

		LinkedHashMap<String, List<Todo>> todoMap = new LinkedHashMap<String, List<Todo>>();
		todos.forEach(todo -> {
			List<Todo> tmp = todoMap.get(todo.getSection());
			if (tmp == null)
				tmp = new LinkedList<Todo>();
			tmp.add(todo);
			todoMap.put(todo.getSection(), tmp);
		});
		this.todos = todoMap;
		return todoMap;
	}

	public LocalDateTime getModifiedAt(Profile profile) {

		try {
			Path todoFile = profile.getPath().resolve("todos.txt");
			BasicFileAttributes attributes;
			attributes = Files.readAttributes(todoFile, BasicFileAttributes.class);
			FileTime lastModifiedTime = attributes.lastModifiedTime();
			Instant instant = lastModifiedTime.toInstant();
			return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (IOException e) {
			loggingService.logError("Error reading todo.txt for profile " + profile.getName());
			log.severe("Error reading todo.txt for profile " + profile.getName() + ". error=" + e.toString());
			return null;
		}

	}
}

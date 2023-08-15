package org.tevid.todo_list.todo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.profile.Profile;
import org.tevid.todo_list.todo.Todo.TodoState;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class ProgressService {

	private static ProgressService instance;

	public static ProgressService getInstance() {
		if (instance == null)
			instance = new ProgressService();
		return instance;
	}

	private TodoService todoService = TodoService.getInstance();
	private LoggingService loggingService = LoggingService.getInstance();

	@Getter
	private Profile currentProfile;

	private List<TodoChangeListener> listeners = new LinkedList<TodoChangeListener>();

	private ProgressConfig config;

	private ProgressService() {
	}

	public void addChangeListener(TodoChangeListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(TodoChangeListener listener) {
		listeners.remove(listener);
	}

	public List<Todo> getTodos() {
		if (config == null || config.getAllTodos() == null || config.getAllTodos().isEmpty())
			return new LinkedList<Todo>();
		return config.getAllTodos().get(config.getCurrentSection());
	}

	public LinkedList<String> getSections() {
		if (config == null)
			return new LinkedList<String>();
		return new LinkedList<String>(config.getAllTodos().keySet());
	}

	public String getCurrentSection() {
		if (config == null)
			return "";
		return config.getCurrentSection();
	}
	
	public void reset(Profile profile) {	
		Path progressFile = profile.getPath().resolve("progress.json");
		if(progressFile.toFile().exists())
			progressFile.toFile().delete();
		load(profile);
		notifyListeners();
	}

	public void markDone() {
		List<Todo> todos = getTodos();

		if (todos.isEmpty())
			return;

		boolean found = false;
		for (Iterator<Todo> iter = todos.iterator(); iter.hasNext();) {
			Todo todo = iter.next();
			if (todo.getState() == TodoState.CURRENT) {
				found = true;
				todo.setState(TodoState.DONE);
				if (iter.hasNext()) {
					Todo next = iter.next();
					next.setState(TodoState.CURRENT);
				}
				break;
			}
		}

		if (found == false) {

			if (todos.get(0).getState() != TodoState.DONE) {
				todos.get(0).setState(TodoState.CURRENT);
			} else {

				LinkedList<String> sections = getSections();
				int index = sections.indexOf(config.getCurrentSection());
				if (index == -1)
					return;
				if (index + 1 < sections.size()) {
					config.setCurrentSection(sections.get(index + 1));
					todos = getTodos();
					if (todos.isEmpty() == false)
						todos.get(0).setState(TodoState.CURRENT);
				}
			}
		}

		notifyListeners();
		save();
	}

	public void markUnDone() {
		List<Todo> todos = getTodos();

		if (todos.isEmpty())
			return;

		boolean found = false;

		for (ListIterator<Todo> iter = todos.listIterator(todos.size()); iter.hasPrevious();) {
			Todo todo = iter.previous();
			if (todo.getState() == TodoState.CURRENT) {
				todo.setState(TodoState.UNDONE);
				if (iter.hasPrevious()) {
					Todo previous = iter.previous();
					previous.setState(TodoState.CURRENT);
					found = true;
				}else {					 
					List<String> sections = getSections();					
					if(sections.isEmpty() == false && sections.get(0) != getCurrentSection()) {
						found = false;
					}
				}
				break;
			}
		}

		if (found == false) {

			if (todos.get(todos.size() - 1).getState() == TodoState.DONE) {
				todos.get(todos.size() - 1).setState(TodoState.CURRENT);
			} else {

				LinkedList<String> sections = getSections();
				int index = sections.indexOf(config.getCurrentSection());
				if (index == -1)
					return;
				if (index - 1 >= 0) {
					config.setCurrentSection(sections.get(index - 1));
					todos = getTodos();
					if (todos.isEmpty() == false)
						todos.get(todos.size() - 1).setState(TodoState.CURRENT);
				}
			}
		}

		notifyListeners();
		save();
	}

	private void notifyListeners() {
		for (TodoChangeListener listener : listeners) {
			listener.onChange();
		}
	}

	public void load(Profile profile) {

		Path progressFile = profile.getPath().resolve("progress.json");

		if (progressFile.toFile().exists() == false) {
			loadFromTodoFile(profile);
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			this.config = mapper.readValue(progressFile.toFile(), ProgressConfig.class);
			this.currentProfile = profile;

		} catch (IOException e) {
			loggingService.logError("Couldn't read progress file " + progressFile.toAbsolutePath().toString()
					+ " . Using Todo file instead");
			log.severe("Couldn't read progress file " + progressFile.toAbsolutePath().toString()
					+ ". Using todo file inszead. error=" + e.toString());

			loadFromTodoFile(profile);
		}
		notifyListeners();
	}

	private void loadFromTodoFile(Profile profile) {
		config = new ProgressConfig();
		config.setAllTodos(todoService.load(profile));
		config.setModifiedAt(todoService.getModifiedAt(profile));
		config.setCurrentSection(todoService.getSections().getFirst());

		// TODO remove this. This is only temporary
		config.getAllTodos().values().forEach(list -> list.forEach(todo -> todo.setState(TodoState.UNDONE)));

		List<Todo> todos = config.getAllTodos().get(config.getCurrentSection());
		if (todos != null && todos.isEmpty() == false)
			todos.get(0).setState(TodoState.CURRENT);
		this.currentProfile = profile;
	}

	private void save() {

		if (currentProfile == null) {
			loggingService.logInfo("Can't save progress, no profile selected");
			return;
		}

		Path progressFile = currentProfile.getPath().resolve("progress.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(progressFile.toFile(), config);
		} catch (IOException e) {
			loggingService.logError("Couldn't save progress to file " + progressFile.toAbsolutePath().toString());
			log.severe("Couldn't save progress to file " + progressFile.toAbsolutePath().toString() + ". error="
					+ e.toString());
		}
	}

	public static interface TodoChangeListener {
		public void onChange();
	}
}

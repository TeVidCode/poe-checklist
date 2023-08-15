package org.tevid.todo_list.todo;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

@Data
public class ProgressConfig {	
	private LocalDateTime modifiedAt;
	private LinkedHashMap<String, List<Todo>> allTodos = new LinkedHashMap<String, List<Todo>>();	
	private String currentSection;
}

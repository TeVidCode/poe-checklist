package org.tevid.todo_list.todo;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
	private String section;
	private String caption;
	private List<TodoContent> contents;
	private boolean isOptional;
	private TodoState state;
	private String hash;
	
	public static enum TodoState {
		UNDONE, CURRENT, DONE
	}
	
	public static class TodoBuilder {
		
		private List<TodoContent> contents = new LinkedList<TodoContent>();
		public TodoBuilder addContent(TodoContent content) {
			contents.add(content);
			return this;
		}
	}
}

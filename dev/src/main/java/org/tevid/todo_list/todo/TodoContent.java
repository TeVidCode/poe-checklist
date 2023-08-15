package org.tevid.todo_list.todo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TodoContent {
	private String text;
	private String image;
	private TodoContentType type;

	
	public static TodoContent buildText(String text) {
		return new TodoContent(text, null, TodoContentType.TEXT);
	}
	
	public static TodoContent buildImage(String text) {
		return new TodoContent(text, null, TodoContentType.IMG);
	}	
	
	public static enum TodoContentType {
		TEXT, IMG
	}
	
}
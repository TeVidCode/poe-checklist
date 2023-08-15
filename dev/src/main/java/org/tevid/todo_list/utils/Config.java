package org.tevid.todo_list.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Config {
	private String activeProfile;
	private int port;
	private int nextKey;
	private int previousKey;
}

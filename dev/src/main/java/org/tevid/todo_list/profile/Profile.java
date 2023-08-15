package org.tevid.todo_list.profile;

import java.nio.file.Path;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Profile {
	private String name;
	private Path path;
	private boolean active;
}

package org.tevid.todo_list;
import java.nio.file.Path;

import com.beust.jcommander.Parameter;

import lombok.Data;

@Data
public class ProgramArgs {

	@Parameter(names = "-root", description = "Root directory for all created files", required = false)
	private Path rootDir;

}

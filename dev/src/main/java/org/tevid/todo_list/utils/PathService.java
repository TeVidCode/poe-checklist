package org.tevid.todo_list.utils;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class PathService {
	private static PathService instance;

	public static PathService getInstance() {
		if (instance == null)
			instance = new PathService();
		return instance;
	}
	
	@Getter
	private Path profileFolder;
	
	@Getter
	private Path rootDir;

	private PathService() {
	}

	public void setRootDir(Path rootDir) {
		if(rootDir == null) {
			this.rootDir = Paths.get(".");
		}else {
			this.rootDir = rootDir;
		}
		
		this.profileFolder = this.rootDir.resolve("profiles");		
	}
}

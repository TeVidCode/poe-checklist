package org.tevid.todo_list.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;

import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.utils.PathService;

import lombok.extern.java.Log;

@Log
public class ProfileService {
	private static ProfileService instance;

	public static ProfileService getInstance() {
		if (instance == null)
			instance = new ProfileService();
		return instance;
	}

	private LoggingService loggingService;
	private PathService pathService = PathService.getInstance();

	private ProfileService() {
	}
	
	public InputStream getImageAsStream(Profile profile, String name) throws FileNotFoundException {
		Path path = profile.getPath().resolve(name);
		if(path.toFile().exists() == false)
			return null;
		return new FileInputStream(path.toFile());
	}
	
	public void deleteProfile(Profile profile) throws IOException {		
		 Files.walk(profile.getPath())
         .sorted(Comparator.reverseOrder()) // Delete from bottom to top
         .map(Path::toFile)
         .forEach(File::delete);	
	}

	public void updateName(Profile profile, String newName)
			throws InvalidProfileNameException, DuplicateProfileException {

		if (newName.matches("([A-Za-z0-9\\-\\_]+)") == false)
			throw new InvalidProfileNameException(newName);

		Path newProfileFolder = pathService.getProfileFolder().resolve(newName);
		if (newProfileFolder.toFile().exists() == true)
			throw new DuplicateProfileException(newName);

		profile.getPath().toFile().renameTo(newProfileFolder.toFile());
	}

	public void createProfile(String name) throws InvalidProfileNameException, IOException, DuplicateProfileException {
		if (name.matches("([A-Za-z0-9\\-\\_]+)") == false)
			throw new InvalidProfileNameException(name);

		Path profileFolder = pathService.getProfileFolder().resolve(name);
		if (profileFolder.toFile().exists() == true)
			throw new DuplicateProfileException(name);

		profileFolder.toFile().mkdir();

		Path exampleProfile = pathService.getRootDir().resolve("example_profile");
		if (exampleProfile.toFile().exists() == false) {
			loggingService.logWarning("exmample_profile folder is missing. Empty todo list was created");
			profileFolder.resolve("todos.txt").toFile().createNewFile();
			return;
		}

		try (DirectoryStream<Path> paths = Files.newDirectoryStream(exampleProfile)) {
			for (Path file : paths) {
				try {
					Path dst = profileFolder.resolve(file.getFileName());
					Files.copy(file, dst);
				} catch (IOException e) {
					loggingService.logWarning("Couldn't copy example file: " + file.getFileName()
							+ ". Maybe the example won't work correctly");
				}
			}
		}		
	}

	public LinkedList<Profile> getProfiles() {
		LinkedList<Profile> profiles = new LinkedList<Profile>(); 
		Path profileFolder = pathService.getProfileFolder();

		if (profileFolder.toFile().exists() == false) {
			loggingService.logError("Can't find profiles folder: " + profileFolder.toAbsolutePath().toString());
			log.severe("Can't find profiles folder: " + profileFolder.toAbsolutePath().toString());
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(profileFolder)) {
			for (Path dir : stream) {

				if (Files.isDirectory(dir) == false)
					continue;

				Profile profile = Profile.builder().name(dir.getFileName().toString()).path(dir).build();
				profiles.add(profile);
			}
		} catch (IOException e) {
			loggingService.logError("Can't iterate profile folder: " + profileFolder.toAbsolutePath().toString());
			log.severe("Can't iterate profile folder: " + e.toString());
		}
		return profiles;
	}

	public static class InvalidProfileNameException extends Exception {
		private static final long serialVersionUID = 1L;

		public InvalidProfileNameException(String newName) {
			super("Can't rename Profile. Name must only contain letters, numbers, underscore and dashes. name was: "
					+ newName);
		}
	}

	public static class DuplicateProfileException extends Exception {		
		private static final long serialVersionUID = 1L;

		public DuplicateProfileException(String newName) {
			super("Profile with the name='" + newName + "' already exists.");
		}
	}
}

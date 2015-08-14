package eu.waldonia.ipl.populator;

import java.net.URI;
import java.nio.file.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import org.slf4j.*;

/**
 * Responsible for collecting and parsing a number of roster files from a
 * directory.
 * 
 * @author sid
 *
 */
@Configuration
@ComponentScan("eu.waldonia.ipl")
public class RosterSeasonProcessor {

	Logger logger = LoggerFactory.getLogger(RosterSeasonProcessor.class);

	@Autowired
	RosterFileProcessor rfp;

	/**
	 * 
	 * @param rosterSeasonDirectory
	 *            The location of the season directory
	 * @return Errors keyed by the file URI
	 */
	public Map<String, Map<String, String>> process(
			final URI rosterSeasonDirectory) throws Exception {
		// errors by file
		Map<String, Map<String, String>> errorsByFile = new HashMap<String, Map<String, String>>();

		if (rosterSeasonDirectory != null) {
			// search file directory

			Path p = Paths.get(rosterSeasonDirectory);

			if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
				throw new IllegalArgumentException(rosterSeasonDirectory
						+ " needs to be a directory containing team roster .txt files");
			}

			List<Path> teamRosters = new ArrayList<>();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p,"*.txt")) {
				for (Path entry : stream) {
					teamRosters.add(entry);
				}
			} catch (Exception e) {
				throw e;
			}	
			
			logger.info("Found "+teamRosters.size()+" teams to process");

			// for each item
			for (Path path : teamRosters) {
				String fileName = path.getFileName().toString();
				errorsByFile.put(fileName,rfp.process(path.toUri()));
			}

		}

		return errorsByFile;
	}

}

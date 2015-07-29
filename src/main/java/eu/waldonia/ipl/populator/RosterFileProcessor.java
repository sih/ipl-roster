package eu.waldonia.ipl.populator;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import eu.waldonia.ipl.domain.AllRounder;
import eu.waldonia.ipl.domain.Batter;
import eu.waldonia.ipl.domain.Bowler;
import eu.waldonia.ipl.domain.Franchise;
import eu.waldonia.ipl.domain.Left;
import eu.waldonia.ipl.domain.Player;
import eu.waldonia.ipl.domain.Right;
import eu.waldonia.ipl.domain.WicketKeeper;
import eu.waldonia.ipl.domain.Year;
import eu.waldonia.ipl.repository.FranchiseRepostitory;
import eu.waldonia.ipl.repository.PlayerRepository;

public class RosterFileProcessor {
	
	@Autowired
	FranchiseRepostitory franchiseRepository;
	
	@Autowired
	PlayerRepository playerRepository;
	
	Left lh = new Left();
	Right rh = new Right();
	
	
	static final String ROSTER = "roster";

	public void process(final URI fileLocation) throws Exception {
		
		Path p = Paths.get(fileLocation).toAbsolutePath();
		String[] directories = p.toString().split("/");

		// check we're processing a roster file
		if (!rosterFile(directories)) {
			throw new IllegalArgumentException("Wrong file supplied "+p.toString());
		}

		Franchise f = parseFranchise(directories); 	// grab franchise from filename
		Year y = parseYear(directories);			// grab year from path
		
		Stream<String> lines = Files.lines(Paths.get(fileLocation));
		processStream(lines, f, y);
		
		
	}

	private Year parseYear(String[] directories) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean rosterFile(String[] directories) {
		// roster should be the penultimate dir
		return ROSTER.equalsIgnoreCase(directories[directories.length-2]);
	}
	
	private Franchise parseFranchise(String[] directories) {
		Franchise f = null;
		String filename = directories[directories.length-1];
		
		String[] filesplit = filename.split(".");
		String code = filesplit[0].toUpperCase();
		
		f = franchiseRepository.findByCode(code);
		if (null == f) {
			f = new Franchise(code);
		}
		
		return f;
	}

	private void processStream(Stream<String> stream, Franchise f, Year y) {
		
		Iterator<String> lines = stream.iterator();
		String type = null;
		while (lines.hasNext()) {
			String line = lines.next();
			if (line.equals("Batsmen") || line.equals("All-rounders") || line.equals("Wicket-keepers") || line.equals("Bowlers")) {
				type = line;
			}
			else  {
				Player p = parseLine(line, type, f, y);
			}
		}
		
	}
	
	private Player parseLine(String line, String type, Franchise f, Year y) {
		Player p = null;
		if (line != null) {
			if (type.equals("Batsmen")) {
				p = new Batter();
			}
			else if (type.equals("All-rounders")) {
				p = new AllRounder();
			}
			else if (type.equals("Wicket-keepers")) {
				p = new WicketKeeper();
			}
			else if (type.equals("Bowlers")) {
				p = new Bowler();
			}
			String[] tokens = line.split("\t");
			for (int i = 0; i < tokens.length; i++) {
				switch (i) {
				case 0:
					// p.number = Integer.parseInt(tokens[i]);
					break;
				case 1:
					p.name = tokens[i];
					break;
				// nationality
				case 2:
					// p.nationality = tokens[i];
					break;
				// dob
				case 3:
					// p.dob = tokens[i];
					break;
				// bats
				case 4:
					
					if (tokens[i].equals("Left-handed")) {
						// p.bats = lh;
					}
					else if (tokens[i].equals("Left-handed")) {
						// p.bats = rh;
					}

					break;
					
				case 5:
					
					String bowling = tokens[i].toLowerCase();	// normalise
				
					if (bowling.contains("slow")) {
						
					}
					
				default:
					break;
				}
			}
		}
		return p;
	}
	
	public static void main(String[] args) throws Exception {
		RosterFileProcessor rfp = new RosterFileProcessor();
		rfp.process(new URI("file:///Users/sid/dev/iplpopneo/src/main/resources/2015/roster/csk.txt"));
	}
}

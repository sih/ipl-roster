package eu.waldonia.ipl.populator;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import eu.waldonia.ipl.domain.AllRounder;
import eu.waldonia.ipl.domain.Batter;
import eu.waldonia.ipl.domain.Bowler;
import eu.waldonia.ipl.domain.Contract;
import eu.waldonia.ipl.domain.Franchise;
import eu.waldonia.ipl.domain.Left;
import eu.waldonia.ipl.domain.Player;
import eu.waldonia.ipl.domain.Right;
import eu.waldonia.ipl.domain.Signs;
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
	
	static final String SHIRT_NUMBER = "shirtNumber";
	static final String NAME = "name";
	static final String SALARY = "salary";
	static final String CURRENCY = "currency";
	static final String BOWL_PACE = "pace";
	static final String BOWL_VARIETY = "variety";
	static final String NATIONALITY = "nationality";
	static final String DOB = "dob";
	static final String HANDED = "handed";	
	
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
		
		String yyyy = directories[directories.length-3];
		Integer YYYY = Integer.parseInt(yyyy);
		
		return new Year(YYYY);
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
				Map<Player, Map<String,String>> playerAttrs = parseLine(line, type);
				populateAndSavePlayer(playerAttrs, y, f);
			}
		}
		
	}
	
	private void populateAndSavePlayer(Map<Player, Map<String, String>> playerAttrs,
			Year y, Franchise f) {
		
		Player p = playerAttrs.keySet().iterator().next();
		Map<String,String> attrs = playerAttrs.get(p);
		p.name = attrs.get(NAME);
		Integer value = Integer.parseInt(attrs.get(SALARY));
		Contract c = new Contract(y, value, attrs.get(CURRENCY));
		Signs s = new Signs(p,c);
		f.holds(c);
		
	
		playerRepository.save(p);
		franchiseRepository.save(f);		
	}

	private Map<Player, Map<String,String>> parseLine(String line, String type) {
		
		Map<Player, Map<String,String>> playerAttribtues = new HashMap<Player, Map<String,String>>();
		
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
			
			Map<String,String> attrMap = new HashMap<String,String>();
			playerAttribtues.put(p,attrMap);
			
			String[] tokens = line.split("\t");
			
			for (int i = 0; i < tokens.length; i++) {
				switch (i) {
				case 0:
					attrMap.put(SHIRT_NUMBER, tokens[i]);
					break;
				case 1:
					attrMap.put(NAME, tokens[i]);
					break;
				case 2:
					attrMap.put(NATIONALITY, tokens[i]);
					break;
				// dob
				case 3:
					attrMap.put(DOB, tokens[i]);
					break;
				// bats
				case 4:
					attrMap.put(HANDED, tokens[i]);
					break;
					
				case 5:
					// TODO
					break;
				default:
					break;
				}
			}
		}
		return playerAttribtues;
	}
	
	public static void main(String[] args) throws Exception {
		RosterFileProcessor rfp = new RosterFileProcessor();
		rfp.process(new URI("file:///Users/sid/dev/iplpopneo/src/main/resources/2015/roster/csk.txt"));
	}
}

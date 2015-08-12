package eu.waldonia.ipl.populator;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.waldonia.ipl.domain.*;
import eu.waldonia.ipl.repository.*;

@Configuration
@EnableNeo4jRepositories("eu.waldonia.ipl.repository")
@EnableTransactionManagement
@ComponentScan("eu.waldonia.ipl")
public class RosterFileProcessor {

	@Autowired
	FranchiseRepostitory franchiseRepository;

	@Autowired
	PlayerRepository playerRepository;

	@Autowired
	DOBRepository dobRepository;

	@Autowired
	CountryRepository countryRepository;

	static final String SHIRT_NUMBER = "shirtNumber";
	static final String NAME = "name";
	static final String SALARY = "salary";
	static final String CURRENCY = "currency";
	static final String BOWL_PACE = "pace";
	static final String BOWL_VARIETY = "variety";
	static final String COUNTRY = "country";
	static final String DOB = "dob";
	static final String HANDED = "handed";
	static final String ARM = "arm";

	static final String LEFT = "Left";
	static final String RIGHT = "Right";

	static final String ROSTER = "roster";

	static Logger logger;

	Map<String, Integer> months = new HashMap<String, Integer>(12);

	public RosterFileProcessor() {
		// no need for Calendars etc. this dataset has a fixed set of month
		// strings
		months.put("January", 1);
		months.put("February", 2);
		months.put("March", 3);
		months.put("April", 4);
		months.put("May", 5);
		months.put("June", 6);
		months.put("July", 7);
		months.put("August", 8);
		months.put("September", 9);
		months.put("October", 10);
		months.put("November", 11);
		months.put("December", 12);

		logger = LoggerFactory.getLogger(RosterFileProcessor.class);
	}

	public void process(final URI fileLocation) throws Exception {

		Path p = Paths.get(fileLocation).toAbsolutePath();
		String[] directories = p.toString().split("/");

		// check we're processing a roster file
		if (!rosterFile(directories)) {
			throw new IllegalArgumentException(
					"Wrong file supplied " + p.toString());
		}

		Franchise f = parseFranchise(directories); // grab franchise from
													// filename
		Year y = parseYear(directories); // grab year from path

		Stream<String> lines = Files.lines(Paths.get(fileLocation));
		processStream(lines, f, y);

	}

	private Year parseYear(String[] directories) {

		String yyyy = directories[directories.length - 3];
		Integer YYYY = Integer.parseInt(yyyy);

		return new Year(YYYY);
	}

	private boolean rosterFile(String[] directories) {
		// roster should be the penultimate dir
		return ROSTER.equalsIgnoreCase(directories[directories.length - 2]);
	}

	private Franchise parseFranchise(String[] directories) {
		Franchise f = null;
		String filename = directories[directories.length - 1];

		String[] filesplit = filename.split("\\.");
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
			if (line.equals("Batsmen") || line.equals("All-rounders")
					|| line.equals("Wicket-keepers")
					|| line.equals("Bowlers")) {
				type = line;
			}
			else {
				Map<Player, Map<String, String>> playerAttrs = parseLine(line,
						type);
				populateAndSavePlayer(playerAttrs, y, f);
			}
		}

	}

	private void populateAndSavePlayer(
			Map<Player, Map<String, String>> playerAttrs, Year y, Franchise f) {

		Player p = playerAttrs.keySet().iterator().next();
		Map<String, String> attrs = playerAttrs.get(p);
		p.name = attrs.get(NAME);

		// contract
		Integer value = Integer.parseInt(attrs.get(SALARY));
		Contract c = new Contract(y, value, attrs.get(CURRENCY), p);
		Integer shirtNumber = Integer.parseInt(attrs.get(SHIRT_NUMBER));
		p.signed(c, f, shirtNumber);
		f.holds(c); // don't forget the franchise

		// country
		Country ctry = null;
		String countryName = attrs.get(COUNTRY);
		ctry = countryRepository.findCountryByName(countryName);
		if (null == ctry) {
			ctry = new Country(countryName);
		}
		p.country(ctry);

		// bat handed
		Handedness h = null;
		if (attrs.get(HANDED).startsWith(RIGHT)) {
			h = new Right();

		}
		else if (attrs.get(HANDED).startsWith(LEFT)) {
			h = new Left();
		}
		p.bats(h);

		String birthday = attrs.get(DOB);
		String[] doblets = birthday.split(" ");
		Integer day = Integer.parseInt(doblets[0]);
		Integer month = months.get(doblets[1]);
		Integer year = Integer.parseInt(doblets[2]);

		// DOB
		DOB dob = dobRepository.getBirthday(day, month, year);
		if (null == dob) {
			dob = new DOB(day, month, year);
			dobRepository.save(dob);
		}
		p.bornOn = dob;

		// bowling style
		Handedness arm = null;
		if (LEFT.equals(attrs.get(ARM))) {
			arm = new Left();
		}
		else if (RIGHT.equals(attrs.get(ARM))) {
			arm = new Right();
		}

		String pace = attrs.get(BOWL_PACE);
		String variety = attrs.get(BOWL_VARIETY);

		if (arm != null) {
			p.bowls(arm, pace, variety);
		}

		// TODO country

		playerRepository.save(p);
		franchiseRepository.save(f);
	}

	private Map<Player, Map<String, String>> parseLine(String line,
			String type) {

		Map<Player, Map<String, String>> playerAttribtues = new HashMap<Player, Map<String, String>>();

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

			Map<String, String> attrMap = new HashMap<String, String>();
			playerAttribtues.put(p, attrMap);

			String[] tokens = line.split("\t");

			int indexSalary = tokens.length - 2;

			for (int i = 0; i < tokens.length; i++) {
				switch (i) {
				case 0:
					attrMap.put(SHIRT_NUMBER, tokens[i]);
					break;
				case 1:
					attrMap.put(NAME, tokens[i]);
					break;
				case 2:
					attrMap.put(COUNTRY, tokens[i]);
					break;
				// dob
				case 3:
					attrMap.put(DOB, tokens[i]);
					break;
				// bats
				case 4:
					attrMap.put(HANDED, tokens[i]);
					break;
				// bowling
				case 5:
					String bowlDesc = tokens[i].toLowerCase();
					attrMap = addBowlingDetails(attrMap, bowlDesc);
					break;

				default:
					break;
				}
			}
			// TODO think of a better way to find sal
			int salIndex = line.indexOf("INR");
			int millionIndex = line.indexOf(" million");

			String sal = line.substring(salIndex, millionIndex);
			String currency = sal.substring(0, 3);

			BigDecimal bdSal = new BigDecimal(sal.substring(3));
			bdSal = bdSal.multiply(new BigDecimal(1000000));

			attrMap.put(SALARY, String.valueOf(bdSal.intValue()));
			attrMap.put(CURRENCY, currency);
		}
		return playerAttribtues;
	}

	private Map<String, String> addBowlingDetails(Map<String, String> attrMap,
			String bowlDesc) {
		if (bowlDesc.contains("left")) {
			attrMap.put(ARM, LEFT);
		}
		else if (bowlDesc.contains("right")) {
			attrMap.put(ARM, RIGHT);
		}
		else {
			// TOOD log me
			System.out.println("Can't derive bowling arm from " + bowlDesc);
		}
		String variety = null;
		String pace = null;
		// variety
		if (bowlDesc.contains("off")) {
			variety = Bowls.OFF_BREAK;
			pace = Bowls.SLOW;
		}
		else if (bowlDesc.contains("googly")) {
			variety = Bowls.LEG_BREAK_GOOGLY;
			pace = Bowls.SLOW;
		}
		else if (bowlDesc.contains("leg")) {
			variety = Bowls.LEG_BREAK;
			pace = Bowls.SLOW;
		}
		else if (bowlDesc.contains("orthodox")) {
			variety = Bowls.ORTHODOX;
		}
		// pace
		if (bowlDesc.contains("slow")) {
			pace = Bowls.SLOW;
		}
		else if (bowlDesc.contains("medium-fast")) {
			pace = Bowls.MEDIUM_FAST;
		}
		else if (bowlDesc.contains("medium fast")) {
			pace = Bowls.MEDIUM_FAST;
		}
		else if (bowlDesc.contains("fast-medium")) {
			pace = Bowls.FAST_MEDIUM;
		}
		else if (bowlDesc.contains("fast medium")) {
			pace = Bowls.FAST_MEDIUM;
		}
		else if (bowlDesc.contains("medium")) {
			pace = Bowls.MEDIUM;
		}
		else if (bowlDesc.contains("fast")) {
			pace = Bowls.FAST;
		}

		if (null == pace) {
			logger.warn("Couldn't parse pace for " + bowlDesc);
		}
		if (null == pace) {
			logger.warn("Couldn't parse pace for " + bowlDesc);
		}
		attrMap.put(BOWL_PACE, pace);
		attrMap.put(BOWL_VARIETY, variety);

		return attrMap;
	}

}

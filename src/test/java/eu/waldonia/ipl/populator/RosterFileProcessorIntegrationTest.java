package eu.waldonia.ipl.populator;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.testutil.WrappingServerIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.waldonia.ipl.PersistenceContext;
import eu.waldonia.ipl.domain.*;
import eu.waldonia.ipl.repository.*;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RosterFileProcessorIntegrationTest extends WrappingServerIntegrationTest {

    @Autowired
    RosterFileProcessor rfp;
    
    @Autowired
    PlayerRepository playerRepository;
    
    @Autowired
    FranchiseRepostitory franchiseRepository;

    @Autowired
    DOBRepository dobRepository;
    
    @Autowired
    YearRepository yearRepository;

    @Autowired
    CountryRepository countryRepository;
    
    @Autowired
    Session session;

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
    }
    
	@Test
	public void shouldProcessCSKFile() {
		try {
			Player p = null;			
			p = playerRepository.findPlayerByName("Suresh Raina");
			// batter
			assertNull(p);
			// run in the file
			Map<String,String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/csk.txt"));
			
			// check no errors
			assertTrue(linesInError.isEmpty());
			
			// check batter
			p = playerRepository.findPlayerByName("Suresh Raina");
			
			/*
			 * Note there is a bug in Spring Data Neo4j 4.0.0.M1
			 * "Upgrading" to 4.0.0.BUILD-SNAPSHOT causes other issues with dependencies and when
			 * those are resolved with the fact that WrappingServerIntegrationTest no longer exists.
			 * TODO Find the fix in the latest release and incorporate in to a forked copy
			 * http://stackoverflow.com/questions/30465012/spring-data-neo4j-findbynamestring-name-in-interface-returns-incorrect-results
			 */
			
			assertNotNull(p);
			assertTrue(p instanceof Batter);
			Handedness h = p.bats();
			assertTrue(h instanceof Left);
			assertEquals("Suresh Raina", p.name());
			
			// country
			
			Country india = countryRepository.findCountryByName("India");
			assertNotNull(india);
			assertEquals(india, p.country());
			
			DOB dbDOB = dobRepository.getBirthday(27, 11, 1986);
			assertEquals(dbDOB, p.bornOn());
			
			Franchise csk = franchiseRepository.findByCode("CSK");
			Year y2015 = yearRepository.findYearByYear(2015);
			
			List<Contract> contracts = p.contracts();
			Contract contract = (Contract) contracts.stream()
				.filter(s -> s.franchise().equals(csk))
				.toArray()[0];
			
			// check the contract
			assertNotNull(contract);
			assertEquals(95000000,contract.value());
			assertEquals("INR", contract.currency());
			assertEquals(y2015, contract.year());
			
			// check the contract from the franchise pov
			Contract fContract = (Contract)csk.contracts.stream()
			.filter(s -> s.franchise().equals(csk) && s.player().name().equals("Suresh Raina"))
			.toArray()[0];
			
			assertEquals(contract, fContract);
			
			// check bowl style
			Bowls b = p.bowls();
			assertNotNull(b);
			assertEquals(Bowls.OFF_BREAK, b.variety);
			assertEquals(Bowls.SLOW, b.pace);
			assertTrue(b.arm() instanceof Right);
		} 
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}
	}

	/*
	 * Tests Issue #14 where Traded Players have no contract value set
	 * Failure on this line 
	 * 15	Unmukt Chand	India	26 March 1993 (age 22)	Right-handed	Right arm off break	2015	Traded player	
	 * String index out of range: -1}
	 */
	@Test
	public void shouldProcessMumbaiIndians() {
		try {
			Player p = null;			
			p = playerRepository.findPlayerByName("Unmukt Chand");
			// batter
			assertNull(p);
			// run in the file
			Map<String,String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/mi.txt"));
			
			// check no errors
			assertTrue(linesInError.isEmpty());
			
			// check batter
			p = playerRepository.findPlayerByName("Unmukt Chand");
			assertNotNull(p);
			
			// test traded is set
			List<Contract> contracts = p.contracts();
			assertNotNull(contracts);
			assertEquals(1, contracts.size());
			
			Contract c = contracts.iterator().next();
			assertTrue(c.traded());
			assertEquals(0,c.value());
			assertNull(c.currency());
		}
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}

	}
	
	
	/*
	 * Tests Issue #15 where replacement players have no contract value set
	 * Failure on this line 
	 * 6	Sreenath Aravind	India	8 April 1984 (age 31) Left-handed	Left-arm medium-fast	2015	Replacement signing
	 * String index out of range: -1}
	 */
	@Test
	public void shouldProcessRoyalChallengersBangalore() {
		try {
			Player p = null;			
			p = playerRepository.findPlayerByName("Sreenath Aravind");
			// batter
			assertNull(p);
			// run in the file
			Map<String,String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/rcb.txt"));
			
			// check no errors
			assertTrue(linesInError.isEmpty());
			
			// check batter
			p = playerRepository.findPlayerByName("Sreenath Aravind");
			assertNotNull(p);
			
			// test replacement is set
			List<Contract> contracts = p.contracts();
			assertNotNull(contracts);
			assertEquals(1, contracts.size());
			
			Contract c = contracts.iterator().next();
			assertTrue(c.replacement());
			assertEquals(0,c.value());
			assertNull(c.currency());
			
			
		}
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}

	}
	
	
	/*
	 * Tests Issue #16 where Imran Tahir has NA set for his contract value
	 * Failure on this line
	 * 99	Imran Tahir	South Africa	27 March 1979 (age 36)	Right-handed	Right-arm leg break googly	2014	
	 * NA=String index out of range: -1	 
	 */
	@Test
	public void shouldProcessDelhiDaredevils() {
		try {
			Player p = null;			
			p = playerRepository.findPlayerByName("Imran Tahir");
			// batter
			assertNull(p);
			// run in the file
			Map<String,String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/dd.txt"));
			
			// check no errors
			assertTrue(linesInError.isEmpty());
			
			// check batter
			p = playerRepository.findPlayerByName("Imran Tahir");
			assertNotNull(p);
			
			// should have a contract
			List<Contract> contracts = p.contracts();
			assertNotNull(contracts);
			assertEquals(1, contracts.size());
			
			Contract c = contracts.iterator().next();
			// but not a replacement
			assertFalse(c.replacement());
			// or a traded player
			assertFalse(c.replacement());
			// and no contract value
			assertEquals(0,c.value());
			assertNull(c.currency());
			
			
		}
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}

	}
	
}

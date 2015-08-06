package eu.waldonia.ipl.populator;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

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
			rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/csk.txt"));
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
			Handedness h = p.bats;
			assertTrue(h instanceof Left);
			assertEquals("Suresh Raina", p.name);
			
			// country
			
			Country india = countryRepository.findCountryByName("India");
			assertNotNull(india);
			assertEquals(india, p.country());
			
			DOB dbDOB = dobRepository.getBirthday(27, 11, 1986);
			assertEquals(dbDOB, p.bornOn);
			
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
			.filter(s -> s.franchise().equals(csk))
			.toArray()[0];
			
			assertEquals(contract, fContract);
			
			// check bowl style
			Bowls b = p.bowls;
			assertNotNull(b);
			assertEquals(Bowls.OFF_BREAK, b.variety);
			assertEquals(Bowls.SLOW, b.pace);
			assertTrue(b.arm() instanceof Right);
		} 
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}
	}

}

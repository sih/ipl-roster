package eu.waldonia.ipl.domain;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.testutil.WrappingServerIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.waldonia.ipl.PersistenceContext;
import eu.waldonia.ipl.repository.*;

@ContextConfiguration({"classpath:application-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class DomainTest extends WrappingServerIntegrationTest {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    FranchiseRepostitory franchiseRepository;
    
    @Autowired
    YearRepository yearRepository;
    
    @Autowired
    DOBRepository dobRepository;
    
    @Autowired
    CountryRepository countryRepository;
    
    @Autowired
    Session session;
    

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
    }
    
    @Test
    public void shouldSave() {
    	Player bravo = new Player("Dwayne Bravo");
    	Right rightArm = new Right();
    	String pace = "Medium-Fast";
    	String variety = null;
    	bravo.bowls(rightArm, pace, variety);
    	
    	// test save
    	playerRepository.save(bravo);
    }

    @Test
    public void shouldAllowPlayerCreation() {
    	Player bravo = new Player("Dwayne Bravo");
    	Right rightArm = new Right();
    	String pace = "Medium-Fast";
    	String variety = null;
    	bravo.bowls(rightArm, pace, variety);
    	
    	// all empty to begin with
    	Iterable<Player> playaz = playerRepository.findAll();
    	assertFalse(playaz.iterator().hasNext());
    	
    	// test save
    	playerRepository.save(bravo);
    	Player db = playerRepository.findOne(bravo.id());
    	
    	// test basic
    	assertEquals("Dwayne Bravo", db.name());
    	// test relationship
    	Bowls bowls = db.bowls();
    	assertNotNull(bowls);
    	assertEquals("Medium-Fast",bowls.pace);
    	assertNull(bowls.variety);
    	assertTrue((bowls.arm instanceof Right));
    }
    
    @Test
    public void shouldRepresentTernaryContractsRelationship() {
    	Player bravo = new Player("Dwayne Bravo");
    	Year y2015 = new Year(2015);
    	Contract contract = new Contract(y2015,40000000,"INR",bravo);
    	Franchise csk = new Franchise("CSK", "Chennai Super Kings");
    	bravo.signed(contract, csk, 47);
    	
    	playerRepository.save(bravo);
    	Player dbPlayer = playerRepository.findOne(bravo.id());
    	
    	franchiseRepository.save(csk);
    	Franchise dbFranchise = franchiseRepository.findOne(csk.id);
    	
    	
    	assertEquals("Dwayne Bravo", dbPlayer.name());
    	Set<Signs> contracts = dbPlayer.signs();
    	assertEquals(1, contracts.size());
    	Signs s = contracts.iterator().next();
    	
    	assertEquals(new Integer(47),s.shirtNumber());
    	Contract c = s.contract();
    	assertNotNull(c);
    	assertEquals(y2015,c.year());
    	assertEquals(40000000,c.value());
    	assertEquals("INR", c.currency());
    	
    	// now check the franchise
    	assertEquals(1, dbFranchise.contracts.size());
    	Contract fc = dbFranchise.contracts.iterator().next();
    	assertNotNull(fc);
    	assertEquals(y2015,fc.year());
    	assertEquals(40000000,fc.value());
    	assertEquals("INR", fc.currency());
    	assertEquals(c.id, fc.id);		// should be the same contract!
    	
    	
    }
    
    @Test
    public void shouldFindAFranchiseByCode() {
    	Franchise f = new Franchise("ABC");
    	franchiseRepository.save(f);
    	
    	Franchise dbF = franchiseRepository.findByCode("ABC");
    	assertNotNull(dbF);
    	assertEquals(f.code(), dbF.code());
    	assertEquals(f.id, dbF.id);
    }
    
    @Test
    public void shouldFindYearByYear() {
    	Year y = new Year(1999);
    	yearRepository.save(y);
    	
    	Year dbY = yearRepository.findYearByYear(1999);
    	assertNotNull(dbY);
    	assertEquals(y.year, dbY.year);
    	assertEquals(y.id, dbY.id);
    
    
    }
    
    
    @Test
    public void shouldFindBirthdays() {
    	
    	DOB dob = new DOB(26,11,1987);
    	dobRepository.save(dob);
    	
    	DOB dbD = dobRepository.findOne(dob.id);
    	
    	dbD = dobRepository.getBirthday(26,11,1987);    	
    	assertNotNull(dbD);
    	assertEquals(dob,dbD);
    	
    	dbD = dobRepository.getBirthday(26,11,1986);    	
    	assertNull(dbD);
    }

    
    @Test
    public void shouldFindPlayersByName() {
    	Player p = null;
    	p = playerRepository.findPlayerByName("IVA Richards");
    	assertNull(p);
    	Player kv = new Player();
    	kv.name("IVA Richards");
    	playerRepository.save(kv);
    	p = playerRepository.findPlayerByName("IVA Richards");
    	assertNotNull(p);
    }
    
    
    /**
     * Test for issue in spring-data-neo4j 4.0.0.M1
     * https://github.com/sih/ipl-roster/issues/8
     */
    @Test
    public void shouldFindPlayersByNameMulti() {
    	Player p = null;
    	Player kv = new Player();
    	kv.name("IVA Richards");
    	playerRepository.save(kv);
    	p = playerRepository.findPlayerByName("IVA Richards");
    	assertNotNull(p);
    	assertEquals("IVA Richards", p.name());
    	// multi
    	Player mh = new Player();
    	mh.name("Michael Holding");
    	playerRepository.save(mh);
    	try {
        	p = playerRepository.findPlayerByName("IVA Richards");
        	assertNotNull(p);
        	assertEquals("IVA Richards", p.name());
    	}
    	catch (Exception e) {
    		fail("Shouldn't have thrown an exception "+e.getMessage());
    	}

    }
    
    @Test
    public void shouldSupportBowlsRelationship() {
    	Player p = new Player();
    	p.name("Michael Holding");
    	Handedness rightArm = new Right();
    	String pace = Bowls.FAST;
    	String variety = null;
    	p.bowls(rightArm,pace,variety);
    	
    	playerRepository.save(p);
    	
    	Player dbp = playerRepository.findPlayerByName("Michael Holding");
    	assertNotNull(dbp);
    	Bowls b = dbp.bowls();
    	assertNull(b.variety);
		assertEquals(Bowls.FAST, b.pace);
		assertTrue(b.arm() instanceof Right);
    }
    
    @Test
    public void shouldSupportBatsAndBowlsRelationship() {
    	Player p = new Player();
    	p.name("Michael Holding");
    	Handedness rightArm = new Right();
    	String pace = Bowls.FAST;
    	String variety = null;
    	p.bowls(rightArm,pace,variety);
    	
    	Handedness rightHand = new Right();
    	p.bats(rightHand);
    	
    	playerRepository.save(p);
    	
    	Player dbp = playerRepository.findPlayerByName("Michael Holding");
    	assertNotNull(dbp);
    	Bowls b = dbp.bowls();
    	assertNull(b.variety);
		assertEquals(Bowls.FAST, b.pace);
		assertTrue(b.arm() instanceof Right);    	
    }

    @Test
    public void shouldSupportRepresentingCountry() {
    	Player p = new Player();
    	p.name("Michael Holding");
    	Handedness rightArm = new Right();
    	String pace = Bowls.FAST;
    	String variety = null;
    	p.bowls(rightArm,pace,variety);
    	
    	Handedness rightHand = new Right();
    	p.bats(rightHand);

    	Country jamaica = new Country("Jamaica");
    	p.country(jamaica);
    	
    	playerRepository.save(p);
    	
    	Player dbp = playerRepository.findPlayerByName("Michael Holding");
    	
    	assertNotNull(dbp);
    	assertNotNull(dbp.country());
    	assertEquals("Jamaica", dbp.country().name());
    	
    }
    
    @Test
    public void shouldFindCountryByName() {
    	Country c= new Country("Guyana");
    	countryRepository.save(c);

    	Country dbC = countryRepository.findCountryByName("Guyana");
    	assertNotNull(dbC);
    	assertEquals(c.id, dbC.id);
    }
    
}

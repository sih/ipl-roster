package eu.waldonia.ipl.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import eu.waldonia.ipl.repository.PlayerRepository;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class DomainTest extends WrappingServerIntegrationTest{

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    Session session;
    

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
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
    	Player db = playerRepository.findOne(bravo.id);
    	
    	// test basic
    	assertEquals("Dwayne Bravo", db.name);
    	// test relationship
    	Bowls bowls = db.bowls;
    	assertNotNull(bowls);
    	assertEquals("Medium-Fast",bowls.pace);
    	assertNull(bowls.variety);
    	assertTrue((bowls.arm instanceof Right));
    }
    
    @Test
    public void shouldRepresentTernaryContractsRelationship() {
    	Player bravo = new Player("Dwayne Bravo");
    	Year y2015 = new Year(2015);
    	Contract contract = new Contract(y2015,40000000,"INR");
    	Franchise csk = new Franchise("CSK", "Chennai Super Kings");
    	bravo.signed(contract, csk, 47);
    	
    	playerRepository.save(bravo);
    	Player db = playerRepository.findOne(bravo.id);
    	
    	assertEquals("Dwayne Bravo", db.name);
    	Set<Signs> contracts = db.signs;
    	assertEquals(1, contracts.size());
    	Signs s = contracts.iterator().next();
    	
    	assertEquals(new Integer(47),s.shirtNumber);
    	Contract c = s.contract;
    	assertNotNull(c);
    	assertEquals(y2015,c.year);
    	assertEquals(40000000,c.value);
    	assertEquals("INR", c.currency);
    	
    }
    
}

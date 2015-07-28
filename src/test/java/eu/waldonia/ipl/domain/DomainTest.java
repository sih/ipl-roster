package eu.waldonia.ipl.domain;

import static org.junit.Assert.*;

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
    	playaz = playerRepository.findAll();
    	assertTrue(playaz.iterator().hasNext());
    	Player db = playaz.iterator().next();
    	
    	// test basic
    	assertEquals("Dwayne Bravo", db.name);
    	// test relationship
    	Bowls bowls = db.bowls;
    	assertNotNull(bowls);
    	assertEquals("Medium-Fast",bowls.pace);
    	assertNull(bowls.variety);
    	assertTrue((bowls.arm instanceof Right));

    
    }
}

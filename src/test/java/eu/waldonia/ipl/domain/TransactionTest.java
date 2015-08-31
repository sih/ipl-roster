package eu.waldonia.ipl.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.testutil.WrappingServerIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.waldonia.ipl.PersistenceContext;
import eu.waldonia.ipl.repository.PlayerRepository;

@ContextConfiguration({"classpath:application-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionTest extends WrappingServerIntegrationTest {

    @Autowired
    Session session;
    
    @Autowired
    PlayerRepository playerRepository;
    
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
    	try {
        	playerRepository.save(bravo);
    	}
    	catch (Exception e) {
    		fail("shouldn't have thrown exception "+e.getMessage());
    	}

    }

}

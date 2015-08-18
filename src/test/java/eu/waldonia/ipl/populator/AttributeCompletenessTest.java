package eu.waldonia.ipl.populator;

import static org.junit.Assert.*;

import java.net.*;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.testutil.WrappingServerIntegrationTest;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.waldonia.ipl.PersistenceContext;
import eu.waldonia.ipl.domain.*;
import eu.waldonia.ipl.repository.PlayerRepository;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class AttributeCompletenessTest extends WrappingServerIntegrationTest {
	
	Logger logger = LoggerFactory.getLogger(AttributeCompletenessTest.class);
	
	@Autowired
    RosterSeasonProcessor rsp;
	
	@Autowired
    RosterFileProcessor rfp;
	
	@Autowired
    PlayerRepository playerRepository;
	
	@Autowired
    Session session;
    

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
    }
    
    /**
     * KS doesn't have his bowling arm specified
     */
    @Test
    public void shouldProcessKaranveerSingh() {
    	// run in the file
    	try {
			Map<String, String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/kxip.minimal"));
			
			assertTrue(linesInError.isEmpty());
			Iterable<Player> playaz = playerRepository.findAll();
			
			for (Player player : playaz) {
				logger.info("Processing "+player.name());
				if (player instanceof Bowler) {
					Bowls bowls = player.bowls();
					assertNotNull(bowls);
					assertNotNull(bowls.pace);
				}
			}
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	
    }
    
    // @Test
    public void shouldHaveBowlingPaceKXIP() {
    	// run in the file
    	try {
			Map<String, String> linesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster/kxip.txt"));
			
			assertTrue(linesInError.isEmpty());
			Iterable<Player> playaz = playerRepository.findAll();
			
			for (Player player : playaz) {
				logger.info("Processing "+player.name());
				if (player instanceof Bowler) {
					Bowls bowls = player.bowls();
					assertNotNull(bowls);
					assertNotNull(bowls.pace);
				}
			}
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    // @Test
    public void shouldAllHaveBowlingPace() {
    	// run in the file
    	try {
			Map<String, Map<String, String>> filesInError = rsp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster"));
			
			for (Map<String,String> errors : filesInError.values()) {
				assertTrue(errors.isEmpty());
			}
			
			
			Iterable<Player> playaz = playerRepository.findAll();
			
			
			for (Player player : playaz) {
				logger.info("Processing "+player.name());
				if (player instanceof Bowler) {
					Bowls bowls = player.bowls();
					assertNotNull(bowls);
					assertNotNull(bowls.pace);
				}
			}
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    				
    }
}

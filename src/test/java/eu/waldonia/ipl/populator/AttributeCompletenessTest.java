package eu.waldonia.ipl.populator;

import static org.junit.Assert.*;

import java.net.*;
import java.util.*;

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
    
    @Test
    public void shouldAllHaveRelevantAttributes() {
    	// run in the file
    	try {
			Map<String, Map<String, String>> filesInError = rsp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster"));
			
			for (Map<String,String> errors : filesInError.values()) {
				assertTrue(errors.isEmpty());
			}
		
			Iterable<Player> playaz = playerRepository.findAll();
			for (Player player : playaz) {
				logger.info("Processing "+player.name());
				
				assertTrue(validateBattingHand(player));
				assertTrue(validateDOB(player));
				assertTrue(validateContract(player));
				if (player instanceof Bowler) {
					assertTrue(validateBowlingDetails(player));
				}
			}
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    				
    }

    /*
     * Check that bowling details are valid
     */
    private boolean validateBowlingDetails(final Player player) {
    	Bowls bowls = player.bowls();
    	return (bowls != null && bowls.pace != null);
    }
    
    /*
     * Check they are either right or left handed
     */
    private boolean validateBattingHand(final Player player) {
    	Handedness hand = player.bats();
    	return (hand != null && (hand instanceof Left || hand instanceof Right));
    }
    
    /*
     * Check they have a valid date of birth
     */
    private boolean validateDOB(final Player player) {
    	DOB dob = player.bornOn();
    	return (dob != null && dob.day >=1 && dob.day <= 31 && dob.month >= 1 && dob.month <=13 && dob.year >= 1966 && dob.year <= 2000);
    }
    
    /*
     * Check they have a contract and its valid
     */
    private boolean validateContract(final Player player) {
    	List<Contract> contracts = player.contracts();
    	boolean valid = false;
    	
    	valid = (contracts != null);
    	
    	Iterator<Contract> cList = contracts.iterator();
    	while (valid && cList.hasNext()) {
    		Contract c = cList.next();
    		valid = (c != null);
    		if (valid) {
    			valid = (c.franchise() != null);	// must have another side 
    		}
    	}
    	
    	return valid;
    }
    
    

}

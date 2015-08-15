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
public class RosterSeasonProcessorIntegrationTest extends WrappingServerIntegrationTest {

    @Autowired
    RosterSeasonProcessor rfp;
    
    @Autowired
    Session session;

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
    }
    
	@Test
	public void shouldProcess2015Season() {
		try {
			// run in the file
			Map<String,Map<String,String>> filesInError = rfp.process(new URI("file:///Users/sid/dev/ipl-roster/src/test/resources/2015/roster"));

			for (Map<String,String> fileReport : filesInError.values()) {
				assertTrue(fileReport.isEmpty());				
			}

		} 
		catch (Exception e) {
			fail("Shouldn't have thrown exception "+e.getMessage());
		}
	}

}

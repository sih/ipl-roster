package eu.waldonia.ipl.populator;

import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class RosterFileProcessorTest {

	private RosterFileProcessor rfp;
	
	@Before
	public void setUp() {
		rfp = new RosterFileProcessor();
	}
	
	@Test
	public void shouldFailGracefullyIfNotRosterFile() {
		try {
			URI nofile = new URI("file:///dev/null/null.txt");
			rfp.process(nofile);
			fail("Should have thrown an excpetion");
		}
		catch(IllegalArgumentException e) {
			// all good
		}
		catch(Exception e) {
			fail("Shoudln't have thrown an exception "+e.getMessage());
		}

	}

	
	
}

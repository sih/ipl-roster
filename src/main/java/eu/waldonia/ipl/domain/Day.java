package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
@NodeEntity
public class Day {

	@GraphId Long id;
	
	public Day(int day) {
		this.day = day;
	}
	
	private int day;
	
}

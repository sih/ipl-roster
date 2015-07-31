package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Month {
	
	public Month(int month) {
		this.month = month;
	}
	
	public int month;

	@GraphId Long id;
}

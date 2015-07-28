package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Contract {
	@GraphId Long id;
	public int value;
	public String currency;
	
	public Contract(Year year, int value, String currency) {
		this.year = year;
		this.value = value;
		this.currency = currency;
	}
	
	@Relationship(type = "DATED", direction = Relationship.OUTGOING)
	public Year year;
}

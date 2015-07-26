package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class DOB {

	@GraphId Long id;
	
	@Relationship(type="DAY", direction = Relationship.OUTGOING)
	private Day day;
	
	@Relationship(type="MONTH", direction = Relationship.OUTGOING)
	private Month month;
	
	@Relationship(type="YEAR", direction = Relationship.OUTGOING)
	private Year year;
}

package eu.waldonia.ipl.domain;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
@NodeEntity
public class Player {

	@GraphId Long id;
	public String name;
	
	@Relationship(type="SIGNED", direction = Relationship.OUTGOING)
	private Set<Contract> contracts;
	
	@Relationship(type="BORN", direction = Relationship.OUTGOING)
	private DOB dob;
	
	@Relationship(type="BOWLS", direction = Relationship.OUTGOING)
	private Handedness arm;
	
	@Relationship(type="BATS", direction = Relationship.OUTGOING)
	private Handedness bats;
	
}

package eu.waldonia.ipl.domain;

import java.util.Set;


import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


@NodeEntity
public class Player {
	
	public Player() {
		
	}
	
	public Player(final String name) {
		this.name = name;
	}

	@GraphId Long id;
	public String name;
	
	@Relationship(type="SIGNED", direction = Relationship.OUTGOING)
	private Set<Contract> contracts;
	
	@Relationship(type="BORN", direction = Relationship.OUTGOING)
	private DOB dob;
	
	@Relationship(type="BATS", direction = Relationship.OUTGOING)
	private Handedness bats;

	@Relationship(type="BOWLS", direction = Relationship.OUTGOING)
	public Bowls bowls;
	
	public void bowls(Handedness arm, String pace, String variety) {
		Bowls bowls = new Bowls(this, arm, pace, variety);
		this.bowls = bowls;
	}
}

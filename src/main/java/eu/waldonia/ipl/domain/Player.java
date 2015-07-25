package eu.waldonia.ipl.domain;

import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Player {

	@GraphId Long id;
	public String name;
	
	@RelatedTo(type="SIGNED")
	private Set<Contract> contracts;
	
	@RelatedTo(type="BORN")
	private DOB dob;
	
	@RelatedToVia(type="BOWLS")
	private Handedness arm;
	
	@RelatedTo(type="BATS")
	private Handedness bats;
	
}

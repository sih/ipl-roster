package eu.waldonia.ipl.domain;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Franchise {

	@GraphId Long id;
	public String name;
	public String code;
	
	@Relationship(type="HOLDS", direction = Relationship.OUTGOING)
	private Set<Contract> contracts;
	
}

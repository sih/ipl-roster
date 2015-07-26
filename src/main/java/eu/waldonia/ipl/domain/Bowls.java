package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity
public class Bowls {

	public String pace;
	public String variety;
	
	@StartNode private Player player;
	@EndNode private Handedness arm;
	
	
}

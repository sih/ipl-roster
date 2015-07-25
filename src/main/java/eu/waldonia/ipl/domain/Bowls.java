package eu.waldonia.ipl.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity
public class Bowls {

	public String pace;
	public String variety;
	
	@StartNode private Player player;
	@EndNode private Handedness arm;
	
	
}

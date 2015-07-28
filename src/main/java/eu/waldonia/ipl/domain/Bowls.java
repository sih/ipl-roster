package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="BOWLS")
public class Bowls {

	public Bowls() {}
	
	public Bowls(Player player, Handedness arm, String pace, String variety) {
		this.player = player;
		this.arm = arm;
		this.pace = pace;
		this.variety = variety;
	}
	
	private Long id;
	public String pace;
	public String variety;
	
	@StartNode Player player;
	@EndNode Handedness arm;
	
	
	
	
}
	
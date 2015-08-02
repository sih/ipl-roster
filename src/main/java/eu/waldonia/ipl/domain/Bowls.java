package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="BOWLS")
public class Bowls {


	private Long id;
	
	public static final String OFF_BREAK = "Off-break";
	public static final String LEG_BREAK = "Leg-break";
	public static final String LEG_BREAK_GOOGLY = "Leg-break googly";
	public static final String ORTHODOX = "Orthodox";
	
	public static final String SLOW = "Slow";
	public static final String MEDIUM = "Medium";
	public static final String MEDIUM_FAST = "Medium-fast";
	public static final String FAST_MEDIUM = "Fast-medium";
	public static final String FAST = "Fast";
	
	public Bowls() {}
	
	public Bowls(Player player, Handedness arm, String pace, String variety) {
		this.player = player;
		this.arm = arm;
		this.pace = pace;
		this.variety = variety;
	}
	
	public String pace;
	public String variety;
	
	@StartNode public Player player;
	@EndNode public Handedness arm;
	
	public Handedness arm() {
		return arm;
	}
	
	
}
	
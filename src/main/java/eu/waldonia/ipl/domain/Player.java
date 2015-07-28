package eu.waldonia.ipl.domain;

import java.util.HashSet;
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
	public Set<Signs> signs;
	
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
	
	/**
	 * @param contract The contract to link to the player and franchise
	 * @param franchise The franchise to pass the contract to
	 * @param shirtNumber The shirt number for this player
	 */
	public void signed(Contract contract, Franchise franchise, Integer shirtNumber) {
		if (null == signs) signs = new HashSet<Signs>();
		signs.add(new Signs(this,contract,shirtNumber));
		franchise.holds(contract);
	}
}

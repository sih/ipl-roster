package eu.waldonia.ipl.domain;

import java.util.*;
import java.util.stream.*;

import org.neo4j.ogm.annotation.*;


@NodeEntity
public class Player {
	
	public Player() {
		
	}
	
	public Player(final String name) {
		this.name = name;
	}

	@GraphId private Long id;
	private String name;
	
	@Relationship(type="SIGNED", direction = Relationship.OUTGOING)
	private Set<Signs> signs;
	
	@Relationship(type="BORN", direction = Relationship.OUTGOING)
	private DOB bornOn;
	
	@Relationship(type="BATS", direction = Relationship.OUTGOING)
	private Handedness bats;

	@Relationship(type="BOWLS", direction = Relationship.OUTGOING)
	private Bowls bowls;

	@Relationship(type="REPRESENTS", direction = Relationship.OUTGOING)
	private Country country;
	
	
	public Country country() {
		return country;
	}
	
	public void country(Country country) {
		this.country = country;
	}
	
	public void bowls(Handedness arm, String pace, String variety) {
		Bowls bowls = new Bowls(this, arm, pace, variety);
		this.bowls = bowls;
	}
	
	public void bats(Handedness h) {
		this.bats = h;
	}
	
	public Set<Signs> signs() {
		return signs;
	}
	
	/**
	 * @param contract The contract to link to the player and franchise
	 * @param franchise The franchise to pass the contract to
	 * @param shirtNumber The shirt number for this player
	 */
	public void signed(Contract contract, Franchise franchise, Integer shirtNumber) {
		if (null == signs) {
			this.signs = new HashSet<Signs>();
		}
		this.signs.add(new Signs(this,contract,shirtNumber));
		franchise.holds(contract);
	}
	
	public List<Contract> contracts() {
		return this.signs.stream()
			.map(s -> s.contract())
			.collect(Collectors.toList());
	}

	public Long id() {
		return id;
	}
	
	
	public Bowls bowls() {
		return bowls;
	}
	
	public String name() {
		return name;
	}
	
	public void name(final String name) {
		this.name = name;
	}
	
	public DOB bornOn() {
		return this.bornOn;
	}
	
	public void bornOn(final DOB bornOn) {
		this.bornOn = bornOn;
	}
	
	public Handedness bats() {
		return this.bats;
	}
	
}

package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Country {
	
	@GraphId Long id;
	private String name;
	
	public Country(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
}

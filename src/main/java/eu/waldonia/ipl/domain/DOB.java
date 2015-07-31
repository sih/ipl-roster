package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class DOB {

	@GraphId Long id;
	
	public DOB(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year; 
	}

	public int day;
	public int month;
	public int year;
	
}

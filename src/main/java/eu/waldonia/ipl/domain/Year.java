package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Year {

	@GraphId Long id;
	
	private int year;
	
	/**
	 * Default to the current year if none supplied
	 */
	public Year() {
		this(java.time.Year.now().getValue());
	}
	
	/**
	 * @param year The integer value of the year to set
	 */
	public Year(int year) {
		this.year = year;
	}
}

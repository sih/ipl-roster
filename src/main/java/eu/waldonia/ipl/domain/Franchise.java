package eu.waldonia.ipl.domain;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Franchise {

	@GraphId Long id;
	private String name;
	private String code;

	/**
	 * @param code The code of this franchise, e.g. CSK
	 */
	public Franchise(String code) {
		this.code = code;
	}
	
	/**
	 * @param code The code of this franchise, e.g. CSK
	 * @param name The name of this franchise, e.g. Chennai Super Kings
	 */
	public Franchise(String code, String name) {
		this(code);
		this.name = name;
	}
	
	@Relationship(type="HOLDS", direction = Relationship.OUTGOING)
	public Set<Contract> contracts;
	
	public void holds(Contract c) {
		if (null == contracts) {
			contracts = new HashSet<Contract>();
		}
		this.contracts.add(c);
		c.franchise(this);
	}
	
	public String code() {
		return code;
	}

	
	
}

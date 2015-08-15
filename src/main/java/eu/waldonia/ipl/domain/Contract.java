package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author sid
 *
 */
@NodeEntity
public class Contract {
	@GraphId Long id;
	private int value;
	private String currency;
	private Franchise franchise;
	private Player player;
	private boolean traded;			// for issue #14 where traded players don't have a conrtact value set
	private boolean replacement;	// for issue #14 where traded players don't have a conrtact value set

	/**
	 * Basic constructor with mandatory fields
	 * @param year
	 * @param p
	 */
	public Contract(Year year, Player p) {
		this.year = year;
		this.player = p;
	}

	
	/**
	 * Convenience constructor for those (most) players hwo have a contract value
	 * @param year
	 * @param value
	 * @param currency
	 * @param p
	 */
	public Contract(Year year, int value, String currency, Player p) {
		this(year,p);
		this.value = value;
		this.currency = currency;
	}
	
	
	@Relationship(type = "DATED", direction = Relationship.OUTGOING)
	private Year year;
	
	public void franchise(Franchise f) {
		this.franchise = f;
	}
	
	public Franchise franchise() {
		return franchise;
	}
	
	public void value(int v) {
		this.value = v;
	}
	
	public int value() {
		return value;
	}
	
	public Year year() {
		return year;
	}
	
	public String currency() {
		return currency;
	}
	
	public Player player() {
		return player;
	}
	
	public boolean traded() {
		return traded;
	}
	
	public void traded(final boolean traded) {
		this.traded = traded;
	}
	
	public boolean replacement() {
		return replacement;
	}
	
	public void replacement(final boolean replacement) {
		this.replacement = replacement;
	}
}

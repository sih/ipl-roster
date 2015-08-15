package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Contract {
	@GraphId Long id;
	private int value;
	private String currency;
	private Franchise franchise;
	private Player player;
	private boolean traded;
	
	public Contract(Year year, int value, String currency, Player p) {
		this.year = year;
		this.value = value;
		this.currency = currency;
		this.player = p;
	}
	
	public Contract(Year year, Player p, boolean traded) {
		this.traded(traded);
		this.year = year;
		this.player = p;
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
}

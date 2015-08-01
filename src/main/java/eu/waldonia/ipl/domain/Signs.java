package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Specific relationship entity for a player signing a contract as we'll store
 * the players number along with this.
 * @author sid
 *
 */
@RelationshipEntity(type="SIGNED")
public class Signs {
	
	Long id;

	public Integer shirtNumber;

	/**
	 * A player might not have a shirt number (yet). In this case use this constructor
	 * @param player The player signing the contract
	 * @param contract The contract the player signed
	 */
	public Signs(Player player, Contract contract) {
		this(player, contract, null);
	}
	
	/**
	 * @param shirtNumber The player's shirt number with the franchise this year
	 */
	public Signs(Player player, Contract contract, Integer shirtNumber) {
		this.player = player;
		this.contract = contract;
		this.shirtNumber = shirtNumber;
	}
	
	@StartNode public Player player;
	@EndNode public Contract contract;
}

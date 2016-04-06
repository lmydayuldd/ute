/**
 * 
 */
package at.sume.dm.indicators.simple;

import java.util.ArrayList;

import at.sume.dm.entities.PersonRow;
import at.sume.dm.types.HouseholdType;

/**
 * @author Alexander Remesch
 *
 */
public interface HouseholdCharacteristics {
	public HouseholdType getHouseholdType();
	public short getHouseholdSize();
	public ArrayList<PersonRow> getMembers();
}

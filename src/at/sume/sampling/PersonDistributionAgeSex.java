package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.dm.types.AgeGroup;
import at.sume.sampling.distributions.PersonsPerAgeSex;

public class PersonDistributionAgeSex {
	private Distribution<PersonsPerAgeSex> personsPerAgeSexRepr, personsPerAgeSexNonRepr;
	private PersonsPerAgeSex result;
	private byte resultAge;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public PersonDistributionAgeSex(Database db, int spatialUnitId) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException
	{
		// Load distribution for household representatives
		String sqlStatement = "select SpatialUnitId, Sex, AgeGroupId, HouseholdRepresentative, PersCount " + 
			"from [_DM_Persons per age, sex, hh-repr, spatial unit] where SpatialUnitId = " + spatialUnitId + " and HouseholdRepresentative = -1;";
		ArrayList<PersonsPerAgeSex> p = db.select(PersonsPerAgeSex.class, sqlStatement);
		personsPerAgeSexRepr = new Distribution<PersonsPerAgeSex>(p, "persCount");
		// Load distribution for non-representatives
		sqlStatement = "select SpatialUnitId, Sex, AgeGroupId, HouseholdRepresentative, PersCount " + 
			"from [_DM_Persons per age, sex, hh-repr, spatial unit] where SpatialUnitId = " + spatialUnitId + " and HouseholdRepresentative = 0;";
		p = db.select(PersonsPerAgeSex.class, sqlStatement);
		personsPerAgeSexNonRepr = new Distribution<PersonsPerAgeSex>(p, "persCount");
	}
	
	public void randomSample(boolean householdRepresentative) {
		if (householdRepresentative) {
			result = personsPerAgeSexRepr.get(personsPerAgeSexRepr.randomSample());
			// Group 15-19: person must be of full age to be a household representative!
			if ((AgeGroup.getMinAge(result.ageGroupId) < 18) && (AgeGroup.getMaxAge(result.ageGroupId) >= 18)) {
				Random r = new Random();
				resultAge = (byte) (18 + r.nextInt(2));
			}
		} else {
			result = personsPerAgeSexNonRepr.get(personsPerAgeSexNonRepr.randomSample());
		}
		resultAge = AgeGroup.sampleAge(result.ageGroupId);
	}
	
	public byte getSampledAge() {
		return resultAge;
	}
	public byte getSampledAgeGroupId() {
		return result.ageGroupId;
	}
	public byte getSampledSex() {
		return result.sex;
	}
}

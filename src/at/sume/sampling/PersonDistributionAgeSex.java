package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.types.AgeGroup16;
import at.sume.sampling.distributions.PersonsPerAgeSex;
import net.remesch.db.Database;
import net.remesch.util.Random;

public class PersonDistributionAgeSex {
	private Distribution<PersonsPerAgeSex> personsPerAgeSexRepr, personsPerAgeSexNonRepr;
	private PersonsPerAgeSex result;
	private short resultAge;
	private Random r = new Random();

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
			"from [_DM_Persons per age, sex, hh-repr, spatial unit] where SpatialUnitId = " + spatialUnitId + " and HouseholdRepresentative <> 0;";
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
			if ((AgeGroup16.getMinAge(result.ageGroupId) < 18) && (AgeGroup16.getMaxAge(result.ageGroupId) >= 18)) {
				resultAge = (byte) (18 + r.nextInt(2));
			}
		} else {
			result = personsPerAgeSexNonRepr.get(personsPerAgeSexNonRepr.randomSample());
		}
		resultAge = AgeGroup16.sampleAge(result.ageGroupId);
	}
	
	public short getSampledAge() {
		return resultAge;
	}
	public byte getSampledAgeGroupId() {
		return result.ageGroupId;
	}
	public byte getSampledSex() {
		return result.sex;
	}
}

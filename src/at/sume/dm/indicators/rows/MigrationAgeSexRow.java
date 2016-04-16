/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class MigrationAgeSexRow implements Comparable<MigrationAgeSexRow>, Fileable {
	private byte sex;
	private byte ageGroupId;
	private HouseholdType householdType;
	private MigrationRealm migrationRealm;
	private int personCount;
	
	public MigrationAgeSexRow() {
	}

	/**
	 * @return the sex
	 */
	public byte getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}

	/**
	 * @return the ageGroupId
	 */
	public byte getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(byte ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/**
	 * @return the householdType
	 */
	public HouseholdType getHouseholdType() {
		return householdType;
	}

	/**
	 * @param householdType the householdType to set
	 */
	public void setHouseholdType(HouseholdType householdType) {
		this.householdType = householdType;
	}

	/**
	 * @return the migrationRealm
	 */
	public MigrationRealm getMigrationRealm() {
		return migrationRealm;
	}

	/**
	 * @param migrationRealm the migrationRealm to set
	 */
	public void setMigrationRealm(MigrationRealm migrationRealm) {
		this.migrationRealm = migrationRealm;
	}

	/**
	 * @return the personCount
	 */
	public int getPersonCount() {
		return personCount;
	}

	/**
	 * @param personCount the personCount to set
	 */
	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}

	@Override
	public int compareTo(MigrationAgeSexRow o) {
		int result = ((Byte)sex).compareTo(o.sex);
		if (result != 0)
			return result;
		result = ((Byte)ageGroupId).compareTo(o.ageGroupId);
		if (result != 0)
			return result;
		result = migrationRealm.compareTo(o.migrationRealm);
		if (result != 0)
			return result;
		result = householdType.compareTo(o.householdType);
		return result;
	}

	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + "Sex" + delimiter + "AgeGroup" + delimiter + "HouseholdType" + delimiter + "MigrationType" + delimiter + "PersonCount";
	}

	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + sex + delimiter + ageGroupId + delimiter + householdType + delimiter + migrationRealm + delimiter + personCount;
	}
}

/**
 * 
 */
package at.sume.dm.model.output;

import at.sume.dm.entities.PersonRow;
import at.sume.dm.model.timeuse.TimeUseType;

/**
 * @author Alexander Remesch
 *
 */
public class OutputPersonRow implements OutputRow {
	private short modelYear;
	private int personId;
	private int householdId;
	private byte sex;
	private short age;
	private int yearlyIncome;
	private int workplaceCellId;
	private TimeUseType timeUseType;

	public OutputPersonRow(short modelYear, PersonRow person) {
		this.personId = person.getPersonId();
		this.modelYear = modelYear;
		this.householdId = person.getHousehold().getHouseholdId();
		this.sex = person.getSex();
		this.age = person.getAge();
		this.yearlyIncome = person.getYearlyIncome();
		this.workplaceCellId = person.getWorkplaceCellId();
		this.timeUseType = person.getTimeUseType();
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.OutputRow#toCsv()
	 */
	@Override
	public String toCsv() {
		return modelYear + ";" + personId + ";" + householdId + ";" + sex + ";" + age + ";" + yearlyIncome + ";" + workplaceCellId + ";" + timeUseType;
	}
}

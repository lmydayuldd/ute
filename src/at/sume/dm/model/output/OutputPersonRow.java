/**
 * 
 */
package at.sume.dm.model.output;

import at.sume.dm.entities.PersonRow;

/**
 * @author Alexander Remesch
 *
 */
public class OutputPersonRow implements OutputRow {
	private short modelYear;
	private int personId;
	private int householdId;
	private byte sex;
	private byte age;
	private int yearlyIncome;

	public OutputPersonRow(short modelYear, PersonRow person) {
		this.personId = person.getPersonId();
		this.modelYear = modelYear;
		this.householdId = person.getHousehold().getHouseholdId();
		this.sex = person.getSex();
		this.age = person.getAge();
		this.yearlyIncome = person.getYearlyIncome();
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.OutputRow#toCsv()
	 */
	@Override
	public String toCsv() {
		return modelYear + ";" + personId + ";" + householdId + ";" + sex + ";" + age + ";" + yearlyIncome;
	}
}

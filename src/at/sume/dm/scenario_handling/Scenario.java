/**
 * 
 */
package at.sume.dm.scenario_handling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.db.Database;

/**
 * Scenario handling for the model
 * 
 * @author Alexander Remesch
 */
public class Scenario {
	private String scenarioName;
	private String migrationScenario;
	private String migrationHouseholdSizeScenario;
	private String householdPrefsScenario;
	private String buildingProjectScenario;
	private String additionalDwellingsScenario;
	private String newDwellingSizeScenario;
	private String rentScenario;
	private String fertilityScenario;
	private String migrationIncomeScenario;
	private String travelTimesScenario;
	private String timeUseTypeScenario;
	private String travelTimeModifierScenario;
	private String migrationPerAgeSexScenario;
	
	public Scenario(Database db, short scenarioId) throws SQLException {
		this(db, scenarioId, true);
	}
	public Scenario(Database db, short scenarioId, boolean verbose) throws SQLException {
		String sql = "select * from _DM_Scenarios where scenarioId = ?";
		PreparedStatement ps = db.con.prepareStatement(sql);
		ps.setShort(1, scenarioId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			scenarioName = rs.getString("ScenarioName");
			migrationScenario = rs.getString("MigrationScenarioName");
			migrationHouseholdSizeScenario = rs.getString("MigrationHouseholdSizeScenarioName");
			householdPrefsScenario = rs.getString("HouseholdPrefsScenarioName");
			buildingProjectScenario = rs.getString("BuildingProjectScenarioName");
			additionalDwellingsScenario = rs.getString("AdditionalDwellingsScenarioName");
			newDwellingSizeScenario = rs.getString("NewDwellingSizeScenarioName");
			migrationIncomeScenario = rs.getString("MigrationIncomeScenarioName");
			rentScenario = rs.getString("RentScenarioName");
			if (rentScenario == null) rentScenario = "NULL";
			fertilityScenario = rs.getString("FertilityScenarioName");
			travelTimesScenario = rs.getString("TravelTimesScenarioName");
			timeUseTypeScenario = rs.getString("TimeUseTypeScenarioName");
			travelTimeModifierScenario = rs.getString("TravelTimeModifierScenarioName");
			migrationPerAgeSexScenario = rs.getString("MigrationPerAgeSexScenarioName");
			if (verbose) {
				System.out.println("    Scenario: MigrationPerAgeSexScenarioName = " + migrationPerAgeSexScenario);
				System.out.println("    Scenario: " + scenarioName + " (id = " + scenarioId + ")");
				System.out.println("    Scenario: MigrationScenarioName = " + migrationScenario);
				System.out.println("    Scenario: MigrationHouseholdSizeScenarioName = " + migrationHouseholdSizeScenario);
				System.out.println("    Scenario: HouseholdPrefsScenarioName = " + householdPrefsScenario);
				System.out.println("    Scenario: BuildingProjectScenarioName = " + buildingProjectScenario);
				System.out.println("    Scenario: AdditionalDwellingsScenarioName = " + additionalDwellingsScenario);
				System.out.println("    Scenario: NewDwellingSizeScenarioName = " + newDwellingSizeScenario);
				System.out.println("    Scenario: MigrationIncomeScenarioName = " + migrationIncomeScenario);
				System.out.println("    Scenario: RentScenarioName = " + rentScenario);
				System.out.println("    Scenario: FertilityScenarioName = " + fertilityScenario);
				System.out.println("    Scenario: TravelTimesScenarioName = " + travelTimesScenario);
				System.out.println("    Scenario: TimeUseTypeScenarioName = " + timeUseTypeScenario);
				System.out.println("    Scenario: TravelTimeModifierScenarioName = " + travelTimeModifierScenario);
			}
		} else {
			throw new AssertionError("Scenario " + scenarioId + " not found!");
		}
	}

	/**
	 * @return the scenarioName
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * @return the migrationScenario
	 */
	public String getMigrationScenario() {
		return migrationScenario;
	}

	/**
	 * @return the migrationHouseholdSizeScenario
	 */
	public String getMigrationHouseholdSizeScenario() {
		return migrationHouseholdSizeScenario;
	}

	/**
	 * @return the householdPrefsScenario
	 */
	public String getHouseholdPrefsScenario() {
		return householdPrefsScenario;
	}

	/**
	 * @return the buildingProjectScenario
	 */
	public String getBuildingProjectScenario() {
		return buildingProjectScenario;
	}

	/**
	 * @return the additionalDwellingsScenario
	 */
	public String getAdditionalDwellingsScenario() {
		return additionalDwellingsScenario;
	}

	/**
	 * @return the newDwellingSizeScenario
	 */
	public String getNewDwellingSizeScenario() {
		return newDwellingSizeScenario;
	}

	/**
	 * @return the rentScenario
	 */
	public String getRentScenario() {
		return rentScenario;
	}

	/**
	 * @return the fertilityScenario
	 */
	public String getFertilityScenario() {
		return fertilityScenario;
	}

	/**
	 * @return the migrationIncomeScenario
	 */
	public String getMigrationIncomeScenario() {
		return migrationIncomeScenario;
	}
	/**
	 * 
	 * @return
	 */
	public String getTravelTimesScenario() {
		return travelTimesScenario;
	}

	/**
	 * @return the timeUseTypeScenario
	 */
	public String getTimeUseTypeScenario() {
		return timeUseTypeScenario;
	}

	/**
	 * @return the travelTimeModifierScenario
	 */
	public String getTravelTimeModifierScenario() {
		return travelTimeModifierScenario;
	}

	/**
	 * @return the migrationPerAgeSexScenario
	 */
	public String getMigrationPerAgeSexScenario() {
		return migrationPerAgeSexScenario;
	}
}

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
	
	public Scenario(Database db, short scenarioId) throws SQLException {
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
}

/**
 * 
 */
package at.sume.sampling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.sume.sampling.distributions.CommuterMatrixRow;
import at.sume.sampling.distributions.WorkplacesPerZBRow;
import net.remesch.db.Database;

/**
 * Sampling of person's workplaces for Monte Carlo-sampling of synthetic agents,
 * dependent on person's place of residence, 
 * data from tables "_DM_CommuterMatrix_AD" and "_DM_Workplaces_ZB"
 *
 * @author Alexander Remesch
 */
public class SampleWorkplaces {
	Distribution<CommuterMatrixRow> commuterMatrix;
	HashMap<Integer,Distribution<WorkplacesPerZBRow>> workplaceADtoZB;

	/**
	 * Load the distributions of workplaces per place of residence (AD) and workplaces per Zähbezirk
	 * @param db
	 * @param residenceId
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 */
	public SampleWorkplaces(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT DISTINCT WorkplaceAD FROM _DM_Workplaces_ZB ORDER BY WorkplaceAD";
		ResultSet resultSet = db.executeQuery(sqlStatement);
		List<Integer> districts = new ArrayList<Integer>();
		while (resultSet.next()) {
			districts.add(resultSet.getInt(1));
		}
		assert districts.size() > 0 : "No records found from '" + sqlStatement + "'";
		// load workplaces per Zählbezirk (this doesn't change with the residenceId, so it could be done only once, on the other hand it shouldn't be so costly...)
		workplaceADtoZB = new HashMap<Integer,Distribution<WorkplacesPerZBRow>>();
		for (Integer d : districts) {
			sqlStatement = "select WorkplaceZB, Persons from _DM_Workplaces_ZB where WorkplaceAD = " + d + " order by WorkplaceZB";
			ArrayList<WorkplacesPerZBRow> w = db.select(WorkplacesPerZBRow.class, sqlStatement);
			assert w.size() > 0 : "No records found from '" + sqlStatement + "'";
			workplaceADtoZB.put(d, new Distribution<WorkplacesPerZBRow>(w, "persons"));
		}
	}
	/**
	 * Load the commuter matrix for a given residenceId (i.e. the number of workplaces per administrative district for a given residential district)
	 * @param residenceId
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public void loadCommuterMatrix(Database db, int residenceId) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		// Convert residenceId to AD level
		int r = Integer.parseInt(Integer.toString(residenceId).substring(0, 3) + "01");
		// Load commuter matrix for the residenceId
		String sqlStatement = "select WorkplaceAD, Persons from _DM_CommuterMatrix_AD where ResidenceAD = " + r + " order by WorkplaceAD";
		ArrayList<CommuterMatrixRow> workplaceDistribution = db.select(CommuterMatrixRow.class, sqlStatement);
		assert workplaceDistribution.size() > 0 : "No records found from '" + sqlStatement + "'";
		commuterMatrix = new Distribution<CommuterMatrixRow>(workplaceDistribution, "persons");
	}
	/**
	 * Sample a workplace (Zählbezirk)
	 * @return A random workplace for the given place of residence 
	 */
	public int randomSample() {
		CommuterMatrixRow resultAD = commuterMatrix.get(commuterMatrix.randomSample());
		if (resultAD.workplaceAD == 3 || resultAD.workplaceAD == 1) { // Don't get the details for surroundings of Vienna (NÖ/Bgld)
			return resultAD.workplaceAD;
		} else {
			Distribution<WorkplacesPerZBRow> d = workplaceADtoZB.get(resultAD.workplaceAD);
			return d.get(d.randomSample()).workplaceZB;
		}
	}
}

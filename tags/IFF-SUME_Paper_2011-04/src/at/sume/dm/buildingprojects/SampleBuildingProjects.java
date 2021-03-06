/**
 * 
 */
package at.sume.dm.buildingprojects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.types.LivingSpaceGroup6;
import at.sume.sampling.Distribution;

/**
 * Sampling of the 2a (2001 - 2025) + 2b (2026 - 2050) residential building projects. The base data has
 * been supplied to us by the �IR.
 * 
 * @author Alexander Remesch
 */
public class SampleBuildingProjects {
	public static class NewDwellingSize implements Comparable<NewDwellingSize> {
		private int newDwellingSizeId;
		private byte livingSpaceGroupId;
		public double share;
		
		/**
		 * @return the newDwellingSizeId
		 */
		public int getNewDwellingSizeId() {
			return newDwellingSizeId;
		}
		/**
		 * @param newDwellingSizeId the newDwellingSizeId to set
		 */
		public void setNewDwellingSizeId(int newDwellingSizeId) {
			this.newDwellingSizeId = newDwellingSizeId;
		}
		/**
		 * @return the livingSpaceGroupId
		 */
		public byte getLivingSpaceGroupId() {
			return livingSpaceGroupId;
		}
		/**
		 * @param livingSpaceGroupId the livingSpaceGroupId to set
		 */
		public void setLivingSpaceGroupId(byte livingSpaceGroupId) {
			this.livingSpaceGroupId = livingSpaceGroupId;
		}
		/**
		 * @return the share
		 */
		public double getShare() {
			return share;
		}
		/**
		 * @param share the share to set
		 */
		public void setShare(double share) {
			this.share = share;
		}
		@Override
		public int compareTo(NewDwellingSize o) {
			return ((Integer)newDwellingSizeId).compareTo(o.newDwellingSizeId);
		}
	}

	private Distribution<NewDwellingSize> newDwellingSize;
	private BuildingProjectsPerYear buildingProjectsPerYear;
	private SpatialUnits spatialUnits;

	public SampleBuildingProjects(String buildingProjectScenarioName, String newDwellingSizeScenarioName, SpatialUnits spatialUnits) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT newDwellingSizeId, livingSpaceGroupId, share " +
			"FROM _DM_NewDwellingSize " +
			"WHERE newDwellingSizeScenarioName = '" + newDwellingSizeScenarioName + "' " +
			"ORDER BY newDwellingSizeId, livingSpaceGroupId";
		newDwellingSize = new Distribution<NewDwellingSize>(Common.db.select(NewDwellingSize.class, selectStatement), "share");
		assert newDwellingSize.size() > 0 : "No rows selected from _DM_NewDwellingSize (scenarioName = " + newDwellingSizeScenarioName + ")";

		buildingProjectsPerYear = new BuildingProjectsPerYear(buildingProjectScenarioName, spatialUnits.getSpatialUnitLevel());
		
		this.spatialUnits = spatialUnits;
	}
	/**
	 * Sample newly built dwellings for the given model year that are part of a large building project
	 * @param modelYear
	 * @return
	 */
	public List<DwellingRow> sample(int modelYear) {
		List<DwellingRow> result = new ArrayList<DwellingRow>();
		// Get total number of new dwellings per spatial unit for this model year
		List<NewDwellingsPerSpatialUnit> newDwellingsPerSpatialUnit = buildingProjectsPerYear.getNewDwellingCount(modelYear);
		// Sample them!
		for (NewDwellingsPerSpatialUnit n : newDwellingsPerSpatialUnit) {
			for (int i = 0; i != n.newDwellingCount; i++) {
				DwellingRow dwelling = new DwellingRow();
				dwelling.setSpatialunit(spatialUnits.lookup(n.spatialUnitId));
				int index = newDwellingSize.randomSample();
				dwelling.setLivingSpaceGroup6Id(newDwellingSize.get(index).livingSpaceGroupId);
				dwelling.setDwellingSize(LivingSpaceGroup6.sampleLivingSpace(dwelling.getLivingSpaceGroup6Id()));
				dwelling.setSpatialunitId(n.spatialUnitId);
				dwelling.calcTotalYearlyDwellingCosts(true);
				result.add(dwelling);
			}
		}
		return result;
	}
	/**
	 * Sample newly built dwellings that are randomly scattered over the city area
	 * @param numberOfDwellings
	 * @return
	 */
	public List<DwellingRow> sampleRandomDwellings(int numberOfDwellings) {
		Random r = new Random();
		List<DwellingRow> result = new ArrayList<DwellingRow>();
		for (int i = 0; i != numberOfDwellings; i++) {
			DwellingRow dwelling = new DwellingRow();
			int index = r.nextInt(spatialUnits.size());
			SpatialUnitRow spatialUnit = spatialUnits.get(index);
			while (spatialUnit.isFreeDwellingsAlwaysAvailable()) {
				index = r.nextInt(spatialUnits.size());
				spatialUnit = spatialUnits.get(index);
			}
			dwelling.setSpatialunit(spatialUnit);
			index = newDwellingSize.randomSample();
			dwelling.setLivingSpaceGroup6Id(newDwellingSize.get(index).livingSpaceGroupId);
			dwelling.setDwellingSize(LivingSpaceGroup6.sampleLivingSpace(dwelling.getLivingSpaceGroup6Id()));
			dwelling.setSpatialunitId(spatialUnit.getSpatialUnitId());
			dwelling.calcTotalYearlyDwellingCosts(true);
			result.add(dwelling);
		}
		return result;
	}
}

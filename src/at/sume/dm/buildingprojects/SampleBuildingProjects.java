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
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.dm.types.LivingSpaceGroup6;
import at.sume.sampling.Distribution;

/**
 * Sampling of the 2a (2001 - 2025) + 2b (2026 - 2050) residential building projects. The base data has
 * been supplied to us by the ÖIR.
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

	public SampleBuildingProjects(String scenarioName, SpatialUnits spatialUnits) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT newDwellingSizeId, livingSpaceGroupId, share " +
			"FROM _DM_NewDwellingSize " +
			"WHERE newDwellingSizeScenarioName = '" + scenarioName + "' " +
			"ORDER BY newDwellingSizeId, livingSpaceGroupId";
		newDwellingSize = new Distribution<NewDwellingSize>(Common.db.select(NewDwellingSize.class, selectStatement), "share");
		assert newDwellingSize.size() > 0 : "No rows selected from _DM_NewDwellingSize (scenarioName = " + scenarioName + ")";

		buildingProjectsPerYear = new BuildingProjectsPerYear(scenarioName, spatialUnits.getSpatialUnitLevel());
		
		this.spatialUnits = spatialUnits;
	}
	/**
	 * Sample newly built dwellings for the given model year 
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
				// TODO: this must be done by the DwellingRow class
				Random r = new Random();
				long yearlyRentPer100Sqm = RentPerSpatialUnit.getYearlyAverageRentPer100Sqm(dwelling.getSpatialunitId()) / 100;
				// TODO: 20% random deviance from the avg. rent price -> sysparam!
				yearlyRentPer100Sqm = yearlyRentPer100Sqm + Math.round(yearlyRentPer100Sqm * (r.nextGaussian() - 0.5) * 0.1);
				int dwellingCosts = Math.round(dwelling.getDwellingSize() * yearlyRentPer100Sqm / 100);
				dwelling.setTotalYearlyDwellingCosts(dwellingCosts);
				result.add(dwelling);
			}
		}
		return result;
	}
}

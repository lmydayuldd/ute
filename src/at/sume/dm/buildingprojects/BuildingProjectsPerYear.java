package at.sume.dm.buildingprojects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.entities.SpatialUnitLevel;


public class BuildingProjectsPerYear {
	public static class BuildingProjectRow implements Comparable<BuildingProjectRow> {
		private int buildingProjectId;
		private short modelYearStart;
		private short modelYearFinish;
		private int spatialUnitId;
		private int dwellingCount;
		private int dwellingCountPerYear;

		/**
		 * @return the buildingProjectId
		 */
		public int getBuildingProjectId() {
			return buildingProjectId;
		}

		/**
		 * @param buildingProjectId the buildingProjectId to set
		 */
		public void setBuildingProjectId(int buildingProjectId) {
			this.buildingProjectId = buildingProjectId;
		}

		/**
		 * @return the modelYearStart
		 */
		public short getModelYearStart() {
			return modelYearStart;
		}

		/**
		 * @param modelYearStart the modelYearStart to set
		 */
		public void setModelYearStart(short modelYearStart) {
			this.modelYearStart = modelYearStart;
		}

		/**
		 * @return the modelYearFinish
		 */
		public short getModelYearFinish() {
			return modelYearFinish;
		}

		/**
		 * @param modelYearFinish the modelYearFinish to set
		 */
		public void setModelYearFinish(short modelYearFinish) {
			this.modelYearFinish = modelYearFinish;
		}

		/**
		 * @return the spatialUnitId
		 */
		public int getSpatialUnitId() {
			return spatialUnitId;
		}

		/**
		 * @param spatialUnitId the spatialUnitId to set
		 */
		public void setSpatialUnitId(int spatialUnitId) {
			this.spatialUnitId = spatialUnitId;
		}

		/**
		 * @return the dwellingCount
		 */
		public int getDwellingCount() {
			return dwellingCount;
		}

		/**
		 * @param dwellingCount the dwellingCount to set
		 */
		public void setDwellingCount(int dwellingCount) {
			this.dwellingCount = dwellingCount;
		}

		/**
		 * @return the dwellingCountPerYear
		 */
		public int getDwellingCountPerYear() {
			return dwellingCountPerYear;
		}

		/**
		 * @param dwellingCountPerYear the dwellingCountPerYear to set
		 */
		public void setDwellingCountPerYear(int dwellingCountPerYear) {
			this.dwellingCountPerYear = dwellingCountPerYear;
		}

		@Override
		public int compareTo(BuildingProjectRow o) {
			return ((Integer)buildingProjectId).compareTo(o.buildingProjectId);
		}
	}

	private ArrayList<BuildingProjectRow> buildingProjects;

	public BuildingProjectsPerYear(String scenarioName, SpatialUnitLevel spatialUnitLevel) throws SQLException, InstantiationException, IllegalAccessException {
		String spatialUnitFieldName = "spatialUnitId_" + spatialUnitLevel.toString();
		String selectStatement = "SELECT buildingProjectId, modelYearStart, modelYearFinish, " + spatialUnitFieldName + ", dwellingCount " +
			"FROM _DM_BuildingProjects " +
			"WHERE buildingProjectScenarioName = '" + scenarioName + "' " +
			"ORDER BY buildingProjectId";
//		buildingProjects = Common.db.select(BuildingProjectRow.class, selectStatement);
		buildingProjects = new ArrayList<BuildingProjectRow>();
		ResultSet rs = Common.db.executeQuery(selectStatement);
		while (rs.next()) {
			BuildingProjectRow b = new BuildingProjectRow();
			b.setBuildingProjectId(rs.getInt("BuildingProjectId"));
			b.setSpatialUnitId(rs.getInt(spatialUnitFieldName));
			b.setModelYearStart(rs.getShort("ModelYearStart"));
			b.setModelYearFinish(rs.getShort("ModelYearFinish"));
			b.setDwellingCount(rs.getInt("DwellingCount"));
			buildingProjects.add(b);
		}
		rs.close();
		assert buildingProjects.size() > 0 : "No rows selected from _DM_BuildingProjects (scenarioName = " + scenarioName + ")";
		
		// calculate the new dwellings per year
		for (BuildingProjectRow b : buildingProjects) {
			b.setDwellingCountPerYear(b.getDwellingCount() / (b.getModelYearFinish() - b.getModelYearStart()));
		}
	}
	/**
	 * Calculate the number of new-built dwellings per spatial unit for a given model year
	 * @param spatialUnitId
	 * @param modelYear
	 * @return
	 */
	public List<NewDwellingsPerSpatialUnit> getNewDwellingCount(int modelYear) {
		List<NewDwellingsPerSpatialUnit> result = new ArrayList<NewDwellingsPerSpatialUnit>();
		for (BuildingProjectRow b : buildingProjects) {
				if ((b.getModelYearStart() <= modelYear) && (modelYear <= b.getModelYearFinish())) {
					NewDwellingsPerSpatialUnit n = new NewDwellingsPerSpatialUnit();
					n.spatialUnitId = b.getSpatialUnitId();
					n.newDwellingCount = b.getDwellingCountPerYear();
					result.add(n);
				}
		}
		return result;
	}
}

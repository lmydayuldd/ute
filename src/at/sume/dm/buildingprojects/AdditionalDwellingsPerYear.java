/**
 * 
 */
package at.sume.dm.buildingprojects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
 */
public class AdditionalDwellingsPerYear {
	public static class AdditionalDwellingRow {
		private short modelYearStart;
		private short modelYearFinish;
		private short additionalDwellingsOnMarket;
		private short newlyBuiltDwellings;
		private int additionalDwellingsOnMarketPerYear;
		private int newlyBuiltDwellingsPerYear;
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
		 * @return the additionalDwellingsOnMarket
		 */
		public short getAdditionalDwellingsOnMarket() {
			return additionalDwellingsOnMarket;
		}
		/**
		 * @param additionalDwellingsOnMarket the additionalDwellingsOnMarket to set
		 */
		public void setAdditionalDwellingsOnMarket(short additionalDwellingsOnMarket) {
			this.additionalDwellingsOnMarket = additionalDwellingsOnMarket;
		}
		/**
		 * @return the newlyBuiltDwellings
		 */
		public short getNewlyBuiltDwellings() {
			return newlyBuiltDwellings;
		}
		/**
		 * @param newlyBuiltDwellings the newlyBuiltDwellings to set
		 */
		public void setNewlyBuiltDwellings(short newlyBuiltDwellings) {
			this.newlyBuiltDwellings = newlyBuiltDwellings;
		}
		/**
		 * @return the additionalDwellingsOnMarketPerYear
		 */
		public int getAdditionalDwellingsOnMarketPerYear() {
			return additionalDwellingsOnMarketPerYear;
		}
		/**
		 * @param additionalDwellingsOnMarketPerYear the additionalDwellingsOnMarketPerYear to set
		 */
		public void setAdditionalDwellingsOnMarketPerYear(
				int additionalDwellingsOnMarketPerYear) {
			this.additionalDwellingsOnMarketPerYear = additionalDwellingsOnMarketPerYear;
		}
		/**
		 * @return the newlyBuiltDwellingsPerYear
		 */
		public int getNewlyBuiltDwellingsPerYear() {
			return newlyBuiltDwellingsPerYear;
		}
		/**
		 * @param newlyBuiltDwellingsPerYear the newlyBuiltDwellingsPerYear to set
		 */
		public void setNewlyBuiltDwellingsPerYear(int newlyBuiltDwellingsPerYear) {
			this.newlyBuiltDwellingsPerYear = newlyBuiltDwellingsPerYear;
		} 
	}
	
	private ArrayList<AdditionalDwellingRow> additionalDwellings;
	
	public AdditionalDwellingsPerYear(String scenarioName) throws SQLException {
		byte hRed = Common.getHouseholdReductionFactor();
		String sql = "select modelYearStart, modelYearFinish, additionalDwellingsOnMarket / " + hRed + ", newlyBuiltDwellings / " + hRed + 
			" from _DM_AdditionalDwellings where scenarioName = '" + scenarioName + "'";
		additionalDwellings = new ArrayList<AdditionalDwellingRow>();
		ResultSet rs = Common.db.executeQuery(sql);
		while (rs.next()) {
			AdditionalDwellingRow adr = new AdditionalDwellingRow();
			adr.setModelYearStart(rs.getShort("ModelYearStart"));
			adr.setModelYearFinish(rs.getShort("ModelYearFinish"));
			adr.setAdditionalDwellingsOnMarket(rs.getShort("AdditionalDwellingsOnMarket"));
			adr.setNewlyBuiltDwellings(rs.getShort("NewlyBuiltDwellings"));
			additionalDwellings.add(adr);
		}
		rs.close();
		assert additionalDwellings.size() > 0 : "No rows selected from _DM_AdditionalDwellings (scenarioName = " + scenarioName + ")";

		// calculate the new dwellings per year
		for (AdditionalDwellingRow adr : additionalDwellings) {
			adr.setNewlyBuiltDwellingsPerYear(adr.getNewlyBuiltDwellings() / (adr.getModelYearFinish() - adr.getModelYearStart()));
			adr.setAdditionalDwellingsOnMarketPerYear(adr.getAdditionalDwellingsOnMarket() / (adr.getModelYearFinish() - adr.getModelYearStart()));
		}
	
	}

	public int getAdditionalDwellingsOnMarket(int modelYear) {
		for (AdditionalDwellingRow adr : additionalDwellings) {
			if ((adr.getModelYearStart() <= modelYear) && (modelYear <= adr.getModelYearFinish())) {
				return adr.getAdditionalDwellingsOnMarket();
			}
		}
		return 0;
	}
	
	public int getNewlyBuiltDwellings(int modelYear) {
		for (AdditionalDwellingRow adr : additionalDwellings) {
			if ((adr.getModelYearStart() <= modelYear) && (modelYear <= adr.getModelYearFinish())) {
				return adr.getNewlyBuiltDwellings();
			}
		}
		return 0;
	}
}

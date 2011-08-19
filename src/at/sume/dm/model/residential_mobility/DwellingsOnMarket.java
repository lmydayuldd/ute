/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.types.LivingSpaceGroup6;

/**
 * This class contains all dwellings that currently are available on the housing market.
 * 
 * In Vienna only a certain amount of dwellings that are free actually really enters the housing market.
 * See: Eichener, Volker: Zukunft des Wohnens. Demographische und wirtschaftliche Herausforderungen für den 
 * Wohnungsmarkt und die Stadtentwicklung, 2004, Bochum, URL: http://www.inwis.de/pdf/vortraege/Wien-Wohnen.pdf [19.10.2010]
 * 
 * The following categories of dwellings falls out of the gross surplus of dwellings over households in Vienna
 * 2001:
 * - dwellings used for other purposes than housing (like offices, etc.)
 * - non-hireable dwellings of the lowest category
 * - free dwellings that don't enter the market for other reasons (e.g. publicly funded dwellings that
 *   are kept because of their low rents in case they are needed in future, etc.) 
 *   
 * According to:
 * Statistik Austria: Wohnsituation der Bevölkerung. Ergebnisse der Volks-, Gebäude- und Wohnungszählung 2001, 2006, Wien, 
 * URL: http://www.statistik.at/web_de/dynamic/services/publikationen/7/publdetail?id=7&listid=7&detail=274 [11.03.2010], 
 * p.57   
 * the following dwelling numbers apply to Vienna:
 * total number of dwellings:  910,745
 * of these:
 * - subsidiary residences:     59,540
 * - no residence information:  80,250
 * - main places of residence: 770,955 - of these there are 5.422 dwellings in institutions resulting in 
 * a total of 765,533 private households. This number corresponds with the number of households in Vienna.
 * 
 * Dwellings on the housing market must come from those that are listed under the "no residential information"
 * category.
 * 
 * @author Alexander Remesch
 */
public class DwellingsOnMarket {
	// Dwellings on the market per spatial unit
	private ArrayList<DwellingRow> dwellingsOnMarketList[];
	private ArrayList<DwellingRow> dwellingsOnMarketFullList;
	private ArrayList<DwellingRow> dwellingsNotOnMarketFullList;
	private int grossFreeDwellingCount[];	// Total number of free dwellings available, only a fraction of this number is put on the market
	private int grossFreeDwellingTotal;
	private SpatialUnits spatialUnits;
	private ArrayList<DwellingRow> suitableDwellings;
	private boolean headLineWritten = false;
	private NoDwellingFoundReason noDwellingFoundReason = NoDwellingFoundReason.NO_REASON;

	@SuppressWarnings("unchecked")
	public DwellingsOnMarket(Dwellings dwellings, SpatialUnits spatialUnits, int dwellingsOnMarketShare) {
		this.spatialUnits = spatialUnits;
		dwellingsOnMarketList = (ArrayList<DwellingRow>[])new ArrayList[spatialUnits.size()];
		for (int i = 0; i != spatialUnits.size(); i++)
			dwellingsOnMarketList[i] = new ArrayList<DwellingRow>();
		dwellingsOnMarketFullList = new ArrayList<DwellingRow>();
		dwellingsNotOnMarketFullList = new ArrayList<DwellingRow>();
		grossFreeDwellingCount = new int[spatialUnits.size()];
		addAll(dwellings.getRowList(), dwellingsOnMarketShare);
	}
	public DwellingsOnMarket(Dwellings dwellings, SpatialUnits spatialUnits) {
		this(dwellings, spatialUnits, Common.getDwellingsOnMarketShare());
	}
	private int spatialUnitArrayPosition(long spatialUnitId) {
		SpatialUnitRow su = spatialUnits.lookup(spatialUnitId);
		return spatialUnits.indexOf(su);
	}
	public ArrayList<DwellingRow> getDwellingsOnMarket(long spatialUnitId) {
		return dwellingsOnMarketList[spatialUnitArrayPosition(spatialUnitId)];
	}
	/**
	 * Return the gross free dwelling count (that is all dwellings that are not taken by a household)
	 * per spatial unit.
	 * This number is different (larger) than the number of dwellings on market!
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public int getGrossFreeDwellingCount(long spatialUnitId) {
		return grossFreeDwellingCount[spatialUnitArrayPosition(spatialUnitId)];
	}
	/**
	 * Return the gross free dwelling count (that is all dwellings that are not taken by a household).
	 * This number is different (larger) than the number of dwellings on market!
	 * 
	 * @return The number of dwellings not taken by a household
	 */
	public int getGrossFreeDwellingTotal() {
		return grossFreeDwellingTotal;
	}
	/**
	 * Add a share of the dwellings given to the list of dwellings on the market.
	 * The dwellings that will be added are selected by random.
	 * 
	 * @param dwellings List of dwellings, out of which elements are randomly selected
	 * @param dwellingsOnMarketShare Share of dwellings that will be selected
	 */
	public void addAll(List<DwellingRow> dwellings, int dwellingsOnMarketShare) {
		Random r = new Random();
		for (DwellingRow row : dwellings) {
			if (row.getHousehold() == null) {
				int pos = spatialUnits.indexOf(row.getSpatialunit());
				grossFreeDwellingCount[pos]++;
				grossFreeDwellingTotal++;
				if (r.nextInt(100) <= dwellingsOnMarketShare) {
					putDwellingOnMarket(row);
				} else {
					dwellingsNotOnMarketFullList.add(row);
				}
			}
		}
	}
	/**
	 * Additionally put a given number of dwellings from the initial free dwelling pool on the dwelling
	 * market.
	 * 
	 * @param additionalDwellingsCount Number of dwellings to put on the market
	 * @return Number of dwellings that were actually put on the market
	 */
	public int increase(int additionalDwellingsCount) {
		int result = 0;
		if (dwellingsNotOnMarketFullList.size() == 0)
			return result;
		Random r = new Random();
		double share = (double) additionalDwellingsCount / dwellingsNotOnMarketFullList.size();
		for (DwellingRow row : dwellingsNotOnMarketFullList) {
			int pos = spatialUnits.indexOf(row.getSpatialunit());
			grossFreeDwellingCount[pos]++;
			grossFreeDwellingTotal++;
			if (r.nextDouble() <= share) {
				result++;
				putDwellingOnMarket(row);
			}
		}
		return result;
	}
	/**
	 * Add all dwellings given to the list of dwellings on the market
	 * 
	 * @param dwellings List of dwellings that will be added
	 */
	public void addAll(List<DwellingRow> dwellings) {
		addAll(dwellings, 100);
	}
	/**
	 * Select a list of available dwellings in a certain spatial unit within a defined size range and
	 * below a given yearly price per m²
	 * 
	 * @param spatialUnitId the spatial unit that the dwellings should be in
	 * @param minSize the minimum size of the dwellings
	 * @param maxSize the maximum size of the dwellings
	 * @param maxYearlyPricePerSqm the maximum yearly price per m² for the dwellings
	 * @return 
	 */
	public int selectSuitableDwellingsOnMarket(long spatialUnitId, int minSize, int maxSize, long maxYearlyPricePerSqm) {
		ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
		suitableDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow dwelling : allDwellingsPerArea) {
			if ((minSize <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= maxSize) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= maxYearlyPricePerSqm) || (maxYearlyPricePerSqm < 0)) {
				suitableDwellings.add(dwelling);
			}
		}
		return suitableDwellings.size();
	}
	/**
	 * Select a list of available dwellings from a list of spatial units within a defined size range and
	 * below a given yearly price per m²
	 * 
	 * @param spatialUnitIdList the spatial units that the dwelling should be in
	 * @param minSize the minimum size of the dwellings
	 * @param maxSize the maximum size of the dwellings
	 * @param maxYearlyPricePerSqm the maximum yearly price per m² for the dwellings
	 * @return Number of suitable dwellings found
	 */
	public int selectSuitableDwellingsOnMarket(ArrayList<Integer> spatialUnitIdList, short minSize, short maxSize, long maxYearlyPricePerSqm) {
		suitableDwellings = new ArrayList<DwellingRow>();
		for (long spatialUnitId : spatialUnitIdList) {
			selectSuitableDwellingsOnMarket(spatialUnitId, minSize, maxSize, maxYearlyPricePerSqm);
//			ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
//			for (DwellingRow dwelling : allDwellingsPerArea) {
//				if ((minSize <= dwelling.getDwellingSize()) && 
//						(dwelling.getDwellingSize() <= maxSize) &&
//						((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= maxYearlyPricePerSqm) || (maxYearlyPricePerSqm < 0)) {
//					suitableDwellings.add(dwelling);
//				}
//			}
		}
		return suitableDwellings.size();
	}
	/**
	 * Select a list of available dwellings from a list of spatial units within a defined size range and
	 * without any recognition of the price of the dwelling
	 * 
	 * @param spatialUnitIdList the spatial units that the dwelling should be in
	 * @param minSize the minimum size of the dwellings
	 * @param maxSize the maximum size of the dwellings
	 * @return Number of suitable dwellings found
	 */
	public int selectSuitableDwellingsOnMarket(ArrayList<Integer> spatialUnitIdList, short minSize, short maxSize) {
		return selectSuitableDwellingsOnMarket(spatialUnitIdList, minSize, maxSize, -1);
	}
	/**
	 * Randomly pick one dwelling of the list of suitable dwellings for a household created with
	 * selectSuitableDwellingsOnMarket()
	 * 
	 * @return
	 */
	public DwellingRow pickRandomSuitableDwelling() {
		Random r = new Random();
		assert suitableDwellings.size() > 0 : "no suitable dwellings";
		return suitableDwellings.get(r.nextInt(suitableDwellings.size()));
	}
	/**
	 * Get the first available (free) dwelling that matches the given parameters
	 * @param spatialUnitId
	 * @param livingSpaceGroup6Id
	 * @return
	 */
	public DwellingRow getFirstMatchingDwelling(long spatialUnitId, byte livingSpaceGroup6Id) {
		int index = spatialUnitArrayPosition(spatialUnitId);
		assert index != -1 : "Spatial unit id " + spatialUnitId + " not found - maybe the model is running in the wrong spatial resolution (SGT instead of ZB)?";
		ArrayList<DwellingRow> dwellings =  dwellingsOnMarketList[index];
		for (DwellingRow dwelling : dwellings) {
			if (dwelling.getLivingSpaceGroup6Id() == livingSpaceGroup6Id)
				return dwelling;
		}
		return null;
	}
	/**
	 * Get the first available (free) dwelling that matches the given parameters
	 * @param spatialUnitId
	 * @param minSize
	 * @param maxSize
	 * @return
	 */
	public DwellingRow getFirstMatchingDwelling(long spatialUnitId, short minSize, short maxSize, long maxYearlyPricePerSqm) {
		ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
		for (DwellingRow dwelling : allDwellingsPerArea) {
			if ((minSize <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= maxSize) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= maxYearlyPricePerSqm) || (maxYearlyPricePerSqm < 0)) {
				return dwelling;
			}
		}
		return null;
	}
	/**
	 * Get the first available (free) dwelling that matches the given parameters
	 * @param spatialUnitId
	 * @param minSize
	 * @param maxSize
	 * @return
	 */
	public DwellingRow getFirstMatchingDwelling(long spatialUnitId, short minSize, short maxSize) {
		return getFirstMatchingDwelling(spatialUnitId, minSize, maxSize, -1);
	}
	/**
	 * Get the first available dwelling anywhere that matches the given size & rent criteria
	 * @param minSize
	 * @param maxSize
	 * @param maxYearlyPricePerSqm
	 * @return
	 */
	public DwellingRow getFirstMatchingDwelling(short minSize, short maxSize, long maxYearlyPricePerSqm) {
		int startPos = (int)(Math.random() * dwellingsOnMarketFullList.size());
		int endPos = dwellingsOnMarketFullList.size();
		for (int i = startPos; i != endPos; i++) {
			DwellingRow dwelling = dwellingsOnMarketFullList.get(i);
			if ((minSize <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= maxSize) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= maxYearlyPricePerSqm) || (maxYearlyPricePerSqm < 0)) {
				return dwelling;
			}
		}
		for (int i = 0; i != startPos; i++) {
			DwellingRow dwelling = dwellingsOnMarketFullList.get(i);
			if ((minSize <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= maxSize) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= maxYearlyPricePerSqm) || (maxYearlyPricePerSqm < 0)) {
				return dwelling;
			}
		}
		return null;
	}
	
	private long spatialUnitId;
	private HouseholdRow household;
	private boolean compareDwellingCosts;
	private int modelYear;
	private int currentDwellingIndex;
	/**
	 * Get the first available (free) dwelling that matches the given parameters
	 * 
	 * @param spatialUnitId Spatial unit to search dwellings in
	 * @param household Properties of the household looking for the dwelling
	 * @param compareDwellingCosts True if the dwelling costs shall be considered in the comparison
	 * @param modelYear
	 * @return The first available (free) dwelling that matches the given parameters
	 */
	public DwellingRow getFirstMatchingDwelling(long spatialUnitId, HouseholdRow household, boolean compareDwellingCosts, int modelYear) {
		ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
		
		// save parameters for later use by getNextMatchingDwelling()
		this.spatialUnitId = spatialUnitId;
		this.household = household;
		this.compareDwellingCosts = compareDwellingCosts;
		this.modelYear = modelYear;
		
//		for (DwellingRow dwelling : allDwellingsPerArea) {
		for (currentDwellingIndex = 0; currentDwellingIndex != allDwellingsPerArea.size(); currentDwellingIndex++) {
			DwellingRow dwelling = allDwellingsPerArea.get(currentDwellingIndex);
			if ((household.getAspirationRegionLivingSpaceMin() <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= household.getAspirationRegionLivingSpaceMax()) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= household.getAspirationRegionMaxCosts()) || (!compareDwellingCosts)) {
				if (household.getCurrentResidentialSatisfaction() < household.calcResidentialSatisfaction(dwelling, modelYear)) {
					return dwelling;
				} else {
					noDwellingFoundReason = NoDwellingFoundReason.NO_SATISFACTION;
				}
			}
		}
		if (noDwellingFoundReason == NoDwellingFoundReason.NO_REASON)
			noDwellingFoundReason = NoDwellingFoundReason.NO_SUITABLE_DWELLING;
		return null;
	}
	/**
	 * Get the next available (free) dwelling that matches the parameters passed to a previous call to
	 * getFirstMatchingDwelling()
	 * 
	 * @return The next available (free) dwelling that matches the given parameters
	 */
	public DwellingRow getNextMatchingDwelling() {
		ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
		if (currentDwellingIndex >= allDwellingsPerArea.size())
			return null;
//		assert currentDwellingIndex < allDwellingsPerArea.size() : "currentDwellingIndex (" + currentDwellingIndex + ") out of range (max = " + allDwellingsPerArea.size() + ")";
		for (;currentDwellingIndex != allDwellingsPerArea.size(); currentDwellingIndex++) {
			DwellingRow dwelling = allDwellingsPerArea.get(currentDwellingIndex);
			if ((household.getAspirationRegionLivingSpaceMin() <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= household.getAspirationRegionLivingSpaceMax()) && 
					((dwelling.getTotalYearlyDwellingCosts() / dwelling.getDwellingSize()) <= household.getAspirationRegionMaxCosts()) || (!compareDwellingCosts)) {
				if (household.getCurrentResidentialSatisfaction() < household.calcResidentialSatisfaction(dwelling, modelYear)) {
					return dwelling;
				} else {
					noDwellingFoundReason = NoDwellingFoundReason.NO_SATISFACTION;
				}
			}
		}
		if (noDwellingFoundReason == NoDwellingFoundReason.NO_REASON)
			noDwellingFoundReason = NoDwellingFoundReason.NO_SUITABLE_DWELLING;
		return null;
	}
	/**
	 * @return The reason why no dwelling was found by getFirstMatchingDwelling()
	 */
	public NoDwellingFoundReason getNoDwellingFoundReason() {
		return noDwellingFoundReason;
	}
	public void putDwellingOnMarket(DwellingRow dwelling) {
		int su = spatialUnits.indexOf(dwelling.getSpatialunit());
		dwellingsOnMarketList[su].add(dwelling);
		dwelling.setHousehold(null);
		dwelling.calcTotalYearlyDwellingCosts(true);
		dwellingsOnMarketFullList.add(dwelling);
	}
	public void removeDwellingFromMarket(DwellingRow dwelling) {
		int su = spatialUnits.indexOf(dwelling.getSpatialunit());
		dwellingsOnMarketList[su].remove(dwelling);
		dwellingsOnMarketFullList.remove(dwelling);
	}
	public void outputDwellingsPerSize(PrintStream ps, int modelYear, String label) {
		StringBuffer output = new StringBuffer();
		// Headline - written only once per model run
		if (!headLineWritten) {
			output.append("ModelYear;SpatialUnit;Label;Total");
			for (byte i = 0; i != LivingSpaceGroup6.getLivingSpaceGroupCount(); i++) {
				output.append(";" + LivingSpaceGroup6.getLivingSpaceGroupName((byte) (i + 1)));
			}
			ps.println(output);
			headLineWritten = true;
		}
		for (int i = 0; i != spatialUnits.size(); i++) {
			if (!spatialUnits.get(i).isFreeDwellingsAlwaysAvailable()) { // don't output for spatial units outside the model area
				int dwellingSizeCount[] = new int[LivingSpaceGroup6.getLivingSpaceGroupCount()];
				output = new StringBuffer(modelYear + ";" + spatialUnits.get(i).getSpatialUnitId() + ";" + label + ";" + dwellingsOnMarketList[i].size());
				// Count dwellings per living space group
				for (DwellingRow dwelling : dwellingsOnMarketList[i]) {
					dwellingSizeCount[LivingSpaceGroup6.getLivingSpaceGroupId(dwelling.getDwellingSize()) - 1]++;
				}
				// Output dwelling count per living space group
				for (byte j = 0; j != LivingSpaceGroup6.getLivingSpaceGroupCount(); j++) {
					output.append(";" + dwellingSizeCount[j]);
				}
				ps.println(output);
			}
		}
	}
	/**
	 * @return The total number of free dwellings that are available on the dwelling market
	 */
	public int getFreeDwellingsCount() {
		return dwellingsOnMarketFullList.size();
	}
}

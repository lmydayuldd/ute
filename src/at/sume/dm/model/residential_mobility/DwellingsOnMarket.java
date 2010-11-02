/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.util.ArrayList;
import java.util.Random;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.SpatialUnits;

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
	private int grossFreeDwellingCount[];
	private SpatialUnits spatialUnits;
	private ArrayList<DwellingRow> suitableDwellings;

	@SuppressWarnings("unchecked")
	public DwellingsOnMarket(Dwellings dwellings, SpatialUnits spatialUnits) {
		Random r = new Random();
		this.spatialUnits = spatialUnits;
		dwellingsOnMarketList = (ArrayList<DwellingRow>[])new ArrayList[spatialUnits.size()];
		for (int i = 0; i != spatialUnits.size(); i++)
			dwellingsOnMarketList[i] = new ArrayList<DwellingRow>();
		grossFreeDwellingCount = new int[spatialUnits.size()];
		for (DwellingRow row : dwellings) {
			if (row.getHousehold() == null) {
				int pos = spatialUnits.indexOf(row.getSpatialunit());
				grossFreeDwellingCount[pos]++;
				if (r.nextInt(100) <= Common.getDwellingsOnMarketShare()) {
					dwellingsOnMarketList[pos].add(row);
				}
			}
		}
	}
	private int spatialUnitArrayPosition(long spatialUnitId) {
		return spatialUnits.indexOf(spatialUnits.lookup(spatialUnitId));
	}
	public ArrayList<DwellingRow> getDwellingsOnMarket(long spatialUnitId) {
		return dwellingsOnMarketList[spatialUnitArrayPosition(spatialUnitId)];
	}
	public int getGrossFreeDwellingCount(long spatialUnitId) {
		return grossFreeDwellingCount[spatialUnitArrayPosition(spatialUnitId)];
	}
	/**
	 * Select a list of available dwellings in a certain spatial unit within a defined size range and
	 * below a given yearly price per m²
	 * 
	 * @param spatialUnitId the spatial unit that the dwellings should be in
	 * @param minSize the minimum size of the dwellings
	 * @param maxSize the maximum size of the dwellings
	 * @param maxYearlyPricePerSqm the maximum yearly price per m² for the dwellings
	 */
	public void selectSuitableDwellingsOnMarket(long spatialUnitId, int minSize, int maxSize, long maxYearlyPricePerSqm) {
		ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
		suitableDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow dwelling : allDwellingsPerArea) {
			if ((minSize <= dwelling.getDwellingSize()) && 
					(dwelling.getDwellingSize() <= maxSize) && 
					(dwelling.getDwellingCosts() / dwelling.getDwellingSize() <= maxYearlyPricePerSqm)) {
				suitableDwellings.add(dwelling);
			}
		}
	}
	/**
	 * Select a list of available dwellings fronm a list of spatial units within a defined size range and
	 * below a given yearly price per m²
	 * 
	 * @param spatialUnitIdList the spatial units that the dwelling should be in
	 * @param minSize the minimum size of the dwellings
	 * @param maxSize the maximum size of the dwellings
	 * @param maxYearlyPricePerSqm the maximum yearly price per m² for the dwellings
	 */
	public void selectSuitableDwellingsOnMarket(ArrayList<Long> spatialUnitIdList, int minSize, int maxSize, long maxYearlyPricePerSqm) {
		suitableDwellings = new ArrayList<DwellingRow>();
		for (long spatialUnitId : spatialUnitIdList) {
			ArrayList<DwellingRow> allDwellingsPerArea = getDwellingsOnMarket(spatialUnitId);
			for (DwellingRow dwelling : allDwellingsPerArea) {
				if ((minSize <= dwelling.getDwellingSize()) && 
						(dwelling.getDwellingSize() <= maxSize) && 
						(dwelling.getDwellingCosts() / dwelling.getDwellingSize() <= maxYearlyPricePerSqm)) {
					suitableDwellings.add(dwelling);
				}
			}
		}
	}
	/**
	 * Randomly pick one dwelling of the list of suitable dwellings for a household created with
	 * selectSuitableDwellingsOnMarket()
	 * 
	 * @return
	 */
	public DwellingRow pickRandomSuitableDwelling() {
		Random r = new Random();
		return suitableDwellings.get(r.nextInt(suitableDwellings.size()));
	}
}

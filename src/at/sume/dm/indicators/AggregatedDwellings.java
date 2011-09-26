/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.CostOfResidenceGroup;
import at.sume.dm.types.LivingSpaceGroup6;

/**
 * @author Alexander Remesch
 *
 */
public class AggregatedDwellings {
	private ArrayList<AggregatedDwellingRow> indicatorList;
	
	public AggregatedDwellings() {
		indicatorList = new ArrayList<AggregatedDwellingRow>();
	}

	private int lookupIndicator(int spatialUnitId, short costOfResidenceGroupId, byte livingSpaceGroupId, boolean vacant) {
		AggregatedDwellingRow lookup = new AggregatedDwellingRow();
		lookup.setSpatialUnitId(spatialUnitId);
		lookup.setCostOfResidenceGroupId(costOfResidenceGroupId);
		lookup.setLivingSpaceGroupId(livingSpaceGroupId);
		lookup.setVacant(vacant);
		return Collections.binarySearch(indicatorList, lookup);
	}

	public void build(ArrayList<DwellingRow> dwellings) {
		clear();
		for (DwellingRow dwelling : dwellings) {
			// TODO: stop using double for currencies!
			// round to two decimal places here!
			double monthlyRentPerSqm = Math.round((((double) dwelling.getTotalYearlyDwellingCosts()) / dwelling.getDwellingSize() / 12) * 100) / 100;
			short costOfResidenceGroupId = CostOfResidenceGroup.getCostOfResidenceGroupId(monthlyRentPerSqm);
			byte livingSpaceGroup6 = LivingSpaceGroup6.getLivingSpaceGroupId(dwelling.getDwellingSize());
			boolean vacant = (dwelling.getHousehold() == null);
			assert monthlyRentPerSqm > 0 : "Monthly rent per m² <= 0";
			int pos = lookupIndicator(dwelling.getSpatialunitId(), costOfResidenceGroupId, livingSpaceGroup6, vacant);  
			if (pos < 0) {
				// insert at position pos
				pos = (pos + 1) * -1;
				AggregatedDwellingRow b = new AggregatedDwellingRow();
				b.setSpatialUnitId(dwelling.getSpatialunitId());
				b.setCostOfResidenceGroupId(costOfResidenceGroupId);
				b.setLivingSpaceGroupId(livingSpaceGroup6);
				b.setVacant(vacant);
				b.setDwellingCount(1);
				indicatorList.add(pos, b);
			} else {
				// available at position pos
				AggregatedDwellingRow b = indicatorList.get(pos);
				b.setDwellingCount(b.getDwellingCount() + 1);
				indicatorList.set(pos, b);
			}
		}
	}

	public void clear() {
		indicatorList.clear();
	}

	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}

/**
 * 
 */
package net.remesch.obsolete;

import java.io.FileNotFoundException;
import java.io.IOException;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.base.IndicatorBase;

/**
 * @author Alexander Remesch
 *
 */
public enum MigrationIndicatorManager {
	IMMIGRATING_HOUSEHOLDS("Immigrating households", new ImmigratingHouseholds(), "immigrating_households.txt");
	
	private String label;
	private IndicatorBase<?> indicatorBase;

	MigrationIndicatorManager(String label, IndicatorBase<?> indicatorBase, String outputFileName) {
		this.label = label;
		this.indicatorBase = indicatorBase;
		indicatorBase.setOutputFileName(outputFileName);
		try {
			indicatorBase.initOutputFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (MigrationIndicatorManager indicatorManager : values()) {
			indicatorManager.indicatorBase.add(household);
		}
	}
	
	public static void resetIndicators() {
		for (MigrationIndicatorManager indicatorManager : values()) {
			indicatorManager.indicatorBase.clear();
		}
	}
	
	public static void outputIndicators(int modelYear) throws FileNotFoundException, IOException {
		for (MigrationIndicatorManager indicatorManager : values()) {
			if (indicatorManager.indicatorBase != null)
				indicatorManager.indicatorBase.outputIndicatorData(modelYear);
		}
	}
}

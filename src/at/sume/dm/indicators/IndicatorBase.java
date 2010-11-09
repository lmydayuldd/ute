/**
 * 
 */
package at.sume.dm.indicators;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import net.remesch.util.StringUtil;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.rows.IndicatorRow;

/**
 * This class is thought to replace class Indicator
 * 
 * @author Alexander Remesch
 */
public abstract class IndicatorBase<E extends IndicatorRow> {
	protected String outputFileName;
	protected PrintStream output;
	protected ArrayList<E> indicatorList;
	
	public IndicatorBase() {
		indicatorList = new ArrayList<E>();
	}
	/**
	 * Add data of a household to the set of indicators
	 * @param hh
	 */
	public void add(HouseholdRow household) {
		int pos = lookup(household);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			insert(pos, household);
		} else {
			// available at position pos
			update(pos, household, false);
		}
	}
	public int lookup(HouseholdRow household) {
		throw new AssertionError("IndicatorBase.lookup() not implemented");
	}
	public void insert(int pos, HouseholdRow household) {
		throw new AssertionError("IndicatorBase.insert() not implemented");
	}
	public void update(int pos, HouseholdRow household, boolean remove) {
		throw new AssertionError("IndicatorBase.update() not implemented");
	}
	/**
	 * Remove data of a household from the set of indicators
	 * @param hh
	 */
	public void remove(HouseholdRow household) {
		int pos = lookup(household);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("IndicatorBase.remove() - no matching indicator found");
		} else {
			// available at position pos
			update(pos, household, true);
		}
	}
	/**
	 * Reset the set of indicators to zero
	 */
	public void clear() {
		indicatorList.clear();
	}
	
	public long get(HouseholdRow household) {
		throw new AssertionError();
	}
	
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	public void initOutputFile() throws FileNotFoundException, IOException {
		FileOutputStream outFile = new FileOutputStream(outputFileName, false);
		output = new PrintStream(outFile);
		outputHeadline();
	}
	/**
	 * 
	 */
	public void outputHeadline() {
		String result = "";
		E e = null;
		// TODO: causes exception because of null pointer - ist this fixable?
		Class<? extends IndicatorRow> c = e.getClass();
		for (Field field : c.getDeclaredFields()) {
			result = StringUtil.concat("\t", result, field.getName());
		}
		output.println(result);
	}
	public void outputIndicatorData(int modelYear) throws FileNotFoundException, IOException {
		E.setDelimiter("\t");
		for (E indicator : indicatorList) {
			output.println(modelYear + "\t" + indicator.display());
		}
		output.flush();
	}
}

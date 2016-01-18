/**
 * 
 */
package at.sume.sampling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;
import net.remesch.db.Database;
import net.remesch.util.Random;

/**
 * General handling of distributions for random ("Monte Carlo") sampling
 * 
 * @author Alexander Remesch
 *
 * @param <E> Data that may be stored with each sample record 
 * 
 * TODO: do we need this to be an implementation of Collection<T> - if not, we should extract Collection<T> out of
 * RecordSet<E> and make a RecordSetCollectable<E> instead;
 * furthermore it seems necessary to clarify this interface - e.g. fieldnames() and primaryKeyFieldnames() should be
 * replaced by methods like dependentVariables and independentVariables, 
 * Method tablename() seems to be completely obsolete and doesn't make sense in this classe,
 */
public abstract class SamplingDistribution<E extends RecordSetRow<?>> extends RecordSet<E> {
	private ArrayList<Long> thresholdList;
	private long maxThreshold;
	protected Database db;
	
	/**
	 * Construct class and load distribution count from the database. Variable parts have to be implemented in implementation
	 * classes ("Factories")
	 * @param db Database to load rows from
	 * @throws SQLException
	 */
	public SamplingDistribution(Database db) {
		super();
		this.db = db;
	}
	
	public void loadDistribution() throws SQLException {
		int rowcount = 0; // TODO: get correct row count
		PreparedStatement ps = db.con.prepareStatement(selectStatement());
		ResultSet rs = getResultSet(ps);
		long thresholdCount = 0;
		rowList = new ArrayList<E>(rowcount);
		thresholdList = new ArrayList<Long>(rowcount);
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(fieldnames()));
		while (rs.next())
		{
			E item = createRecordSetRow();
			for (String key : keys) {
				item.loadFromDatabase(rs, key);
			}
			String fieldname = valueField();
			thresholdCount += rs.getLong(fieldname);
			thresholdList.add(thresholdCount);
			rowList.add(item);
		}
//		if (rowList.size() == 0)
//			throw new IllegalArgumentException("No data found");
		rs.close();
		ps.close();
		maxThreshold = thresholdCount;
	}
	
	public abstract ResultSet getResultSet(PreparedStatement ps) throws SQLException;
	
	/**
	 * Default factory for the field name of the distribution count field retrieved by the SQL select statement
	 * @return Field name "PersonCount"
	 */
	public String valueField() {
		return "PersonCount";
	}

	/**
	 * Random selection ("sampling") of a record out of the sample
	 * @return index of the record selected
	 * @throws ArrayIndexOutOfBoundsException internal error - returned index larger than number of records in sample
	 */
	public int randomSample() throws ArrayIndexOutOfBoundsException
	{
		Random r = new Random();
		// generate random number for sampling
		long rand = (long) (r.nextDouble() * maxThreshold);
		// lookup index of element where random number falls within the boundaries
		int index = Collections.binarySearch(thresholdList, rand);
		if (index < 0)
			index = (index + 1) * -1;
		if (index > thresholdList.size())
			throw(new ArrayIndexOutOfBoundsException("rand = " + rand + ", max threshold = " + maxThreshold + ", index = " + index + ", max index = " + thresholdList.size()));
		return index;
	}
}

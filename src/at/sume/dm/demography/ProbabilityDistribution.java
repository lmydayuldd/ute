/**
 * 
 */
package at.sume.dm.demography;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

import net.remesch.util.Database;


/**
 * Base class for implementing probability distributions to store and retrieve the probability of an event for
 * a certain entity (e.g. person, household, etc.)
 * 
 * @author Alexander Remesch
 */
public abstract class ProbabilityDistribution<E extends RecordSetRow<?>> extends RecordSet<E> {
	private ArrayList<E> itemList;
	private ArrayList<Double> probabilityList;
	
	/**
	 * Construct class and load probabilities from the database. Variable parts have to be implemented in implementation
	 * classes ("Factories")
	 * @param db Database to load rows from
	 * @throws SQLException
	 */
	public ProbabilityDistribution(Database db) throws SQLException {
		int rowcount = 0; // TODO: get correct row count

		ResultSet rs = db.executeQuery(selectStatement());
		
		itemList = new ArrayList<E>(rowcount);
		probabilityList = new ArrayList<Double>(rowcount);
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(keyFields()));
				
		while (rs.next())
		{
			E item = createProbabilityItem();
			for (String key : keys) {
				item.loadFromDatabase(rs, key);
			}
			String fieldname = valueField();
			Double p = rs.getDouble(fieldname);
			probabilityList.add(p);
			itemList.add(item);
		}
		rs.close();
	}
	
	/**
	 * Determine the probability of an event for a given entity
	 * 
	 * @param lookupItem Entity to determine the probability for a certain event for an entity with certain properties
	 * @return Probability of the occurrence of the event
	 */
	public double probability(E lookupItem) {
		int i = itemList.indexOf(lookupItem);
		if (i == -1) {
			return 0;	// no matching ProbabilityItem found
		} else {
			return probabilityList.get(i);
		}
	}

	/**
	 * Factory for the SQL select statement to retrieve the event-probabilities depending on various properties 
	 * @return SQL select string
	 */
	public abstract String selectStatement();
	/**
	 * Factory for the field names of the key fields (= properties that the event-probabilities depend on)
	 * @return Array of field names retrieved by the SQL select statement
	 */
	public String[] keyFields() {
		return primaryKeyFieldnames();
	}
	/**
	 * Factory for the field name of the probability value field retrieved by the SQL select statement
	 * @return Field name retrieved by the SQL select statement
	 */
	public String valueField() {
		return "p";
	}
	/**
	 * Factory to create the implementation of ProbabilityItem, i.e. an object containing a description of the properties
	 * the probability of an event will depend on
	 * @return
	 */
	public abstract E createProbabilityItem();
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord(at.sume.db.RecordSet)
	 */
	@Override
	public E createDatabaseRecord() {
		return createProbabilityItem();
	}
}

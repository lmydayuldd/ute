/**
 * 
 */
package at.sume.sampling;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import net.remesch.db.Database;
import net.remesch.util.DateUtil;
import at.sume.dm.Common;


/**
 * Match households with dwellings depending on spatial unit and living space of the household and the dwelling
 * 
 * @author Alexander Remesch
 */
public class AssociateHouseholdsDwellings {
	public static class HouseholdRow {
		public long householdId;
		public long dwellingId;
		public long spatialunitId;
		public int livingSpace;
	}
	public static class DwellingRow implements Comparable<DwellingRow> {
		public long dwellingId;
		public long spatialunitId;
		public int minSpace;
		public int maxSpace;
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DwellingRow arg0) {
			return ((Long)spatialunitId).compareTo(arg0.spatialunitId);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		DwellingRow lookupDwelling = new DwellingRow();
		Database db = Common.openDatabase();
		Common.init();
		try {
			PreparedStatement updateStatement = db.con.prepareStatement("update _DM_Households set dwellingId = ? where householdId = ?");

			String sqlStatement = "SELECT [_DM_Dwellings].DwellingId, [_DM_Dwellings].SpatialunitId, [_DM_LivingSpaceGroup6].MinSpace, [_DM_LivingSpaceGroup6].MaxSpace " +
				"FROM (_DM_Dwellings INNER JOIN _DM_LivingSpaceGroup6 ON [_DM_Dwellings].LivingSpaceGroup6Id = [_DM_LivingSpaceGroup6].LivingSpaceGroup6Id) LEFT JOIN _DM_Households ON [_DM_Dwellings].DwellingId = [_DM_Households].DwellingId " +
				"WHERE ((([_DM_Households].HouseholdId) Is Null)) " +
				"ORDER BY [_DM_Dwellings].SpatialunitId;";
			ArrayList<DwellingRow> dwellings = db.select(DwellingRow.class, sqlStatement);
			
			sqlStatement = "select * from _DM_Households where dwellingId = 0";
            Statement stmt = db.con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            for (int i = 0; rs.next(); i++) {
            	lookupDwelling.spatialunitId = rs.getLong("spatialunitId");
				int livingSpace = rs.getInt("LivingSpace");
				long householdId = rs.getLong("householdId");
            	int pos = Collections.binarySearch(dwellings, lookupDwelling);
            	if (pos < 0) {
            		// not there
//            		System.out.println(DateUtil.now() + ": can't find a dwelling for hh " + householdId + ", su " + lookupDwelling.spatialunitId + ", livingspace " + livingSpace);
            	} else {
            		// find first record of that spatial unit
            		DwellingRow h;
            		int max = dwellings.size();
            		int j;
            		boolean found = false;
            		for (j = pos; j != 0; j--) {
            			h = dwellings.get(j);
            			if (h.spatialunitId != lookupDwelling.spatialunitId) {
            				// no dwelling found!
                    		break;
            			} else {
	        				if ((h.minSpace <= livingSpace) && (h.maxSpace >= livingSpace)) {
	        					updateStatement.setString(1, Long.toString(h.dwellingId));
	        					updateStatement.setString(2, Long.toString(householdId));
	        					updateStatement.execute();
	        					dwellings.remove(j);
	        					found = true;
	        					break;
	        				}
            			}
            		}
            		if (!found) {
	            		pos = j + 1;
	            		// found - look for first dwelling with matching size
	            		for (j = pos; j < max; j++) {
	            			h = dwellings.get(j);
	            			if (h.spatialunitId != lookupDwelling.spatialunitId) {
	            				// no dwelling found!
//	                    		System.out.println(DateUtil.now() + ": can't find a dwelling for hh " + householdId + ", su " + lookupDwelling.spatialunitId + ", livingspace " + livingSpace);
	                    		break;
	            			} else {
	            				if ((h.minSpace <= livingSpace) && (h.maxSpace >= livingSpace)) {
	            					updateStatement.setString(1, Long.toString(h.dwellingId));
	            					updateStatement.setString(2, Long.toString(householdId));
	            					updateStatement.execute();
	            					dwellings.remove(j);
	            					break;
	            				}
	            			}
	            		}
            		}
            	}
                if (i % 1000 == 0) {
                	System.out.println(DateUtil.now() + ": " + i + " households processed, " + dwellings.size() + " dwellings remain free");
                }
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}

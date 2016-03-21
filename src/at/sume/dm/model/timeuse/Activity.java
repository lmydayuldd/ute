/**
 * 
 */
package at.sume.dm.model.timeuse;

import java.util.Arrays;
import java.util.List;

/**
 * All activities in the model
 *
 * Inspired by http://stackoverflow.com/questions/4197988/java-enum-valueof-with-multiple-values/12659023#12659023
 * 
 * @author Alexander Remesch
 */
public enum Activity {
	NONE(""),
	TRAVEL_WORK("twork"),
	TRAVEL_CARING("tcaring"),
	TRAVEL_CULTURE("tculture"),
	TRAVEL_DIY("tdiy"),
	TRAVEL_GENERAL("tgeneral"),
	TRAVEL_HOBBY("thobby"),
	TRAVEL_MEDIA("tmedia"),
	TRAVEL_PERSONAL("tpers"),
	TRAVEL_SOCIAL("tsocial"),
	TRAVEL_SHOPPING("tshop"),
	TRAVEL_SPORTS("tsports"),
	TRAVEL_STUDYING("tstudy"),
	TRAVEL_VOLUNTARY("tvolun");

    private final List<String> values;

    Activity(String ...values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }

    public static Activity find(String name) {
        for (Activity lang : Activity.values()) {
            if (lang.getValues().contains(name)) {
                return lang;
            }
        }
        return null;
    }    
    
//	public static Activity getEnum(String s) {
//		switch (s) {
//		case "twork":
//			return TRAVEL_WORK;
//		case "tcaring":
//			return TRAVEL_CARING; 
//		case "tculture":
//			return TRAVEL_CULTURE; 
//		case "tdiy":
//			return TRAVEL_DIY; 
//		case "tgeneral":
//			return TRAVEL_GENERAL; 
//		case "thobby":
//			return TRAVEL_HOBBY; 
//		case "tmedia":
//			return TRAVEL_MEDIA; 
//		case "tpers":
//			return TRAVEL_PERSONAL;
//		case "tsocial":
//			return TRAVEL_SOCIAL;
//		case "tshop":
//			return TRAVEL_SHOPPING;
//		case "tsports":
//			return TRAVEL_SPORTS;
//		case "tstudy":
//			return TRAVEL_STUDYING;
//		case "tvolun":
//			return TRAVEL_VOLUNTARY;
//		default:
//			throw new AssertionError("Unknown Activity");
//		}
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return values.get(0);
//		switch (this) {
//		case TRAVEL_WORK:
//			return "twork";
//		case TRAVEL_CARING: 
//			return "tcaring";
//		case TRAVEL_CULTURE: 
//			return "tculture";
//		case TRAVEL_DIY: 
//			return "tdiy";
//		case TRAVEL_GENERAL: 
//			return "tgeneral";
//		case TRAVEL_HOBBY: 
//			return "thobby";
//		case TRAVEL_MEDIA: 
//			return "tmedia";
//		case TRAVEL_PERSONAL:
//			return "tpers";
//		case TRAVEL_SOCIAL:
//			return "tsocial";
//		case TRAVEL_SHOPPING:
//			return "tshop";
//		case TRAVEL_SPORTS:
//			return "tsports";
//		case TRAVEL_STUDYING:
//			return "tstudy";
//		case TRAVEL_VOLUNTARY:
//			return "tvolun";
//		default:
//			throw new AssertionError("Unknown Activity");
//		}
	}
}

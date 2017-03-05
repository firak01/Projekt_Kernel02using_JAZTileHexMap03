package tryout.hibernate;

import javax.persistence.Embeddable;

/* alte VErsion, hier funktioniert nicht das deserialisieren 
 public enum AreaType {
 	OCEAN, LAND
}
*/

//20170201: Mache diesen AreaType embeddable. Ziel: Es soll nicht mehr als BLOB in der Datenbank (SQLITE) gespeichert werden.
@Embeddable
public enum AreaType { 
OCEAN("Ozean", "OC"),
LAND("Land", "LA");

private String name, abbr;

AreaType(String fullName, String abbr) {
    this.name = fullName;
    this.abbr = abbr;
}

@Override
public String toString() {
    return this.name;
}

// the identifierMethod ---> Going in DB
public String getAbbreviation() {
    return this.abbr;
}

// the valueOfMethod <--- Translating from DB
public static AreaType fromAbbreviation(String s) {
    for (AreaType state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}

}
package use.thm.persistence.model;

import javax.persistence.Embeddable;

/* alte Version, hier funktioniert nicht das deserialisieren 
 public enum AreaType {
 	OCEAN, LAND
}
*/

//20170201: Mache diesen AreaType embeddable. Ziel: Es soll nicht mehr als BLOB in der Datenbank (SQLITE) gespeichert werden.
@Embeddable
public enum AreaCellType { 
OCEAN("Ozean", "OC"),
LAND("Land", "LA");

private String name, abbr;

AreaCellType(String fullName, String abbr) {
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
public static AreaCellType fromAbbreviation(String s) {
    for (AreaCellType state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}

}
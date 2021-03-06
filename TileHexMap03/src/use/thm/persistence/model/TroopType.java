package use.thm.persistence.model;

import javax.persistence.Embeddable;

/* alte VErsion, hier funktioniert nicht das deserialisieren 
 public enum AreaType {
 	OCEAN, LAND
}
*/

//Embeddable. Ziel: Es soll nicht mehr als BLOB in der Datenbank (SQLITE) gespeichert werden.
//WICHTIG: Die Abkürzungen sollen für alle Spielsteine unterschiedlich sein. Das verflacht die Fallunterscheidung . TileMouseMotionHandlerTHM.onMouseRelease(...) und TileDao.getTroopType()  (20170802) 
@Embeddable
public enum TroopType { 
ARMY("Armee", "AR"),
FLEET("Flotte", "FL");

private String name, abbr;

TroopType(String fullName, String abbr) {
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
public static TroopType fromAbbreviation(String s) {
    for (TroopType state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}

}
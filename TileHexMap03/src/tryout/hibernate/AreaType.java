package tryout.hibernate;

/* alte VErsion, hier funktioniert nicht das deserialisieren 
 public enum AreaType {
 	OCEAN, LAND
}
*/

/* Version, für die Enumeration funktionieren soll
public enum USState { ALABAMA("Alabama", "AL"),
ALASKA("Alaska", "AK"),

WYOMING("Wyoming", "WY");

private String name, abbr;

USState(String fullName, String abbr) {
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
public static USState fromAbbreviation(String s) {
    for (USState state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct state: " + s);
}

}
*/

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
    throw new IllegalArgumentException("Not a correct state: " + s);
}

}
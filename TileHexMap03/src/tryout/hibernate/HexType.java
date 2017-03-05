package tryout.hibernate;

public enum HexType {
	AREA("AREA","AR");
	

private String name, abbr;

HexType(String fullName, String abbr) {
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
public static HexType fromAbbreviation(String s) {
    for (HexType state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}
	
}

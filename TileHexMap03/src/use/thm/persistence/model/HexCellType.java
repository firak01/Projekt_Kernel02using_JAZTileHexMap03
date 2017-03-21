package use.thm.persistence.model;

public enum HexCellType {
	AREA("AREA","AR");
	

private String name, abbr;

HexCellType(String fullName, String abbr) {
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
public static HexCellType fromAbbreviation(String s) {
    for (HexCellType state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}
	
}

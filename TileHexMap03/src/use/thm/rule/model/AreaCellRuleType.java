package use.thm.rule.model;

//Analog zu z.B. AreaCellType im ....persistance.model... Package. Allerdings wird hier erst mal nix perisitert und die Namen der Methoden sind andere.
public enum AreaCellRuleType { 
TILETYPE_FLEET("Gelände darf keine Flotte enthalten", 01),
TILETYPE_ARMY("Gelände darf keine Armee enthalten", 02),
STACKING_LIMIT_MAX("Die maximale Anzahl der Flotten in diesem Feld ist erreicht", 03);

private String sMessage;
private int iCode;

AreaCellRuleType(String sMessage, int iCode) {
    this.sMessage = sMessage;
    this.iCode = iCode;
}

@Override
public String toString() {
    return this.sMessage;
}

public int getCode() {
    return this.iCode;
}

public static AreaCellRuleType fromCode(int iCode) {
    for (AreaCellRuleType state : values()) {
        if (iCode == state.getCode())
            return state;
    }
    throw new IllegalArgumentException("Not a correct code: " + iCode);
}

}
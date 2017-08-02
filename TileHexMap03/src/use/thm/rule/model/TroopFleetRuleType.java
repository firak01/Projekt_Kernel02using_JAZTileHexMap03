package use.thm.rule.model;

//Analog zu z.B. AreaCellType im ....persistance.model... Package. Allerdings wird hier erst mal nix perisitert und die Namen der Methoden sind andere.
public enum TroopFleetRuleType { 
AREATYPE("Darf die Flotte nicht in dieses Gelände bewegen", 01),
STACKING_LIMIT_MAX("Die maximale Anzahl der Flotten in diesem Feld ist erreicht", 02);

private String sMessage;
private int iCode;

TroopFleetRuleType(String sMessage, int iCode) {
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

public static TroopFleetRuleType fromCode(int iCode) {
    for (TroopFleetRuleType state : values()) {
        if (iCode == state.getCode())
            return state;
    }
    throw new IllegalArgumentException("Not a correct code: " + iCode);
}

}
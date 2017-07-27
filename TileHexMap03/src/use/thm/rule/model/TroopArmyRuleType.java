package use.thm.rule.model;

//Analog zu z.B. AreaCellType im ....persistance.model... Package. Allerdings wird hier erst mal nix perisitert und die Namen der Methoden sind andere.
public enum TroopArmyRuleType { 
AREATYPE("Darf die Armee nicht in dieses Gelände bewegen", 01),
STACKING_LIMIT_MAX("Die maximale Anzahl der Armeen in diesem Feld ist erreicht", 02);

private String sMessage;
private int iCode;

TroopArmyRuleType(String sMessage, int iCode) {
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

public static TroopArmyRuleType fromCode(int iCode) {
    for (TroopArmyRuleType state : values()) {
        if (iCode == state.getCode())
            return state;
    }
    throw new IllegalArgumentException("Not a correct code: " + iCode);
}

}
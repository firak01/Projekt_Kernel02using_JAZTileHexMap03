package use.thm.persistence.interfaces.enums;

import java.util.EnumSet;

import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;

public interface IEnumSetTroopArmyVariantTHM extends IThiskeyProviderZZZ<Long>{
	
	//Das bring ENUM von sich auch mit
	public String getName();  //Das ist name() von Enum
	public int getIndex();      //Das ist ordinal() von Enum
	public String toString();
	
	//Das ist meine Interface Erweiterung
	public EnumSet<?> getEnumSetUsed();
	
	//TODO GOON 20180119: Dies an die Angaben zur "Variante" anpassen.
	//Das sind meine Erweiterungen
	public int getMapMoveRange();
	public String getImageUrl();
	public int getDefaulttextThisid();
	public int getImmutabletextThisid(); 	
}

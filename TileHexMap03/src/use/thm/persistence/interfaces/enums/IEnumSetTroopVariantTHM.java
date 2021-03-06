package use.thm.persistence.interfaces.enums;

import java.util.EnumSet;

import basic.zBasic.persistence.interfaces.enums.ICategoryProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyUserZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;

public interface IEnumSetTroopVariantTHM extends IThiskeyUserZZZ,IThiskeyProviderZZZ<Long>,ICategoryProviderZZZ{
	
	//Das bring ENUM von sich auch mit
	public String getName();  //Das ist name() von Enum
	public int getIndex();      //Das ist ordinal() von Enum
	public String toString();
		
	//Dies sind die "technisch/fachlichen" Angaben zur "Variante".
	public int getMapMoveRange();
	public float getHealthInitial();
	public String getImageUrlString();
	public int getDefaulttextThisid();
	public int getImmutabletextThisid();	
}

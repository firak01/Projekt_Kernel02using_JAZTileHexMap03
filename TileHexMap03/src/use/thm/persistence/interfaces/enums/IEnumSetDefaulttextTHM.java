package use.thm.persistence.interfaces.enums;

import java.util.EnumSet;

import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;

public interface IEnumSetDefaulttextTHM extends IThiskeyProviderZZZ<Long>{
	
	//Das bring ENUM von sich auch mit
	public String getName();  //Das ist name() von Enum
	public int getIndex();      //Das ist ordinal() von Enum
	public String toString();
	
	//Das sind meine Erweiterungen
	public String getShorttext();
	public String getLongtext();
	public String getDescription();
	public int getPosition(); //das ist normalerweies order()+1
	public EnumSet<?> getEnumSetUsed();
	
}

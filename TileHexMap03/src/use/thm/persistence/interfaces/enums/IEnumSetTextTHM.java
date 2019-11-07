package use.thm.persistence.interfaces.enums;

import java.util.EnumSet;

import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetZZZ;

public interface IEnumSetTextTHM extends IEnumSetZZZ, IThiskeyProviderZZZ<Long>{
		
	//Das sind meine Erweiterungen, die über das von IEnumSetZZZ hinausgehen.
	public String getShorttext();
	public String getLongtext();
	public String getDescription();	
}

package use.thm.rule.facade;

import basic.rule.facade.GeneralRuleHibernateFacadeZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

public class GeneralRuleFacadeTHM extends GeneralRuleHibernateFacadeZZZ implements IRuleStackingLimitTHM{
	
	protected GeneralRuleFacadeTHM(){
		super();
	}
	public GeneralRuleFacadeTHM(IHibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public int computeStackingLimit(String sCallingFlag) throws ExceptionZZZ {
		int iReturn = 0;
		main:{
			//STACKING LIMIT von 1 beim  PREINSERT, aber die Armee ist schon in der HEXCelle. Das sollte nur noch nicht committed sein...
			
			//TODO: Hole pro Geländetyp das Stackinglimit für einen Truppentyp
			//IDEE: Stackiing Limit vom Armee / Flottentyp-Spielstein abhängig machen. D.h. man muss 
			//         die Spielsteine ermitteln, deren "Größe" aufsummieren und dazu die Größe des 'neu platzierten" Spielsteins hinzuaddieren. 
			//         Diese Summe dann mit dem Stacking-Limit des Geländetyps vergleichen.
			int iStackingLimitUsedDefault = 1;
			if(StringZZZ.isEmpty(sCallingFlag)){
				iReturn = iStackingLimitUsedDefault;
			}else if(sCallingFlag.equalsIgnoreCase("PREINSERT")){
				iReturn = iStackingLimitUsedDefault;
			}else if(sCallingFlag.equalsIgnoreCase("UPDATE")){
				iReturn = iStackingLimitUsedDefault +1;
			}else{
				ExceptionZZZ ez = new ExceptionZZZ("Calling Flag '" + sCallingFlag + "' gefunden. Aber es wird hier noch nicht behandelt.", ExceptionZZZ.iERROR_PARAMETER_VALUE, ReflectCodeZZZ.getPositionCurrent());
		    	throw ez;
			}
		}//end main:
		return iReturn;
	}
}

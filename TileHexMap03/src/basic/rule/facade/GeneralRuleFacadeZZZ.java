package basic.rule.facade;

import use.thm.rule.model.TroopArmyRuleType;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;


public class GeneralRuleFacadeZZZ implements IFacadeResultRuleZZZ {
	private FacadeResultRuleZZZ objFacadeRuleResult = null;
	
	public GeneralRuleFacadeZZZ(){		
	}
	
	public FacadeResultRuleZZZ getFacadeRuleResult() {
		if(this.objFacadeRuleResult==null){
			this.objFacadeRuleResult = new FacadeResultRuleZZZ();
		}
		return this.objFacadeRuleResult;
	}

	public void setFacadeRuleResult(FacadeResultRuleZZZ objFacadeResult) {
		this.objFacadeRuleResult = objFacadeResult;
	}


	public String getMessagesAsString() {
		String sReturn = new String("");
		main:{					
			VectorExtendedZZZ<Enum<?>> vecMessage = this.getFacadeRuleResult().getMessageVector();				
			for(Object objMessage :  vecMessage){
				//@SuppressWarnings("unchecked")
				//Enum<TroopArmyRuleType> rule = (Enum<TroopArmyRuleType>) objMessage;					
				Enum<?> rule = (Enum<?>) objMessage;
				String sMessageTemp = rule.toString();
				if(sReturn.length()==0){
					sReturn = sMessageTemp;
				}else{
					sReturn += "\n" + sMessageTemp; 
				}				
			}//end for
		}//end main:
		return sReturn;
	}
	
		

}

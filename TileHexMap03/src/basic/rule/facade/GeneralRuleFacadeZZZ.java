package basic.rule.facade;


public class GeneralRuleFacadeZZZ implements IFacadeResultRuleZZZ {
	private FacadeResultRuleZZZ objFacadeRuleResult = null;
	
	public GeneralRuleFacadeZZZ(){		
	}
	
	@Override
	public FacadeResultRuleZZZ getFacadeRuleResult() {
		if(this.objFacadeRuleResult==null){
			this.objFacadeRuleResult = new FacadeResultRuleZZZ();
		}
		return this.objFacadeRuleResult;
	}

	@Override
	public void setFacadeRuleResult(FacadeResultRuleZZZ objFacadeResult) {
		this.objFacadeRuleResult = objFacadeResult;
	}

}

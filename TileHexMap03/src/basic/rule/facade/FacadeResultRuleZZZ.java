package basic.rule.facade;

import basic.zBasic.util.abstractList.VectorExtendedZZZ;

public class FacadeResultRuleZZZ {
	private VectorExtendedZZZ<Enum<?>> vector = null;
	
//	public HashMap<Integer,String> getMessageMap(){
//		if(this.hmMessage==null){
//			this.hmMessage = new HashMap<Integer, String>();
//		}
//		return this.hmMessage;
//	}
//	public void setMessageMap(HashMap<Integer,String> hmMessage){
//		this.hmMessage = hmMessage;
//	}
	
	public VectorExtendedZZZ<Enum<?>> getMessageVector(){
		if(this.vector==null){
			this.vector = new VectorExtendedZZZ<Enum<?>>();
		}
		return this.vector;
	}
	@SuppressWarnings("unchecked")
	public void addMessage(Enum<?> enumRule){
		this.getMessageVector().add(enumRule);
	}
}
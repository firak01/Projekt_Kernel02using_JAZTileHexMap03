package use.thm.web.webservice.axis2.pojo;

public class TroopArmyPojo {
	private String sUniquename;
	private String sType;
	private Integer intPlayer;
	private String sMapAlias;
	private Integer intMapX;
	private Integer intMapY;
	
	public String getUniquename(){
		return this.sUniquename;
	}
	public void setUniquename(String s){
		this.sUniquename = s;
	}
	
	public String getType(){
		return this.sType;
	}
	public void setType(String sType){
		this.sType = sType;
	}
	
	public Integer getPlayer(){
		return this.intPlayer;
	}
	public void setPlayer(Integer intPlayer){
		this.intPlayer = intPlayer;
	}
	
	public String getMapAlias(){
		return this.sMapAlias;
	}
	public void setMapAlias(String sMapAlias){
		this.sMapAlias = sMapAlias;
	}
	
	public Integer getMapX(){
		return this.intMapX;
	}
	public void setMapX(Integer intMapX){
		this.intMapX = intMapX;
	}
	
	public Integer getMapY(){
		return this.intMapY;
	}
	public void setMapY(Integer intMapY){
		this.intMapY = intMapY;
	}
	
	
	
}

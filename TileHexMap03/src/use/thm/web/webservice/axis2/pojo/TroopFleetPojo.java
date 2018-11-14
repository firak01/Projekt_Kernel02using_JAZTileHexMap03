package use.thm.web.webservice.axis2.pojo;

import java.util.ArrayList;
import java.util.List;

import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;

/* Von mir eingeführte Klasse, um den Datenaustausch zwischen dem WebService und dem DAO-Objekt 
 * möglichst einfach hinzubekommen. Wenn es eine Liste von diesem Pojo-Typ als Rückgabewert im WebService gibt,
 * dann klappt das automatisch. 
 * Z.B. in der Form einer WebService Definition:
 * public List<Tile> searchTileCollectionByHexCell(String sMapAlias, String sX, String sY){
			List<Tile> listReturn = new ArrayList<Tile>();
 * 
 * oder:
 * public List<TroopArmy> searchTroopArmyCollectionByHexCell(String sMapAlias, String sX, String sY){
			List<TroopArmy> listReturn = new ArrayList<TroopArmy>();
 */
public class TroopFleetPojo {
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

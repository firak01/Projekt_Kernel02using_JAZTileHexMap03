package tryout.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CellId implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String sMapAlias = new String("TEST"); //Hiermit werden alle Felder einer Map zusammengehalten, momentan wird nur 1 Map in der Tabelle gespeichert
	private String sMapX = null; //X-Koordinate
	private String sMapY = null; //Y-Koordinate
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public CellId(){
	}
	public CellId(String sMapAlias, String sMapX, String sMapY){
		this.sMapAlias = sMapAlias;
		this.sMapX = sMapX;
		this.sMapY = sMapY;		
	}
	
	public String getMapAlias(){
		return this.sMapAlias;
	}
	
	public String getMapX(){
		return this.sMapX;
	}
	public String getMapY(){
		return this.sMapY;
	}
	
	@Override
	public int hashCode(){
		return this.sMapAlias.hashCode() + this.sMapX.hashCode() + this.sMapY.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(!(obj instanceof CellId)){
			return false;
		}
		
		CellId objId = (CellId)obj;
		if(!sMapAlias.equalsIgnoreCase(objId.getMapAlias())){
			return false;
		}
		if(!sMapX.equalsIgnoreCase(objId.getMapX())){
			return false;
		}
		if(!sMapY.equalsIgnoreCase(objId.getMapY())){
			return false;
		}
		
		return true;
		
	}
	 
	
}

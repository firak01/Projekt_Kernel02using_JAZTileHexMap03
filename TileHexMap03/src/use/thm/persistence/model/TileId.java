package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import basic.persistence.model.IPrimaryKeys;

//Buch "Java Persistence API", S. 48
//Merke: Bei der Variante mit der Generierten ID dies nicht mehr als @EmbeddedID einbinden, man kann nur eine ID Variante verwenden.

@Embeddable
@Access(AccessType.PROPERTY)
public class TileId { //Diese Klasse dient hier nicht als EmbeddedID, darum nicht: implements IPrimaryKeys{ //IPrimaryKeys hat zwar @Embeddable aber das wird scheinbar nicht vererbt.

	/** Diese Klasse hat den Zweck eine ID zur Verfügung zu stellen.
	 *   @Id ist nicht erlaubt. Alle Properties bilden automatisch den Schlüssel.
	 */
	private static final long serialVersionUID = 1L;

	private String sMapAlias = new String("TEST"); //Hiermit werden alle Felder einer Map zusammengehalten, momentan wird nur 1 Map in der Tabelle gespeichert	
	private String sPlayer = null; 
	private String sUniquename = null;

	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public TileId(){
	}
	public TileId(String sMapAlias, String sPlayer, String sUniquename){
		this.sMapAlias = sMapAlias;
		this.sPlayer = sPlayer;
		this.sUniquename = sUniquename;		
	}
	
	public String getMapAlias(){
		return this.sMapAlias;
	}
	public void setMapAlias(String sAlias){
		this.sMapAlias = sAlias;
	}

	public String getPlayer(){
		return this.sPlayer;
	}
	
	public void setPlayer(String sPlayer){
		this.sPlayer = sPlayer;
	}

	public String getUniquename(){
		return this.sUniquename;
	}
	public void setUniquename(String sUniquename){
		this.sUniquename = sUniquename;
	}
	
	//Merke: Es hat nicht geklappt hier den Generator für einen Key einzubinden
	
	@Override
	public int hashCode(){
		return this.sMapAlias.hashCode() + this.sPlayer.hashCode() + this.sUniquename.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(!(obj instanceof TileId)){
			return false;
		}
		
		TileId objId = (TileId)obj;
		if(!sMapAlias.equalsIgnoreCase(objId.getMapAlias())){
			return false;
		}
		if(!sPlayer.equalsIgnoreCase(objId.getPlayer())){
			return false;
		}
		if(!sUniquename.equalsIgnoreCase(objId.getUniquename())){
			return false;
		}
		
		return true;
		
	}
	 
	
}

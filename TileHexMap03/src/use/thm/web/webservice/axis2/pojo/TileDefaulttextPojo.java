package use.thm.web.webservice.axis2.pojo;

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
public class TileDefaulttextPojo{
	private Long lngThiskey;
	private String sShorttext;
	private String sLongtext;
	private String sDescription;

	public void setThiskey(Long lngThiskey) {
		this.lngThiskey = lngThiskey;
	}
	public Long getThiskey(){
		return this.lngThiskey;
	}

	public void setShorttext(String sShorttext) {
		this.sShorttext = sShorttext;
	}
	public String getShorttext(){
		return this.sShorttext;
	}
	
	public void setLongtext(String sLongtext) {
		this.sLongtext = sLongtext;
	}
	public String getLongtext(){
		return this.sLongtext;
	}

	public void setDescriptiontext(String sDescription) {
		this.sDescription = sDescription;
	}
	public String getDescriptiontext(){
		return this.sDescription;
	}

}

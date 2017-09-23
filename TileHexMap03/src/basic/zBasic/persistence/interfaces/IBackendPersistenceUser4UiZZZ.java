package basic.zBasic.persistence.interfaces;

/* Über diese Methoden wird der Zugriff des UI auf das Backens sichergestellt. 
 */
public interface IBackendPersistenceUser4UiZZZ extends IBackendPersistenceDtoUserZZZ{
	/* Variable zur eindeutigen Identifizierung, um die Entsprechung des UI-Objekts im Backend zu suchen
	 * Diese sollte nur bei der Erzeugung des UI - Objekts erstellt werden und dann nicht mehr verändert werde. */
	public String getUniquename();	
}

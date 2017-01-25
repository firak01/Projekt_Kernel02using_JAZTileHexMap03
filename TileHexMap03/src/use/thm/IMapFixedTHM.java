package use.thm;

import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public interface IMapFixedTHM {
	/*Setzt die gespeicherte Position auf der Karte in Form von XY-Koordinaten.
	 * Dabei sind in diesem Interface die Werte nicht veränderbar, z.B. für Gebäude, Landschaften, etc.
	 * 
	 */
	public String getMapX();
	public String getMapY();
	
	public KernelJPanelCascadedZZZ getMapPanel();
}

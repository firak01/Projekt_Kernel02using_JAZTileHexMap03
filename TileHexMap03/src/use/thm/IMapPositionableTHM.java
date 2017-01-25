package use.thm;

import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public interface IMapPositionableTHM extends IMapFixedTHM{
	/*Setzt die gespeicherte Position auf der Karte in Form von XY-Koordinaten
	 * Dabei sind durch dieses Interface die Werte veränderbar, z.B. Armeen, Flotten.
	 */
	public void setMapX(String sMapAliasX);
	public void setMapY(String sMapAliasY);
}

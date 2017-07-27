package use.thm.client.event;

import java.awt.Event;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import use.thm.IMapPositionableTHM;
import use.thm.client.component.TileTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.IListenerSelectionResetZZZ;
import basic.zKernelUI.component.model.ISenderSelectionResetZZZ;

public class TileMoveEventBrokerTHM extends KernelUseObjectZZZ implements ISenderTileMovedTHM{
	public TileMoveEventBrokerTHM(KernelZZZ objKernel){
		super(objKernel);
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#fireEvent(basic.zKernelUI.component.model.KernelEventComponentSelectionResetZZZ)
	 */
	private ArrayList listaLISTENER_REGISTERED = new ArrayList();  //Das ist die Arrayliste, in welche  die registrierten Komponenten eingetragen werden 
																							  //wichtig: Sie muss private sein und kann nicht im Interace global definiert werden, weil es sonst nicht m�glich ist 
	                                                                                          //             mehrere Events, an verschiedenen Komponenten, unabh�ngig voneinander zu verwalten.
	private ArrayList listaEVENT_STOPPER = new ArrayList();   //TODO: Als Interface ISenderFeedback
																								//Wenn hierin Eintr�ge vorhanden sind, dann sind die objInterfaceUser (also die angemeldenent Listener) 
	                                                                                           //nicht mit der Fortf�hrung des Events einverstanden.
	
	public final void fireEvent(EventCellEnteredTHM event){	
	//nur f�r SPIELSTEINE if(event.getSource() instanceof IMapPositionableTHM){
		boolean bQuery=false;
		this.getListenerEventStopper().clear(); //Am Anfang die Feedback-Liste leeren
		
		
		if(event.getSource() instanceof TileTHM){
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				IListenerTileMovedTHM objlnterfaceUser = (IListenerTileMovedTHM) this.getListenerRegisteredAll().get(i);
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + event.getSource().getClass().getName() + " fired: " + i);
				bQuery = objlnterfaceUser.onTileEnter(event);  //hat das geklappt ? oder hat einer etwas dagegen
				
				if(bQuery==false){
					this.setContinue(objlnterfaceUser, false);
				}
				
			}
		}else if( (event.getSource() instanceof TileTHM)==false && event.getSource() instanceof IMapPositionableTHM) {
			
			//Dann wurde etwas anderes als ein "Spielstein bewegt"
			
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				////  IListenerSelectionResetZZZ l = (IListenerSelectionResetZZZ) this.getListenerRegisteredAll().get(i);				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist zwar beweglich aber kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
				//?????   l.doMove(event);
			}
		}else{
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist nicht beweglich und  kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
		}
	}
	
	
	public final void fireEvent(EventCellLeavedTHM event){
		//Merke: Event.getSource() ist der Spielstein, der gerade bewegt wird.
		boolean bQuery=false;
		this.getListenerEventStopper().clear(); //Am Anfang die Feedback-Liste leeren
		
		//		nur f�r SPIELSTEINE if(event.getSource() instanceof IMapPositionableTHM){
		if(event.getSource() instanceof TileTHM){
			
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				IListenerTileMovedTHM objlnterfaceUser =(IListenerTileMovedTHM) this.getListenerRegisteredAll().get(i);
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellLeavedTHM by " + event.getSource().getClass().getName() + " fired: " + i);
				bQuery = objlnterfaceUser.onTileLeave(event);
				
				
				if(bQuery==false){
					this.setContinue(objlnterfaceUser, false);
				}
			}
		}else if( (event.getSource() instanceof TileTHM)==false && event.getSource() instanceof IMapPositionableTHM) {
			
			//Dann wurde etwas anderes als ein "Spielstein bewegt"
			
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				////  IListenerSelectionResetZZZ l = (IListenerSelectionResetZZZ) this.getListenerRegisteredAll().get(i);				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellLeavedTHM by " + this.getClass().getName() + " - object !!!!! Das ist zwar beweglich aber kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
				//?????   l.doMove(event);
			}
		}else{
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellLeavedTHM by " + this.getClass().getName() + " - object !!!!! Das ist nicht beweglich und  kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
		}
	}
	
	
	public final void fireEvent(EventTileDroppedToCellTHM eventTileDropped){
		//Merke: Event.getSource() ist der Spielstein,d er gerade bewegt wird.
		main:{
		boolean bQuery=false;
		this.getListenerEventStopper().clear(); //Am Anfang die Feedback-Liste leeren
		
		//nur f�r SPIELSTEINE if(event.getSource() instanceof IMapPositionableTHM){
		if(eventTileDropped.getSource() instanceof TileTHM){
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				IListenerTileMovedTHM objlnterfaceUser =(IListenerTileMovedTHM) this.getListenerRegisteredAll().get(i);
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventTileDroppedTHM by " + eventTileDropped.getSource().getClass().getName() + " fired: " + i);
				bQuery = objlnterfaceUser.onTileDrop(eventTileDropped);
				
				if(bQuery==false){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventTileDroppedTHM by " + eventTileDropped.getSource().getClass().getName() + " fired: " + i + " !!! ungültig  bei start von: " + eventTileDropped.getTile().getMapX() + "/" + eventTileDropped.getTile().getMapY()  + " !!!");
					
					String sX = eventTileDropped.getMapX();
					String sY = eventTileDropped.getMapY();
					JOptionPane.showMessageDialog(null, "UI-Prüfung. Backendprüfung sollte das eigentlich verhindern: Spielstein '" + eventTileDropped.getTile().getName() + "' kann hier (" + sX + "/" +sY +") nicht erzeugt werden. Feld ist schon von anderem Spielstein besetzt.");
					
					this.setContinue(objlnterfaceUser, false);					
					break main;
				}
			}
		}else if( (eventTileDropped.getSource() instanceof TileTHM)==false && eventTileDropped.getSource() instanceof IMapPositionableTHM) {
			
			//Dann wurde etwas anderes als ein "Spielstein bewegt"
			
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				////  IListenerSelectionResetZZZ l = (IListenerSelectionResetZZZ) this.getListenerRegisteredAll().get(i);				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist zwar beweglich aber kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
				//?????   l.doMove(event);
			}
		}else{
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist nicht beweglich und  kein Spielstein !!!! Es passiert nix weiter, vollkommen �berfl�ssig  !!!!");
		}
	}//End main:
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#removeSelectionResetListener(basic.zKernelUI.component.model.ISelectionResetListener)
	 */
	public final void removeListenerTileMoved(IListenerTileMovedTHM lnterfaceUser){
		this.getListenerRegisteredAll().remove(lnterfaceUser);
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#addSelectionResetListener(basic.zKernelUI.component.model.ISelectionResetListener)
	 */
	public final void addListenerTileMoved(IListenerTileMovedTHM lnterfaceUser){
		this.getListenerRegisteredAll().add(lnterfaceUser);
	}
	
	public final ArrayList getListenerRegisteredAll(){
		return this.listaLISTENER_REGISTERED;
	}
	public final ArrayList getListenerEventStopper(){
		return this.listaEVENT_STOPPER;
	}
	
	
	/** Listener Objekte werden der Array Liste hinzugef�gt.
	 *   Damit sind in der ArrayListe diejenigen, die den Event "ablehnen"
	* @param eventListener
	* @param bValue
	* 
	* lindhaueradmin; 04.10.2008 14:29:05
	 */
	public void setContinue(EventListener eventListener, boolean bValue){
		if(bValue==true){
			if(this.getListenerEventStopper().contains(eventListener)){
				this.getListenerEventStopper().remove(eventListener);
			}else{
				//nix machen
			}
		}else if(bValue==false){
			if(this.getListenerEventStopper().contains(eventListener)){
				//nix machen
			}else{
				this.getListenerEventStopper().add(eventListener);
			}
		}
	}
	/** Sobald einer der Listener Einspruch erhebt, geht nix mehr
	* @return
	* 
	* lindhaueradmin; 04.10.2008 14:28:39
	 */
	public boolean getContinue(){
		if(this.getListenerEventStopper().isEmpty()){
			return true;
		}else{
			return false;
		}
	}
}

package use.thm.client.event;

import java.util.ArrayList;
import java.util.EventListener;

import use.thm.IMapPositionableTHM;
import use.thm.client.component.TileTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public class TileMetaEventBrokerTHM extends KernelUseObjectZZZ implements ISenderTileMetaTHM{
	public TileMetaEventBrokerTHM(KernelZZZ objKernel){
		super(objKernel);
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#fireEvent(basic.zKernelUI.component.model.KernelEventComponentSelectionResetZZZ)
	 */
	private ArrayList listaLISTENER_REGISTERED = new ArrayList();  //Das ist die Arrayliste, in welche  die registrierten Komponenten eingetragen werden 
																							  //wichtig: Sie muss private sein und kann nicht im Interace global definiert werden, weil es sonst nicht möglich ist 
	                                                                                          //             mehrere Events, an verschiedenen Komponenten, unabhängig voneinander zu verwalten.
	private ArrayList listaEVENT_STOPPER = new ArrayList();   //TODO: Als Interface ISenderFeedback
																								//Wenn hierin Einträge vorhanden sind, dann sind die objInterfaceUser (also die angemeldenent Listener) 
	                                                                                           //nicht mit der Fortführung des Events einverstanden.
	
	public final void fireEvent(EventTileCreatedInCellTHM event){	
	//nur für SPIELSTEINE if(event.getSource() instanceof IMapPositionableTHM){
		boolean bQuery=false;
		this.getListenerEventStopper().clear(); //Am Anfang die Feedback-Liste leeren
		
		
		if(event.getSource() instanceof TileTHM){
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				IListenerTileMetaTHM objlnterfaceUser = (IListenerTileMetaTHM) this.getListenerRegisteredAll().get(i);
				//System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventTileCreatedInCellTHM by " + event.getSource().getClass().getName() + " fired: " + i);
				bQuery = objlnterfaceUser.onTileCreated(event);  //hat das geklappt ? oder hat einer etwas dagegen
				
				if(bQuery==false){
					this.setContinue(objlnterfaceUser, false);
				}
				
			}
		}else if( (event.getSource() instanceof TileTHM)==false && event.getSource() instanceof IMapPositionableTHM) {
			
			//Dann wurde etwas anderes als ein "Spielstein bewegt"
			
			for(int i = 0 ; i < this.getListenerRegisteredAll().size(); i++){
				////  IListenerSelectionResetZZZ l = (IListenerSelectionResetZZZ) this.getListenerRegisteredAll().get(i);				
				//System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist zwar beweglich aber kein Spielstein !!!! Es passiert nix weiter, vollkommen überflüssig  !!!!");
				//?????   l.doMove(event);
			}
		}else{
			//System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "# EventCellEnteredTHM by " + this.getClass().getName() + " - object !!!!! Das ist nicht beweglich und  kein Spielstein !!!! Es passiert nix weiter, vollkommen überflüssig  !!!!");
		}
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#removeSelectionResetListener(basic.zKernelUI.component.model.ISelectionResetListener)
	 */
	public final void removeListenerTileMoved(IListenerTileMovedTHM lnterfaceUser){
		this.getListenerRegisteredAll().remove(lnterfaceUser);
	}
		
	public final ArrayList getListenerRegisteredAll(){
		return this.listaLISTENER_REGISTERED;
	}
	public final ArrayList getListenerEventStopper(){
		return this.listaEVENT_STOPPER;
	}
	
	
	/** Listener Objekte werden der Array Liste hinzugefügt.
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

	//### Interface IListernerTileMeta ##################
	public void removeListenerTileMeta(IListenerTileMetaTHM InterfaceUser) {
		this.getListenerRegisteredAll().remove(InterfaceUser);
	}
	
	/* (non-Javadoc)
	 * @see use.via.client.module.export.ISenderEventComponentReset#addSelectionResetListener(basic.zKernelUI.component.model.ISelectionResetListener)
	 */
	public final void addListenerTileMeta(IListenerTileMetaTHM lnterfaceUser){
		this.getListenerRegisteredAll().add(lnterfaceUser);
	}
}

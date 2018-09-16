package use.thm.client.component.menu;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import custom.zKernel.LogZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.client.DlgAboutTHM;
import use.thm.client.PanelMain_CENTERTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.TileTHM;
import use.zBasicUI.component.UIHelper_SwingWorker4ProgramMapZoomTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelActionJMenuZZZ;
import basic.zKernelUI.component.KernelButtonGroupZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class TilePopupMenuTHM extends JPopupMenu implements IConstantZZZ, IObjectZZZ, IKernelUserZZZ {
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJFrameCascadedZZZ frameParent;
	private KernelJPanelCascadedZZZ panelParent;
	
	private TileTHM objTile;
	
    JMenuItem itemAbout;
    JMenuItem itemDetail;
    JMenuItem itemDelete;
    public TilePopupMenuTHM(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent, KernelJPanelCascadedZZZ panelParent, TileTHM objTile){
    	super();
    	try {
    		this.setKernelObject(objKernel);
    		this.setLogObject(this.getKernelObject().getLogObject());
    		this.setFrameParent(frameParent);
    		this.setPanelParent(panelParent);
    		this.setTile(objTile);
    		
			Font objFontGui = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
			this.setFont(objFontGui);			
			 
			itemAbout = new JMenuItem("About");
			itemAbout.setFont(objFontGui);			
			ActionMenuHelpAboutTHM listenerAbout = new ActionMenuHelpAboutTHM(this.getKernelObject(), this.getFrameParent());
			itemAbout.addActionListener( listenerAbout);			 			 
		    add(itemAbout);
		    
			itemDetail = new JMenuItem("Detail");
			itemDetail.setFont(objFontGui);			
			ActionMenuTileDetailTHM listenerDetail = new ActionMenuTileDetailTHM(this.getKernelObject(), this.getPanelParent());
			itemDetail.addActionListener( listenerDetail);			 			 
		    add(itemDetail);
		    
			itemDelete = new JMenuItem("Delete");
			itemDelete.setFont(objFontGui);
			ActionMenuTileDeleteTHM listenerDelete = new ActionMenuTileDeleteTHM(this.getKernelObject(), this.getPanelParent());
			itemDelete.addActionListener( listenerDelete);			 			
		    add(itemDelete);
		    
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    //##### GETTER / SETTER
    private void setTile(TileTHM objTile){
    	this.objTile = objTile;
    }
    public TileTHM getTile(){
    	return this.objTile;
    }
    

	//######################
	//### Interfaces
	/* (non-Javadoc)
	 * @see basic.zBasic.IObjectZZZ#getExceptionObject()
	 */
	public ExceptionZZZ getExceptionObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see basic.zBasic.IObjectZZZ#setExceptionObject(basic.zBasic.ExceptionZZZ)
	 */
	public void setExceptionObject(ExceptionZZZ objException) {
		// TODO Auto-generated method stub	
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#getKernelObject()
	 */
	public KernelZZZ getKernelObject() {
		return this.objKernel;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#setKernelObject(custom.zKernel.KernelZZZ)
	 */
	public void setKernelObject(KernelZZZ objKernel) {
		this.objKernel = objKernel;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#getLogObject()
	 */
	public LogZZZ getLogObject() {
		return this.objLog;
	}

	/* (non-Javadoc)
	 * @see basic.zKernel.IKernelZZZ#setLogObject(custom.zKernel.LogZZZ)
	 */
	public void setLogObject(LogZZZ objLog) {
		this.objLog = objLog;
	}
	
	
	//###### GETTER / SETTER
	public KernelJFrameCascadedZZZ getFrameParent(){
		return this.frameParent;
	}
	public void setFrameParent(KernelJFrameCascadedZZZ frameParent){
		this.frameParent = frameParent;
	}
	
	public KernelJPanelCascadedZZZ getPanelParent(){
		return this.panelParent;
	}
	public void setPanelParent(KernelJPanelCascadedZZZ panelParent){
		this.panelParent = panelParent;
	}
	
 //######################################################
//	Innere Klassen, welche eine Action behandelt
//	########### ActionListener #################################
	private class ActionMenuHelpAboutTHM extends KernelActionJMenuZZZ{
		private DlgAboutTHM dlgAbout=null;
		
		public ActionMenuHelpAboutTHM(KernelZZZ objKernel, KernelJFrameCascadedZZZ frmParent) {
			super(objKernel, frmParent);			
		}
		
		public void actionPerformed(ActionEvent e){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing Action: 'Help/About'");
			if(this.dlgAbout==null){
				this.dlgAbout = new DlgAboutTHM(this.getKernelObject(), this.getFrameParent());
			}
			try {
				dlgAbout.showDialog(this.getFrameParent(), "Help/About");
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Ended Action: 'Help/About'");
			} catch (ExceptionZZZ ez) {					
				System.out.println(ez.getDetailAllLast()+"\n");
				ez.printStackTrace();
				ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());			
			}
		}
		
	}//End class ActionMenuHelpAbout
	
//	Innere Klassen, welche eine Action behandelt
//	#######################################
	//TILE DETAIL Innere Klassen, welche eine Action behandelt. (Merke in den ZOOM-Buttons wird dann auch noch ein Swing Worker gestartet. Den brauchts hier nicht.)	
	class ActionMenuTileDetailTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
		
		public ActionMenuTileDetailTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'Tile Detail'");
			TileCommonComponentHelperUITHM.showTileDetailDialog(objTile, ae);			
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
		}

		@Override
		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {		
		}			 							
		
		
}//End class ...KErnelActionCascaded....
//##############################################

	//	Innere Klassen, welche eine Action behandelt
	//	#######################################
		//TILE DETAIL Innere Klassen, welche eine Action behandelt. (Merke in den ZOOM-Buttons wird dann auch noch ein Swing Worker gestartet. Den verwenden wir hier auch.)	
		class ActionMenuTileDeleteTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{
			
			public ActionMenuTileDeleteTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
				super(objKernel, panelParent);			
			}
			
			public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'Tile Delete'");
				
				KernelZZZ objKernel = this.getKernelObject();				
				SwingWorker4ProgramTileDelete worker = new SwingWorker4ProgramTileDelete(objKernel, objTile, null);
				worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
				
				return true;
			}
	
			public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
				return true;
			}
	
			public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			}
	
			@Override
			public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {		
			}			 							
			
			
			//############################################
		    //### PRIVATE KLASSEN #############################
			class SwingWorker4ProgramTileDelete extends SwingWorker implements IObjectZZZ, IKernelUserZZZ{
									private KernelZZZ objKernel;
									private LogZZZ objLog;
									private KernelJPanelCascadedZZZ panel;
									private String[] saFlag4Program;
									
									private TileTHM objTile;
												
									protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
									
									public SwingWorker4ProgramTileDelete(KernelZZZ objKernel, TileTHM objTile, String[] saFlag4Program){
										super();
										
										this.objKernel = objKernel;
										this.objLog = objKernel.getLogObject();
										
										this.objTile = objTile;													
										this.panel = objTile.getMapPanel();
										
										this.saFlag4Program = saFlag4Program;					
									}
									
									//#### abstracte - Method aus SwingWorker
									public Object construct() {
//										try{
											//UIHelper_SwingWorker4ProgramMapZoomTHM.constructPlus();
										    									
										    System.out.println("Updating Panel ...");
											KernelJPanelCascadedZZZ objPanelParent = this.panel.getPanelParent();
											updatePanelMap(objPanelParent); //20180819: Damit das klappt muss eine Komponentenliste über alle Panels zusammengesucht werden....																							
//										}catch(ExceptionZZZ ez){
//											System.out.println(ez.getDetailAllLast());
//											ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());					
//										}
										return "all done";
									}
											
									/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
									 *  
									* @param stext
									* 					
									 */
									public void updatePanelMap(KernelJPanelCascadedZZZ panel2updateStart){
										this.panel = panel2updateStart;

//										Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
										Runnable runnerUpdatePanel= new Runnable(){

											public void run(){
//												try {
													ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Removing Tile: BACKEND");
													//TODO GOON 20180916: Den Spielstein im Backen löschen!!!
													
													ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Removing Tile: FRONTEND");
													HexCellTHM objCell = (HexCellTHM) objTile.getParent();													
													objCell.remove(objTile); //reicht das im Frontend aus?
													
													ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating HEXCELL in MAP");
																																							
													//PROBLEM: Panels updaten und neu zeichnen
													//ApplicationSingletonTHM.getInstance().setHexFieldSideLengthCurrent(0); //20180901: Damit sich auch die Größe der Sechsecke ändert. Deren Seitenlänge aus der ini-Formel neu Berechnen lassen.													
//													PanelMain_CENTERTHM panelMap = (PanelMain_CENTERTHM) panel.searchPanelSub("CENTER");		
//													panelMap.validate();
//													panelMap.repaint();
													
													//Also: Es reicht, die einzelne Zelle des Spielsteins neu zu zeichnen.										            
										            objCell.validate();
										            objCell.repaint();
										            
																															
//												} catch (ExceptionZZZ e) {
//													e.printStackTrace();
//												}
											}
										};
										
										SwingUtilities.invokeLater(runnerUpdatePanel);	
										//Ggfs. nach dem Swing Worker eine Statuszeile etc. aktualisieren....
									}
									
									
									public KernelZZZ getKernelObject() {
										return this.objKernel;
									}

									public void setKernelObject(KernelZZZ objKernel) {
										this.objKernel = objKernel;
									}

									public LogZZZ getLogObject() {
										return this.objLog;
									}

									public void setLogObject(LogZZZ objLog) {
										this.objLog = objLog;
									}
									
									
									/* (non-Javadoc)
									 * @see zzzKernel.basic.KernelAssetObjectZZZ#getExceptionObject()
									 */
									public ExceptionZZZ getExceptionObject() {
										return this.objException;
									}
									/* (non-Javadoc)
									 * @see zzzKernel.basic.KernelAssetObjectZZZ#setExceptionObject(zzzKernel.custom.ExceptionZZZ)
									 */
									public void setExceptionObject(ExceptionZZZ objException) {
										this.objException = objException;
									}
									
									
									/**Overwritten and using an object of jakarta.commons.lang
									 * to create this string using reflection. 
									 * Remark: this is not yet formated. A style class is available in jakarta.commons.lang. 
									 */
									public String toString(){
										String sReturn = "";
										sReturn = ReflectionToStringBuilder.toString(this);
										return sReturn;
									}

								} //End Class MySwingWorker

								
			
	}//End class ...KErnelActionCascaded....
	
}

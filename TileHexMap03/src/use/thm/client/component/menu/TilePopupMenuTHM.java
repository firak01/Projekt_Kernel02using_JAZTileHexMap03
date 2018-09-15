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
//			ActionMenuTileDeleteTHM listenerDelete = new ActionMenuTileDeleteTHM(this.getKernelObject(), this.getFrameParent());
//			itemDelete.addActionListener( listenerDelete);			 			 //TileCommonComponentHelperUITHM.showTileDetailDialog(this.getTile(), arg0);
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
	
}

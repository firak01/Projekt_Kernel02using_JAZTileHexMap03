package use.thm.client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import use.thm.ApplicationSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelActionJMenuZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.LogZZZ;




/**
 * @author 0823
 *
 */
public class MenuMainTHM extends JMenuBar implements IConstantZZZ, IObjectZZZ, IKernelUserZZZ {
	private KernelZZZ objKernel;
	private LogZZZ objLog;
	private KernelJFrameCascadedZZZ frameParent;
	
	public MenuMainTHM(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		super();
		this.setKernelObject(objKernel);
		this.setLogObject(this.getKernelObject().getLogObject());
		this.setFrameParent(frameParent);
		
		Font objFontGui = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
		
		
		JMenu menuHelp = new JMenu("Help");
		menuHelp.setFont(objFontGui);
							
		JMenuItem mitemAbout = new JMenuItem("About");
		mitemAbout.setFont(objFontGui);
		ActionMenuHelpAboutTHM listenerAbout = new ActionMenuHelpAboutTHM(this.getKernelObject(), this.getFrameParent());
		mitemAbout.addActionListener( listenerAbout);
		
		menuHelp.add(mitemAbout);		
		this.add(menuHelp);			
	}
	
	//#### GETTER / SETTER 
	public KernelJFrameCascadedZZZ getFrameParent(){
		return this.frameParent;
	}
	public void setFrameParent(KernelJFrameCascadedZZZ frameParent){
		this.frameParent = frameParent;
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
	
	
}


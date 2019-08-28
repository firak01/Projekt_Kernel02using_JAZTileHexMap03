package use.thm;



import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.client.FrmMapSingletonTHM;
import use.thm.client.component.HexMapTHM;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ.FLAGZ;
import basic.zKernelUI.util.JFrameHelperZZZ;

public class ApplicationSingletonTHM  extends ApplicationTHM{
	private static ApplicationSingletonTHM objApplicationSingleton = null;  //muss static sein, wg. getInstance()!!!
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @param objFrame
	 * @throws ExceptionZZZ
	 */
	private ApplicationSingletonTHM(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	private ApplicationSingletonTHM(){
		super(); 
	}
	
	public static ApplicationSingletonTHM getInstance() throws ExceptionZZZ{
		if(objApplicationSingleton==null){
			objApplicationSingleton = new ApplicationSingletonTHM();
			
			//!!! Das Singleton Kernel Objekt holen und setzen !!!
			KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
			objApplicationSingleton.setKernelObject(objKernel);			
		}
		return objApplicationSingleton;		
	}
	
	public static ApplicationSingletonTHM getInstance(IKernelZZZ objKernel) throws ExceptionZZZ{
		if(objApplicationSingleton==null){
			objApplicationSingleton = new ApplicationSingletonTHM(objKernel);
		}
		return objApplicationSingleton;		
	}

	
	
		
	//###################################################
	/** TODO What the method does.
	 * @param args
	 * 
	 * lindhaueradmin; 10.09.2008 14:31:05
	 */
	public static void main(String[] args) {
		try{
		//NEIN, nur im frmMain:   ConfigZZZ objConfig = new ConfigZZZ(saArg);
		
		//---- Nun das eigentliche KernelObjekt initiieren. Dabei können z.B. Debug-Einstellungen ausgwählt worden sein.
		//KernelZZZ objKernel = new KernelZZZ(sApplicationKey, sSystemNr, sDir, sFile,(String)null);
		//20170413 ERSETZE DIESE ZENTRALE STELLE DURCH EIN SINGELTON... KernelZZZ objKernel = new KernelZZZ("THM", "01", "", "ZKernelConfigTileHexMap02Client.ini", (String[]) null);
	    String[] saFlagZpassed={"USEFORMULA","USEFORMULA_MATH"};
		//KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance( "01", "", "ZKernelConfigTileHexMap02Client.ini", saFlagZpassed);
	    KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance(args, saFlagZpassed);
		
		ApplicationSingletonTHM objApplication = ApplicationSingletonTHM.getInstance(objKernel);
		objApplication.launchIt();
		
		
		} catch (ExceptionZZZ ez) {				
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
	}
}

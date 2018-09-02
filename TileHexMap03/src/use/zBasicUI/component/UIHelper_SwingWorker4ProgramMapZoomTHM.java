package use.zBasicUI.component;

import use.thm.ApplicationSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import custom.zKernel.file.ini.FileIniZZZ;

public class UIHelper_SwingWorker4ProgramMapZoomTHM {
	public static void constructMinus() throws ExceptionZZZ{
		UIHelper_SwingWorker4ProgramMapZoomTHM.construct(false);
	}
	
	public static void constructPlus() throws ExceptionZZZ{
		UIHelper_SwingWorker4ProgramMapZoomTHM.construct(true);
	}
		
	private static void construct(boolean bPlus) throws ExceptionZZZ{
		//0. Dene GUI Zoom um +1 erhöhen
		KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
		FileIniZZZ objFileConfig = objKernel.getFileConfigIni();
	
		//0. Hole den gerade in der Applikation für das GUI eingestellten ZoomFaktor. Diesen als Variable für die INI-Berechnungen zur Verfügung stellen
		//String sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();	
		
		String sMapZoomFactorAliasCurrent = null;
		if(bPlus){
			//0. 20180819: Holen einen um +1 erhöhten ZoomFactorAlias
			sMapZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getHexZoomFactorAliasNext();						
			
		}else{
			//0. 20180819: Holen einen um +1 verringerten ZoomFactorAlias
			sMapZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getHexZoomFactorAliasPrevious();	
		}
		ApplicationSingletonTHM.getInstance().setHexZoomFactorAliasCurrent(sMapZoomFactorAliasCurrent);
		
		String sMapZoomFactorCurrent = ApplicationSingletonTHM.getInstance().getHexZoomFactorCurrent();							
		//objFileConfig.setVariable("MapZoomFactorUsed", sMapZoomFactorCurrent);
		objFileConfig.setVariable("HexZoomFactorUsed", sMapZoomFactorCurrent);
				
	}
}

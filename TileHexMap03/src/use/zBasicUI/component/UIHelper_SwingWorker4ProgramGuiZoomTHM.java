package use.zBasicUI.component;

import use.thm.ApplicationSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import custom.zKernel.file.ini.FileIniZZZ;

public class UIHelper_SwingWorker4ProgramGuiZoomTHM {
	public static void constructMinus() throws ExceptionZZZ{
		UIHelper_SwingWorker4ProgramGuiZoomTHM.construct(false);
	}
	
	public static void constructPlus() throws ExceptionZZZ{
		UIHelper_SwingWorker4ProgramGuiZoomTHM.construct(true);
	}
		
	private static void construct(boolean bPlus) throws ExceptionZZZ{
		//0. Dene GUI Zoom um +1 erhöhen
		KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
		FileIniZZZ objFileConfig = objKernel.getFileConfigIni();
	
		//0. Hole den gerade in der Applikation für das GUI eingestellten ZoomFaktor. Diesen als Variable für die INI-Berechnungen zur Verfügung stellen
		//String sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();	
		
		String sGuiZoomFactorAliasCurrent = null;
		if(bPlus){
			//0. 20180819: Holen einen um +1 erhöhten ZoomFactorAlias
			sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasNext();						
			
		}else{
			//0. 20180819: Holen einen um +1 verringerten ZoomFactorAlias
			sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasPrevious();	
		}
		ApplicationSingletonTHM.getInstance().setGuiZoomFactorAliasCurrent(sGuiZoomFactorAliasCurrent);
		
		String sGuiZoomFactorCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorCurrent();							
		objFileConfig.setVariable("GuiZoomFactorUsed", sGuiZoomFactorCurrent);
				
	}
}

package use.thm.client;

import java.awt.Frame;

import basic.zKernelUI.component.KernelJDialogExtendedZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;

/**Dialogbox "Help/About". Wird aus dem Men� des Hauptframes gestartet.
 * @author 0823
 *
 */
public class DlgAboutTHM extends KernelJDialogExtendedZZZ {

	/**
	 * @param owner
	 * @param bModal
	 * @param bSnappedToScreen
	 * @param panelCenter
	 */
	public DlgAboutTHM(IKernelZZZ objKernel, KernelJFrameCascadedZZZ frameOwner) {		
		super(objKernel, frameOwner, true, null);  //true, d.h. modal, gehtl leider nur im Konstruktor zu �bergeben, weil JDialog diesen Parameter im Konstruktor braucht und Super(...) kann keinen Code beinhalten, der auf eigene Properties etc. zugreift.
	}
	public boolean isCentered(){
		return true;
	}	
	public boolean isJComponentSnappedToScreen(){
		return true;
	}
	public boolean isButtonCancelAvailable(){
		return false;
	}
	public boolean isButtonOKAvailable(){
		return true;
	}
	public KernelJPanelCascadedZZZ getPanelButton(){
		PanelDlgAboutButtonAlternativeTHM panelButton = new PanelDlgAboutButtonAlternativeTHM(this.getKernelObject(), this, true, false);//ok-button=true, cancel-button = false		
		return panelButton;
	}
	public KernelJPanelCascadedZZZ getPanelContent(){
		PanelDlgAboutTHM panelContent = new PanelDlgAboutTHM(this.getKernelObject(), this);
		return panelContent;
	}
	public String getText4ContentDefault(){
		return "Das Panel für diese Dialogbox scheint zu fehlen, wenn Sie dies lesen können";
	}


	
	/* NICHT L�SCHEN: !!! Testweise die Methoden mit null �berschreiben. Es m�ssen nur die Default Einstellungen angezigt werden.
	public KernelJPanelCascadedZZZ getPanelButton(){			
		return null;
	}
	public KernelJPanelCascadedZZZ getPanelContent(){		
		return null;
	}
	public String getText4ContentDefault(){
		return "Das ist ein Test f�r den Default Text.(" + ReflectionZZZ.getMethodCurrentName() + ")";
	}
	*/
	
}//END Class
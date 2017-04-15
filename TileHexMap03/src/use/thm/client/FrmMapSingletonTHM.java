package use.thm.client;



import java.awt.Color;
import java.awt.Dimension;

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

import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import basic.zKernelUI.util.JFrameHelperZZZ;

public class FrmMapSingletonTHM  extends KernelJFrameCascadedZZZ implements IGhostGlassPaneFrame{
	private static FrmMapSingletonTHM dlgSingleton = null;  //muss static sein, wg. getInstance()!!!
	private GhostGlassPane glassPane; //damit etwas, das per Drag/Drop bewegt wird dorthin als Bild kopiert wird.
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @param objFrame
	 * @throws ExceptionZZZ
	 */
	private FrmMapSingletonTHM(KernelZZZ objKernel, KernelJFrameCascadedZZZ objFrame) throws ExceptionZZZ{
		super(objKernel, objFrame);
		
		//Todo: Den glassPane in die Klasse KernelJFrameCascadedZZZ �bernehmen, oder eine Unterklasse daf�r zur Verf�gung stellen.
		//Ohne diesen Glass Pane funktionieren die Ghost...Adapter nicht, z.B. der GhostMotionAdapter.
	     setGhostGlassPane(new GhostGlassPane(this));
	}
	private FrmMapSingletonTHM(){
		super(); 
	
	     //Todo: Den glassPane in die Klasse KernelJFrameCascadedZZZ �bernehmen, oder eine Unterklasse daf�r zur Verf�gung stellen.
	     //Ohne diesen Glass Pane funktionieren die Ghost...Adapter nicht, z.B. der GhostMotionAdapter.
         setGhostGlassPane(new GhostGlassPane(this));		
	}
	
	public static FrmMapSingletonTHM getInstance(){
		if(dlgSingleton==null){
			dlgSingleton = new FrmMapSingletonTHM();
		}
		return dlgSingleton;		
	}
	
	public static FrmMapSingletonTHM getInstance(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		if(dlgSingleton==null){
			dlgSingleton = new FrmMapSingletonTHM(objKernel, frameParent);
		}
		return dlgSingleton;		
	}

	
	public boolean launchCustom() throws ExceptionZZZ {
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "launch - thread: " + ". doing CUSTOM....");
		return true;
	}
	
	/* (non-Javadoc)
	 * @see basic.zKernelUI.component.KernelJFrameCascadedZZZ#getPaneContent()
	 */
	/*
	public KernelJPanelCascadedZZZ getPaneContent(){
		KernelJPanelCascadedZZZ objPanel = null;
		//objPanel = new KernelJPanelCascadedZZZ(); //BlankoPanel, wenn noch etwas um das Sechseck herum sein soll
		return objPanel;
	}*/
	
	public JComponent getPaneContent(String sAliasIn) throws ExceptionZZZ {
		JComponent objPaneReturn = null;
		String sAlias = null;
//		try {
			if(StringZZZ.isEmpty(sAliasIn)){
				sAlias = "ContentPaneSub";
			}else{
				sAlias = sAliasIn;
			} 
			
			if(sAlias.equalsIgnoreCase("ContentPaneSub")){
				objPaneReturn = (JComponent) new PanelFrmMapSingletonTHM(this.getKernelObject(), this);
				this.setContentPane(objPaneReturn);
			}else if(sAlias.equalsIgnoreCase("LayeredPane")){				
				/*BEISPIEL, nicht löschen, wie ein LayeredPane auch noch angehängt würde, oder als einzige Alternative, wenn es keine anderen ContentPanes gibt.
				objPaneReturn = (JComponent) new JLayeredPane();
				objPaneReturn.setPreferredSize(new Dimension(400, 600));
		        objPaneReturn.setBorder(BorderFactory.createTitledBorder("Test layered pane"));
				
				int iDirection = 1;
				
				JLabel label = new JLabel("Test");
				label.setBounds(340,490,30, 30);
				label.setBackground(Color.red);
				label.setOpaque(true);
				label.setVisible(true);
				objPaneReturn.add(label, JLayeredPane.DEFAULT_LAYER.intValue()-1*(iDirection));
				
				JLabel label2 = new JLabel("dr�ber");
				label2.setBounds(350,500,30, 30);
				label2.setBackground(Color.green);
				label2.setOpaque(true);
				label2.setVisible(true);
				objPaneReturn.add(label2, JLayeredPane.DEFAULT_LAYER.intValue()+1*(iDirection));
				*/
			}
			/*
		} catch (ExceptionZZZ e) {
			ReportLogZZZ.write(ReportLogZZZ.ERROR, "ExceptionZZZ: " + e.getDetailAllLast());
		}*/
		return objPaneReturn;
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
		KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance("THM", "01", "", "ZKernelConfigTileHexMap02Client.ini", (String[]) null);
		
		//Lösung: Singleton, damit man nur eine Dialogbox öffnen kann 
		FrmMapSingletonTHM frameInfo = FrmMapSingletonTHM.getInstance(objKernel, null);
				
		//---- Bereite das Reporten über Log4J vor...
		//KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frmMain.getClass().getName(), frmMain.getClass().getName());					
		KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frameInfo.getClass().getName());  //Damit ist das ein Context Provider, der die Informationen auf "Modulebene" sucht.
		ReportLogZZZ.loadKernelContext(objContext, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Start of main-frame");				
		
		//---- Bereite Hibernate und die SQLite Datenbank vor.
		//Die eigentliche Session und Configuration wird in der Klasse use.thm.client.hibernate.HibernateContextProviderTHM gemacht.
		//Hier könnten dann noch vorgelagerte Dinge gemacht werden.
		
		//---- Starte den Frame
		boolean bLaunched = frameInfo.launch(objKernel.getApplicationKey() + " - Client (Map)");
		if(bLaunched == true){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'TileHexMap', was successfull");
			
			boolean bCentered = frameInfo.centerOnParent();
			if(bCentered==true){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'TileHexMap', was successfull");					
			}else{
				ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'TileHexMap', was NOT successfull");	
			}
			
		}
		
		/*Merke: Dieser Code wird vor dem Fensterstart ausgef�hrt. Nur m�glich, weil der EventDispatcher-Code nebenl�ufig ausgef�hrt.... wird.
		//            Und das ist nur m�glich, wenn das der "Erste Frame/ der Hauptframe" der Applikation ist.
		try{			
			for(int icount = 0; icount <= 10; icount++){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "main - thread (actionPerformed): " + icount + ". doing something....");
				Thread.sleep(10);
			}
		}catch(InterruptedException ie){			
		} */
		} catch (ExceptionZZZ ez) {				
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
	}

	@Override
	public boolean setSizeDefault() throws ExceptionZZZ {
		String sPercent = this.getKernelObject().getParameter("FrameSizeInScreenPercent");
		int iPercent = Integer.parseInt(sPercent);
		JFrameHelperZZZ.setSizeInScreenPercent(this, iPercent);
		return true;
	}
	
	//#### GETTER SETTER
	public GhostGlassPane getGhostGlassPane(){
		return this.glassPane;
	}
	public void setGhostGlassPane(GhostGlassPane glassPane){
		this.glassPane = glassPane;
		this.setGlassPane(this.glassPane); //.setGlassPane ist die eigentliche Methode von JFrame		
	}
}

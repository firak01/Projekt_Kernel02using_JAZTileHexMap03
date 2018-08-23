package use.thm.client;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import custom.zKernel.LogZZZ;
import custom.zKernel.file.ini.FileIniZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.IVariantCatalogUserTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonalLayoutTHM;
import use.thm.client.component.VariantCatalogTHM;
import use.thm.client.dragDropTranslucent.GhostDropManagerHexMapPanelTHM;
import use.zBasicUI.component.UIHelper_SwingWorker4ProgramGuiZoomTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropEvent;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;
import basic.zBasicUI.thread.SwingWorker;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelActionCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelMain_NORTHTHM extends KernelJPanelCascadedZZZ{
	public static final String sBUTTON_GUI_ZOOM_MINUS = "buttonGuiZoomMinus"; //Definition des Aliaswerts als Konstante
	public static final String sBUTTON_GUI_ZOOM_PLUS = "buttonGuiZoomPlus"; //Definition des Aliaswerts als Konstante
	
	//Default Konstruktor, damit die Klasse per Refelction einfachmit newInstance erzeugt werden kann.
	public PanelMain_NORTHTHM(){		
	}	
	public PanelMain_NORTHTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, HashMap<String, Boolean>hmFlag) throws ExceptionZZZ  {
		super(objKernel, panelParent,hmFlag);
		
//		try{				
			this.setJComponentContentDraggable(true); //NICHT nur die einzelnen Labels ziehbar machen											    		    
			this.setLayout((LayoutManager) new BoxLayout( this, BoxLayout.X_AXIS ) );

			//GUI Font holen		
			Font objFontGui = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
			
		     //Nun die Buttons hinzufügen
			JButton buttonGuiMinus = new JButton(" - ");
			buttonGuiMinus.setFont(objFontGui);
			
			
			ActionGuiZoomMinusTHM actionGuiZoomMinus = new ActionGuiZoomMinusTHM(objKernel, this);
			buttonGuiMinus.addActionListener(actionGuiZoomMinus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_GUI_ZOOM_MINUS, buttonGuiMinus);
			this.add(buttonGuiMinus);
								
			JButton buttonGuiPlus = new JButton(" + ");
			buttonGuiPlus.setFont(objFontGui);
			
			
			ActionGuiZoomPlusTHM actionGuiZoomPlus = new ActionGuiZoomPlusTHM(objKernel, this);
			buttonGuiPlus.addActionListener(actionGuiZoomPlus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_GUI_ZOOM_PLUS, buttonGuiPlus);
			this.add(buttonGuiPlus);
			
			
			
			//TODO GOON 20180818: Entsprechend die Buttons positionieren
//			//Diese einfache Maske besteht aus 3 Zeilen und 4 Spalten. 
//			//Es gibt au�en einen Rand von jeweils einer Spalte/Zeile
//			//Merke: gibt man pref an, so bewirkt dies, das die Spalte beim ver�ndern der Fenstergr��e nicht angepasst wird, auch wenn grow dahinter steht.
//			
//			FormLayout layout = new FormLayout(
//					"5dlu, right:pref:grow(0.5), 5dlu:grow(0.5), left:50dlu:grow(0.5), 5dlu, center:pref:grow(0.5),5dlu",         //erster Parameter sind die Spalten/Columns (hier: vier), als Komma getrennte Eint�ge.
//					"5dlu, center:10dlu, 5dlu"); 				 //zweiter Parameter sind die Zeilen/Rows (hier:  drei), Merke: Wenn eine feste L�nge k�rzer ist als der Inhalt, dann wird der Inaht als "..." dargestellt
//			this.setLayout(layout);              //!!! wichtig: Das layout muss dem Panel zugwiesen werden BEVOR mit constraints die Componenten positioniert werden.
//			CellConstraints cc = new CellConstraints();
//			
//			JLabel label = new JLabel("Server IP:");
//			this.add(label, cc.xy(2,2));
//				
//			JTextField textfieldIPExternal = new JTextField("Enter or refresh", 20);
//			textfieldIPExternal.setHorizontalAlignment(JTextField.LEFT);
//			textfieldIPExternal.setCaretPosition(0);
//			//Dimension dim = new Dimension(10, 15);
//			//textfield.setPreferredSize(dim);
//			this.add(textfieldIPExternal, cc.xy(4,2));
//			
//			// Dieses Feld soll einer Aktion in der Buttonleiste zur Verf�gung stehen.
//			//Als CascadedPanelZZZ, wird diese Componente mit einem Alias versehen und in eine HashMap gepackt.
//			//Der Inhalt des Textfelds soll dann beim O.K. Button in die ini-Datei gepackt werden.
//			this.setComponent("text1", textfieldIPExternal);      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//			
//			
//			JButton buttonReadIPExternal = new JButton("Refresh server ip from the web.");
//			ActionIPRefreshVIA actionIPRefresh = new ActionIPRefreshVIA(objKernel, this);
//			buttonReadIPExternal.addActionListener(actionIPRefresh);
//			
//			this.add(buttonReadIPExternal, cc.xy(6,2));
//			
//			
//			/* Das funktioniert nicht. Funktionalit�t des JGoodies-Framework. Warum ???
//			PanelBuilder builder = new PanelBuilder(layout);
//			builder.setDefaultDialogBorder();
//			builder.addLabel("Externe IP Adresse des Servers");
//			JTextField textfield = new JTextField("noch automatisch zu f�llen");
//			builder.add(textfield, cc.xy(3,2));
//			*/	
	}		
			
			//20180819: VARIANTE MIT SWING WORKER
			//######################################
	//MINUS BUTTON - Innere Klassen, welche eine Action behandelt	
	class ActionGuiZoomMinusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
		public ActionGuiZoomMinusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
//			try {
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'GUI Zoom: Minus'");
												
			String[] saFlag = null;			
			KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																	
			SwingWorker4ProgramGuiZoomMinus worker = new SwingWorker4ProgramGuiZoomMinus(objKernel, panelParent, saFlag);
			worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
			

		/*} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}	*/
			
			return true;
		}

		public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
			return true;
		}

		public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
		}			 							
		
		class SwingWorker4ProgramGuiZoomMinus extends SwingWorker implements IObjectZZZ, IKernelUserZZZ{
			private KernelZZZ objKernel;
			private LogZZZ objLog;
			private KernelJPanelCascadedZZZ panel;
			private String[] saFlag4Program;
			
			private String sText2Update;    //Der Wert, der ins Label geschreiben werden soll. Jier als Variable, damit die intene Runner-Klasse darauf zugreifen kann.
														// Auch: Dieser Wert wird aus dem Web ausgelesen und danach in das Label des Panels geschrieben.
			
						
			protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
			
			public SwingWorker4ProgramGuiZoomMinus(KernelZZZ objKernel, KernelJPanelCascadedZZZ panel, String[] saFlag4Program){
				super();
				this.objKernel = objKernel;
				this.objLog = objKernel.getLogObject();
				this.panel = panel;
				this.saFlag4Program = saFlag4Program;					
			}
			
			//#### abstracte - Method aus SwingWorker
			public Object construct() {
				try{
				
					UIHelper_SwingWorker4ProgramGuiZoomTHM.constructMinus();
					System.out.println("Updating Panel ...");
					KernelJPanelCascadedZZZ objPanelParent = this.panel.getPanelParent();
					updatePanelAll(objPanelParent); //20180819: Damit das klappt muss eine Komponentenliste über alle Panels zusammengesucht werden....						
					//updatePanelCurrent(this.panel); //Findet nur Komponenten im aktuellen Panel
						
					//Merke: Das wurde im VIA Projekt so genutzt, zum Auslesen einer externen Webseite
					//2. IP Auslesen von der Webseite
					//ProgramIPContentVIA objProg =new ProgramIPContentVIA(objKernel, this.panel, this.saFlag4Program);					
					//String sIp = objProg.getIpExternal();
						
					//3. Diesen Wert wieder ins Label schreiben.
					//updateTextField(sIp);
					
				}catch(ExceptionZZZ ez){
					System.out.println(ez.getDetailAllLast());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());					
				}
				return "all done";
			}
			
			/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
			 *  Entspricht auch ProgramIPContext.updateLabel(..)
			* @param stext
			* 
			* lindhaueradmin; 17.01.2007 12:09:17
			 */
			public void updateTextField(String stext){
				this.sText2Update = stext;
				
//				Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
				Runnable runnerUpdateLabel= new Runnable(){

					public void run(){
//						In das Textfeld den gefundenen Wert eintragen, der Wert ist ganz oben als private Variable deklariert			
						ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Writing '" + sText2Update + "' to the JTextField 'text1");				
						JTextField textField = (JTextField) panel.getComponent("text1");					
						textField.setText(sText2Update);
						textField.setCaretPosition(0);   //Das soll bewirken, dass der Anfang jedes neu eingegebenen Textes sichtbar ist.  
					}
				};
				
				SwingUtilities.invokeLater(runnerUpdateLabel);	
				
//				In das Textfeld eintragen, das etwas passiert.								
				//JTextField textField = (JTextField) panelParent.getComponent("text1");					
				//textField.setText("Lese aktuellen Wert .....");
				
			}
			
			/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
			 *  
			* @param stext
			* 					
			 */
			public void updatePanelCurrent(KernelJPanelCascadedZZZ panel2update){
				this.panel = panel2update;
				
				
				
//				Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
				Runnable runnerUpdatePanel= new Runnable(){

					public void run(){
						try {
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Updating Gui");	
							ApplicationSingletonTHM.getInstance().setGuiFontCurrent(null);
							
														
//								In das Textfeld den gefundenen Wert eintragen, der Wert ist ganz oben als private Variable deklariert
							Font font = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
							JButton buttonTest = (JButton) panel.getComponent(PanelMain_NORTHTHM.sBUTTON_GUI_ZOOM_PLUS);	//Der wird so nur im aktuellen Panel gefunden, sonst nicht				
							buttonTest.setFont(font);
							buttonTest.repaint();
							
							panel.repaint();	
						} catch (ExceptionZZZ e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				
				SwingUtilities.invokeLater(runnerUpdatePanel);	
				
//				In das Textfeld eintragen, das etwas passiert.								
				//JTextField textField = (JTextField) panelParent.getComponent("text1");					
				//textField.setText("Lese aktuellen Wert .....");
				
			}

			
			/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
			 *  
			* @param stext
			* 					
			 */
			public void updatePanelAll(KernelJPanelCascadedZZZ panel2updateStart){
				this.panel = panel2updateStart;
				
				
				
//				Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
				Runnable runnerUpdatePanel= new Runnable(){

					public void run(){
						try {							
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Updating Gui Font");	
							ApplicationSingletonTHM.getInstance().setGuiFontCurrent(null);
																					
							Font font = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
							panel.updateComponentFontAll(font);																					
							panel.repaint();	
							
							//PROBLEM: Nachbarpanels updaten und neu zeichnen
							PanelMain_WESTTHM panelCatalog = (PanelMain_WESTTHM) panel.searchPanelSub("WEST");
							HashMapMultiZZZ hmCatalog = panelCatalog.getVariantCatalog().getMapCatalog();
							 for (Iterator<String> iteratorVariantTypes = hmCatalog.getOuterKeySetIterator(); iteratorVariantTypes.hasNext();) {
						    	 String sVariantType = (String) iteratorVariantTypes.next();
						    	 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariantType + "'");

						    	 for (Iterator<String> iteratorVariant = hmCatalog.getInnerKeySetIterator(sVariantType); iteratorVariant.hasNext();) {
						    		 String sVariant = (String) iteratorVariant.next();
						    		 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariant + "'");

						    		 Box boxTemp = (Box) hmCatalog.get(sVariantType, sVariant);//z.B.: Box boxTemp = (Box) hmCatalog.get("ARMY","new_sale");		    		 		    		 		     
								     //this.add(BorderLayout.CENTER, boxTemp);				    
						    		 panel.add(boxTemp);
						    	 }		    	 		    	
						     }
							panelCatalog.repaint();
							
						} catch (ExceptionZZZ e) {
							e.printStackTrace();
						}
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

		}

		@Override
		public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
			// TODO Auto-generated method stub
			
		} //End Class MySwingWorker

}//End class ...KErnelActionCascaded....
//##############################################
	
//			#######################################
			//PLUS BUTTON - Innere Klassen, welche eine Action behandelt	
			class ActionGuiZoomPlusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
				public ActionGuiZoomPlusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
					super(objKernel, panelParent);			
				}
				
				public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
//					try {
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'IP-Refresh'");
														
					String[] saFlag = null;			
					KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																			
					SwingWorker4ProgramGuiZoomPlus worker = new SwingWorker4ProgramGuiZoomPlus(objKernel, panelParent, saFlag);
					worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
					

				/*} catch (ExceptionZZZ ez) {				
					this.getLogObject().WriteLineDate(ez.getDetailAllLast());
					ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
				}	*/
					
					return true;
				}

				public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
					return true;
				}

				public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
				}			 							
				
				class SwingWorker4ProgramGuiZoomPlus extends SwingWorker implements IObjectZZZ, IKernelUserZZZ{
					private KernelZZZ objKernel;
					private LogZZZ objLog;
					private KernelJPanelCascadedZZZ panel;
					private String[] saFlag4Program;
					
					private String sText2Update;    //Der Wert, der ins Label geschreiben werden soll. Jier als Variable, damit die intene Runner-Klasse darauf zugreifen kann.
																// Auch: Dieser Wert wird aus dem Web ausgelesen und danach in das Label des Panels geschrieben.
					
								
					protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
					
					public SwingWorker4ProgramGuiZoomPlus(KernelZZZ objKernel, KernelJPanelCascadedZZZ panel, String[] saFlag4Program){
						super();
						this.objKernel = objKernel;
						this.objLog = objKernel.getLogObject();
						this.panel = panel;
						this.saFlag4Program = saFlag4Program;					
					}
					
					//#### abstracte - Method aus SwingWorker
					public Object construct() {
						try{
							UIHelper_SwingWorker4ProgramGuiZoomTHM.constructPlus();
							System.out.println("Updating Panel ...");
							KernelJPanelCascadedZZZ objPanelParent = this.panel.getPanelParent();
							updatePanelAll(objPanelParent); //20180819: Damit das klappt muss eine Komponentenliste über alle Panels zusammengesucht werden....						
							//updatePanelCurrent(this.panel); //Findet nur Komponenten im aktuellen Panel
													
						}catch(ExceptionZZZ ez){
							System.out.println(ez.getDetailAllLast());
							ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());					
						}
						return "all done";
					}
					
				
					
				

					
					/**Aus dem Worker-Thread heraus wird ein Thread gestartet (der sich in die EventQueue von Swing einreiht.)
					 *  
					* @param stext
					* 					
					 */
					public void updatePanelAll(KernelJPanelCascadedZZZ panel2updateStart){
						this.panel = panel2updateStart;

//						Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
						Runnable runnerUpdatePanel= new Runnable(){

							public void run(){
								try {
									ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Updating Gui Font");	
									ApplicationSingletonTHM.getInstance().setGuiFontCurrent(null);
																							
									Font font = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
									panel.updateComponentFontAll(font);								
									panel.repaint();	
									
									//PROBLEM: Nachbarpanels updaten und neu zeichnen
									PanelMain_WESTTHM panelCatalog = (PanelMain_WESTTHM) panel.searchPanelSub("WEST");
									HashMapMultiZZZ hmCatalog = panelCatalog.getVariantCatalog().getMapCatalog();
									 for (Iterator<String> iteratorVariantTypes = hmCatalog.getOuterKeySetIterator(); iteratorVariantTypes.hasNext();) {
								    	 String sVariantType = (String) iteratorVariantTypes.next();
								    	 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariantType + "'");

								    	 for (Iterator<String> iteratorVariant = hmCatalog.getInnerKeySetIterator(sVariantType); iteratorVariant.hasNext();) {
								    		 String sVariant = (String) iteratorVariant.next();
								    		 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariant + "'");

								    		 Box boxTemp = (Box) hmCatalog.get(sVariantType, sVariant);//z.B.: Box boxTemp = (Box) hmCatalog.get("ARMY","new_sale");		    		 		    		 		     
										     //this.add(BorderLayout.CENTER, boxTemp);				    
								    		 panel.add(boxTemp);
								    	 }		    	 		    	
								     }
									
									
									panelCatalog.repaint();
									
								} catch (ExceptionZZZ e) {
									e.printStackTrace();
								}
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

				}

				@Override
				public void actionPerformCustomOnError(ActionEvent ae, ExceptionZZZ ez) {
					// TODO Auto-generated method stub
					
				} //End Class MySwingWorker

		}//End class ...KErnelActionCascaded....
		//##############################################
			
////#######################################################################
//		} catch (ExceptionZZZ ez) {				
//			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
//			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
//		}

	
	//#### Interface Methoden #####################
		//.............
}

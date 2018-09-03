package use.thm.client;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import custom.zKernel.LogZZZ;
import custom.zKernel.file.ini.FileIniZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.IVariantCatalogUserTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonaMapLayoutTHM;
import use.thm.client.component.VariantCatalogTHM;
import use.thm.client.dragDropTranslucent.GhostDropManagerHexMapPanelTHM;
import use.zBasicUI.component.UIHelper_SwingWorker4ProgramGuiZoomTHM;
import use.zBasicUI.component.UIHelper_SwingWorker4ProgramMapZoomTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
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
	public static final String sBUTTON_MAP_ZOOM_MINUS = "buttonMapZoomMinus"; //Definition des Aliaswerts als Konstante
	public static final String sBUTTON_MAP_ZOOM_PLUS = "buttonMapZoomPlus"; //Definition des Aliaswerts als Konstante
	
	
	//Default Konstruktor, damit die Klasse per Refelction einfachmit newInstance erzeugt werden kann.
	public PanelMain_NORTHTHM(){		
	}	
	public PanelMain_NORTHTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, HashMap<String, Boolean>hmFlag) throws ExceptionZZZ  {
		super(objKernel, panelParent,hmFlag);
		
//		try{				
			this.setJComponentContentDraggable(true); //NICHT nur die einzelnen Labels ziehbar machen											    		    
			this.setLayout((LayoutManager) new BoxLayout( this, BoxLayout.X_AXIS ) );

			//Die Buttons in ener Box - Hinzufügen. Damit man mit "glue Komponente" arbeiten kann
			Box b = Box.createHorizontalBox();
			
			//GUI Font holen		
			Font objFontGui = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
			
		     //Nun die Buttons hinzufügen
			//A) Button für GUI +++++++++++++++++++
			JButton buttonGuiMinus = new JButton(" - ");
			buttonGuiMinus.setFont(objFontGui);
			
			
			ActionGuiZoomMinusTHM actionGuiZoomMinus = new ActionGuiZoomMinusTHM(objKernel, this);
			buttonGuiMinus.addActionListener(actionGuiZoomMinus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_GUI_ZOOM_MINUS, buttonGuiMinus);
			//this.add(buttonGuiMinus);
			b.add(buttonGuiMinus);
		
			//++++++++++++++++++++
			JButton buttonGuiPlus = new JButton(" + ");
			buttonGuiPlus.setFont(objFontGui);
			
			
			ActionGuiZoomPlusTHM actionGuiZoomPlus = new ActionGuiZoomPlusTHM(objKernel, this);
			buttonGuiPlus.addActionListener(actionGuiZoomPlus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_GUI_ZOOM_PLUS, buttonGuiPlus);
			//this.add(buttonGuiPlus);
			b.add(buttonGuiPlus);
			
			//+++++++++++++++++++++++++++++
			
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
			
			
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//Alternativer Ansatz, der eine dynamische Positionierung der Buttons erlauben soll mit  "glue"
			//http://ecomputernotes.com/java/swing/glue-in-java
			//http://www.java2s.com/Tutorial/Java/0240__Swing/Showshowtorightalignamenuinthemenubarusingagluecomponent.htm
			b.add(Box.createGlue());
						
			//B) Button für Karte +++++++++++++++++++
			JButton buttonMapMinus = new JButton(" - ");
			buttonMapMinus.setFont(objFontGui); //Also: Die ButtonGröße wird durch das GUI definiert. Auch wenn diese Buttons die Karte zoomen....

			ActionMapZoomMinusTHM actionMapZoomMinus = new ActionMapZoomMinusTHM(objKernel, this);
			buttonMapMinus.addActionListener(actionMapZoomMinus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_MAP_ZOOM_MINUS, buttonMapMinus);
			//this.add(buttonMapMinus);
			b.add(buttonMapMinus);
		
			//++++++++++++++++++++
			JButton buttonMapPlus = new JButton(" + ");
			buttonMapPlus.setFont(objFontGui);
						
			ActionMapZoomPlusTHM actionMapZoomPlus = new ActionMapZoomPlusTHM(objKernel, this);
			buttonMapPlus.addActionListener(actionMapZoomPlus);		
			this.setComponent(PanelMain_NORTHTHM.sBUTTON_MAP_ZOOM_PLUS, buttonMapPlus);
			//this.add(buttonMapPlus);
			b.add(buttonMapPlus);
			
			//+++++++++++++++++++++++++++++
			this.add(b);
	}		
			
			//20180819: VARIANTE MIT SWING WORKER
			//######################################
	//MINUS BUTTON GUI - Innere Klassen, welche eine Action behandelt	
	class ActionGuiZoomMinusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
		public ActionGuiZoomMinusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
			super(objKernel, panelParent);			
		}
		
		public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'GUI Zoom: Minus'");
												
			String[] saFlag = null;			
			KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																	
			SwingWorker4ProgramGuiZoomMinus worker = new SwingWorker4ProgramGuiZoomMinus(objKernel, panelParent, saFlag);
			worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
		
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
				
//				Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
				Runnable runnerUpdatePanel= new Runnable(){

					public void run(){
						try {							
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - CATALOG IN NEIGHBORPANEL");
							ApplicationSingletonTHM.getInstance().setGuiFontCurrent(null);
																					
							Font font = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
							panel.updateComponentFontAll(font);	
							panel.validate();
							panel.repaint();	
							
																											
							//PROBLEM: Nachbarpanels updaten und neu zeichnen
							PanelMain_WESTTHM panelCatalog = (PanelMain_WESTTHM) panel.searchPanelSub("WEST");
							
							//Zuerst leeren
							panelCatalog.removeAll();

							VariantCatalogTHM objCatalog = panelCatalog.getVariantCatalog();
						    objCatalog.fillCatalog(true);
						    panelCatalog.setVariantCatalog(objCatalog);
						    
						    //+++++++++++++++++++++++++++++++++++++++
							panelCatalog.setLayout((LayoutManager) new BoxLayout( panelCatalog, BoxLayout.Y_AXIS ) );
						     									
							HashMapMultiZZZ hmCatalog = panelCatalog.getVariantCatalog().getMapCatalog();
							 for (Iterator<String> iteratorVariantTypes = hmCatalog.getOuterKeySetIterator(); iteratorVariantTypes.hasNext();) {
						    	 String sVariantType = (String) iteratorVariantTypes.next();
						    	 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariantType + "'");

						    	 for (Iterator<String> iteratorVariant = hmCatalog.getInnerKeySetIterator(sVariantType); iteratorVariant.hasNext();) {
						    		 String sVariant = (String) iteratorVariant.next();
						    		 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariant + "'");

						    		 Box boxTemp = (Box) hmCatalog.get(sVariantType, sVariant);//z.B.: Box boxTemp = (Box) hmCatalog.get("ARMY","new_sale");		    		 		    		 		     
								     //this.add(BorderLayout.CENTER, boxTemp);				    
						    		 panelCatalog.add(boxTemp);
						    	 }		    	 		    	
						     }
							 
							 //Merke 20180828: Erst einmal daran Gescheitert. Die Idee war: "Statt des Entfernen und Neuanlegen in den Box-Komponenten eine paint-Methode verwenden."
							 panelCatalog.validate();
							panelCatalog.repaint();
							
							//+++++++++++++++++++++++
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - COMPONENT (d.h. Buttons des aktuellen Panels)");														
							panel.updateComponentFontAll(font);
							panel.validate();
							panel.repaint();	

							//+++++++++++++++++++++++
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - MENU (d.h. Menu des Parent Frames)");
							panel.getFrameParent().updateMenuBarFontAll(font);
								
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
			//PLUS BUTTON -GUI Innere Klassen, welche eine Action behandelt	
			class ActionGuiZoomPlusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
				public ActionGuiZoomPlusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
					super(objKernel, panelParent);			
				}
				
				public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'IP-Refresh'");
														
					String[] saFlag = null;			
					KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																			
					SwingWorker4ProgramGuiZoomPlus worker = new SwingWorker4ProgramGuiZoomPlus(objKernel, panelParent, saFlag);
					worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
	
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
									ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - CATALOG IN NEIGHBORPANEL");	
									ApplicationSingletonTHM.getInstance().setGuiFontCurrent(null);
																							
									Font font = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
									panel.updateComponentFontAll(font);								
									panel.repaint();	
																									
									//PROBLEM: Nachbarpanels updaten und neu zeichnen
									PanelMain_WESTTHM panelCatalog = (PanelMain_WESTTHM) panel.searchPanelSub("WEST");
									
									//Zuerst leeren
									panelCatalog.removeAll();
									
									VariantCatalogTHM objCatalog = panelCatalog.getVariantCatalog();
								    objCatalog.fillCatalog(true);
								    panelCatalog.setVariantCatalog(objCatalog);
								    
								    //+++++++++++++++++++++++++++++++++++++++
									panelCatalog.setLayout((LayoutManager) new BoxLayout( panelCatalog, BoxLayout.Y_AXIS ) );
								     									
									HashMapMultiZZZ hmCatalog = panelCatalog.getVariantCatalog().getMapCatalog();
									 for (Iterator<String> iteratorVariantTypes = hmCatalog.getOuterKeySetIterator(); iteratorVariantTypes.hasNext();) {
								    	 String sVariantType = (String) iteratorVariantTypes.next();
								    	 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariantType + "'");

								    	 for (Iterator<String> iteratorVariant = hmCatalog.getInnerKeySetIterator(sVariantType); iteratorVariant.hasNext();) {
								    		 String sVariant = (String) iteratorVariant.next();
								    		 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariant + "'");

								    		 Box boxTemp = (Box) hmCatalog.get(sVariantType, sVariant);//z.B.: Box boxTemp = (Box) hmCatalog.get("ARMY","new_sale");		    		 		    		 		     
										     //this.add(BorderLayout.CENTER, boxTemp);				    
								    		 panelCatalog.add(boxTemp);
								    	 }		    	 		    	
								     }
									
									 //Merke 20180828: Erst einmal daran Gescheitert. Die Idee war: "Statt des Entfernen und Neuanlegen in den Box-Komponenten eine paint-Methode verwenden."
									panelCatalog.repaint();
									
									//+++++++++++++++++++++++
									ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - COMPONENT (d.h. Buttons des aktuellen Panels)");
									panel.updateComponentFontAll(font);																					
									panel.repaint();	

									//+++++++++++++++++++++++
									ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating Gui Font - MENU (d.h. Menu des Parent Frames)");
									panel.getFrameParent().updateMenuBarFontAll(font);
									
								
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
			
			//MINUS BUTTON MAP - Innere Klassen, welche eine Action behandelt	
			class ActionMapZoomMinusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
				public ActionMapZoomMinusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
					super(objKernel, panelParent);			
				}
				
				public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'MAP Zoom: Minus'");
														
					String[] saFlag = null;			
					KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																			
					SwingWorker4ProgramMapZoomMinus worker = new SwingWorker4ProgramMapZoomMinus(objKernel, panelParent, saFlag);
					worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
					
					return true;
				}

				public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
					return true;
				}

				public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
				}			 							
				
				class SwingWorker4ProgramMapZoomMinus extends SwingWorker implements IObjectZZZ, IKernelUserZZZ{
					private KernelZZZ objKernel;
					private LogZZZ objLog;
					private KernelJPanelCascadedZZZ panel;
					private String[] saFlag4Program;
					
					private String sText2Update;    //Der Wert, der ins Label geschreiben werden soll. Jier als Variable, damit die intene Runner-Klasse darauf zugreifen kann.
																// Auch: Dieser Wert wird aus dem Web ausgelesen und danach in das Label des Panels geschrieben.
					
								
					protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
					
					public SwingWorker4ProgramMapZoomMinus(KernelZZZ objKernel, KernelJPanelCascadedZZZ panel, String[] saFlag4Program){
						super();
						this.objKernel = objKernel;
						this.objLog = objKernel.getLogObject();
						this.panel = panel;
						this.saFlag4Program = saFlag4Program;					
					}
					
					//#### abstracte - Method aus SwingWorker
					public Object construct() {
						try{
						
							UIHelper_SwingWorker4ProgramMapZoomTHM.constructMinus();
							System.out.println("Updating Panel ...");
							KernelJPanelCascadedZZZ objPanelParent = this.panel.getPanelParent();
							updatePanelMap(objPanelParent); //20180819: Damit das klappt muss eine Komponentenliste über alle Panels zusammengesucht werden....						
							
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
					public void updatePanelMap(KernelJPanelCascadedZZZ panel2updateStart){
						this.panel = panel2updateStart;

//						Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
						Runnable runnerUpdatePanel= new Runnable(){

							public void run(){
								try {							
									ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating MAP IN NEIGHBORPANEL");
									ApplicationSingletonTHM.getInstance().setHexFieldSideLengthCurrent(0); //20180901: Damit sich auch die Größe der Sechsecke ändert. Deren Seitenlänge aus der ini-Formel neu Berechnen lassen.
									
									//PROBLEM: Nachbarpanels updaten und neu zeichnen
									PanelMain_CENTERTHM panelMap = (PanelMain_CENTERTHM) panel.searchPanelSub("CENTER");		
									panelMap.validate();
									panelMap.repaint();
																	
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
			
//					#######################################
					//PLUS BUTTON - MAP Innere Klassen, welche eine Action behandelt	
					class ActionMapZoomPlusTHM extends  KernelActionCascadedZZZ{ //KernelUseObjectZZZ implements ActionListener{						
						public ActionMapZoomPlusTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent){
							super(objKernel, panelParent);			
						}
						
						public boolean actionPerformCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
							ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: 'IP-Refresh'");
																
							String[] saFlag = null;			
							KernelJPanelCascadedZZZ panelParent = (KernelJPanelCascadedZZZ) this.getPanelParent();
																					
							SwingWorker4ProgramMapZoomPlus worker = new SwingWorker4ProgramMapZoomPlus(objKernel, panelParent, saFlag);
							worker.start();  //Merke: Das Setzen des Label Felds geschieht durch einen extra Thread, der mit SwingUtitlities.invokeLater(runnable) gestartet wird.
							
							return true;
						}

						public boolean actionPerformQueryCustom(ActionEvent ae) throws ExceptionZZZ {
							return true;
						}

						public void actionPerformPostCustom(ActionEvent ae, boolean bQueryResult) throws ExceptionZZZ {
						}			 							
						
						class SwingWorker4ProgramMapZoomPlus extends SwingWorker implements IObjectZZZ, IKernelUserZZZ{
							private KernelZZZ objKernel;
							private LogZZZ objLog;
							private KernelJPanelCascadedZZZ panel;
							private String[] saFlag4Program;
							
							private String sText2Update;    //Der Wert, der ins Label geschreiben werden soll. Jier als Variable, damit die intene Runner-Klasse darauf zugreifen kann.
																		// Auch: Dieser Wert wird aus dem Web ausgelesen und danach in das Label des Panels geschrieben.
							
										
							protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
							
							public SwingWorker4ProgramMapZoomPlus(KernelZZZ objKernel, KernelJPanelCascadedZZZ panel, String[] saFlag4Program){
								super();
								this.objKernel = objKernel;
								this.objLog = objKernel.getLogObject();
								this.panel = panel;
								this.saFlag4Program = saFlag4Program;					
							}
							
							//#### abstracte - Method aus SwingWorker
							public Object construct() {
								try{
									UIHelper_SwingWorker4ProgramMapZoomTHM.constructPlus();
									System.out.println("Updating Panel ...");
									KernelJPanelCascadedZZZ objPanelParent = this.panel.getPanelParent();
									updatePanelMap(objPanelParent); //20180819: Damit das klappt muss eine Komponentenliste über alle Panels zusammengesucht werden....						
															
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
							public void updatePanelMap(KernelJPanelCascadedZZZ panel2updateStart){
								this.panel = panel2updateStart;

//								Das Schreiben des Ergebnisses wieder an den EventDispatcher thread �bergeben
								Runnable runnerUpdatePanel= new Runnable(){

									public void run(){
										try {
											ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Updating MAP IN NEIGHBORPANEL");
											ApplicationSingletonTHM.getInstance().setHexFieldSideLengthCurrent(0); //20180901: Damit sich auch die Größe der Sechsecke ändert. Deren Seitenlänge aus der ini-Formel neu Berechnen lassen.
											
											//PROBLEM: Nachbarpanels updaten und neu zeichnen
											PanelMain_CENTERTHM panelMap = (PanelMain_CENTERTHM) panel.searchPanelSub("CENTER");		
											panelMap.validate();
											panelMap.repaint();
																													
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

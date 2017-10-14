package use.thm.client.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.IBackendPersistenceUser4UiZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import use.thm.IMapPositionableTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.client.handler.TileMouseMotionHandlerTHM;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.dto.TileDtoFactory;

public class TileTHM extends JPanel implements IMapPositionableTHM, IBackendPersistenceUser4UiZZZ {
	private String sUniquename; //Ein eindeutige Bezeichnung. Über diese wird die UI Komponente mit der BackendPersistence-Entsprechung verbunden.
	private String sAliasX; //Die Koordinaten auf der Karte
	private String sAliasY;
	private int iHexSideLength=0; //= RadiusOuter
	private int iTileSideLength=0;
	
	private KernelJPanelCascadedZZZ panelMap;
	private  TileMouseMotionHandlerTHM objTileMouseMotionHandler;
	
	private boolean bDragModeStarted = false; //Hiermit erkennt man, ob �ber der Componente eine Maustaste "einmal" gedr�ckt worden ist.
	
	private GenericDTO<ITileDtoAttribute>objDto = null;
	
	/** Der Spielstein
	* lindhaueradmin; 03.10.2008 10:54:37
	 * @param panelMap
	 * @param objEventBroker, Der EventBroker. Beim Bewegen des Spielsteins (Drag) benachrichtigt er alle an ihn angemeldeten Komponenten (z.B. die HexZellen) über das Betreten und Verlassen einer Zelle mit der Maus. 
	 * @param sAliasX
	 * @param sAliasY
	 * @param iHexSideLength
	 */
//	public TileTHM(KernelJPanelCascadedZZZ panelMap,TileMoveEventBrokerTHM objEventBroker, String sUniquename, String sAliasX, String sAliasY, int iHexSideLength){
//		super();
//		
//
//		try {
//			this.setUniquename(sUniquename);
//			
//			this.setMapX(sAliasX);
//			this.setMapY(sAliasY);
//			this.panelMap = panelMap;
//			this.setHexSideLength(iHexSideLength);
//			
//			//Klasse, die alle Maus Events vereint
//			TileMouseMotionHandlerTHM objMotionHandler = new TileMouseMotionHandlerTHM(this, objEventBroker);
//			this.setMouseMotionHandler(objMotionHandler);
//			
//			//Für das Anclicken des Spielsteins gilt:
//			//- Doppelclick öffnet Dialog
//			//Jetzt alles in einer Klasse this.addMouseListener(new TileMouseHandlerTHM(this));
//			this.addMouseListener(objMotionHandler);
//			this.addMouseMotionListener(objMotionHandler);
////			.addFocusListener()
//		} catch (Exception e) { 
//			// FGL: Anders als im Buch, werden hier die Klassen nicht als Bestandteil der anderen Klassen definiert, sondern müssen über Properties kommunizieren.
//			e.printStackTrace();
//		}
//		
//		
//		this.setBackground(Color.green);
//		this.setForeground(Color.green);
//				
//		//Dimension ausrechnen anhand der Seitenlänge des Sechsecks !!!!
//		//Merke: Der Umkreisradius des Sechsecks entspricht der Seitenlänge (Variable a)
//		//Aber: Wenn der Spielstein innerhalb des Sechsecks bleiben soll, ist der Inkreisradius interessanter.
//		//      Inkreisradius = a * ( (Wurzel aus 3) / 2 )
//		//Dimension dim = new Dimension(30,30);
//		int iTileSideLength = this.getTileSideLength();
//		Dimension dim = new Dimension(iTileSideLength, iTileSideLength);
//		
//		//Bounds ausrechnen anhand der Seitenlänge des Sechsecks !!!!
//		//this.setBounds(30, 30, 30, 30); //Ziel: Es soll nicht in der linken oberen Ecke erscheinen ! //ABER: Es soll noch eine Layout Manger f�r die Zelle geben, der dann automatisch positioniert
//		this.setBounds(iTileSideLength, iTileSideLength,iTileSideLength, iTileSideLength);
//		this.setPreferredSize(dim);
//	}
	
	
	/** Der Spielstein
	* lindhaueradmin; 03.10.2008 10:54:37
	* 
	* 
	 * @param panelMap
	 * @param objEventBroker, Der EventBroker. Beim Bewegen des Spielsteins (Drag) benachrichtigt er alle an ihn angemeldeten Komponenten (z.B. die HexZellen) über das Betreten und Verlassen einer Zelle mit der Maus. 
	 * @param sAliasX
	 * @param sAliasY
	 * @param iHexSideLength
	 */
	public TileTHM(KernelJPanelCascadedZZZ panelMap,TileMoveEventBrokerTHM objEventBroker, GenericDTO<ITileDtoAttribute> objDto, String sAliasX, String sAliasY, int iHexSideLength){
		super();
		

		try {			
			//this.setUniquename(sUniquename);
			this.setDto(objDto);
			
			this.setMapX(sAliasX);
			this.setMapY(sAliasY);
			this.panelMap = panelMap;
			this.setHexSideLength(iHexSideLength);
			
			//Klasse, die alle Maus Events vereint
			TileMouseMotionHandlerTHM objMotionHandler = new TileMouseMotionHandlerTHM(this, objEventBroker);
			this.setMouseMotionHandler(objMotionHandler);
			
			//Für das Anclicken des Spielsteins gilt:
			//- Doppelclick öffnet Dialog
			//Jetzt alles in einer Klasse this.addMouseListener(new TileMouseHandlerTHM(this));
			this.addMouseListener(objMotionHandler);
			this.addMouseMotionListener(objMotionHandler);
//			.addFocusListener()
		} catch (Exception e) { 
			// FGL: Anders als im Buch, werden hier die Klassen nicht als Bestandteil der anderen Klassen definiert, sondern müssen über Properties kommunizieren.
			e.printStackTrace();
		}
		
		
		this.setBackground(Color.green);
		this.setForeground(Color.green);
				
		//Dimension ausrechnen anhand der Seitenlänge des Sechsecks !!!!
		//Merke: Der Umkreisradius des Sechsecks entspricht der Seitenlänge (Variable a)
		//Aber: Wenn der Spielstein innerhalb des Sechsecks bleiben soll, ist der Inkreisradius interessanter.
		//      Inkreisradius = a * ( (Wurzel aus 3) / 2 )
		//Dimension dim = new Dimension(30,30);
		int iTileSideLength = this.getTileSideLength();
		Dimension dim = new Dimension(iTileSideLength, iTileSideLength);
		
		//Bounds ausrechnen anhand der Seitenlänge des Sechsecks !!!!
		//this.setBounds(30, 30, 30, 30); //Ziel: Es soll nicht in der linken oberen Ecke erscheinen ! //ABER: Es soll noch eine Layout Manger f�r die Zelle geben, der dann automatisch positioniert
		this.setBounds(iTileSideLength, iTileSideLength,iTileSideLength, iTileSideLength);
		this.setPreferredSize(dim);
	}

	public void paintComponent(Graphics g){
		//super.paintComponent(g);

		int iTileSideLength = this.getTileSideLength();	
		g.setColor(Color.red);
		//g.fillRect(0,0, 30,30);
		g.fillRect(0,0, iTileSideLength,iTileSideLength);
		g.setColor(Color.green);
		//g.drawString("test",0,15);
		g.drawString("test",0,(int)(iTileSideLength/2));
		setOpaque(false);
	}
	
	public void setDragModeStarted(boolean bStarted){
		this.bDragModeStarted = bStarted;
		if(bStarted==true){  //Merke: im MouseMove - Event des MouseMotionHandlers wird der Cursor zum "isMovable" Cursor.
			Cursor objCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);		
			this.setCursor(objCursor);  //Den Cursor wieder zur�cksetzen
		}else{
			Cursor objCursor = new Cursor(Cursor.DEFAULT_CURSOR);		
			this.setCursor(objCursor);  //Den Cursor wieder zur�cksetzen
		}
	}
	public boolean isDragModeStarted(){
		return this.bDragModeStarted;
	}
	
	//###### Interface
	public String getMapX(){
		return this.sAliasX;
	}
	public void setMapX(String sAliasX){
		this.sAliasX = sAliasX;
	}
	public String getMapY(){
		return this.sAliasY;
	}
	public void setMapY(String sAliasY){
		this.sAliasY = sAliasY;
	}

	public KernelJPanelCascadedZZZ getMapPanel() {
		return this.panelMap;
	}
	
	private void setMouseMotionHandler(TileMouseMotionHandlerTHM objHandler){
		this.objTileMouseMotionHandler = objHandler;
	}
	public TileMouseMotionHandlerTHM getMouseMotionHandler(){
		return this.objTileMouseMotionHandler;
	}
	
	private void setHexSideLength(int iHexSideLength){
		this.iHexSideLength = iHexSideLength;
	}
	public int getHexSideLength(){
		return this.iHexSideLength;
	}
	
	private double computeRadiusInner(int iHexSideLength){
		return iHexSideLength * ( MathZZZ.square2(3));
	}
	
	private int computeTileSideLength(int iHexSideLength){
		double dRadiusInner = this.computeRadiusInner(iHexSideLength);
		return (int) dRadiusInner / 2;
	}
	
	private int getTileSideLength(){
		if(this.iTileSideLength==0){
			this.iTileSideLength = this.computeTileSideLength(this.getHexSideLength());
		}
		return this.iTileSideLength;
	}

	@Override
	public String getUniquename() {
		//return this.sUniquename;
		return (String) this.getDto().get(ITileDtoAttribute.UNIQUENAME);
	}
	protected void setUniquename(String sUniquename) {
		//this.sUniquename=sUniquename;
		this.getDto().set(ITileDtoAttribute.UNIQUENAME, sUniquename);
	}

	@Override
	public GenericDTO<ITileDtoAttribute> getDto() {
		if(this.objDto==null){
			//this.objDto =GenericDTO.getInstance(ITileDtoAttribute.class); //ITileDtoAttribute bestimmt also welche Properties in der DTO-Klasse gespeicehrt sind.
			
			//FGL 20171011: Ersetzt durch eine Factory - Klasse
//			TileDtoFactory factoryTile = new TileDtoFactory();
//			GenericDTO dto = factoryTile.createDTO();	
			
			//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator, die ls Singleton umgebaut wurde:
			try {
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
				IDtoFactoryZZZ objFactory = objFactoryGenerator.getDtoFactory(this.getClass());
				GenericDTO dto = objFactory.createDTO();				
				this.objDto = dto;				
			} catch (ExceptionZZZ e) {
				e.printStackTrace();
				System.out.println("Ein Fehler ist aufgetreten: " + e.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
			}						
		}
		return this.objDto;
	}

//	@Override
//	public void setDto(GenericDTO<ITileDtoAttribute>objDto) {
//		this.objDto = objDto;
//	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setDto(GenericDTO objDto) {
		this.objDto = objDto;
	}



	
}

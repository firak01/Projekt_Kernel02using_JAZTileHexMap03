package use.thm.client.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IBackendPersistenceUser4UiZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.component.UITransparencyHelper;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import use.thm.ApplicationSingletonTHM;
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
	private int iTileSideLength=0; //= RadiusOuter / 4
	private int iTileSideHeight=0; //= RadiusOuter / 4
	private int iTileSideWidth=0; //= RadiusOuter / 2
	private int iTileLabelHeight=0; 
	private int iTileLabelWidth=0; 
	
	private KernelJPanelCascadedZZZ panelMap;
	private  TileMouseMotionHandlerTHM objTileMouseMotionHandler;
	
	private boolean bDragModeStarted = false; //Hiermit erkennt man, ob über der Componente eine Maustaste "einmal" gedrückt worden ist.
	
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
		
		try{			
			int iFontOffset = 3;//Irgenwie die Fontgröße justieren
			
			//1.  Der Hintergrund des Spielsteins: Das Bild...  Merke. Zeichne das zuerst. Dann kann man ggfs. etwas Text darübergeschrieben tollerieren.
			//++++++++++
		    //Die Größe der Icons aus der KernelKonfiguration auslesen
			KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();	
			String sModuleAlias = this.getMapPanel().getModuleName();
			String sProgramAlias = this.getMapPanel().getProgramAlias();				
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
			String sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );
			int iIconWidth = Integer.parseInt(sIconWidth);				
			String sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			int iIconHeight = Integer.parseInt(sIconHeight);
			
		    //+++++++++	
			
			String sTileIconName = this.getVariantImageUrlString();			
			 String sBaseDirectory = ApplicationSingletonTHM.getInstance().getBaseDirectoryStringForImages();//WICHTIG: NUN Noch den Basispfad davorhängen!
	    	 String sFilename = sBaseDirectory + File.separator + sTileIconName;
			File objFile = new File(sFilename);		   
			BufferedImage objBufferdImageTemp = ImageIO.read(objFile);
					 			
			//!!! Wenn es Army Bilder sind, dann diese noch weiter verkleinern
			BufferedImage objBufferedImageResized = null;			
			String sSubtype = this.getSubtype(); //Army oder Fleet
			if(sSubtype.equalsIgnoreCase("AR")){
				objBufferedImageResized = UIHelper.cropImageByPoints(objBufferdImageTemp, 0,50,60,10);	//Schneide das Bild erst aus dem Rahmen aus. Sehr viel vom unteren Rand weg, sehr viel vom linken Rand weg.
				objBufferedImageResized = UIHelper.resizeImage(objBufferedImageResized, iIconWidth, iIconHeight);
			}else{
				objBufferedImageResized = UIHelper.resizeImage(objBufferdImageTemp, iIconWidth, iIconHeight);		
			}
			
			//+++++++++ Merke: Anders als beim ImageIcon ist das Image nicht transparent. Das muss extra noch gemacht werden.
			Image imageResizedAndTransparent = UITransparencyHelper.makeColorTransparent(objBufferedImageResized, Color.WHITE);
			//ABER: So richtig schick ist diese Lösung nicht... nur leicht besser, als wenn der Weisse Rand in ein anderes HEX-Feld reinragt.
			//Vielleicht ist das Beser: 
			//https://stackoverflow.com/questions/12020597/java-convert-image-to-icon-imageicon
			//https://tips4java.wordpress.com/2010/08/22/alpha-icons/
			
			//+++++++++ Das Bild an der errechneten Postion (unterhalb des Labels) zeichnen.
			int iTileSideLength = this.getTileSideLength();
			int iPositionIconInHeight = (iTileSideLength - iIconHeight - iFontOffset); //Darüber kommt noch die Schrift
			//g.drawImage(objBufferedImageResized, 0,iPositionIconInHeight, null);//Hierdurch wird wohl das Image wieder in das neue, zurückzugebend BufferedImage gepackt.	
			g.drawImage(imageResizedAndTransparent, 0,iPositionIconInHeight, null);//Hierdurch wird wohl das Image wieder in das neue, zurückzugebend BufferedImage gepackt.
			
			//####################
			//2. Der Hintergrund des Spielsteins: Der Labelkasten (über das Bild, darum erst nach dem Bild malen!!!)
			int iTileLabelWidth = this.getTileLabelWidth();
			int iTileLabelHeight = this.getTileLabelHeight();
			g.setColor(Color.red);
			g.fillRect(0,0, iTileLabelWidth,iTileLabelHeight);
			
			
			//Die Beschriftung des Spielsteins
			g.setColor(Color.green);												//Schriftfarbe
			// Font f = new Font("Comic Sans MS", Font.BOLD, 20); //Die Schriftgröße ändern, hier einen bestimmten Font setzen
			Font font = g.getFont().deriveFont( 8.0f );					//Die Schriftgöße ändern, hier des aktuellen Fonts						
			g.setFont( font );
			
			String sComponentLabelUsed = null;//Einen Namen (Kurz, nomal, lang) als Eigenschaft den Objekten hinzufügen (über die Dto-Funktionalität) und dann die "Kurzform" hier anzeigen.
			String sVariantShorttext = this.getVariantShorttext();		
			if(StringZZZ.isEmpty(sVariantShorttext)){
				String sUniquename = this.getUniquename();
				sComponentLabelUsed = sUniquename;
			}else{
				sComponentLabelUsed = sVariantShorttext;
			}
						
			//Den gefundenen Namen abkürzen 
				sComponentLabelUsed = StringZZZ.toShorten(sComponentLabelUsed, StringZZZ.iSHORTEN_METHOD_VOWEL, 1);//Entferne aus dem String die Vokale, offset 1, D.h. Beginnende Vokale werden nicht gekürzt. 
				sComponentLabelUsed = StringZZZ.abbreviateDynamic(sComponentLabelUsed, 8 );//Nach 8 Zeichen soll der Name abgekürzt werden, d.h. abgeschnitten und "..." am Ende, um das Abkürzen zu kennzeichnen..
				
				Integer intInstanceVariantUniquenumber = this.getInstanceVariantUniqueNumber();
				String sInstanceUniquenumber = null;
				if(intInstanceVariantUniquenumber!=null){
					sInstanceUniquenumber = intInstanceVariantUniquenumber.toString();
					sComponentLabelUsed = sComponentLabelUsed + "_" + sInstanceUniquenumber;
				}						
				//g.drawString(sComponentLabelUsed,0,(int)(this.getTileLabelHeight()));//Unter dem Bild
				g.drawString(sComponentLabelUsed,0,(int)this.getTileLabelHeight()-iFontOffset);//über dem Bild. -3 ist ein Offset, so dass der Text in der Höhe zentriert in den Labelkasten reinpasst. Bei 0 wird ein Teil nach unten verschwinden.
			
				setOpaque(false);				
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void setDragModeStarted(boolean bStarted){
		this.bDragModeStarted = bStarted;
		if(bStarted==true){  //Merke: im MouseMove - Event des MouseMotionHandlers wird der Cursor zum "isMovable" Cursor.
			Cursor objCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);		
			this.setCursor(objCursor);  //Den Cursor wieder zurücksetzen
		}else{
			Cursor objCursor = new Cursor(Cursor.DEFAULT_CURSOR);		
			this.setCursor(objCursor);  //Den Cursor wieder zurücksetzen
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
	public int getHexSideHeight(){
		return this.iHexSideLength;
	}
	public int getHexSideWidth(){
		return this.iHexSideLength;
	}
	
	private double computeRadiusInner(int iHexSideLength){
		return iHexSideLength * ( MathZZZ.square2(3));
	}
	
	private int computeTileSideLength(int iHexSideLength, int iDivisor){
		double dRadiusInner = this.computeRadiusInner(iHexSideLength);
		return (int) dRadiusInner / iDivisor;
	}
	
	private int getTileSideLength(){
		if(this.iTileSideLength==0){
			this.iTileSideLength = this.computeTileSideLength(this.getHexSideLength(), 2); // 2 wäre der halbe Platz, 1 ist der ganze Platz
		}
		return this.iTileSideLength;
	}
	
	private int getTileLabelHeight(){
		if(this.iTileLabelHeight==0){
			this.iTileLabelHeight = this.computeTileSideLength(this.getHexSideLength(),6); // 2 wäre der halbe Platz, 1 ist der ganze Platz
		}
		return this.iTileLabelHeight;
	}
	private int getTileLabelWidth(){
		if(this.iTileLabelWidth==0){
			this.iTileLabelWidth = this.computeTileSideLength(this.getHexSideLength(), 2); // 2 wäre der halbe Platz, 1 ist der ganze Platz
		}
		return this.iTileLabelWidth;
	}
	
	private int getTileSideHeight(){
		if(this.iTileSideHeight==0){
			this.iTileSideHeight = this.computeTileSideLength(this.getHexSideLength(), 1); // 2 wäre der halbe Platz, 1 ist der ganze Platz
		}
		return this.iTileSideHeight;
	}
	
	private int getTileSideWidth(){
		if(this.iTileSideHeight==0){
			this.iTileSideHeight = this.computeTileSideLength(this.getHexSideLength(), 2); // 2 wäre der halbe Platz, 1 ist der ganze Platz
		}
		return this.iTileSideHeight;
	}
	
	

	public String getUniquename() {
		return (String) this.getDto().get(ITileDtoAttribute.UNIQUENAME);
	}
	protected void setUniquename(String sUniquename) {
		this.getDto().set(ITileDtoAttribute.UNIQUENAME, sUniquename);
	}
	
	public String getSubtype() {
		return (String) this.getDto().get(ITileDtoAttribute.SUBTYPE);
	}
	protected void setSubtype(String sSubtype) {
		this.getDto().set(ITileDtoAttribute.SUBTYPE, sSubtype);
	}
	
	public String getVariantShorttext(){
		return (String) this.getDto().get(ITileDtoAttribute.VARIANT_SHORTTEXT);
	}
	protected void setVariantShorttext(String sVariantShorttext){
		this.getDto().set(ITileDtoAttribute.VARIANT_SHORTTEXT, sVariantShorttext);
	}
	
	public String getVariantImageUrlString(){
		return (String) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGE_URL_STRING);
	}
	protected void setVariantImageUrlString(String sVariantImageUrlString){
		this.getDto().set(ITileDtoAttribute.VARIANT_IMAGE_URL_STRING, sVariantImageUrlString);
	}
	
	public Integer getInstanceVariantUniqueNumber(){
		return (Integer) this.getDto().get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
	}
	protected void setVariantShorttext(Integer intInstanceVariantUniqueNumber){
		this.getDto().set(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER, intInstanceVariantUniqueNumber);
	}

	public GenericDTO<ITileDtoAttribute> getDto() {
		if(this.objDto==null){
			//this.objDto =GenericDTO.getInstance(ITileDtoAttribute.class); //ITileDtoAttribute bestimmt also welche Properties in der DTO-Klasse gespeicehrt sind.
			
			//FGL 20171011: Ersetzt durch eine Factory - Klasse
//			TileDtoFactory factoryTile = new TileDtoFactory();
//			GenericDTO dto = factoryTile.createDTO();	
			
			//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator, die als Singleton umgebaut wurde:
			try {
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
				GenericDTO dto = objFactoryGenerator.createDtoForClass(this.getClass());										
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
	public void setDto(GenericDTO objDto) {
		this.objDto = objDto;
	}



	
}

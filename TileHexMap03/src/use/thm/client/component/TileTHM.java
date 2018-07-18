package use.thm.client.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
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
import basic.zBasicUI.component.UIHelperAlphaIcon;
import basic.zBasicUI.component.UIHelperAlphaImageIcon;
import basic.zBasicUI.component.UIHelperAnalyseImage;
import basic.zBasicUI.component.UIHelperTransparency;
import basic.zBasicUI.component.UIHelperTransparencyRange;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.IPanelCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.IMapPositionableTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.client.handler.TileMouseMotionHandlerTHM;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.dto.TileDtoFactory;

//Merke: 20180718: Zum Zugriff auf INI-Werte, den Kernel aus der PanelMap Holen oder aus dem Singelton-Objekt.
//dadurch spart man sich: //public class TileTHM extends KernelJPanelCascadedZZZ implements IMapPositionableTHM, IBackendPersistenceUser4UiZZZ {
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
			this.setDto(objDto);
			
			this.setMapX(sAliasX);
			this.setMapY(sAliasY);
			this.setMapPanel(panelMap);
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
			setOpaque(true);//Schaltet den default Hintergrund aus (normalerweise grau). // Dies auf false gesetzt "opaque heisst 'undurchsichtig' ").
						
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
			//20180630: Hier auf das BLOB-Objekt der DTO zugreifen!!! Welches aus der Variante stammt. Performanter. Es ist für jeden "Anwendungsfall" das Bild passend berechnet in der Datenbank gespeichert.
			//Das wäre die Dateisystem-Lösung: Der Pfad zum lokal im Dateisystem abgelegten Bild
//			String sTileIconName = this.getVariantImageUrlString();			
//			 String sBaseDirectory = ApplicationSingletonTHM.getInstance().getBaseDirectoryStringForImages();//WICHTIG: NUN Noch den Basispfad davorhängen!
//	    	 String sFilename = sBaseDirectory + File.separator + sTileIconName;
//			File objFile = new File(sFilename);		   
//			BufferedImage objBufferedImageTemp = ImageIO.read(objFile);		
			
			//TODO GOON 20180718: Hier in Abhängigkeit vom gerade eingestellten "ZoomFaktor" das passende Bild holen.
			//Also z.B. this.getVariantImageUsedInByte(int iZoomFactor);
			//
			//Das Bild aus dem in der Datenbank hinterlegten byte[] beziehen. Transportiert wird das über DTO-Objekt.
			byte[] imageInByte = this.getVariantImageUsedInByte();
			BufferedImage objBufferedImageTransparentAndResized = UIHelper.toBufferedImage(imageInByte);
						
			//+++++++++ Das Bild an der errechneten Postion (unterhalb des Labels) zeichnen.			
			String sHexZoomFactor = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "HexZoomFactor" );
			int iHexZoomFactor = Integer.parseInt(sHexZoomFactor);
			String sFontOffset = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconLabelFontOffsetHeight_float" );//Irgendwie die Fontgröße justieren in der Höhe. Wird dann auch vom HexMapZoomFaktor beeinflusst ...			//
			Float fltFontOffset = new Float(sFontOffset);
			float fFontOffset = fltFontOffset.floatValue();//z.B. 1.5f
		
			//TODO GOON 20180718: Die Formel zu Berechnung mit dem ZoomFactor in die ini-DAtei ausgelagert.
			//int iFontOffsetUsed=(int) (fFontOffset*iHexZoomFactor);
			int iFontOffsetUsed = (int) fFontOffset;
			
			
			int iTileSideLength = this.getTileSideLength();
			int iPositionIconInHeight = (iTileSideLength - iIconHeight - iFontOffsetUsed); //Darüber kommt noch die Schrift
			g.drawImage(objBufferedImageTransparentAndResized, 0,iPositionIconInHeight, null);//Hierdurch wird wohl das Image wieder in das neue, zurückzugebend BufferedImage gepackt.
			
			//####################
			//2. Der Hintergrund des Spielsteins: Der Labelkasten (über das Bild, darum erst nach dem Bild malen!!!)
			int iTileLabelWidth = this.getTileLabelWidth();
			int iTileLabelHeight = this.getTileLabelHeight();
			

			//++++ Den Labelkasten als Rechteck über dem Spielstein, mit einem schmalen Rand. Diesen dann mit GRÜN auffüllen, dabei  die HEALTH des Spielsteins beachten.
			//Verwende dafür Graphics2D. Merke: Graphics Object can always be cast Graphics2D g2d = (Graphics2D)g;
			//Merke: Den Kasten zuerst, sonst franst das irgendwie aus.
			Graphics2D g2 = (Graphics2D)g;			
			float thickness = 4;
			Stroke oldStroke=g2.getStroke();
			g2.setStroke(new BasicStroke(thickness));
			g2.setColor(Color.red); //TODO: Der Kasten sollte irgendwie die "Spielerfarbe sein".
			g2.drawRect(0,0,iTileLabelWidth,iTileLabelHeight);
			g2.setStroke(oldStroke);
			
			
			//... und darüber dann den Kasten. Hier die optiemierten Weiten und Höhenangaben
			//Das Problem ist, dass die Ränder beim 100%igen Überlappen ausfransen. 
			//Also: Das gefüllte Rechteck etwas unterhalb des gemalten Rahmens beginnen (x=2) und noch etwas tiefer ansetzen (y=2), damit die untere Linie nicht zu dick wird.
			//       und dann etwas kleiner (-2 in der Höhe, bzw. -4 in der Breite).       	
			int iWidthInBox_full=iTileLabelWidth-4;
			int iHeightInBox = iTileLabelHeight-2;
			int ixInBox=2;
			int iyInBox=2;
		
			//... und darüber dann den Kasten. Zuerst einen weissen Hintergrund
			g.setColor(Color.white);
			g.fillRect(ixInBox,iyInBox, iWidthInBox_full,iHeightInBox);
			 
			////TODO 20180705 ... und darüber dann den Kasten, nun mit grünem Hintergrund.			Das zeigt die HEALT an.
			//Hole den Health Grad des Spielsteins aus dem aus dem DTO.
			int iWidthInBox_used = 0;
			Float fltHealth = this.getHealth();
			if(fltHealth==null){
//				iWidthInBox_used = 0;
				
				//TESTWEISE
				//Float fltWithInBox_used = (new Float(iWidthInBox_full * fltHealth.floatValue());
				Float fltWithInBox_used = new Float(iWidthInBox_full * 0.75);
				iWidthInBox_used = fltWithInBox_used.intValue();
			}else{
				if(fltHealth.intValue()==1){
					iWidthInBox_used = iWidthInBox_full;
//				}else if(fltHealth.intValue()==0){
//					//Sollte der Spielstein dann nicht schon vernichtet sein?
//					iWidthInBox_used = 0;
				}else{
					Float fltWithInBox_used = new Float(iWidthInBox_full * fltHealth.floatValue());
					//Float fltWithInBox_used = new Float(iWidthInBox_full * 0.75);
					iWidthInBox_used = fltWithInBox_used.intValue();
				}
			}
									
			g.setColor(Color.green); 	
			g.fillRect(ixInBox,iyInBox, iWidthInBox_used,iHeightInBox); 	
							
	
			//Die Beschriftung des Spielsteins
			g.setColor(Color.black);												//Schriftfarbe
			// Font f = new Font("Comic Sans MS", Font.BOLD, 20); //Die Schriftgröße ändern, hier einen bestimmten Font setzen
			
			
			//Font font = g.getFont().deriveFont( 8.0f );					//Die Schriftgöße ändern, hier des aktuellen Fonts						
			//Font font = g.getFont().deriveFont( 12.0f );					//Die Schriftgöße ändern, hier des aktuellen Fonts
						
			String sIconLabelFontSize = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconLabelFontSize_float" );
			if(StringZZZ.isEmpty(sIconLabelFontSize)){ sIconLabelFontSize="8.0"; }
			Float fltIconLabelFontSize = new Float(sIconLabelFontSize);
			
			//TODO GOON 20180718: Die Formel zu Berechnung mit dem ZoomFactor in die ini-DAtei ausgelagert.
			//Float fltIconLabelFontSizeUsed = fltIconLabelFontSize.floatValue() * iHexZoomFactor; 
			Float fltIconLabelFontSizeUsed = fltIconLabelFontSize;
			Font font = g.getFont().deriveFont( fltIconLabelFontSizeUsed.floatValue());	//Die Schriftgöße ändern, hier des aktuellen Fonts
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
			sComponentLabelUsed = StringZZZ.abbreviateDynamic(sComponentLabelUsed, 5 );//Nach 5 Zeichen soll der Name abgekürzt werden, d.h. abgeschnitten und "..." am Ende, um das Abkürzen zu kennzeichnen..
				
			//20180703 Hier wird die tatsächliche Nummer der Variante reingeschrieben und nicht mehr nur zwiwchen ARMY / FLEET unterschieden. Gelöst durch: DAO Klasse .findColumnValueMaxForVariant();	 UND dann in die DTO übertragen			
			Integer intInstanceVariantUniquenumber = this.getInstanceVariantUniqueNumber();
			String sInstanceUniquenumber = null;
			if(intInstanceVariantUniquenumber!=null){
				sInstanceUniquenumber = intInstanceVariantUniquenumber.toString();
				sComponentLabelUsed = sComponentLabelUsed + "_" + sInstanceUniquenumber;
			}						
			//g.drawString(sComponentLabelUsed,0,(int)(this.getTileLabelHeight()));//Unter dem Bild
			//nach links wg. des Rahmens etwas Platz (darum x=3) und etwas tiefer, darum in  der Höhe +1
			//g.drawString(sComponentLabelUsed,ixInBox+1,(int)this.getTileLabelHeight()-iFontOffset+1);//über dem Bild. -3 ist ein Offset, so dass der Text in der Höhe zentriert in den Labelkasten reinpasst. Bei 0 wird ein Teil nach unten verschwinden.						
			g.drawString(sComponentLabelUsed,ixInBox+1,(int)this.getTileLabelHeight()-iFontOffsetUsed+1);//über dem Bild. 
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
	private void setMapPanel(KernelJPanelCascadedZZZ panelMap){
		this.panelMap = panelMap;
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
	
	public byte[] getVariantImageUsedInByte(){
		//das wäre das Bild in normaler Größe return (byte[]) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE); //es müsste kliner gerechnet werden
		//das kleiner und transparent gerechnete Bild
		return (byte[]) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE);
		//return (byte[]) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE);
	}
	protected void setVariantImageUsedInByte(byte[] imageInByte){
		//das wäre das Bild in normaler Größe   this.getDto().set(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE, imageInByte); //es müsste kliner gerechnet werden
		this.getDto().set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE, imageInByte);
	}
	
	public Integer getInstanceVariantUniqueNumber(){
		return (Integer) this.getDto().get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
	}
	protected void setInstanceVariantUniqueNumber(Integer intInstanceVariantUniqueNumber){
		this.getDto().set(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER, intInstanceVariantUniqueNumber);
	}
	
	public Float getHealth(){
		return (Float) this.getDto().get(ITileDtoAttribute.HEALTH);
	}
	public void setHealth(float fHealth){
		Float fltValue = new Float(fHealth);
		this.getDto().set(ITileDtoAttribute.HEALTH, fltValue);
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

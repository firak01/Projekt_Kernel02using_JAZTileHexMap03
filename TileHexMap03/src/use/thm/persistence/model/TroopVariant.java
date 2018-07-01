package use.thm.persistence.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.EnumSet;

import javax.imageio.ImageIO;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import use.thm.ApplicationSingletonTHM;
import use.thm.persistence.interfaces.ITroopArmyVariantTHM;
import use.thm.persistence.interfaces.ITroopVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.model.Immutabletext.EnumImmutabletext;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.enums.ICategoryProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.component.UIHelperTransparencyRange;
import basic.zKernel.KernelZZZ;

/**Klasse für die Werte der TroopVarianten - SO NICHT persistierbar.
 * Erst die Kindklassen sind per JPA Persistierbar. 
 * 
* @author lindhaueradmin
*/

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity  //Vererbung: Falls es Vererbung gibt, kann man die Elternklasse wohl nicht vermeiden. DAS WILL ICH ABER NICHT
@Access(AccessType.PROPERTY)
@org.hibernate.annotations.Immutable //Ziel: Performancesteigerung. Siehe Buch "Java Persistance with Hibernate", S. 107. Dafür dürfen die POJOs aber keine public Setter-Methoden haben.

//VERERBUNG und STRATEGIEN:
//ZIEL: Nur bestimmte Entities in einer eigenen Klasse 
//@Inheritance(strategy =  InheritanceType.JOINED )

//Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Ziel: Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhand des Discriminator Wertes unterschieden
//DAS IST DER DEFAULT, wenn nur java-mäßig verebt wird.
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
//Merke: Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
//@DiscriminatorColumn(name="Entityclass", discriminatorType = DiscriminatorType.STRING) 
//
@Table(name="troopvariant")
public abstract class TroopVariant  extends KeyImmutable implements ITroopVariantTHM, ICategoryProviderZZZ, Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	private int iMyTestSequence;
		
	//Variante 1) Beispielsweise für das Spielbrett gewählte Variante...
	//Realisierung eines Zusammengesetzten Schlüssels
	//Siehe Buch "Java Persistence API 2", Seite 48ff.
	//@EmbeddedId
	
	//Merke: Attribut Access über FIELD.
//	@AttributeOverrides({
//			@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
//			@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
//			@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
//	})
	
	/*Meine Variante VOR dem Lösungsversuch mit dem generierten, eindeutigem Schlüssel.... Beide Varainten kann man nicht vereinen. 
	//Merke: Attribut Access über PROPERTY	
	@AttributeOverrides({
			@AttributeOverride(name = "mapAlias", column= @Column(name="MAPALIAS")),
			@AttributeOverride(name = "player", column= @Column(name="PLAYER", length = 2)),
			@AttributeOverride(name = "uniquename", column= @Column(name="UNIQUENAME", length = 6))	//Merke UNIQUE selbst nicht verwendbar, ist ein Schlüsselwort				
	})
	*/
	
	//für die "thiskey_id"
	private Long lKey;
	
	//Jetzt die verschiedenene Eigenschaften eines Armeetypens hier festlegen.
	private TileDefaulttext objDefaulttext;
	private TileImmutabletext objImmutabletext;
	private String sUniquetext;
	private String sCategorytext;
	private Integer intMapMoveRange;
	private String sImageUrl;
	
	//Blob selbst funtkioniert nicht bei dieser SQLIte Datenbank. Alternativer Lösungsversuch Speicherung als Byte-Array
	//Das Bild der Spielsteinvariante (, noch nicht angepasst)
	@Transient
	private byte[] imageInByte01;
	
	@Transient
	private String sImageInByte01;
	
	@Transient
	private Long lngImageInByte01;
	
	//Das Bild der Spielsteinvariante für Katalog (, entsprechend angepasst)
	@Transient
	private byte[] imageInByte01Catalog;
	
	@Transient
	private String sImageInByte01Catalog;
	
	@Transient
	private Long lngImageInByte01Catalog;
	
	//Das Bild der Spielsteinvariante für die Darstellung in der HexMap - Karte (, entsprechend angepasst)
			@Transient
			private byte[] imageInByte01Hexmap;
			
			@Transient
			private String sImageInByte01Hexmap;
			
			@Transient
			private Long lngImageInByte01Hexmap;
	
	//Das Bild der Spielsteinvariante für die Darstellung im Dialog (, entsprechend angepasst)
		@Transient
		private byte[] imageInByte01Dialog;
		
		@Transient
		private String sImageInByte01Dialog;
		
		@Transient
		private Long lngImageInByte01Dialog;
	
		
		//Das Bild der Spielsteinvariante für die Darstellung beim Ziehen über die HEXMAP (, entsprechend angepasst)
				@Transient
				private byte[] imageInByte01Drag;
				
				@Transient
				private String sImageInByte01Drag;
				
				@Transient
				private Long lngImageInByte01Drag;
				
	//... und weitere Eigenschaften.
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public TroopVariant(){		
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
	 }
	 	 
	 //Konstruktor, an den alles übergeben wird. Wg. "Immutable" gibt es keine 'public' Setter.
	 public TroopVariant(int iKey, String sUniquetext, String sCategorytext, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
		 
		 this.setThiskey(Long.valueOf(iKey));
		 this.setUniquetext(sUniquetext);
		 this.setCategorytext(sCategorytext);
		 this.setMapMoveRange(Integer.valueOf(intMapMoveRange));
		 this.setDefaulttextObject(objDefaulttext);
		 this.setImmutabletextObject(objImmutabletext);
		 this.setImageUrlString(sImageUrl);
		 
		 //+++++++++++++++++++++++++++++++
		//Problem: Man kann zwar jedes mal das Bild neu aus dem Dateisystem holen und dann für den entsprechenden Einsatz verglößern/verkleiner, etc. Aber die Performance leidet darunter.
		//Lösungsansatz: Speicher das einmal ersetzte Bild als BLOB in der Datenbank (unter der Variante), packe das Bild in das DTO-Objekt
		//               und rufe hier dann immer nur das optimierte, größenmäßig unveränderte Bild auf.
		 
		 //Hole ein byte[], welches dann mit weiteren Informationen im Entity als Bild gespeichert werden kann.				 
		 //Das Bild wird zuvor noch bearbeitet.
		 //... noch weitere Infos
		 try{
			 KernelZZZ objKernel = ApplicationSingletonTHM.getInstance().getKernelObject();
		 	
			 //+++++++++++++++++++++++++++++++
			 //1. Bild als Grundlage, unverändert....
			 String sModuleAlias = "THM";//this.getModuleName();
			 String sProgramAlias = "EntityTroopVariant"; //this.getProgramName();			
			 System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
					 
			String sTileIconName =  sImageUrl;	//so holt man es aus dem DTO Objekt, wenn man im UI ist... this.getTile().getVariantImageUrlString();			
			String sBaseDirectory = ApplicationSingletonTHM.getInstance().getBaseDirectoryStringForImages();//WICHTIG: NUN Noch den Basispfad davorhängen!
	    	String sFilename = sBaseDirectory + File.separator + sTileIconName;
			File objFile = new File(sFilename);		   
			BufferedImage objBufferedImageOriginal = ImageIO.read(objFile);					
						
			/* Da wäre der Ansatz das Bild direkt von Platte zu lesen. Wir haben aber schon ein BufferedImage 
			// Hole erst einmal das Bild				
			String sTileIconName = saFile[iImageIndex];
			String sFilename = sBaseDirectory + File.separator + sTileIconName;
			File objFile = new File(sFilename);
			
			//Wir schreiben die Bytes weg
			byte[] imageBytes = this.getByteArrayFromFile(sFilename);		
			long lngFileLength = objFile.length();
			*/
			
			/*Ansatz, direkt aus dem BufferedImage eine byte[] machen...*/				
			byte[] imageInByte = UIHelper.getByteArrayFromBufferedImage(objBufferedImageOriginal,"png");			
			this.setImage01(imageInByte);			
			long lngFileSize = imageInByte.length; //Diese Infos braucht man, um das Bild wieder auszulesen. Oder?
			this.setImage01Length(lngFileSize);
			this.setImage01Name(sTileIconName);
			
			//++++++++++++++++++++++++++++++++++++++++
			//2. Bild als Katalogeintrag
			sModuleAlias = "THM";//this.getModuleName();
			sProgramAlias = "CatalogPanel"; //this.getProgramName();			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
						 
			//... Größen holen aus der Kernelkonfiguration
			String sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );			
			int iIconWidth = Integer.parseInt(sIconWidth);				
			String sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			int iIconHeight = Integer.parseInt(sIconHeight);
			
			//... Bild bearbeitet als Katalogeintrag
			BufferedImage objBufferdImageResized = UIHelper.resizeImage(objBufferedImageOriginal, iIconWidth, iIconHeight);
			byte[] imageInByteCatalog = UIHelper.getByteArrayFromBufferedImage(objBufferdImageResized,"png");			
			this.setImage01Catalog(imageInByteCatalog);			
			long lngFileSizeCatalog = imageInByteCatalog.length;
			this.setImage01LengthCatalog(lngFileSizeCatalog);
			this.setImage01NameCatalog(sTileIconName);
					
			
			//+++++++++++++++++++++++++
			//3. ... Bild bearbeitet für die Darstellung in der Karte (wurde ohne diese Abspeicherung zuvor jedesmal in TileTHM.paintComponent() gemacht. Das "jedes Mal" Berechnen spart man sich nun.
			//Hier wird versucht den weissen Rand zu entfernen und es wird ggfs. noch gesondert ausgeschnitten (crop).
			sModuleAlias = "THM";//this.getModuleName();
			sProgramAlias = "HexMapCentral"; //this.getProgramName();			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
				
			//... Größen holen aus der Kernelkonfiguration
			sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );			
			iIconWidth = Integer.parseInt(sIconWidth);				
			sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			iIconHeight = Integer.parseInt(sIconHeight);
			
			//1. Versuche das Bild mit einem transparenten Hintergrund auszustatten:
			//Beispielsansatz das Bild mit einem Transparenten Hintergund hinzubekommen:
			//Ein ImageIcon zeichnen
//			Icon icon = new ImageIcon(objBufferedImageTransparentAndResized);
//			int x = 0;
//			int y = 0;
//			//DAS IST DANN NOCH NICHT TRANSPARENT .... icon.paintIcon(this, g, x, y);
//			float alpha = 0.0F; //1.0F; ist untransparent //0.0F ist voll transparent
//			UIHelperAlphaIcon iconTransparent = new UIHelperAlphaIcon(icon, alpha);
//			//UIHelperAlphaImageIcon iconTransparent = new UIHelperAlphaImageIcon(icon, alpha);
//			iconTransparent.paintIcon(this, g, x, y);
			
			
			//Realisierter Ansatz
			Color color01 = new Color(255,255,255);//weiss
						
			//Image imageTransparent = UIHelperTransparency.makeColorTransparent(objBufferedImageTemp, color01);			
			//ABER: So richtig schick ist diese Lösung nicht... nur leicht besser, als wenn der Weisse Rand in ein anderes HEX-Feld reinragt.
			//Grund dafür war, dass das Bild noch weitaus mehr untransparente Farben hatte, die "annähernd" weiss waren. 
			//Lösungsansatz: Ersetze nun einen Bereich 			
			Image imageTransparent = UIHelperTransparencyRange.transformColorRangeToTransparency(objBufferedImageOriginal, color01, 6, 0, color01);
			BufferedImage objBufferedImageTransparent = UIHelper.toBufferedImage(imageTransparent);
			//!!! Wenn es Army Bilder sind, dann diese noch weiter verkleinern
			BufferedImage objBufferedImageTransparentAndResized = null;			
			//String sSubtype = this.getSubtype(); //Army oder Fleet. Das steht aber nur zur Verfügung in TileTHM.paintComponent() und kommt aus dem DTO. 
			//Hierher verlagert, muss man entweder den SubType "simulieren" oder auf alle möglichen Catagorietexte abprüfen. Es wird also in den Unterklassen der SubType "simuliert".
			
			String sSubtype = this.getSubtype();
			System.out.println(ReflectCodeZZZ.getClassCurrentName() +": Erzeuge Bilder für einen Subtype: '" + sSubtype + "'");
			if(sSubtype.equalsIgnoreCase("AR")){
				if(this.getCategorytext().equalsIgnoreCase("Infantery")){
					objBufferedImageTransparentAndResized = UIHelper.cropImageByPoints(objBufferedImageTransparent, 0,50,60,10);	//Schneide das Bild erst aus dem Rahmen aus. Sehr viel vom unteren Rand weg, sehr viel vom linken Rand weg.
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparentAndResized, iIconWidth, iIconHeight);
				}else{
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, iIconWidth-10, iIconHeight-5);
				}
				
							
				//-1. Analyse des verkleinerten und "um weiss entfernten" Bildes. Ziel ist es dies NOCH transparenter zu machen.
				//    Es sind noch andere Farben, die stören....
//				String sX = this.getMapX();
//				String sY = this.getMapY();
//				BufferedImage objBufferedImage2analyse = objBufferedImageTransparentAndResized;
//								
//			    System.out.println("Klasse " + this.getClass().getName());
//			    System.out.println(("AAAA "  + sSubtype + " an Position X/Y: " + sX + "/" +sY ));
//			    UIHelperAnalyseImage.debugPrintImagePixelData(objBufferedImage2analyse, true);			        
//				System.out.println(("ZZZZ "  + sSubtype + " an Position X/Y: " + sX + "/" +sY ));

			}else{
				objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, iIconWidth, iIconHeight);		
			}
								
			byte[] imageInByteHexmap = UIHelper.getByteArrayFromBufferedImage(objBufferedImageTransparentAndResized,"png");			
			this.setImage01Hexmap(imageInByteHexmap);			
			long lngFileSizeHexmap = imageInByteHexmap.length; //Diese Infos braucht man, um das Bild wieder auszulesen. Oder?
			this.setImage01LengthHexmap(lngFileSizeHexmap);
			this.setImage01NameHexmap(sTileIconName);
			
			//4. Bild für das Öffnen der Detailangaben in einer Dialogbox
			sModuleAlias = "THM";
			sProgramAlias = "TileDetailDialog"	;
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
			
			//FALLS GILT: GENUTZT WERDEN SOLL DAS MODUL FÜR DIE GRÖSSENANGABEN AUF DER KARTE. Code stand ursprünglich in TileMouseMotionHandlerTHM.mouseClicked(..)
			//String sModuleAlias = this.getTile().getMapPanel().getModuleName();
			//String sProgramAlias = this.getTile().getMapPanel().getProgramAlias();			
			
			//FALLS GILT: GENUTZT WERDEN SOLL DAS MODUL FÜR DIE GRÖSSENANGABEN AUF DEM CATALOG
			//		KernelJPanelCascadedZZZ objPanelToSearch = this.getTile().getMapPanel().getPanelNeighbour("WEST");			
			//		String sModuleAlias = objPanelToSearch.getModuleName();
			//		String sProgramAlias = objPanelToSearch.getProgramName();
			
			//... Größen holen aus der Kernelkonfiguration
			sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );			
			iIconWidth = Integer.parseInt(sIconWidth);				
			sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			iIconHeight = Integer.parseInt(sIconHeight);
			
			//... Bild bearbeitet als Dialogeintrag
			BufferedImage objBufferdImage4DialogResized = UIHelper.resizeImage(objBufferedImageOriginal, iIconWidth, iIconHeight);
			byte[] imageInByteDialog = UIHelper.getByteArrayFromBufferedImage(objBufferdImage4DialogResized,"png");			
			this.setImage01Dialog(imageInByteDialog);			
			long lngFileSizeDialog = imageInByteDialog.length;
			this.setImage01LengthDialog(lngFileSizeDialog);
			this.setImage01NameDialog(sTileIconName);
			
			
			//5. Bild für das Ziehen über die Karte (im Glasspane, per GhostPictureAdapter, der bei der Erstellung der Katalogboxen erzeugt wird.)
			sModuleAlias = "THM";
			sProgramAlias = "CatalogPanel"	;
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
			
			//FALLS GILT: GENUTZT WERDEN SOLL DAS MODUL FÜR DIE GRÖSSENANGABEN AUF DER KARTE. Code stand ursprünglich in TileMouseMotionHandlerTHM.mouseClicked(..)
			//String sModuleAlias = this.getTile().getMapPanel().getModuleName();
			//String sProgramAlias = this.getTile().getMapPanel().getProgramAlias();			
			
			//FALLS GILT: GENUTZT WERDEN SOLL DAS MODUL FÜR DIE GRÖSSENANGABEN AUF DEM CATALOG
			//		KernelJPanelCascadedZZZ objPanelToSearch = this.getTile().getMapPanel().getPanelNeighbour("WEST");			
			//		String sModuleAlias = objPanelToSearch.getModuleName();
			//		String sProgramAlias = objPanelToSearch.getProgramName();
			
			//... Größen holen aus der Kernelkonfiguration
			sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidthOnDrag" );			
			iIconWidth = Integer.parseInt(sIconWidth);				
			sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeightOnDrag" );
			iIconHeight = Integer.parseInt(sIconHeight);
			
			//... Bild bearbeitet als "Ziehen über das HEXFeld"
			BufferedImage objBufferdImage4DragResized = UIHelper.resizeImage(objBufferedImageOriginal, iIconWidth, iIconHeight);
			byte[] imageInByteDrag = UIHelper.getByteArrayFromBufferedImage(objBufferdImage4DragResized,"png");			
			this.setImage01Drag(imageInByteDrag);			
			long lngFileSizeDrag = imageInByteDrag.length;
			this.setImage01LengthDrag(lngFileSizeDrag);
			this.setImage01NameDrag(sTileIconName);
			
			
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	 
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
		//ABER NICHT AUF DIESER EBENEN, DA SIE ERBT VON KEY.java
	   //ABER: BEIM ERBEN VON KEY wira automatisch eine Tabelle Key erstellt.... das will ich nicht
		
	 
		 @Id				
		 @TableGenerator(name="lidGeneratorTroopVariant001", table="COMMON_FUER_IDGENERATOR_TROOPVARIANT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorTroopVariant001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
		 //Entferne also das unique...
		 @Column(name="TROOPVARIANT_ID_INCREMENTED", nullable=false)
		 public int getId(){
			 return this.iMyTestSequence;
		 }
		 public void setId(int iLid){
			 this.iMyTestSequence = iLid;
		 }
	 
	 //### getter / setter
		 //TODO: Dies in eine Oberklasse für alle "Varianten" verschieben.
		 @Column(name="KEYTYPE")
			@Access(AccessType.PROPERTY)
			public String getKeyType(){
				return super.getKeyType();
			}	
			public void setKeyType(String sKeyType){
				super.setKeyType(sKeyType);
			}
					
		 //### Aus ICategoryProviderZZZ
		 @Column(name="TILE_UNIQUETEXT", nullable=false)
		 public String getUniquetext(){
			 return this.sUniquetext;
		 }
		 protected void setUniquetext(String sUniquetext){
			 this.sUniquetext = sUniquetext;
		 }
		 
		 @Column(name="TILE_CATEGORYTEXT", nullable=false)
		 public String getCategorytext(){
			 return this.sCategorytext;
		 }
		 protected void setCategorytext(String sCategorytext){
			 this.sCategorytext= sCategorytext;
		 }
		 
		 //### Aus ITroopArmyVariant
	//TODO GOON: Diese Properties dann in einen Oberklasse für alle Spielsteine bringen TileVariant
	//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		//Variante 1) mit einer gemeinsamen Spalte
		//@Transient //Ich will kein BLOB speichern
		@OneToOne(fetch = FetchType.LAZY)
		//@JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id") //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Ohne die columnDefinition funktioniert das bei der SQLite Datenbank nicht.
		@JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1")
	 public Defaulttext getDefaulttextObject(){
		return this.objDefaulttext;
	 }
	 
		//Ist protected wg. immutable
	 protected void setDefaulttextObject(TileDefaulttext objDefaulttext){
		 this.objDefaulttext = objDefaulttext;
	 }
	 
	 
	 //### getter / setter
	//### Aus ITroopArmyVariant
	//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		//Variante 1) mit einer gemeinsamen Spalte
		//@Transient //Ich will kein BLOB speichern
		
	 //20180203: NEIN: Das ist eine n:1 Beziehung
		//@OneToOne(fetch = FetchType.LAZY)
		//@JoinColumn(name="immutabletext_thiskey_id", nullable = true) //Hiermit wird die ID in der Spalte gespeichert
	 
	 @ManyToOne(fetch = FetchType.LAZY)
	 //@JoinColumn(name="immutabletext_thiskey_id", referencedColumnName = "thiskey_id") //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
	 //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
	 //Ohne die columnDefinition funktioniert das bei der SQLite Datenbank nicht.
	 @JoinColumn(name="immutabletext_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1") 
	 public TileImmutabletext getImmutabletextObject(){
		return this.objImmutabletext;
	 }
	 
	 //Ist protected wg. immutable
	 protected void setImmutabletextObject(TileImmutabletext objImmutabletext){
		 this.objImmutabletext = objImmutabletext;
	 }
	 
	 //TODO GOON: Diese Reichweite dann in einen Oberklasse für alle Spielsteine bringen
	 @Column(name="TILE_MAPMOVE_RANGE", nullable=false)
	 public Integer getMapMoveRange(){
		 return this.intMapMoveRange;
	 }
	 protected void setMapMoveRange(Integer intMapMoveRange){
		 this.intMapMoveRange = intMapMoveRange;				 
	 }
	 
	 //TODO GOON: Diese Bildressource dann in eine Oberklasse für alle Spielsteine bringen
	 @Column(name="TILE_IMAGEURL", nullable=false)
	 public String getImageUrlString(){
		 return this.sImageUrl;
	 }
	 protected void setImageUrlString(String sImageUrl){
		 this.sImageUrl = sImageUrl;			 
	 }
	 

	 	//### DAS BILD SELBST IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)
		//ABER SQLite: Probleme beim Holen der Daten per HQL. ... @Lob auch ohne dies Annotation wird ein blob in der Datenbank angelegt... UND Nur so kann dann per HQL wieder auf diese Zelle zugegriffen werden. 
		//Merke: dependent on the hibernate version, the Lob annotation could have no type parameter. quote from here: @Lob no longer has attributes, the lob type (CLOB, BLOB) is guessed. If the underlying type is a String or an array of character then CLOB are used. Othersise BLOB are used.		
		@Column(name="image01", nullable=false)	
		public  byte[] getImage01() {
			return this.imageInByte01;
		}		
		public void setImage01(byte[] imageBlob) {
			this.imageInByte01 = imageBlob;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="image01name", nullable=false)
		public String getImage01Name() {
			return this.sImageInByte01;
		}
		public void setImage01Name(String sFileName) {
			this.sImageInByte01 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="image01length", nullable=false)
		public long getImage01Length() {
			return this.lngImageInByte01;
		}
		public void setImage01Length(long lngFileSize) {
			this.lngImageInByte01 = lngFileSize;
		}
		
		
		//### DAS BILD FÜR DIE KATLOGAUSWAHL IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)
		//ABER SQLite: Probleme beim Holen der Daten per HQL. ... @Lob auch ohne dies Annotation wird ein blob in der Datenbank angelegt... UND Nur so kann dann per HQL wieder auf diese Zelle zugegriffen werden. 
		//Merke: dependent on the hibernate version, the Lob annotation could have no type parameter. quote from here: @Lob no longer has attributes, the lob type (CLOB, BLOB) is guessed. If the underlying type is a String or an array of character then CLOB are used. Othersise BLOB are used.		
		@Column(name="CatalogImage01", nullable=false)	
		public  byte[] getImage01Catalog() {
			return this.imageInByte01Catalog;
		}		
		public void setImage01Catalog(byte[] imageInByte) {
			this.imageInByte01Catalog = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="CatalogImage01name", nullable=false)
		public String getImage01NameCatalog() {
			return this.sImageInByte01Catalog;
		}
		public void setImage01NameCatalog(String sFileName) {
			this.sImageInByte01Catalog = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="CatalogImage01length", nullable=false)
		public long getImage01LengthCatalog() {
			return this.lngImageInByte01Catalog;
		}
		public void setImage01LengthCatalog(long lngFileSize) {
			this.lngImageInByte01Catalog = lngFileSize;
		}
		
		//### DAS BILD FÜR DIE DARSTELLUNG IN DER KARTE IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)
		//ABER SQLite: Probleme beim Holen der Daten per HQL. ... @Lob auch ohne dies Annotation wird ein blob in der Datenbank angelegt... UND Nur so kann dann per HQL wieder auf diese Zelle zugegriffen werden. 
		//Merke: dependent on the hibernate version, the Lob annotation could have no type parameter. quote from here: @Lob no longer has attributes, the lob type (CLOB, BLOB) is guessed. If the underlying type is a String or an array of character then CLOB are used. Othersise BLOB are used.		
		@Column(name="HexmapImage01", nullable=false)	
		public  byte[] getImage01Hexmap() {
			return this.imageInByte01Hexmap;
		}		
		public void setImage01Hexmap(byte[] imageInByte) {
			this.imageInByte01Hexmap = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="HexmapImage01name", nullable=false)
		public String getImage01NameHexmap() {
			return this.sImageInByte01Hexmap;
		}
		public void setImage01NameHexmap(String sFileName) {
			this.sImageInByte01Hexmap = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="HexmapImage01length", nullable=false)
		public long getImage01LengthHexmap() {
			return this.lngImageInByte01Hexmap;
		}
		public void setImage01LengthHexmap(long lngFileSize) {
			this.lngImageInByte01Hexmap = lngFileSize;
		}
		
		//### DAS BILD FÜR DIE DARSTELLUNG IM DIALOG IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)
		//ABER SQLite: Probleme beim Holen der Daten per HQL. ... @Lob auch ohne dies Annotation wird ein blob in der Datenbank angelegt... UND Nur so kann dann per HQL wieder auf diese Zelle zugegriffen werden. 
		//Merke: dependent on the hibernate version, the Lob annotation could have no type parameter. quote from here: @Lob no longer has attributes, the lob type (CLOB, BLOB) is guessed. If the underlying type is a String or an array of character then CLOB are used. Othersise BLOB are used.		
		@Column(name="DialogImage01", nullable=false)	
		public  byte[] getImage01Dialog() {
			return this.imageInByte01Dialog;
		}		
		public void setImage01Dialog(byte[] imageInByte) {
			this.imageInByte01Dialog = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="DialogImage01name", nullable=false)
		public String getImage01NameDialog() {
			return this.sImageInByte01Dialog;
		}
		public void setImage01NameDialog(String sFileName) {
			this.sImageInByte01Dialog = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="DialogImage01length", nullable=false)
		public long getImage01LengthDialog() {
			return this.lngImageInByte01Dialog;
		}
		public void setImage01LengthDialog(long lngFileSize) {
			this.lngImageInByte01Dialog = lngFileSize;
		}
		
		//### DAS BILD FÜR DIE DARSTELLUNG BEIM ZIEHEN ÜBER DIE HEXKARTE. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)
		//ABER SQLite: Probleme beim Holen der Daten per HQL. ... @Lob auch ohne dies Annotation wird ein blob in der Datenbank angelegt... UND Nur so kann dann per HQL wieder auf diese Zelle zugegriffen werden. 
		//Merke: dependent on the hibernate version, the Lob annotation could have no type parameter. quote from here: @Lob no longer has attributes, the lob type (CLOB, BLOB) is guessed. If the underlying type is a String or an array of character then CLOB are used. Othersise BLOB are used.		
		@Column(name="DragImage01", nullable=false)	
		public  byte[] getImage01Drag() {
			return this.imageInByte01Drag;
		}		
		public void setImage01Drag(byte[] imageInByte) {
			this.imageInByte01Drag = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="DragImage01name", nullable=false)
		public String getImage01NameDrag() {
			return this.sImageInByte01Drag;
		}
		public void setImage01NameDrag(String sFileName) {
			this.sImageInByte01Drag = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="DragImage01length", nullable=false)
		public long getImage01LengthDrag() {
			return this.lngImageInByte01Drag;
		}
		public void setImage01LengthDrag(long lngFileSize) {
			this.lngImageInByte01Drag = lngFileSize;
		}
	 
	 //#### abstracte Methoden
	 //20171106 KÄME DAS ETWA DRUCH VERERBUNG AUS DER KLASSE "KEY" ???
	 //JAAA!!!	 ABER DANN GIBT ES EINE EIGENE TABELLE "KEY" und das will ich nicht.
	 //Merke: Wenn man diese Definition nicht in den hieraus erbenden Klassen explzit überschreibt, gibt es beim Refernzieren der Spalte eine Fehlermeldung
	 //Exception in thread "main" org.hibernate.AnnotationException: referencedColumnNames(thiskey_id) of use.thm.persistence.model.TroopArmy.troopArmyVariantObject referencing use.thm.persistence.model.TroopArmyVariant not mapped to a single property
	 //Lösung: die Spalte explizit in die hierauserbende Klasse aufnehmen.
	 //Nachdem dadurch der oben genannte Fehler behoben ist, gibt es die Fehlermeldung, dass insert="false" update="false" notwendig ist. Also: , insertable=false, updatable=false, 
	 //in der oben neu hinzugefügten Spalte.
	 @JoinColumn(name="trooparmyvariant_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1")	 
	 @Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	 @Override
	public Long getThiskey() {
		 return this.lKey;
	}
	 
	@Override
	public void setThiskey(Long thiskeyId) {
		this.lKey = thiskeyId;
	}

	@Transient
	@Override
	public abstract String getSubtype();
}

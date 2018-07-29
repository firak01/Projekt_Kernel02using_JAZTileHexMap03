package use.thm.persistence.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

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

import custom.zKernel.file.ini.FileIniZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.persistence.interfaces.ITroopArmyVariantTHM;
import use.thm.persistence.interfaces.ITroopVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.model.Immutabletext.EnumImmutabletext;
import base.reflection.ReflectionUtil;
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
	private Float fltHealtInitial;
	private String sImageUrl;
	
	//Blob selbst funtkioniert nicht bei dieser SQLIte Datenbank. Alternativer Lösungsversuch Speicherung als Byte-Array
	//Das Bild der Spielsteinvariante (, noch nicht angepasst)
	@Transient
	private byte[] imageInByte;	
	@Transient
	private String sImageInByte;	
	@Transient
	private Long lngImageInByte;
	
	//Das Bild der Spielsteinvariante für Katalog (, entsprechend angepasst)
	@Transient
	private byte[] imageInByteCatalog01;
	@Transient
	private String sImageInByteCatalog01;
	@Transient
	private Long lngImageInByteCatalog01;
	
	@Transient
	private byte[] imageInByteCatalog02;
	@Transient
	private String sImageInByteCatalog02;
	@Transient
	private Long lngImageInByteCatalog02;
	
	@Transient
	private byte[] imageInByteCatalog03;
	@Transient
	private String sImageInByteCatalog03;
	@Transient
	private Long lngImageInByteCatalog03;
	
	//Das Bild der Spielsteinvariante für die Darst	ellung im Dialog (, entsprechend angepasst)
	@Transient
	private byte[] imageInByteDialog01;	
	@Transient
	private String sImageInByteDialog01;	
	@Transient
	private Long lngImageInByteDialog01;

	@Transient
	private byte[] imageInByteDialog02;	
	@Transient
	private String sImageInByteDialog02;	
	@Transient
	private Long lngImageInByteDialog02;
	
	@Transient
	private byte[] imageInByteDialog03;	
	@Transient
	private String sImageInByteDialog03;	
	@Transient
	private Long lngImageInByteDialog03;
	
	//Das Bild der Spielsteinvariante für die Darstellung in der HexMap - Karte (, entsprechend angepasst)
			@Transient
			private byte[] imageInByteHexmap01;	
			@Transient
			private String sImageInByteHexmap01;			
			@Transient
			private Long lngImageInByteHexmap01;
			
			@Transient
			private byte[] imageInByteHexmap02;	
			@Transient
			private String sImageInByteHexmap02;			
			@Transient
			private Long lngImageInByteHexmap02;
	
			@Transient
			private byte[] imageInByteHexmap03;	
			@Transient
			private String sImageInByteHexmap03;			
			@Transient
			private Long lngImageInByteHexmap03;
			
			@Transient
			private byte[] imageInByteHexmap04;	
			@Transient
			private String sImageInByteHexmap04;			
			@Transient
			private Long lngImageInByteHexmap04;
			
			@Transient
			private byte[] imageInByteHexmap05;	
			@Transient
			private String sImageInByteHexmap05;			
			@Transient
			private Long lngImageInByteHexmap05;
			
			@Transient
			private byte[] imageInByteHexmap06;	
			@Transient
			private String sImageInByteHexmap06;			
			@Transient
			private Long lngImageInByteHexmap06;
			
		
		//Das Bild der Spielsteinvariante für die Darstellung beim Ziehen über die HEXMAP (, entsprechend angepasst)
				@Transient
				private byte[] imageInByteDrag01;				
				@Transient
				private String sImageInByteDrag01;				
				@Transient
				private Long lngImageInByteDrag01;
				
				@Transient
				private byte[] imageInByteDrag02;				
				@Transient
				private String sImageInByteDrag02;				
				@Transient
				private Long lngImageInByteDrag02;
				
				@Transient
				private byte[] imageInByteDrag03;				
				@Transient
				private String sImageInByteDrag03;				
				@Transient
				private Long lngImageInByteDrag03;
				
				@Transient
				private byte[] imageInByteDrag04;				
				@Transient
				private String sImageInByteDrag04;				
				@Transient
				private Long lngImageInByteDrag04;
				
				@Transient
				private byte[] imageInByteDrag05;				
				@Transient
				private String sImageInByteDrag05;				
				@Transient
				private Long lngImageInByteDrag05;
				
				@Transient
				private byte[] imageInByteDrag06;				
				@Transient
				private String sImageInByteDrag06;				
				@Transient
				private Long lngImageInByteDrag06;
				
				
	//... und weitere Eigenschaften.
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public TroopVariant(){		
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
	 }
	 	 
	 //Konstruktor, an den alles übergeben wird. Wg. "Immutable" gibt es keine 'public' Setter.
	 public TroopVariant(int iKey, String sUniquetext, String sCategorytext, int iMapMoveRange, float fHealthInital, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
		 
		 this.setThiskey(Long.valueOf(iKey));
		 this.setUniquetext(sUniquetext);
		 this.setCategorytext(sCategorytext);
		 this.setMapMoveRange(Integer.valueOf(iMapMoveRange));
		 this.setHealthInitial(Float.valueOf(fHealthInital));
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
			this.setImage(imageInByte);			
			long lngFileSize = imageInByte.length; //Diese Infos braucht man, um das Bild wieder auszulesen. Oder?
			this.setImageLength(lngFileSize);
			this.setImageName(sTileIconName);
			
			//+++++++++++++++++++++++++++++++++++++++
			String sSubtype = this.getSubtype();
			System.out.println(ReflectCodeZZZ.getClassCurrentName() +": Erzeuge Bilder für einen Subtype: '" + sSubtype + "'");

			BufferedImage objBufferedImageOriginalUsed = null;
			boolean bImageWasFlipped=false;
			
			//0. Die Schiffsbilder sind anders herum gedreht als die Army-Bilder. Darum hier versuchen diese zu spiegeln.
			//Diese Spiegelung vor allen anderen Bildverarbeitungen machen. Nicht wg. der Performance (da wäre anderes besser), sondern wg. des Ergebnisses, das überall weiterverwendet werden muss.
			if(sSubtype.equalsIgnoreCase("FL")){
				objBufferedImageOriginalUsed = UIHelper.flipImageVertically(objBufferedImageOriginal);
				bImageWasFlipped=true;	//Merke: wenn das Bild vertikal gedreht wird, muss man auch die andere Seite ausschneiden, sonst bekommt man z.B. bei einem Schiff nur das Heck statt dem Bug.
			}else{
				objBufferedImageOriginalUsed = objBufferedImageOriginal;
			}
			
			
			//##############################################################################################################
			//BILDER IN VERSCHIEDENEN ZOOMFAKTOREN
			//A) GUI-ZOOMFAKTOR
			
			//20180711 Ziel ist: Hole einen "Zoomwert" und rechne den festen Wert damit um.... in der ini-Konfiguration...
			//...........    so dass an dieser Stelle im Code nichts geändert werden muss.			
			sModuleAlias = "THM";//this.getModuleName();
			sProgramAlias = "THM"; //this.getProgramName();			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
			
			HashMap<String,String>hmZoomFactorGui=ApplicationSingletonTHM.getInstance().getHashMapZoomFactorGui(sModuleAlias, sProgramAlias);
			Set<String>setZoomFactorAliasGUI=hmZoomFactorGui.keySet();
			for(String sZoomFactorAlias : setZoomFactorAliasGUI){
				String sZoomFactor = hmZoomFactorGui.get(sZoomFactorAlias); //Merke: Dieser ZoomFaktor-Alias ist dann Bestandteil der Spaltennamen für das Bild. Also das Bild mit der passenden Größe.
				
				//Wichtig: Nun eine Variable im Ini-FileZZZ setzen, dann kann mit der Variablen die Größe der Icons - basierend auf der hinterlegten Formel - errechnet werden.
				FileIniZZZ objIni = objKernel.getFileConfigIni();
				objIni.setVariable("GuiZoomFactorUsed", sZoomFactor);
			
			//++++++++++++++++++++++++++++++++++++++++
			//A1. Bild als Katalogeintrag						
				sModuleAlias = "THM";//this.getModuleName();
				sProgramAlias = "CatalogPanel"; //this.getProgramName();			
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");

			//... Größen holen aus der Kernelkonfiguration
			String sIconWidth = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );
			float fIconWidth = StringZZZ.toFloat(sIconWidth);
								
			String sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			float fIconHeight = StringZZZ.toFloat(sIconHeight);
			
			//... Bild bearbeitet als Katalogeintrag
			BufferedImage objBufferdImageResized = UIHelper.resizeImage(objBufferedImageOriginalUsed, fIconWidth, fIconHeight);
			byte[] imageInByteCatalog = UIHelper.getByteArrayFromBufferedImage(objBufferdImageResized,"png");	
			
			
			//TODO GOON 20180725 ---- Rufe hier per Reflection jeweils die Methode für den passenden ZoomAlias auf.
			//Method mtest = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", "getImage01");
			//byte[]byteImage=(byte[]) mtest.invoke(this, null);
			
			//this.setImageCatalog01(imageInByteCatalog);
			String sMethodNameCatalog = "setImageCatalog"+sZoomFactorAlias;
			Method mtestCatalog = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameCatalog);
			mtestCatalog.invoke(this, imageInByteCatalog);
			
						
			long lngFileSizeCatalog = imageInByteCatalog.length;
			//this.setImageCatalogLength01(lngFileSizeCatalog);
			String sMethodNameCatalogLength = "setImageCatalogLength"+sZoomFactorAlias;
			Method mtestCatalogLength = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameCatalogLength);
			mtestCatalogLength.invoke(this, lngFileSizeCatalog);
			
			String sTileIconNameZoomed = sTileIconName + "_x_" + sZoomFactor;
			//this.setImageCatalogName01(sTileIconNameZoomed);
			String sMethodNameCatalogName = "setImageCatalogName"+sZoomFactorAlias;
			Method mtestCatalogName = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameCatalogName);
			mtestCatalogName.invoke(this, sTileIconNameZoomed);
			
            //++++++++++++++++++++++++++++++++++++++++++++++++++
			//A2. Bild für das Öffnen der Detailangaben in einer Dialogbox
			sModuleAlias = "THM";
			sProgramAlias = "TileDetailDialog";
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
			fIconWidth = StringZZZ.toFloat(sIconWidth);
							
			sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			fIconHeight = StringZZZ.toFloat(sIconHeight);
						
			//... Bild bearbeitet als Dialogeintrag
			BufferedImage objBufferdImage4DialogResized = UIHelper.resizeImage(objBufferedImageOriginalUsed, fIconWidth, fIconHeight);
			byte[] imageInByteDialog = UIHelper.getByteArrayFromBufferedImage(objBufferdImage4DialogResized,"png");	

			//this.setImageDialog01(imageInByteDialog);		
			String sMethodNameDialog = "setImageDialog"+sZoomFactorAlias;
			Method mtestDialog = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDialog);
			mtestDialog.invoke(this, imageInByteDialog);
			
			long lngFileSizeDialog = imageInByteDialog.length;
			//this.setImageDialogLength01(lngFileSizeDialog);
			String sMethodNameDialogLength = "setImageDialogLength"+sZoomFactorAlias;
			Method mtestDialogLength = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDialogLength);
			mtestDialogLength.invoke(this, lngFileSizeDialog);
		
			//String sTileIconNameZoomed = sTileIconName + "_x_" + sZoomFactor;
			//this.setImageDialogName01(sTileIconNameZoomed);
			String sMethodNameDialogName = "setImageDialogName"+sZoomFactorAlias;
			Method mtestDialogName = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDialogName);
			mtestDialogName.invoke(this, sTileIconNameZoomed);
			
			
		 }//end for String sZoomFactorAlias : setZoomFactorAliasGUI){
			
			//####################################################################
			//####################################################################################################
			//+++++++++++++++++++++++++
			//A) KARTEN-ZOOMFAKTOR		
			sModuleAlias = "THM";					 //this.getModuleName();
			sProgramAlias = "HexMapCentral"; //this.getProgramName();			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
			
			//20180711 Ziel ist: Hole einen "Zoomwert" und rechne den festen Wert damit um.... in der ini-Konfiguration...
			//...........    so dass an dieser Stelle im Code nichts geändert werden muss.
			HashMap<String,String>hmZoomFactor=ApplicationSingletonTHM.getInstance().getHashMapZoomFactorMap(sModuleAlias, sProgramAlias);
			Set<String>setZoomFactorAlias=hmZoomFactor.keySet();
			for(String sZoomFactorAlias : setZoomFactorAlias){
			String sZoomFactor = hmZoomFactor.get(sZoomFactorAlias); //Merke: Dieser ZoomFaktor-Alias ist dann Bestandteil der Spaltennamen für das Bild. Also das Bild mit der passenden Größe.
			
			//Wichtig: Nun eine Variable im Ini-FileZZZ setzen, dann kann mit der Variablen die Größe der Icons - basierend auf der hinterlegten Formel - errechnet werden.
			FileIniZZZ objIni = objKernel.getFileConfigIni();
			objIni.setVariable("HexZoomFactorUsed", sZoomFactor);
			
			//+++++++++++++++++++
			//B1. ... Bild bearbeitet für die Darstellung in der Karte (wurde ohne diese Abspeicherung zuvor jedesmal in TileTHM.paintComponent() gemacht. Das "jedes Mal" Berechnen spart man sich nun.
			sModuleAlias = "THM";					 //this.getModuleName();
			sProgramAlias = "HexMapCentral"; //this.getProgramName();		
			
			//... Größen holen aus der Kernelkonfiguration
			String sIconWidth = objKernel.getParameterByProgramAlias(objIni, sModuleAlias, sProgramAlias, "IconWidth" );							
			float fIconWidth = StringZZZ.toFloat(sIconWidth);
									
			String sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
			float fIconHeight = StringZZZ.toFloat(sIconHeight);
						
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
		
			//Hier wird versucht den weissen Rand zu entfernen und es wird ggfs. noch gesondert ausgeschnitten (crop).		
			//Realisierter Ansatz, Farbe zum Transparent machen
			Color color01 = new Color(255,255,255);//weiss
						
			//Image imageTransparent = UIHelperTransparency.makeColorTransparent(objBufferedImageTemp, color01);			
			//ABER: So richtig schick ist diese Lösung nicht... nur leicht besser, als wenn der Weisse Rand in ein anderes HEX-Feld reinragt.
			//Grund dafür war, dass das Bild noch weitaus mehr untransparente Farben hatte, die "annähernd" weiss waren. 
			//Lösungsansatz: Ersetze nun einen Bereich 			
			Image imageTransparent = UIHelperTransparencyRange.transformColorRangeToTransparency(objBufferedImageOriginalUsed, color01, 6, 0, color01);
			BufferedImage objBufferedImageTransparent = UIHelper.toBufferedImage(imageTransparent);
			//!!! Wenn es Army Bilder sind, dann diese noch weiter verkleinern
			BufferedImage objBufferedImageTransparentAndResized = null;			
			//String sSubtype = this.getSubtype(); //Army oder Fleet. Das steht aber nur zur Verfügung in TileTHM.paintComponent() und kommt aus dem DTO. 
			//Hierher verlagert, muss man entweder den SubType "simulieren" oder auf alle möglichen Catagorietexte abprüfen. Es wird also in den Unterklassen der SubType "simuliert".
						
			if(sSubtype.equalsIgnoreCase("AR")){
				if(this.getCategorytext().equalsIgnoreCase("Infantry Unit")){ ////TODO GOON 20180703: Hier soll kein String mehr rein, sondern die ThisKey-Id einer entsprechenden CategoryText Tabelle.
					objBufferedImageTransparentAndResized = UIHelper.cropImageByPoints(objBufferedImageTransparent, 0,50,60,10);	//Schneide das Bild erst aus dem Rahmen aus. Sehr viel vom unteren Rand weg, sehr viel vom linken Rand weg.
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparentAndResized, fIconWidth, fIconHeight);
				}else if(this.getCategorytext().equalsIgnoreCase("Tank Unit")){
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, fIconWidth/1.15f, fIconHeight/1.15f);//Mache das Bild noch kleiner als bei normaler Infanterie.
					
					String sHexZoomFactor = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "HexZoomFactorAliasStart" );			
					int iHexZoomFactor = StringZZZ.toInteger(sHexZoomFactor);
							
					objBufferedImageTransparentAndResized = UIHelper.cropImageByPoints(objBufferedImageTransparentAndResized, 0,(4*iHexZoomFactor), 0, 0);	//Schneide das Bild vom linken Rand aus. Ziel ist es wie bei gespiegelten Schiffen,										
				}else{
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, fIconWidth, fIconHeight);
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

			}else if(sSubtype.equalsIgnoreCase("FL")){				
				if(bImageWasFlipped){ //Merke: wenn das Bild vertikal gedreht wird, muss man auch die andere Seite abschneiden (hier: links), sonst bekommt man nur das Heck statt dem Bug - in das kleine HexFeld-Icon gepresst.
					String sHexZoomFactor = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "HexZoomFactorAliasStart" );			
					int iHexZoomFactor = StringZZZ.toInteger(sHexZoomFactor);						
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, fIconWidth/1.15f, fIconHeight/1.0f);	//Damit es noch nach etwas aussieht.... die Höhe nicht reduzieren.					
					objBufferedImageTransparentAndResized = UIHelper.cropImageByPoints(objBufferedImageTransparentAndResized, 0, (5*iHexZoomFactor), 0, 0);	
				}else{
					objBufferedImageTransparentAndResized = objBufferedImageTransparent;
					objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparentAndResized, (fIconWidth), (fIconHeight));	//Damit es noch nach etwas aussieht.... die Höhe nicht reduzieren.
				}			
			}else{
				objBufferedImageTransparentAndResized = UIHelper.resizeImage(objBufferedImageTransparent, fIconWidth, fIconHeight);		
			}
			
			
			//TODO GOON 20180724: Die Bilddaten in die passende Spalte speichern.
			//Die aufgerufene Methode muss für das Bild den Zoomfaktor im Namen haben. Nur so kommen die Bilddaten in die pasende Spalte 

			byte[] imageInByteHexmap = UIHelper.getByteArrayFromBufferedImage(objBufferedImageTransparentAndResized,"png");			
			//this.setImageHexmap01(imageInByteHexmap);			
			String sMethodNameHexmap = "setImageHexmap"+sZoomFactorAlias;
			Method mtestHexmap = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameHexmap);
			mtestHexmap.invoke(this, imageInByteHexmap);
			
			long lngFileSizeHexmap = imageInByteHexmap.length; //Diese Infos braucht man, um das Bild wieder auszulesen. Oder?			
			//this.setImageHexmapLength01(lngFileSizeHexmap);
			String sMethodNameHexmapLength = "setImageHexmapLength"+sZoomFactorAlias;
			Method mtestHexmapLength = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameHexmapLength);
			mtestHexmapLength.invoke(this, lngFileSizeHexmap);
		
			String sTileIconNameZoomed = sTileIconName + "_x_" + sZoomFactor;
			//this.setImageHexmapName01(sTileIconNameZoomed);
			String sMethodNameHexmapName = "setImageHexmapName"+sZoomFactorAlias;
			Method mtestHexmapName = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameHexmapName);
			mtestHexmapName.invoke(this, sTileIconNameZoomed);
			
			
			//++++++++++++++++++++++++++++++++++
			//B2. Bild für das Ziehen über die Karte (im Glasspane, per GhostPictureAdapter, der bei der Erstellung der Katalogboxen erzeugt wird.)
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
			fIconWidth = StringZZZ.toFloat(sIconWidth);
						
			sIconHeight = objKernel.getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeightOnDrag" );
			fIconHeight = StringZZZ.toFloat(sIconHeight);
			
			
			//... Bild bearbeitet als "Ziehen über das HEXFeld"
			BufferedImage objBufferdImage4DragResized = UIHelper.resizeImage(objBufferedImageOriginalUsed, fIconWidth, fIconHeight);
			byte[] imageInByteDrag = UIHelper.getByteArrayFromBufferedImage(objBufferdImage4DragResized,"png");			
			//this.setImageDrag01(imageInByteDrag);			
			String sMethodNameDrag = "setImageDrag"+sZoomFactorAlias;
			Method mtestDrag = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDrag);
			mtestDrag.invoke(this, imageInByteDrag);
			
			long lngFileSizeDrag = imageInByteDrag.length;			
			//this.setImageDragLength01(lngFileSizeDrag);
			String sMethodNameDragLength = "setImageDragLength"+sZoomFactorAlias;
			Method mtestDragLength = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDragLength);
			mtestDragLength.invoke(this, lngFileSizeDrag);
		
			//String sTileIconNameZoomed = sTileIconName + "_x_" + sZoomFactor;
			//this.setImageDragName01(sTileIconNameZoomed);
			String sMethodNameDragName = "setImageDragName"+sZoomFactorAlias;
			Method mtestDragName = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", sMethodNameDragName);
			mtestDragName.invoke(this, sTileIconNameZoomed);
			
			}//end for String sZoomFactorAlias : setZoomFactorAlias){
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Variante erstellt.");
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
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
	 
	 //TODO GOON: Diese Reichweite dann in eine Oberklasse für alle Spielsteine bringen
	 @Column(name="TILE_MAPMOVE_RANGE", nullable=false)
	 public Integer getMapMoveRange(){
		 return this.intMapMoveRange;
	 }
	 protected void setMapMoveRange(Integer intMapMoveRange){
		 this.intMapMoveRange = intMapMoveRange;				 
	 }
	 
	//TODO GOON: Diese initale "Gesundheit" dann in eine Oberklasse für alle Spielsteine bringen
		 @Column(name="TILE_HEALTH_INITIAL", nullable=false)
		 public Float getHealthInitial(){
			 return this.fltHealtInitial;
		 }
		 protected void setHealthInitial(Float fltHealthInitial){
			 this.fltHealtInitial = fltHealthInitial;				 
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
		@Column(name="image", nullable=false)	
		public  byte[] getImage() {
			return this.imageInByte;
		}		
		public void setImage(byte[] imageBlob) {
			this.imageInByte = imageBlob;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="imageName", nullable=false)
		public String getImageName() {
			return this.sImageInByte;
		}
		public void setImageName(String sFileName) {
			this.sImageInByte = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="imageLength", nullable=false)
		public long getImageLength() {
			return this.lngImageInByte;
		}
		public void setImageLength(long lngFileSize) {
			this.lngImageInByte = lngFileSize;
		}
		
		
		//### DAS BILD FÜR DIE KATLOGAUSWAHL IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)		
		@Column(name="ImageCatalog01", nullable=false)	
		public  byte[] getImageCatalog01() {
			return this.imageInByteCatalog01;
		}		
		public void setImageCatalog01(byte[] imageInByte) {
			this.imageInByteCatalog01 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)		
		@Column(name="ImageCatalog02", nullable=false)	
		public  byte[] getImageCatalog02() {
			return this.imageInByteCatalog02;
		}		
		public void setImageCatalog02(byte[] imageInByte) {
			this.imageInByteCatalog02 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)		
		@Column(name="ImageCatalog03", nullable=false)	
		public  byte[] getImageCatalog03() {
			return this.imageInByteCatalog03;
		}		
		public void setImageCatalog03(byte[] imageInByte) {
			this.imageInByteCatalog03 = imageInByte;
		}
		
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogName01", nullable=false)
		public String getImageCatalogName01() {
			return this.sImageInByteCatalog01;
		}
		public void setImageCatalogName01(String sFileName) {
			this.sImageInByteCatalog01 = sFileName;
		}
				
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogName02", nullable=false)
		public String getImageCatalogName02() {
			return this.sImageInByteCatalog02;
		}
		public void setImageCatalogName02(String sFileName) {
			this.sImageInByteCatalog02 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogName03", nullable=false)
		public String getImageCatalogName03() {
			return this.sImageInByteCatalog03;
		}
		public void setImageCatalogName03(String sFileName) {
			this.sImageInByteCatalog03 = sFileName;
		}
		
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogLength01", nullable=false)
		public long getImageCatalogLength01() {
			return this.lngImageInByteCatalog01;
		}
		public void setImageCatalogLength01(long lngFileSize) {
			this.lngImageInByteCatalog01 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogLength02", nullable=false)
		public long getImageCatalogLength02() {
			return this.lngImageInByteCatalog02;
		}
		public void setImageCatalogLength02(long lngFileSize) {
			this.lngImageInByteCatalog02 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageCatalogLength03", nullable=false)
		public long getImageCatalogLength03() {
			return this.lngImageInByteCatalog03;
		}
		public void setImageCatalogLength03(long lngFileSize) {
			this.lngImageInByteCatalog03 = lngFileSize;
		}
		
		//### DAS BILD FÜR DIE DARSTELLUNG IM DIALOG IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)			
		@Column(name="ImageDialog01", nullable=false)	
		public  byte[] getImageDialog01() {
			return this.imageInByteDialog01;
		}		
		public void setImageDialog01(byte[] imageInByte) {
			this.imageInByteDialog01 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageDialog02", nullable=false)	
		public  byte[] getImageDialog02() {
			return this.imageInByteDialog02;
		}		
		public void setImageDialog02(byte[] imageInByte) {
			this.imageInByteDialog02 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageDialog03", nullable=false)	
		public  byte[] getImageDialog03() {
			return this.imageInByteDialog03;
		}		
		public void setImageDialog03(byte[] imageInByte) {
			this.imageInByteDialog03 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogName01", nullable=false)
		public String getImageDialogName01() {
			return this.sImageInByteDialog01;
		}
		public void setImageDialogName01(String sFileName) {
			this.sImageInByteDialog01 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogName02", nullable=false)
		public String getImageDialogName02() {
			return this.sImageInByteDialog02;
		}
		public void setImageDialogName02(String sFileName) {
			this.sImageInByteDialog02 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogName03", nullable=false)
		public String getImageDialogName03() {
			return this.sImageInByteDialog03;
		}
		public void setImageDialogName03(String sFileName) {
			this.sImageInByteDialog03 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogLength01", nullable=false)
		public long getImageDialogLength01() {
			return this.lngImageInByteDialog01;
		}
		public void setImageDialogLength01(long lngFileSize) {
			this.lngImageInByteDialog01 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogLength02", nullable=false)
		public long getImageDialogLength02() {
			return this.lngImageInByteDialog02;
		}
		public void setImageDialogLength02(long lngFileSize) {
			this.lngImageInByteDialog02 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDialogLength03", nullable=false)
		public long getImageDialogLength03() {
			return this.lngImageInByteDialog03;
		}
		public void setImageDialogLength03(long lngFileSize) {
			this.lngImageInByteDialog03 = lngFileSize;
		}
		
		//### DAS BILD FÜR DIE DARSTELLUNG IN DER KARTE IN DER DATENBANK ABSPEICHERN. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap01", nullable=false)	
		public  byte[] getImageHexmap01() {
			return this.imageInByteHexmap01;
		}		
		public void setImageHexmap01(byte[] imageInByte) {
			this.imageInByteHexmap01 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap02", nullable=false)	
		public  byte[] getImageHexmap02() {
			return this.imageInByteHexmap02;
		}		
		public void setImageHexmap02(byte[] imageInByte) {
			this.imageInByteHexmap02 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap03", nullable=false)	
		public  byte[] getImageHexmap03() {
			return this.imageInByteHexmap03;
		}		
		public void setImageHexmap03(byte[] imageInByte) {
			this.imageInByteHexmap03 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap04", nullable=false)	
		public  byte[] getImageHexmap04() {
			return this.imageInByteHexmap04;
		}		
		public void setImageHexmap04(byte[] imageInByte) {
			this.imageInByteHexmap04 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap05", nullable=false)	
		public  byte[] getImageHexmap05() {
			return this.imageInByteHexmap05;
		}		
		public void setImageHexmap05(byte[] imageInByte) {
			this.imageInByteHexmap05 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)			
		@Column(name="ImageHexmap06", nullable=false)	
		public  byte[] getImageHexmap06() {
			return this.imageInByteHexmap06;
		}		
		public void setImageHexmap06(byte[] imageInByte) {
			this.imageInByteHexmap06 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName01", nullable=false)
		public String getImageHexmapName01() {
			return this.sImageInByteHexmap01;
		}
		public void setImageHexmapName01(String sFileName) {
			this.sImageInByteHexmap01 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName02", nullable=false)
		public String getImageHexmapName02() {
			return this.sImageInByteHexmap02;
		}
		public void setImageHexmapName02(String sFileName) {
			this.sImageInByteHexmap02 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName03", nullable=false)
		public String getImageHexmapName03() {
			return this.sImageInByteHexmap03;
		}
		public void setImageHexmapName03(String sFileName) {
			this.sImageInByteHexmap03 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName04", nullable=false)
		public String getImageHexmapName04() {
			return this.sImageInByteHexmap04;
		}
		public void setImageHexmapName04(String sFileName) {
			this.sImageInByteHexmap04 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName05", nullable=false)
		public String getImageHexmapName05() {
			return this.sImageInByteHexmap05;
		}
		public void setImageHexmapName05(String sFileName) {
			this.sImageInByteHexmap05 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapName06", nullable=false)
		public String getImageHexmapName06() {
			return this.sImageInByteHexmap06;
		}
		public void setImageHexmapName06(String sFileName) {
			this.sImageInByteHexmap06 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength01", nullable=false)
		public long getImageHexmapLength01() {
			return this.lngImageInByteHexmap01;
		}
		public void setImageHexmapLength01(long lngFileSize) {
			this.lngImageInByteHexmap01 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength02", nullable=false)
		public long getImageHexmapLength02() {
			return this.lngImageInByteHexmap02;
		}
		public void setImageHexmapLength02(long lngFileSize) {
			this.lngImageInByteHexmap02 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength03", nullable=false)
		public long getImageHexmapLength03() {
			return this.lngImageInByteHexmap03;
		}
		public void setImageHexmapLength03(long lngFileSize) {
			this.lngImageInByteHexmap03 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength04", nullable=false)
		public long getImageHexmapLength04() {
			return this.lngImageInByteHexmap04;
		}
		public void setImageHexmapLength04(long lngFileSize) {
			this.lngImageInByteHexmap04 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength05", nullable=false)
		public long getImageHexmapLength05() {
			return this.lngImageInByteHexmap05;
		}
		public void setImageHexmapLength05(long lngFileSize) {
			this.lngImageInByteHexmap05 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageHexmapLength06", nullable=false)
		public long getImageHexmapLength06() {
			return this.lngImageInByteHexmap06;
		}
		public void setImageHexmapLength06(long lngFileSize) {
			this.lngImageInByteHexmap06 = lngFileSize;
		}

		//### DAS BILD FÜR DIE DARSTELLUNG BEIM ZIEHEN ÜBER DIE HEXKARTE. EIN BILD IST PFLICHT !
	 	@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag01", nullable=false)	
		public  byte[] getImageDrag01() {
			return this.imageInByteDrag01;
		}		
		public void setImageDrag01(byte[] imageInByte) {
			this.imageInByteDrag01 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag02", nullable=false)	
		public  byte[] getImageDrag02() {
			return this.imageInByteDrag02;
		}		
		public void setImageDrag02(byte[] imageInByte) {
			this.imageInByteDrag02 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag03", nullable=false)	
		public  byte[] getImageDrag03() {
			return this.imageInByteDrag03;
		}		
		public void setImageDrag03(byte[] imageInByte) {
			this.imageInByteDrag03 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag04", nullable=false)	
		public  byte[] getImageDrag04() {
			return this.imageInByteDrag04;
		}		
		public void setImageDrag04(byte[] imageInByte) {
			this.imageInByteDrag04 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag05", nullable=false)	
		public  byte[] getImageDrag05() {
			return this.imageInByteDrag05;
		}		
		public void setImageDrag05(byte[] imageInByte) {
			this.imageInByteDrag05 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)	
		@Column(name="ImageDrag06", nullable=false)	
		public  byte[] getImageDrag06() {
			return this.imageInByteDrag06;
		}		
		public void setImageDrag06(byte[] imageInByte) {
			this.imageInByteDrag06 = imageInByte;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName01", nullable=false)
		public String getImageDragName01() {
			return this.sImageInByteDrag01;
		}
		public void setImageDragName01(String sFileName) {
			this.sImageInByteDrag01 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName02", nullable=false)
		public String getImageDragName02() {
			return this.sImageInByteDrag02;
		}
		public void setImageDragName02(String sFileName) {
			this.sImageInByteDrag02 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName03", nullable=false)
		public String getImageDragName03() {
			return this.sImageInByteDrag03;
		}
		public void setImageDragName03(String sFileName) {
			this.sImageInByteDrag03 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName04", nullable=false)
		public String getImageDragName04() {
			return this.sImageInByteDrag04;
		}
		public void setImageDragName04(String sFileName) {
			this.sImageInByteDrag04 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName05", nullable=false)
		public String getImageDragName05() {
			return this.sImageInByteDrag05;
		}
		public void setImageDragName05(String sFileName) {
			this.sImageInByteDrag05 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragName06", nullable=false)
		public String getImageDragName06() {
			return this.sImageInByteDrag06;
		}
		public void setImageDragName06(String sFileName) {
			this.sImageInByteDrag06 = sFileName;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength01", nullable=false)
		public long getImageDragLength01() {
			return this.lngImageInByteDrag01;
		}
		public void setImageDragLength01(long lngFileSize) {
			this.lngImageInByteDrag01 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength02", nullable=false)
		public long getImageDragLength02() {
			return this.lngImageInByteDrag02;
		}
		public void setImageDragLength02(long lngFileSize) {
			this.lngImageInByteDrag02 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength03", nullable=false)
		public long getImageDragLength03() {
			return this.lngImageInByteDrag03;
		}
		public void setImageDragLength03(long lngFileSize) {
			this.lngImageInByteDrag03 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength04", nullable=false)
		public long getImageDragLength04() {
			return this.lngImageInByteDrag04;
		}
		public void setImageDragLength04(long lngFileSize) {
			this.lngImageInByteDrag04 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength05", nullable=false)
		public long getImageDragLength05() {
			return this.lngImageInByteDrag05;
		}
		public void setImageDragLength05(long lngFileSize) {
			this.lngImageInByteDrag05 = lngFileSize;
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="ImageDragLength06", nullable=false)
		public long getImageDragLength06() {
			return this.lngImageInByteDrag06;
		}
		public void setImageDragLength06(long lngFileSize) {
			this.lngImageInByteDrag06 = lngFileSize;
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

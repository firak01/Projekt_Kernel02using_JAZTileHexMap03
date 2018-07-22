package tryout.zBasic.persistence.dao;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.hibernate.Session;

import base.reflection.ReflectionUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.ApplicationTHM;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopVariant;

public class TryoutReadBinaryImageForVariant {

	/** Hierdurch wird deutlich, dass eine STRING-WERT Spalte 
	 *  durchaus anders ist als eine INTEGER-WERT Spalte
	 * @param args
	 */
	public static void main(String[] args) {
		TryoutReadBinaryImageForVariant tryout = new TryoutReadBinaryImageForVariant();
		tryout.start();
	}
	
	public TryoutReadBinaryImageForVariant() {
	}
	
	public void start() {
		/**
		Lies für eine Variante die gespeicherten Bilder aus
		und zeig diese in einer Dialogbox an.
		 */
		main:{
		try {	
			
			//### Grundlage ist ein Download-Verzeichnis.
			ApplicationSingletonTHM appl = ApplicationSingletonTHM.getInstance();
			String sBaseDirectory = appl.getBaseDirectoryStringForDownload();
			File objDir = new File(sBaseDirectory);
			if(!objDir.exists()){
				String stemp = "Konfiguriertes Download-Verzeichnis existiert nicht: '" + sBaseDirectory + "'";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
				ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_CONFIGURATION_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;		
			}
			
			
			//#####
			//Irgendwie eine Thiskey.id übergeben, vielleicht per dialog?
			//TODO: Vielleicht auch alle Varianten durchgehen, abhängig vom Kartenfeld		
//			List<TroopVariant> listaHex = this.searchTroopVariant("EINS", "2", "1");
//			System.out.println("Anzahl gefundener Felder: " + listaHex.size());
			//Long lngThiskey = new Long(22);//Für TroopFleetVariant
			Long lngThiskey = new Long(12);//Für TroopArmyVariant
						
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance();
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
									
			//Merke: Auf dem DEV15VM "Rechner gibt es ein Tryout für Binary Images. Dort wird so verfahren...
			//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.		
			//EntityManager em = objContextHibernate.getEntityManager("TryoutHibernateBinaryImage001");
			
			//Hier verwenden wir sofort das DAO-Objekt
			TroopArmyVariantDao daoTroopVariant = new TroopArmyVariantDao(objContextHibernate);
		    //String sKeytype = "TROOPFLEETVARIANT";
			String sKeytype = "TROOPARMYVARIANT";
			
			TroopArmyVariant objKey02 = (TroopArmyVariant) daoTroopVariant.searchKey(sKeytype, lngThiskey );
			if(objKey02==null){
				System.out.println("2. Abfrage: UNERWARTETES ERGEBNIS. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				break main;
			}else{
				System.out.println("2. Abfrage: Erwartetes Ergebnis. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");					
			}		
			
			String sCategorytext = objKey02.getCategorytext();
			System.out.println("Categorytext = " + sCategorytext);
		

			//2. Aus der so gefundenen Variante byte[] auslesen und in eine Datei zurück...
			//TODO GOON 20180720: Alle Bildvarianten ermitteln, irgendwie genierisch über den Methodennamen und dann eine Ziffer angehängt, oder so....
			//20180721: COOLER LÖSUNGSANSATZ: Per Reflection 
			Method mtest = ReflectionUtil.findMethodForMethodName("use.thm.persistence.model.TroopArmyVariant", "getImage01");
			byte[]byteImage=(byte[]) mtest.invoke(objKey02, null);
			
			//for(HexCell objCell : listaHex) {
				//byte[] byteImage = objKey02.getImage01();
				if(byteImage!=null) {
					BufferedImage objBufferedImage = ImageIO.read(new ByteArrayInputStream(byteImage));
					ImageIcon objImageIconReturn = new ImageIcon(objBufferedImage);
					//JOptionPane.showMessageDialog(null, "Images saved successfully!","Successfull",JOptionPane.INFORMATION_MESSAGE); 
					
					
					 JDialog dialog = new JDialog();     
	                 dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	                 dialog.setTitle("Image Loading Demo");

	                 dialog.add(new JLabel(objImageIconReturn)); //Das wäre, wenn man es direkt aus der Datei liest: new ImageIcon(ImageIO.read(getClass().getResourceAsStream(IMAGE_URL)))));

	                 dialog.pack();
	                 dialog.setLocationByPlatform(true);
	                 dialog.setVisible(true);
				}
			//}
	

		

//		} catch (IOException e) {		
//			e.printStackTrace();
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}//end main:
	}
	
	/** Hole das HexFeld per HQL.
	 *  Merke: Hier wird eine Hibernate Query verwendet, im Gegensatz zu javax.persistence.Query
	 * @param sMapAlias
	 * @param sX
	 * @param sY
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public List<TroopVariant> searchTroopVariant(String sMapAlias, String sX, String sY) throws ExceptionZZZ{
		List<TroopVariant> listReturn = new ArrayList<TroopVariant>();
		List<Troop> listAtPosition = new ArrayList<Troop>();
		
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance();
				
		//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.		
		//EntityManager em = objContextHibernate.getEntityManager("TryoutHibernateBinaryImage001");
			
		
		/*++++++++++++++*/
		//Erzeugen der Entities		
		Session session = objContextHibernate.getSession();
		
		
		
//		select mate
//		from Cat as cat
//		    inner join cat.mate as mate
		    
		//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
		//String sHql = "SELECT id from Tile as tableTile";								
		//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
		
		//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
		//Session session = this.getSession();
		//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
		//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
		
		//Also über die HEXCELL gehen...
		//JA, das liefert die HEXCELL-Objekte zurück
		//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
						
		//JA, das liefert die CellId-Objekte der Hexcell zurück
		//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
		
		//JA, das liefert die Alias Map-Werte zurück
		//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
		
		//DARAUS VERSUCHEN DIE ABFRAGE ZU BAUEN....
		//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");
		
			
		//JA, das funktioniert
		//Merke: DAs hotl im TileHexMap Projekt Spielstein-Objekte
		//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias");
		//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");

		//Merke: Das holt im TileHexMap Projekt Truppen-Objekte		
		//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
		Query query = session.createQuery("from Troop as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
		query.setString("mapAlias", sMapAlias);
		query.setString("mapX", sX);
		query.setString("mapY", sY);
		
		//Hole eine HexCell
		//Query query = session.createQuery("from HexCell as tableHex where tableHex.id.mapAlias = :mapAlias AND tableHex.mapX = :mapX AND tableHexmapY = :mapY");		
//		Query query = session.createQuery("from HexCell as tableHex where tableHex.mapAlias = :mapAlias AND tableHex.mapX = :mapX AND tableHex.mapY = :mapY");		
//		query.setString("mapAlias", sMapAlias);
//		query.setString("mapX", sX);
//		query.setString("mapY", sY);
		
		//Object objResult = query.uniqueResult(); //Das sind aber ggfs. mehrere Werte		
		listReturn = query.list(); 
		
		//TODO GOONMIT DER IDEE
		//Aus der Liste der Troops in dem Hexfeld nun die jeweilige Variante holen...
				
		
		return listReturn;
	}
}

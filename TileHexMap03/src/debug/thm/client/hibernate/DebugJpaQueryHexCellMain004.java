package debug.thm.client.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import basic.zBasic.ExceptionZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellType;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;

public class DebugJpaQueryHexCellMain004 {

	public static void main(String[] args) {
		HibernateContextProviderSingletonTHM objContextHibernate;
		try {
			//objContextHibernate = new HibernateContextProviderSingletonTHM();
			objContextHibernate = HibernateContextProviderSingletonTHM.getInstance();
		
		//TEST: SQL ABFRAGE...
		//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.		
		EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
		Query objQuery = em.createQuery("SELECT MAX(c.id.sMapX) FROM HexCell c"); //Voraussetzung, das so überhaupt ein Resultset zurückgeliefert wird, ist, das AreaType als BLOB gespeichert wird.
		//NEIN, keine Property Query objQuery = em.createQuery("SELECT c.getMapX FROM HexCell c");
		//Query objQuery = em.createQuery("SELECT c.id FROM HexCell c");  //Kein Fehler aber nur mit BLOB bei AreaType Enumeration, gibt es Ergebnis
		//Query objQuery = em.createQuery("SELECT c FROM HexCell c");

		List objResult = objQuery.getResultList();
		int max = -1;
		for(Object obj : objResult){
			System.out.println("obj.class: " + obj.getClass().getName());
			String sId = (String) obj;
			System.out.println("Max(x) : " + sId); //Es wird 9 zurückgeliefert, ABER: das ist nur String mäßig der höhere Wert.
		}
		
		
		//Buch Seite 61: .find() anwenden auf einen zusammengesetzten Schlüssel
		CellId objId = new CellId("EINS", "29","9");
		Object objResultFind = em.find(HexCell.class, objId);
		if(objResultFind==null){
			System.out.println("Kein Objekt gefunden.");
		}else{
			System.out.println("objFound.class: " +  objResultFind.getClass().getName());
			AreaCell objCellFound = (AreaCell) objResultFind;
			System.out.println("Der Typ dieser Zelle ist: " + objCellFound.getAreaTypeObject().name());
			System.out.println("Die Beschreibung für den Zelltyp ist: " + objCellFound.getAreaType().toString());
		}
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end main

}//end class

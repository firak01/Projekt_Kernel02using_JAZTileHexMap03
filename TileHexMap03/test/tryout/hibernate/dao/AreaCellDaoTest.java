package tryout.hibernate.dao;

import java.util.EnumSet;
import java.util.Set;

import javax.persistence.EntityManager;

import custom.zUtil.io.FileZZZ;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.persistence.jdbc.UrlLogicZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;

public class AreaCellDaoTest extends TestCase{
	 private AreaCellDao objDaoTest = null;
	 private  HibernateContextProviderSingletonTHM objContextHibernate = null;
	 
	    protected void setUp(){
	      
		try {			
	    	IKernelZZZ objKernel = new KernelZZZ("THM", "01", "", "ZKernelConfigTileHexMap02Client.ini", (String[]) null);
	    	//HibernateContextProviderSingletonTHM objContextHibernate = new HibernateContextProviderSingletonTHM(objKernel);
	    	HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
	    	this.objContextHibernate = objContextHibernate;
	    	
	    	//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.			
			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);
			if(bDbExists){
				System.out.println("Datenbank existiert als Datei.");
				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert.				
			}else{
				System.out.println("Datenbank exisitert nicht als Datei");
				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
				
				//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.	
				//Wichtig: Das darf erst NACH dem Überprüfen auf die Datenbankexistenz passieren, da hierdurch die Datei erzeugt wird (wenn auch noch ohne Tabellen)
				EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
				
				//TODO GOON: Lege etwas zum Test an.
				
			}
	    	
			//### Das spezielle DAO Testobjekt			
			objDaoTest = new AreaCellDao(this.objContextHibernate);
				

		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		} 
		
		
	}//END setup
	    
	    public void testFlagZ(){
			try{
				boolean  bExists = objDaoTest.proofFlagZExists("NIXDA");
				assertFalse("Object should NOT have FlagZ 'NIXDA'",bExists);
				
				boolean bSetted = false;
				try{
					bSetted = objDaoTest.setFlagZ("NIXDA", true);
					assertFalse("Setting an unavailable FLAGZ 'NIXDA' should return false",bSetted);
				} catch (ExceptionZZZ ez) {
					fail("Setting an unavailable FLAGZ should NOT throw an error.");		
				}
				
				//TestKonfiguration prüfen
				boolean bFlagValue = objDaoTest.getFlag("init");
				assertFalse(bFlagValue); //Nun wäre init falsch		
				
				
				AreaCellDao objDaoInit = new AreaCellDao();
				bFlagValue = objDaoInit.getFlag("init"); 
				assertTrue(bFlagValue);
				
				bFlagValue = objDaoTest.getFlag("Nixda"); 
				assertFalse(bFlagValue);
																
			} catch (ExceptionZZZ ez) {
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
		}
	    
	    public void testCount(){
//			try{

			    int iCount = objDaoTest.count();		
			    System.out.println("Anzahl der AreaCell in der Datenbank = '" + iCount + "'");
				assertTrue("Mindestens 1 AreaCell in der Datenbank erwartet", iCount >= 1);
				
//			} catch (ExceptionZZZ ez) {
//				fail("Method throws an exception." + ez.getMessageLast());
//			} 
	    }
	    
	   
	}//end class
	



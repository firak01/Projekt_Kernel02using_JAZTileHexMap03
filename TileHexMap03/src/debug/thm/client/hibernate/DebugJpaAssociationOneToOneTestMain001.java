package debug.thm.client.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import debug.thm.persistence.hibernate.HibernateContextProviderTHM;
import debug.thm.persistence.model.association001.AssociationTargetTester;
import debug.thm.persistence.model.association001.AssociationTargetTesterAutoKey;
import debug.thm.persistence.model.association001.AssociationTester;
import debug.thm.persistence.model.sequence001.SequenceTester;


public class DebugJpaAssociationOneToOneTestMain001 extends KernelUseObjectZZZ {
	public DebugJpaAssociationOneToOneTestMain001(){
    	try {	
	    	//Kernel Objekt
	    	KernelZZZ objKernel  = new KernelZZZ("XXX", "01", "", "ZKernelConfigDebugPersistence.ini",(String[]) null);
			this.setKernelObject(objKernel);
    	} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void doit(){
    	try {			    							
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.
    		HibernateContextProviderTHM objContextHibernate = new HibernateContextProviderTHM(this.getKernelObject());
			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);											
			if(bDbExists){
				System.out.println("Datenbank existiert als Datei.");
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.				
							
				
			}else{
				//Fall: Datenbank existiert noch nicht
				System.out.println("Datenbank existiert nicht als Datei");
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
			
	
			}//end if bDbExists
			
			Session session = objContextHibernate.getSession();
			
			//Vorbereiten der Wertübergabe an die Datenbank
			session.beginTransaction();
						

			//########### AUTO KEY
			AssociationTargetTesterAutoKey[] objaTargetAutoKey = new AssociationTargetTesterAutoKey[10];
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = new AssociationTargetTesterAutoKey("Wert mit AutoKey");				
				System.out.println("Target AUTOKEY- Objekt  erstellt.");
				objaTargetAutoKey[icount]=objAssociationTargetAutoKeyTester;
			}			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = objaTargetAutoKey[icount];		
				session.save(objAssociationTargetAutoKeyTester);
				System.out.println("Target Objekt (AUTOKEY) gespeichert");
			}
			
		   //############## MANUAL KEY
			AssociationTargetTester objAssociationTargetTester = new AssociationTargetTester("MANUAL_EINS", "Erster Wert");				
			System.out.println("Target Objekt  (MANUALKEY) erstellt.");
			session.save(objAssociationTargetTester);
			System.out.println("Target Objekt (MANUALKEY) gespeichert");
						
			//##########  HAUPTOBJEKT UND ZUORDNUNG
			AssociationTester objAssociationTester = new AssociationTester("Erster  Wert");				
			System.out.println("Erstes (HAUPT) Objekt erstellt.");
			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = objaTargetAutoKey[icount];		
				objAssociationTester.setTargetAutoKey(objAssociationTargetAutoKeyTester);
				System.out.println("Objekt (AUTOKEY) dem HAUPT Objekt zugeordnet.");
			}
			//ERGEBNIS: WEGEN 1:1 Zuordnung wird nur das 10. Objekt in der Tabelle gespeichert sein!!!
			
			
			objAssociationTester.setTarget(objAssociationTargetTester);
			System.out.println("Objekt (MANUAL KEY) dem HAUPT Objekt zugeordnet.");
			
			
			session.save(objAssociationTester);
			System.out.println("Objekt (HAUPT) gespeichert");
			
			//####################
			session.getTransaction().commit();
			System.out.println("commit erfolgt");
			session.close();
			
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public static void main(String[] args) {
		DebugJpaAssociationOneToOneTestMain001 objMain = new DebugJpaAssociationOneToOneTestMain001();
		objMain.doit();		
	}
		

}//end class

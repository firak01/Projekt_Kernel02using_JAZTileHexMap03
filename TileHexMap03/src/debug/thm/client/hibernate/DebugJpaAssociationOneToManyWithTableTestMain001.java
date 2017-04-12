package debug.thm.client.hibernate;

import java.util.Set;

import org.hibernate.Session;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import debug.thm.persistence.dao.association003.AssociationTesterDao;
import debug.thm.persistence.hibernate.HibernateContextProviderAssociationOneToManyWithTableXXX;
import debug.thm.persistence.model.association003.AssociationTargetTesterAutoKey;
import debug.thm.persistence.model.association003.AssociationTester;

/** 2. Beispiel für @OneToOne Association
 *  Als Erweiterung zum vorherigen Beispel wird hier die association selbst in einer eigenen Tabelle gespeichert.
 *  Das hat den Vorteil, dass in der ASSOCIATIO_TESTR Haupttabelle keine Fremdschlüssel Ids mehr vorhanden sien müssen. 
 *  So kann mann z.B. NULL Einträge in solch einer Spalte vermeiden
 * 
 * @author Fritz Lindhauer
 *
 */
public class DebugJpaAssociationOneToManyWithTableTestMain001 extends KernelUseObjectZZZ {
	public static void main(String[] args) {
		DebugJpaAssociationOneToManyWithTableTestMain001 objMain = new DebugJpaAssociationOneToManyWithTableTestMain001();
		objMain.createInitial();		
		
		objMain.readitl();				
	}
	
	//########### Konstruktor
	public DebugJpaAssociationOneToManyWithTableTestMain001(){
    	try {	
	    	//Kernel Objekt
	    	KernelZZZ objKernel  = new KernelZZZ("XXX", "01", "", "ZKernelConfigDebugPersistence.ini",(String[]) null);
			this.setKernelObject(objKernel);
    	} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void createInitial(){
    	main:{
    	try {			    							
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.
    		HibernateContextProviderAssociationOneToManyWithTableXXX objContextHibernate = new HibernateContextProviderAssociationOneToManyWithTableXXX(this.getKernelObject());
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
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = new AssociationTargetTesterAutoKey("x mal " + icount + ". Wert mit AutoKey");				
				System.out.println("Target AUTOKEY- Objekt  erstellt.");
				objaTargetAutoKey[icount]=objAssociationTargetAutoKeyTester;
			}			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = objaTargetAutoKey[icount];		
				session.save(objAssociationTargetAutoKeyTester);
				System.out.println("Target Objekt (AUTOKEY) gespeichert");
			}
			
		
			//##########  ERSTES HAUPTOBJEKT UND ZUORDNUNG
			AssociationTester objAssociationTester = new AssociationTester("Erster  Wert");				
			System.out.println("Erstes (HAUPT) Objekt erstellt.");
			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester = objaTargetAutoKey[icount];		
				//Das wäre bei 1:1 objAssociationTester.setTargetAutoKey(objAssociationTargetAutoKeyTester);
				//Abspeichern in einem Set ist 1:n
				objAssociationTester.getTargetAutoKey().add(objAssociationTargetAutoKeyTester);
				System.out.println("Objekt (AUTOKEY) dem ersten HAUPT Objekt zugeordnet.");
			}
			//ERGEBNIS: WEGEN 1:1 Zuordnung wird nur das 10. Objekt in der Tabelle gespeichert sein!!!
			
			session.save(objAssociationTester);
			System.out.println("Erstes Objekt (HAUPT) gespeichert");
			
			//####### Zweites Target AutoKey
			AssociationTargetTesterAutoKey[] objaTargetAutoKey02 = new AssociationTargetTesterAutoKey[10];
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester02 = new AssociationTargetTesterAutoKey("02 x mal " + icount + ". Wert mit AutoKey");				
				System.out.println("2. Target AUTOKEY- Objekt  erstellt.");
				objaTargetAutoKey02[icount]=objAssociationTargetAutoKeyTester02;
			}			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester02 = objaTargetAutoKey02[icount];		
				session.save(objAssociationTargetAutoKeyTester02);
				System.out.println("2. Target Objekt (AUTOKEY) gespeichert");
			}
			
			//##########  ZWEITES HAUPTOBJEKT UND ZUORDNUNG
			//Damit das Klappt muss man die @Id Spaltengenerierung so abändern, das hier kein unique drin auftaucht.
			//Daher hier den Test mit einem zweiten Hauptobjekt, um zu sehen, ob die incrementierung mit dem Generator funktioniert.
			AssociationTester objAssociationTester02 = new AssociationTester("Zweiter  Wert");				
			System.out.println("Zweites (HAUPT) Objekt erstellt.");
			
			for (int icount = 0 ; icount <= 9; icount++){
				AssociationTargetTesterAutoKey objAssociationTargetAutoKeyTester02 = objaTargetAutoKey02[icount];		
				//Das wäre bei 1:1 objAssociationTester.setTargetAutoKey(objAssociationTargetAutoKeyTester);
				//Abspeichern in einem Set ist 1:n
				objAssociationTester02.getTargetAutoKey().add(objAssociationTargetAutoKeyTester02);
				System.out.println("Objekt (2. AUTOKEY) dem zweiten HAUPT Objekt zugeordnet.");
			}
			//ERGEBNIS: WEGEN 1:1 Zuordnung wird nur das 10. Objekt in der Tabelle gespeichert sein!!!
			
			session.save(objAssociationTester02);
			System.out.println("Zweites Objekt (HAUPT) gespeichert");
			
			
			
			//####################
			session.getTransaction().commit();
			System.out.println("commit erfolgt");
			session.close();
			
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } //end main:    
    }		
    
    public void readitl(){
    	main:{
    	try {			    							
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.
    		HibernateContextProviderAssociationOneToManyWithTableXXX objContextHibernate = new HibernateContextProviderAssociationOneToManyWithTableXXX(this.getKernelObject());
			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);											
			if(bDbExists){
				System.out.println("Datenbank existiert als Datei.");
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.				
							
				
			}else{
				//Fall: Datenbank existiert noch nicht
				System.out.println("Datenbank existiert nicht als Datei");
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
			
				break main;
			}//end if bDbExists
			
			Session session = objContextHibernate.getSession();
			
			AssociationTesterDao objTesterDao = new AssociationTesterDao(objContextHibernate);
			
			Integer primaryKey = 1;
			
			AssociationTester objTester = objTesterDao.findById(primaryKey);
			if(objTester==null){
				System.out.println("Kein Objekt gefunden");
				break main;
			}else{
				System.out.println("Objekt gefunden");
			}
			
			Set<AssociationTargetTesterAutoKey> objsetTargetAutoKey = objTester.getTargetAutoKey();
			if(objsetTargetAutoKey==null){
				System.out.println("Kein referenziertes Objekt gefunden");
				break main;
			}else{
				System.out.println("Referenziertes Objekt gefunden");
				for(AssociationTargetTesterAutoKey objTargetAutoKey : objsetTargetAutoKey){
					System.out.println("Identifikationsstring: " + objTargetAutoKey.getDummyString());
				}
			}
			
			session.close();
			
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }//end main:
    }
    

}//end class

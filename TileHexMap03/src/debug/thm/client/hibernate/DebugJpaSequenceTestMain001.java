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
import debug.thm.persistence.hibernate.HibernateContextProviderSequenceAssociationXXX;
import debug.thm.persistence.model.sequence001.SequenceTester;


public class DebugJpaSequenceTestMain001 extends KernelUseObjectZZZ {
	    public DebugJpaSequenceTestMain001(){
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
	    		HibernateContextProviderSequenceAssociationXXX objContextHibernate = new HibernateContextProviderSequenceAssociationXXX(this.getKernelObject());
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
				
				SequenceTester[] objaSequenceTester = new SequenceTester[10];
				for(int icount = 0 ; icount <= 9; icount++){
					SequenceTester objSequenceTester = new SequenceTester((icount+1) + ".  Wert");
					objaSequenceTester[icount]=objSequenceTester;
					System.out.println("Objekt erstellt: " + icount);
				}
				
				for(int icount = 0 ; icount <= 9; icount++){
					SequenceTester objSequenceTester = 	objaSequenceTester[icount];
					session.save(objSequenceTester);
					System.out.println("Objekt gespeichert: " + icount);
				}
				
				
				session.getTransaction().commit();
				System.out.println("commit erfolgt");
				session.close();
				
			
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
		public static void main(String[] args) {
			DebugJpaSequenceTestMain001 objMain = new DebugJpaSequenceTestMain001();
			objMain.doit();		
		}
			
	
	}//end class

	

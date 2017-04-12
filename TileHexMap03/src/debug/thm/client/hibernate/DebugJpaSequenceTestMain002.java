package debug.thm.client.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import debug.thm.persistence.hibernate.HibernateContextProviderSequenceAssociation002XXX;
import debug.thm.persistence.model.sequence002.SequenceTester;


/**Anders als in der 001er Version wird hier der EntityManager verwendet.
* Ziel ist es trotzdem den KeyGenarator aus Hibernate weiter nutzen zu können.
 * @author Fritz Lindhauer
 *
 */
public class DebugJpaSequenceTestMain002 extends KernelUseObjectZZZ {	  
	    public DebugJpaSequenceTestMain002(){
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
	    	HibernateContextProviderSequenceAssociation002XXX objContextHibernate = null;
	    	try {			    							
				//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.
	    		objContextHibernate = new HibernateContextProviderSequenceAssociation002XXX(this.getKernelObject());
				boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);											
				if(bDbExists){
					System.out.println("Datenbank existiert als Datei.");
					objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.																	
				}else{
					//Fall: Datenbank existiert noch nicht
					System.out.println("Datenbank existiert nicht als Datei");
					objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.						
				}//end if bDbExists
				
				//Session session = objContextHibernate.getSession();
				
				//JPA Weg über den EntityManager
				String sSchemaName = "SequenceAssociation002";//das kommt aus META-INF\persistence.xml
				EntityManager em = objContextHibernate.getEntityManager(sSchemaName);
				
				//Vorbereiten der Wertübergabe an die Datenbank
				//session.beginTransaction();
				em.getTransaction().begin();
				
				SequenceTester[] objaSequenceTester = new SequenceTester[10];
				for(int icount = 0 ; icount <= 9; icount++){
					SequenceTester objSequenceTester = new SequenceTester((icount+1) + ".  Wert");
					objaSequenceTester[icount]=objSequenceTester;
					System.out.println("Objekt erstellt: " + icount);
				}
				
				for(int icount = 0 ; icount <= 9; icount++){
					SequenceTester objSequenceTester = 	objaSequenceTester[icount];
					//session.save(objSequenceTester);
					em.persist(objSequenceTester);
					System.out.println("Objekt gespeichert: " + icount);
				}
				
				
				//session.getTransaction().commit();				
				//session.close();
				
				em.close();				
				System.out.println("commit erfolgt");
			
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	finally {
	            //TM.rollback();
	    		if(objContextHibernate!=null){
		    		EntityManagerFactory emf = objContextHibernate.getEntityManagerFactory();
		            if(emf!=null)emf.close();
	    		}
	        }
	    }
	    
		public static void main(String[] args) {
			DebugJpaSequenceTestMain002 objMain = new DebugJpaSequenceTestMain002();
			objMain.doit();		
		}
			
	
	}//end class

	

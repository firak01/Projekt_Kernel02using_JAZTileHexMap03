package tryout.hibernate;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;

import use.thm.client.hibernate.HibernateContextProviderTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.persistence.jdbc.JdbcDatabaseTypeZZZ;
import basic.zBasic.util.persistence.jdbc.JdbcDriverClassTypeZZZ;
import basic.zBasic.util.persistence.jdbc.UrlLogicZZZ;

import org.hibernate.cfg.Configuration;

public class SQLiteUtilZZZ  extends ObjectZZZ{
	private SQLiteUtilZZZ(){
		//Zum Verstecken des Konstruktors
	}
	public static boolean databaseFileExists(String sFilePath) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			bReturn = FileEasyZZZ.exists(sFilePath);
		}//end main:		
		return bReturn;
	}
	public static boolean databaseFileExists(HibernateContextProviderTHM objHibernateContext) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objHibernateContext == null){
				ExceptionZZZ ez  = new ExceptionZZZ("HibernateContextProviderTHM", iERROR_PARAMETER_MISSING, FileEasyZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			Configuration objConfig = objHibernateContext.getConfiguration();
			if(objConfig==null){
				ExceptionZZZ ez  = new ExceptionZZZ("HibernateContextProviderTHM enthält kein Configuration Objekt", iERROR_PARAMETER_VALUE, FileEasyZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sUrl = objConfig.getProperty("hibernate.connection.url");
			if(StringZZZ.isEmpty(sUrl)){
				ExceptionZZZ ez  = new ExceptionZZZ("Leere URL aus 'HibernateContextProviderTHM Configuration Objekt'", iERROR_PARAMETER_VALUE, FileEasyZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			//Aus folgenden Konfigurations-String den Dateipfad holen
			//.setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
			//Merke: Das jdbc: muss als Protokoll erkannt werden. jdbc:sqlite::memory wäre auch eine alternative, die erlaubt ist. 
			//                                                                       und jdbc:mysql://localhost .... wäre dann auch möglich.
				String sDatabaseUrl = UrlLogicZZZ.getUrlWithoutProtocol(sUrl); 
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": DatabaseUrl="+sDatabaseUrl);

				//Enumeration objEnum = objHtValue.keys(); //code aus KernelFileIniZZZ - hier Enumeration aus HashMap
//				Enumeration objEnum = 
//				while(objEnum.hasMoreElements()){
//					String sProperty = (String) objEnum.nextElement();
//					if(!sProperty.trim().equals("")){
//						String sValue = (String) objHtValue.get(sProperty);
//						
//						//Auch falls der Wert ein Leerstring ist, diesen setzen.
//						this.setPropertyValue(sSection, sProperty, sValue, false);
//					}
//				} //end while
								
				/* so durchläuft man eine Enumeration, EnumSet importieren.
				 * If you don't care about the order this should work:
					Set<Direction> directions = EnumSet.allOf(Direction.class);
					for(Direction direction : directions) {
					    // do stuff
					}

				//Alternative:
				for (Direction  d : Direction.values()) {
			       //your code here   
			    }//ABER: .values() existiert  wohl nur im Compiler, wird also auch nicht vorgeschlagen.

				 */
				
				//TODO Goon: Verwende hier eine Methode aus EnumSetMappedUtitlityZZZ
				//                  .
				Set<JdbcDriverClassTypeZZZ> drivers = EnumSet.allOf(JdbcDriverClassTypeZZZ.class);
				for(JdbcDriverClassTypeZZZ driver : drivers) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				  if(!StringZZZ.isEmpty(driver.getAbbreviation())){
					  if(sDatabaseUrl.startsWith(driver.getAbbreviation())){
						  System.out.println("Bingo einen Treiber gefunden.");
						  sDatabaseUrl = StringZZZ.right(sDatabaseUrl, driver.getAbbreviation() + UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL, true);
						  
						  break;
					  }
				  }
				}
				
				//Vom Rest dann Enumeration Datenbankaliasse durchgehen.
				String sDatabaseFound="";
				Set<JdbcDatabaseTypeZZZ> databases = EnumSet.allOf(JdbcDatabaseTypeZZZ.class);
				for(JdbcDatabaseTypeZZZ database : databases) {
//				   System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Database ALIAS als database.name() from Enumeration="+database.name());
//				   System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Database als database.toString() from Enumeration="+database.toString());
//				   System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Database als database.abbreviation from Enumeration="+database.getAbbreviation());
				   if(!StringZZZ.isEmpty(database.getAbbreviation())){
					   if(sDatabaseUrl.startsWith(database.getAbbreviation())){						  
						  sDatabaseFound=database.getAbbreviation();
						  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Bingo eine Datenbank gefunden '"+ sDatabaseFound +"'");
						  						  
						  sDatabaseUrl = StringZZZ.right(sDatabaseUrl, sDatabaseFound+UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL, true);
						  System.out.println((ReflectCodeZZZ.getPositionCurrent() + ": Datenbank string '" + sDatabaseUrl + "'"));
						  break;
					  }
				   }
				}
				
				//TODO: Wie die Existenz anderer Datenbanken , die per IP Adresse und nicht per einfacher lokaler Datei erreichbar sind prüfen?
				if(sDatabaseFound.equalsIgnoreCase("sqlite")){
					 //Merke: SQLIte Datenbanken könne theoretisch auch InMemory sein.  :memory					
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank gefunden. Prüfe auf in Memory.");
					if(sDatabaseUrl.startsWith(":memory")){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank  in Memory. Existenz vorausgesetzt?");
						bReturn = true;
						break main;
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank NICHT in Memory");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Prüfe Datei Existenz: " + sDatabaseUrl);
						bReturn = FileEasyZZZ.exists(sDatabaseUrl);
					}
				}												
			//}
		}//end main:		
		return bReturn;
	}
	
}

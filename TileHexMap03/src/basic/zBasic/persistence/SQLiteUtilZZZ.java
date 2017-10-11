package basic.zBasic.persistence;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractEnum.EnumSetMappedUtilZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
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
	public static boolean databaseFileExists(IHibernateContextProviderZZZ objHibernateContext) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objHibernateContext == null){
				ExceptionZZZ ez  = new ExceptionZZZ("HibernateContextProviderTHM", iERROR_PARAMETER_MISSING, SQLiteUtilZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			Configuration objConfig = objHibernateContext.getConfiguration();
			if(objConfig==null){
				ExceptionZZZ ez  = new ExceptionZZZ("HibernateContextProviderTHM enthält kein Configuration Objekt", iERROR_PARAMETER_VALUE, SQLiteUtilZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sUrl = objConfig.getProperty("hibernate.connection.url");
			if(StringZZZ.isEmpty(sUrl)){
				ExceptionZZZ ez  = new ExceptionZZZ("Leere URL aus 'HibernateContextProviderTHM Configuration Objekt'", iERROR_PARAMETER_VALUE, SQLiteUtilZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			//Aus folgenden Konfigurations-String den Dateipfad holen
			//.setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
			//Merke: Das jdbc: muss als Protokoll erkannt werden. jdbc:sqlite::memory wäre auch eine alternative, die erlaubt ist. 
			//                                                                       und jdbc:mysql://localhost .... wäre dann auch möglich.
			String sDatabaseUrl = UrlLogicZZZ.getUrlWithoutProtocol(sUrl); 
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": DatabaseUrl="+sDatabaseUrl);
					
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
			
			//Hier ein Beispiel wie man eine Enumeration aus einer Hashmap holt.
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
			
			//Verwende hier eine Methode aus EnumSetMappedUtitlityZZZ				
			EnumSetMappedUtilZZZ objUtil = new EnumSetMappedUtilZZZ(JdbcDriverClassTypeZZZ.class);
			IEnumSetMappedZZZ driverType = objUtil.startsWithAnyAlias_EnumMappedObject(sDatabaseUrl);
			if(driverType!=null){
				String sDescription = driverType.getDescription(); //Die Description soll das sein, was in der URL steht..., darum..
				sDatabaseUrl = StringZZZ.rightback((UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL + sDatabaseUrl), sDescription + UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL); //Damit bei einem Leerstring von sDescription auch passend abgeschnitten wird
			}
							
			//Vom Rest dann Enumeration Datenbankaliasse durchgehen.
			objUtil = new EnumSetMappedUtilZZZ(JdbcDatabaseTypeZZZ.class);				
			IEnumSetMappedZZZ databaseType = objUtil.startsWithAnyAlias_EnumMappedObject(sDatabaseUrl);
			if(databaseType!=null){
				String sDatabaseFound= databaseType.getAbbreviation();
				  sDatabaseUrl = StringZZZ.rightback((UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL + sDatabaseUrl), sDatabaseFound+UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL);
				  System.out.println((ReflectCodeZZZ.getPositionCurrent() + ": DatabasePath = '" + sDatabaseUrl + "'"));
							
				//TODO: Wie die Existenz anderer Datenbanken , die per IP Adresse und nicht per einfacher lokaler Datei erreichbar sind prüfen?
				if(databaseType.getAbbreviation().equalsIgnoreCase("sqlite")){
					 //Merke: SQLIte Datenbanken könne theoretisch auch InMemory sein.  :memory					
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank gefunden. Prüfe auf in Memory.");
					if(sDatabaseUrl.startsWith(":memory")){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank  in Memory. Existenz vorausgesetzt?");
						bReturn = true;
						break main;
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": SQLITE Datenbank NICHT in Memory sondern als lokale Datei.");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Prüfe Datei Existenz: " + sDatabaseUrl);
						bReturn = FileEasyZZZ.exists(sDatabaseUrl);
					}
				}
			}else{
				//databaseType nicht gefunden
				ExceptionZZZ ez  = new ExceptionZZZ("Datenbanktyp nicht gefunden, der im Konfigurationsstring genannt wird: '" + sUrl +"'", iERROR_PARAMETER_VALUE, SQLiteUtilZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}//end if databaseType != null
		}//end main:		
		return bReturn;
	}
	
}

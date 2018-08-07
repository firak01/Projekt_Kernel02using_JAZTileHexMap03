package use.thm.persistence.daoFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import use.thm.persistence.interfaces.IBackendPersistenceFacadeTHM;
import use.thm.persistence.model.Tile;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;

public abstract class TroopVariantDaoFacade extends GeneralDaoFacadeZZZ implements IBackendPersistenceFacadeTHM{
	public TroopVariantDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public abstract String getFacadeType();
	
	public  String computeUniquename(){
		String sReturn = new String("");
		main:{
			String sFacadeType = this.getFacadeType();
			sReturn = TroopVariantDaoFacade.computeUniquename(sFacadeType);			
		}
		return sReturn;
	}
	
	public static String computeUniquename(String sFacadeType){
		String sReturn = new String("");
		main:{
			String sTimestamp = DateTimeZZZ.computeTimestampUniqueString();
			sReturn = sFacadeType + "_" + sTimestamp;
		}//end main:
		return sReturn;
	}
	
	protected boolean makeCreatedDates(Tile objTroopTemp){
		boolean bReturn = false;
		main:{
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			//FGL: TEST 20180215, Probiere das Setzen eines Datum aus, HIS Style
			//Das Klappt. Das Ergebnis ist aber in der SQLite Datenbank ebenfalls nur ein "kryptischer" (d.h. Long Zahl) Timestamp
			Calendar cal = Calendar.getInstance();
			Date objDate = cal.getTime(); //Ist letztendlich nur der Timestamp
			objTroopTemp.setCreatedThisAt(objDate); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
			
			//Ist der Vorteil der HIS-Lösung, dass man beliebige Datumsformate "reinwerfen kann"?
			//Read more: http://www.java67.com/2013/01/how-to-format-date-in-java-simpledateformat-example.html#ixzz58xBTYHvU
			//Date today = new Date();
//			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
//	        String sDate = DATE_FORMAT.format(objDate);
//	        System.out.println("Today in dd-MM-yyyy format : " + sDate);
//	     
//	        //Another Example of formatting Date in Java using SimpleDateFormat
//	        DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
//	        sDate = DATE_FORMAT.format(objDate);
//	        System.out.println("Today in dd/MM/yy pattern : " + sDate);
	     
	        //formatting Date with time information	        
			String sDateFormatAlternative = "dd_MM_yy";
	        SimpleDateFormat DATE_FORMATALTERNATIVE = new SimpleDateFormat(sDateFormatAlternative);
	        String sDateAlternative = DATE_FORMATALTERNATIVE.format(objDate);
	        System.out.println("Today (alternative) in '" + sDateFormatAlternative + "' : " + sDateAlternative);
	        objTroopTemp.setCreatedThisAtString(sDateAlternative); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
			
	        String sDateFormatValid = DateMapping.DATE_FORMAT_SIMPLE_FULL_FGL; //"dd-MM-yy:HH:mm:SS"
	        SimpleDateFormat DATE_FORMATVALID = new SimpleDateFormat(sDateFormatValid);
	        String sDateValid = DATE_FORMATVALID.format(objDate);
	        System.out.println("Today (valid) in '" + sDateFormatValid + "' : " + sDateValid);
	        objTroopTemp.setCreatedThisAtStringValid(sDateValid); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
			
	        //TEST AUF UNGÜLTIGES DATUMSFORMAT
	        String sDateFormatInvalid = "ddMMyy";
	        SimpleDateFormat DATE_FORMATINVALID = new SimpleDateFormat(sDateFormatInvalid);
	        String sDateInValid = DATE_FORMATINVALID.format(objDate);
	        
	        //Merke: Den Fehler muss man wohl beim Commit abfangen, vorher geht wohl nicht.
//	        try{	        
		        //System.out.println("Today (InValid) in '" + sDateFormat + "' : " + sDateInValid);
		        //objTroopTemp.setCreatedThisAtStringValid(sDateInValid); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
		        
//		        ExceptionZZZ ez = new ExceptionZZZ("DAS DATUM IST IN DEM FORMAT UNGUELTIG. DAS HAETTE ABGEFANGEN WERDEN SOLLEN: '" + sDateInValid + "'", IConstantZZZ.iERROR_PROPERTY_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//	        }catch(RuntimeException e){
//	        	System.out.println("Today (InValid) wurde erfolgreich als Fehler abgefangen (Format / Datum): '(" + sDateFormat + "'/'" + sDateInValid + "'");
//	        }
	        
	        //20180314: Wenn schon CustomTypes verwendet werden, dann sollte es unnötig sein überhaupt einen Wert als Eingabeparameter zu übergeben.
	        //          Das aktuelle Datum kann auch hier verwendet werden.
	        //          Merke: Ganz ohne ein Argument in der Methode kann Hibernate diese nicht Mappen
	        //Exception in thread "main" org.hibernate.MappingException: Could not get constructor for org.hibernate.persister.entity.SingleTableEntityPersister
	        System.out.println("Setze createdThis(null), d.h. CustomType sollte aktuelles Datum autmatisch verwenden");
	        objTroopTemp.setCreatedThis(null); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
	        
	        System.out.println("Setze createdThisString(null), d.h. CustomType sollte aktuelles Datum autmatisch verwenden");
	        objTroopTemp.setCreatedThisString(null); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.

	        //Teste die DateMappingCustomTimestampStringAsComment.java Hier Fall: Kommentarstring wird übergeben.
	        System.out.println("Setze createdThisStringComment('TileHexMap ist toll'), d.h. CustomType sollte aktuelles Datum autmatisch verwenden");
	        objTroopTemp.setCreatedThisStringComment("TileHexMap ist toll"); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
			
	        //Teste die DateMappingCustomTimestampStringAsComment.java Hier Fall: Korrektes Datum-Objekt wird übergeben.
	       // objTroopTemp.setCreatedThisStringComment(sDateValid); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
	        
	      //Teste die DateMappingCustomTimestampStringAsComment.java Hier Fall: Korrektes Datum-Objekt wird übergeben, liegt aber ausserhalb des gültigen Wertebereichs.
	       //Calendar cal4invalid = new GregorianCalendar(793,6,8);//Setze ein beliebiges Datum, ausserhalb des Datumsbereichs. 8. Juni 793: Überfall der Wikinger auf das Kloster Lindisfarne....
//	       Calendar cal4invalid = Calendar.getInstance();
//	       cal4invalid.set(793,6,8);//Setze ein beliebiges Datum, ausserhalb des Datumsbereichs. 8. Juni 793: Überfall der Wikinger auf das Kloster Lindisfarne....
//	       SimpleDateFormat DATE_FORMATHISTRORIC = new SimpleDateFormat(DateMapping.DATE_FORMAT_SIMPLE_FULL_HISTORIC_FGL);
//	       Date objDate4invalid = cal4invalid.getTime(); //Ist letztendlich nur der Timestamp
//	       String sDate4invalid=DATE_FORMATHISTRORIC.format(objDate4invalid);
//		   objTroopTemp.setCreatedThisStringComment(sDate4invalid); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
		        	        
	      //Teste die DateMappingCustomTimestampStringAsComment.java Hier Fall: Ungültiges Datum-Objekt wird übergeben.
		  //objTroopTemp.setCreatedThisStringComment(sDateInValid); //Die HIS Lösung, für die zahlreiche andere Klassen (s. Packages in base) und Bibliotheken (u.a. aspectj Tools) eigebunden werden mussten.
		        
	        
			
			bReturn = true;
		}//end main
		return bReturn;				
	}
	
}

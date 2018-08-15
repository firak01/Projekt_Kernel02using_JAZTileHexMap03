package use.thm.persistence.daoFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.interfaces.IBackendPersistenceFacadeTHM;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopVariant;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;

public abstract class TroopVariantDaoFacade extends GeneralDaoFacadeZZZ implements IBackendPersistenceFacadeTHM{
	public TroopVariantDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public abstract String getFacadeType();
	public abstract TroopVariant getEntityUsed();
	public abstract void setEntityUsed(TroopVariant objTroopVariant);
		
	/* (non-Javadoc)
	 * @see use.thm.persistence.interfaces.IBackendPersistenceFacadeTHM#computeUniquename()
	 * 
	 * Anders als bei den erzeugten Spielsteinen ist nicht der Zeitpunkt der Erzeugung, sondern der verwendete Thiskey Bestandteil des Schlüssels
	 * (vgl. TileDaoFacade...)
	 */
	public  String computeUniquename() throws ExceptionZZZ{
		String sReturn = new String("");
		main:{
			String sFacadeType = this.getFacadeType();
			String sThiskeyUsed = this.getThiskeyUsed();
			sReturn = TroopVariantDaoFacade.computeUniquename(sFacadeType, sThiskeyUsed);			
		}
		return sReturn;
	}
	
	public static String computeUniquename(String sFacadeType, String sThiskeyUsed){
		String sReturn = new String("");
		main:{		
			sReturn = sFacadeType + "_" + sThiskeyUsed;
		}//end main:
		return sReturn;
	}
	
	public String getThiskeyUsed() throws ExceptionZZZ{
		String sReturn = new String("");
		main:{
			TroopVariant objTroopVariant = this.getEntityUsed();
			if(objTroopVariant == null){
				ExceptionZZZ ez = new ExceptionZZZ("objTroopVariant", IConstantZZZ.iERROR_PROPERTY_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			sReturn = objTroopVariant.getThiskey().toString();
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

	public boolean fillTroopVariantDto(TroopVariant objTroopVariant, GenericDTO<IBoxDtoAttribute> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopVariantDto(objTroopVariant)  ####################");
			if(objTroopVariant == null){
				objTroopVariant = this.getEntityUsed();
				if(objTroopVariant == null) break main;
			}					
			
			
				//FRAGE: FUNKTIONIERT HIERBEI CALL BY REFERENCE? JA. Es werden nämlich Werte in den Objekten gefüllt.		
				//dto.set(IBoxDtoAttribute.UNIQUENAME, objTroopVariant.getThiskey().toString());
				dto.set(IBoxDtoAttribute.UNIQUENAME, this.computeUniquename());
				dto.set(IBoxDtoAttribute.SUBTYPE,objTroopVariant.getSubtype());
	
				dto.set(IBoxDtoAttribute.VARIANT_IMAGE_URL_STRING,objTroopVariant.getImageUrlString());
					
				//20180630: Nun das Bild ausch direkt als byte[] gespeichert aus der Datenbank holen.
				dto.set(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE,objTroopVariant.getImage());
											
				//Diese sind alle auf den Katalog bezogen, darum nur in den 3 Zoomstufen des GUI
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01,objTroopVariant.getImageCatalogDialog01());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_02,objTroopVariant.getImageCatalogDialog02());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_03,objTroopVariant.getImageCatalogDialog03());
				
				//Diese sind alle auf den Katalog bezogen, darum nur in den 3 Zoomstufen des GUI
				dto.set(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE_01,objTroopVariant.getImageCatalog01());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE_02,objTroopVariant.getImageCatalog02());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE_03,objTroopVariant.getImageCatalog03());
				
				//Diese sind auf die Hexmap bezogen (also vom Katalog in die HexMap ziehen), darum hier auch die 6 Zoomstufen der Hexmap
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_01,objTroopVariant.getImageCatalogDrag01());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_02,objTroopVariant.getImageCatalogDrag02());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_03,objTroopVariant.getImageCatalogDrag03());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_04,objTroopVariant.getImageCatalogDrag04());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_05,objTroopVariant.getImageCatalogDrag05());
				dto.set(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_06,objTroopVariant.getImageCatalogDrag06());

			bReturn = true;
		}//end main:
		return bReturn;
	}
		
	
}

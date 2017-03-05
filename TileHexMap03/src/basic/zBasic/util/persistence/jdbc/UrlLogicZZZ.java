package basic.zBasic.util.persistence.jdbc;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.UrlLogicBaseZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;

public class UrlLogicZZZ extends UrlLogicBaseZZZ {
	public static final String sURL_PROTOCOL_PERSISTENCE_JDBC = "jdbc";
	public static final String sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL = ":";
		
	public static String getProtocol(String sUrl) throws ExceptionZZZ{		
		/* Besser wäre es  diese Methode abstract zu machen 
		  als einen ExceptionZZZ - Hinweis auszugeben. Wie aktell gemacht wird..
		 ABER: static Methoden könne wohl erst ab Java 8.0 abstract sein!!!
		*/
		
		//Hinweis darauf, dass diese Methode überschrieben werden muss !!!
//		ExceptionZZZ ez = new ExceptionZZZ(sERROR_ZFRAME_METHOD, iERROR_ZFRAME_METHOD, UrlLogicBaseZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName()); 
//		throw ez;	
		
		//####################################################################
		//DAS IST JETZT DIE IMPLEMENTIERUNG
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrl)){
				ExceptionZZZ ez  = new ExceptionZZZ("URL", iERROR_PARAMETER_MISSING, UrlLogicZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			boolean bIsPersistenceJdbc = UrlLogicZZZ.isPersistenceJdbc(sUrl);
			if(!bIsPersistenceJdbc){
				sReturn = basic.zBasic.util.web.cgi.UrlLogicZZZ.getProtocol(sUrl);
			}else{
				sReturn = StringZZZ.left(sUrl, UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL);
			}
		}//end main:
		return sReturn;
	}
	
	public static boolean isPersistenceJdbc(String sUrl) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrl)){
				ExceptionZZZ ez  = new ExceptionZZZ("URL", iERROR_PARAMETER_MISSING, UrlLogicZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(sUrl.startsWith(UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_JDBC)){
				bReturn = true;
			}else{
				bReturn = false;
			}			
		}//end main:
		return bReturn;
	}
	
	public static String getUrlWithoutProtocol(String sUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrl)){
				ExceptionZZZ ez  = new ExceptionZZZ("URL", iERROR_PARAMETER_MISSING, UrlLogicZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sProtocol = "";
			boolean bIsPersistenceJdbc = UrlLogicZZZ.isPersistenceJdbc(sUrl);
			if(!bIsPersistenceJdbc){
				sProtocol = basic.zBasic.util.web.cgi.UrlLogicZZZ.getProtocol(sUrl);
			}else{
				sProtocol = StringZZZ.left(sUrl, UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL);
			}
			sReturn = StringZZZ.right(sUrl, sProtocol + UrlLogicZZZ.sURL_PROTOCOL_PERSISTENCE_SEPARATOR_PROTOCOL, true);			
		}//end main:
		return sReturn;
	}
	
}

package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.ITroopVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopVariantTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.KeyImmutable;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopVariant;
import use.thm.util.datatype.enums.EnumSetTroopArmyVariantUtilTHM;
import use.thm.util.datatype.enums.EnumSetTroopFleetVariantUtilTHM;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zBasic.util.dataype.calling.ReferenceZZZ;
import basic.zKernel.KernelZZZ;
/**Diese DAO Klasse dient nur der dazu den Code der daraus erbenden Klassen nicht redundant zu haben.
 * Sie wird selbst nicht direkt aufgerufen.
 * 
 * @author lindhaueradmin
 *
 * @param <T>
 */
public class TroopVariantDao<T> extends KeyImmutableDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public TroopVariantDao() throws ExceptionZZZ{
		super();
		this.installLoger(TroopVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopVariantDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(TroopVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopVariantDao(HibernateContextProviderSingletonTHM objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(TroopVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopVariantDao(HibernateContextProviderSingletonTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(TroopVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	
	
	public List<T> findByHQL(String hql, int first, int max) {
		return this.findByHQLGeneral(hql, first, max);
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first,
			int max) {
		// TODO Auto-generated method stub
		return null;
	}
	

//####### EIGENE METHODEN ###########
/* Das ist die Variante für Entities, die nicht mit der Annotation "Immutable" versehen sind.
* Die Entities mit der Annotation "Immutable" haben nämlich keine setter-Methoden.
*/
//Da Java nur ein CALL_BY_VALUE machen kann, weden hier für die eingefüllten Werte Referenz-Objekte verwendet.
//Erst die normalen Enum-Werte, dann ... sUniquetext / sCategorytext / iMoveRange / sImageUrl / iThisKeyDefaulttext / iThiskeyImmutabletext;
protected <T> void _fillValueImmutable(ITroopVariantTHM objValue,String sEnumAlias, ReferenceZZZ<Long> objlngThiskey, 
	ReferenceZZZ<String> objsName, ReferenceZZZ<String> objsUniquetext, ReferenceZZZ<String> objsCategorytext, 
	ReferenceZZZ<Integer> objintMoveRange, ReferenceZZZ<String> objsImageUrl,ReferenceZZZ<Long> objlngThisidTextDefault, ReferenceZZZ<Long> objlngThisidTextImmutable
	){

	//Merke: Direktes Reinschreiben geht wieder nicht wg. "bound exception"
	//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, sEnumAlias);
			
	//Also: Klasse holen und danach CASTEN.
	Class<?> objClass = ((KeyImmutable) objValue).getThiskeyEnumClass();
	Long lngThiskey = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_ThiskeyValue((Class<IEnumSetTroopVariantTHM>) objClass, sEnumAlias);//Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
	System.out.println("Gefundener Thiskey: " + lngThiskey.toString());
	objlngThiskey.set(lngThiskey); //Damit wird CALL_BY_VALUE quasi gemacht....
	
	String sName = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_NameValue((Class<IEnumSetTroopVariantTHM>) objClass, sEnumAlias);
	System.out.println("Gefundener Typname: " + sName);
	objsName.set(sName); //Damit wird CALL_BY_VALUE quasi gemacht....
	
	String sUniquetext = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_UniquetextValue((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener Uniquewert: " + sUniquetext);
	objsUniquetext.set(sUniquetext); //Damit wird CALL_BY_VALUE quasi gemacht....
	
	String sCategorytext = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_CategorytextValue((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener Categorytext: " + sCategorytext);
	objsCategorytext.set(sCategorytext); //Damit wird CALL_BY_VALUE quasi gemacht....
		 
	Integer intMoveRange = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_MoveRangeValue((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener MoveRange: " + intMoveRange);
	objintMoveRange.set(intMoveRange); //Damit wird CALL_BY_VALUE quasi gemacht....
		
	String sImageUrl = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_ImageUrlStringValue((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener ImageUrlString: " + sImageUrl);
	objsImageUrl.set(sImageUrl); //Damit wird CALL_BY_VALUE quasi gemacht....	
	 
	Long lngThisKeyDefaulttext = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_DefaulttextThisid((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener ThisKeyDefaulttext: " + lngThisKeyDefaulttext);
	objlngThisidTextDefault.set(lngThisKeyDefaulttext); //Damit wird CALL_BY_VALUE quasi gemacht....
	
	Long lngThisKeyImmutabletext = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_ImmutabletextThisid((Class<IEnumSetTroopVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener ThisKeyImmutabletext: " + lngThisKeyImmutabletext);	
	objlngThisidTextImmutable.set(lngThisKeyImmutabletext); //Damit wird CALL_BY_VALUE quasi gemacht....		
}
			
}//end class
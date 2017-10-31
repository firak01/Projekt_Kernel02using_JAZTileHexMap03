package basic.zBasic.util.datatype.enums;

import java.util.EnumSet;
import java.util.Set;

import use.thm.persistence.interfaces.enums.IEnumSetDefaulttextTHM;
import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractEnum.EnumSetDefaulttextTestTypeTHM;
import basic.zBasic.util.abstractEnum.EnumSetTestFactoryTHM;
import basic.zBasic.util.abstractEnum.IEnumSetFactoryZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.enums.EnumSetMappedUtilZZZ;

public class EnumSetDefaulttextUtilZZZTest<E>  extends TestCase{
	 private HashMapExtendedZZZ<String, EnumSetDefaulttextTestTypeTHM> hmTestGenerics = null;
	 
	    protected void setUp(){
	      
		//try {			
		
			//### Das spezielle Generics Testobjekt			
			hmTestGenerics = new HashMapExtendedZZZ<String, EnumSetDefaulttextTestTypeTHM>();
			
			Set<EnumSetDefaulttextTestTypeTHM> allTypes = EnumSet.allOf(EnumSetDefaulttextTestTypeTHM.class);
			for(EnumSetDefaulttextTestTypeTHM myType : allTypes) {
				//String sType = myType.getAbbreviation();
				String sName = myType.name();
				hmTestGenerics.put(sName,myType);
			}
			
		
			
		/*
		} catch (ExceptionZZZ ez) {
			fail("Method throws an exception." + ez.getMessageLast());
		} 
		*/
		
	}//END setup
	    public void testGetEnumSet(){
			try{
				boolean btemp;
				
				//Static Zugriff
				//EnumSet soll von der EnumerationZZZ-Klasse schon "generisch/per Reflektion" erzeugt und mitgebracht werden.
				EnumSet setEnumGenerated = EnumSetDefaulttextTestTypeTHM.getEnumSet();		
				int iSize = setEnumGenerated.size();
				assertTrue("3 Elemente im Set erwartet.", iSize==3);
				
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetTestFactoryTHM.getInstance();				
				Class objClass = EnumSetDefaulttextTestTypeTHM.class;
				EnumSetDefaulttextUtilZZZ enumSetUtil = new EnumSetDefaulttextUtilZZZ(objFactory, objClass);
				
				//Beim Wiederholten Zugriff über die Util-Klasse soll das einmal erstellte EnumSet wiederverwendet werden.	
				EnumSet setEnumReused = enumSetUtil.getEnumSetCurrent();
				assertNotNull(setEnumReused);
				
				int iSizeReused = setEnumReused.size();
				assertTrue("3 Elemente im SetReused erwartet.", iSizeReused==3);				
			
			} catch (ExceptionZZZ ez) {
				fail("Method throws an exception." + ez.getMessageLast());
			} 
	    }
	    
	    public void testStartsWith(){
			try{
				boolean btemp;
				
				//Merke: Varaussetzung ist: EnumSet wird von der EnumerationZZZ-Klasse schon "generisch/per Reflektion" erzeugt und mitgebracht werden. Alles basisert auf einer static Methode getEnumSet().

				//Variante A) EnumSet selbst erzeugen
				//Positivfall

				EnumSet<EnumSetDefaulttextTestTypeTHM> setEnumCurrent = EnumSet.of(EnumSetDefaulttextTestTypeTHM.TESTVALUE01, EnumSetDefaulttextTestTypeTHM.TESTVALUE02, EnumSetDefaulttextTestTypeTHM.TESTVALUE03);
				assertNotNull(setEnumCurrent);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("TESTVALUE01", setEnumCurrent);
				assertTrue("A) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativfall
				EnumSet<EnumSetDefaulttextTestTypeTHM> setEnumCurrent02 = EnumSet.of(EnumSetDefaulttextTestTypeTHM.TESTVALUE01, EnumSetDefaulttextTestTypeTHM.TESTVALUE02, EnumSetDefaulttextTestTypeTHM.TESTVALUE03);
				assertNotNull(setEnumCurrent02);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("Nixdaaa", setEnumCurrent02);
				assertFalse("A) Prüfstring sollte in der Enumeration NICHT vorhanden sein .", btemp);
				
				//Variante B) EnumSet per statischer Methode holen
				//Positivfall
				EnumSet<?> setEnumGenerated = EnumSetDefaulttextTestTypeTHM.getEnumSet();
				assertNotNull(setEnumGenerated);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("TESTVALUE02", setEnumGenerated);
				assertTrue("B) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativvall
				EnumSet<?> setEnumGenerated02 = EnumSetDefaulttextTestTypeTHM.getEnumSet();
				assertNotNull(setEnumGenerated02);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("NIXDA", setEnumGenerated02);
				assertFalse("B) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
				//Variante C) direkter
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetTestFactoryTHM.getInstance();	
				//Class objClass = EnumSetDefaulttextUtilZZZ.class;  //TODO
				Class objClass = EnumSetDefaulttextTestTypeTHM.class;
				EnumSetDefaulttextUtilZZZ enumSetUtil = new EnumSetDefaulttextUtilZZZ(objFactory, objClass);
				
				
				///FGL########## dann hören hier ggfs. die Geminsamkeiten mit der mapped... Struktur auf, heir haben wir Defaulttexte.
				
				//Positivfall
				btemp = enumSetUtil.startsWithAnyShorttext("Test03");
				assertTrue("C) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
												
				//Negativfall
				btemp = enumSetUtil.startsWithAnyShorttext("XXXX");
				assertFalse("C) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
			} catch (ExceptionZZZ ez) {
				fail("Method throws an exception." + ez.getMessageLast());
			} 
		}    
	    
	    public void testGetEnumConstant(){
	    	//Merke: Die Spezielle Enum Struktur unterscheidet sich dann ab einem bestimmten Punkt von der normalen Enum Struktur.
	    	//           Das bedeutet, dass auch die Utility-Klasse ganz andere Methoden braucht.
	    	
	    	Class<?> objClass = EnumSetDefaulttextTestTypeTHM.class;
	    	String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue(objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'TESTVALUE01' sein", "TESTVALUE01".equals(sName));
	    	
	    	String sString = EnumSetDefaulttextUtilZZZ.readEnumConstant_StringValue(objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'Test01' sein", "Test01".equals(sString));
	    	
	    	Integer intOrdinal = EnumSetDefaulttextUtilZZZ.readEnumConstant_OrdinalValue(objClass, "TESTVALUE01");
	    	assertEquals("Prüfinteger solllte als Ergebnis 0 sein", intOrdinal.intValue(),0);
	    	
	    	//#### Dann hören hier ggfs. die Gemeinsamkeiten mit der mapped... Struktur auf, hieir haben wir Defaulttexte.				    	
	     	@SuppressWarnings("unchecked")
			String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetDefaulttextTHM>) objClass, "TESTVALUE01");
	     	//String sDescription = EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue((Class<E>)objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'A Test01 value description' sein", "A Test01 value description".equals(sDescription));
	    	
	    	@SuppressWarnings("unchecked")
			String sAbbreviation = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetDefaulttextTHM>) objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'Test01' sein", "Test01".equals(sAbbreviation));
	    	
	    	@SuppressWarnings("unchecked")
			Integer intPosition = EnumSetDefaulttextUtilZZZ.readEnumConstant_PositionValue((Class<IEnumSetDefaulttextTHM>) objClass, "TESTVALUE01");
	    	assertEquals("Prüfinteger solllte als Ergebnis 1 sein", intPosition.intValue(),1);

	    	
	    	@SuppressWarnings("unchecked")
			Integer intIndex = EnumSetDefaulttextUtilZZZ.readEnumConstant_IndexValue((Class<IEnumSetDefaulttextTHM>) objClass, "TESTVALUE01");
	    	assertEquals("Prüfinteger solllte als Ergebnis 0 sein", intIndex.intValue(),0);
	    	assertEquals("Prüfinteger solllte als Ergebnis dem ordinal - Wert entsprechen", intIndex.intValue(), intOrdinal.intValue());
	    	assertEquals("Prüfinteger solllte als Ergebnis um 1 höher als der Index sein", intPosition.intValue(),intIndex.intValue()+1);	    	
	    }
	    
	
	}//end class
	

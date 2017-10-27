package basic.zBasic.util.datatype.enums;

import java.util.EnumSet;
import java.util.Set;

import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractEnum.EnumSetDefaulttextTestType;
import basic.zBasic.util.abstractEnum.EnumSetTestFactoryZZZ;
import basic.zBasic.util.abstractEnum.EnumSetMappedTestTypeZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetFactoryZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.enums.EnumSetMappedUtilZZZ;

public class EnumSetDefaulttextUtilZZZTest  extends TestCase{
	 private HashMapExtendedZZZ<String, EnumSetDefaulttextTestType> hmTestGenerics = null;
	 
	    protected void setUp(){
	      
		//try {			
		
			//### Das spezielle Generics Testobjekt			
			hmTestGenerics = new HashMapExtendedZZZ<String, EnumSetDefaulttextTestType>();
			
			Set<EnumSetDefaulttextTestType> allTypes = EnumSet.allOf(EnumSetDefaulttextTestType.class);
			for(EnumSetDefaulttextTestType myType : allTypes) {
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
				EnumSet setEnumGenerated = EnumSetDefaulttextTestType.getEnumSet();		
				int iSize = setEnumGenerated.size();
				assertTrue("3 Elemente im Set erwartet.", iSize==3);
				
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetTestFactoryZZZ.getInstance();				
				Class objClass = EnumSetDefaulttextTestType.class;
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
				EnumSet<EnumSetDefaulttextTestType> setEnumCurrent = EnumSet.of(EnumSetDefaulttextTestType.TESTVALUE01, EnumSetDefaulttextTestType.TESTVALUE02, EnumSetDefaulttextTestType.TESTVALUE03);
				assertNotNull(setEnumCurrent);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("TESTVALUE01", setEnumCurrent);
				assertTrue("A) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativfall
				EnumSet<EnumSetDefaulttextTestType> setEnumCurrent02 = EnumSet.of(EnumSetDefaulttextTestType.TESTVALUE01, EnumSetDefaulttextTestType.TESTVALUE02, EnumSetDefaulttextTestType.TESTVALUE03);
				assertNotNull(setEnumCurrent02);
				btemp = EnumSetMappedUtilZZZ.startsWithAnyAlias("Nixdaaa", setEnumCurrent02);
				assertFalse("A) Prüfstring sollte in der Enumeration NICHT vorhanden sein .", btemp);
				
				//Variante B) EnumSet per statischer Methode holen
				//Positivfall
				EnumSet<?> setEnumGenerated = EnumSetDefaulttextTestType.getEnumSet();
				assertNotNull(setEnumGenerated);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("TESTVALUE02", setEnumGenerated);
				assertTrue("B) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativvall
				EnumSet<?> setEnumGenerated02 = EnumSetDefaulttextTestType.getEnumSet();
				assertNotNull(setEnumGenerated02);
				btemp = EnumSetDefaulttextUtilZZZ.startsWithAnyAlias("NIXDA", setEnumGenerated02);
				assertFalse("B) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
				//Variante C) direkter
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetTestFactoryZZZ.getInstance();	
				Class objClass = EnumSetDefaulttextUtilZZZ.class;
				EnumSetDefaulttextUtilZZZ enumSetUtil = new EnumSetDefaulttextUtilZZZ(objFactory, objClass);
				
				
				///FGL########## dann hören hier ggfs. die Geminsamkeiten mit der mapped... Struktur auf, heir haben wir Defaulttexte.
				
				//Positivfall
				btemp = enumSetUtil.startsWithAnyAbbreviation("3");
				assertTrue("C) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
												
				//Negativfall
				btemp = enumSetUtil.startsWithAnyAbbreviation("XXXX");
				assertFalse("C) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
			} catch (ExceptionZZZ ez) {
				fail("Method throws an exception." + ez.getMessageLast());
			} 
		}    
	    
	    public void testGetEnumConstant(){
	    	//Merke: Die Spezielle Enum Struktur unterscheidet sich dann ab einem bestimmten Punkt von der normalen Enum Struktur.
	    	//           Das bedeutet, dass auch die Utility-Klasse ganz andere Methoden braucht.
	    	
	    	Class<?> objClass = EnumSetDefaulttextTestType.class;
	    	String sName = EnumSetDefaulttextUtilZZZ.getEnumConstant_NameValue(objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'TESTVALUE01' sein", "TESTVALUE01".equals(sName));
	    	
	    	String sString = EnumSetDefaulttextUtilZZZ.getEnumConstant_StringValue(objClass, "TESTVALUE01");
	    	assertTrue("Prüfstring solllte als Ergebnis 'Test01' sein", "Test01".equals(sString));
	    	
	    	Integer intOrdinal = EnumSetDefaulttextUtilZZZ.getEnumConstant_OrdinalValue(objClass, "TESTVALUE01");
	    	assertEquals("Prüfinteger solllte als Ergebnis 0 sein", intOrdinal.intValue(),0);
	    	
	    	//FGL TODO GOON 20171027
	    	///FGL########## dann hören hier ggfs. die Gemeinsamkeiten mit der mapped... Struktur auf, heir haben wir Defaulttexte.				    	
	     	@SuppressWarnings("unchecked")
			String sDescription = EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue((Class<IEnumSetMappedZZZ>) objClass, "ONE");
	    	assertTrue("Prüfstring solllte als Ergebnis 'eins' sein", "eins".equals(sDescription));
	    	assertEquals("StringValue sollte gleich DescriptionValue sein", sString, sDescription);
	    	
	    	@SuppressWarnings("unchecked")
			String sAbbreviation = EnumSetDefaulttextUtilZZZ.getEnumConstant_AbbreviationValue((Class<IEnumSetMappedZZZ>) objClass, "ONE");
	    	assertTrue("Prüfstring solllte als Ergebnis '1' sein", "1".equals(sAbbreviation));
	    	
	    	@SuppressWarnings("unchecked")
			Integer intPosition = EnumSetDefaulttextUtilZZZ.getEnumConstant_PositionValue((Class<IEnumSetMappedZZZ>) objClass, "ONE");
	    	assertEquals("Prüfinteger solllte als Ergebnis 1 sein", intPosition.intValue(),1);

	    	
	    	@SuppressWarnings("unchecked")
			Integer intIndex = EnumSetDefaulttextUtilZZZ.getEnumConstant_IndexValue((Class<IEnumSetMappedZZZ>) objClass, "ONE");
	    	assertEquals("Prüfinteger solllte als Ergebnis 0 sein", intIndex.intValue(),0);
	    	assertEquals("Prüfinteger solllte als Ergebnis dem ordinal - Wert entsprechen", intIndex.intValue(), intOrdinal.intValue());
	    	assertEquals("Prüfinteger solllte als Ergebnis um 1 höher als der Index sein", intPosition.intValue(),intIndex.intValue()+1);	    	
	    }
	    
	
	}//end class
	

package basic.zBasic.util.abstractEnum;

import java.util.EnumSet;
import java.util.Set;

import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;

public class EnumSetMappedUtilZZZTest  extends TestCase{
	 private HashMapExtendedZZZ<String, EnumSetMappedTestTypeZZZ> hmTestGenerics = null;
	 
	    protected void setUp(){
	      
		//try {			
		
			//### Das spezielle Generics Testobjekt			
			hmTestGenerics = new HashMapExtendedZZZ<String, EnumSetMappedTestTypeZZZ>();
			
			Set<EnumSetMappedTestTypeZZZ> allTypes = EnumSet.allOf(EnumSetMappedTestTypeZZZ.class);
			for(EnumSetMappedTestTypeZZZ myType : allTypes) {
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
				EnumSet setEnumGenerated = EnumSetMappedTestTypeZZZ.getEnumSet();		
				int iSize = setEnumGenerated.size();
				assertTrue("3 Elemente im Set erwartet.", iSize==3);
				
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetMappedTestFactoryZZZ.getInstance();				
				Class objClass = EnumSetMappedTestTypeZZZ.class;
				EnumSetMappedUtilZZZ enumSetUtil = new EnumSetMappedUtilZZZ(objFactory, objClass);
				
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
				EnumSet<EnumSetMappedTestTypeZZZ> setEnumCurrent = EnumSet.of(EnumSetMappedTestTypeZZZ.ONE, EnumSetMappedTestTypeZZZ.TWO, EnumSetMappedTestTypeZZZ.THREE);
				assertNotNull(setEnumCurrent);
				btemp = EnumSetMappedUtilZZZ.anyStartsWithAlias(setEnumCurrent, "ONE");
				assertTrue("A) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativfall
				EnumSet<EnumSetMappedTestTypeZZZ> setEnumCurrent02 = EnumSet.of(EnumSetMappedTestTypeZZZ.ONE, EnumSetMappedTestTypeZZZ.TWO, EnumSetMappedTestTypeZZZ.THREE);
				assertNotNull(setEnumCurrent02);
				btemp = EnumSetMappedUtilZZZ.anyStartsWithAlias(setEnumCurrent02, "Nixdaaa");
				assertFalse("A) Prüfstring sollte in der Enumeration NICHT vorhanden sein .", btemp);
				
				//Variante B) EnumSet per statischer Methode holen
				//Positivfall
				EnumSet<?> setEnumGenerated = EnumSetMappedTestTypeZZZ.getEnumSet();
				assertNotNull(setEnumGenerated);
				btemp = EnumSetMappedUtilZZZ.anyStartsWithAlias(setEnumGenerated, "TWO");
				assertTrue("B) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
				
				//Negativvall
				EnumSet<?> setEnumGenerated02 = EnumSetMappedTestTypeZZZ.getEnumSet();
				assertNotNull(setEnumGenerated02);
				btemp = EnumSetMappedUtilZZZ.anyStartsWithAlias(setEnumGenerated02, "NIXDA");
				assertFalse("B) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
				//Variante C) direkter
				//Generischer Zugriff, ist  nicht ganz möglich, darum Verwendung der Factory Klasse
				IEnumSetFactoryZZZ objFactory = EnumSetMappedTestFactoryZZZ.getInstance();	
				Class objClass = EnumSetMappedTestTypeZZZ.class;
				EnumSetMappedUtilZZZ enumSetUtil = new EnumSetMappedUtilZZZ(objFactory, objClass);
				
				//Positivfall
				btemp = enumSetUtil.anyStartsWithAbbreviation("THREE");
				assertTrue("C) Prüfstring sollte in der Enumeration vorhanden sein.", btemp);
												
				//Negativfall
				btemp = enumSetUtil.anyStartsWithAbbreviation("XXXX");
				assertFalse("C) Prüfstring sollte in der Enumeration NICHT vorhanden sein.", btemp);
				
			} catch (ExceptionZZZ ez) {
				fail("Method throws an exception." + ez.getMessageLast());
			} 
		}    
	    
	    public void testGetEnumConstant(){
	    	Class objClass = EnumSetMappedTestTypeZZZ.class;
	    	Object obj = EnumSetMappedUtilZZZ.getEnumConstant_NameValue(objClass, "ONE");
	    	assertTrue("Prüfstring solllte als Ergebnis 'eins' sein", "eins".equals(obj));
	    }
	    
	
	}//end class
	

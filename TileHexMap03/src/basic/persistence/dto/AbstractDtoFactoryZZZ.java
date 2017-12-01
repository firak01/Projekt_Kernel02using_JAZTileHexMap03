package basic.persistence.dto;

import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;


public abstract class AbstractDtoFactoryZZZ implements IDtoFactoryZZZ{
	
	
	/* Merke:
	 * Mache diese Factory - Klassen NICHT  zum Singleton:
	 * 1. Das geht in abstrakter Klasse aber nicht, da in einer abstrakten Klasse zwar ein Konsturktor vorhanden sein kann, .... 
	 * ABER:  Es kann die Abstrakte Klasse nicht instantiiert werden. 
	 * 
	 * 2. Alle erbenden Klassen brauchen dann einen Konstruktor. Viel zu aufwändig.
	 * 
	 * 3. Die FactoiyGenaratro Klasse zum Singleton zu machen reicht.
	 * 
	 * */
//	private static AbstractDtoFactoryZZZ objFactorySingelton; //muss als Singleton static sein
//
//	public static AbstractDtoFactoryZZZ getInstance() throws ExceptionZZZ{
//		if(objFactorySingelton==null){
//			objFactorySingelton = new AbstractDtoFactoryZZZ(); //Das geht in abstrakter Klasse eben nicht... !!!!!!!!!!!!!!!!!!!!1
//		}
//		return objFactorySingelton;	
//	}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
//	private AbstractDtoFactoryZZZ() throws ExceptionZZZ{
//		//super();
//	}
	
	//TODO für die hieraus erbenden Klassen(!): Erlaube das setzen von Flags im Konstruktor.... 
	//dazu muss die Klasse aber auch die getFlag / set Flag Methodik verwenden, die in anderen Kernel-Objekten genutzt wird.
	//Diese Flags müssen vererbbar sein....
	
	//Wenn der Konstruktor ein Singleton erzeugt, dann geht das auch nur in den daraus erbenden Klassen.	
//	public static  AbstractDtoFactoryZZZ getInstance(tring sFlagControl) throws ExceptionZZZ{
//		if(objFactorySingelton==null){
//			objFactorySingelton = new AbstractDtoFactoryZZZ(sFlagControl);
//		}
//		return objFactorySingelton;	
//	}
	

	
	
	@SuppressWarnings("unchecked")
	public GenericDTO<?> createDTO(Class classObjectType) {
		GenericDTO<?> dto = GenericDTO.getInstance(classObjectType);
		return dto;
	}
	
}

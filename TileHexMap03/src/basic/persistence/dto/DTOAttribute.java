package basic.persistence.dto;

import java.io.Serializable;

import basic.zBasic.ExceptionZZZ;

public class DTOAttribute<G extends IDTOAttributeGroup, T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;

    private DTOAttribute(String name) {

        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public static <F extends IDTOAttributeGroup, S> DTOAttribute<F, S> getInstance(String name) {
        return new DTOAttribute<F, S>(name);
    }

    @Override
    public String toString() {
        return name;
    }

    //equals(), hashCode() ...
    //20180729: Will man diese Attibute erzeugen, um z.B. programmatisch auf ein bestimmtes Attribut zuzugreifen, dann muss man die equals-Methode überschrieben haben.
    //               und zum Wiederfinden des Wertes die HashCode-Methode
    //Genutzt wird das in public byte[] getVariantImageUsedInByte() throws ExceptionZZZ{
    @Override
    public boolean equals(Object obj){
    	if(obj==null) return false;
    	if(obj instanceof DTOAttribute){
    		DTOAttribute objDtoAttribute = (DTOAttribute) obj;    		
    		if(objDtoAttribute.name.equals(this.name)){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    }
    
    //20180729: Will man diese Attibute erzeugen, um z.B. programmatisch auf ein bestimmtes Attribut zuzugreifen, dann muss man zum Wiederfinden des Wertes die HashCode-Methodeüberschrieben haben.
    //               und natürlich auch die equals-Methode 
    //Genutzt wird das in public byte[] getVariantImageUsedInByte() throws ExceptionZZZ{
    @Override
    public int hashCode() {
    	return this.name.hashCode();
    }
}

package debug.thm.persistence.dto.tile;

import use.thm.persistence.dto.ITileDtoAttribute;
import basic.persistence.dto.GenericDTO;

public class DebugTileDto {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
		
		//o.k.
		dto.set(ITileDtoAttribute.UNIQUENAME, "Mein erstess DTO");
		
		
		String stemp = (String) dto.get(ITileDtoAttribute.UNIQUENAME);
		System.out.println("Aus dem DTO zurück: " + stemp);
	}

}

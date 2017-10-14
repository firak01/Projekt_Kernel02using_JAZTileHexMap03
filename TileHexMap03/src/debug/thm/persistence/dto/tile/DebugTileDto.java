package debug.thm.persistence.dto.tile;

import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.TileTHM;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.dto.TileDtoFactory;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;

public class DebugTileDto {

	public static void main(String[] args) {
		
		//############################
		//1.	Variante: Direkte Instanziierung
		GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
		
		//o.k.
		dto.set(ITileDtoAttribute.UNIQUENAME, "Mein erstes DTO");
		
		
		String stemp = (String) dto.get(ITileDtoAttribute.UNIQUENAME);
		System.out.println("Aus dem DTO zurück: " + stemp);
		
		//########################################
		//2. Variante: Verwende eine Factory	
		TileDtoFactory factoryTile = new TileDtoFactory();
		GenericDTO dtoByFactory = factoryTile.createDTO();
		
		//o.k.
		dtoByFactory.set(ITileDtoAttribute.UNIQUENAME, "Mein zweites DTO");
		
		String stempByFactory = (String) dtoByFactory.get(ITileDtoAttribute.UNIQUENAME);
		System.out.println("Aus dem DTObyFactory zurück: " + stempByFactory);
		
		//####################################
		//3. Variante: Hole die Verwendete Factory per Generator aus dem Objekt, das ein DTO nutzen will.
		try {
			DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
			IDtoFactoryZZZ objFactory = objFactoryGenerator.getDtoFactory(ArmyTileTHM.class);
			GenericDTO dtoByFactoryGenerated = objFactory.createDTO();
			
			//o.k.
			dtoByFactoryGenerated.set(ITileDtoAttribute.UNIQUENAME, "Mein drittes DTO");
			
			String stempByFactoryGenerated = (String) dtoByFactoryGenerated.get(ITileDtoAttribute.UNIQUENAME);
			System.out.println("Aus dem DTObyFactory zurück: " + stempByFactoryGenerated);
			
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
			System.out.println("Fehler ist aufgetreten: " + e.getDetailAllLast());
		}
		
		
		
		
		
		
		
		
		
		
	}

}

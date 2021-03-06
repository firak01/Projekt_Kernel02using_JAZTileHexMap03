package use.thm.persistence.model;

import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
/** Nicht verwendete Lösung. Statt dessen wird in den Entity Klassen für die Schlüsselerttabellen und den Defaultwerten 
 *   eine interen Klasse für das enum verwendet.
 *   
 * @author Fritz Lindhauer
 *
 */
public enum TileDefaulttextType implements IKeyEnum<Long>, IThiskeyProviderZZZ<Long>{		
	
    /**
     * Präsenzstudium 
     */
    @IFieldDescription(description = "Präsenzstudium") Praesenzstudium(1),

    /**
     * Fernstudium
     */
    @IFieldDescription(description = "Fernstudium") Fernstudium(2),
    
    /**
     * Praxissemester Ausland
     */
    @IFieldDescription(description = "Praxissemester Ausland") PraxissemesterImAusland(5);
	
    
    private Long objKeyLong;
    TileDefaulttextType(int key) {
            this.objKeyLong = Long.valueOf(key);
        }

        public Long getKey() {
            return this.objKeyLong;
        }

        //### Aus IKeyProviderZZZ
		public Long getThiskey() {
			return this.objKeyLong;
		}		
        
       

}//end enum
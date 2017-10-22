package use.thm.persistence.model;

import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;

public enum TileDefaulttextType implements IKeyEnum<Long>{		
	
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

        @Override
        public Long getKey() {
            return this.objKeyLong;
        }		
        
       

}//end enum
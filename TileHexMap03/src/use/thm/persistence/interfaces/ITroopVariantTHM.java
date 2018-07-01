package use.thm.persistence.interfaces;

import javax.persistence.Transient;

import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.Immutabletext;

public interface ITroopVariantTHM {
	@Transient
	public String getSubtype(); //AR oder FL, das wird in den einzelnen Unterklassen fest vorgegeben und entspricht dem Subtype des dazugehörenden Entities. Wird zur Steuerung, z.B. des berechneten Bildes vewendet.
	
	public Integer getMapMoveRange();
	public String getImageUrlString();
	
	 public Defaulttext getDefaulttextObject();
	 public Immutabletext getImmutabletextObject();
}

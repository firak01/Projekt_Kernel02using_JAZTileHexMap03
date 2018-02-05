package use.thm.persistence.interfaces;

import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.Immutabletext;

public interface ITroopVariantTHM {
	public Integer getMapMoveRange();
	public String getImageUrlString();
	
	 public Defaulttext getDefaulttextObject();
	 public Immutabletext getImmutabletextObject();
}

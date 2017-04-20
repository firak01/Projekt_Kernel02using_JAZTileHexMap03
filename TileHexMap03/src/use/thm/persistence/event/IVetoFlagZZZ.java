package use.thm.persistence.event;

import java.util.Calendar;

public interface IVetoFlagZZZ {	
	public boolean isVeto();
	public void veto(boolean bResult);
	public void resetVeto();
	public Calendar getVetoDate();
}

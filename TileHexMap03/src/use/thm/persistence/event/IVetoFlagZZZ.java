package use.thm.persistence.event;

import java.util.Calendar;

public interface IVetoFlagZZZ {	
	public boolean isVeto();
	public void veto(boolean bResult);
	public void veto(boolean bResult, String sResultMessage);
	public void resetVeto();
	public Calendar getVetoDate();
	public VetoFlag4ListenerZZZ getCommitResult();
}

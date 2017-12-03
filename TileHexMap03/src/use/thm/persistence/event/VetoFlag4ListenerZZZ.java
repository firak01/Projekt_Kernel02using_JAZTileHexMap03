package use.thm.persistence.event;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**Statt nur 2 Zustände, gibt es hier auch den Zustand "nicht ausgeführt".
 *  Genutzt wird das durch HibernateUtil.wasCommittedSuccessful(...)
 *  
 *  Eingebunden wird dies in den Listernern.
 *    
 * @author Fritz Lindhauer
 *
 */
public class VetoFlag4ListenerZZZ implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean bVeto= false;
	private boolean bVetoUsed = true;
	private Calendar objCalendarVetoSet = null;
	private String sVetoMessage = new String("");
	
	public VetoFlag4ListenerZZZ(){		
	}
	
	//Das setzen des Vetos
	public void veto(boolean bResult){
		this.setVeto(bResult);
		this.setVetoUsedStatus(false);
		this.objCalendarVetoSet = Calendar.getInstance(); //soll das aktuelle Datum holen. Dadurch wird bewiesen, dass in dem Listener sich die Werte ändern.
	}
	
	public void veto(boolean bResult, String sMessage){
		this.veto(bResult);
		this.setVetoMessage(sMessage);
	}
	
	//Das Abholen des Vetos, ohne zurücksetzen. Das sollte dann noch gemacht weden.
	public boolean isVeto(){
		if(!this.getVetoUsedStatus()){
			return this.getVeto();
		}else{
			return false;
		}				
	}
	
	public Calendar getVetoDate(){
		return this.objCalendarVetoSet;
	}
	private void setVetoDate(Calendar objCalendar){
		this.objCalendarVetoSet = objCalendar;
	}
	
	private boolean getVeto(){
		return this.bVeto;
	}
	private void setVeto(boolean bVeto){
		this.bVeto=bVeto;
	}
	
	private void setVetoMessage(String sVetoMessage){
		this.sVetoMessage = sVetoMessage;
	}
	public String getVetoMessage(){
		return this.sVetoMessage;
	}
	
	//An kann einmal das Veto abholen, dann sollte es zurückgesetz werden. Nur weil man nicht weiss, ob beim nächsten Lauf der Listener überhaupt aufgerufen wird.
	private void setVetoUsedStatus(boolean bStatus){
		this.bVetoUsed = bStatus;
	}
	private boolean getVetoUsedStatus(){
		return this.bVetoUsed;
	}
	
	//Nach dem Abholen des Vetos, dieses zurücksetzen.
	public void resetVeto(){
		this.setVeto(false);
		this.setVetoUsedStatus(true);
		this.setVetoDate(null);
		this.setVetoMessage("");
	}
	
	
}

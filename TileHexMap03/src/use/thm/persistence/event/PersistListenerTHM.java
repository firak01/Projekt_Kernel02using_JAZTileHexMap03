package use.thm.persistence.event;

import java.util.Calendar;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;

import custom.zKernel.LogZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.IKernelZZZ;

/**Wird nicht eingesetzt. Funktioniert wahrscheinlich nur mit JPA, d.h. bei einem reinen Hibernate session.save(xxx) wird das nicht aufgerufen.
 *  TODO GOON: 20170415: Mache dafür einen DEBUG TRYOUT, in dem mit JPA (also dem EntityManager) gearbeitet wird.
 * @author Fritz Lindhauer
 *
 */
public class PersistListenerTHM implements PersistEventListener, IKernelUserZZZ, IVetoFlagZZZ {
	private static final long serialVersionUID = 1L;
	private IKernelZZZ objKernel;
	private LogZZZ objLog; 
	
	private VetoFlag4ListenerZZZ objLastResult=new VetoFlag4ListenerZZZ();
	
	public void onPersist(PersistEvent arg0) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event...");
	}

	public void onPersist(PersistEvent arg0, Map arg1) throws HibernateException {		
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event 02...");
	}

	@Override
	public IKernelZZZ getKernelObject() {
		return this.objKernel;
	}

	@Override
	public void setKernelObject(IKernelZZZ objKernel) {
		this.objKernel = objKernel;
	}

	@Override
	public LogZZZ getLogObject() {
		return this.objLog;
	}

	@Override
	public void setLogObject(LogZZZ objLog) {
		this.objLog = objLog;
	}
	
	public boolean isVeto() {
		return this.objLastResult.isVeto();
	}
	
	public void veto(boolean bResult) {
		this.objLastResult.veto(bResult);			
	}
	
	public void veto(boolean bResult, String sResultMessage) {
		this.objLastResult.veto(bResult, sResultMessage); 
	}
			
	public void resetVeto() {
		this.objLastResult.resetVeto();
	}

	public Calendar getVetoDate() {
		return this.objLastResult.getVetoDate();
	}

	public VetoFlag4ListenerZZZ getCommitResult(){
		return this.objLastResult;
	}
}

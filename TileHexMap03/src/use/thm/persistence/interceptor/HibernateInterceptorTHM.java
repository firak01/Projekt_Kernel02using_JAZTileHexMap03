package use.thm.persistence.interceptor;

import java.io.Serializable;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;

/**20170409: Funktioniert, wir aber bei allen Ereignissen und allen Entitäten des Projekts aufgerufen.
 * @author Fritz Lindhauer
 *
 */
public class HibernateInterceptorTHM extends EmptyInterceptor {
	private static final long serialVersionUID = 1L;

	@Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
           String[] propertyNames, Type[] types) {
		try{
	       System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onsave Method is getting called");
	       //Merke: Der Interceptor schein nach dem PreInsert-Event aufgerufen zu werden.
	       System.out.println("==== DETAILS OF ENTITY ARE ====");
	       if(entity instanceof TroopArmy)
	       {
	           System.out.println("Id of an Entity is :" + id);
	           System.out.println("Property Names ");
	
	           for(int i=0;i<propertyNames.length;i++)
	           {
	               System.out.println(propertyNames[i] );
	
	               if("name".equals(propertyNames[i]))
	               {
	                   state[i]= "Hibernate Tutorial Updated";
	               }
	           }
	
	           TroopArmy book = (TroopArmy)entity;
	           System.out.println("ARMY STATE is ");
	           System.out.println(book);
	       }
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
			return false;
		}
       return true;
   }    
	
}

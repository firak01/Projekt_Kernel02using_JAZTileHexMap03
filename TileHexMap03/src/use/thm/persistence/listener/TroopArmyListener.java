package use.thm.persistence.listener;

import java.io.Serializable;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ReflectCodeZZZ;

/**20170409: Funktioniert noch nicht..... Hibernate Weg:
 * Ich denke es liegt daran, dass dieser Listener an der Sesion registriert werden muss.Ausserdem muss derListener EmptyListener extenden.
 * SessionBuilder builder = factory.withOptions().interceptor(new ConsoleLogInterceptor());
 * 
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyListener extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrePersist
	public void vorEinsetzen(TroopArmy objTroop){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vor dem Einsetzen einer Armee.");
	}
	
	@PostPersist
	public void nachEinsetzen(TroopArmy objTroop){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Nach dem Einsetzen einer Armee.");
	}
	
	@Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
           String[] propertyNames, Type[] types) {
       System.out.println("onsave Method is getting called");
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
           System.out.println("BOOK STATE is ");
           System.out.println(book);
       }
       return true;
   }    
	
}

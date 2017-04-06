package use.thm.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import use.thm.persistence.hibernate.HibernateContextProviderTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.GeneralDaoZZZ;
public class TroopDao<T> extends GeneralDaoZZZ<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor */
	public TroopDao() throws ExceptionZZZ{
		super();
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopDao(HibernateContextProviderTHM objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopDao(HibernateContextProviderTHM objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopDao(HibernateContextProviderTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
    public List<T> findLazyAll(int first, int max){
    	return this.findLazyAll("Troop", first, max);
    }
    
	@Override
	public int count(){
		this.getLog().debug("counting Troops");
		Query q = getSession().createQuery("select count(t) from Troop t");
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}
	
	
	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#countByCriteria(java.util.Map, java.util.Map)
	 */
	@Override
	public int countByCriteria(Map<String, Object> whereBy, 	Map<String, String> filter) {
		return this.countByCriteria("Troop", whereBy, filter);
	}


	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#getID(tryout.hibernate.AreaCell)
	 */
//	@Override
//	public Map<String, Object> getID(T instance) {
//		Map<String, Object> id = new HashMap<String, Object>();
//		id.put("id", instance.);		
//		return id;
//	}
	
	public List<T> findByHQL(String hql, int first, int max) {
		return this.findByHQLGeneral(hql, first, max);
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first,
			int max) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//####### EIGENE METHODEN ###########
	//....
	
}

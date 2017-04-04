package debug.thm.persistence.dao.association003;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.GeneralDaoZZZ;
import debug.thm.persistence.hibernate.HibernateContextProviderAssociationOneToManyWithTableXXX;
import debug.thm.persistence.model.association003.AssociationTester;
public class AssociationTesterDao extends GeneralDaoZZZ<AssociationTester> {
	private static final long serialVersionUID = 1L;

	/* Constructor */
	public AssociationTesterDao() throws ExceptionZZZ{
		super();
		this.installLoger( AssociationTester.class);
	}
	public AssociationTesterDao(HibernateContextProviderAssociationOneToManyWithTableXXX objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(AssociationTester.class);
	}
	public AssociationTesterDao(HibernateContextProviderAssociationOneToManyWithTableXXX objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(AssociationTester.class);
	}
	public AssociationTesterDao(HibernateContextProviderAssociationOneToManyWithTableXXX objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(AssociationTester.class);
	}
	
    public List<AssociationTester> findLazyAll(int first, int max){
    	return this.findLazyAll("AssociationTester", first, max);
    }
    
	@Override
	public int count(){
		this.getLog().debug("counting AreaCells");
		Query q = getSession().createQuery("select count(c) from AreaCell c");
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}
	
	
	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#countByCriteria(java.util.Map, java.util.Map)
	 */
	@Override
	public int countByCriteria(Map<String, Object> whereBy, 	Map<String, String> filter) {
		return this.countByCriteria("AreaCell", whereBy, filter);
	}


	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#getID(tryout.hibernate.AreaCell)
	 */
//	@Override
//	public Map<String, Object> getID(AssociationTester instance) {
//		Map<String, Object> id = new HashMap<String, Object>();
//		Integer intKey = new Integer(instance.getKey());
//		id.put("key", intKey.toString() );		
//		return id;
//	}
	public Map<String, Object> getID(AssociationTester instance) {
		Map<String, Object> id = new HashMap<String, Object>();
		Integer intKey = new Integer(instance.getKey());
		id.put("key", intKey);		
		return id;
	}
	
	public List<AssociationTester> findByHQL(String hql, int first, int max) {
		return this.findByHQLGeneral(hql, first, max);
	}

	public List<AssociationTester> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AssociationTester> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first,
			int max) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//####### EIGENE METHODEN ###########

}

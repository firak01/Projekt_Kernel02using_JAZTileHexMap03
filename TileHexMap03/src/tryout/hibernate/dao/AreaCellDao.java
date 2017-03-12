package tryout.hibernate.dao;

import java.util.List;
import java.util.Map;

import tryout.hibernate.AreaCell;
import use.thm.persistence.dao.GeneralDAO;

public class AreaCellDao extends GeneralDAO<AreaCell> {
	
	private static final long serialVersionUID = 1L;
	
	/* Constructor */
	public AreaCellDao(){
		this.installLoger(AreaCell.class);
	}
	
	/**
	 * Find all BusinessProvider
	 * 
	 * @param businessProviderID
	 * @param first
	 * @param max
	 * @return
	 */
	public List<Consumer> findFirstConsumerDependingOnBusinessProvider(int businessProviderID,int first, int max){
		log.debug("find all BusinessProvider");
		Query q = getSession().createQuery("from Consumer c where c.businessProvider.businessProviderNo = :id").setParameter("id", businessProviderID);	
		
		q.setFirstResult(first);
		q.setMaxResults(max);
		
		@SuppressWarnings("unchecked")
		List<Consumer> allConsumer = q.list();
		
		return allConsumer;
	}
	
		
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findByCriteria(java.util.Map, java.util.List)
	 */
	@Override
	public List<Consumer> findByCriteria(Map<String,Object> criteria, List<String> orderByList, Map<String, String> filter){
		log.debug("findByCriteria Portaluserser Dao");
		return this.findByCriteria("Consumer", 0, -1, criteria, orderByList, filter);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findByCriteria(java.util.Map, java.util.List, int, int)
	 */
	@Override
	public List<Consumer> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first, int max) {
		return this.findByCriteria("Consumer", first, max, whereBy, orderByList, filter);
	}
    
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findLazyAll(int, int)
	 */
    @Override
    public List<Consumer> findLazyAll(int first, int max){
    	return this.findLazyAll("Consumer", first, max);
    }
    
    /*
     * (non-Javadoc)
     * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#count()
     */
	@Override
	public int count(){
		log.debug("count Consumer");
		Query q = getSession().createQuery("select count(c) from Consumer c");
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}
	
	
	/**
	 * Count only the Consumer, which depending on a BusinessProvider.
	 * @param businessProviderID
	 * @return
	 */
	public int countDependingOnBusinessProviderID(int businessProviderID) {
		log.debug("count Consumer");
		Query q = getSession().createQuery("select count(c) from Consumer c where c.businessProvider.businessProviderNo = :id").setParameter("id", businessProviderID);	
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#countByCriteria(java.util.Map, java.util.Map)
	 */
	@Override
	public int countByCriteria(Map<String, Object> whereBy,
			Map<String, String> filter) {
		return this.countByCriteria("Consumer", whereBy, filter);
	}

	/*
	 * (non-Javadoc)
	 * @see de.applicodata.serviceportal.persistence.interfaces.DaoInterface#getID(java.lang.Object)
	 */
	@Override
	public Map<String, Object> getID(Consumer instance) {
		Map<String, Object> id = new HashMap<String, Object>();
		id.put("consumerNo", instance.getConsumerNo());
		
		return id;
	}
	
	/**
	 * Read all E-Mails from Service Provider List.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllEMails(){
		return (List<String>) this.getColumn("email","Consumer");
	}
	
	public List<Consumer> findByHQL(String hql, int first, int max) {
		return this.findByHQLGeneral(hql, first, max);
	}

	public List<AreaCell> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AreaCell> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first,
			int max) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AreaCell> findLazyAll(int first, int max) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countByCriteria(Map<String, Object> whereBy,
			Map<String, String> filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map<String, Object> getID(AreaCell instance) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

/**
 * 
 */
package basic.persistence.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.PersistentObjectException;
import org.hibernate.PropertyAccessException;
import org.hibernate.PropertyValueException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;

import basic.persistence.model.IPrimaryKeys;
import basic.persistence.type.IntLongTupel;
import basic.persistence.util.HibernateUtilByAnnotation;
import basic.zBasic.persistence.IConstantHibernateZZZ;
import use.thm.persistence.model.AreaCell;

/**
 * 
 * @author DKatzberg
 * @author TRueping (added some functions)
 */
public abstract class GeneralDAO<T> implements IDaoInterface<T>{
		
	private static final long serialVersionUID = 1L;
	protected Log log = null;
	
	//Stale - Optimistic Locking
	private boolean staleObjectStateException = false;
	private T actualObject;
	private T databaseObject;
	
	/**
	 * Install the Logger. This is important, because ...
	 * ... 1. without a Logger -> Nullpointer
	 * ... 2. this method set the class var ! 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void installLoger(Class t){
		log = LogFactory.getLog(t);
		this.setT(t);
	}
		
	//Vars
	private Class<T> t = null;
	
	private Session session = null;
	

	/**
	 * Returns the actual session.
	 * @return
	 */
	public Session getSession() {
		//if session is not installed -> open once (and only once!)
		if(session==null){
			
			//open session in view
			session = HibernateUtilByAnnotation.getHibernateUtil().getCurrentSession();
		}
		
		
		//empty or closed? make a new session
		if(session==null || !session.isOpen()) {
			SessionFactory sf = HibernateUtilByAnnotation.getHibernateUtil().getSessionFactory();
			if (sf!=null){
				sf.openSession();
			}else{
				System.out.println("SessionFactory kann nicht erstellt werden. Tip: Alternativ den EntityManager verwenden oder ... (Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial). ");
			}
		}
		
		return session;
	}
	
	/** Erweitert von FGL, weil der HibernateContextProvider (per ZKernel) auch die Session holen kann.
	 * 
	 */
	public static Session getSessionObject(){
		Session session = HibernateUtilByAnnotation.getHibernateUtil().getCurrentSession();
		//empty or closed? make a new session
		if(session==null || !session.isOpen()) {
			SessionFactory sf = HibernateUtilByAnnotation.getHibernateUtil().getSessionFactory();
			if (sf!=null){
				sf.openSession();
			}else{
				System.out.println("SessionFactory kann nicht erstellt werden. Tip: Alternativ den EntityManager verwenden oder ... (Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial). ");
			}
		}
		return session;
	}
	
	/** Erweitert von FGL, weil der HibernateContextProvider (per ZKernel) auch die Session holen kann.
	 * 
	 *  
	 * 
	 * @param session
	 */
	public void setSession(Session session){
		this.session = session;
	}
	
	public Log getLog(){
		if(this.log==null){
			this.installLoger(this.getClass());			
		}
		return this.log;
	}
	
	/**
	 * Session is open
	 */
	protected void begin() {
		try {
			Session session = this.getSession();
			session.beginTransaction();
		} catch (Exception e) {
			this.getLog().error("Method begin failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n", e);
		}
	}

	/**
	 * Commit Transaction
	 */
	protected void commit() {
		try {
			if (!getSession().getTransaction().wasCommitted()) {
//				tx.commit();
				getSession().getTransaction().commit();
			}
			getSession().flush();
		} catch (Exception e) {
			this.getLog().error("Method commit failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() + "\n", e);
		}
//		this.session = null;
	}
	
	/**
	 * Rollback, if transaction missed
	 */
	protected void rollback() {
		
		//Rollback
		try {
			this.getSession().getTransaction().rollback();
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
		}
		
		//Close
		try {
			this.getSession().close();
		} catch (HibernateException e) {
			log.error("Method rollback failed +\n" + session.hashCode() +"\n ThreadID:" + Thread.currentThread().getId() + "\n" , e);
//			System.out.println(e.getMessage());
		}
		session = null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.applicodata.serviceportal.persistence.interfaces.DaoInterface#clearFirstLevelCache(java.lang.Object)
	 */
	public void clearFirstLevelCache(T instance){
		try {
			getSession().clear();
			getSession().refresh(instance);		
		} catch (Exception e) {
			log.error("Method clearFirstLevelCache failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() + "\n" , e);
		}
	}
	
	/**
	 * This method update three Vars. It saves the actualObject, the database Object and the sets
	 * the instance Change to true;
	 * @param instance
	 * @return
	 */
	private boolean staleObjectStateException(T instance, StaleObjectStateException er){
		System.out.println("StaleObjectStateException: "+er);
		this.setStaleObjectStateException(true);
		this.setActualObject(instance);
		
		this.getSession().evict(instance);
		
		//read actual DB state
		T dataBaseInstance = this.findByCriteria(this.getID(instance), new ArrayList<String>(), new HashMap<String, String>(),0,1).get(0);
		this.setDatabaseObject(dataBaseInstance);
		
		//return ever false, for non success delete/update action
		return false;
	}
	
	/**
	 * This method reset the three vars for staleObjectStateExceptions.
	 */
	private void resetStaleObjectStateException(){
		this.setStaleObjectStateException(false);
		this.setActualObject(null);
		this.setDatabaseObject(null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#delete(java.lang.Object)
	 */
	//@Override
	public boolean delete(T instance) {
		log.debug("deleting " + this.toString() + " instance");

		try {
			this.begin();
			getSession().delete(instance);
			this.commit();
			log.debug("delete successful");
			return true;
		}
		catch (NonUniqueObjectException non) {
			log.error("Method delete failed NonUniqueObjectException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , non);
			System.out.println("NON UNIQUE!!!");
			return this.helpCatchException(non, instance);			
		}
		catch(StaleObjectStateException er){
			log.error("Method delete failed StaleObjectStateException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , er);
			System.out.println("STALE!!!");
			return this.staleObjectStateException(instance, er);
		}
		catch(HibernateException he){
			log.error("Method delete failed HibernateException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , he);
			System.out.println("HIBERNATE EXCEPTION!!!!");
			he.printStackTrace();
			 return false;
		}
		finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug("rollback executed");
			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.applicodata.serviceportal.persistence.interfaces.DaoInterface#findById(de.applicodata.serviceportal.persistence.interfaces.PrimaryKeys)
	 */
	//@Override
	public T findByKey(IPrimaryKeys primaryKey) {
//		log.debug("getting " + this.getT().toString() + " instance with id: " + primaryKey);

		try {
			this.begin();
			@SuppressWarnings("unchecked")
			T instance = (T) getSession().get(this.getT(), primaryKey);
			this.commit();
			if (instance == null) {
				log.debug("get successful, no instance found");
			}
			else {
				log.debug("get successful, instance found");
				//getSession().refresh(instance);//RELOAD FROM DB!
			}
			return instance;
		}
		catch (Exception e) {
			log.error("Method findByKey failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , e);
			return null;
		}
		finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug("Method findByKey rollback executed");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findById(int)
	 */
	@SuppressWarnings("unchecked")
	//@Override
	public T findById(int id) {
		log.debug("getting " + this.getT().toString() + " instance with id: " + id);

		try {
			this.begin();
			T instance = (T) getSession().get(this.getT(), id);
			this.commit();
			if (instance == null) {
				log.debug("get successful, no instance found");
			}
			else {
				log.debug("get successful, instance found");
				//getSession().refresh(instance);//RELOAD FROM DB!
			}
			return instance;
		}
		catch (Exception e) {
			log.error("Method findById failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , e);
			return null;
		}
		finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug("rollback executed");
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#persist(java.lang.Object)
	 */
	//@Override
	public boolean persist(T instance) {
		log.debug("persisting " + this.toString() + " instance");
		
		try {
			this.begin();
			getSession().persist(instance);
			this.commit();
			log.debug("persist successful");
			return true;
		}
		catch(PropertyValueException pve){
			log.error("Method persist failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , pve);
			log.error("persist failed non null reference has a null reference", pve);
			System.out.println("persist failed non null reference has a null reference"+pve);
			pve.printStackTrace();
			return false;
		}
		catch(PropertyAccessException pae){
			return this.saveNotPersist(instance);
		}
		catch(PersistentObjectException poe){
			return this.saveNotPersist(instance);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug("rollback executed");
			}
		}
	}
	
	/**
	 * If persist do not work, use the save method.
	 * @param instance
	 * @return
	 */
	private boolean saveNotPersist(T instance){
		try{
			getSession().save(instance);
			this.commit();
			log.debug("persist successful -> PropertyAccessException");
			return true;
		}catch (Exception e) {
			log.error("Method saveNotPersist failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , e);
//			log.error("persist failed", e);
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#update(java.lang.Object)
	 */
	//@Override
	public boolean update(T instance) {
		log.debug("updating " + this.getT().toString() +  " instance");
		
		try {
			this.begin();
			this.getSession().update(instance);
			this.commit();
			log.debug("update successful");
			return true;
		}
		catch (NonUniqueObjectException non) {
			return this.helpCatchException(non, instance);
		}
		catch(PropertyValueException pve){
			log.error("Method update failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , pve);
//			log.info("persist failed non null reference has a null reference", pve);
//			System.out.println("persist failed non null reference has a null reference"+pve);
			pve.printStackTrace();
			return false;
		}
		catch(StaleObjectStateException er){
			return this.staleObjectStateException(instance, er);
		}
		catch(HibernateException he){
			 return this.helpCatchException(he, instance);
		}
		finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug("rollback executed");
			}
		}
	}
	
	/**
	 * This method try to evict an instance and clear the 
	 * @param he
	 * @param instance
	 * @return
	 */
	private boolean helpCatchException(HibernateException he, T instance){
		try {
			this.begin();
			getSession().evict(instance);
			getSession().clear();	
			
			getSession().merge(instance);
			this.commit();
			getSession().flush();
			getSession().refresh(instance);			
			log.debug("update successful");
			this.resetStaleObjectStateException();
			return true;
		}
		catch(PropertyValueException pve){
			log.error("Method helpCatchException failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , pve);
//			log.error("persist failed non null reference has a null reference", pve);
//			System.out.println("persist failed non null reference has a null reference"+pve);
			pve.printStackTrace();
			return false;
		}
		catch (Exception ee) {
			log.error("Method helpCatchException failed +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , ee);
			log.error("update failed @saveorUpdate ", he);
			he.printStackTrace();
			log.error("update failed @with clear/evict ", ee);
			ee.printStackTrace();
			System.out.println("update failed @saveorUpdate "+ he);
			System.out.println("update failed @with clear/evict "+ ee);
			return false;
		}
	}

	/**
	 * Close Session. 
	 * 
	 * ATTENTION: IF YOU CLOSE THE SESSION, LAZY LOADING DOES NOT WORK ANY MORE!
	 * You should close a session on the end of a user-session.
	 */
//	public void close() {
//		this.getSession().close();
//		this.session = null;
//	}
	
	/**
	 * This method creates a FindByCriteria HQL.
	 * 
	 * @param criteria
	 * @param orderByList
	 * @param filter
	 * @return
	 */
	protected String createHQLCriteria(Map<String,Object> criteria, List<String> orderByList, Map<String, String> filter){
		
		DaoHelper daoHelper = new DaoHelper();
		
		String hql = "";
		StringBuffer hqlBuffer = new StringBuffer();
		
		boolean whereByCriteria = false;
		boolean whereByClauselActiv = false;
		
		// Set where Tag ONLY if filter or criteria have items for a where clausel
		if(criteria.keySet().size()>0 || filter.keySet().size()>0){
			hqlBuffer.append("where ");
			whereByClauselActiv = true;
		}	
		
		//for the filter
		if(criteria.keySet().size()>0)
			whereByCriteria = true;
		
		/* WHERE BY */
		Iterator<String> coloums = criteria.keySet().iterator();
		while(coloums.hasNext()){
			String coloumName = coloums.next();
			
			String idName = daoHelper.valueNameChanger(coloumName);
			
			//update hql order
			hqlBuffer.append("g."+coloumName+" = :"+idName);
			
			//are more criteria?
			if(coloums.hasNext())
				hqlBuffer.append(" and ");
		}
		
		/* LIKE / STRING MATCHING */
		if(filter.keySet().size()>0){
			//empty sign + and
			if(whereByCriteria)
				hqlBuffer.append(" and ");
		}	
		
		Iterator<String> coloumsFilter = filter.keySet().iterator();
		while(coloumsFilter.hasNext()){
			String coloumName = coloumsFilter.next();
			
			//update hql order
			//FGL Define filters from a joined table starting with a "h." 
			String sproof = coloumName.substring(0, 2); 
			if(sproof.equalsIgnoreCase("h.")){
				hqlBuffer.append(coloumName+" like '%"+filter.get(coloumName)+"%'");
			}else{
				hqlBuffer.append("g."+coloumName+" like '%"+filter.get(coloumName)+"%'");
			}
			//hqlBuffer.append("g."+coloumName+" like '%"+filter.get(coloumName)+"%'");
			
			//are more criteria?
			if(coloumsFilter.hasNext())
				hqlBuffer.append(" and ");
		}
		
		
		/* ORDER BY */
		if(orderByList.size()>0){
			//empty sign, 
			if(whereByClauselActiv)
				hqlBuffer.append(" ");
			
			//orderby command
			hqlBuffer.append("order by ");
			
			Iterator<String> iteOrderBy = orderByList.iterator();
			//add first element
			//FGL Define orderBy from a joined table starting with a "h."
			if(iteOrderBy.hasNext()) {
				String scolumn = iteOrderBy.next();
				String sproof = scolumn.substring(0, 2); 
				if(sproof.equalsIgnoreCase("h.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(scolumn);
				}else{
					hqlBuffer.append("g."+scolumn);
				}				
			}
														
			//every other elements
			while(iteOrderBy.hasNext()){
				String scolumn = iteOrderBy.next();
				String sproof = scolumn.substring(0, 2); 
				if(sproof.equalsIgnoreCase(", h.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(", " + scolumn);
				}else{
					hqlBuffer.append(", g."+scolumn);
				}				
			}			
		}
		
		hql = hqlBuffer.toString();
		
		return hql;
	}
	
	
	/**
	 * This method creates a FindByCriteria HQL.
	 * 
	 * @param criteria
	 * @param orderByList
	 * @param filter
	 * @return
	 */
	protected String createHQLCriteriaAdditionalForJoined(Map<String,Object> criteria, List<String> orderByList, Map<String, String> filter){
		
		DaoHelper daoHelper = new DaoHelper();
		
		String hql = "";
		StringBuffer hqlBuffer = new StringBuffer();
		
		boolean whereByCriteria = false;
		boolean whereByClauselActiv = false;
		
		// Set where Tag ONLY if filter or criteria have items for a where clausel
		if(criteria.keySet().size()>0 || filter.keySet().size()>0){
			hqlBuffer.append(" and ");
			whereByClauselActiv = true;
		}	
		
		//for the filter
		if(criteria.keySet().size()>0)
			whereByCriteria = true;
		
		/* WHERE BY */
		Iterator<String> coloums = criteria.keySet().iterator();
		while(coloums.hasNext()){
			String coloumName = coloums.next();
			
			String idName = daoHelper.valueNameChanger(coloumName);
			
			//update hql order
			//FGL: Define filters of the second tabel starting with h, thirs with i, ....
			String sproof = coloumName.substring(0, 2); 
			if(sproof.equalsIgnoreCase("h.")){
				hqlBuffer.append(coloumName+" = :"+idName);
			}else if(sproof.equalsIgnoreCase("i.")){
				hqlBuffer.append(coloumName+" = :"+idName);
			}else{
				hqlBuffer.append("g."+coloumName+" = :"+idName);
			}
			
			//are more criteria?
			if(coloums.hasNext())
				hqlBuffer.append(" and ");
		}
		
		/* LIKE / STRING MATCHING */
		if(filter.keySet().size()>0){
			//empty sign + and
			if(whereByCriteria)
				hqlBuffer.append(" and ");
		}	
		
		Iterator<String> coloumsFilter = filter.keySet().iterator();
		while(coloumsFilter.hasNext()){
			String coloumName = coloumsFilter.next();
			
			//update hql order
			//FGL: Define filters of the second tabel starting with h.
			String sproof = coloumName.substring(0, 2); 
			if(sproof.equalsIgnoreCase("h.")){
				hqlBuffer.append(coloumName+" like '%"+filter.get(coloumName)+"%'");
			}else if(sproof.equalsIgnoreCase("i.")){
				hqlBuffer.append(coloumName+" like '%"+filter.get(coloumName)+"%'");
			}else{
				hqlBuffer.append("g."+coloumName+" like '%"+filter.get(coloumName)+"%'");
			}
			//are more criteria?
			if(coloumsFilter.hasNext())
				hqlBuffer.append(" and ");
		}
		
		
		/* ORDER BY */
		if(orderByList.size()>0){
			//empty sign, 
			if(whereByClauselActiv)
				hqlBuffer.append(" ");
			
			//orderby command
			hqlBuffer.append("order by ");
			
//			Iterator<String> iteOrderBy = orderByList.iterator();
//			//add first element
//			if(iteOrderBy.hasNext())
//				hqlBuffer.append("g."+iteOrderBy.next());
//			
//			//every other elements
//			while(iteOrderBy.hasNext())
//				hqlBuffer.append(", g."+iteOrderBy.next());
//		}
			
			Iterator<String> iteOrderBy = orderByList.iterator();
			//add first element
			//FGL Define orderBy from a joined table starting with a "h."
			if(iteOrderBy.hasNext()) {
				String scolumn = iteOrderBy.next();
				String sproof = scolumn.substring(0, 2); 
				if(sproof.equalsIgnoreCase("h.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(scolumn);
				}else if(sproof.equalsIgnoreCase("i.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(scolumn);
				}else{
					hqlBuffer.append("g."+scolumn);
				}				
			}
														
			//every other elements
			while(iteOrderBy.hasNext()){
				String scolumn = iteOrderBy.next();
				String sproof = scolumn.substring(0, 2); 
				if(sproof.equalsIgnoreCase("h.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(", " + scolumn);
				}else if(sproof.equalsIgnoreCase("i.")){
//					hqlBuffer.append(scolumn+" like '%"+filter.get(scolumn)+"%'");
					hqlBuffer.append(", " + scolumn);
				}else{
					hqlBuffer.append(", g."+scolumn);
				}				
			}			
		}
		
		hql = hqlBuffer.toString();
		
		return hql;
	}
	
	/**
	 * Add the Elements for the whereBy clauses.
	 * @param q
	 * @param whereBy
	 */
	protected void addWhereByElements(Query q, Map<String,Object> whereBy){
		DaoHelper daoHelper = new DaoHelper();
		
		//add parameters
		Iterator<String> coloums = whereBy.keySet().iterator();
		while(coloums.hasNext()){

			//load Data
			String coloumName = coloums.next();
			Object value = whereBy.get(coloumName);
			
			//create Parameter
			String idName = daoHelper.valueNameChanger(coloumName);
			q.setParameter(idName, value);
		}
	}
	
	/**
	 * Add till and from parameter to the query.
	 * @param q
	 * @param from
	 * @param till
	 */
	protected void addDateElements(Query q, Date from, Date till) {
		if (from != null) {
		    q.setParameter("from", from);
		}
		if (till != null) {
	        q.setParameter("till", till);
		}
	}
	
	/**
	 * General find by Criteria
	 * 
	 * table = TableName @ Hibernate Object
	 * first = Index of the first Element that be found at table
	 * max = max Number of find Elements
	 * criteria = WhereBy Clausel
	 * orderByList = Sort List
	 * filter = Filter String/int and this stuff (search mode) 
	 * 
	 * @param table
	 * @param first
	 * @param max
	 * @param whereBy
	 * @param orderByList
	 * @param filter 
	 * @return
	 */
	protected List<T> findByCriteria(String table, int first, int max, Map<String,Object> whereBy, List<String> orderByList, Map<String, String> filter){
				
		this.begin();
		//initialize
		String hql = "from "+table+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, orderByList, filter);
		
		//create query		
		Query q = getSession().createQuery(hql);
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		q.setFirstResult(first);
		q.setMaxResults(max);
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		
		this.commit();
		
		//return this.refreshList(list);
		return list;
	}
	
	//FGL: 
	protected List<T> findJoinedByCriteria(String tableName1, String column1, String tableName2, String column2, int first, int max, Map<String,Object> whereBy, List<String> orderByList, Map<String, String> filter){
		
		this.begin();
		
		//initialize
		String hql = " from "+tableName1+" g, "+tableName2+" h where g."+column1+"= h."+column2 ;
		
		//Create hql String
		String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, orderByList, filter);
		hql = hql + hqltemp;
		System.out.println("GeneralDAO.findJoinedByCriteria() - HQL : " + hql);
		
		//create query		
		Query q = getSession().createQuery(hql);
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		q.setFirstResult(first);
		q.setMaxResults(max);
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		List<T> listReturn = new ArrayList<T>();
		int iPositionCurrent=0; 
		int iPosition2Find=0;
		Iterator<T> it = list.iterator();
		while(it.hasNext()){
			T obja = it.next();
			iPositionCurrent = 0;
			for(T obj : (T[])obja){				
				if(iPositionCurrent==iPosition2Find){
					listReturn.add(obj);								
				}
				iPositionCurrent++;
			}
		}

		this.commit();
		
		//return this.refreshList(list);
		System.out.println("GeneralDAO.findJoinedByCriteria() - listReturn.size() : " + listReturn.size());
		return listReturn;
	}
	
	//FGL: 
		protected List<T> findJoined03ByCriteria(String tableName1, String column1, String tableName2, String column2, String columnJoinTable1WithTable3, String tableName3, String column3, int first, int max, Map<String,Object> whereBy, List<String> orderByList, Map<String, String> filter){
			
			this.begin();
			
			//initialize
			String hql = " from "+tableName1+" g, "+tableName2+" h, " +tableName3+" i where g."+column1+"= h."+column2+" and g."+columnJoinTable1WithTable3+"=i."+column3 ;
			
			//Create hql String
			String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, orderByList, filter);
			hql = hql + hqltemp;
			System.out.println("GeneralDAO.findJoined03ByCriteria() - HQL : " + hql);
			
			//create query		
			Query q = getSession().createQuery(hql);
			
			//add criteria(whereBy) Elements to the List
			this.addWhereByElements(q, whereBy);
			
			q.setFirstResult(first);
			q.setMaxResults(max);
			
			@SuppressWarnings("unchecked")
			List<T> list = q.list();
			
			List<T> listReturn = new ArrayList<T>();
			int iPositionCurrent=0; 
			int iPosition2Find=0;
			Iterator<T> it = list.iterator();
			while(it.hasNext()){
				T obja = it.next();
				iPositionCurrent = 0;
				for(T obj : (T[])obja){				
					if(iPositionCurrent==iPosition2Find){
						listReturn.add(obj);								
					}
					iPositionCurrent++;
				}
			}

			this.commit();
			
			//return this.refreshList(list);
			System.out.println("GeneralDAO.findJoined03ByCriteria() - listReturn.size() : " + listReturn.size());
			return listReturn;
		}
	
	
	public int countByHQL(String hql) {
		this.begin();
		Query q = getSession().createQuery(hql);
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}
	
	protected List<T> findByHQLGeneral(String hql, int first, int max) {
		this.begin();
		Query q = getSession().createQuery(hql);
		q.setFirstResult(first);
		q.setMaxResults(max);
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		this.commit();
		return list;
	}
		
	protected List<Integer> findIDColumnByHQL(String hql) {
		this.begin();
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) q.list();
		
		this.commit();
		return list;
	}
	
	protected List<Integer> findIDColumnBySQL(String sql) {
		this.begin();
		Query q = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) q.list();
		
		this.commit();
		return list;
	}
	
	protected int countBySQL(String sql) {
		this.begin();
		Query q = getSession().createSQLQuery(sql);
		this.commit();
		return ((BigInteger)q.uniqueResult()).intValue();
	}
	
	
	/**
	 * This method reload all Elements from the DB and refreh the first level cache.
	 * @param list
	 * @return
	 */
	/*protected List<T> refreshList(List<T> list){
		Iterator<T> iteList =  list.iterator();
		List<T> refreshList = new ArrayList<T>();//return list
		
		//touch every object
		while(iteList.hasNext()){
			T instance = iteList.next();
			getSession().refresh(instance);//RELOAD FROM DB!
			refreshList.add(instance);
		}
		
		return refreshList;
	}*/
	
	/**
	 * 
	 * @param table
	 * @param coloum
	 * @param whereBy
	 * @param from
	 * @param till
	 * @return
	 */
	protected List<T> findByDate(String table, String coloum, Map<String, Object> whereBy, Date from, Date till){
		
		this.begin();
		
		//initialize
		String hql = "from "+table+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		
		boolean otherWhereClausel;
		
		//check, if other where clausel exist
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(coloum, from, till, otherWhereClausel);
		
		hql += " order by "+coloum;

		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		//add date elements(from/till), only if one border is set 
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		this.commit();
		
		//return this.refreshList(list);
		return list;
		
	}
	
	protected List<T> findByDateAndLimit(String table,  String coloum, Map<String, Object> whereBy, Date from, Date till, int first, int max){
		
		this.begin();
		
		//initialize
		String hql = "from "+table+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		
		boolean otherWhereClausel;
		
		//check, if other where clausel exist
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(coloum, from, till, otherWhereClausel);
		
		hql += " order by "+coloum;

		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		
		q.setFirstResult(first);
		q.setMaxResults(max);
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		//add date elements(from/till), only if one border is set 
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		this.commit();
		
		//return this.refreshList(list);
		return list;
		
	}
	
	protected List<T> findByDateCriteriaAndLimit(String table, String coloum, String filterColumn, List<String> filterValues, Map<String, Object> whereBy, List<String> orderByList, Map<String, String> filter, Date from, Date till, int first, int max){
		
		this.begin();
		
		//initialize
		String hql = "from "+table+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
		
		boolean otherWhereClausel;
		
		//check, if other where clausel exist
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(coloum, from, till, otherWhereClausel);

		if (filterValues != null && filterValues.size()>0) {
			if (hql.contains("where")) {
				hql += " and ";
			} else {
				hql += " where ";
			}
			hql += filterColumn+" in ('";
			boolean firstValue = true;
			for (String filterValue : filterValues) {
				if (!firstValue) {
					hql+=", '";
				}
				hql += filterValue+"'";
				firstValue = false;
			}
			hql += ")";
		}
		
//		SELECT * FROM serviceportal.business_provider_order where status in ('complete', 'open');
		
		if(orderByList.size()>0){
			//empty sign, 
			if(whereBy.size()>0)
				hql+=" ";
			
			//orderby command
			hql+="order by ";
			
			Iterator<String> iteOrderBy = orderByList.iterator();
			//add first element
			if(iteOrderBy.hasNext())
				hql += "g."+iteOrderBy.next();
			
			//every other elements
			while(iteOrderBy.hasNext())
				hql += ", g."+iteOrderBy.next();
		}
		
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		if (first != 0 && max != 0) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		//add date elements(from/till), only if one border is set 
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		this.commit();
		
		//return this.refreshList(list);
		return list;
		
	}
	
	
	
	/**
	 * Update the hql with an time selection
	 * 
	 * @param coloum Name of Coloum which Hibernate Name
	 * @param from java.util.Date lower time stamp
	 * @param till java.util.Date upper time stamp
	 * @param otherWhereClausel
	 * @return
	 */
	protected String createDateCriteria(String coloum, Date from, Date till, boolean otherWhereClausel) {
		
		String dateBorders;
		
		//if other where clausel set, do no forget the 'and' tag
		if(otherWhereClausel)
			dateBorders = " and ";
		else
			dateBorders = "where ";
		
		if(from!=null && till!=null) {
			dateBorders += "g."+coloum+" BETWEEN :from AND :till";
		} else {
		
		//set lower border (from which time stamp)
		if(from!=null)
			dateBorders += "g."+coloum+" >= :from";
		
		//set only an 'and', if both are set 
		if(from!=null && till!=null)
			dateBorders += " and ";
		
		//set upper border (till which time stamp)
		if(till!=null)
			dateBorders += "g."+coloum+" <= :till";
		}
		return dateBorders;
	}
	
	/**
	 * General findLazyAll for managing the find Lazy methods.
	 */
	protected List<T> findLazyAll(String table, int first, int max){
		log.debug("find all "+this.getClass().toString());
		
		this.begin();
		
		Session objSession = this.getSession();		
		Query q = objSession.createQuery("from "+table+" p") ;
		
		//FGL: 20171031 - Sinnvolle Erweiterung
		if(first <= -1) {
			//nix setzen. Es werden die Defaults genommen
		}else{
			q.setFirstResult(first);
		}
		
		if(max <= 0){
			//nix setzen. Es werden die Defaults genommen
		}else{
			q.setMaxResults(max);
		}
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		
		this.commit();
		
		//return this.refreshList(list);
		return list;
	}
	
	/**
	 * FGL: Sinnvolle Ergänzung: Nimm alles, wenn es keine Parameter gibt.
	 * @param table
	 * @return
	 */
	protected List<T> findLazyAll(String table){
		log.debug("find all without size restrictions"+this.getClass().toString());		
		List<T> list = this.findLazyAll(table, 0,-1);
		return list;
	}

	/**
	 * This method use the hql generation of the findByCriteria, but it count only the elements AND DO NOT return or load a list of Elements
	 * 
	 * @param tableName
	 * @param whereBy
	 * @param filter
	 * @return
	 */
	protected int countByCriteria(String tableName, Map<String, Object> whereBy, Map<String, String> filter) {
				
		this.begin();
		
		//initialize
		String hql = "select count(g) from "+tableName+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
		
		//count 
		Query q = getSession().createQuery(hql);

		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		int count = ((Long)q.uniqueResult()).intValue();
		this.commit();
		return count;
	}
	
	/**
	 * This method use the hql generation of the findByCriteria, but it count only the elements AND DO NOT return or load a list of Elements
	 * 
	 * @param tableName
	 * @param whereBy
	 * @param filter
	 * @return
	 */
	protected int countJoinedByCriteria(String tableName1, String column1, String tableName2, String column2, Map<String, Object> whereBy, Map<String, String> filter) {
				
		this.begin();
		
		//initialize
		String hql = "select count(g) from "+tableName1+" g, "+tableName2+" h  where g."+column1+"= h."+column2;
		
		//Create hql String
		String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, new ArrayList<String>(), filter);
		//String hqltemp = this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
		hql = hql + hqltemp;
		System.out.println("GeneralDAO.countJoinedByCriteria() - HQL : " + hql);
		
		//count 
		Query q = getSession().createQuery(hql);

		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		int count = ((Long)q.uniqueResult()).intValue();
		this.commit();
		System.out.println("GeneralDAO.countJoinedByCriteria() - counted : " + count);
		return count;
	}
	
	/**
	 * This method use the hql generation of the findByCriteria, but it count only the elements AND DO NOT return or load a list of Elements
	 * 
	 * @param tableName
	 * @param whereBy
	 * @param filter
	 * @return
	 */
	protected int countJoined03ByCriteria(String tableName1, String column1, String tableName2, String column2, String columnJoinTable1WithTable3, String tableName3, String column3, Map<String, Object> whereBy, Map<String, String> filter) {
				
		this.begin();
		
		//initialize
		String hql = "select count(g) from "+tableName1+" g, "+tableName2+" h, " +tableName3+" i where g."+column1+"= h."+column2+" and g."+columnJoinTable1WithTable3+"= i."+column3;
		
		//Create hql String
		String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, new ArrayList<String>(), filter);
		//String hqltemp = this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
		hql = hql + hqltemp;
		System.out.println("GeneralDAO.countJoined03ByCriteria() - HQL : " + hql);
		
		//count 
		Query q = getSession().createQuery(hql);

		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		int count = ((Long)q.uniqueResult()).intValue();
		this.commit();
		System.out.println("GeneralDAO.countJoined03ByCriteria() - counted : " + count);
		return count;
	}
	
	/**
	 * This method use the hql generation of the findByCriteria, but it count only the elements AND DO NOT return or load a list of Elements
	 * 
	 * @param tableName
	 * @param whereBy
	 * @param filter
	 * @return
	 */
	protected int countByCriteriaAndDate(String column, String tableName, Map<String, Object> whereBy, Date from, Date till) {
				
		this.begin();
		
		//initialize
		String hql = "select count(g) from "+tableName+" g ";
		
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		
		Boolean otherWhereClausel;
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(column, from, till, otherWhereClausel);
		
		//count 
		Query q = getSession().createQuery(hql);

		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		int count = ((Long)q.uniqueResult()).intValue();
		this.commit();
		return count;
	}
	
	protected int countByCriteriaAndDateAndFilterValues(String column, String tableName, Map<String, Object> whereBy, Map<String, String> filter,  String filterColumn, List<String> filterValues, Date from, Date till) {
			this.begin();
			
			//initialize
			String hql = "select count(g) from "+tableName+" g ";
			
			//Create hql String
			hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
			
			Boolean otherWhereClausel;
			if(whereBy.size()>0)
				otherWhereClausel = true;
			else 
				otherWhereClausel = false;
			
			//only time search, if one border is set
			if(from!=null || till!=null)
				hql += this.createDateCriteria(column, from, till, otherWhereClausel);
			
			if (filterValues != null && filterValues.size()>0) {
				if (hql.contains("where")) {
					hql += " and ";
				} else {
					hql += " where ";
				}
				hql += filterColumn+" in ('";
				boolean firstValue = true;
				for (String filterValue : filterValues) {
					if (!firstValue) {
						hql+=", '";
					}
					hql += filterValue+"'";
					firstValue = false;
				}
				hql += ")";
			}
			
			
			//count 
			Query q = getSession().createQuery(hql);
			
			//add criteria(whereBy) Elements to the List
			this.addWhereByElements(q, whereBy);
			
			if(from!=null || till!=null)
				this.addDateElements(q, from, till);
			
			int count = ((Long)q.uniqueResult()).intValue();
			this.commit();
			return count;
		}
	
	
	protected List<IntLongTupel> countByCriteriaAndGroupBy(String tableName, String column, Map<String, Object> whereBy, String groupBy, Date from, Date till) {
		
		this.begin();
		
		//initialize
//		String hql = "from "+tableName+" g ";
		String hql = "select new IntLongTupel("+groupBy+"("+column+"), count(g)) from "+tableName+" g ";

		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		
		Boolean otherWhereClausel;
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(column, from, till, otherWhereClausel);
		
		hql += " group by "+groupBy+"("+column+")";
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		@SuppressWarnings("unchecked")
		List<IntLongTupel> list = q.list();
		this.commit();
		
		//return this.refreshList(list);
		return list;
	}

	protected List<IntLongTupel> sumByCriteriaAndGroupBy(String tableName, String column, String columnForSum, Map<String, Object> whereBy, String groupBy, Date from, Date till) {
		
		this.begin();
		
		//initialize
//		String hql = "from "+tableName+" g ";
		String hql = "select new IntLongTupel("+groupBy+"("+column+"), sum("+columnForSum+")) from "+tableName+" g ";

		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		
		Boolean otherWhereClausel;
		if(whereBy.size()>0)
			otherWhereClausel = true;
		else 
			otherWhereClausel = false;
		
		//only time search, if one border is set
		if(from!=null || till!=null)
			hql += this.createDateCriteria(column, from, till, otherWhereClausel);
		
		hql += " group by "+groupBy+"("+column+")";
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		if(from!=null || till!=null)
			this.addDateElements(q, from, till);
		
		@SuppressWarnings("unchecked")
		List<IntLongTupel> list = q.list();
		this.commit();
		
		//return this.refreshList(list);
		return list;
		
	}

	protected Integer sumByCriteria(String tableName, String column, Map<String, Object> whereBy) {
	
	this.begin();
	
	//initialize
	String hql = "select sum("+column+") from "+tableName+" g ";

	//Create hql String
	hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
	
	//create query, hql is here complete		
	Query q = getSession().createQuery(hql);
	//add criteria(whereBy) Elements to the List
	this.addWhereByElements(q, whereBy);
	Long result = ((Long)q.uniqueResult());
	this.commit();
	
	Integer sum = (result != null) ? result.intValue() : 0;
	//return this.refreshList(list);
	return sum;
	
}
	
//FGL
	protected Double sumDblByCriteria(String tableName, String column, Map<String, Object> whereBy, Map<String, String>filter) {
		
		this.begin();
		
		//initialize
		String hql = "select sum("+column+") from "+tableName+" g ";

		//Create hql String
		//hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
		
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
//		Long result = ((Long)q.uniqueResult());
		Double result = ((Double) q.uniqueResult());
		this.commit();
		
		Double sum = (result != null) ? result.doubleValue() : 0;
		//return this.refreshList(list);
		return sum;
		
	}
	
	//FGL
		protected Double sumJoinedDblByCriteria(String columnCounted, String tableName1, String column1, String tableName2, String column2, Map<String, Object> whereBy, Map<String, String>filter) {
			
			this.begin();
			
			//initialize
			String hql = "select sum(g."+columnCounted+") from "+tableName1+" g, " +tableName2+" h where g."+column1+"= h."+column2;

			//Create hql String
			//hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
			//hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
			String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, new ArrayList<String>(), filter);
			hql = hql + hqltemp;
			System.out.println("GeneralDAO.sumJoinedDblByCriteria() - HQL : " + hql);
			
			//create query, hql is here complete		
			Query q = getSession().createQuery(hql);
			//add criteria(whereBy) Elements to the List
			this.addWhereByElements(q, whereBy);
//			Long result = ((Long)q.uniqueResult());
			Double result = ((Double) q.uniqueResult());
			this.commit();
			
			Double sum = (result != null) ? result.doubleValue() : 0;
			//return this.refreshList(list);
			System.out.println("GeneralDAO.sumJoinedDblByCriteria() - Summe : " + sum);
			
			return sum;
			
		}
		
		
		
		//FGL
				protected Double sumJoined03DblByCriteria(String columnCounted, String tableName1, String column1, String tableName2, String column2, String columnJoinTable1WithTable3, String tableName3, String column3, Map<String, Object> whereBy, Map<String, String>filter) {
					
					this.begin();
					
					//initialize
					String hql = "select sum(g."+columnCounted+") from "+tableName1+" g, " +tableName2+" h, "+tableName3+" i where g."+column1+"= h."+column2+ " and g."+columnJoinTable1WithTable3+"=i."+column3;

					//Create hql String
					//hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
					//hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), filter);
					String hqltemp = this.createHQLCriteriaAdditionalForJoined(whereBy, new ArrayList<String>(), filter);
					hql = hql + hqltemp;
					System.out.println("GeneralDAO.sumJoined03DblByCriteria() - HQL : " + hql);
					
					//create query, hql is here complete		
					Query q = getSession().createQuery(hql);
					//add criteria(whereBy) Elements to the List
					this.addWhereByElements(q, whereBy);
//					Long result = ((Long)q.uniqueResult());
					Double result = ((Double) q.uniqueResult());
					this.commit();
					
					Double sum = (result != null) ? result.doubleValue() : 0;
					//return this.refreshList(list);
					System.out.println("GeneralDAO.sumJoined03DblByCriteria() - Summe : " + sum);
					
					return sum;
					
				}
	
	protected Integer sumByCriteriaAndDate(String tableName, String column, String dateColumn, Map<String, Object> whereBy, Date from, Date till) {
		
	this.begin();
	
	//initialize
	String hql = "select sum("+column+") from "+tableName+" g ";

	
	//Create hql String
	hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
	
	Boolean otherWhereClausel;
	if(whereBy.size()>0)
		otherWhereClausel = true;
	else 
		otherWhereClausel = false;
	
	//only time search, if one border is set
	if(from!=null || till!=null)
		hql += this.createDateCriteria(dateColumn, from, till, otherWhereClausel);
	
	//create query, hql is here complete		
	Query q = getSession().createQuery(hql);
	//add criteria(whereBy) Elements to the List
	this.addWhereByElements(q, whereBy);
	
	if(from!=null || till!=null)
		this.addDateElements(q, from, till);
	
	Long result = ((Long)q.uniqueResult());
	this.commit();
	
	Integer sum = (result != null) ? result.intValue() : 0;
	//return this.refreshList(list);
	return sum;
	
}
	
	
	protected Timestamp maxDateByCriteria(String tableName, String column, Map<String, Object> whereBy) {
		
	this.begin();
	
	//initialize
	String hql = "select max("+column+") from "+tableName+" g ";

	//Create hql String
	hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
	
	//create query, hql is here complete		
	Query q = getSession().createQuery(hql);
	//add criteria(whereBy) Elements to the List
	this.addWhereByElements(q, whereBy);
	Timestamp result = ((Timestamp)q.uniqueResult());
	this.commit();
	
	Timestamp timestamp  = (result != null) ? result : null;
	//return this.refreshList(list);
	return timestamp;
	
}
	
	
	/**FGL: Gib den Maximalwert einer IntegerSpalte zurück. 
	 *          Inspiriert aus maxDateByCriteria(...)
	 * @param tableName
	 * @param column
	 * @param whereBy
	 * @return
	 */
	protected Integer maxIntegerByCriteria(String tableName, String column, Map<String, Object> whereBy) {
		
	this.begin();
	
	//initialize
	String hql = "select max("+column+") from "+tableName+" g ";

	//Create hql String
	hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
	
	//create query, hql is here complete		
	Query q = getSession().createQuery(hql);
	//add criteria(whereBy) Elements to the List
	this.addWhereByElements(q, whereBy);
	Integer result = ((Integer)q.uniqueResult());
	this.commit();
	
	Integer intReturn  = (result != null) ? result : null;
	//return this.refreshList(list);
	return intReturn;
	
}
	
	/**FGL: Gib den Minimalwert einer IntegerSpalte zurück. 
	 *          Inspiriert aus maxDateByCriteria(...)
	 * @param tableName
	 * @param column
	 * @param whereBy
	 * @return
	 */
	protected Integer minIntegerByCriteria(String tableName, String column, Map<String, Object> whereBy) {
		
	this.begin();
	
	//initialize
	String hql = "select min("+column+") from "+tableName+" g ";

	//Create hql String
	hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
	
	//create query, hql is here complete		
	Query q = getSession().createQuery(hql);
	//add criteria(whereBy) Elements to the List
	this.addWhereByElements(q, whereBy);
	Integer result = ((Integer)q.uniqueResult());
	this.commit();
	
	Integer intReturn  = (result != null) ? result : null;
	//return this.refreshList(list);
	return intReturn;	
}
	
	protected List<T> findByCriteriaAndDaysBefore(String table, String column, Map<String, Object> whereBy, int daysBefore){
		
		this.begin();
		
		//initialize
		String hql = "from "+table+" g ";
		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		if (whereBy != null && whereBy.size() > 0) {
			hql += " and "; 
		}
		
		hql += "date(g."+column+")-current_date()="+daysBefore;
		try {		
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		@SuppressWarnings("unchecked")
		List<T> list = q.list();
		this.commit();
		
		//return this.refreshList(list);
		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
	protected List<String> findListByTableNameColumnAndCriteria(String tableName, String column,Map<String, Object> whereBy) {
		
		this.begin();
		
		//initialize
		String hql = "select distinct "+column+" from "+tableName+" g ";

		//Create hql String
		hql += this.createHQLCriteria(whereBy, new ArrayList<String>(), new HashMap<String, String>());
		if (whereBy.size() < 1) {
			hql += " where "; 
		} else {
			hql += " and ";
		}
		hql += "g."+column+" != null and g."+column+" !=''";
		//create query, hql is here complete		
		Query q = getSession().createQuery(hql);
		//add criteria(whereBy) Elements to the List
		this.addWhereByElements(q, whereBy);
		
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) q.list();
		this.commit();
		
		//return this.refreshList(list);
		return list;
		
	}
	
	
	
	/**
	 * This method return all elements from one column.
	 * 
	 * @param column
	 * @return
	 */
	protected List<?> getColumn(String column, String tableName) {
		Query q = getSession().createQuery("select "+ column +" from "+tableName+" g");
		List<?> list = q.list();
		
		return list;
	}
	
	/**
	 * This method return all elements from one column.
	 * FGL 20170809: Sortiert nach den Werten einer anderen Spalte, aufsteigend, absteigend oder unsortiert.
	 * @param column
	 * @return
	 */
	protected List<?> getColumnSortedByColumn(String sTableName, String sColumn,  int iSortingDirection, String sColumnSorted) {
		List<?> listReturn = null;
		Query q = null;
		switch (iSortingDirection){
		case IConstantHibernateZZZ.iSORT_ASCENDING:
			q = getSession().createQuery("select "+ sColumn +" from "+ sTableName +" g " + "order by " + sColumnSorted + " " + IConstantHibernateZZZ.sSORT_ASCENDING);
			break;
		case IConstantHibernateZZZ.iSORT_DESCENDING:
			q = getSession().createQuery("select "+ sColumn +" from "+ sTableName +" g " + "order by " + sColumnSorted + " " + IConstantHibernateZZZ.sSORT_DESCENDING);
			break;
		case IConstantHibernateZZZ.iSORT_NONE:
			q= getSession().createQuery("select "+ sColumn +" from "+ sTableName +" g " + "order by " + sColumnSorted);
			break;
		default:			
		}
		if(q!=null){ listReturn = q.list(); }

		return listReturn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.applicodata.serviceportal.persistence.interfaces.DaoInterface#refresh(java.lang.Object)
	 */
	//@Override
	public void refresh(T instance) {
		this.getSession().clear();
		this.getSession().refresh(instance);
	}

	/* GETTER / SETTER */
	public Class<T> getT() {
		return t;
	}

	public void setT(Class<T> t) {
		this.t = t;
	}

	public boolean isStaleObjectStateException() {
		return staleObjectStateException;
	}

	private void setStaleObjectStateException(boolean staleObjectStateException) {
		this.staleObjectStateException = staleObjectStateException;
	}

	public T getActualObject() {
		return actualObject;
	}

	private void setActualObject(T actualObject) {
		this.actualObject = actualObject;
	}

	public T getDatabaseObject() {
		return databaseObject;
	}

	private void setDatabaseObject(T databaseObject) {
		this.databaseObject = databaseObject;
	}

	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countByCriteria(Map<String, Object> whereBy,
			Map<String, String> filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map<String, Object> getID(T instance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

package basic.zBasic.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import basic.persistence.dao.GeneralDAO;
import basic.persistence.model.IPrimaryKeys;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderUserZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zUtil.io.KernelFileZZZ.FLAGZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;

public abstract class GeneralDaoZZZ<T> extends GeneralDAO<T> implements IObjectZZZ, IFlagZZZ, IHibernateContextProviderUserZZZ{
	private static final long serialVersionUID = 1L;

	//Speziell für ZZZ
	private HashMap<String, Boolean>hmFlag = new HashMap<String, Boolean>(); //Neu 20130721	
	protected ExceptionZZZ objException = null;    // diese Exception hat jedes Objekt
		
	//Speziell für DaoZZZ
	private  IHibernateContextProviderZZZ objContextHibernate = null;
	private Session session = null;
	
	//##### KONSTRUKTOREN
	public GeneralDaoZZZ() throws ExceptionZZZ{		
		this(null, "init");
	}
	public GeneralDaoZZZ(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		this(objContextHibernate, (String[])null);				
	}
	
	public GeneralDaoZZZ(IHibernateContextProviderZZZ objContextHibernate, String sFlagControlIn) throws ExceptionZZZ{
		String[] saFlagControl = new String[1];
		saFlagControl[0] = sFlagControlIn;
		GeneralDaoNew_(objContextHibernate, saFlagControl);
	}
	
	public GeneralDaoZZZ(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControlIn) throws ExceptionZZZ{
		GeneralDaoNew_(objContextHibernate, saFlagControlIn);
	}
	
	private void GeneralDaoNew_(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
	main:{
		if(saFlagControl!=null){
			boolean btemp = false;
			for(int icount=0;icount <= saFlagControl.length-1;icount++){
				String stemp = saFlagControl[icount];
				btemp = this.setFlag(stemp, true);
				
				if(btemp==false){ 								   
					   ExceptionZZZ ez = new ExceptionZZZ( sERROR_FLAG_UNAVAILABLE + stemp, iERROR_FLAG_UNAVAILABLE, this.getClass(), ReflectCodeZZZ.getMethodCurrentName()); 
					   throw ez;		 
				}
			}
			if(this.getFlag("init")) break main;
		}	
			
		this.setHibernateContextProvider(objContextHibernate);	
		this.getSession();
		}//end main:
	}
	
	//######## Neue Methoden, die als Komfortfunktionen in diese ZZZ-Klasse kommen ###############
	/**Gib den (im Konstruktor angegebenen) Namen der Entity-Klasse an. 
	 *   Dieser kann dann in der Erzeugung von HQL verwendet werden. (Wird momentan nur für den Logger verwendet)
	 *   
	 * @return
	 */
	public String getDaoTableName(){
		Class<T> objClass = this.getT();
		return objClass.getSimpleName();
	}
	
	
	@Override
	public int count(){
		String sTableName = this.getDaoTableName();
		
		this.getLog().debug(ReflectCodeZZZ.getPositionCurrent() + ": Counting '" + sTableName);
		Query q = getSession().createQuery("select count(t) from " + sTableName + " t");
		int count = ((Long)q.uniqueResult()).intValue();
		return count;
	}
	
//	@Override
	public int countByCriteria(Map<String, Object> whereBy, 	Map<String, String> filter) {		
		String sTableName = this.getDaoTableName();
		this.getLog().debug(ReflectCodeZZZ.getPositionCurrent() + ": Counting '" + sTableName);
		
		return this.countByCriteria(sTableName, whereBy, filter);
	}
	
	
	/**FGL: Ermittle den Max-Wert einer Spalte.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMax(String sTableName, String sColumn, HashMap<String,Object>hmWhereByParameter) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//null für die WhereBy-Klausel ist nicht erlaubt.	
				if(hmWhereByParameter==null){
				   ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; HashMap für WhereByParameter darf nicht NULL sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				   throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
					
				intReturn = this.maxIntegerByCriteria(sTableName, sColumn, hmWhereByParameter);
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Min-Wert einer Spalte.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMin(String sTableName, String sColumn, HashMap<String,Object>hmWhereByParameter) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//null für die WhereBy-Klausel ist nicht erlaubt.	
				if(hmWhereByParameter==null){
				   ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; HashMap für WhereByParameter darf nicht NULL sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				   throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
					
				intReturn = this.minIntegerByCriteria(sTableName, sColumn, hmWhereByParameter);
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Max-Wert einer Spalte.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMax(String sTableName, String sColumn) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMax(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Max-Wert einer Spalte.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMin(String sTableName, String sColumn) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMin(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Max-Wert einer Spalte.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.  
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMax(String sColumn) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMax(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Min-Wert einer Spalte.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.  
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMin(String sColumn) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMin(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Max-Wert der ID-Spalte des aktuellen Dao-Objekts.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.
	 *         Der Name des Attributs ist hier fest mit "id" vorgegeben.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findIdValueMax() throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				String sColumn = "id";
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMax(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Min-Wert der ID-Spalte des aktuellen Dao-Objekts.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.
	 *         Der Name des Attributs ist hier fest mit "id" vorgegeben.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findIdValueMin() throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				String sColumn = "id";
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMin(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	
	
	public List<T> findLazyAll( int first, int max){
		String sTableName = this.getDaoTableName();
		return this.findLazyAll(sTableName, first, max);
	}
	
	//######## METHODEN, DIE VON GENERALDAO ÜBERSCHREIEBEN WERDEN ################
	public Session getSession() {		
		Session objReturn = null;
		try {	
			if(this.session==null){
				IHibernateContextProviderZZZ objHibernateContext = this.getHibernateContextProvider();
				if(objHibernateContext==null){
					objReturn= GeneralDAO.getSessionObject();			
				}else{
					objReturn = objHibernateContext.getSession();
				}
				
				if(objReturn==null){				
					throw new ExceptionZZZ("Session weder aus reinem Hibernate noch aus dem EntityManager (s. HibernateContextProvider) zu holen. Keine HibernateContextProvider vorhanden.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				}else{
					this.setSession(objReturn);
				}				
			}
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.session;				
	}
	
	public void setSession(Session objSession){
		if(this.session!=null){
			if(this.session.isOpen()) this.session.close();
		}
		this.session=objSession;
	}
	
	
	//#### GETTER / SETTER 
	public void setHibernateContextProvider(IHibernateContextProviderZZZ objContextHibernate){
		this.objContextHibernate = objContextHibernate;
	}
	public IHibernateContextProviderZZZ getHibernateContextProvider(){
		return this.objContextHibernate;
	}
	
	public ExceptionZZZ getExceptionObject() {
		return this.objException;
	}
	public void setExceptionObject(ExceptionZZZ objException) {
		this.objException = objException;
	}
	
	//### NEUE DAO METHODEN ###################
	
	//  FGL 20170316:
	//!!!Beachte, diese Methode ist protected, also nur innerhalb des DAOs nutzbar.
	//  Das ist erst einmal Absicht. So muss jedes DAO den HQL - String "qualifiziert" bereitstellen und es kann nicht einfach von aussen ein HQL String ausgeführt werden.
	//
	//BEACHTE: VERWENDE HIER SESSION AUS Hibernate
	protected List<?> executeHQLBySession(String hql) {
		
		//BEACHTE: VERWENDE HIER SESSION
		this.begin();
				
		Query q = getSession().createQuery(hql);

		List<?> list = (List<?>) q.list();
		
		this.commit();
		return list;
	}
	
//  FGL 20170316:
	//!!!Beachte, diese Methode ist protected, also nur innerhalb des DAOs nutzbar.
	//  Das ist erst einmal Absicht. So muss jedes DAO den HQL - String "qualifiziert" bereitstellen und es kann nicht einfach von aussen ein HQL String ausgeführt werden.
	//
	//BEACHTE: VERWENDE HIER HibernateContextProviderZZZ und den EntityManager aus javax.persistence
	protected List<?> executeHQLByEntityManager(String hql) throws ExceptionZZZ {
		IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
		EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
		
		javax.persistence.Query objQuery = em.createQuery(hql);
		List<?> objResult = objQuery.getResultList();
		
		//TODO: Methode anbieten executeHQLByEntityManager_singleResult(String hql) throws ExceptionZZZ {
//		Object objSingle =objQuery.getSingleResult();
//		if(objSingle!=null){
//			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Objekt als Single Result der Query " + objSingle.hashCode());
//		}else{
//			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt als Single Result der Query " + hql);
//		}
		
		return objResult;
	}
	
	
//  FGL 20170316:
	//!!!Beachte, diese Methode ist protected, also nur innerhalb des DAOs nutzbar.
	//  Das ist erst einmal Absicht. So muss jedes DAO den HQL - String "qualifiziert" bereitstellen und es kann nicht einfach von aussen ein HQL String ausgeführt werden.
	//
	//BEACHTE: VERWENDE HIER HibernateContextProviderZZZ und den EntityManager aus javax.persistence
	protected Object executeHQLByEntityManager_singleResult(String hql) throws ExceptionZZZ {
		IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
		EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
		
		javax.persistence.Query objQuery = em.createQuery(hql);
		Object objResult =objQuery.getSingleResult();
		if(objResult!=null){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Objekt als Single Result der Query " + objResult.hashCode());
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt als Single Result der Query " + hql);
		}		
		return objResult;
	}
	
	//### FlagMethods ##########################		
	public Class getClassFlagZ(){
		return FLAGZ.class;
	}
	
	
		public HashMap<String, Boolean>getHashMapFlagZ(){
			return this.hmFlag;
		} 
		
		/* @see basic.zBasic.IFlagZZZ#getFlagZ(java.lang.String)
		 * 	 Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.(non-Javadoc)
		 */
		public boolean getFlagZ(String sFlagName) {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
											
				HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
				Boolean objBoolean = hmFlag.get(sFlagName.toUpperCase());
				if(objBoolean==null){
					bFunction = false;
				}else{
					bFunction = objBoolean.booleanValue();
				}
								
			}	// end main:
			
			return bFunction;	
		}
		
				
		public boolean getFlag(String sFlagName) {
//			boolean bFunction = false;
//		main:{
//			if(StringZZZ.isEmpty(sFlagName)) break main;
//			
//			// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//			// bFunction = super.getFlag(sFlagName);
//			// if(bFunction == true) break main;
//			
//			// Die Flags dieser Klasse setzen
//			String stemp = sFlagName.toLowerCase();
//			if(stemp.equals("debug")){
//				bFunction = this.bFlagDebug;
//				break main;
//			}else if(stemp.equals("init")){
//				bFunction = this.bFlagInit;
//				break main;
//			}else{
//				bFunction = false;
//			}		
//		}	// end main:
	//	
//		return bFunction;	
			return this.getFlagZ(sFlagName);
		}

		public boolean setFlag(String sFlagName, boolean bFlagValue) {
			//Version Vor Java 1.6
//			boolean bFunction = true;
//			main:{
//				if(StringZZZ.isEmpty(sFlagName)) break main;
//				
//				// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//				// bFunction = super.setFlag(sFlagName, bFlagValue);
//				// if(bFunction == true) break main;
//				
//				// Die Flags dieser Klasse setzen
//				String stemp = sFlagName.toLowerCase();
//				if(stemp.equals("debug")){
//					this.bFlagDebug = bFlagValue;
//					bFunction = true;                            //durch diesen return wert kann man "reflexiv" ermitteln, ob es in dem ganzen hierarchie-strang das flag �berhaupt gibt !!!
//					break main;
//				}else if(stemp.equals("init")){
//					this.bFlagInit = bFlagValue;
//					bFunction = true;
//					break main;
//				}else{
//					bFunction = false;
//				}	
//				
//			}	// end main:
//			
//			return bFunction;	
			try {
				return this.setFlagZ(sFlagName, bFlagValue);
			} catch (ExceptionZZZ e) {
				System.out.println("ExceptionZZZ (aus compatibilitaetgruenden mit Version vor Java 6 nicht weitergereicht) : " + e.getDetailAllLast());
				return false;
			}
		}
		
		
		
		/** DIESE METHODE MUSS IN ALLEN KLASSEN VORHANDEN SEIN - über Vererbung -, DIE IHRE FLAGS SETZEN WOLLEN
		 * Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.
		 * @param objClassParent
		 * @param sFlagName
		 * @param bFlagValue
		 * @return
		 * lindhaueradmin, 23.07.2013
		 */
		public boolean setFlagZ(String sFlagName, boolean bFlagValue) throws ExceptionZZZ {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
				

				bFunction = this.proofFlagZExists(sFlagName);												
				if(bFunction == true){
					
					//Setze das Flag nun in die HashMap
					HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
					hmFlag.put(sFlagName.toUpperCase(), bFlagValue);
					bFunction = true;								
				}										
			}	// end main:
			
			return bFunction;	
		}
		
		//Aus IObjectZZZ, siehe FileZZZ
		public boolean proofFlagZExists(String sFlagName) {
			boolean bReturn = false;
			main:{
				bReturn = ObjectZZZ.proofFlagZExists(this.getClass(), sFlagName);
			
				//Schon die oberste IObjectZZZ nutzende Klasse, darum ist der Aufruf einer Elternklasse mit der Methode nicht möglich. 
				//boolean bReturn = super.proofFlagZExists(sFlagName);
			
				if(!bReturn){						
					for(Object obj : FLAGZ.class.getEnumConstants()){
						//System.out.println(obj + "; "+obj.getClass().getName());
						if(sFlagName.equalsIgnoreCase(obj.toString())) {
							bReturn = true;
							break main;
						}
					}				
				}
			}//end main:
			return bReturn;
		}
		
	
	
	//##########################################################################
	///Beipsiele .....................
	/**
	 * Find all BusinessProvider
	 * 
	 * @param businessProviderID
	 * @param first
	 * @param max
	 * @return
	 */
//	public List<Consumer> findFirstConsumerDependingOnBusinessProvider(int businessProviderID,int first, int max){
//		log.debug("find all BusinessProvider");
//		Query q = getSession().createQuery("from Consumer c where c.businessProvider.businessProviderNo = :id").setParameter("id", businessProviderID);	
//		
//		q.setFirstResult(first);
//		q.setMaxResults(max);
//		
//		@SuppressWarnings("unchecked")
//		List<Consumer> allConsumer = q.list();
//		
//		return allConsumer;
//	}
//	
		
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findByCriteria(java.util.Map, java.util.List)
	 */
//	@Override
//	public List<Consumer> findByCriteria(Map<String,Object> criteria, List<String> orderByList, Map<String, String> filter){
//		log.debug("findByCriteria Portaluserser Dao");
//		return this.findByCriteria("Consumer", 0, -1, criteria, orderByList, filter);
//	}
	
	/*
	 * (non-Javadoc)
	 * @see de.betzemeier.serviceportal.persistence.interfaces.DaoInterface#findByCriteria(java.util.Map, java.util.List, int, int)
	 */
//	@Override
//	public List<Consumer> findByCriteria(Map<String, Object> whereBy,
//			List<String> orderByList, Map<String, String> filter, int first, int max) {
//		return this.findByCriteria("Consumer", first, max, whereBy, orderByList, filter);
//	}
	
	
	/**
	 * Count only the Consumer, which depending on a BusinessProvider.
	 * @param businessProviderID
	 * @return
	 */
//	public int countDependingOnBusinessProviderID(int businessProviderID) {
//		log.debug("count Consumer");
//		Query q = getSession().createQuery("select count(c) from Consumer c where c.businessProvider.businessProviderNo = :id").setParameter("id", businessProviderID);	
//		int count = ((Long)q.uniqueResult()).intValue();
//		return count;
//	}
	
	/**
	 * Read all E-Mails from Service Provider List.
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public List<String> getAllEMails(){
//		return (List<String>) this.getColumn("email","Consumer");
//	}
	
	
}

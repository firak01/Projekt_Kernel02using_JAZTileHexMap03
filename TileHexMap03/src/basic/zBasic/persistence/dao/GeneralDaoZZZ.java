package basic.zBasic.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;

import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import basic.persistence.dao.GeneralDAO;
import basic.persistence.model.IPrimaryKeys;
import basic.persistence.util.HibernateUtilByAnnotation;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.IObjectZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.IConstantHibernateZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderUserZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
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
	/*Merke: Für die hieraus erbenden(!!!) Klassen gilt:
	 * WICHTIG: Der in angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
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
		int iReturn = -1;
		try{
		String sTableName = this.getDaoTableName();
		
		this.getLog().debug(ReflectCodeZZZ.getPositionCurrent() + ": Counting '" + sTableName);
		
//		20171101: daoKey.count "locked" die Datenbank. 
//		Lösungsidee: Es wurde hier keine Transaktion gebraucht? Das Ziel muss aber sein 1 Session : 1 Transaktion.
//		                    Daher this.begin() und this.commit(), um die Transaktion wieder zu schliessen.
		
		Session session = getSession();
		this.begin();
			
		Query q = getSession().createQuery("select count(t) from " + sTableName + " t");
		iReturn = ((Long)q.uniqueResult()).intValue();
		
		this.commit();
		
//		catch (NonUniqueObjectException non) {
//			log.error("Method delete failed NonUniqueObjectException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , non);
//			System.out.println("NON UNIQUE!!!");
//			return this.helpCatchException(non, instance);			
//		}
//		catch(StaleObjectStateException er){
//			log.error("Method delete failed StaleObjectStateException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , er);
//			System.out.println("STALE!!!");
//			return this.staleObjectStateException(instance, er);
		}catch(HibernateException he){
			log.error("Method delete failed HibernateException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , he);
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HIBERNATE EXCEPTION!!!!");
			he.printStackTrace();
			 iReturn = -1;
		}finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug(ReflectCodeZZZ.getPositionCurrent() + ": rollback executed");
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HIBERNATE ROLLBACK EXECUTED!!!!");
				iReturn = -1;
			}
		}

		return iReturn;
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
	public Integer findColumnValueMax(String sTableName, String sColumn, Map<String,Object>hmWhereByParameter) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//null für die WhereBy-Klausel ist nicht erlaubt.	
				if(hmWhereByParameter==null){
				   ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; HashMap für WhereByParameter darf nicht NULL sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				   throw ez;		 
				}
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				if(StringZZZ.isEmpty(sTableName)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
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
	public Integer findColumnValueMin(String sTableName, String sColumn, Map<String,Object>hmWhereByParameter) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				//null für die WhereBy-Klausel ist nicht erlaubt.	
				if(hmWhereByParameter==null){
				   ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; HashMap für WhereByParameter darf nicht NULL sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				   throw ez;		 
				}
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				if(StringZZZ.isEmpty(sTableName)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
					
				intReturn = this.minIntegerByCriteria(sTableName, sColumn, hmWhereByParameter);
			}//end main
			return intReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sColumn) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
						
			String sTableName = this.getDaoTableName();
			listReturn = this.findColumnValueSortedByColumn(sTableName, sColumn);
		
		}//end main:
		return listReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sColumn, int iSortingDirection) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
						
			String sTableName = this.getDaoTableName();
			listReturn = this.findColumnValueSortedByColumn(sTableName, sColumn, iSortingDirection);
		
		}//end main:
		return listReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sColumn, int iSortingDirection, String sColumnSorted) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
						
			String sTableName = this.getDaoTableName();
			listReturn = this.findColumnValueSortedByColumn(sTableName, sColumn, iSortingDirection, sColumnSorted);
		
		}//end main:
		return listReturn;
	}
	
	
	public List<?> findColumnValueSortedByColumn(String sTableName, String sColumn) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sTableName)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
								
			//Nun eine Methode aus GeneralDao aufrufen.
			String sColumnSorted = sColumn; 
			listReturn = this.getColumnSortedByColumn(sTableName, sColumn, IConstantHibernateZZZ.iSORT_NONE, sColumnSorted);
		
		}//end main:
		return listReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sTableName, String sColumn, int iSortingDirection) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sTableName)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
								
			//Nun eine Methode aus GeneralDao aufrufen.
			String sColumnSorted = sColumn; 
			listReturn = this.getColumnSortedByColumn(sTableName, sColumn, iSortingDirection, sColumnSorted);
		
		}//end main:
		return listReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sTableName, String sColumn, String sColumnSorted) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sTableName)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sColumnSorted)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den sortierten Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			
			//Nun eine Methode aus GeneralDao aufrufen.
			listReturn = this.getColumnSortedByColumn(sTableName, sColumn, IConstantHibernateZZZ.iSORT_NONE, sColumnSorted);
		
		}//end main:
		return listReturn;
	}
	
	public List<?> findColumnValueSortedByColumn(String sTableName, String sColumn, int iSortingDirection, String sColumnSorted ) throws ExceptionZZZ{
		List<?> listReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sTableName)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			if(StringZZZ.isEmpty(sColumnSorted)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den sortierten Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			
			//Nun eine Methode aus GeneralDao aufrufen.
			listReturn = this.getColumnSortedByColumn(sTableName,  sColumn, iSortingDirection, sColumnSorted);
		
		}//end main:
		return listReturn;
	}
	
	/**FGL: Ermittle den Max-Wert einer Spalte.
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMax(String sTableName, String sColumn) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				if(StringZZZ.isEmpty(sTableName)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
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
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				if(StringZZZ.isEmpty(sTableName)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Tabellennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
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
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMax(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	
	/**FGL: Ermittle den Max-Wert einer Spalte, gib dabei noch einen WHERE Parameter an.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.  
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMax(String sColumn, Map<String, Object> hmWhere) throws ExceptionZZZ{
		Integer intReturn = null;
		main:{
			if(StringZZZ.isEmpty(sColumn)){
				 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
				  throw ez;		 
			}
			
			//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
			
			String sTableName = this.getDaoTableName();
			
			if(hmWhere==null){
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMax(sTableName, sColumn, new HashMap<String,Object>());
			}else{
				intReturn = this.findColumnValueMax(sTableName, sColumn, hmWhere);					
			}
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
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
				
				
				//null für die WhereBy-Klausel ist nicht erlaubt.				
				intReturn = this.findColumnValueMin(sTableName, sColumn, new HashMap<String,Object>());
			}//end main
			return intReturn;
	}
	
	/**FGL: Ermittle den Min-Wert einer Spalte, gib dabei noch einen WHERE Paramteran.
	 *         Hole dazu den "Tabellennamen" aus dem aktuellen DaoObjekt. Nämlich das was unterder Dao - Klasse angegeben ist.  
	 * @param sColumn
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public Integer findColumnValueMin(String sColumn, Map<String,Object>hmWhere) throws ExceptionZZZ{
			Integer intReturn = null;
			main:{
				if(StringZZZ.isEmpty(sColumn)){
					 ExceptionZZZ ez = new ExceptionZZZ( sERROR_PARAMETER_EMPTY + "; String für den Spaltennamen darf nicht NULL oder leer sein.", iERROR_PARAMETER_EMPTY, this.getClass(),ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				}
				
				//this.begin();//wird in maxIntegerByCriteria schon gemacht, darf hier nicht gemacht werden.
				
				String sTableName = this.getDaoTableName();
								
				if(hmWhere==null){
					//null für die WhereBy-Klausel ist nicht erlaubt.						
					intReturn = this.findColumnValueMin(sTableName, sColumn, new HashMap<String,Object>());
				}else{
					intReturn = this.findColumnValueMin(sTableName, sColumn, hmWhere);
				}
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
	
	public List<T> findLazyAll(){
		String sTableName = this.getDaoTableName();
		return this.findLazyAll(sTableName);
	}
	
	//######## METHODEN, DIE VON GENERALDAO ÜBERSCHREIEBEN WERDEN ################
	public Session getSession() {		
		Session objReturn = null;
		try {	
			if(this.session==null){
				IHibernateContextProviderZZZ objHibernateContext = this.getHibernateContextProvider();
				if(objHibernateContext==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": WARNUNG - Hole Session über GeneralDAO und nicht über HibernateContextProvider !!!");
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
			
			// Fehler: "Session Closed", wenn man eine Dao-Methode mehrfach hintereinander ausführen will. Ziel ist: 1 Session für 1 Transaction
			if(!this.session.isOpen()){
				//LÖSUNGSANSATZ: Eine neue, offene Session erstellen.
				
				//Dazu Verschiedenen Alternativen an die SessionFactory zu kommen, die aber alle nicht funktionieren
				//LÖSUNGSANSATZ: JNDI
				//DAS KLAPPT NICHT WG. JNDI ist falsch....
//				SessionFactory sf = HibernateUtilByAnnotation.getHibernateUtil().getSessionFactory();
//				if (sf!=null){
//					sf.openSession();
//				}else{
//					System.out.println("SessionFactory kann nicht erstellt werden. Tip: Alternativ den EntityManager verwenden oder ... (Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial). ");
//				}
				
				//LÖSUNGSANSATZ: Den EntityManager verwenden
				//###################### 
				//Option 1 through EntityManagerFactory
				//
				//If you use Hibernate >= 4.3 and JPA 2.1 you can accces the SessionFactory from a EntityManagerFactory through <T> T EntityManagarFactory#unwrap(Class<T> cls).
				//
				//SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
				//
				//Option 2 through EntityManager
				//
				//If you use Hibernate >= 4.3 and JPA >= 2.0 then you can accces the SessionFactory from a EntityManager through <T> T EntityManagar#unwrap(Class<T> cls).
				//
				//Session session = entityManager.unwrap(Session.class);
				//SessionFactory sessionFactory = session.getSessionFactory();
				//#################################

				//allerdings setze ich Hibernate 4.2.xx ein...., d.h. es funktioniert der unwrap für SessionFactory nicht...
				
				//EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
//				String sSchemaName = "TileHexMap03";
//				EntityManager em = this.getHibernateContextProvider().getEntityManager(sSchemaName);
//				SessionFactory sf = em.unwrap(SessionFactory.class);
//				if (sf!=null){
//					sf.openSession();
//				}else{
//					System.out.println("Alternativ den EntityManager verwenden  hat nix gebracht... ");
//				}
				
				
				
				//############################
				//LÖSUNGSANSATZ: Versuch auf die bestehende SessionFactory zuzugreifen.
				IHibernateContextProviderZZZ objHibernateContext = this.getHibernateContextProvider();
				if(objHibernateContext==null){					
				}else{
					SessionFactory sf = objHibernateContext.getSessionFactory();
					objReturn = sf.openSession();
					this.session = objReturn;
				}
			}else{
				//WICHTIG: FALLS es eine noch nicht geschlossene Transaction gab, diese hier beenden.
				//Ansonsten droht der Fehler: Nested Transaction not allowed.
				Transaction tx = this.session.getTransaction();
				if(tx!=null){
					//Aber wg. Fehlermeldung: Transaction not successfully started
					//Diese liegt wohl daran, dass ich nach jeder Session Erzeugung eine Transaction beginne. Sicher ist sicher.
					//Hohle ich dann mit getSession() eine neue Session ist die alte ggfs. noch nicht mal angefangen. (durch ein .save(), .commit().
					//Lösungsansatz: Frage den Status der Transaction ab.
					//wohl so nicht complilierbar if (tx.getStatus().equals(TransactionStatus.ACTIVE)) {
					if(!tx.wasCommitted()){
						//tx.commit();
						//tx.rollback(); //Es gibt wohl einen Grund warum noch nicht committed wurde
						if(tx.isActive()){
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": XXXXX NEUE SESSION TROTZ AKTIVER TRANSACTION ########");
						}
					}
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
	
	public Session getSessionOpen() throws ExceptionZZZ{
		Session objReturn = null;
		
		if(this.session==null){
			IHibernateContextProviderZZZ objHibernateContext = this.getHibernateContextProvider();
			if(objHibernateContext==null){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": WARNUNG - Hole Session über GeneralDAO und nicht über HibernateContextProvider !!!");									
				objReturn= GeneralDAO.getSessionObject();	//Diese Session ist dann auch per SessionFactory Geöffnet.					
			}else{
				objReturn = objHibernateContext.getSessionOpen();
			}
			
			if(objReturn==null){				
				throw new ExceptionZZZ("Session weder aus reinem Hibernate noch aus dem EntityManager (s. HibernateContextProvider) zu holen. Keine HibernateContextProvider vorhanden.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			}else{
				this.setSession(objReturn);
			}				
		}
			
		// Fehler: "Session Closed", wenn man eine Dao-Methode mehrfach hintereinander ausführen will. Ziel ist: 1 Session für 1 Transaction
		if(!this.session.isOpen()){
				throw new ExceptionZZZ("Session ist nicht geöffnet. Sollte es aber sein.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());					
		}else{
			//WICHTIG: FALLS es eine noch nicht geschlossene Transaction gab, diese hier beenden.
			//Ansonsten droht der Fehler: Nested Transaction not allowed.
			Transaction tx = this.session.getTransaction();
			if(tx!=null){
				//Aber wg. Fehlermeldung: Transaction not successfully started
				//Diese liegt wohl daran, dass ich nach jeder Session Erzeugung eine Transaction beginne. Sicher ist sicher.
				//Hohle ich dann mit getSession() eine neue Session ist die alte ggfs. noch nicht mal angefangen. (durch ein .save(), .commit().
				//Lösungsansatz: Frage den Status der Transaction ab.
				//wohl so nicht complilierbar if (tx.getStatus().equals(TransactionStatus.ACTIVE)) {
				if(!tx.wasCommitted()){
					//tx.commit();
					//tx.rollback(); //Es gibt wohl einen Grund warum noch nicht committed wurde
					if(tx.isActive()){
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": XXXXX NEUE SESSION TROTZ AKTIVER TRANSACTION ########");
					}
				}
			}
		}		
		return this.session;		
	}
	

	
	public Session getSessionCurrent(){
		return this.session;				
	}

	public Session getSessionNew(){
		Session objReturn = null;
		
		//1. Session schon vorhanden
		Session session = this.getSessionCurrent();
		if(session!=null){
			if(session.isOpen()){		
				//WICHTIG: FALLS es eine noch nicht geschlossene Transaction gab, diese hier beenden.
				//Ansonsten droht der Fehler: Nested Transaction not allowed.
				Transaction tx = this.session.getTransaction();
				if(tx!=null){
					//Aber wg. Fehlermeldung: Transaction not successfully started
					//Diese liegt wohl daran, dass ich nach jeder Session Erzeugung eine Transaction beginne. Sicher ist sicher.
					//Hohle ich dann mit getSession() eine neue Session ist die alte ggfs. noch nicht mal angefangen. (durch ein .save(), .commit().
					//Lösungsansatz: Frage den Status der Transaction ab.
					//wohl so nicht complilierbar if (tx.getStatus().equals(TransactionStatus.ACTIVE)) {
					if(!tx.wasCommitted()){
						//tx.commit();
						//tx.rollback(); //Es gibt wohl einen Grund warum noch nicht committed wurde
						if(tx.isActive()){
							System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": XXXXX NEUE SESSION TROTZ AKTIVER TRANSACTION ########");
						}
					}
				}
			}
			this.setSession(null);
		}
		objReturn = this.getSession();
		return objReturn;
	}
	
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
		
		//Aus IFlagZZZ, siehe ObjectZZZ
		/**Gibt alle möglichen FlagZ Werte als Array zurück. 
		 * @return
		 */
		public String[] getFlagZ(){
			String[] saReturn = null;
			main:{				
					Class objClass4Enum = this.getClassFlagZ();	//Aufgrund des Interfaces IFlagZZZ wird vorausgesetzt, dass diese Methode vorhanden ist.
					String sFilterName = objClass4Enum.getSimpleName();
					
					ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
					if(listEmbedded == null) break main;
					//out.format("%s# ListEmbeddedClasses.size()...%s%n", ReflectCodeZZZ.getPositionCurrent(), listEmbedded.size());
					
					ArrayList <String> listasTemp = new ArrayList<String>();
					for(Class objClass : listEmbedded){
						//out.format("%s# Class...%s%n", ReflectCodeZZZ.getPositionCurrent(), objClass.getName());
						Field[] fields = objClass.getDeclaredFields();
						for(Field field : fields){
							if(!field.isSynthetic()){ //Sonst wird ENUM$VALUES auch zurückgegeben.
								//out.format("%s# Field...%s%n", ReflectCodeZZZ.getPositionCurrent(), field.getName());
								listasTemp.add(field.getName());
							}				
					}//end for
				}//end for
					
				//20170307: Durch das Verschieben von FLAGZ mit den Werten DEBUG und INIT in das IObjectZZZ Interface, muss man explizit auch dort nachsehen.
			   //                Merke: Das Verschieben ist deshlab notwenig, weil nicht alle Klassen direkt von ObjectZZZ erben können, sondern das Interface implementieren müsssen.
			
												
					//+++ Nun die aktuelle Klasse 
					Class<FLAGZ> enumClass = FLAGZ.class;								
					for(Object obj : FLAGZ.class.getEnumConstants()){
						//System.out.println(obj + "; "+obj.getClass().getName());
						listasTemp.add(obj.toString());
					}
					saReturn = listasTemp.toArray(new String[listasTemp.size()]);
			}//end main:
			return saReturn;
		}
	
		/**Gibt alle "true" gesetzten FlagZ - Werte als Array zurück. 
		 * @return
		 */
		public String[] getFlagZ(boolean bValueToSearchFor){
			String[] saReturn = null;
			main:{
				
				
				ArrayList<String>listasTemp=new ArrayList<String>();
				
				//FALLUNTERSCHEIDUNG: Alle gesetzten FlagZ werden in der HashMap gespeichert. Aber die noch nicht gesetzten FlagZ stehen dort nicht drin.
				//                                  Diese kann man nur durch Einzelprüfung ermitteln.
				if(bValueToSearchFor==true){
					HashMap<String,Boolean>hmFlag=this.getHashMapFlagZ();
					if(hmFlag==null) break main;
					
					Set<String> setKey = hmFlag.keySet();
					for(String sKey : setKey){
						boolean btemp = hmFlag.get(sKey);
						if(btemp==bValueToSearchFor){
							listasTemp.add(sKey);
						}
					}
				}else{
					String[]saFlagZ = this.getFlagZ();
					for(String sFlagZ : saFlagZ){
						boolean btemp = this.getFlagZ(sFlagZ);
						if(btemp==bValueToSearchFor ){ //also 'false'
							listasTemp.add(sFlagZ);
						}
					}
				}
				saReturn = listasTemp.toArray(new String[listasTemp.size()]);
			}//end main:
			return saReturn;
		}
		
		/**Gibt alle "true" gesetzten FlagZ - Werte als Array zurück, die auch als FLAGZ in dem anderen Objekt überhaupt vorhanden sind.
		 *  Merke: Diese Methode ist dazu gedacht FlagZ-Werte von einem Objekt auf ein anderes zu übertragen.	
		 *    
		 * @return
		 * @throws ExceptionZZZ 
		 */
		public String[] getFlagZ_passable(boolean bValueToSearchFor, IFlagZZZ objUsingFlagZ) throws ExceptionZZZ{
			String[] saReturn = null;
			main:{
				
				//1. Hole alle FlagZ, DIESER Klasse, mit dem gewünschten Wert.
				String[] saFlag = this.getFlagZ(bValueToSearchFor);
				
				//2. Hole alle FlagZ der Zielklasse
				String[] saFlagTarget = objUsingFlagZ.getFlagZ();
				
				ArrayList<String>listasFlagPassable=new ArrayList<String>();
				//Nun nur die Schnittmenge der beiden StringÄrrays hiolen.
				
				saReturn = StringArrayZZZ.intersect(saFlag, saFlagTarget);
			}//end main:
			return saReturn;
		}
		
		/**Gibt alle "true" gesetzten FlagZ - Werte als Array zurück, die auch als FLAGZ in dem anderen Objekt überhaupt vorhanden sind.
		 *  Merke: Diese Methode ist dazu gedacht FlagZ-Werte von einem Objekt auf ein anderes zu übertragen.	
		 *    
		 * @return
		 * @throws ExceptionZZZ 
		 */
		public String[] getFlagZ_passable(IFlagZZZ objUsingFlagZ) throws ExceptionZZZ{
			String[] saReturn = null;
			main:{
				
				//1. Hole alle FlagZ, DIESER Klasse, mit dem gewünschten Wert.
				String[] saFlag = this.getFlagZ();
				
				//2. Hole alle FlagZ der Zielklasse
				String[] saFlagTarget = objUsingFlagZ.getFlagZ();
				
				ArrayList<String>listasFlagPassable=new ArrayList<String>();
				//Nun nur die Schnittmenge der beiden StringÄrrays hiolen.
				
				saReturn = StringArrayZZZ.intersect(saFlag, saFlagTarget);
			}//end main:
			return saReturn;
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

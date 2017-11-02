package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
public class TileDefaulttextDao<T> extends KeyDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor */
	public TileDefaulttextDao() throws ExceptionZZZ{
		super();
		this.installLoger(TileDefaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TileDefaulttextDao(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(TileDefaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TileDefaulttextDao(HibernateContextProviderSingletonTHM objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(TileDefaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TileDefaulttextDao(HibernateContextProviderSingletonTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(TileDefaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	//Den Namen des Aktuellen Objekts kann ich nun auslesen.
	//Darum diese Methode generisch in die DaoZZZ - Klasse ausgelagert.
//    public List<T> findLazyAll(int first, int max){
//    	return this.findLazyAll("Troop", first, max);
//    }
//    
//	@Override
//	public int count(){
//		this.getLog().debug("counting Troops");
//		Query q = getSession().createQuery("select count(t) from Troop t");
//		int count = ((Long)q.uniqueResult()).intValue();
//		return count;
//	}
//	
//	
//	/* (non-Javadoc)
//	 * @see use.thm.persistence.dao.GeneralDAO#countByCriteria(java.util.Map, java.util.Map)
//	 */
//	@Override
//	public int countByCriteria(Map<String, Object> whereBy, 	Map<String, String> filter) {
//		return this.countByCriteria("Troop", whereBy, filter);
//	}


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
	public boolean deleteAll(){
		boolean bReturn = false;
		main:{
			//Nun alle holen
			ArrayList<TileDefaulttext> listaTileDefaulttext = (ArrayList<TileDefaulttext>) this.findLazyAll();
			
			for(TileDefaulttext text : listaTileDefaulttext){
				System.out.println("Lösche: TileDefaulttext.toString(): " + text.toString());
				String sDescriptionStored = text.getDescription();
				System.out.println("Description (gespeichert): " + sDescriptionStored);	

				this.delete(text);
			}//End for
			bReturn = true;
			
		}//End main
		return bReturn;				
	}
	
	public void delete(TileDefaulttext text) {
		main:{
			if(text==null)break main;
			super.delete((T) text);
		}//end main
	}
}

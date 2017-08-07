package use.thm.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
public class AreaCellDao extends GeneralDaoZZZ<AreaCell> {
	private static final long serialVersionUID = 1L;

	/* Constructor */
	public AreaCellDao() throws ExceptionZZZ{
		super();
		this.installLoger(AreaCell.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AreaCellDao(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(AreaCell.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AreaCellDao(HibernateContextProviderSingletonTHM objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(AreaCell.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AreaCellDao(HibernateContextProviderSingletonTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(AreaCell.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
    public List<AreaCell> findLazyAll(int first, int max){
    	return this.findLazyAll("AreaCell", first, max);
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
	@Override
	public Map<String, Object> getID(AreaCell instance) {
		Map<String, Object> id = new HashMap<String, Object>();
		id.put("fieldAlias", instance.getFieldAlias());		
		return id;
	}
	
	public List<AreaCell> findByHQL(String hql, int first, int max) {
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
	
	
	//####### EIGENE METHODEN ###########
	public int computeMaxMapX(){
		int iReturn = -1;
		main:{
			try {
				//Nur als Beispiel wie kompliziert es sein kann den korrekten String herauszubekommen:
				//Query objQuery = em.createQuery("SELECT MAX(c.sMapX) FROM HexCell c");//Fehler: could not resolve property: sMapX of: tryout.hibernate.HexCell 
				//Query objQuery = em.createQuery("SELECT MAX(c.MapX) FROM HexCell c");//Fehler: could not resolve property: MapX of: tryout.hibernate.HexCell
				//Query objQuery = em.createQuery("SELECT MAX(c.x) FROM HexCell c");//Fehler: could not resolve property: x of: tryout.hibernate.HexCell
				//Query objQuery = em.createQuery("SELECT MAX(c.X) FROM HexCell c");//Fehler: could not resolve property: X of: tryout.hibernate.HexCell
			
				
				
			String sQueryTemp = "SELECT MAX(c.mapX) FROM HexCell c";
			
			//VARIANTE 1)
//			List<Integer> listResult = this.executeHQLBySession(sQueryTemp);
			
			//VARIANTE 2A:
//			@SuppressWarnings("unchecked")
//			List<Integer> listResult = (List<Integer>) this.executeHQLByEntityManager(sQueryTemp);
//			for(Object obj : listResult){
//				if(obj==null){										
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt in ResultList der Query " + sQueryTemp);
//					break main;
//				}else{					
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Gefundenes Objekt obj.class= " + obj.getClass().getName());
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Wert im Objekt ist = " + obj.toString());
//					
//					Integer intResult = (Integer) obj;
//				}
//			}
			
			//VARIANTE 2B 
			Integer intResult = (Integer) this.executeHQLByEntityManager_singleResult(sQueryTemp);
			iReturn = intResult.intValue();
		
									
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}//end main:
		return iReturn;			
	}
	
	
	public int computeMaxMapY(){
		int iReturn = -1;
		main:{
			try {
			String sQueryTemp = "SELECT MAX(c.mapY) FROM HexCell c";
			
			//VARIANTE 1)
//			List<Integer> listResult = this.executeHQLBySession(sQueryTemp);
			
			//VARIANTE 2A:
//			@SuppressWarnings("unchecked")
//			List<Integer> listResult = (List<Integer>) this.executeHQLByEntityManager(sQueryTemp);
//			for(Object obj : listResult){
//				if(obj==null){										
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt in ResultList der Query " + sQueryTemp);
//					break main;
//				}else{					
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Gefundenes Objekt obj.class= " + obj.getClass().getName());
//					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Wert im Objekt ist = " + obj.toString());
//					
//					Integer intResult = (Integer) obj;
//				}
//			}
			
			//VARIANTE 2B 
			Integer intResult = (Integer) this.executeHQLByEntityManager_singleResult(sQueryTemp);
			iReturn = intResult.intValue();
		
									
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}//end main:
		return iReturn;			
	}
}

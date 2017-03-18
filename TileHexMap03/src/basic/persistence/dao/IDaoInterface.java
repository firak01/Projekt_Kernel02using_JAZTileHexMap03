/**
 * 
 */
package basic.persistence.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import basic.persistence.model.IPrimaryKeys;

/**
 * @author DKatzberg
 *
 */
public interface IDaoInterface<T> extends Serializable {
	
	/**
	 * Add a Element to db.
	 * @param instance
	 */
	public boolean persist(T instance);
	
	/**
	 * Find a Element by ID
	 * @param id
	 * @return
	 */
	public T findById(int id);
	
	/**
	 * Find a Element by ID
	 * @param primaryKey
	 * @return
	 */
	public T findByKey(IPrimaryKeys primaryKey);
	
	/**
	 * Update A Element to DB
	 * @param instance
	 */
	public boolean update(T instance);
	
	/**
	 * Delete a Element from DB
	 * @param instance
	 */
	public boolean delete(T instance);
	
	/**
	 * Clear the FirstLevelCache and reload the actual instance.
	 */
	public void clearFirstLevelCache(T instance);
	
	/**
	 * Refresh the actual instance with the db.
	 */
	public void refresh(T instance);
	
	/**
	 * OrderBy/WhereBy Clauses to find Elements
	 * 
	 * Find by Crteria creates a variable hql call with any where conditions.
	 * 
	 * criteria is a Map. String are the coloum names (@see *.models) and the Object are the Values.
	 * 
	 * the orderByList is a List of String. It can have the coloum name like "username", but it can 
	 * also be the coloum name WITH order kind, like "username asc" or "username desc".
	 * 
	 * The filter is a List of coloum names and search values. You can set the MatchMode in all Dao Classes. Default 
	 * MatchMode ist ANYWHERE. The Filter is a Map. The key is the coloum name, the value is the search String.
	 * @param orderByList
	 * @param filter 
	 * @param criteria
	 * 
	 * @return
	 */
	public List<T> findByCriteria(Map<String,Object> whereBy, List<String> orderByList, Map<String, String> filter);
	
	/**
	 * OrderBy/WhereBy Clauses to find Elements
	 * 
	 * Find by Crteria creates a variable hql call with any where conditions.
	 * 
	 * criteria is a Map. String are the coloum names (@see *.models) and the Object are the Values.
	 * 
	 * the orderByList is a List of String. It can have the coloum name like "username", but it can 
	 * also be the coloum name WITH order kind, like "username asc" or "username desc".
	 * 
	 * The filter is a List of coloum names and search values. You can set the MatchMode in all Dao Classes. Default 
	 * MatchMode ist ANYWHERE. The Filter is a Map. The key is the coloum name, the value is the search String.
	 * @param orderByList
	 * @param first (index of the first element)
	 * @param max (number of elements)
	 * @param criteria
	 * @param filter
	 * 
	 * 
	 * @return
	 */
	public List<T> findByCriteria(Map<String,Object> whereBy, List<String> orderByList, Map<String, String> filter, int first, int max);
	
	/**
	 * Load from Element with index "first" "max" elements
	 * @param first
	 * @param max
	 * @return
	 */
	public List<T> findLazyAll(int first, int max);
	
	/**
	 * Count the Elements in the Database.
	 * @return
	 */
	public int count();
	
	/**
	 * 
	 *  OrderBy/WhereBy Clauses to find Elements
	 * 
	 * Find by Criteria creates a variable hql call with any where conditions.
	 * 
	 * criteria is a Map. String are the coloum names (@see *.models) and the Object are the Values.
	 * 
	 * 
	 * The filter is a List of coloum names and search values. You can set the MatchMode in all Dao Classes. Default 
	 * MatchMode ist ANYWHERE. The Filter is a Map. The key is the coloum name, the value is the search String.
	 * 
	 * This method do not return the Criteria List, but it returns the count of them.
	 * 
	 * @param orderByList
	 * @param filter
	 * @return
	 */
	public int countByCriteria(Map<String,Object> whereBy, Map<String, String> filter);
	
	/**
	 * The DAO Class can read out a ID from an instance and the coloumName of the ID Field.
	 *  
	 * @param instance
	 * @return
	 */
	public Map<String, Object> getID(T instance);
}

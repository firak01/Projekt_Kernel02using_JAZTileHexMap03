/**
 * 
 */
package basic.persistence.dao;

/**
 * @author DKatzberg
 */
public class DaoHelper {
	
	/**
	 * This method change an HQL Table name in a accepted id/value name for the Method "addParameter".
	 * @param oldValueName
	 * @return
	 */
	public String valueNameChanger(String oldValueName){
		
		if(oldValueName.lastIndexOf(".")>-1)
			return oldValueName.substring(oldValueName.lastIndexOf(".")+1, oldValueName.length());
		else return oldValueName;
	}
}

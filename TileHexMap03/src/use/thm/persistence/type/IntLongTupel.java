/**
 * 
 */
package use.thm.persistence.type;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * @author TRueping
 *
 */
@Entity
public class IntLongTupel implements Serializable{

private static final long serialVersionUID = 1L;
	
	@Id
	private int int1;
	private long long1;
	
	public IntLongTupel(){		
	}
	
	/**
	 * Constructor
	 * @param int1
	 * @param int2
	 */
	public IntLongTupel(int int1, long long1) {
		this.setInt1(int1);
		this.setLong1(long1);
	}

	/* GETTER / SETTER */
	public int getInt1() {
		return int1;
	}

	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public long getLong1() {
		return long1;
	}

	public void setLong1(long long1) {
		this.long1 = long1;
	}
}

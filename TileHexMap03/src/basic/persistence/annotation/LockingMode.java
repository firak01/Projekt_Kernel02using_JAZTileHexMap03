/**
 * 
 */
package basic.persistence.annotation;

import java.lang.annotation.Inherited;

/**
 * @author DKatzberg
 *
 */
@org.hibernate.annotations.Entity(optimisticLock = org.hibernate.annotations.OptimisticLockType.ALL)
@Inherited
public @interface LockingMode {}

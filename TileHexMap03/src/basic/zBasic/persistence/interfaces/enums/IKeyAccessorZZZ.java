/*
 * Copyright (c) 2016 HIS eG All Rights Reserved.
 *
 * $Id: HisKeyAccessor.java,v 1.1 2016/06/15 17:36:39 bienert-extern#his.de Exp $
 *
 * $Log: HisKeyAccessor.java,v $
 * Revision 1.1  2016/06/15 17:36:39  bienert-extern#his.de
 * #134437 – zPA: Manuelles Anlegen von Bescheidanforderungen (bwCMS AP10)
 * * technischer Umbau der Bescheide (FlowScope-Handling nun ausschließlich über OutputRequest und Eliminierung der ReportFunctionAllocationDtoImpl-Klasse)
 *
 *
 * Created on 14.06.2016 by Matthias Bienert
 */

package basic.zBasic.persistence.interfaces.enums;

/** TODO GOON 20171024: Verwende Thiskey statt HisKey...
 *                                       
 * Das ist schmissiger als FglKey
 * Und ZzzKey ist nicht gut, das ZZZ für den Kernel steht
 * Und ApplicationKey wird ebenfalls für die globale Anwendung verwendet. Das wird ausserdem auch der besonderen Bedeutung des Schlüssels nicht gerecht, da es zu "normal" klingt.
 *  
 * Providing an accessor that gives access to a his key via a concrete element.
 *
 * @param <E>   the type of the Object that gives access to a {@code HisKey}
 * @param <T>   the type of the his key to be provided
 */
public interface IKeyAccessorZZZ<E, T> {
    /**
     * Returns a defined {@code HisKey} from a given element.
     * @param element
     * @return the {@code HisKey}
     */
    public T getThiskey(final E element);
}
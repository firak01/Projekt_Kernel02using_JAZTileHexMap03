/*
 * Copyright (c) 2007 HIS eG All Rights Reserved
 * Created on 26.10.2007
 *
 * $Id: KeyEnum.java,v 1.3 2014/07/30 08:39:41 mclaren#his.de Exp $
 * $Log: KeyEnum.java,v $
 * Revision 1.3  2014/07/30 08:39:41  mclaren#his.de
 * changed HIS-GmbH to HIS-eG. #111173
 *
 * Revision 1.2  2014-07-28 16:40:28  keunecke#his.de
 * Clean up source with save actions #115160
 *
 * Revision 1.1  2012-03-05 15:08:49  mclaren#his.de
 * Refactoring: KeyEnum verschoben #62354
 *
 * Revision 1.3  2008-08-29 10:02:52  burchard
 * Überflüssige Klassen entfernt, JavaDoc
 *
 * Revision 1.2  2008-07-24 08:10:05  hoss
 * Javadoc korrigiert
 *
 * Revision 1.1  2007-12-20 19:48:22  weyland
 * Generischer Key-Enum hinzugefügt, der HKZ und ValueEnum ersetzen soll
 *
 */
package basic.zBasic.persistence.interfaces.enums;

/**
 * Generisches Interface für ein Enum um einen Identifikationsschlüssel eines Objekts
 * abzufragen.
 * 
 * @param <T>
 */
public interface KeyEnum<T> {
    /**
     * Gibt den Identifikationsschlüssel eines Objekts zurück.
     * 
     * @return Identifikationsschlüssel des Objekts
     */
    public T getKey();
}
/*
 * Copyright (c)2009 Hochschul-Informations-System eG
 *
 * $Id: HisKeyValue.java,v 1.7 2017/06/26 08:16:54 siegel#his.de Exp $
 *
 * $Log: HisKeyValue.java,v $
 * Revision 1.7  2017/06/26 08:16:54  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.6  2017/06/26 07:53:22  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.5  2014/07/30 09:19:21  mclaren#his.de
 * changed HIS-GmbH to HIS-eG. #111173
 *
 * Revision 1.4  2014-07-28 16:43:57  keunecke#his.de
 * Clean up source with save actions #115160
 *
 * Revision 1.3  2009-10-30 14:35:44  t.neumann#his.de
 * Alle HisKeyValue-Unterklassen sollten (vermutlich ;-) serializable sein
 *
 * Revision 1.2  2009-09-11 12:56:00  d.scholz#his.de
 * In allen Schlüsseltabellen-Entities, die das Interface HisKeyValue implementieren, die Methode getHiskeyEnumClass() ergänzt, die das Enum zu den hiskeyIds zurück liefert, sofern es eines gibt. Wird für die Schlüsseltabellenbearbeitung verwendet.
 *
 * Revision 1.1  2009-07-01 15:56:59  weyland#werkstoffbit.de
 * Added interface to query his key identifiers
 */
package basic.zBasic.persistence.interfaces.enums;

import java.io.Serializable;


/**
 * Value object enabling the setting and getting of his key identifiers.
 *
 * @author mweyland
 * @version $Revision: 1.7 $
 */
public interface IThiskeyValueZZZ<E extends Enum<E> & IThiskeyProviderZZZ<Long>> extends Serializable {

    /**
     * Returns the his key identifier.
     *
     * @return a value representing the his key identifier
     */
   abstract Long getThiskey();

    /**
     * Sets the his key identifier.
     *
     * @param hisKeyId  a value representing the his key identifier to be set
     */
   abstract void setThiskey(Long thisKeyId);

    /**
     * @return HisKeyProvider<Long> Enum mit allen unterstützten Hiskeys
     */
   abstract Class<E> getThiskeyEnumClass();
}

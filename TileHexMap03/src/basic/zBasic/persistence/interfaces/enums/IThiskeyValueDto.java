/*
 * Copyright (c) 2011 HIS eG All Rights Reserved.
 *
 * $Id: HisKeyValueDto.java,v 1.2 2014/07/30 09:33:44 mclaren#his.de Exp $
 *
 * $Log: HisKeyValueDto.java,v $
 * Revision 1.2  2014/07/30 09:33:44  mclaren#his.de
 * changed HIS-GmbH to HIS-eG. #111173
 *
 * Revision 1.1  2011-05-11 06:41:35  hoersch#his.de
 * HisKeyValueDto-Interface hinzugefügt
 *
 *
 * Created on 10.05.2011 by hoersch
 */
package basic.zBasic.persistence.interfaces.enums;


/**
 * Value object enabling the setting and getting of his key identifiers.
 *
 * <br />
 * Company: HIS
 * @author hoersch
 * @version $Revision: 1.2 $
 */
public interface IThiskeyValueDto {

    /**
     * Returns the his key identifier.
     *
     * @return a value representing the his key identifier
     */
    public Long getThiskeyId();

    /**
     * Sets the his key identifier.
     *
     * @param hisKeyId  a value representing the his key identifier to be set
     */
    public void setThiskeyId(Long hisKeyId);

    // Das Enum ist für die DTOs (noch) nicht vorgesehen
    //
    // /**
    // * @return HisKeyProvider<Long> Enum mit allen unterstützten Hiskeys
    // */
    // public Class<? extends HisKeyProvider<Long>> getHiskeyEnumClass();
}

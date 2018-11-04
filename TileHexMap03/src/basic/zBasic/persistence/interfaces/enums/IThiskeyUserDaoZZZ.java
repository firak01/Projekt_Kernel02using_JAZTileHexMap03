/*
 * Copyright (c) 2009 HIS eG All Rights Reserved.
 *
 * $Id: HisKeyProvider.java,v 1.4 2017/06/22 15:19:14 siegel#his.de Exp $
 *
 * $Log: HisKeyProvider.java,v $
 * Revision 1.4  2017/06/22 15:19:14  siegel#his.de
 * added comments, better type variable names #000000
 *
 * Revision 1.3  2014/07/30 09:19:10  mclaren#his.de
 * changed HIS-GmbH to HIS-eG. #111173
 *
 * Revision 1.2  2009-07-01 16:09:33  weyland#werkstoffbit.de
 * Changed to accept generic type key
 *
 * Revision 1.1  2009-06-15 08:52:24  weyland#werkstoffbit.de
 * Added enum to identify mimecontexts
 *
 *
 * Created on 12.06.2009 by mweyland
 */

package basic.zBasic.persistence.interfaces.enums;

import java.util.EnumSet;

import use.thm.persistence.model.Key;



/**
 * Provided that gives access to a thiskey.
 * Im Detail auf diesen KeyType beschränkt.
 * Durch Einführen des KeyTypes ist es möglich auch bei der Suche oder dem Löschen die "Vererbung" zu ignorieren, bzw. auf den korrekten KeyType abzuprüfen.
 *
 */
public interface IThiskeyUserDaoZZZ{
    /**
     * Returns a defined his key.
     *
     * @return the hisKey
     */
	public String getKeyTypeUsed(); //z.B. in TroopVariantDaoTHM:  "TROOPARMYVARIANT" , "TROOPFLEETVARIANT"
	
	public Long getThiskeyUsed(); //wird in den Varianten verwendet, u.a. in der VariniantDaoFactory. Hier besteht eine 1:1 Beziehung zwischen der Variante und dem Dao-Objekt.
	abstract void setThiskeyUsed(Long lngThiskey);	
}
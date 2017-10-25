/*
 * Copyright (c) 2013 HIS eG All Rights Reserved.
 *
 * $Log: HisKeyUtil.java,v $
 * Revision 1.8  2017/06/26 09:02:28  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.7  2017/06/26 08:16:55  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.6  2017/06/26 07:53:23  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.5  2017/06/26 07:50:15  siegel#his.de
 * fixing generic types #000000
 *
 * Revision 1.4  2017/06/22 15:04:31  siegel#his.de
 * added comments, better type variable names #000000
 *
 * Revision 1.3  2017/03/31 10:30:47  jgrimm#werkstoffbit.de
 * added first version of the refactoring for the static application contents (related to #160224)
 *
 * Revision 1.2  2016-07-28 11:09:24  tneumann#his.de
 * removed to-do comment #144916
 *
 * Revision 1.1  2016-07-28 11:02:19  tneumann#his.de
 * HisKeyUtil needs model -> moved from core.util to appserver.model.common #144916
 *
 * Revision 1.17  2016-06-15 17:36:35  bienert-extern#his.de
 * #134437 – zPA: Manuelles Anlegen von Bescheidanforderungen (bwCMS AP10)
 * * technischer Umbau der Bescheide (FlowScope-Handling nun ausschließlich über OutputRequest und Eliminierung der ReportFunctionAllocationDtoImpl-Klasse)
 *
 * Revision 1.16  2014-07-30 08:40:37  mclaren#his.de
 * changed HIS-GmbH to HIS-eG. #111173
 *
 * Revision 1.15  2013-03-15 09:14:04  mshenavai#werkstoffbit.de
 * added unit-tests for verification of preliminary pictures
 *
 * Revision 1.14  2013-01-24 17:10:11  mshenavai#werkstoffbit.de
 * verification of preliminary pictures
 *
 * Revision 1.13  2012-09-21 09:48:41  jgrimm#werkstoffbit.de
 * Added unit-tests for last missing student registration processors
 *
 * Revision 1.12  2012-06-27 10:19:32  jgrimm#werkstoffbit.de
 * made equalsHiskey null-safe
 *
 * Revision 1.11  2012-01-10 14:57:20  jgrimm#werkstoffbit.de
 * added first version of person attribute management to the alumni management profile
 *
 * Revision 1.10  2011-10-26 14:10:20  hoersch#his.de
 * Methode getHiskeyEnumUnchecked()
 *
 * Revision 1.9  2011-07-27 14:13:53  hoersch#his.de
 * neue Hilfsmethode equalsHiskey mit HiskeyValueDto
 *
 * Revision 1.8  2011-07-13 15:48:31  weyland#werkstoffbit.de
 * added HisKeyValueDto to hiskey ID mapping function
 *
 * Revision 1.7  2011-07-13 12:43:46  jgrimm#werkstoffbit.de
 * added method hiskeyIdsOf
 *
 * Revision 1.6  2011-01-27 08:28:20  weyland#werkstoffbit.de
 * using set instead of enum set
 *
 * Revision 1.5  2011-01-25 15:28:05  jgrimm#werkstoffbit.de
 * added some methods returning predicates for hiskeys
 *
 * Revision 1.4  2010-09-07 12:32:21  weyland#werkstoffbit.de
 * Added helper method to collect hiskeys from a given list of hiskey providers
 *
 * Revision 1.3  2010-06-22 14:54:09  weyland#werkstoffbit.de
 * Introduce HisKeyEnumMappingException
 *
 * Revision 1.2  2010-05-26 13:08:49  mlindner#werkstoffbit.de
 * Add method to find the corresponding enum to a hisKey.
 *
 * Revision 1.1  2009-09-29 11:58:49  t.neumann#his.de
 * utility class for hiskey handling
 */
package basic.zBasic.util.genericEnum;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import basic.zBasic.persistence.interfaces.enums.IKeyAccessorZZZ;
import basic.zBasic.persistence.interfaces.enums.IKeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IKeyValueZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueDto;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;


/**
 * Utility class that supports working with applicationkeys.
 *
 * Company: HIS
 * @author T.Neumann
 * @version $Revision: 1.8 $
 */
// TODO REFACTOR: Extract methods using Dto's
public class EnumSetInnerUtilZZZ {
    /**
     * Checked exception which indicates that an hiskey enum mapping has failed.
     *
     * @author mweyland
     * @version $Revision: 1.8 $
     */
    public static class ThiskeyEnumMappingExceptionZZZ extends Exception {
        private static final long serialVersionUID = 1L;

        private final Class<?> providerClass;
        private final Object key;

        /**
         * Constructs the exception with a message the enum type and the hiskey which could not be found
         * within the enum.
         *
         * @param message       the message for this exception
         * @param providerClass the provider of the hiskey
         * @param key           the hiskey which could not be extracted from the provider class
         */
        public ThiskeyEnumMappingExceptionZZZ(String message, Class<?> providerClass, Object key) {
            super(message);
            this.providerClass = providerClass;
            this.key = key;
        }

        /**
         * The class from which the hiskey could not be extracted.
         *
         * @return the provider class
         */
        public Class<?> getProviderClass() {
            return this.providerClass;
        }

        /**
         * The key which could not be found.
         *
         * @return the key
         */
        public Object getKey() {
            return this.key;
        }
    }

    /**
     * Returns all hiskeys of an enumset of HisKeyProviders.
     * These can be easily defined with e.g. <code>EnumSet<ElementtypeValue> enumset = EnumSet.of(ElementtypeValue.ElementtyperHKZ.Pruefung,ElementtypeValue.ElementtyperHKZ.Modul,ElementtypeValue.ElementtyperHKZ.Konto)</code>.
     * Then, given a particular <code>HisKeyValue hiskeyValue</code>, we can check if some functionality shall be applied to it depending on its hiskey simple by
     * <code>enumset.contains(hiskeyValue.getHiskeyId())</code>.
     * @param <E> clazz of enums with HisKeyProvider functionality (required because class Enum can not be subclassed)
     * @param <K> clazz of hiskeys (usually Long, sometimes Integer)
     * @param enumset set of HisKeyProvider enums
     * @return set of his keys covered by the given set of enums
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<K>, K> Set<K> getThiskeySet(Set<? extends E> enumset) {
        Set<K> hiskeys = new HashSet<K>();
        if (enumset != null) {
            for (IKeyProviderZZZ<K> enumValue : enumset) {
                K hiskey = enumValue != null ? enumValue.getThiskey() : null;
                if (hiskey != null) {
                    hiskeys.add(hiskey);
                }
            }
        }
        return hiskeys;
    }

    /**
     * Returns the enum for a specified his key.
     *
     * <p>An failed mapping of a value to an enum instance should usually be handled and recoverable.</p>
     *
     * @param <E>  clazz of enums with IKeyProviderZZZ functionality (required because class Enum can not be subclassed)
     * @param <K>  clazz of hiskeys (usually Long)
     * @param enu  enumerator from which the enum is searched
     * @param key  the key which is searched
     *
     * @return  the found enum
     *
     * @throws HisKeyEnumMappingException    if the passed enumerator does not contain the searched key.
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<K>, K> E getThiskeyEnum(Class<E> enu, K key) throws ThiskeyEnumMappingExceptionZZZ {
        if (key == null) {
            return null;
        }

        for (E e : enu.getEnumConstants()) {
            if (e.getThiskey().equals(key)) {
                return e;
            }
        }

        throw new ThiskeyEnumMappingExceptionZZZ("Supplied Enum type does not contain key!", enu, key);
    }

    /**
     * Returns the enum for a specified his key.
     *
     * <p>An failed mapping of a value to an enum instance should usually be handled and recoverable.</p>
     *
     * @param <E>           clazz of enums with HisKeyProvider functionality (required because class Enum can not be subclassed)
     * @param hisKeyValue   the value to convert
     *
     * @return  the found enum
     *
     * @throws HisKeyEnumMappingException    if the value could not be converted.
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<Long>> E getThiskeyEnum(IKeyValueZZZ<E> hisKeyValue) throws ThiskeyEnumMappingExceptionZZZ {
        if (hisKeyValue == null) {
            return null;
        }

        for (E e : hisKeyValue.getThiskeyEnumClass().getEnumConstants()) {
            if (e.getThiskey().equals(hisKeyValue.getThiskeyId())) {
                return e;
            }
        }

        throw new ThiskeyEnumMappingExceptionZZZ("Supplied Enum type does not contain key!", hisKeyValue.getThiskeyEnumClass(), hisKeyValue.getThiskeyId());
    }

    /**
     * Returns the enum for a specified his key.
     *
     * <p>An failed mapping of a value to an enum instance should usually be handled and recoverable.</p>
     *
     * @param <E>  clazz of enums with HisKeyProvider functionality (required because class Enum can not be subclassed)
     * @param <K>  clazz of hiskeys (usually Long)
     * @param enu  enumerator from which the enum is searched
     * @param key  the key which is searched
     *
     * @return  the found enum
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<K>, K> E getThiskeyEnumUnchecked(Class<E> enu, K key) {
        try {
            return getThiskeyEnum(enu, key);
        } catch (ThiskeyEnumMappingExceptionZZZ e) {
            Throwables.propagate(e);
        }
        return null;
    }

    /**
     * Collects a set of hiskeys collected from the provided hiskey providers.
     *
     * @param <E>       the type of the hiskey provider
     * @param <K>       the type of the hiskey
     *
     * @return a set of the collected hiskeys
     */
    public static <E extends IKeyProviderZZZ<K>, K> Set<K> thiskeysOf(E firstHiskeyProvider, E secondHiskeyProvider) {
        Set<K> hiskeys = Sets.newLinkedHashSet();
        hiskeys.add(firstHiskeyProvider.getThiskey());
        hiskeys.add(secondHiskeyProvider.getThiskey());
        return hiskeys;
    }

    /**
     * Collects a set of hiskey-ids collected from the provided hiskey providers.
     *
     * @param <E>               the type of the hiskey provider
     * @param hiskeyProviders   the key providers
     *
     * @return a set of the collected hiskey-ids
     */
    public static <E extends IKeyProviderZZZ<Long>> Set<Long> thiskeyIdsOf(Set<E> hiskeyProviders) {
        return newHashSet(transform(hiskeyProviders, toThiskey()));
    }

    /**
     * Returns a predicate that matches if a {@link IKeyProviderZZZ} has the given hiskey.
     *
     * @param hiskey    the required hiskey
     *
     * @return a predicate
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<K>, K> Predicate<E> hasThiskey(final K hiskey) {
        return new Predicate<E>() {
            @Override
            public boolean apply(E hiskeyProvider) {
                return hiskeyProvider.getThiskey().equals(hiskey);
            }
        };
    }

    /**
     * Returns a function that returns the hiskey provided by a {@link IKeyProviderZZZ}.
     *
     * @return a function
     */
    public static Function<IKeyProviderZZZ<Long>, Long> toThiskey() {
        return new Function<IKeyProviderZZZ<Long>, Long>() {
            @Override
            public Long apply(IKeyProviderZZZ<Long> hiskeyProvider) {
                return hiskeyProvider.getThiskey();
            }
        };
    }

    /**
     * @param accessor that provides a {@code HisKey} of a given element
     * @return a function
     */
    public static <E> Function<E, Long> toThiskey(final IKeyAccessorZZZ<E, Long> accessor) {
        return new Function<E, Long>() {
            @Override
            public Long apply(E hiskeyProvidingElement) {
                return accessor.getThiskey(hiskeyProvidingElement);
            }
        };
    }

    /**
     * Returns a function that returns the hiskey ID provided by a {@link IThiskeyValueDto}.
     *
     * @return a function
     */
    public static Function<IThiskeyValueDto, Long> valueDtoToThiskey() {
        return new Function<IThiskeyValueDto, Long>() {
            @Override
            public Long apply(IThiskeyValueDto input) {
                return input.getThiskeyId();
            }
        };
    }

    /**
     * Returns a function that returns the hiskey ID provided by a {@link IKeyValueZZZ}.
     *
     * @return a function
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<Long>> Function<IKeyValueZZZ<E>, Long> valueToThiskey() {
        return new Function<IKeyValueZZZ<E>, Long>() {
            @Override
            public Long apply(IKeyValueZZZ<E> input) {
                return input.getThiskeyId();
            }
        };
    }

    /**
     * Returns whether a {@link IKeyValueZZZ} equals another {@link IKeyValueZZZ}
     * with respect to the hiskey.
     *
     * <p>If one of the arguments ({@code firstValue, secondValue}) is {@code null}
     * the result will be {@code false} without throwing an exception.</p>
     *
     * @param firstValue    a the hiskey value
     * @param secondValue   another hiskey value
     * @return {@code true} if the hiskeys of the value are equal
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<Long>> boolean equalsThiskey(IKeyValueZZZ<E> firstValue, IKeyValueZZZ<E> secondValue) {
        if (firstValue == null) {
            return false;
        }

        if (secondValue == null) {
            return false;
        }

        return equal(firstValue.getThiskeyId(), secondValue.getThiskeyId());
    }

    /**
     * Returns whether a {@link IKeyValueZZZ} equals a specified {@link IKeyProviderZZZ}
     * with respect to the hiskey.
     *
     * <p>If one of the arguments ({@code value, provider}) is {@code null}
     * the result will be {@code false} without throwing an exception.</p>
     *
     * @param value     the hiskey value
     * @param provider  the hiskey provider
     * @return {@code true} if the hiskeys of the provider and the value are equal
     */
    public static <E extends Enum<E> & IKeyProviderZZZ<Long>> boolean equalsThiskey(IKeyValueZZZ<E> value, IKeyProviderZZZ<Long> provider) {
        if (value == null) {
            return false;
        }

        if (provider == null) {
            return false;
        }

        return equal(value.getThiskeyId(), provider.getThiskey());
    }

    /**
     * Returns whether a {@link IThiskeyValueDto} equals a specified {@link IKeyProviderZZZ}
     * with respect to the hiskey.
     *
     * <p>If one of the arguments ({@code value, provider}) is {@code null}
     * the result will be {@code false} without throwing an exception.</p>
     *
     * @param value     the hiskey value
     * @param provider  the hiskey provider
     *
     * @return {@code true} if the hiskeys of the provider and the value are equal
     */
    public static boolean equalsThiskey(IThiskeyValueDto value, IKeyProviderZZZ<Long> provider) {
        if (value == null) {
            return false;
        }

        if (provider == null) {
            return false;
        }

        return equal(value.getThiskeyId(), provider.getThiskey());
    }

}

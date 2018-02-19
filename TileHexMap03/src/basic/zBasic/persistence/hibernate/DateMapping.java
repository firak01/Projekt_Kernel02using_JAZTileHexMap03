package basic.zBasic.persistence.hibernate;
//package de.his.appserver.persistence.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor;
import org.hibernate.type.descriptor.java.JdbcTimeTypeDescriptor;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.UserVersionType;

import com.google.common.base.Optional;




//import de.his.core.base.invariants.EnsureArgument;
import base.invariants.EnsureArgument;
//import de.his.core.datatype.KeyEnum;
import base.datatype.KeyEnum; 
//import de.his.core.util.KeyEnumHelper;
import base.datatype.KeyEnumHelper;


/**
 * Date mapping for {@link java.util.Date} properties of an entity.
 *
 * <p>This may be used to ensure that the property value is between
 * the min date {@code 01.01.1000 00:00:00.000} and the max date
 * {@code 31.12.2100 23:59:59.999}. If the date does not match the
 * constraint a {@link HibernateException} will be thrown.
 *
 * <p>By default the {@code sqlType} of {@link StandardBasicTypes#TIMESTAMP}
 * will be used. If another type is required this may be configured (see below
 * for examples).</p>
 *
 * <p><strong>Usage:</strong></p>
 *
 * <p>Default usage with {@code sqlType} {@link StandardBasicTypes#TIMESTAMP}.</p>
 *
 * <pre><code>
 * &#64;Entity
 * public class MyEntity extends AbstractPersistentObject {
 *
 *     &#64;Type(type = DateMapping.USER_TYPE_NAME)
 *     private Date myProperty;
 *
 * }
 * </code></pre>
 *
 * <p>By default the dates will be mapped to the {@code sqlType} {@link StandardBasicTypes#TIMESTAMP}.
 * If a mapping to {@link StandardBasicTypes#TIME} or {@link StandardBasicTypes#DATE} is required
 * this may be specified as follows:</p>
 *
 * <pre><code>
 * &#64;Entity
 * public class MyEntity extends AbstractPersistentObject {
 *
 *     &#64;Type(type = DateMapping.USER_TYPE_NAME, parameters = &#64;Parameter( name = DateMapping.DATE_TYPE, value = DateMapping.DATE_TYPE_TIME))
 *     private Date myProperty;
 *
 * }
 * </code></pre>
 *
 * <p>or</p>
 *
 * <pre><code>
 * &#64;Entity
 * public class MyEntity extends AbstractPersistentObject {
 *
 *     &#64;Type(type = DateMapping.USER_TYPE_NAME, parameters = &#64;Parameter( name = DateMapping.DATE_TYPE, value = DateMapping.DATE_TYPE_DATE))
 *     private Date myProperty;
 *
 * }
 * </code></pre>
 *
 * @author jgrimm
 * @version $Revision: 1.10 $
 */
//@formatter:off

//FGL: Original HIS, ohne UserVersionType: public class DateMapping implements UserType, ParameterizedType{
//UserVersionType ist wohl notwendig, damit man @Version der Spalte hinzufügen kann...
//FGL: Das klappt aber so nicht  public class DateMapping implements UserType, ParameterizedType, UserVersionType {
public class DateMapping implements UserType, ParameterizedType{
    /**
     * Date types supported by the {@link DateMapping}.
     */
    public static enum DateType implements KeyEnum<String> {
        /** A date mapping (corresponds to {@link StandardBasicTypes#DATE}). */
        DATE(DateMapping.DATE_TYPE_DATE, StandardBasicTypes.DATE.sqlType(), true) {
            @Override
            Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
                java.sql.Date date = rs.getDate(names[0]);

                if (rs.wasNull()) {
                    return null;
                }

                return new java.sql.Date(date.getTime());
            }

            @Override
            void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException {
                java.sql.Date date = new java.sql.Date(value.getTime());
                st.setDate(index, date);
            }

            @Override
            Date deepCopy(Date value) {
                return JdbcDateTypeDescriptor.INSTANCE.getMutabilityPlan().deepCopy(value);
            }
        },

        /** A time mapping (corresponds to {@link StandardBasicTypes#TIME}). */
        TIME(DateMapping.DATE_TYPE_TIME, StandardBasicTypes.TIME.sqlType(), false) {
            @Override
            Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
                Time time = rs.getTime(names[0]);

                if (rs.wasNull()) {
                    return null;
                }

                return new java.sql.Time(time.getTime());
            }

            @Override
            void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException {
                Time time = new Time(value.getTime());
                st.setTime(index, time);
            }

            @Override
            Date deepCopy(Date value) {
                return JdbcTimeTypeDescriptor.INSTANCE.getMutabilityPlan().deepCopy(value);
            }
        },

        /** A timestamp mapping (corresponds to {@link StandardBasicTypes#TIMESTAMP}). */
        TIMESTAMP(DateMapping.DATE_TYPE_TIMESTAMP, StandardBasicTypes.TIMESTAMP.sqlType(), true) {
            @Override
            Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
                Timestamp timestamp = rs.getTimestamp(names[0]);

                if (rs.wasNull()) {
                    return null;
                }

                return new java.sql.Timestamp(timestamp.getTime());
            }

            @Override
            void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException {
                Timestamp timestamp = new Timestamp(value.getTime());
                st.setTimestamp(index, timestamp);
            }

            @Override
            Date deepCopy(Date value) {
                return JdbcTimestampTypeDescriptor.INSTANCE.getMutabilityPlan().deepCopy(value);
            }
        };

        private final int sqlType;
        private final String key;
        private final boolean performDateValidation;

        private DateType(String key, int sqlType, boolean performDateValidation) {
            this.key = key;
            this.sqlType = sqlType;
            this.performDateValidation = performDateValidation;
        }

        @Override
        public String getKey() {
            return key;
        }

        int getSqlType() {
            return sqlType;
        }

        boolean isPerformDateValidation() {
            return performDateValidation;
        }

        abstract Date nullSafeGet(ResultSet rs, String[] names) throws SQLException;

        abstract void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException;

        abstract Date deepCopy(Date value);
    }

    /** The name of the user type required for the {@link Type}-annotation ({@link Type#type()}). */
    //FGL 20180215: Das ist der Origanle PackageName dieser Klasse. Natürlich angepasst
    //public static final String USER_TYPE_NAME = "de.his.appserver.persistence.hibernate.DateMapping";
    public static final String USER_TYPE_NAME = "basic.zBasic.persistence.hibernate.DateMapping";

    /** The {@code dateType} property. See {@link DateMapping} for usage example. */
    public static final String DATE_TYPE = "dateType";

    /** dateType for dates; Corresponds to {@link DateType#DATE} */
    public static final String DATE_TYPE_DATE = "DATE";

    /** dateType for times; Corresponds to {@link DateType#TIME} */
    public static final String DATE_TYPE_TIME = "TIME";

    /** dateType for timestamps (default); Corresponds to {@link DateType#TIMESTAMP} */
    public static final String DATE_TYPE_TIMESTAMP = "TIMESTAMP";

    /** the minimum time supported by {@link DateMapping} used for dates within entities */
    public static final Date MIN_TIMESTAMP = parseIso("1000-01-01 00:00:00.000");

    /** the maximum time supported by {@link DateMapping} used for dates within entities */
    public static final Date MAX_TIMESTAMP = parseIso("2100-12-31 23:59:59.999");

    private static final DateType DEFAULT_DATE_TYPE = DateType.TIMESTAMP;

    private static final Log LOGGER = LogFactory.getLog(DateMapping.class);

    private DateType dateType = DEFAULT_DATE_TYPE;

    private static final Class<Date> RETURNED_CLASS = Date.class;

    private static Date parseIso(String date) {
        try {
	        //FGL 20180218: An der ganzen Date-Lösung der HIS hängen unzählige andere Klassen, für die dann wieder zahlreiche Bibliotheken importiert werden müssen (inklusive aspectj - Tools)         	       
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXX  FGL MISSING DateUtil.parseISO(date).                            xxxxxxxxxxxxxx");
	    	System.out.println("XXXXXX  Hier für war es notwendig viele andere Bibliotheken aufzunehmen XXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	//return de.his.core.common.datetime.DateUtil.parseISO(date);
	    	return base.common.datetime.DateUtil.parseISO(date);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse ISO date '" + date + "'.", e);
        }
    }

    /**
     * Checks whether the given field is marked with the given {@code dateType} or not.
     *
     * @param field     the field to check
     * @param dateType  the required date type
     *
     * @return {@code true} if the field is a {@link Date} and has the given type; {@code false otherwise}
     *
     * @throws IllegalArgumentException <ul>
     *                                      <li>if {@code field == null}</li>
     *                                      <li>if {@code dateType == null}</li>
     *                                  </ul>
     */
    public static boolean hasDateType(Field field, DateType dateType) {
        EnsureArgument.notNull(field, "field must not be null");
        EnsureArgument.notNull(dateType, "dateType must not be null");

        if (!Date.class.isAssignableFrom(field.getType())) {
            return false;
        }

        Type userType = field.getAnnotation(Type.class);
        if (userType == null || !USER_TYPE_NAME.equals(userType.type())) {
            return false;
        }

        Parameter dateTypeParameter = findParameter(userType, DATE_TYPE);
        if (dateTypeParameter == null) {
            return DEFAULT_DATE_TYPE.equals(dateType); // check if is default type
        }

        return dateType.getKey().equals(dateTypeParameter.value());
    }

    /**
     * Checks whether the given {@code date} is within the valid
     * and supported date range or not.
     *
     * @param date  the date to check
     *
     * @return {@code true} if the date is within the valid range; {@code false} otherwise
     *
     * @throws IllegalArgumentException if {@code date == null}
     */
    public static boolean isWithinValidRange(Date date) {
        EnsureArgument.notNull(date, "date must not be null");

        if (underrunsLowerBoundary(date) || exceedsUpperBoundary(date)) {
            return false;
        }

        return true;
    }

    private static Parameter findParameter(Type userType, String parameterName) {
        EnsureArgument.notBlank(parameterName, "parameterName must not be blank");

        Parameter[] parameters = userType.parameters();
        if (parameters == null) {
            return null;
        }

        for (Parameter parameter : parameters) {
            if (parameterName.equals(parameter.name())) {
                return parameter;
            }
        }

        return null;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { dateType.getSqlType() };
    }

    @Override
    public Class<?> returnedClass() {
        return RETURNED_CLASS;
    }

    @Override
    public boolean equals(Object left, Object right) throws HibernateException {
        return Objects.equals(left, right);
    }

    @Override
    public int hashCode(Object obj) throws HibernateException {
        return Objects.hashCode(obj);
    }

    

    /**
     * @param date must not be {@code null}
     */
    private void validateDateRange(Date date) {
        if (underrunsLowerBoundary(date)) {
            throw new HibernateException("Given date '" + date + "' is less than the allowed minimum date '" + MIN_TIMESTAMP + "'.");
        }

        if (exceedsUpperBoundary(date)) {
            throw new HibernateException("Given date '" + date + "' is greater than the allowed maximum date '" + MAX_TIMESTAMP + "'.");
        }
    }

    private static boolean exceedsUpperBoundary(Date date) {
        return MAX_TIMESTAMP.compareTo(date) < 0;
    }

    private static boolean underrunsLowerBoundary(Date date) {
        return MIN_TIMESTAMP.compareTo(date) > 0;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }

        try {
            Date instance = (Date) value;
            return dateType.deepCopy(instance);
        } catch (ClassCastException e) {
            throw new HibernateException("Could not save '" + value + "' as it could not be casted to '" + RETURNED_CLASS.getCanonicalName() + "'.");
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        if (parameters == null) {
            return;
        }

        Optional<String> optionalSqlType = Optional.fromNullable(StringUtils.trimToNull(parameters.getProperty(DATE_TYPE)));
        if (optionalSqlType.isPresent()) {
            try {
                dateType = KeyEnumHelper.getKeyEnum(DateType.class, optionalSqlType.get());
            } catch (IllegalArgumentException e) {
                dateType = DEFAULT_DATE_TYPE;
                LOGGER.warn( DATE_TYPE + "'" + optionalSqlType.get() + "' is not a valid value. Falling back to default dateType. See " + DateMapping.class.getCanonicalName() + " for further information.", e);
            }
        }
    }

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		 return dateType.nullSafeGet(rs, names);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		 if (value == null) {
	            st.setNull(index, dateType.getSqlType());
	            return;
	        }

	        try {
	            Date date = (Date) value;

	            if (dateType.isPerformDateValidation()) {
	                validateDateRange(date);
	            }

	            dateType.nullSafeSet(st, date, index);
	        } catch (ClassCastException e) {
	            throw new HibernateException("Could not save '" + value + "' as it could not be casted to '" + RETURNED_CLASS.getCanonicalName() + "'.");
	        }
	}

	//FGL 20180215: Ziel ist es dies als UserVersionType in Hibernate nutzen zu können.
	//              aber da das so nicht klappt, auskommentiert
//	@Override
//	public int compare(Object arg0, Object arg1) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public Object seed(SessionImplementor session) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object next(Object current, SessionImplementor session) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//IM HIS ORIGINAL	
//	@Override
//    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
//        return dateType.nullSafeGet(rs, names);
//    }
//
//    @Override
//    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
//        if (value == null) {
//            st.setNull(index, dateType.getSqlType());
//            return;
//        }
//
//        try {
//            Date date = (Date) value;
//
//            if (dateType.isPerformDateValidation()) {
//                validateDateRange(date);
//            }
//
//            dateType.nullSafeSet(st, date, index);
//        } catch (ClassCastException e) {
//            throw new HibernateException("Could not save '" + value + "' as it could not be casted to '" + RETURNED_CLASS.getCanonicalName() + "'.");
//        }
//    }

}


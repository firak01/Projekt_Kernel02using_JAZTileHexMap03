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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.hibernate.type.DateType;
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
public abstract class AbstractDateMapping implements UserType, ParameterizedType, IDateType{
//    /**
//     * Date types supported by the {@link AbstractDateMapping}.
//     * 
//     * FGL:
//     * Merke, der konstruktor ist: private DateType(String key, int sqlType, boolean performDateValidation) {
//     */
//    public static enum DateType implements KeyEnum<String> {
//        /** A date mapping (corresponds to {@link StandardBasicTypes#DATE}). */
//        DATE(AbstractDateMapping.DATE_TYPE_DATE, StandardBasicTypes.DATE.sqlType(), true) {
//            @Override
//            Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
//  ................
	
    //FGL 20180313: Die Enumeration bleibt in den aus dieser Abstrakten Klasse erbenden Klassen.
    //FGL 20180305: HIS Original: private static final DateType DEFAULT_DATE_TYPE = DateType.TIMESTAMP;
//  private static final DateType DEFAULT_DATE_TYPE = DateType.TIMESTAMP_SQLITE_FGL;
//  private DateType dateType = DEFAULT_DATE_TYPE;
	

//    /** The name of the user type required for the {@link Type}-annotation ({@link Type#type()}). */
//    public static final String USER_TYPE_NAME = "basic.zBasic.persistence.hibernate.DateMappingString";

    /** The {@code dateType} property. See {@link AbstractDateMapping} for usage example. */
    public static final String DATE_TYPE = "dateType";

    /** dateType for dates; Corresponds to {@link DateType#DATE} */
    public static final String DATE_TYPE_DATE = "DATE";

    /** dateType for times; Corresponds to {@link DateType#TIME} */
    public static final String DATE_TYPE_TIME = "TIME";

    /** dateType for timestamps (default); Corresponds to {@link DateType#TIMESTAMP} */
    public static final String DATE_TYPE_TIMESTAMP = "TIMESTAMP";
    
    /** dateType for timestamps (default). FGL 20180305: Notwendig, weil die Großschreibung TIMESTAMP in den Annotations einen Fehler wirft "Class not found ... TIMESTAMP" zumindest bei SQLITE !; Corresponds to {@link DateType#TIMESTAMP} */
    public static final String DATE_TYPE_TIMESTAMP_SQLITE_FGL = "timestamp";
    
    /** dateType for timestamps (default). FGL 20180305: Also, den Usertype "string" gibt es auch nur in der Kleinscheibung bei SQLITE! "STRING" in den Annotations einen Fehler wirft "Class not found ... STRING" zumindest bei SQLITE !; Corresponds to {@link DateType#STRING} */
    public static final String DATE_TYPE_TIMESTAMP_SQLITE_STRING_FGL = "string";
    public static final String DATE_FORMAT_SIMPLE_FULL_FGL = "dd-MM-yy:HH:mm:SS";
    
    /** the minimum time supported by {@link AbstractDateMapping} used for dates within entities */
    public static final Date MIN_TIMESTAMP = parseIso("1000-01-01 00:00:00.000");

    /** the maximum time supported by {@link AbstractDateMapping} used for dates within entities */
    public static final Date MAX_TIMESTAMP = parseIso("2100-12-31 23:59:59.999");

    private static final Log LOGGER = LogFactory.getLog(AbstractDateMapping.class);

    private static final Class<Date> RETURNED_CLASS = Date.class;

    protected static Date parseIso(String date) {
        try {
	        //FGL 20180218: An der ganzen Date-Lösung der HIS hängen unzählige andere Klassen, für die dann wieder zahlreiche Bibliotheken importiert werden müssen (inklusive aspectj - Tools)         	       
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXX  FGL AbstractDateMapping: MISSING DateUtil.parseISO(date).                            xxxxxxxxxxxxxx");
	    	System.out.println("XXXXXX  Hier für war es notwendig viele andere Bibliotheken aufzunehmen XXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    	//return de.his.core.common.datetime.DateUtil.parseISO(date);
	    	return base.common.datetime.DateUtil.parseISO(date);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse ISO date '" + date + "'.", e);
        }
    }
    
    /*FGL: Ergänzt um das SimpleDateFormat */
    protected static Date parseSimpleDateFormat(String sDate) throws ParseException{
    	Date dateReturn = null;
    	main:{
    		String sDateFormat = AbstractDateMapping.DATE_FORMAT_SIMPLE_FULL_FGL;
    		SimpleDateFormat formatter = new SimpleDateFormat(sDateFormat);
    		
    		dateReturn = formatter.parse(sDate);
    		
    	}//end main:
    	return dateReturn;
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

    protected static Parameter findParameter(Type userType, String parameterName) {
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
    protected void validateDateRange(Date date) {
        if (underrunsLowerBoundary(date)) {
            throw new HibernateException("Given date '" + date + "' is less than the allowed minimum date '" + MIN_TIMESTAMP + "'.");
        }

        if (exceedsUpperBoundary(date)) {
            throw new HibernateException("Given date '" + date + "' is greater than the allowed maximum date '" + MAX_TIMESTAMP + "'.");
        }
    }

    protected static boolean exceedsUpperBoundary(Date date) {
        return MAX_TIMESTAMP.compareTo(date) < 0;
    }

    protected static boolean underrunsLowerBoundary(Date date) {
        return MIN_TIMESTAMP.compareTo(date) > 0;
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

    //FGL: TODO ABER DIESE ABSTRAHIERUNG IST FÜR EINEN ERSTEN WURF WOHL ZU KOMPLEX...
//	abstract  Enum<?> getDateTypeUsed();
//	abstract void setDateTypeUsed(Enum<?> datetype);	
//	abstract Enum<?> getDateTypeObjectUsed();

}


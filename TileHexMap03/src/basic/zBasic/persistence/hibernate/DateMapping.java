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
import basic.zBasic.util.datatype.string.StringZZZ;


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
//UserVersionType wäre wohl notwendig, damit man @Version der Spalte hinzufügen kann...
//FGL: Das klappt aber so nicht  public class DateMapping implements UserType, ParameterizedType, UserVersionType {
public class DateMapping  extends AbstractDateMapping{
    /**
     * Date types supported by the {@link DateMapping}.
     * 
     * FGL:
     * Merke, der konstruktor ist: private DateType(String key, int sqlType, boolean performDateValidation) {
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

			@Override
			void nullSafeSet(PreparedStatement st, String string, int index) throws SQLException {
				//FGL Erweiterung: Den Timestamp auch als String speichern, wenn er als String ankommt.
				//    Wird hier noch nicht genutzt.
				// TODO Auto-generated method stub
				
			}

			@Override
			void nullSafeSet(PreparedStatement st, int index)
					throws SQLException {
				// TODO Auto-generated method stub
				
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

			@Override
			void nullSafeSet(PreparedStatement st, String string, int index)  throws SQLException {
				//FGL Erweiterung: Den Timestamp auch als String speichern, wenn er als String ankommt.
				//    Wird hier noch nicht genutzt.
				// TODO Auto-generated method stub				
			}

			@Override
			void nullSafeSet(PreparedStatement st, int index)
					throws SQLException {
				// TODO Auto-generated method stub
				
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

			@Override
			void nullSafeSet(PreparedStatement st, String string, int index)  throws SQLException  {
				//FGL Erweiterung: Den Timestamp auch als String speichern, wenn er als String ankommt.
				//    Wird hier noch nicht genutzt.
				// TODO Auto-generated method stub				
			}

			@Override
			void nullSafeSet(PreparedStatement st, int index)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
        },                
        
        /** A timestamp mapping (corresponds to {@link StandardBasicTypes#TIMESTAMP}). */
        TIMESTAMP_SQLITE_FGL(DateMapping.DATE_TYPE_TIMESTAMP_SQLITE_FGL, StandardBasicTypes.TIMESTAMP.sqlType(), true) {
        	  @Override
              Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
        		  
        		  //FGL 20180218: An der ganzen Date-Lösung der HIS hängen unzählige andere Klassen, für die dann wieder zahlreiche Bibliotheken importiert werden müssen (inklusive aspectj - Tools)         	       
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXX  FGL DateMapping.nullSafeGet(...)  für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION                       xxxxxxxxxxxxxx");    	    	
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                          		          		  
                  Timestamp timestamp = rs.getTimestamp(names[0]);

                  if (rs.wasNull()) {
                      return null;
                  }

                  return new java.sql.Timestamp(timestamp.getTime());
              }

              @Override
              void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException {
            	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXX  FGL DateMapping.nullSafeSet(...NICHT  String ...) für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxx");    	    	
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                  
            	  
                  Timestamp timestamp = new Timestamp(value.getTime());
                  st.setTimestamp(index, timestamp);
              }

              @Override
              Date deepCopy(Date value) {
                  return JdbcTimestampTypeDescriptor.INSTANCE.getMutabilityPlan().deepCopy(value);
              }

  			@Override
  			void nullSafeSet(PreparedStatement st, String string, int index)  throws SQLException  {
  				//FGL Erweiterung: Den Timestamp auch als String speichern, wenn er als String ankommt.
  				//    Wird hier noch nicht genutzt.
  				// TODO Auto-generated method stub	
  				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXX  FGL DateMapping.nullSafeSet(...String ... muss in Date umgewandelt werden) für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxx");    	    	
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
      	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                
  			}

			@Override
			void nullSafeSet(PreparedStatement st, int index)
					throws SQLException {
				 //FGL 201800314: An der ganzen Date-Lösung der HIS hängen unzählige andere Klassen, für die dann wieder zahlreiche Bibliotheken importiert werden müssen (inklusive aspectj - Tools)         	       
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXX  FGL DateMaping.nullSafeSet(... LEER: Mit Timestamp füllen ...)  für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxxxxxxx");    	    	
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                
    	    	//Hier nun den aktuellen Wert als Timestamp "erzeugen" und speichern
    	    	//Das Klappt. Das Ergebnis ist aber in der SQLite Datenbank ebenfalls nur ein "kryptischer" (d.h. Long Zahl) Timestamp
    			Calendar cal = Calendar.getInstance();
    			//TEST: Setze ein beliebiges Datum
    			//cal.set(2006,5,25);			
    			
    			//Hiermit wird erfolgreich der Timestamp gesetzt.	
    			Date objDate = cal.getTime(); //Ist letztendlich nur der Timestamp
    	    	
    			Timestamp timestamp = new Timestamp(objDate.getTime());
                st.setTimestamp(index, timestamp);
			}
            },
            	
            	/** A timestamp mapping (corresponds to {@link StandardBasicTypes#STRING}). */
                TIMESTAMP_SQLITE_STRING_FGL(DateMapping.DATE_TYPE_TIMESTAMP_SQLITE_STRING_FGL, StandardBasicTypes.STRING.sqlType(), true) {
                    @Override
                    Object nullSafeGet(ResultSet rs, String[] names) throws SQLException {
                    //FGL: Weil wir jetzt auch einen String als Wert zurückgeben wollen: Date nullSafeGet(ResultSet rs, String[] names) throws SQLException {
                		
                        //FGL 20180218: An der ganzen Date-Lösung der HIS hängen unzählige andere Klassen, für die dann wieder zahlreiche Bibliotheken importiert werden müssen (inklusive aspectj - Tools)         	       
            	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            	    	System.out.println("XXXXXX  FGL DateMaping.nullSafeGet(...)  für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxxxxxxx");    	    	
            	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        
            	    	//FGL 20180306: Merke, das ist eine Methode aus einer  - von Hibernate (CustomType) überschreiben Methode aufgerufen wird. 
                		//              Man weiss nix über den Datentyp. Daher kann an nicht einfach eine  "String"-Methode hinzufügen
                		//              sondern muss hierin den Datentyp prüfen.     
            	    	System.out.println("Wert von names[0]='" + names[0] + "'");
            	    	Object obj = rs.getObject(names[0]);
            	    	 if (rs.wasNull()) {
                         	System.out.println("wasNull Zweig");
                             return null;
                         }
            	    	     	    	
            	    	if(obj instanceof String){
            	    		System.out.println("return newTimestamp STRING Zweig");
            	    		String sDateToParse = (String)obj; //Ich verlasse mich darauf, dass nur sinnvolle und gltige Stirngs in der Datenbank stehen.
            	    		System.out.println("String '" + sDateToParse +"'");
            	    		return sDateToParse;    	    		 
            	    		
//            	    		String sDateFormat = DateMapping.DATE_FORMAT_SIMPLE_FULL_FGL;
//            	    		SimpleDateFormat formatter = new SimpleDateFormat(sDateFormat);
//            	    		
//            	    		Date dateReturn = null;
//            	    		try {
//        						dateReturn = formatter.parse(sDateToParse);
//        					} catch (ParseException e) {
//        						// TODO Auto-generated catch block
//        						e.printStackTrace();
//        					}
//            	    		return dateReturn; //Die Rückgabe eines Date - Wertes gibt einen Hibernate Fehler, wenn der Wert als String gespeichert wurde. 	
            	    	}else{
            	    		System.out.println("return newTimestamp NICHT STRING Zweig");
            	    		//obj ist NULL, warum? 
//            	    		Timestamp timestamp = rs.getTimestamp(names[0]);    	    		
//            	    		long  lTimestamp = timestamp.getTime();    	    		
//            	    		return new java.sql.Timestamp(lTimestamp);
            	    		
            	    		java.sql.Timestamp objReturnTimestamp = null;
            	    		if(obj == null){
            	    			System.out.println("return newTimestamp NICHT STRING Zweig. obj ist NULL. Also ganz neu holen.");
            	    			// (2) create a java timestamp object that represents the current time (i.e., a "current timestamp")
            	    		    Calendar calendar = Calendar.getInstance();
            	    		    objReturnTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
            	    		    return objReturnTimestamp;
            	    		    
            	    		  //Aus diesem Timestamp Objekt nun einen String machen.
                	    		//String objReturn = objReturnTimestamp.toGMTString(); //DEPRECATED
//                	    		Date date = new Date();
//                	    		date.setTime(objReturnTimestamp.getTime());
//                	    		
//                	    		SimpleDateFormat objSimpleDateFormat  = new SimpleDateFormat(DateMapping.DATE_FORMAT_SIMPLE_FULL_FGL);
//                	    		String sReturn = objSimpleDateFormat.format(date);
//                	    		return sReturn;
            	    		    
            	    		}else{
            	    			System.out.println("return newTimestamp NICHT STRING Zweig. obj ist vorhanden. Also nach String umwandeln.");
            	    			objReturnTimestamp = new java.sql.Timestamp((Long) obj);      
            	    			return objReturnTimestamp;
            	    			
//            	    			Date date = new Date();    	    			
//                	    		date.setTime(objReturnTimestamp.getTime());
//                	    		
//                	    		SimpleDateFormat objSimpleDateFormat  = new SimpleDateFormat(DateMapping.DATE_FORMAT_SIMPLE_FULL_FGL);
//                	    		String sReturn = objSimpleDateFormat.format(date);
//                	    		return sReturn;
            	    		}    	    		    	    		
                    	}
            }

            @Override
            void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException {
            	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXX  FGL DateMapping.nullSafeSet(... DATE! Muss nach String!! ...) für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxx");    	    	
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                
                //Timestamp timestamp = new Timestamp(value.getTime());                   	    	
    	    	Date date = new Date();    	    			
	    		date.setTime(value.getTime());
	    		
	    		SimpleDateFormat objSimpleDateFormat  = new SimpleDateFormat(DateMapping.DATE_FORMAT_SIMPLE_FULL_FGL);
	    		String sTimestamp = objSimpleDateFormat.format(date);

    	    	//Das gibt es nicht.. st.setTimestamp(index, sTimestamp);
	    		st.setString(index, sTimestamp);
            }

            //FGL: Erweiterungsvariante: Wenn der Zeitstempel als String ankommt, ihn auch als String speichern.
            //     Merke: Der String ist zuvor in DateMapping.nullSafeSet(...) valide geparsed worden und hinsichtlich der Datumswerte (Range, min und max Datum) geprüft worden.
            @Override
            void nullSafeSet(PreparedStatement st, String sValue, int index) throws SQLException {                      	       
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXX  FGL DateMapping.nullSafeSet(... String ...) für die als Usertype angegebene DateMapping Klasse. Hier: ENUMERATION xxxxxxxxxxxxxx");    	    	
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                
                st.setString(index, sValue);
            }
            
            @Override
            Date deepCopy(Date value) {
                return JdbcTimestampTypeDescriptor.INSTANCE.getMutabilityPlan().deepCopy(value);
            }

			@Override
			void nullSafeSet(PreparedStatement st, int index)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
        };

        private final int sqlType;
        private final String key;
        private final boolean performDateValidation;

        /*FGL: Der Konstruktor für die Enumeration-Klassen*/
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

        //abstract Date nullSafeGet(ResultSet rs, String[] names) throws SQLException;
        //FGL: Ggfs. auch einen STRING als Timestamp zurückgeben können        
        abstract Object nullSafeGet(ResultSet rs, String[] names) throws SQLException;

        abstract void nullSafeSet(PreparedStatement st, Date value, int index) throws SQLException;

        abstract Date deepCopy(Date value);

        //FGL 20180306: Erweiterung: Den Timestamp auch als String speichern, wenn er als String ankommt
        //              Beachte: Dieser Timestamp wird dann nicht validiert sein bzgl. einrs bestimmten "erlaubten" Stringformats.
        //                       DateMappingString erlaubt hingegen nur valide Strings.
		abstract void nullSafeSet(PreparedStatement st, String string, int index) throws SQLException;
		
		//FGL 20180314: Erweiterung: Wenn man schon CustomTypes in den HibernateAnnotations verwendet,
		//                           dann kann man sich für einen Timestamp auch die ganze Parameterübergabe sparen.
		//                           In den Methoden wird dann der gewünschte Wert einfach gesetzt. 
		//                           Ist sinnvollerweise das aktuelle Datum in irgendeiner Form (Date-Objekt, String, ...)
		abstract void nullSafeSet(PreparedStatement st, int index) throws SQLException;
    }

    /** The name of the user type required for the {@link Type}-annotation ({@link Type#type()}). */
    //FGL 20180215: Das ist der Originale PackageName dieser Klasse. Natürlich angepasst
    //public static final String USER_TYPE_NAME = "de.his.appserver.persistence.hibernate.DateMapping";
    public static final String USER_TYPE_NAME = "basic.zBasic.persistence.hibernate.DateMapping";

    //FGL 20180305: HIS Original: private static final DateType DEFAULT_DATE_TYPE = DateType.TIMESTAMP;
    //Merke: Diese Enumeration-Klasse wird aufgerufen, wenn in den Annotation diese Klasse als CustomType genannt wird: 
    //      Also: @Type(type = DateMappingString.USER_TYPE_NAME)
    private static final DateType DEFAULT_DATE_TYPE = DateType.TIMESTAMP_SQLITE_FGL;

    private static final Log LOGGER = LogFactory.getLog(DateMapping.class);

    private DateType dateType = DEFAULT_DATE_TYPE; //Merke: Hier wird dataType mit der angegebenen Enumeration-Klasse initialisiert

    private static final Class<Date> RETURNED_CLASS = Date.class;

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



    @Override
    public int[] sqlTypes() {
        return new int[] { dateType.getSqlType() };
    }

    @Override
    public Class<?> returnedClass() {
        return RETURNED_CLASS;
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
		//FGL 20180306: Merke, das ist eine Methode von Hibernate (CustomType), die überschreiben wird. 
		//              Daher kann an nicht einfach eine  "String"-Methode hinzufügen
		//               sondern muss eine andere dateType-Methode aufrufen.
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXX  FGL DateMapping.nullSaveGet(ResultSet rs, Sring[] names, ...) für die als Usertype angegebene DateMapping Klasse. Hier: In der Klasse selbst xxxxxxx");    	
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		return dateType.nullSafeGet(rs, names);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		//FGL 20180306: Merke, das ist eine Methode von Hibernate (CustomType), die überschreiben wird. 
		//              Daher kann an nicht einfach eine  "String"-Methode hinzufügen
		//               sondern muss eine andere dateType-Methode aufrufen.
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxcxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXX  FGL DateMapping.nullSafeSet(... Object value ...) für die als Usertype angegebene DateMapping Klasse. Hier: In der Klasse selbst xxxxxxxxxxxxx");    	
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		 if (value == null) {
			 System.out.println("Null - Zweig");
//	            st.setNull(index, dateType.getSqlType());

			   //20180314: Fall NULL übergeben wird, soll nun für den Datatype die Methode OHNE Wertübergabe aufgerufen werden.
			   //          Dann wird ggfs. ein Defaultwert eingetragen.
			 	dateType.nullSafeSet(st, index);
			 	return; //Hiernach dann beenden, weil value NULL ist schlägt alles nachfolgende fehl.
	        }

	        try {
	        	System.out.println("Not Null - Zweig. Umwandlung in date und danach Validierung.");
	        	System.out.println("Datetype='"+dateType.getKey()+"'");
	        	 Date date = null;
	        	//FGL: TODO GOON 20180305: Hier tritt dann der CASTING Fehler auf, 
	        	//     wenn man @Type(type = DateMapping.USER_TYPE_NAME) als Annotation an eine String-Spalte angibt.
	        	
	        	//Also: Prüfen, ob String. 
	        	boolean bSaveAsString = false;
	        	if(value instanceof String){	
	        		bSaveAsString = true;
	        		String sToParse = (String) value;
	        		System.out.println("Übergebener String '" + sToParse + "'.");
	        		
	        		//Besonderheit: Falls ein Leerstring übergeben wurde hier wie beim NULL Fall verfahren.
	        		if(StringZZZ.isBlank((String) value)){
	        			dateType.nullSafeSet(st, index);
	    			 	return; //Hiernach dann beenden. Wie beim value NULL ist schlägt alles nachfolgende fehl.
	        		}
	        			        		
	        		System.out.println("Parse String '" + sToParse + "' nach einem Datumswert.");
	        		
	        		//Diverse Parse-Varianten. Merke: Fehler beim Parsen abfangen.
	        		boolean isParsingError = true;
	        		
	        		//1. Variante:
	        		if(isParsingError){
		        		try{
		        			System.out.println("1. Variante: Iso");			        		
			        		date = parseIso(sToParse); 
			        		isParsingError = false;
			        		
		        		}catch (Exception e){
		        			isParsingError = true;	        			
		        		}
	        		}
	        		
	        		//2. Variante
	        		//Falls es String ist mit "Simple Date Format arbeiten und diesen String irgendwie parsen, um daraus ein echtes Datum zu bekommen.
		            //ABER: Nicht vergessen. Falls es String ist, soll es auch als String gespeichert werden.
	        		if(isParsingError){
		        		try{
		        			System.out.println("2. Variante: SimpleDateFormat");			        		
		        			date = parseSimpleDateFormat(sToParse); 
			        		isParsingError = false;
		        		}catch (ParseException e){
		        			isParsingError = true;	        			
		        		}
	        		}
	        		
	        		//Wenn alle Parseversuche gescheitert sind:
	        		//HIER KEINENE FEHLER WERFEN
	        		if(isParsingError){
	        			//throw new RuntimeException("Could not parse date '" + sToParse + "'.");
	        			bSaveAsString = true;
	        		}
	        	}else{
	        		System.out.println("Caste value nach Date.");
	        		date = (Date) value;
	        	}                                     
	          
	        	System.out.println("dateType=" + dateType.key);
	            if (dateType.isPerformDateValidation()) {
	            	System.out.println("Validiere Datumswert.");
	                validateDateRange(date);
	            }

	            if(bSaveAsString){
	                 //FGL 20180306: Erweiterung: String rein und Speichern als String
	            	System.out.println("DateMapping.nullSafeSet(...): bSaveAsString=TRUE");
	            	System.out.println("dateType.key = '" + dateType.key + "'");
	            	dateType.nullSafeSet(st, ((String)value), index);
	            }else{
	            	System.out.println("DateMapping.nullSafeSet(...): bSaveAsString=FALSE");
	            	System.out.println("dateType.key = '" + dateType.key + "'");
	            	dateType.nullSafeSet(st, date, index);	            
	            }
	        } catch (ClassCastException e) {
	            throw new HibernateException("Could not save '" + value + "' as it could not be casted to '" + RETURNED_CLASS.getCanonicalName() + "'.");
	        }
	}
}


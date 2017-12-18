package basic.zBasic.persistence.hibernate;

import org.hibernate.cfg.Configuration;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;

public abstract class HibernateConfigurationProviderZZZ implements IHibernateConfigurationProviderZZZ, IConstantZZZ{					
	private Configuration objConfiguration = new Configuration();
		
	public HibernateConfigurationProviderZZZ() throws ExceptionZZZ{
		//NEIN: Sonst wird die Konfiguration 2x gefüllt. fillConfiguration();
		//      Grund: HibernateContextPRoviderZZZ.fillConfiguration() ruft explizit fillConfiguration() auf.
	}
	
	public abstract boolean fillConfiguration() throws ExceptionZZZ;

	public abstract boolean fillConfigurationGlobal() throws ExceptionZZZ;

	public abstract boolean fillConfigurationLocalDb() throws ExceptionZZZ;

	public abstract boolean fillConfigurationMapping() throws ExceptionZZZ;
	
	/** Wird eine zu persisierende Klasse nicht der Konfiguration übergeben, kommt es z.B. zu folgender Fehlermeldung
	 *  Exception in thread "main" org.hibernate.MappingException: Unknown entity: use.thm.client.component.AreaCellTHM
	 * @param cls
	 * @return
	 * @throws ExceptionZZZ
	 */
	public boolean addConfigurationAnnotatedClass(Configuration cfg, Class cls) throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		cfg.addAnnotatedClass(cls);
		return true;
	}
	

	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		return this.addConfigurationAnnotatedClass(	this.getConfiguration(), cls);
	}
	
	/**Für die so hinzugefügte Klasse muss es eine XML Konfigurationsdatei geben.
	 * Ansonsten Fehler, z.B. für eine User.class : org.hibernate.MappingNotFoundException: resource: tryout/hibernate/User.hbm.xml not found
	 * 
	 * Wird eine zu persisierende Klasse nicht der Konfiguration übergeben, kommt es z.B. zu folgender Fehlermeldung
	 * Exception in thread "main" org.hibernate.MappingException: Unknown entity: use.thm.client.component.AreaCellTHM
	 * @param cls
	 * @return
	 * @throws ExceptionZZZ
	 */
	public boolean addConfigurationClass(Class cls)throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.getConfiguration().addClass(cls);
		return true;
	}
	
	public Configuration getConfiguration() {
		return this.objConfiguration;
	}

}

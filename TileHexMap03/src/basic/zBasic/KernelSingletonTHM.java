package basic.zBasic;

import custom.zKernel.ConfigZZZ;
import use.thm.ApplicationSingletonTHM;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.KernelKernelZZZ;
import basic.zKernel.KernelSingletonZZZ;
import basic.zKernel.KernelZZZ;

public class KernelSingletonTHM extends KernelKernelZZZ{
	private static KernelSingletonTHM objKernelSingleton; //muss als Singleton static sein	
	public static KernelSingletonTHM getInstance() throws ExceptionZZZ{
		if(objKernelSingleton==null){
			//Das hier nur zu initialisieren ist falsch. Schliesslich kennt man doch den Application-Key
			//String[] saFlagZ={"init"};
			//objKernelSingelton = new KernelSingletonZZZ(saFlagZ);	
			
			//Verwende hier Config-Objekt mit dem gleichen Suffix der Klasse, also THM
			IKernelConfigZZZ objConfig = new ConfigTHM();
			objKernelSingleton = new KernelSingletonTHM(objConfig, (String) null);			
		}
		return objKernelSingleton;	
	}
		
	public static  KernelSingletonTHM getInstance(IKernelConfigZZZ objConfig, String sFlagControl) throws ExceptionZZZ{
		if(objKernelSingleton==null){
			objKernelSingleton = new KernelSingletonTHM(objConfig, sFlagControl);
		}
		return objKernelSingleton;	
	}
	
	public static  KernelSingletonTHM getInstance(IKernelConfigZZZ objConfig, String[] saFlagControl) throws ExceptionZZZ{
		if(objKernelSingleton==null){
			objKernelSingleton = new KernelSingletonTHM(objConfig, saFlagControl);
		}
		return objKernelSingleton;	
	}
		
	public static KernelSingletonTHM getInstance(String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{			
		if(objKernelSingleton==null){
			//Verwende hier das Suffix der Klasse als Applicationkey, also THM.
			objKernelSingleton = new KernelSingletonTHM("THM", sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
		}
		return objKernelSingleton;	
	}
	
//	public static KernelSingletonTHM getInstance(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
//	if(objKernelSingelton==null){
//		objKernelSingelton = new KernelSingletonTHM(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
//	}
//	return objKernelSingelton;	
//}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonTHM() throws ExceptionZZZ{
			super();
		}
		
		private KernelSingletonTHM(String[] saFlagControl) throws ExceptionZZZ{
			super(saFlagControl);
		}
		
		//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonTHM(IKernelConfigZZZ objConfig, String sFlagControl) throws ExceptionZZZ{
			super(objConfig, sFlagControl);
		}
		
		//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonTHM(IKernelConfigZZZ objConfig, String[] saFlagControl) throws ExceptionZZZ{
			super(objConfig, saFlagControl);
		}
		
		private KernelSingletonTHM(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
			super(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
		}		
		
		public String getFileConfigKernelName() throws ExceptionZZZ{			
			return super.getFileConfigKernelName();
		}
		public String getApplicationKey() throws ExceptionZZZ{			
			return super.getApplicationKey();
		}
		
		
		//#### Interfaces
		public IKernelConfigZZZ getConfigObject() throws ExceptionZZZ{
			IKernelConfigZZZ objConfig = super.getConfigObject();
			if(objConfig==null){
				objConfig = new ConfigTHM();
				this.setConfigObject(objConfig);
			}
			return objConfig;
		}
}

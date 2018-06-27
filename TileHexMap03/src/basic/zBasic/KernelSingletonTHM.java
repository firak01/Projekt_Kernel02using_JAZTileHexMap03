package basic.zBasic;

import use.thm.ApplicationSingletonTHM;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.KernelZZZ;

public class KernelSingletonTHM extends KernelZZZ{
	private static KernelSingletonTHM objKernelSingelton; //muss als Singleton static sein
	private static String sFILE_CONFIG_DEFAULT = "ZKernelConfigTileHexMap02Client.ini";  //muss static sein, wg. getInstance()!!!
	private static String sKEY_APPLICATION_DEFAULT = "THM";
	public static KernelSingletonTHM getInstance() throws ExceptionZZZ{
		if(objKernelSingelton==null){
			objKernelSingelton = new KernelSingletonTHM();			
		}
		return objKernelSingelton;	
	}
	
	public static  KernelSingletonTHM getInstance(IKernelConfigZZZ objConfig, String sFlagControl) throws ExceptionZZZ{
		if(objKernelSingelton==null){
			objKernelSingelton = new KernelSingletonTHM(objConfig, sFlagControl);
		}
		return objKernelSingelton;	
	}
	
	public static  KernelSingletonTHM getInstance(IKernelConfigZZZ objConfig, String[] saFlagControl) throws ExceptionZZZ{
		if(objKernelSingelton==null){
			objKernelSingelton = new KernelSingletonTHM(objConfig, saFlagControl);
		}
		return objKernelSingelton;	
	}
	
//	public static KernelSingletonTHM getInstance(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
//		if(objKernelSingelton==null){
//			objKernelSingelton = new KernelSingletonTHM(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
//		}
//		return objKernelSingelton;	
//	}
	
	public static KernelSingletonTHM getInstance(String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
		if(objKernelSingelton==null){
			objKernelSingelton = new KernelSingletonTHM("THM", sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
		}
		return objKernelSingelton;	
	}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
		private KernelSingletonTHM() throws ExceptionZZZ{
			super();
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
		
		public String getFileConfigKernelName(){
			String sFileConfigKernelName = super.getFileConfigKernelName();
			if(StringZZZ.isEmpty(sFileConfigKernelName)){
				sFileConfigKernelName=KernelSingletonTHM.sFILE_CONFIG_DEFAULT;
				super.setFileConfigKernelName(sFileConfigKernelName);
			}
			return super.getFileConfigKernelName();
		}
		public String getApplicationKey(){
			String sApplicationKey = super.getApplicationKey();
			if(StringZZZ.isEmpty(sApplicationKey)){
				sApplicationKey = KernelSingletonTHM.sKEY_APPLICATION_DEFAULT;
				super.setApplicationKey(sApplicationKey);
			}
			return sApplicationKey;
		}
}

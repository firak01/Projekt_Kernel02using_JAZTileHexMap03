package basic.zBasic;

import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.KernelZZZ;

public class KernelSingletonTHM extends KernelZZZ{
	private static KernelSingletonTHM objKernelSingelton; //muss als Singleton static sein

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
	
	public static KernelSingletonTHM getInstance(String sApplicationKey, String sSystemNumber, String sFileConfigPath, String sFileConfigName, String[] saFlagControl ) throws ExceptionZZZ{
		if(objKernelSingelton==null){
			objKernelSingelton = new KernelSingletonTHM(sApplicationKey, sSystemNumber, sFileConfigPath, sFileConfigName, saFlagControl);
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
}

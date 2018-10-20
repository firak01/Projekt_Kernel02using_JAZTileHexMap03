package basic.zBasic;

import basic.zKernel.KernelConfigZZZ;

/**
 *  Default Configuration of this project
 * 
 * @author lindhaueradmin, 20.10.2018, 06:35:47
 * 
 */
@SuppressWarnings("unchecked")
public class ConfigTHM extends KernelConfigZZZ{
	private static final long serialVersionUID = 1L;
	private static String sDIRECTORY_CONFIG_DEFAULT = "";
	private static String sFILE_CONFIG_DEFAULT = "ZKernelConfigTileHexMap02Client.ini";
	private static String sKEY_APPLICATION_DEFAULT = "THM";
	private static String sNUMBER_SYSTEM_DEFAULT= "01";
	
	public ConfigTHM() throws ExceptionZZZ{
		super();
	}
	public ConfigTHM(String[] saArg) throws ExceptionZZZ{
		super(saArg);
	}
	@Override
	public String getApplicationKeyDefault() {
		return ConfigTHM.sKEY_APPLICATION_DEFAULT;
	}
	@Override
	public String getConfigDirectoryNameDefault() {
		return ConfigTHM.sDIRECTORY_CONFIG_DEFAULT;
	}
	@Override
	public String getConfigFileNameDefault() {		
		return ConfigTHM.sFILE_CONFIG_DEFAULT;
	}
	@Override
	public String getPatternStringDefault() {
		return "k:s:f:d:";
	}
	@Override
	public String getSystemNumberDefault() {
		return ConfigTHM.sNUMBER_SYSTEM_DEFAULT;
	}
}

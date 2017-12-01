package use.thm.web.webservice.axis2.pojo;

public class TileDefaulttextPojo{
	private Long lngThiskey;
	private String sShorttext;
	private String sLongtext;
	private String sDescription;

	public void setThiskey(Long lngThiskey) {
		this.lngThiskey = lngThiskey;
	}
	public Long getThiskey(){
		return this.lngThiskey;
	}

	public void setShorttext(String sShorttext) {
		this.sShorttext = sShorttext;
	}
	public String getShorttext(){
		return this.sShorttext;
	}
	
	public void setLongtext(String sLongtext) {
		this.sLongtext = sLongtext;
	}
	public String getLongtext(){
		return this.sLongtext;
	}

	public void setDescriptiontext(String sDescription) {
		this.sDescription = sDescription;
	}
	public String getDescriptiontext(){
		return this.sDescription;
	}

}

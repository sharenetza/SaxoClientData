
public class SaxoClientDataObj {
	
	private String sharenetLogin;
	private String saxoUserId; //BDA nacc
	private String saxoName;
	private String saxoClientKey;
	private long sharenetUidn;
	//private SaxoClientOwner account;
	private String defaultAccountId;
	private String defaultAccountKey;
	private String defaultCurrency;
	private String clientName;
	private String houseId;
	private boolean house;
	
	
	
	/*public SaxoClientOwner getAccount() {
		return account;
	}
	public void setAccount(SaxoClientOwner account) {
		this.account = account;
	}*/
	public String getDefaultAccountId() {
		return defaultAccountId;
	}
	public void setDefaultAccountId(String defaultAccountId) {
		this.defaultAccountId = defaultAccountId;
	}
	public String getDefaultAccountKey() {
		return defaultAccountKey;
	}
	public void setDefaultAccountKey(String defaultAccountKey) {
		this.defaultAccountKey = defaultAccountKey;
	}
	public String getDefaultCurrency() {
		return defaultCurrency;
	}
	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getSharenetLogin() {
		return sharenetLogin;
	}
	public void setSharenetLogin(String sharenetLogin) {
		this.sharenetLogin = sharenetLogin;
	}
	public String getSaxoUserId() {
		return saxoUserId;
	}
	public void setSaxoUserId(String saxoUserId) {
		this.saxoUserId = saxoUserId;
	}
	public String getSaxoName() {
		return saxoName;
	}
	public void setSaxoName(String saxoName) {
		this.saxoName = saxoName;
	}
	public String getSaxoClientKey() {
		return saxoClientKey;
	}
	public void setSaxoClientKey(String saxoClientKey) {
		this.saxoClientKey = saxoClientKey;
	}
	public long getSharenetUidn() {
		return sharenetUidn;
	}
	public void setSharenetUidn(long sharenetUidn) {
		this.sharenetUidn = sharenetUidn;
	}
	/*public void setOwner(SaxoClientOwner acc) {this.account = acc;}
	public SaxoClientOwner getOwner() {return account;}*/
	
	public void print() {
		String s = "SaxoName:" + getSaxoName() + ",SaxoUserId:" + getSaxoUserId() + ",SaxoClientKey:" + getSaxoClientKey();
		System.out.println(s);
 	}
	public String getHouseId() {
		return houseId;
	}
	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}
	public int isHouse() {
		return house ? 1 : 0; 
	}
	public void setHouse(boolean house) {
		this.house = house;
	}

}

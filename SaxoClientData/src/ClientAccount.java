
public class ClientAccount {
	
	
	private String clientKey ="";
	private String accountGroupKey = "";
	private String accountGroupName = "";
	private String accountId = "";
	private String accountKey = "";
	private String accountType = "";
	private boolean active ;
	private String clientId = "";
	private String currency = "";
	private String displayName = "";
	
	
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	public String getAccountGroupKey() {
		return accountGroupKey;
	}
	public void setAccountGroupKey(String accountGroupKey) {
		this.accountGroupKey = accountGroupKey;
	}
	public String getAccountGroupName() {
		return accountGroupName;
	}
	public void setAccountGroupName(String accountGroupName) {
		this.accountGroupName = accountGroupName;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
	void print(){
		
		StringBuffer sb = new StringBuffer();
		sb.append("ClientId:" + getClientId());
		sb.append("DisplayName:" + getDisplayName());
		sb.append("AccountGroupKey:" + getAccountGroupKey());
		sb.append("AccountID:" + getAccountId());
		
		
		System.out.println(sb.toString());
		//System.exit(0);
	}
	

}

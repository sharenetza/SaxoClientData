
public class SaxoClientOwner {
	
	
	private String clientId;
	private String clientKey;
	private String defaultAccountId;
	private String defaultAccountKey;
	private String defaultCurrency;
	private String clientName;
	
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
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
	
	void print(){
		StringBuilder sb = new StringBuilder();
		sb.append("ClientId=" + clientId);
		sb.append("ClientKey=" + clientKey);
		sb.append("DefaultAccountId=" + defaultAccountId);
		sb.append("DefaultAccountKey=" + defaultAccountKey);
		sb.append("DefaultCurrency=" + defaultCurrency);
		sb.append("ClientName=" + clientName);
		System.out.println(sb.toString());
	}

}


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountSummary {

	@SerializedName("Accounts")
	@Expose
	private List<Account> accounts = null;
	@SerializedName("ClientId")
	@Expose
	private String clientId;
	@SerializedName("ClientKey")
	@Expose
	private String clientKey;
	@SerializedName("ClientType")
	@Expose
	private String clientType;
	@SerializedName("DefaultAccountId")
	@Expose
	private String defaultAccountId;
	@SerializedName("DefaultAccountKey")
	@Expose
	private String defaultAccountKey;
	@SerializedName("LegalAssetTypes")
	@Expose
	private List<String> legalAssetTypes = null;
	@SerializedName("Name")
	@Expose
	private String name;

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

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

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
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

	public List<String> getLegalAssetTypes() {
		return legalAssetTypes;
}

public void setLegalAssetTypes(List<String> legalAssetTypes) {
	this.legalAssetTypes = legalAssetTypes;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

}
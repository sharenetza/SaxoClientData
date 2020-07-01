import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account {

	@SerializedName("AccountId")
	@Expose
	private String accountId;
	@SerializedName("AccountKey")
	@Expose
	private String accountKey;
	@SerializedName("AccountType")
	@Expose
	private String accountType;

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

}
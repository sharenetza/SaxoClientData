
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountResponse {

	@SerializedName("__count")
	@Expose
	private Integer count;
	@SerializedName("Data")
	@Expose
	private List<AccountSummary> data = null;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
}

public List<AccountSummary> getData() {
	return data;
}

public void setData(List<AccountSummary> data) {
	this.data = data;
}

}
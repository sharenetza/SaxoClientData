import java.util.ArrayList;

public class SaxoClientsAllClientResponse {
	
	public static final int STATUS_INVALID_TOKEN = 0;
	public static final int STATUS_SUCCESSFULL = 1;
	
	private int status;
	
	private ArrayList<ClientInfo> clientList;
	
	SaxoClientsAllClientResponse(int status){
		this.status = status;
	}
	
	public void setClientInfoList(ArrayList<ClientInfo> list) {
		this.clientList = list;
	}
	public void setStatus(int i) {this.status = i;}
	
	
	public int getStatus() {
		return status;
	}
	public ArrayList<ClientInfo> getClientList() {
		return clientList;
	}

}

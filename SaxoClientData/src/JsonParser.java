import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class JsonParser {
	
	
	public ArrayList<SaxoClientDataObj> parseSaxoClientData(JsonObject json){
		
		ArrayList<SaxoClientDataObj> clientList = new ArrayList<SaxoClientDataObj>();
		
		JsonArray array = json.getJsonArray("Data");
		for(int i = 0;i < array.size();i++) {
			//System.out.println("Client:" + i);
			JsonObject o = array.getJsonObject(i);
			//System.out.println("Parsing:" + o);
			SaxoClientDataObj client = new SaxoClientDataObj();
			client.setSaxoClientKey(o.getString("ClientKey"));
			client.setSaxoName(o.getString("Name"));
			client.setSaxoUserId(o.getString("UserId"));
			client.print();
			clientList.add(client);
			
		}
		
		//System.out.println("Clients:" + clientList.size());
		
		return clientList;
	}

	 ArrayList<ClientAccount> parseAccountDetails(JsonObject json) {

		ArrayList<ClientAccount> accList = new ArrayList<ClientAccount>();
		try {
			if (json == null || !json.containsKey("Data"))
				return null;
		JsonArray array = json.getJsonArray("Data");
		for(int i = 0;i < array.size();i++) {
			JsonObject o = array.getJsonObject(i);
			//System.out.println("ParsingAccount:" + o);
			ClientAccount acc = new ClientAccount();
			acc.setAccountGroupKey(o.getString("AccountGroupKey"));
			if(o.containsKey("AccountGroupName"))
				acc.setAccountGroupName(o.getString("AccountGroupName"));
			acc.setAccountId(o.getString("AccountId"));
			acc.setAccountKey(o.getString("AccountKey"));
			acc.setAccountType(o.getString("AccountType"));
			acc.setActive(o.getBoolean("Active"));
			acc.setClientId(o.getString("ClientId"));
			acc.setClientKey(o.getString("ClientKey"));
			acc.setCurrency(o.getString("Currency"));
			if(o.containsKey("DisplayName"))
				acc.setDisplayName(o.getString("DisplayName"));
			//acc.print();
			accList.add(acc);
			}
		}catch(Exception e) {e.printStackTrace();}
		

		return accList;
		
	}
	 
	 ArrayList<SaxoClientOwner> parseClientOwner(JsonObject json){
		 
		 ArrayList<SaxoClientOwner> ownerList = new ArrayList<SaxoClientOwner>();
		 JsonArray array = json.getJsonArray("Data");
		 for(int i = 0;i < array.size();i++) {
				JsonObject o = array.getJsonObject(i);
				//System.out.println("ParsingAccount:" + o);
				SaxoClientOwner owner = new SaxoClientOwner();
				owner.setClientId(o.getString("ClientId"));
				owner.setClientKey(o.getString("ClientKey"));
				owner.setClientName(o.getString("Name"));
				owner.setDefaultAccountId(o.getString("DefaultAccountId"));
				owner.setDefaultAccountKey(o.getString("DefaultAccountKey"));
				owner.setDefaultCurrency(o.getString("DefaultCurrency"));
				ownerList.add(owner);
		 }
		 
		 
		 return ownerList;
	 }
 
	 	ArrayList<SaxoClientDataObj> parseAccount(JsonObject json){
		 
		 ArrayList<SaxoClientDataObj> ownerList = new ArrayList<SaxoClientDataObj>();
		 JsonArray array = json.getJsonArray("Data");
		 for(int i = 0;i < array.size();i++) {
				JsonObject o = array.getJsonObject(i);
				//System.out.println("ParsingAccount:" + o);
				SaxoClientDataObj owner = new SaxoClientDataObj();
				owner.setSaxoUserId(o.getString("ClientId"));
				owner.setSaxoClientKey(o.getString("ClientKey"));
				owner.setClientName(o.getString("Name"));
				owner.setDefaultAccountId(o.getString("DefaultAccountId"));
				owner.setDefaultAccountKey(o.getString("DefaultAccountKey"));
				owner.setDefaultCurrency(o.getString("DefaultCurrency"));
				ownerList.add(owner);
		 }
		 
		 
		 return ownerList;
	 }
	
}

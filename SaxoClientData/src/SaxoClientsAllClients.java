import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.logging.LoggingFeature;



public class SaxoClientsAllClients {
	
	SaxoClientData saxoClientData;
	
	String sharenetHomeClientKey;
	PropsSaxoCD props;
	
	
	SaxoClientsAllClients(SaxoClientData saxoClientData,PropsSaxoCD props ){
		this.saxoClientData = saxoClientData;
		this.props = props;
		
	}
	
	
	
	public int getAllSubCounterParts(String token, String cookie, String server, String ownerId, String accountType) {
		int totalClients = 0;
		try {
		String baseApi;
		ArrayList<SaxoClientDataObj> clientList;
		
		if(props.getServer().equals("SIM"))
			baseApi = "https://gateway.saxobank.com/sim/openapi/port/v1/";
		else
			baseApi = "https://gateway.saxobank.com/openapi/port/v1/";
		
		String endPoint = baseApi +"clients/?OwnerKey=" + URLEncoder.encode(ownerId, "UTF-8") + "&IncludeSubUsers=true";
		
		String params = URLEncoder.encode("clients/?OwnerKey="  +  ownerId + "&IncludeSubUsers=true","UTF-8");
		endPoint = endPoint + params;

		Client client = ClientBuilder.newClient();
		//System.out.println("DEBUG:" + props.isDebugShowResponse() );
		if(props.isDebugShowResponse()) {
			Logger logger = Logger.getLogger(getClass().getName());
			Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
			client.register(feature);
		}
		WebTarget base = client.target(endPoint);
		Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
		Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token );
		int cookInt = cookie.indexOf("=");
		String cookieName = cookie.substring(0,cookInt);
		String cookieValue = cookie.substring(cookInt + 1,cookie.length() );
		invHeader.cookie(cookieName,cookieValue);
		Invocation inv = invHeader.buildGet();
		Response response = inv.invoke();
		if (response.getStatus() != 200 && response.getStatus() == 401) {
			   System.out.println("Token Invalid SubCounterParts "  + response.getStatus());
			   
			  return -1;
			   
			//return new AllClientResponse(AllClientResponse.STATUS_INVALID_TOKEN);  
		 }
		JsonObject jo = response.readEntity(JsonObject.class);
		//System.out.println(jo.toString());
		
		//////////////////////////////////////////////////////////
		//JSON String containing all sub accounts for this OwnerID
		////////////////////////////////////////////////////////////
		
		JsonParser jparser = new JsonParser();
		clientList = jparser.parseAccount(jo);
		totalClients = clientList.size();
		boolean isHouse = false;
		/*
		 * SubAccount Client count > 1 = a House
		 */
		if(totalClients > 1) {
			System.out.println("HOUSE FOUND:" + clientList.get(0).getClientName());
			//Flag saxoid as a house
			saxoClientData.getJDBC().updateSaxoHouse(ownerId,clientList.get(0).getSaxoUserId(),props.getClientDataTableName());
			isHouse = true;
		}
		
			
		
		
			int cnt = updateDatabase(clientList, server, isHouse, ownerId, accountType);
		
		System.out.println("SaxoSubClientData Count:" + totalClients);
		
		//FETCH ACCOUNTS 
		/*for(SaxoClientDataObj counterPart : clientList) {
			
			ArrayList<ClientAccount> ll =  getAccounts(counterPart.getSaxoClientKey(),token, cookie,props.getServer());
			for(ClientAccount c : ll) {
				System.out.println("Fetching Accounts for:" + counterPart.getClientName() + " " + counterPart.getSaxoUserId() );	
				SaxoClientDataObj saxoClient = saxoClientData.getJDBC().getClient(c.getClientId(), props.getServer(),props.getClientDataTableName());
				saxoClient.setDefaultAccountId(c.getAccountId());
				saxoClient.setDefaultAccountKey(c.getAccountKey());
				int i = saxoClientData.getJDBC().getAccountCount(c,props.getClientDataTableName());
				System.out.println("ClientAccountCount:" + i + " " + counterPart.getSaxoUserId() );
				if(i == 0)
					saxoClientData.getJDBC().insertSaxoClientData(saxoClient, props.getServer(), saxoClient.getHouseId(),props.getClientDataTableName());
				else
					saxoClientData.getJDBC().updateSaxoClientData(saxoClient, props.getServer(), saxoClient.getHouseId(),props.getClientDataTableName());
				
			}
			
			
		}		*/
		
	
		}catch(Exception e) {e.printStackTrace();}
		return totalClients;
	}
	
	public SaxoClientsAllClientResponse getAllClientKeysV2(String token,String cookie,String server,String accountType) {
		String baseApi;
		
		/**
		 * FETCH SHARENET TOP LEVEL CLIENT KEY
		 */
		if(props.getServer().equals("SIM"))
			baseApi = "https://gateway.saxobank.com/sim/openapi/port/v1/";
		else
			baseApi = "https://gateway.saxobank.com/openapi/port/v1/";
		//String cookie = getStickyCookie("https://gateway.saxobank.com/sim/openapi/port/");
		
		String endPoint = baseApi +"clients/me";
		System.out.println(accountType + "-> Fetching Sharenet Top Level ClientKey using EndPoint:" + endPoint);
		Client client = ClientBuilder.newClient();
		//System.out.println("DEBUG:" +PropsTokenMan.isDebugShowResponse() );
		if(props.isDebugShowResponse()) {
			Logger logger = Logger.getLogger(getClass().getName());
			Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
			client.register(feature);
		}
		WebTarget base = client.target(endPoint);
		Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
		Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token );
		int cookInt = cookie.indexOf("=");
		String cookieName = cookie.substring(0,cookInt);
		String cookieValue = cookie.substring(cookInt + 1,cookie.length() );
		invHeader.cookie(cookieName,cookieValue);
		Invocation inv = invHeader.buildGet();
		Response response = inv.invoke();
		if (response.getStatus() != 200 && response.getStatus() == 401) {
			   System.out.println("Token Invalid  AllClientKeysV2"  + response.getStatus());
			   Token_Invalid_Event event = new Token_Invalid_Event(this);
			   saxoClientData.fireEvent(event);
			   
			return new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_INVALID_TOKEN);  
		 }
		JsonObject jo = response.readEntity(JsonObject.class);
		sharenetHomeClientKey = jo.getString("ClientKey");
		SaxoClientData.sharenetHouseKey = sharenetHomeClientKey;
		
		/**
		 * 
		 * FETCH DEFAULT CLIENT KEYS- THIS WILL RETURN COUNTERPARTS AND HOUSES
		 * 
		 */
		endPoint = baseApi +"clients/?OwnerKey=" + sharenetHomeClientKey + "&IncludeSubUsers=true";
		client = ClientBuilder.newClient();
		//System.out.println("DEBUG:" +props.isDebugShowResponse() );
		if(props.isDebugShowResponse()) {
			Logger logger = Logger.getLogger(getClass().getName());
			Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
			client.register(feature);
		}
		base = client.target(endPoint);
		invBuilder = base.request(MediaType.APPLICATION_JSON);
		invHeader = invBuilder.header("Authorization", "BEARER " + token );
		invHeader.cookie(cookieName,cookieValue);
		inv = invHeader.buildGet();
		response = inv.invoke();
		if (response.getStatus() != 200 && response.getStatus() == 401) {
			   System.out.println("Token Invalid  clients/OwnerKey"  + response.getStatus());
			   Token_Invalid_Event event = new Token_Invalid_Event(this);
			   saxoClientData.fireEvent(event);
			   
			return new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_INVALID_TOKEN);  
		 }
		jo = response.readEntity(JsonObject.class);
		System.out.println(jo.toString());
		JsonParser jparser = new JsonParser();
		ArrayList<SaxoClientDataObj> clientList = jparser.parseAccount(jo);
		int totalClients = clientList.size();
		//System.out.println("ClientCount:" + totalClients);
		//HOUSES ARE SET TO FALSE BECAUSE WE DON'T KNOW IF IT'S A HOUSE YET. SO UPDATE WITH MAIN SHARENET HOMEKEY
		int cnt = updateDatabase(clientList, server, false, sharenetHomeClientKey, accountType);
		//System.out.println("SaxoClientData Count:" + totalClients);
		if(cnt == totalClients) {
			System.out.println("All SaxoClientData updated:" + cnt);
		}
		/*
		 * Iterate through list finding Counterparts/Houses
		 */
		
		for(SaxoClientDataObj cl : clientList) {
			System.out.println("Fetching Sub CounterParts For:" + cl.getClientName());
			
			int i = getAllSubCounterParts(token, cookie, server, cl.getSaxoClientKey(), accountType);
			
			while(i == -1) {	
				try{Thread.sleep(5000);}catch(Exception e) {e.printStackTrace();}
				
				if (server.equals("LIVE"))
					getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeLogin()), cookie,
							server,
						cl.getSaxoClientKey(), accountType);
				else if (server.equals("OFFSHORE"))
					getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeOFFSHORELogin()),
							cookie, server, cl.getSaxoClientKey(), accountType);

			}
				
			
			
		}
		
		
		
		
		
		SaxoClientsAllClientResponse clientResponse = new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_SUCCESSFULL); 
			return clientResponse;
		
	}
	

	private int updateDatabase(ArrayList<SaxoClientDataObj> clientList, String server, boolean isHouse, String houseId,
			String accountType) {
		System.out.println("ACCOUNT_TYPE:" + server);
		int cnt = 0;
		/*if(!isHouse || houseId.equals(sharenetHomeClientKey)) {houseId = null;}
		else {
			System.out.println("Updating House:" + houseId);
		}*/
		
		int bcode = props.getBcode();

		if (server.equals("OFFSHORE"))
			bcode = 69;
		
		for(SaxoClientDataObj c : clientList) {
			System.out.println("SettingSharenetFields for account:" + c.getSaxoClientKey() + " bcode:" + bcode);

			saxoClientData.getJDBC().setSharenetFields(c, bcode);
			System.out.println("SharenetFields Found:" + c.getSharenetLogin() + " uidn:" + c.getSharenetUidn());
			/*if(c.getSaxoUserId().equals("8918783")) {
				System.out.println("SNLOGIN:" + c.getSharenetLogin());
				System.exit(0);;
			}*/
			if(saxoClientData.getJDBC().getSaxoClientDataCount(c.getSaxoUserId(),server,props.getClientDataTableName()) == 0) {
				
			cnt = saxoClientData.getJDBC().insertSaxoClientData(c,server,houseId,props.getClientDataTableName());
			}
			else {
				if (c.getSharenetLogin() != null)
				cnt = saxoClientData.getJDBC().updateSaxoClientData(c,server,houseId,props.getClientDataTableName());
			}
		}
		
		return cnt;
	}
	
	ArrayList<ClientAccount> getAccounts(String clientKey,String token,String cookie,String server){
		ArrayList<ClientAccount> acc = new ArrayList<ClientAccount>();
		try {
		String baseApi  ;
		if(server.equals("SIM"))
			baseApi = "https://gateway.saxobank.com/sim/openapi/port/v1/";
		else
			baseApi = "https://gateway.saxobank.com/openapi/port/v1/";
		
		//String params = URLEncoder.encode("accounts/?clientkey="  +  clientKey ,"UTF-8");
		//String params = URLEncoder.encode("accounts/?clientkey=mOxlP8|-Su1wQ8z9h0orlA==","UTF-8");
		String params = "accounts/?clientkey=" + URLEncoder.encode(clientKey,"UTF-8");
		String endPoint = baseApi + params;
		
			System.out.println("Fetching Sharenet ClientKey using EndPoint:" + endPoint);
		Client client = ClientBuilder.newClient();
		//System.out.println("DEBUG:" + props.isDebugShowResponse() );
		if(props.isDebugShowResponse()) {
			Logger logger = Logger.getLogger(getClass().getName());
			Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
			client.register(feature);
		}
		WebTarget base = client.target(endPoint);
		Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
		Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token );
		int cookInt = cookie.indexOf("=");
		String cookieName = cookie.substring(0,cookInt);
		String cookieValue = cookie.substring(cookInt + 1,cookie.length() );
		invHeader.cookie(cookieName,cookieValue);
		Invocation inv = invHeader.buildGet();
		Response response = inv.invoke();
		if (response.getStatus() != 200 && response.getStatus() == 401) {
			   System.out.println("Token Invalid - getAccounts "  + response.getStatus());
			   Token_Invalid_Event event = new Token_Invalid_Event(this);
			   saxoClientData.fireEvent(event);
			   
			  
		 }
		JsonObject jo = response.readEntity(JsonObject.class);
		//System.out.println(jo.toString());
		JsonParser jparser = new JsonParser();
		acc = jparser.parseAccountDetails(jo);
		
		}catch(Exception e) {e.printStackTrace();}
		return acc;
	}

}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.logging.LoggingFeature;

import com.google.gson.Gson;



public class SaxoAllClients {
	
	SaxoClientData saxoClientData;
	
	String sharenetHomeClientKey;
	PropsSaxoCD props;
	
	
	SaxoAllClients(SaxoClientData saxoClientData,PropsSaxoCD props ){
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
		//endPoint = "https://gateway.saxobank.com/openapi/port/v1/clients/?OwnerKey=1k2JWwF%7CmtJaz74vacD3Pw==&IncludeSubUsers=true";
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
		if (ownerId.contentEquals("1k2JWwF|mtJaz74vacD3Pw==")) {
			System.out.println("JHB-RESPONSE:" + response.toString());
			System.out.println(jo);
			//System.exit(0);

		}
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
		
			//System.out.println("SaxoSubClientData Count:" + totalClients);
		
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
			System.out.println(response.getEntity().toString());

		JsonObject jo = response.readEntity(JsonObject.class);
		System.out.println("JO:" + jo.toString());
		sharenetHomeClientKey = jo.getString("ClientKey");
		// sharenetHomeClientKey = "I4gmXdPxtNIbh6v-XetomA==";
		SaxoClientData.sharenetHouseKey = sharenetHomeClientKey;
		
		/**
		 * 
		 * FETCH DEFAULT CLIENT KEYS- THIS WILL RETURN COUNTERPARTS AND HOUSES
		 * 
		 */
		endPoint = baseApi + "clients/?OwnerKey=" + sharenetHomeClientKey;
		// endPoint = baseApi + "users/?ClientKey=" + sharenetHomeClientKey +
		// "&IncludeSubUsers=true&top=2&skip=1000";

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
			System.out.println(response);

		jo = response.readEntity(JsonObject.class);
		System.out.println(jo.toString());
		try {
		BufferedWriter bw = new BufferedWriter(new FileWriter("json.txt"));
		bw.write(jo.toString());
		bw.close();
	} catch (Exception e) {
		e.printStackTrace();
	}

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
			
			/*while(i == -1) {	
				try{Thread.sleep(5000);}catch(Exception e) {e.printStackTrace();}
				
				if (server.equals("LIVE"))
					getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeLogin()), cookie,
							server,
						cl.getSaxoClientKey(), accountType);
				else if (server.equals("OFFSHORE"))
					getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeOFFSHORELogin()),
							cookie, server, cl.getSaxoClientKey(), accountType);
			
			}*/
				
			
			
		}
		
		
		System.exit(0);
		
		
		SaxoClientsAllClientResponse clientResponse = new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_SUCCESSFULL); 
			return clientResponse;
		
	}
	
	public SaxoClientsAllClientResponse getAllClientKeysBatch(String token, String cookie, String server,
		String accountType) {
	    String baseApi;

	    /**
	     * FETCH SHARENET TOP LEVEL CLIENT KEY
	     */
	    if (props.getServer().equals("SIM"))
		baseApi = "https://gateway.saxobank.com/sim/openapi/port/v1/";
	    else
		baseApi = "https://gateway.saxobank.com/openapi/port/v1/";
	    // String cookie =
	    // getStickyCookie("https://gateway.saxobank.com/sim/openapi/port/");

	    String endPoint = baseApi + "clients/me";
	    System.out.println(accountType + "-> Fetching Sharenet Top Level ClientKey using EndPoint:" + endPoint);
	    Client client = ClientBuilder.newClient();
	    // System.out.println("DEBUG:" +PropsTokenMan.isDebugShowResponse() );
	    if (props.isDebugShowResponse()) {
		Logger logger = Logger.getLogger(getClass().getName());
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		client.register(feature);
	    }
	    WebTarget base = client.target(endPoint);
	    Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
	    Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token);
	    int cookInt = cookie.indexOf("=");
	    String cookieName = cookie.substring(0, cookInt);
	    String cookieValue = cookie.substring(cookInt + 1, cookie.length());
	    invHeader.cookie(cookieName, cookieValue);
	    Invocation inv = invHeader.buildGet();
	    Response response = inv.invoke();
	    if (response.getStatus() != 200 && response.getStatus() == 401) {
		System.out.println("Token Invalid  AllClientKeysV2" + response.getStatus());
		Token_Invalid_Event event = new Token_Invalid_Event(this);
		saxoClientData.fireEvent(event);

		return new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_INVALID_TOKEN);
	    }
	    System.out.println(response.getEntity().toString());

	    JsonObject jo = response.readEntity(JsonObject.class);
	    sharenetHomeClientKey = jo.getString("ClientKey");
	    // sharenetHomeClientKey = "I4gmXdPxtNIbh6v-XetomA==";
	    SaxoClientData.sharenetHouseKey = sharenetHomeClientKey;

	    /**
	     * 
	     * FETCH DEFAULT CLIENT KEYS- THIS WILL RETURN COUNTERPARTS AND HOUSES
	     * 
	     */
	    endPoint = baseApi + "clients/?OwnerKey=" + sharenetHomeClientKey;
	    // endPoint = baseApi + "users/?ClientKey=" + sharenetHomeClientKey +
	    // "&IncludeSubUsers=true&top=2&skip=1000";

	    client = ClientBuilder.newClient();
	    // System.out.println("DEBUG:" +props.isDebugShowResponse() );
	    if (props.isDebugShowResponse()) {
		Logger logger = Logger.getLogger(getClass().getName());
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		client.register(feature);
	    }
	    base = client.target(endPoint);
	    invBuilder = base.request(MediaType.APPLICATION_JSON);
	    invHeader = invBuilder.header("Authorization", "BEARER " + token);
	    invHeader.cookie(cookieName, cookieValue);
	    inv = invHeader.buildGet();
	    response = inv.invoke();
	    if (response.getStatus() != 200 && response.getStatus() == 401) {
		System.out.println("Token Invalid  clients/OwnerKey" + response.getStatus());
		Token_Invalid_Event event = new Token_Invalid_Event(this);
		saxoClientData.fireEvent(event);

		return new SaxoClientsAllClientResponse(SaxoClientsAllClientResponse.STATUS_INVALID_TOKEN);
	    }
	    System.out.println(response);

	    jo = response.readEntity(JsonObject.class);

		System.out.println(jo.toString());
		/*try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("json.txt"));
			bw.write(jo.toString());
		
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
		*/
	    JsonParser jparser = new JsonParser();
	    ArrayList<SaxoClientDataObj> clientList = jparser.parseAccount(jo);
	    int totalClients = clientList.size();
		System.out.println("TotalClients:" + totalClients);

	    // System.out.println("ClientCount:" + totalClients);
	    // HOUSES ARE SET TO FALSE BECAUSE WE DON'T KNOW IF IT'S A HOUSE YET. SO UPDATE
	    // WITH MAIN SHARENET HOMEKEY
	    int cnt = updateDatabase(clientList, server, false, sharenetHomeClientKey, accountType);
	    // System.out.println("SaxoClientData Count:" + totalClients);
	    if (cnt == totalClients) {
		System.out.println("All SaxoClientData updated:" + cnt);
	    }
		//System.exit(0);

	    /*
	     * Iterate through list finding Counterparts/Houses
	     */

	    for (SaxoClientDataObj cl : clientList) {
			if (cl.getClientName().contentEquals("Donald Frederick Prior")) {
			System.out.println("Fetching Sub CounterParts For:" + cl.getClientName() + " AccountId:" + cl.getDefaultAccountId());
			//System.exit(0);
			}

		int i = getAllSubCounterParts(token, cookie, server, cl.getSaxoClientKey(), accountType);

		while (i == -1) {
		    try {
			Thread.sleep(5000);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		
		    if (server.equals("LIVE"))
			getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeLogin()), cookie,
				server, cl.getSaxoClientKey(), accountType);
		    else if (server.equals("OFFSHORE"))
			getAllSubCounterParts(saxoClientData.getJDBC().getToken(props.getSharenetHomeOFFSHORELogin()),
				cookie, server, cl.getSaxoClientKey(), accountType);
		
	}

	    }
		//System.exit(0);
	    SaxoClientsAllClientResponse clientResponse = new SaxoClientsAllClientResponse(
		    SaxoClientsAllClientResponse.STATUS_SUCCESSFULL);
	    return clientResponse;

	}

	private int updateDatabase(ArrayList<SaxoClientDataObj> clientList, String server, boolean isHouse, String houseId, String accountType) {
		//System.out.println("ACCOUNT_TYPE:" + server);
		int cnt = 0;
		/*if(!isHouse || houseId.equals(sharenetHomeClientKey)) {houseId = null;}
		else {
			System.out.println("Updating House:" + houseId);
		}*/

		int bcode = props.getBcode();

		if (server.equals("OFFSHORE"))
			bcode = 69;

		for (SaxoClientDataObj c : clientList) {
			System.out.println("SettingSharenetFields for account:" + c.getSaxoUserId() + " bcode:" + bcode);
			if (c.getSaxoUserId().contentEquals("9012639")) {
				//System.out.println("Prior found - exiting");
				//System.exit(0);
			}


			//System.out.println("SHARENET-LIVEACCOUNT FOUND");
				List<SaxoClientDataObj> list = saxoClientData.getJDBC().setSharenetFields(c, bcode);
				for (SaxoClientDataObj o : list) {
					if (c.getSaxoUserId().contentEquals("8551699")) {
					if (saxoClientData.getJDBC().getSaxoClientDataCount(o.getSaxoUserId(), server, props.getClientDataTableName(),
							o.getDefaultAccountId(), o.getSharenetLogin()) == 0) {

						cnt = saxoClientData.getJDBC().insertSaxoClientData(o, server, houseId, props.getClientDataTableName());
					} else {
						if (o.getSharenetLogin() != null)
							cnt = saxoClientData.getJDBC().updateSaxoClientData(o, server, houseId, props.getClientDataTableName());

					}


			} else {
				if (saxoClientData.getJDBC().getSaxoClientDataCount1(o.getSaxoUserId(), server, props.getClientDataTableName(),
						o.getDefaultAccountId()) == 0) {

					cnt = saxoClientData.getJDBC().insertSaxoClientData(o, server, houseId, props.getClientDataTableName());
				} else {
					if (o.getSharenetLogin() != null)
						cnt = saxoClientData.getJDBC().updateSaxoClientData(o, server, houseId, props.getClientDataTableName());

				}

			}
		}
		if (list.size() == 0) {
			if (saxoClientData.getJDBC().getSaxoClientDataCount1(c.getSaxoUserId(), server, props.getClientDataTableName(),
					c.getDefaultAccountId()) == 0) {

				cnt = saxoClientData.getJDBC().insertSaxoClientData(c, server, houseId, props.getClientDataTableName());
			} else {
				if (c.getSharenetLogin() != null)
					cnt = saxoClientData.getJDBC().updateSaxoClientData(c, server, houseId, props.getClientDataTableName());

			}

		}

		/*if (c.getSaxoUserId().contentEquals("9318705")) {
			System.out.println("SHARENET-LIVEACCOUNT FOUND");
			System.exit(0);
		}*/
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
		
		//System.out.println("Fetching Sharenet ClientKey using EndPoint:" + endPoint);
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

	public void getAllClientsV3(String token, String cookie, String server, String accountType) {
		String baseApi = "https://gateway.saxobank.com/openapi/cs/v2/";
		String endPoint = baseApi + "clientinfo/clients/search/?includeSubUsers=true";
		Client client = ClientBuilder.newClient();
		// System.out.println("DEBUG:" +PropsTokenMan.isDebugShowResponse() );
		if (props.isDebugShowResponse()) {
			Logger logger = Logger.getLogger(getClass().getName());
			Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
			client.register(feature);
		}
		WebTarget base = client.target(endPoint);
		Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
		Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token);
		int cookInt = cookie.indexOf("=");
		String cookieName = cookie.substring(0, cookInt);
		String cookieValue = cookie.substring(cookInt + 1, cookie.length());
		invHeader.cookie(cookieName, cookieValue);

		String accountToLink = "\"8064491\"";

		String json = "{ \"AccountId\": \"68400/TRADSHS\", \"AccountKey\": \"I4gmXdPxtNIbh6v-XetomA==\", \"ClientId\": \"8249456\", \"ClientKey\": \"NxuCl4a1PmfI0LkXANLRdQ==\",  \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"8249456\" }";
		String json2 = "{ \"AccountId\": \"68400/TRADSHS\",    \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"8249456\" }";
		String json3 = "{     \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"10459269\" }";
		String json4 = "{     \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": " + accountToLink
				+ " }";

		// String json = "{ \"ClientId\": \"8249456\" " + "\"FieldGroups\": [
		// \"Accounts\" ], \"Keywords\": \"\" }";
		System.out.println("JSON:" + json4);

		// System.exit(0);

		Invocation in = invHeader.buildPost(Entity.entity(json4, MediaType.APPLICATION_JSON));
		Response s = in.invoke();
		String jsonResponse = s.readEntity(String.class);
		System.out.println(jsonResponse);
		
		AccountResponse resp = new Gson().fromJson(jsonResponse, AccountResponse.class);

		ArrayList<SaxoClientDataObj> dbList = new ArrayList<SaxoClientDataObj>();

		System.out.println("Count:" + resp.getCount());
		List<AccountSummary> acc = resp.getData();
		for (AccountSummary as : acc) {
			SaxoClientDataObj cd = new SaxoClientDataObj();
			cd.setClientName(as.getName());
			cd.setDefaultAccountId(as.getDefaultAccountId());
			cd.setDefaultAccountKey(as.getDefaultAccountKey());
			cd.setHouse(false);
			cd.setSaxoClientKey(as.getClientKey());
			cd.setSaxoUserId(as.getClientId());
			cd.setHouseId("NxuCl4a1PmfI0LkXANLRdQ==");
			cd.setDefaultCurrency("ZAR");
			cd.setLegalAssetTypes(as.getLegalAssetTypes());
			dbList.add(cd);
			// cd.getDefaultCurrency(as.get

			System.out.println(as.getClientKey());
		}
		updateDatabase(dbList, server, false, "NxuCl4a1PmfI0LkXANLRdQ==", "LIVE");

	}

	public void getOneClient(String token, String cookie, String server, String accountType) {
	    String baseApi = "https://gateway.saxobank.com/openapi/cs/v2/";
	    String endPoint = baseApi + "clientinfo/clients/search/?includeSubUsers=true";
	    Client client = ClientBuilder.newClient();
	    // System.out.println("DEBUG:" +PropsTokenMan.isDebugShowResponse() );
	    if (props.isDebugShowResponse()) {
		Logger logger = Logger.getLogger(getClass().getName());
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		client.register(feature);
	    }
	    WebTarget base = client.target(endPoint);
	    Invocation.Builder invBuilder = base.request(MediaType.APPLICATION_JSON);
	    Invocation.Builder invHeader = invBuilder.header("Authorization", "BEARER " + token);
	    int cookInt = cookie.indexOf("=");
	    String cookieName = cookie.substring(0, cookInt);
	    String cookieValue = cookie.substring(cookInt + 1, cookie.length());
	    invHeader.cookie(cookieName, cookieValue);

		String accountToLink = "\"9012639\"";

		// String json = "{ \"AccountId\": \"68400/TRADSHS\", \"AccountKey\": \"I4gmXdPxtNIbh6v-XetomA==\", \"ClientId\": \"8249456\", \"ClientKey\": \"NxuCl4a1PmfI0LkXANLRdQ==\",  \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"8249456\" }";
		// String json2 = "{ \"AccountId\": \"68400/TRADSHS\",    \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"8249456\" }";
		// String json3 = "{     \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": \"10459269\" }";
	    String json4 = "{     \"FieldGroups\": [ \"Accounts\" ], \"Keywords\": \"\", \"UserId\": " + accountToLink
		    + " }";

	    // String json = "{ \"ClientId\": \"8249456\" " + "\"FieldGroups\": [
	    // \"Accounts\" ], \"Keywords\": \"\" }";
	    // System.out.println("JSON:" + json4);

	    // System.exit(0);

	    Invocation in = invHeader.buildPost(Entity.entity(json4, MediaType.APPLICATION_JSON));
	    Response s = in.invoke();
	    String jsonResponse = s.readEntity(String.class);
	    System.out.println(jsonResponse);

	    AccountResponse resp = new Gson().fromJson(jsonResponse, AccountResponse.class);

	    ArrayList<SaxoClientDataObj> dbList = new ArrayList<SaxoClientDataObj>();

	    System.out.println("Count:" + resp.getCount());
	    List<AccountSummary> acc = resp.getData();
	    for (AccountSummary as : acc) {
		SaxoClientDataObj cd = new SaxoClientDataObj();
		cd.setClientName(as.getName());
		cd.setDefaultAccountId(as.getDefaultAccountId());
		cd.setDefaultAccountKey(as.getDefaultAccountKey());
		cd.setHouse(false);
		cd.setSaxoClientKey(as.getClientKey());
		cd.setSaxoUserId(as.getClientId());
		cd.setHouseId("NxuCl4a1PmfI0LkXANLRdQ==");
		cd.setDefaultCurrency("ZAR");
		cd.setLegalAssetTypes(as.getLegalAssetTypes());
		dbList.add(cd);
		// cd.getDefaultCurrency(as.get

		System.out.println(as.getClientKey());
	    }
	    updateDatabase(dbList, server, false, "NxuCl4a1PmfI0LkXANLRdQ==", "LIVE");

	}

}

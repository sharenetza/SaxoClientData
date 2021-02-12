import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SaxoClientDataJDBC {

	
	

	private Connection conn;
	private PreparedStatement psCookie;
	private PreparedStatement psFetchSaxoClient;
	private PreparedStatement psFetchSharenetHouseId;
	private PreparedStatement psAccountCount;
	private PreparedStatement psSaxoClientDataInsert;
	private PreparedStatement psSaxoClientSharenetDetails;
	private PreparedStatement psSaxoClientDataCount;
	private PreparedStatement psSaxoClientDataUpdate;
	
	
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
public String getToken(String login) {
		
		String token = null;
		String sql = "SELECT access_token FROM trade.saxo_token@uu3 WHERE login = ? ";
		try {
		PreparedStatement ps = conn.prepareStatement(sql);
			
		ps.setString(1, login);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			token = rs.getString(1);
		}
		
		rs.close();
		ps.close();
		}catch(Exception e) {e.printStackTrace();}
		return token;
	}


	public String getDefaultCurrency(SaxoClientDataObj client, String server, String houseId, String tableName) {

		String currency = null;

		String sql = "SELECT currency FROM trade." + tableName
				+ " WHERE saxo_userid = ? AND server = ?  AND (sn_login = ? OR sn_login is null) and currency is not null";
		try {

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, client.getSaxoUserId());
			ps.setString(2, server);
			ps.setString(3, client.getSharenetLogin());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				currency = rs.getString(1);
			}
			rs.close();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return currency;
	}


public int updateSaxoClientData(SaxoClientDataObj client,String server,String houseId,String tableName) {
	//if(houseId != null &&houseId.length() > 0)
	if (client.getSharenetLogin().contentEquals("kgordon01"))
			System.out.println("Updating Client:" + client.getClientName() + " houseId:" + houseId + " saxoId:"
					+ client.getSaxoUserId() + " snusername:" + client.getSharenetLogin() + " table:" + tableName
					+ " server:" + server + " accountId:" + client.getDefaultAccountId() + " Currency:" + client.getDefaultCurrency());

	//client.print();
	int cnt = 0;
	String sql = "UPDATE trade." + tableName  + "  SET sn_login = ?, saxo_userid = ?, saxo_name = ?,"
			+ " saxo_client_key = ?,last_update = sysdate ,  account_id = ?, account_key = ?,currency = ?, houseid = ? , legal_asset_type = ?"
			+ "WHERE saxo_userid = ? AND server = ? AND account_id = ? AND (sn_login = ? OR sn_login is null)";
	try {
		
		if(psSaxoClientDataUpdate == null )
			psSaxoClientDataUpdate = conn.prepareStatement(sql);
		
		psSaxoClientDataUpdate.setString(1, client.getSharenetLogin());
		psSaxoClientDataUpdate.setString(2, client.getSaxoUserId());
		psSaxoClientDataUpdate.setString(3, client.getClientName());
		psSaxoClientDataUpdate.setString(4, client.getSaxoClientKey());
		psSaxoClientDataUpdate.setString(5, client.getDefaultAccountId());
		psSaxoClientDataUpdate.setString(6, client.getDefaultAccountKey());
		String currency = client.getDefaultCurrency();
		if (currency == null)
			currency = client.getAccountCurrency();
		//getDefaultCurrency(client, server, houseId, tableName);
		if (currency != null)
			psSaxoClientDataUpdate.setString(7, currency);
		else
			psSaxoClientDataUpdate.setString(7, client.getDefaultCurrency());
		psSaxoClientDataUpdate.setString(8, houseId);
		psSaxoClientDataUpdate.setString(9, client.getLegalAssetTypes());
		psSaxoClientDataUpdate.setString(10, client.getSaxoUserId());
		psSaxoClientDataUpdate.setString(11, server);
		psSaxoClientDataUpdate.setString(12, client.getDefaultAccountId());
		psSaxoClientDataUpdate.setString(13, client.getSharenetLogin());
		
		
		cnt = psSaxoClientDataUpdate.executeUpdate();
		conn.commit();
		
	}catch(Exception e) {e.printStackTrace();}
	
	return cnt;
}

public  String getCookie(String group) {
	
	String cookie = null;
	String sql = "SELECT cookie FROM trade.saxo_cookies WHERE service_group = ?";
	try {
		if(psCookie == null)
			psCookie = conn.prepareStatement(sql);
		psCookie.setString(1, group);
		
		ResultSet rs = psCookie.executeQuery();
		while(rs.next()) {
			cookie = rs.getString("COOKIE");
		}
		rs.close();
		
	}catch(Exception e) {e.printStackTrace();}
	
	return cookie;
}

public List<SaxoClientDataObj> setSharenetFields(SaxoClientDataObj client, int bcode) {

	List<SaxoClientDataObj> list = new ArrayList<SaxoClientDataObj>();

	//System.out.println("SharenetFields: BCODE:" + bcode + " Nacc:" + client.getSaxoUserId());
	String sql = "SELECT login,uidn FROM trade.user_accs WHERE bcode = ? AND nacc = ? ";
	/*if(client.getSaxoUserId().equals("8918783")) {
		System.out.println(sql + " " + client.getSaxoUserId() + " " + bcode);
	}*/
	
	try {
		if(psSaxoClientSharenetDetails == null)
			psSaxoClientSharenetDetails = conn.prepareStatement(sql);
		psSaxoClientSharenetDetails.setInt(1, bcode);
		psSaxoClientSharenetDetails.setString(2, client.getSaxoUserId());
		ResultSet rs = psSaxoClientSharenetDetails.executeQuery();
		while(rs.next()) {
			SaxoClientDataObj o = client.clone();
			o.setSharenetLogin(rs.getString("LOGIN"));
			//System.out.println("Setting LOGIN: " + rs.getString("LOGIN") + " saxoid:" + client.getSaxoUserId() + " bcode:" + bcode + " uidn:"
			//		+ rs.getLong("UIDN"));
			o.setSharenetUidn(rs.getLong("UIDN"));
			list.add(o);
			if (client.getSaxoUserId().contentEquals("8551699"))
				System.out.println("MultiACC:" + rs.getString("LOGIN"));
			
		}
		rs.close();
	}catch(Exception e) {e.printStackTrace();}
	
	/*if(client.getSaxoUserId().equals("8918783")) {
		System.out.println("FOUND :" + client.getSharenetLogin() ); client.print();
		//System.exit(0);
	}*/
		
		
	
	return list;
}


public int updateSaxoHouse(String ownerId,String saxoId,String tableName) {
	
	System.out.println("House-> OwnerId:" + ownerId + " SaxoId:" + saxoId);
	int i = 0;
	String sql = "UPDATE trade." + tableName + " SET houseid = ? , house = 1 WHERE saxo_userid = ? ";
	try {
		PreparedStatement ps = conn.prepareStatement(sql);
		
		ps.setString(1, ownerId);
		ps.setString(2, saxoId);
		i = ps.executeUpdate();
		conn.commit();
		ps.close();
		
	}catch(Exception e) {e.printStackTrace();}
	System.out.println("House-> OwnerId:" + ownerId + " SaxoId:" + saxoId + " Updated:" + i);
	return i;
}

public int insertSaxoClientData(SaxoClientDataObj client,String server,String ownerId,String tableName) {
	
	int cnt = 0;
	String sql = "INSERT INTO trade." + tableName + " (sn_login,saxo_userid,saxo_name,saxo_client_key,sn_client_uidn,cdate,last_update,server,"
			+ "account_id,account_key,currency,houseid,legal_asset_type) " + "VALUES(?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
	try {
		if(psSaxoClientDataInsert == null)
			psSaxoClientDataInsert = conn.prepareStatement(sql);
		psSaxoClientDataInsert.setString(1, client.getSharenetLogin());
		psSaxoClientDataInsert.setString(2,client.getSaxoUserId());
		System.out.println("InsertingName:" + client.getClientName() + " " + client.getSaxoClientKey() + " Login:" + client.getSharenetLogin());
		//psSaxoClientDataInsert.setString(3, (new String(client.getClientName().getBytes() , Charset.forName("WE8ISO8859P1"))));
		
		psSaxoClientDataInsert.setString(3, (client.getClientName()));
		psSaxoClientDataInsert.setString(4, client.getSaxoClientKey());
		psSaxoClientDataInsert.setLong(5, client.getSharenetUidn());
		psSaxoClientDataInsert.setString(6, server);
		psSaxoClientDataInsert.setString(7, client.getDefaultAccountId());
		psSaxoClientDataInsert.setString(8, client.getDefaultAccountKey());
		psSaxoClientDataInsert.setString(9, client.getDefaultCurrency());
		psSaxoClientDataInsert.setString(10, ownerId);
		psSaxoClientDataInsert.setString(11, client.getLegalAssetTypes());
		System.out.println("Insert SaxoClientData:" + client.getLegalAssetTypes());
		
		

		cnt = psSaxoClientDataInsert.executeUpdate();
		conn.commit();
		
		
	}catch(Exception e) {e.printStackTrace();}
	return cnt;
	
}

public int getAccountCount(ClientAccount account, String tableName) {

	int cnt = 0;
	String sql = "SELECT count(*) FROM trade. " + tableName + " WHERE saxo_userid = ? AND account_id = ? ";
	try {
		if(psAccountCount == null)
			psAccountCount = conn.prepareStatement(sql);
		psAccountCount.setString(1, account.getClientId());
		psAccountCount.setString(2, account.getAccountId());
		ResultSet rs = psAccountCount.executeQuery();
		while(rs.next()) {
			cnt = rs.getInt(1);
		}
		rs.close();
		
	}catch(Exception e) {e.printStackTrace();}
	/*System.out.println("Getting AccountID Count: " + account.getAccountId() + " SaxoUserId:" + account.getClientId()
			+ " Count:" + cnt);*/
	return cnt;
}

public int getSaxoClientDataCount(String saxoUserId, String server, String tableName, String accountId, String sharenetUsername) {
	
	int cnt = 0;
	String sql = "SELECT COUNT(*) FROM trade." + tableName + " WHERE saxo_userid = ? AND server = ? AND account_id = ? AND sn_login = ?";
	try (PreparedStatement ps = conn.prepareStatement(sql)) {
		
		ps.setString(1, saxoUserId);
		ps.setString(2, server);
		ps.setString(3, accountId);
		ps.setString(4, sharenetUsername);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			cnt = rs.getInt(1);
		}
		rs.close();
	}catch(Exception e) {e.printStackTrace();}

	return cnt;
}

public int getSaxoClientDataCount1(String saxoUserId, String server, String tableName, String accountId) {

	int cnt = 0;
	String sql = "SELECT COUNT(*) FROM trade." + tableName + " WHERE saxo_userid = ? AND server = ? AND account_id = ? ";
	try {
		if (psSaxoClientDataCount == null)
			psSaxoClientDataCount = conn.prepareStatement(sql);

		psSaxoClientDataCount.setString(1, saxoUserId);
		psSaxoClientDataCount.setString(2, server);
		psSaxoClientDataCount.setString(3, accountId);
		ResultSet rs = psSaxoClientDataCount.executeQuery();
		while (rs.next()) {
			cnt = rs.getInt(1);
		}
		rs.close();
	} catch (Exception e) {
		e.printStackTrace();
	}

	return cnt;
}

public String getSharenetHouseID(String saxoUserId,String server,String tableName) {
	
	String sharenetHouseId = null;
	String sql = "SELECT saxo_userid FROM trade." + tableName + " WHERE sn_login = ? AND server = ?";
	try {
		if(psFetchSharenetHouseId == null){
			psFetchSharenetHouseId = conn.prepareStatement(sql);
			psFetchSharenetHouseId.setString(1, saxoUserId);	
			psFetchSharenetHouseId.setString(2, server);
			ResultSet rs = psFetchSharenetHouseId.executeQuery();
			while(rs.next()) {
				sharenetHouseId = rs.getString(1);
				
			}
			rs.close();
		}
		
		
	}catch(Exception e) {e.printStackTrace();}
	
	
	return sharenetHouseId;
}


	public ArrayList<SaxoClientDataObj> getAllCounterparts(String server,String tableName){
	
		System.out.println("Fetching ALL Counterparts");
		ArrayList<SaxoClientDataObj> allCounterpartList = new ArrayList<SaxoClientDataObj>();
		String sql = "SELECT * FROM trade." + tableName + " WHERE server = ?  and saxo_userid=9012639";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				
				SaxoClientDataObj client = new SaxoClientDataObj();
				client.setSharenetLogin(rs.getString("SN_LOGIN"));
				client.setSaxoUserId(rs.getString("SAXO_USERID"));
				client.setSaxoName(rs.getString("SAXO_NAME"));
				client.setClientName(rs.getString("SAXO_NAME"));
				client.setSaxoClientKey(rs.getString("SAXO_CLIENT_KEY"));
				client.setSharenetUidn(rs.getLong("SN_CLIENT_UIDN"));
				if(rs.getInt("house") == 1){
					client.setHouse(true);
					client.setHouseId(SaxoClientData.sharenetHouseKey);
				}
				else {
					client.setHouse(false);
					client.setHouseId(rs.getString("HOUSEID"));
				}
				allCounterpartList.add(client);
			}
			
			
			
		}catch(Exception e) {e.printStackTrace();}
		return allCounterpartList;
	
	}



public SaxoClientDataObj getClient(String saxoUserId,String server,String tableName) {
	
	System.out.println("Fetching Client: SaxoId:" + saxoUserId + " Server:" + server);
	SaxoClientDataObj client = new SaxoClientDataObj();
	String sql = "SELECT * FROM trade." + tableName + " WHERE saxo_userid = ? AND server = ?";
	try {
		if(psFetchSaxoClient == null)
			psFetchSaxoClient = conn.prepareStatement(sql);
		psFetchSaxoClient.setString(1,saxoUserId);
		psFetchSaxoClient.setString(2, server);
		ResultSet rs = psFetchSaxoClient.executeQuery();
		while(rs.next()) {
			
			client.setSharenetLogin(rs.getString("SN_LOGIN"));
			client.setSaxoUserId(saxoUserId);
			client.setSaxoName(rs.getString("SAXO_NAME"));
			client.setClientName(rs.getString("SAXO_NAME"));
			client.setSaxoClientKey(rs.getString("SAXO_CLIENT_KEY"));
			client.setSharenetUidn(rs.getLong("SN_CLIENT_UIDN"));
			if(rs.getInt("house") == 1){
				client.setHouse(true);
			}
			else {
				client.setHouse(false);
				client.setHouseId(SaxoClientData.sharenetHouseKey);
			}
			client.setHouseId(rs.getString("HOUSEID"));
			
			
			
			
			
		}
		
		
		
	}catch(Exception e) {e.printStackTrace();}
	
	return client;
	
}
}

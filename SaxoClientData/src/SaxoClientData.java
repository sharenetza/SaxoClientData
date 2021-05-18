import java.util.ArrayList;
import java.util.Date;

import za.co.sharenet.snbasic.Oracle;
import za.co.sharenet.snbasic.PropsSN;

public class SaxoClientData implements EventsInterface{
	
	private SaxoClientDataJDBC jdbc;
	private final double version = 1.3;
	PropsSaxoCD props;
	public static ArrayList<EventsInterface> listeners = new ArrayList<EventsInterface>();
	public static String sharenetHouseKey;
	
	SaxoClientData() {
		System.out.println("#############################");
		System.out.println("# SAXO CLIENT DATA  v" + version  + " - " + new Date().toString());
		
		System.out.println("#############################");
		new PropsSN();
		props = new PropsSaxoCD();
		props.loadExtendedProps();
		System.out.println("#### MODE = " + props.getServer() + " ####");
		new Oracle();
		jdbc = new SaxoClientDataJDBC();
		jdbc.setConnection(Oracle.getConnection());
	}
	
	public void addListener(EventsInterface ei){
		
		listeners.add(ei);
	}

	public void getAllClientKeysBatch(String login, String accountType) {
	    SaxoAllClients allClients = new SaxoAllClients(this, props);
	    String cookie = jdbc.getCookie("port");
	    allClients.getAllClientKeysBatch(jdbc.getToken(login), cookie, accountType, login);
		getAllAccounts(login, accountType);
	}

	public void getAllClients2(String login, String accountType) {
		SaxoAllClients allClients = new SaxoAllClients(this, props);
		String cookie = jdbc.getCookie("cs");
		allClients.getAllClientsV3(jdbc.getToken(login), cookie, accountType, login);
	}
	
	public void getAllClients(String login,String accountType) {
		SaxoAllClients allClients = new SaxoAllClients(this,props);
		String cookie = jdbc.getCookie("port");
		allClients.getAllClientKeysV2(jdbc.getToken(login),cookie,accountType,login);
	}
	public SaxoClientDataJDBC getJDBC() {return jdbc;}
	
	public void fireEvent(Object eventClass) {
		
		for ( int j = 0; j < SaxoClientData.listeners.size(); j++ ) {
			EventsInterface mel = (EventsInterface) SaxoClientData.listeners.get(j);
			
			if(mel != null){
				if(eventClass instanceof Token_Invalid_Event){
					Token_Invalid_Event event = new Token_Invalid_Event(this);
	    		       mel.Token_Invalid_Event(event);
	    		      }
			}
		}
		
	}

	@Override
	public void Token_Invalid_Event(Token_Invalid_Event event) {
		System.out.println("Invalid Token Event Fired!");
		
		
		
	}
	
	public void getAllAccounts(String login, String server) {
		ArrayList<SaxoClientDataObj> counterPartsList = getJDBC().getAllCounterparts(server,
				props.getClientDataTableName());
		System.out.println("Loaded " + counterPartsList.size() + " CounterParts");
		System.out.println("Fetching All Accounts");
		//Instant start = Instant.now();
		Timer.start();
		SaxoAllClients allClients = new SaxoAllClients(this,props);
		String cookie = jdbc.getCookie("port");
		int cnt = 0;
		for(SaxoClientDataObj counterpart : counterPartsList) {
			if (counterpart.getSaxoUserId().contentEquals("13131940")) {
				System.out.println("Prior 1");
			}
			ArrayList<ClientAccount> accountList = allClients.getAccounts(counterpart.getSaxoClientKey(),
					jdbc.getToken(login), cookie, server);
			if (counterpart.getSaxoUserId().contentEquals("9012639")) {
				System.out.println("Prior 2");
			}

			if (accountList != null && accountList.size() > 1) {
				if (counterpart.getSaxoUserId().contentEquals("9012639")) {
					System.out.println("Prior 3");
					//System.exit(0);
				}
				System.out.println(accountList.size() + " accounts found for " + counterpart.getSaxoUserId() + " " + counterpart.getClientName() );
				for(ClientAccount account : accountList) {
					if(account.getAccountId().contains("ERROR") ||
							account.getAccountId().contains("TRAD") ||
							account.getAccountId().contains("COMM") ||
							account.getAccountId().contains("INT") ) continue;
					else {

						//System.out.println("Valid Multiple Account Found:" + account.getAccountId());
						counterpart.setDefaultAccountId(account.getAccountId());
						counterpart.setDefaultAccountKey(account.getAccountKey());
						counterpart.setAccountCurrency(account.getCurrency());
						if (counterpart.getSaxoDefaultAcccount().contentEquals(account.getAccountId())) {
							counterpart.setDefaultAcc(true);
							
						}

						if (getJDBC().getAccountCount(account, props.getClientDataTableName()) == 0) {
							System.out.println("Inserting account:" + counterpart.getDefaultAccountId() + " House:" +counterpart.getHouseId() );
							getJDBC().insertSaxoClientData(counterpart, server, counterpart.getHouseId(),
									props.getClientDataTableName());
						}
						else {
							if (counterpart.getSharenetLogin() != null && counterpart.getSharenetLogin().contentEquals("kgordon01"))
								System.out.println("Updating Account: " + counterpart.getSaxoUserId() + counterpart.getDefaultAccountId() + " "
										+ counterpart.getDefaultCurrency());
							if (counterpart.getSharenetLogin() != null)
								getJDBC().updateSaxoClientData(counterpart, server, counterpart.getHouseId(),
									props.getClientDataTableName());
						}
					}
				}
			}
			if(cnt % 20 == 0 && cnt > 0) {
				System.out.println("Read " + cnt + " Counterparts");
			}
			cnt ++;
		}
		//Instant end = Instant.now();
		Timer.stop();
		Timer.print();
		/*Duration between = java.time.Duration.between(start, end);
		System.out.println( between ); // PT1.001S
		System.out.format("%dD, %02d:%02d:%02d.%04d \n", between.toDays(),
		        between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis());*/
	}
	
	public void loadClientData(String login,String accountType) {
		System.out.println("Loading Data for:" + login);
		Timer.start();
		// getAllClients(login, accountType);
		// getAllClients2(login, accountType);
		getAllClientKeysBatch(login, accountType);
		Timer.stop();
		//Timer.print();
		/*
		 * System.out.println("DONE - COUNTERPARTS"); getAllAccounts(login,accountType);
		 * System.out.println("DONE - ACCOUNTS");
		 */
		
		
		
	}
	
	public void getOneClient(SaxoClientData sx, String login, String accountType, String server, String account) {
		SaxoClientData clientData = new SaxoClientData();
		SaxoAllClients oneClient = new SaxoAllClients(sx, clientData.props);
		 String cookie = jdbc.getCookie("port");
		 String token = jdbc.getToken(login);
		 
			oneClient.getOneClient(token, cookie, server, accountType, account);
	}

	public static void main(String[] arg) {
		
		//ONE CLIENT
		SaxoClientData sx = new SaxoClientData();
		//sx.getOneClient(sx, sx.props.getSharenetHomeLogin(), "LIVE", "LIVE", "13501491");
		//sx.getOneClient(sx, sx.props.getSharenetHomeOFFSHORELogin(), "OFFSHORE", "OFFSHORE", "13131940");
		//System.exit(0);

	    // LOCAL//

		System.out.println("Fetching LOCAL accounts"); // SaxoClientData
		SaxoClientData clientData = new SaxoClientData(); //
		clientData.loadClientData(clientData.props.getSharenetHomeLogin(), "LIVE");
		System.out.println("Fetching LOCAL accounts - DONE");

		// OFFSHORE

		System.out.println("Fetching OFFSHORE accounts");
			SaxoClientData clientDataOffshore = new SaxoClientData();
			clientDataOffshore = new SaxoClientData();
			clientDataOffshore.loadClientData(clientDataOffshore.props.getSharenetHomeOFFSHORELogin(), "OFFSHORE");
			System.out.println("Fetching OFFSHORE accounts - DONE");

	}
}


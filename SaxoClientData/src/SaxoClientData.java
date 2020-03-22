import java.util.ArrayList;
import java.util.Date;

public class SaxoClientData implements EventsInterface{
	
	private SaxoClientDataJDBC jdbc;
	private final double  version = 1.1; 
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
	
	public void getAllClients(String login,String accountType) {
		SaxoClientsAllClients allClients = new SaxoClientsAllClients(this,props);
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
		SaxoClientsAllClients allClients = new SaxoClientsAllClients(this,props);
		String cookie = jdbc.getCookie("port");
		int cnt = 0;
		for(SaxoClientDataObj counterpart : counterPartsList) {
			ArrayList<ClientAccount> accountList = allClients.getAccounts(counterpart.getSaxoClientKey(),
					jdbc.getToken(login), cookie, server);
			if(accountList.size() > 1 ) {
				System.out.println(accountList.size() + " accounts found for " + counterpart.getSaxoUserId() + " " + counterpart.getClientName() );
				for(ClientAccount account : accountList) {
					if(account.getAccountId().contains("ERROR") ||
							account.getAccountId().contains("TRAD") ||
							account.getAccountId().contains("COMM") ||
							account.getAccountId().contains("INT") ) continue;
					else {
						System.out.println("Valid Multiple Account Found:" + account.getAccountId());
						counterpart.setDefaultAccountId(account.getAccountId());
						counterpart.setDefaultAccountKey(account.getAccountKey());
						if(getJDBC().getAccountCount(account,props.getClientDataTableName()) == 0) {
							System.out.println("Inserting account:" + counterpart.getDefaultAccountId() + " House:" +counterpart.getHouseId() );
							getJDBC().insertSaxoClientData(counterpart, server, counterpart.getHouseId(),
									props.getClientDataTableName());
						}
						else {
							System.out.println("Updating Account: " + counterpart.getSaxoUserId());
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
	
	public void loadCLientData(String login,String accountType) {
		System.out.println("Loading Data for:" + login);
		Timer.start();
		getAllClients(login,accountType);
		Timer.stop();
		Timer.print();
		System.out.println("DONE - COUNTERPARTS");
		getAllAccounts(login,accountType);
		System.out.println("DONE - ACCOUNTS");
		
		
		
	}
	
	public static void main(String[] arg) {
		
		

		//LOCAL//

		System.out.println("Fetching LOCAL accounts");
		SaxoClientData clientData = new SaxoClientData();
		clientData.loadCLientData(clientData.props.getSharenetHomeLogin(), "LIVE");
		System.out.println("Fetching LOCAL accounts - DONE");

		//OFFSHORE
		System.out.println("Fetching OFFSHORE accounts");
		SaxoClientData clientDataOffshore = new SaxoClientData();
		clientDataOffshore.loadCLientData(clientDataOffshore.props.getSharenetHomeOFFSHORELogin(), "OFFSHORE");
		System.out.println("Fetching OFFSHORE accounts - DONE");
	}
}


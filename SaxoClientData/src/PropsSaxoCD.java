import java.io.FileInputStream;
import java.util.Properties;

public class PropsSaxoCD {
	
	
	private String sharenetHomeLogin;
	private int bcode;
	private String server;
	private static Properties props;
	private  boolean debugShowResponse;
	private String sharenetHomeSaxoId;
	private String clientDataTableName;
	private String sharenetHomeOFFSHORESaxoId;
	private String sharenetHomeOFFSHORELogin;
	private String sharenetHomeGLOBALSaxoId;
	private String sharenetHomeGLOBALLogin;
	
	
	void loadExtendedProps() {
		props = new Properties();
		loadConfFile();
	}

	
	private void loadConfFile() {

		try {
			//System.out.println("Opening Config File.");
		
			props.load(new FileInputStream("conf"));
			server = props.getProperty("server");
			sharenetHomeLogin = props.getProperty("sharenetHomeLogin");
			debugShowResponse = (Boolean.parseBoolean(props.getProperty("debugShowResponse")));
			sharenetHomeSaxoId = props.getProperty("sharenetHomeSaxoId");
			bcode = Integer.parseInt(props.getProperty("bcode"));
			clientDataTableName = props.getProperty("clientDataTableName");
			sharenetHomeOFFSHORELogin = props.getProperty("sharenetHomeOFFSHORESaxoLogin");
			sharenetHomeOFFSHORESaxoId = props.getProperty("sharenetHomeOFFSHORESaxoId");
			sharenetHomeGLOBALLogin = props.getProperty("sharenetHomeGLOBALSaxoLogin");
			sharenetHomeGLOBALSaxoId = props.getProperty("sharenetHomeGLOBALSaxoId");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSharenetHomeGLOBALSaxoId() {
		return sharenetHomeGLOBALSaxoId;
	}

	public String getSharenetHomeGLOBALLogin() {
		return sharenetHomeGLOBALLogin;
	}
	public String getSharenetHomeOFFSHORESaxoId() {return sharenetHomeOFFSHORESaxoId;}
	public String getSharenetHomeOFFSHORELogin() {return sharenetHomeOFFSHORELogin;}
	
	public String getSharenetHomeSaxoId() {return sharenetHomeSaxoId;}

	public String getSharenetHomeLogin() {
		return sharenetHomeLogin;
	}


	public int getBcode() {
		return bcode;
	}


	public String getServer() {
		return server;
	}


	public static Properties getProps() {
		return props;
	}


	public boolean isDebugShowResponse() {
		return debugShowResponse;
	}


	public String getClientDataTableName() {
		return clientDataTableName;
	}


	public void setClientDataTableName(String clientDataTableName) {
		this.clientDataTableName = clientDataTableName;
	}


	
}

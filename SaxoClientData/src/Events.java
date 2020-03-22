
public class Events  extends java.util.EventObject{

	public Events(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

}

class Token_Invalid_Event extends java.util.EventObject{
	
	private static final long serialVersionUID = 1L;

	public Token_Invalid_Event(Object source) {
		super(source);
	}
	
	
}



class REST_Remove_Token_Event extends java.util.EventObject{

	public REST_Remove_Token_Event(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
}
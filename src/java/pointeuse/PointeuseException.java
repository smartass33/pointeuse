package pointeuse;

@SuppressWarnings("serial")
public class PointeuseException extends Exception{

	public PointeuseException(String message){
		super(message);
	}
	
	
	public PointeuseException(Throwable cause){
		super(cause);
	}
}

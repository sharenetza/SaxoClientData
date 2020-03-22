import java.time.Duration;
import java.time.Instant;

public class Timer {
	private static Instant start;
	private static Instant end;
	
	
	public static void start() {
		 start = Instant.now();
		
	}
	public static void stop() {
		end = Instant.now();
	}
	
	public static void print() {
		Duration between = java.time.Duration.between(start, end);
		System.out.println( between ); // PT1.001S
		System.out.format("%dD, %02d:%02d:%02d.%04d \n", between.toDays(),
		        between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis());
		//RESET
		start = null;
		end = null;
		
	}
}

package carpool.common;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class DebugLog {

	public static void d(String message){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + message);
	}

	public static void d(int number){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + number);
	}

	public static void d_Chinese(String message){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.println(message);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}

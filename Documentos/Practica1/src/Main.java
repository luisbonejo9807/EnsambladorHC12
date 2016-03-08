import java.io.*;

public class Main{

	/**
	 * @param args
	 */
	
	public static void main(String args[])throws IOException{
		
		String direccion="hOlA.Gh";
		System.out.println(direccion.matches("[A-Za-z]+[.]?[A-Za-z]*"));
		direccion="h9JJD_L__";
		System.out.println(direccion.matches("[A-Za-z]+[0-9A-Za-z_]*"));

	}


}

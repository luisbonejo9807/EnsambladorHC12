import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.table.DefaultTableModel;


public class Errores {
	
	String[] errores;
    FileWriter fw;
    PrintWriter pw;
	
	Errores(){
		
		errores = new String [4];
		
		errores[0] = new String ("1. ERROR: lexicografico en Etiqueta");
		errores[1] = new String ("2. ERROR: lexicografico en CODOP");
		errores[2] = new String ("1. ERROR: lexicografico en Operando");
		errores[3] = new String ("1. ERROR: de linea");

	}
	
	void crearArchivo(String direccion){
		try {
			System.out.println("entro a errores");
			direccion = direccion.replace(".asm", ".err");
	        fw = new FileWriter(direccion, false);
	        pw = new PrintWriter(fw);
	        pw.println(String.format("%-8s  %-12s  %s","LINEA","ERROR","DESCRIPCION DEL ERROR"));
	        pw.println("..........................................................");
	        System.out.println("salio de errores");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	}
	
	public void resultado(DefaultTableModel a, int no_error, int linea) {
		Object[] fila = new Object[3];
		fila[0]=linea;
		fila[1]=no_error;
		fila[2]=errores[no_error];

		a.addRow(fila);
		pw.println(String.format(String.format("%-8s  %-12s  %s",fila[0],fila[1],fila[2])));
		
	}
	
	public void cerrarArchivo() throws IOException{
	    pw.close();
	    fw.close();
	}

}

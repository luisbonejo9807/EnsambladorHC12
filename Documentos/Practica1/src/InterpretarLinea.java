import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.table.DefaultTableModel;


public class InterpretarLinea {

	//atributos
	
	String[] interpretacion; 
	Automata[] analizador;
	Errores err;
    String fileNam;
    FileWriter fw;
    PrintWriter pw;
    int error;

	
	InterpretarLinea(){
		
		// automata completo
		analizador = new Automata[3];
		analizador[0] = new AutomataEtiqueta();
		analizador[1] = new AutomataCodop();
		analizador[2] = new AutomataOperandos();
		
	
		interpretacion = new String[3];
		
	}
	
	void crearArchivo(String direccion){
		try {
			err = new Errores();
			err.crearArchivo(direccion);
			direccion = direccion.replace(".asm", ".inst");
	        fw = new FileWriter(direccion, false);
	        pw = new PrintWriter(fw);
	        pw.println(String.format("%-8s  %-10s  %-10s  %s","LINEA","ETIQUETA","CODOP","OPERANDO"));
	        pw.println("..........................................................");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		
	}
	
	boolean analizarLinea(DefaultTableModel a,DefaultTableModel errores,String linea,int contador){
		
		interpretacion[0] = new String("NULL");
		interpretacion[1] = new String("NULL");
		interpretacion[2] = new String("NULL");
			
		if (!linea.isEmpty()&&linea.charAt(0)!=';')
		{
			linea=eliminarComentarios(linea);
			StringTokenizer tokens = new StringTokenizer(linea);
			int menu=tokens.countTokens();
			Character primero = linea.charAt(0);
			System.out.println(contador + "\t cantidad tokens :" +menu+"\n");
			error=-1;
			
			switch(menu){
			
				case 0:
						return false; 
				case 1:
					if (primero.compareTo(' ') ==  0 || primero.compareTo('\t') == 0){
						error = analisis(linea,1,2);
					}
					break;
				case 2:
					if (primero.compareTo(' ') !=  0 && primero.compareTo('\t') != 0){
						error=analisis(linea,0,2);

					}
					
					else{
						error = analisis(linea,1,3);
					}	
					
					break;
					
				case 3:
					if (primero.compareTo(' ') !=  0 && primero.compareTo('\t') != 0){
						error = analisis(linea,0,3);
					}
					break;
					
				
				default: error = 3;
			
			}// fin del switch
			if(error!=-1){
				err.resultado(errores, error, contador);
				return false;
			}
			
			else if(interpretacion[0].compareTo("NULL")==0 && interpretacion[1].compareTo("NULL") == 0 && interpretacion[2].compareTo("NULL") == 0){
				err.resultado(errores, 3, contador);
				return false;			
			}
			else
				return true;
		}// fin del if
		
		else 
			return false;
		
	}
	
	String eliminarComentarios(String linea){
		
		StringTokenizer sin_comentarios = new StringTokenizer(linea, ";");
		return sin_comentarios.nextToken();
	}
	
	int analisis(String linea, int inicio, int fin){
		
		StringTokenizer tokens = new StringTokenizer(linea);
		String token;
	
		for (int aux = inicio ; aux < fin ; aux++ ){
			token = tokens.nextToken();
			interpretacion[aux] = analizador[aux].analizar(token);
			if (interpretacion[aux].compareTo("NULL")==0){
				return aux;
			}
				
		}
		return -1;
	}
	

	public void resultado(DefaultTableModel a, int contador) {

			Object[] fila = new Object[4];
			fila[0]=contador; 
			fila[1]=interpretacion[0];
			fila[2]=interpretacion[1];
			fila[3]=interpretacion[2];
			a.addRow(fila);
			pw.println(String.format("%-8s  %-10s  %-10s  %s",contador,interpretacion[0],interpretacion[1],interpretacion[2]));
		
	}
	
	
	public void cerrarArchivo() throws IOException{
	    pw.close();
	    fw.close();
	    err.cerrarArchivo();
	}
	
	boolean validarEND(){
		if (interpretacion[1].compareToIgnoreCase("END")==0){
			return true;
		}
		else return false;
	}
	
}

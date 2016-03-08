import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class AnalizarArchivo {

	RandomAccessFile archivo = null;
    FileReader fr;
    BufferedReader br;
    InterpretarLinea interprete;
    String direccion;

    AnalizarArchivo(){
    	
    	archivo = null;
        fr = null;
        br = null;
        interprete = new InterpretarLinea();
    }
    
    void abrirArchivo(File direccion) throws IOException{
    	this.direccion=direccion.getAbsolutePath(); 
    	archivo = new RandomAccessFile(direccion, "r");	   
    }// fin del metodo
    
    void leerArchivo(JTextArea a)throws IOException{
    	String linea;
    	int contador=0;
    	while((linea=archivo.readLine())!=null){
    		contador++;
    		a.append(contador + "\t"+linea+"\n");
    	}
    		
	       	  
    	
    	archivo.seek(0);
  
    }
    
    void analizar(DefaultTableModel a, DefaultTableModel errores)throws IOException{
    	String linea;
    	int contador=0;
    	if(verificarArchivo())
    	{
    		interprete.crearArchivo(direccion);
    		while((linea=archivo.readLine())!=null){
        		contador++;
        		
        		if(interprete.analizarLinea(a,errores,linea,contador)&&!interprete.validarEND()){
        			interprete.resultado(a, contador);
        		}
        		if(interprete.validarEND()){
        			interprete.resultado(a, contador);
        			break;
        		}
        	}
    		interprete.cerrarArchivo();
        	archivo.seek(0);
        	JOptionPane.showMessageDialog(null,"Archivo creado en: "+ direccion.replace(".asm", ".ints"));
    	}
    	
    	else{
    		JOptionPane.showMessageDialog(null,"Abrir primero algun archivo");
    	}
  
    	
    }
    
    boolean verificarArchivo(){
    	if(archivo != null)return true;
    	else return false;
    }
    
    
}

/**
 * @(#)SepararPalabras.java
 *
 *
 * @author 
 * @version 1.00 2014/2/25
 */
import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SepararPalabras {



public void Separar()
		{
			try{
				Scanner leer = new Scanner(new File("P1ASM.txt"));
				String lineaLeer;
				String Etiqueta="",Codop="",Operando="";
				boolean ContieneEnd=false;
				
				while (leer.hasNextLine()){
					lineaLeer = leer.nextLine(); 
						if(lineaLeer.charAt(0)==';'){
							System.out.println("Comentario");
							System.out.println("_______________________________________________________________");
						}else{
							StringTokenizer palabras=new StringTokenizer(lineaLeer);
        					System.out.println(".......................................................");
        					
        					//Caso Etiqueta-codop-operando y Caso Etiqueta-Codop
        					if(lineaLeer.charAt(0)!=' '&&lineaLeer.charAt(0)!='\t'){
        							Etiqueta=palabras.nextToken();
        						}
        					else{//caso codop-operando y caso codop
        							Etiqueta=null;
        						}
        						
        					if(palabras.hasMoreTokens()){//Obteniendo Codop
        						Codop=palabras.nextToken();
        					}
        					else{
        						Codop=null;
        					}
        							
							if(palabras.hasMoreTokens()){//Obteniendo Operando
									while(palabras.hasMoreTokens()){
										Operando+=palabras.nextToken()+" ";
									}	
							}
							else{
								Operando=null;
							}
							
							//Se imprime la etiqueta y posteriormente se evalua
        					System.out.println("Etiqueta= "+Etiqueta);
        					if(Etiqueta!=null){
        						ValidarEtiqueta(Etiqueta);
        					}
        					//Se imprime el Codop y posteriormente se evalua
        					System.out.println("Codop= "+Codop);
        					
        					if(Codop!=null){
        						ValidarCodop(Codop);
        						//Se identifica si es el Codop END 
        						if(Codop.toUpperCase()=="END"){
        							Operando="";
        							Codop="";
        							Etiqueta="";
        							ContieneEnd=true;
        							break;
        						}
        					}else{
        						System.out.println("ERROR:Siempre debe de haber un código de operación.");	
        					}
        					
        					System.out.println("Operando= "+Operando);
        					System.out.println("----------------------------------------------------------------");
        					Operando="";
        					Codop="";
        					Etiqueta="";
						}	
					}
					if(!ContieneEnd){
								System.out.println("ERROR: No se encontró el END.");
							}
				}
			catch(Exception e)
			{
				System.out.println("Ocurrió un error  " + e.getMessage());
			}
		}
		
	public static void ValidarEtiqueta(String EtiquetaAEvaluar){
			Pattern pat = Pattern.compile("[^a-zA-Z].*+");
			Pattern pat2 = Pattern.compile(".*[^\\w+].*");
     		Matcher NoIniciaConLetras = pat.matcher(EtiquetaAEvaluar);
     		Matcher ContieneOtrosCaracteres = pat2.matcher(EtiquetaAEvaluar);
			if(EtiquetaAEvaluar.length()>8){
					System.out.println("ERROR:La longitud máxima de una etiqueta es de 8 caracteres");
			}else if(NoIniciaConLetras.matches()){
        		 System.out.println("ERROR:La etiqueta debe de iniciar con letra");
			}
			else if(ContieneOtrosCaracteres.matches()){
				System.out.println("ERROR:Los caracteres válidos en las etiquetas son letras,dígitos(0..9) y el guión bajo");
			}
		}
		
	public static boolean ContieneMasDeUnPunto(String Cadena){
			int n=Cadena.length();
			int numeroDePuntos=0;
			for(int i=0;i<n;i++){
					if(Cadena.charAt(i)=='.'){
						numeroDePuntos++;
						}
					if(numeroDePuntos>1)
						{
							return true; 		
					}
				}
			return false;
		}
		
	public static void ValidarCodop(String CodopAEvaluar){
			Pattern pat = Pattern.compile("[^a-zA-Z].*+");
			Pattern pat3 = Pattern.compile(".*[^a-zA-z.+].*");
     		Matcher NoIniciaConLetras = pat.matcher(CodopAEvaluar);
     		Matcher ContieneOtrosCaracteres = pat3.matcher(CodopAEvaluar);
     		
			if(CodopAEvaluar.length()>5){
				 System.out.println("ERROR:La longitud máxima de un código de operación es de 5 caracteres");
			}else if(NoIniciaConLetras.matches()){
        		 System.out.println("ERROR:Los codigos de operación deben de iniciar con letra");
			}
			else if(ContieneOtrosCaracteres.matches()){
        		 System.out.println("ERROR:Los caracteres válidos de los códigos de operación son letra y el caracter punto");
			}
			else if(ContieneMasDeUnPunto(CodopAEvaluar)){
				System.out.println("ERROR:Los codigos de operación no deben de tener más de un punto");
			}
		}
		
	

    public static void main(String[] args) {
    	
    	// TODO, add your application code
    	SepararPalabras ini=new SepararPalabras();
    	ini.Separar();
    }
}
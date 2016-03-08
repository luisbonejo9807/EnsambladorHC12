
public class AutomataOperandos extends Automata{
	

	/*Atributos
	 *			       SIMBOLOS DE ENTRADA
	 *             	 | L | D | C | 	*/  
	int[][] estados={{ 1 , 1 , 1 },		//O		ESTADOS
					 { 1 , 1 , 1 },};	//1		
	
	//Metodos
	String analizar(String cadena){
		
		int estado = 0;
		
		for(char token : cadena.toCharArray()){
			if(Character.isLetter(token)){
				estado = estados[estado][0];
			}
			
			
			else if(Character.isDigit(token)){
				estado = estados[estado][1];
			}
			
			else {
				estado = estados[estado][2];
			}
		}// fin del for
		
		if (estado == 2)return "NULL";
		else return cadena;
		
	}
}
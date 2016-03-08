
class AutomataEtiqueta extends Automata{
	
	/*Atributos
	 *			       SIMBOLOS DE ENTRADA
	 *             	 | L | _ | N | OTRO_CARACTER	*/  
	int[][] estados={{ 1 , 8 , 8 , 9 },		//O		E
					 { 2 , 2 , 2 , 9 },		//1		S
					 { 3 , 3 , 3 , 9 },		//2		T
					 { 4 , 4 , 4 , 9 },		//3		A
					 { 5 , 5 , 5 , 9 },		//4		D
					 { 6 , 6 , 6 , 9 },		//5		O
					 { 7 , 7 , 7 , 9 },		//6		S
					 { 8 , 8 , 8 , 9 },		//7
					 { 9 , 9 , 9 , 9 },		//8
					 { 9 , 9 , 9 , 9 },};	//9
	
	//Metodos
	
	String analizar(String cadena){
		
		/*int estado = 0;
		
		for(char token : cadena.toCharArray()){
			if(Character.isLetter(token)){
				estado = estados[estado][0];
			}
			
			else if(token == '_'){
				estado = estados[estado][1];
			}
			
			else if(Character.isDigit(token)){
				estado = estados[estado][2];
			}
			
			else {
				estado = estados[estado][3];
			}
			
			
		}
		
		if (estado == 9)return "NULL";
		else return cadena;*/
		if(cadena.matches("[A-Za-z]+([0-9]*[A-Za-z]*[_]*)*")){
			if(cadena.length()<9)
				return cadena;
			else return "NULL";
		}
		else return "NULL";
		
	}
	
}


public class AutomataCodop extends Automata{
	
	
	/*Atributos
	 *			       SIMBOLOS DE ENTRADA
	 *             	 | L | . | N | OTRO_CARACTER	*/  
	int[][] estados={{ 1 , 9 , 9 },		//O		E
					 { 2 , 6 , 9 },		//1		S
					 { 3 , 7 , 9 },		//2		T
					 { 4 , 8 , 9 },		//3		A
					 { 5 , 5 , 9 },		//4		D
					 { 9 , 9 , 9 },		//5		O
					 { 7 , 9 , 9 },		//6		S
					 { 8 , 9 , 9 },		//7
					 { 5 , 9 , 9 },		//8
					 { 9 , 9 , 9 },};	//9
	
	//Metodos
	
	String analizar(String cadena){
		
		/*int estado = 0;
		
		for(char token : cadena.toCharArray()){
			if(Character.isLetter(token)){
				estado = estados[estado][0];
			}
			
			else if(token == '.'){
				estado = estados[estado][1];
			}
			
			else {
				estado = estados[estado][2];
			}
		}
		
		if (estado == 9)return "NULL";
		else return cadena;*/
		
		if(cadena.matches("[A-Za-z]+[.]?[A-Za-z]*")){
			if(cadena.length()<6)
				return cadena;
			else return "NULL";
		}
		else return "NULL";
		
	}

}

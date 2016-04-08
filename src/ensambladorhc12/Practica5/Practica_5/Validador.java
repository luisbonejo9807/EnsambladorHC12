
package ensambladorhc12;

public final class Validador {
    
    private Validador() {
    }
    
    //  Directivas de CONSTANTES:     DW, DB, DC.W, DC.B, FCB, FDB, FCC 
    
    public static String validaCONSTANTEde1Byte(String operando, int LINE_NUMBER){
        //  de 1 byte  ---------------- 
        //  DB: define byte.
        //  DC.B. define constant byte
        //  FCB: full constant byte
        
        //  La variable OPERANDO puede tomar valores entre 0 a 255, en 
        //  cualquier base numérica. Cada una de ellas se puede
        //  representar con mayúsculas o minúsculas. 
        //  Incrementan en 1 el CONTLOC. Si el OPERANDO es NULL se
        //  debe de marcar un error. Cualquier otra representación 
        //  es un error.
        
        return  "CONS1|"+Validador.validaDirecto(operando, LINE_NUMBER);        
    }


    
    public static String validaCONSTANTEde2Byte(String operando, int LINE_NUMBER){
        //  de 2 bytes --------------
        //  DW: define word
        //  DC.W: define constant word
        //  FDB: full double byte
        //  La variable OPERANDO puede tomar valores entre 0 a 
        //  65535, en cualquier base numérica. Cada una de ellas se
        //  puede representar con mayúsculas o minúsculas. 
        
        //  Incrementan en 2 el CONTLOC. Si el OPERANDO es NULL se 
        //  debe de marcar un error. Cualquier otra representación
        //  es un error.
        
        return  "CONS2|"+Validador.validaBase(operando, LINE_NUMBER, 0, 65535, "Constante de 2 Bytes");
    }
    
    
    public static String validaCONSTANTEdeCaracteres(String operando, int LINE_NUMBER){
        //  De caracteres  --------
        //  FCC: full constant carácter
        
        //  Su valor se representa entre comillas dobles (“valor”) 
        //  en la variable OPERANDO. El valor se representa con 
        //  cualquier carácter del código ASCII (incluso, por ejemplo,
        //  el espacio en blanco). 
        
        //  Incrementa el CONTLOC en su equivalente de “longitud del
        //  operando” – 2.
        int inicio = (char)operando.charAt(0);
        int fin = (char)operando.charAt(operando.length()-1);
        
        int caracter1 = (int)'“';
        int caracter2 = (int)'”';
        
        if(operando.length()<=2)
            return "ERROR el operando Constante de Caracteres debe tener mas de 3 caracteres por las comillas";
        else if((operando.startsWith("\"") && operando.endsWith("\""))|| (caracter1 - inicio == 8073 && caracter2 - fin == 8073))
            return "CAR|"+operando;
        return  "ERROR al operando le faltan comillas o caracteres dentro de estas";
    }
    

    public static String validaCONSTANTEdeReserva1byte(String operando, int LINE_NUMBER){
        //    Directivas de RESERVA DE ESPACIO EN MEMORIA:
        //    DS, DS.B, DS.W, RMB, RMW 
        //    De 1 byte en 1 byte  --------- 
        //    DS: define space
        //    DS.B: define space.byte
        //    RMB: reserve memory byte
        //    La variable OPERANDO puede tomar valores entre 0 a 
        //    65535, en cualquier base numérica. Cada una de ellas 
        //    se puede representar con mayúsculas o minúsculas.  
        //    Incrementan el CONTLOC de la siguiente manera:  
        
        //    el valor numérico del OPERANDO se multiplica por 1 y
        //    se suma al CONTLOC. Si el OPERANDO es NULL se debe
        //    de marcar un error. Cualquier otra representación es
        //    un error.
        return  "RES1|"+Validador.validaBase(operando, LINE_NUMBER, 0, 65535, "Constante de Reserva de 1 Bytes");
    }
    
    
    public static String validaCONSTANTEdeReserva2byte(String operando, int LINE_NUMBER){
        //  De 2 en 2 bytes   --------------
        //  DS.W: define space word
        //  RMW: reserve memory word
        //  La variable OPERANDO puede tomar valores entre 0 a 
        //  65535, en cualquier base numérica. Cada una de ellas 
        //  se puede representar con mayúsculas o minúsculas.  
        //  Incrementan el CONTLOC de la siguiente manera:
        
        //  el valor numérico del OPERANDO se multiplica por 2 y se  
        //  suma al CONTLOC. Si el OPERANDO es NULL se debe de   
        //  marcar un error. Cualquier otra representación es un   
        //  error.
        
        //if(codop.matches("^(?i)(DS\\.W|RMW)$"))
        
        return  "RES2|"+Validador.validaBase(operando, LINE_NUMBER, 0, 65535, "Constante de Reserva de 2 Bytes");
    }

    public static String validaTipoDirectivaConstante(String codop, String operando, int LINE_NUMBER) {
        if(codop.toUpperCase().matches("^(DS\\.W|RMW)$"))
            return "RES2";
            //return this.validaCONSTANTEdeReserva2byte(operando, LINE_NUMBER);
        else if(codop.toUpperCase().matches("^(DS|DS\\.B|DS\\.W|RMB|RMW)$"))
            return "RES1";
            //return this.validaCONSTANTEdeReserva1byte(operando, LINE_NUMBER);
        else if(codop.equals("FCC"))
            return "CONSC";
            //return this.validaCONSTANTEdeCaracteres(operando, LINE_NUMBER);
        else if(codop.toUpperCase().matches("^(DW|DC\\.W|FDB)$"))
            return "CONS2B";
            //return this.validaCONSTANTEde2Byte(operando, LINE_NUMBER);
        else if(codop.toUpperCase().matches("^(DB|DC\\.B|FCB)$"))
            return "CONS1B";
            //return this.validaCONSTANTEde1Byte(operando, LINE_NUMBER);
        else 
            return "ERROR NO SE ENCONTRO EL CODOP DE OPERACIÓN en el archivo TABOP.txt";
    }
    
    public static String analizaOperando(String operando, String codop, int LINE_NUMBER) {
        if(operando.toUpperCase().equals("NULL"))
            return "ERROR el "+codop+" no debe tener como operando NULL";
        else if((""+operando.charAt(0)).matches("^(\\$|@|%|[0-9])$"))
            return  Procesador.convertirADecimalString(operando);
        else
            return "ERROR el "+codop+" debe tener un operando decimal en cualquier base valida";
    }
    
        
    
    public static String validarLinea(String ASMTXT_FOLDER_NAME, int LINE_NUMBER, String[] palabras) {
        StringBuilder z = new StringBuilder();
        String codop = Validador.validarCODOP(palabras[0]);
        String operando;
        if(codop.contains("\tERROR "))
            z.append(codop);
        else
            z.append(codop);
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append(" ");
            if(palabras.length<=2)
                operando = palabras[1];
            else
                operando = s.toString().substring(0, s.toString().length()-1);
            z.append(operando);
            z.append("\t").append(Tabop.buscarEnTABOP(ASMTXT_FOLDER_NAME, palabras[0], operando, LINE_NUMBER));
        }
        else
        {
            operando = "null";
            z.append(operando);
            z.append("\t").append(Tabop.buscarEnTABOP(ASMTXT_FOLDER_NAME, palabras[0], operando, LINE_NUMBER));
        }
        return z.toString();
    }

    public static String validarCODOP(String palabra ) {
        StringBuilder s = new StringBuilder(palabra+"\t");
        if(palabra.length()>5)
            s.append("\n\tERROR Tamaño de CODOP mayor a 5").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
            s.append("\n\tERROR Los CODOPS no pueden empezar con carácteres que no sean alfabéticos").toString();
        else if(!palabra.matches("^[^.]*.[^.]*$"))
            s.append("\n\tERROR No se puede usar más de 2 veces el caracter \".\" en los CODOPS").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z\\.]+$"))
               ) 
            s.append("\n\tERROR Existe algún carácter inválido en el CODOP").toString();
        //s.append(this.validaTipoCODOP(palabra));
        return  s.toString();
    }
    
    public static String validarETIQUETA(String palabra ) {
        if(palabra.length()>8)
            return "ERROR Tamaño de ETIQUETA mayor a 8";
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
            return "ERROR Las ETIQUETAS no pueden empezar con carácteres que no sean alfabéticos";
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z0-9_]{0,7}$"))
               )
             return "ERROR Existe algún carácter inválido en la ETIQUETA";
        return palabra;
    }
    
    public static String validaTipoOPERANDO(String palabra, int LINE_NUMBER, int bytes_pendientes) { 
        String s;
        if(palabra.startsWith("#")) 
            return "IMM|"+Validador.validaInmediato(palabra, LINE_NUMBER, bytes_pendientes);
        else if(palabra.startsWith("["))
        {
            if(palabra.contains(","))
            {
                if(palabra.length()>2)
                {
                    if(palabra.substring(1).split(",")[0].matches("^[0-9]*$"))
                        return "[IDX2]|"+Validador.validaIndizadoIndirectoDe16Bits(palabra, LINE_NUMBER);
                    else
                        return "[D,IDX]|"+Validador.validaIndizadoDeAcumuladorIndirecto(palabra, LINE_NUMBER);
                }
                return "ERROR el operando Indizado Indirecto o de Acumulador deben incluir registros validos";
            }
            else
                return "ERROR el operando Indizado Indirecto o de Acumulador deben de incluir , ";
        }
        else if(palabra.contains(","))
        {
            String[] g = palabra.split(",");
            if(g.length ==0)
                return "ERROR el operando Indizado de 5 bits le faltan registros";
            s = g[0];
            if(s.length()>0)
            {
                if(palabra.contains("B")|| palabra.contains("b") || palabra.contains("D") || palabra.contains("d") || palabra.contains("A") || palabra.contains("a"))
                    return "IDX|"+Validador.validaIndizadoDeAcumulador(palabra, LINE_NUMBER);
                    /*s = this.validaIndizadoDeAcumulador(palabra, LINE_NUMBER);
                    if(s.contains("ERROR"))
                        return s;*/
                else if(palabra.contains("+") || palabra.endsWith("-") || palabra.matches("^.*,-.*$"))
                    return "IDX|"+Validador.validaIndizadoDeAutoPrePostDecrementoIncremento(palabra, LINE_NUMBER);
                else if(Validador.isDECIMAL(g[0]))
                {
                    long num = Long.parseLong(s);
                    if((num>= -256 && num <= -17) || (num>= 16 && num<=255))
                        return "IDX1|"+Validador.validaIndizadoDe9Bits(palabra, LINE_NUMBER);
                    else if((num>= 256 && num <= 65535))
                        return  "IDX2|"+Validador.validaIndizadoDe16Bits(palabra, LINE_NUMBER);
                    else
                        return "IDX|"+Validador.validaIndizadoDe5Bits(palabra, LINE_NUMBER);
                }
                return "ERROR algun registro del operando Indizado es invalido";
            }
            else
            {
                return "IDX|"+Validador.validaIndizadoDe5Bits(palabra, LINE_NUMBER);
                //indizado de 5 bits
            }
        }
        else
        {
            if((""+palabra.charAt(0)).matches("^[a-zA-Z]*$"))
            {
                s = Validador.validarETIQUETA(palabra);
                if(s.contains("ERROR"))
                    return s;
                return "EXT|"+Validador.validaExtendido(palabra, LINE_NUMBER);
            }
            s = Procesador.convertirADecimalString(palabra);
            if(s.contains("ERROR"))
                return s;
            long l = Long.parseLong(s);
            if(l >= 0 && l <= 255)
                return "DIR|"+Validador.validaDirecto(palabra, LINE_NUMBER);
            else if(l >= 0 && l <= 65535)
                return "EXT|"+Validador.validaExtendido(palabra, LINE_NUMBER);
            else if(l < 0)
                return "DIR|ERROR el valor decimal es menor minimo para el modo de direccionamiento Decimal y Extendido";
            return "DIR|ERROR el valor decimal es mayor al rango maximo para el modo de direccionamiento Decimal y Extendido";        
        }
        //return this.writeError(LINE_NUMBER, "ERROR el Operando no tiene posible modo direccionamiento valido");
    }
    
    
    public static  String validaBase(String palabra, int LINE_NUMBER, int min, int max, String tipo_direccionamiento) {
        String s = Procesador.quitaCeros(palabra);
        if(Validador.isDECIMAL(palabra))
            return Validador.validaDECIMAL(s, LINE_NUMBER, min, max, tipo_direccionamiento);
        else if(palabra.length() >1)
        {
            if(palabra.startsWith("$"))
                return Validador.validaHEX(s, LINE_NUMBER, min, max, tipo_direccionamiento);
            else if(palabra.startsWith("%"))
                return Validador.validaBINARY(s, LINE_NUMBER, min, max, tipo_direccionamiento);
            else if(palabra.startsWith("@"))
                return Validador.validaOCTAL(s, LINE_NUMBER, min, max, tipo_direccionamiento);
        }
        return "ERROR el Operando "+ tipo_direccionamiento+" no tiene caracteres despues del caracter de base";
    }
    
    public static  String validaInmediato(String palabra, int LINE_NUMBER, int bytes_pendientes) {
        //Modo Inmediato: Utiliza las cuatro bases numéricas en sus operandos, inicia el operando con el símbolo de #, 
        //de acuerdo al TABOP hay inmediatos de 8 bits, son los aquellos en los que hace falta calcular un byte y por lo tanto el operando debe de tener un valor entre 0 255. 
        //También hay inmediatos de 16 bits, son aquellos en los que hace falta calcular dos bytes y por lo tanto el operando debe de tener un valor ente 0 a 65535.
        //Los valores numéricos pueden utilizar ceros a la izquierda.
        //Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$FFF
        //	LDAA	#55
        //	LDX	#$0234
        //	LDY	#$67
        //	LDAA	#%11
        //	LDY	#@234
        //	END
        
        //#(Directo | Tipo de 0 a 65535)
        if(palabra.length()>1)
        {
            int max;
            if(bytes_pendientes == 1)
                max = 256;
            else
                max = 65535;
            String s =  Validador.validaBase(palabra.substring(1), LINE_NUMBER, 0, max, "Inmediato");
            if(s.contains("ERROR"))
                return s;
            //return "Inmediato de "+bytes_pendientes*8+" bits, ";
            return "CONTLOC";
        }
        else
            return "ERROR el Operando Inmediato debe empezar con un caracter de base despues del #";
    }

    public static  String validaDirecto(String palabra, int LINE_NUMBER) {
        //Modo Directo: Utiliza las cuatro bases numéricas en sus operandos,
        //los valores numéricos pueden representarse con ceros a la izquierda, 
        //el operando se puede representar con valores entre 0 a 255. 
        //Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$FFF
        //	LDAA	$55
        //	LDAA	$0055
        //	LDX	%0011
        //	END
        
        //Tipo y de 0 a 255        
        String s =  Validador.validaBase(palabra, LINE_NUMBER, 0, 255, "Directo");
        if(s.contains("ERROR"))
            return s;
        //return "Directo,";
        return "CONTLOC";
    }

    public static  String validaExtendido(String palabra, int LINE_NUMBER) {
        //Modo Extendido: Utiliza las cuatro bases numéricas en sus operandos, 
        //los valores numéricos pueden representarse con ceros a la izquierda,
        //el operando se puede representar con valores entre 256 a 65535,
        // y también el operando puede estar representado por una palabra que cubra las reglas de escritura de las Etiquetas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$FFF
        //	LDAA	300
        //	LDAA	$FFFF
        //	LDAA	VALOR1
        //	END
        //Tipo  de 256 a 65535|etiqueta
        if((""+palabra.charAt(0)).matches("[a-zA-Z]*$"))
        {
                String s = Validador.validarETIQUETA(palabra);
                if(s.contains("ERROR"))
                    return s;
                return "CONTLOC";
                //return "Extendido,";
        }
        else
        {
            String s = Validador.validaBase(palabra, LINE_NUMBER, 256, 65535, "Extendido");
            if(s.contains("ERROR"))
                return s;
            return "CONTLOC";
            //return "Extendido,"; 
        }
    }

    public static  String validaIndizadoDe5Bits(String palabra, int LINE_NUMBER) {
        //Modo Indizado de 5 Bits. En el TABOP se representa con la abreviación IDX,
        // en el operando se representan valores numéricos,
        // únicamente en base DECIMAL,
        // con un rango de -16 a 15,
        // después del valor debe de haber siempre el carácter de “,” (coma) y después el nombre de un registro de computadora válido,
        // como son X, Y, SP y PC (cualquier nombre de registro diferente es un error).
        // Los nombres de los registros se pueden representar indistintamente en mayúsculas o minúsculas.
        // Existe una excepción y es cuando el operando inicia con el carácter de “,” después de la coma debe de representarse cualquier nombre de registro de computadora como los mencionados.
        // Si el operando estuviera representado por “,X” entonces se debe de interpretar como si fuera “0,X”.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$0
        //	LDAA	,X
        //	LDAA	0,X
        //	LDAA	1,sp
        //	LDAA	15,x
        //	LDAA	-1,Pc
        //	LDAA	-16,X
        //	STAB	-8,Y
        //	END
        //[Decimal de -16 a 15 , (?i)(X | Y | SP | PC) ] | ,(?i)(X | Y | SP | PC)
        
        if(palabra.startsWith(","))
        {
            String s =  Validador.validaRegistro(palabra.substring(1), "Indizado De 5Bits");
            if(s.contains("ERROR"))
                return s;
            return "CONTLOC";
            //return "Indizado de 5 bits, ";
        }
        else if(palabra.matches("^-?[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = Validador.validaDECIMAL(palabra.split(",")[0], LINE_NUMBER, -16, 15, "Indizado de 5 bits");
            if(s.contains("ERROR"))
                return s;
            return "CONTLOC";
            //return "Indizado de 5 bits,";
        }        
        return Validador.validaIndizado(palabra, LINE_NUMBER, -16, 15, 0, 0, "Indizado De 5 Bits");
    }

    public static  String validaIndizadoDe9Bits(String palabra, int LINE_NUMBER) {
        //Modo Indizado de 9 Bits. En el TABOP se representa con la abreviación IDX1 en el operando se representan valores numéricos,
        // únicamente en base DECIMAL,
        // con un rango de -256 a -17 y de 16 a 255,
        // después del valor debe de haber siempre el carácter de “,” (coma) y después el nombre de un registro de computadora válido,
        // como son X, Y, SP y PC (cualquier nombre de registro diferente es un error).
        // Los nombres de los registros se pueden representar indistintamente en mayúsculas o minúsculas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$0
        //	LDAA	255,X
        //	LDAA	34,SP
        //	LDAA	-18,pc
        //	LDAA	-256,x
        //	LDAA	-20,Y
        //	END
        //[Decimal de (-256 a -17) |(16,255)  , (?i)(X | Y | SP | PC) ]
        
        if(palabra.matches("^-?[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = Validador.validaRangoDobleDECIMAL(palabra.split(",")[0], -256, -17, 16, 255,"Indizado de 9 bits");
            if(s.toString().contains("ERROR"))
                return s.toString();
            return "CONTLOC";
            //return "Indizado de 9 bits, ";
        }        
        else
            return Validador.validaIndizado(palabra, LINE_NUMBER, -256, -17, 16, 255,"Indizado De 9 Bits");
    }
    
    public static  String validaIndizadoDe16Bits(String palabra, int LINE_NUMBER) {
        //Modo Indizado de 16 Bits. En el TABOP se representa con la abreviación IDX2 en el operando se representan valores numéricos,
        // únicamente en base DECIMAL,
        // con un rango de 256 a 65535,
        // después del valor debe de haber siempre el carácter de “,” (coma) y después el nombre de un registro de computadora válido,
        // como son X, Y, SP y PC (cualquier nombre de registro diferente es un error).
        // Los nombres de los registros se pueden representar indistintamente en mayúsculas o minúsculas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //       ORG	$0
        //	LDAA	31483,X
        //	END
        //[Decimal de 256 a 65535,(?i)(X | Y | SP | PC) ]
        if(palabra.startsWith(","))
        {
            String s = Validador.validaRegistro(palabra, "Indizado de 16 bits");
            if(s.contains("ERROR"))
                return s;
            return "CONTLOC";
            //return "Indizado de 16 bits ";
        }
        else if(palabra.matches("^-*[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = Validador.validaDECIMAL(palabra.split(",")[0].substring(1), LINE_NUMBER, 256, 65535, "Indizado de 16 bits");
            if(s.contains("ERROR"))
                return s.toString();
            return "CONTLOC";
            //return "Indizado de 16 bits,";
        }        
        else
            return Validador.validaIndizado(palabra, LINE_NUMBER, 256, 65535, 0, 0, "Indizado De 16 Bits");
    }

    public static  String validaIndizadoIndirectoDe16Bits(String palabra, int LINE_NUMBER) {
        //Modo Indizado Indirecto de 16 Bits. En el TABOP se representa con la abreviación [IDX2] en el operando se representan valores numéricos,
        // únicamente en base DECIMAL, con un rango de 0 a 65535,
        // después del valor debe de haber siempre el carácter de “,” (coma) y después el nombre de un registro de computadora válido,
        // como son X, Y, SP y PC (cualquier nombre de registro diferente es un error).
        // Los nombres de los registros se pueden representar indistintamente en mayúsculas o minúsculas.
        // Y siempre deben de existir los dos corchetes el que abre y el que cierra.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.
        //
        //		ORG	$0
        //	LDAA	[10,X]
        //	LDAA	[31483,X]
        //	END
        
        // \[Decimal de 0 a 65535,(?i)(X | Y | SP | PC)\] 
        if(palabra.matches("^\\[[0-9]*,(X|SP|PC|Y)\\]$"))
        {
            String s = Validador.validaDECIMAL(palabra.split(",")[0].substring(1), LINE_NUMBER, 0, 65535, "Indizado Indirecto de 16 bits,");
            if(s.contains("ERROR"))
                return s.toString();
            return "CONTLOC";
            //return "Indizado Indirecto de 16 bits,";
        }        
        else
            return Validador.validaIndizado(palabra.substring(1, palabra.length()-1), LINE_NUMBER, 0, 65535, 0, 0, "Indizado Indirecto De 16 Bits");
    }

    public static  String validaIndizadoDeAutoPrePostDecrementoIncremento(String palabra, int LINE_NUMBER) {
        //Modo Indizado de Auto Pre/Post Decremento/Incremento: En el TABOP se representa con la abreviación IDX,
        //en el operando se representan valores numéricos,
        //únicamente en base DECIMAL, con un rango de 1 a 8,
        //después del valor debe de haber siempre el carácter de “,” (coma) y después un signo positivo o negativo y en seguida el nombre de un registro,
        //únicamente son válidos la X, Y, SP.
        // O bien después de la coma puede haber el nombre de un registro, X, Y, SP y enseguida un signo positivo o negativo,
        // tal y como se muestra en el ejemplo.
        // Cualquier nombre de registro diferente es un error.
        // Los nombres de los registros se pueden representar indistintamente en mayúsculas o minúsculas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.
        //        ORG	$0
        //	STAA	1,-SP
        //	STAA	1,Sp-
        //	STX	2,sP+
        //	STX	2,+sp
        //	STX	5,+X
        //	STX	7,y-
        //	END
        if(palabra.matches("^[1-8],(((\\+|-)(?i)(X|SP|Y))|((?i)(X|SP|Y)(\\+|-)))$"))
        {
            String[] g = palabra.split(",");
            String s = Validador.validaDECIMAL(g[0], LINE_NUMBER, 1, 8, "Indizado De Auto Pre Post Decremento Incremento");
            if(s.contains("ERROR"))
                return s;
            StringBuilder res = new StringBuilder();
            res.append("Indizado de ");
            if(g[1].startsWith("-"))
                res.append("pre decremento");
            else if(g[1].startsWith("+"))
                res.append("pre incremento");
            else if(g[1].endsWith("+"))
                res.append("post incremento");
            else if(g[1].endsWith("-"))
                res.append("post decremento");
            return res.toString()+",";
        }
        else
        {
            String tokens[] = palabra.split(",");
            if(tokens.length == 1)
                return "ERROR el primer registro del Operando de Auto Pre Post no es valido";
            else if(tokens.length == 2)
            {
                if(tokens[0].matches("^[1-8]$"))
                {
                    String s = Validador.validaDECIMAL(tokens[0], LINE_NUMBER, 1, 8, "Indizado De Auto Pre Post Decremento Incremento");
                    if(s.contains("ERROR"))
                        return s;
                }
                else
                    return "CONTLOC";
            //return "ERROR el primer registro del Operando de Auto Pre  Post no es valido";
                if(!tokens[1].matches("^(((\\+|-)(?i)(X|SP|Y))|((?i)(X|SP|Y)(\\+|-)))$"))
                    return "CONTLOC";
            //return "ERROR el segundo registro del Operando de Auto Pre Post no es valido";
            }     
            return "ERROR el primer registro del Operando de Auto Pre Post no es valido";
        }
    }
    

    public static  String validaIndizadoDeAcumulador(String palabra, int LINE_NUMBER) {
        //Modo Indizado de Acumulador: En el TABOP se representa con la abreviación IDX,
        // en el operando se representan únicamente nombre de registros de computadora, pero en un orden particular.
        // Los primeros registros que se pueden representar antes del carácter de la coma son A, B y D.
        // Después de la coma se puede representar únicamente los registros X, Y, SP o PC.
        // En todos los casos los registros se pueden representar con letras minúsculas o mayúsculas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$0
        //	LDAA	B,X
        //	LDAA	a,X
        //	LDAA	D,x
        //	STX	b,PC
        //	STX	d,Y
        //	END
        
        if(palabra.matches("^(?i)(B|A|D),(?i)(X|PC|Y|SP)$"))
            return "CONTLOC";
            //return "Indizado de Acumulador,";
        else if(!(""+palabra.charAt(0)).matches("^(?i)(B|A|D)$"))
            return "ERROR el primer registro del Operando Indizado de Acumulador no es valido";
        else
        {
            String tokens[] = palabra.split(",");
            if(tokens.length == 1)
                return "ERROR el primer registro del Operando de Acumulador no es valido";
            else if(tokens.length == 2)
            {
                if(!tokens[0].matches("^(?i)(B|A|D)$"))
                    return "ERROR el primer registro del Operando de Acumulador no es valido";
                else if(!tokens[1].matches("^(((\\+|-)(?i)(X|SP|Y))|((?i)(X|SP|Y)(\\+|-)))$"))
                    return "ERROR el segundo registro del Operando de Acumulador no es valido";
            }     
            return  "ERROR el primer registro del Operando de Acumulador no es valido";
        }
    }

    public static  String validaIndizadoDeAcumuladorIndirecto(String palabra, int LINE_NUMBER) {
        //Modo Indizado de Acumulador Indirecto: En el TABOP se representa con la abreviación [D,IDX],
        // en el operando se representan únicamente nombre de registros de computadora , pero en un orden particular.
        // El único registro que se puede representar antes del carácter de la coma es D.
        // Después de la coma se puede representar únicamente los registros X, Y, SP o PC.
        // En todos los casos los registros se pueden representar con letras minúsculas o mayúsculas.
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //      ORG	$0
        //	STS	[D,PC]
        //	ADCA	[d,X]
        //	ADCB	[D,Sp]
        //	ADDA	[D,y]
        //	END
        //\[(?i)D,(?i)(X | Y | SP)\]
        if(palabra.matches("^\\[(?i)D,(?i)(PC|X|Y|SP)\\]$"))
            return "CONTLOC";
            //return "Indizado Indirecto de Acumulador,";
        else
        {
            if((palabra.startsWith("D") || palabra.startsWith("d"))== false)
                return "ERROR el Operando Indizado de Acumulador debe empezar por el registro D";
            else if(!palabra.contains(",") && !palabra.endsWith(","))
            {
                if(!palabra.substring(palabra.indexOf(",")-1).matches("^(?i)(PC|X|Y|SP)$"))
                    return "ERROR el segundo registro del el CODOP Indizado de Acumulador es inválido";
            }
            else 
                return "ERROR el el Operando Indizado de Acumulador debe tener una ,";        
        }
        return "ERROR el CODOP Indizado de Acumulador está en un formato incorrecto";
    }
    
     public static  String validaIndizado(String palabra, int LINE_NUMBER, int min, int max, int min2, int max2, String tipo_direccionamiento) {
        String tokens[] = palabra.split(",");
        if(tokens.length == 1)
            return "ERROR el primer registro del Operando"+ tipo_direccionamiento+" no es valido";
        else if(tokens.length == 2)
        {
            
            if(tokens[0].matches("^-?[0-9]*$"))
            {
                if(min2 == max2)
                {
                    String s = Validador.validaDECIMAL(tokens[0], LINE_NUMBER, min, max, tipo_direccionamiento);
                    if(s.contains("ERROR"))
                        return s;
                }
                else
                {
                    String s = Validador.validaRangoDobleDECIMAL(tokens[0], min, max, min2, max2, tipo_direccionamiento);
                    if(s.contains("ERROR"))
                        return s;
                }
            }
            else
                return "ERROR el primer registro del Operando"+ tipo_direccionamiento+" no es valido";
            if(!tokens[1].matches("^(?i)(X,PC,Y,SP)$"))
                return "ERROR el segundo registro del Operando"+ tipo_direccionamiento+" no es valido";
        }     
        return "ERROR el segundo registro del Operando"+ tipo_direccionamiento+" no es valido";
    }

    public static  String validaRelativoDe8y16Bits(String palabra, int bytes_pendientes, String bytes_sumados, int LINE_NUMBER) {
        //Modos relativos de 8 y 16 bits. En el TABOP se representan con la abreviación REL.		
        // El operando no puede tener valores numéricos.		
        // El operando debe de ser una palabra que cumpla con las reglas de escritura de las Etiquetas.		
        // El TABOP determina si la instrucción es de 8 o de 16 bits dependiendo de la cantidad de bytes que le correspondan
        // Marcar en pantalla un mensaje de error explícito por cada posible error.		
        //        ORG	$0
        //	BRA	UNO_1
        //	LBRA	DOS_2
        //	BRA	Tres
        //	LBRA	Et_c4
        //	END
        String s = Validador.validarETIQUETA(palabra);
        if(s.contains("ERROR"))
          return s;
        return "CONTLOC";
            //return "Relativo de "+ bytes_pendientes*8 +" bits, de "+bytes_sumados+" bytes";
    }
    
    public static  String validaHEX(String palabra, int LINE_NUMBER,int min, int max, String tipo_direccionamiento) {
        //Carácter de pesos ($) y le pueden seguir las letras, minúsculas y /o mayúsculas, 
        //A a F y los dígitos del 0 al 9.
        if(!Validador.isHEX(palabra))
           return "ERROR el operando es un hexadecimal pero tiene algún carácter inválido";
        return Validador.validaRango(palabra, min, max, 16, tipo_direccionamiento);
    }

    public static String validaOCTAL(String palabra, int LINE_NUMBER,int min, int max,  String tipo_direccionamiento) {
        //Octal, se representa con el carácter de @ y le pueden seguir los dígitos del 0 al 7.
        //^@([1-9]|1[0-8])$  del 1 al 18
        if(!Validador.isOCTAL(palabra))
           return "ERROR el operando es un octal pero tiene algún carácter inválido";
        return Validador.validaRango(palabra, min, max, 8, tipo_direccionamiento);
    }

    public static  String validaBINARY(String palabra, int LINE_NUMBER,int min, int max, String  tipo_direccionamiento) {
        //Binario, se representa con el carácter de % y le pueden seguir los dígitos 0 y 1.        
        if(!Validador.isBINARY(palabra))
           return "ERROR el operando es un binario pero tiene algún carácter inválido";
        return Validador.validaRango(palabra, min, max, 2, tipo_direccionamiento);
    }

    public static  String validaDECIMAL(String palabra, int LINE_NUMBER, int min, int max, String  tipo_direccionamiento) {
        //Decimal inicia con cualquiera de los dígitos de 0 al 9.
        if(!Validador.isDECIMAL(palabra))
           return "ERROR el operando es un decimal pero tiene algún carácter inválido";
        return Validador.validaRango(palabra, min, max, 10, tipo_direccionamiento);
    }

    public static  String validaRango(String palabra, int min, int max, int base, String  tipo_direccionamiento) {
        long l = Long.parseLong(palabra, base);
        if(l < min)
            return "ERROR el rango es menor que el minimo valido para el modo de direccionamiento "+ tipo_direccionamiento;
        else if( l > max)
            return "ERROR el rango es mayor que el maximo valido para el modo de direccionamiento "+ tipo_direccionamiento; 
        return palabra;
    }
    
    public static  String validaRangoDobleDECIMAL(String palabra, int min1, int max1, int min2, int max2, String tipo_direccionamiento) {
        if(!Validador.isDECIMAL(palabra))
            return "ERROR el operando es un decimal pero tiene algún carácter inválido "+tipo_direccionamiento;
        long l = Long.parseLong(palabra);
        if( (l >= min1 && l <= max1) || (l >= min2 && l <= max2))
            return palabra;
        else
            return "ERROR el rango es menor que el minimo valido para el modo de direccionamiento "+ tipo_direccionamiento;
    }
    
    public static  String validaRegistro(String palabra, String tipo_direccionamiento) {
        if(!palabra.matches("^(?i)(X|SP|PC|Y)$"))
            return "ERROR el Registro del Operando "+ tipo_direccionamiento+ " es invalido";
        return palabra;
    }
    
    public static  boolean isHEX(String palabra) {
        return palabra.matches("^[A-Fa-f0-9]*$");
    }

    public static  boolean isBINARY(String palabra) {
        return palabra.matches("^[0-1]*$");
    }

    public static  boolean isOCTAL(String palabra) {
        return palabra.matches("^[0-7]*$");
    }

    public static  boolean isDECIMAL(String palabra) {
        return palabra.matches("^-?[0-9]*$");
    }
    
    public static boolean isComentario(String linea) {
         return Procesador.separarEnPalabras(linea)[0].startsWith(";");
    }
    
    public static boolean hasETIQUETA(String linea ) {
        return !(""+linea.charAt(0)).matches("\\s++");
    }    
}

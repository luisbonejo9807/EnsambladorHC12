package ensambladorhc12;

public class Temporal {
    
        
    public static  String procesarTemporal(String contenidoProcesado) {
        StringBuilder s = new StringBuilder();
        String[] linea = contenidoProcesado.split("\n");
        
        boolean alreadyORG = false;
        Long CONT_LOC = null;
        //  El contador de localidades (CONTLOC). El primer valor asociado a
        //  la variable CONTLOC es el primer valor asociado a la variable 
        //  DIR_INIC. Es una variable de tipo entero, su rango es de 0 a 
        //  65535, cualquier valor asociado a esta variable y que no se 
        //  encuentre en el rango permitido es un error y se debe de mostrar
        //   en pantalla. El valor de la variable CONTLOC se incrementa en
        //   dos casos: con la cantidad de bytes de cada instrucción y con
        //   la cantidad de bytes de las directivas de tipo constante y de
        //   reserva de espacio. Algunas directivas no incrementan  el valor
        //   del CONTLOC como son ORG, END y EQU.

        //  Los valores de DIR_INIC y CONTLOC se representan en los archivos 
        //  TEMPORAL y en TABSIM, en un formato de dos bytes en base numérica 
        //  hexadecimal.
        Long DIR_INIC = null;
        //  La dirección inicial: es el valor que está asociado a la
        //  directiva ORG y está representado en la variable del OPERANDO. 
        //  Si el operando estuviera en NULL entonces se debe de marcar un
        //  error en pantalla. El valor puede estar representado en
        //  cualquier base numérica, cualquier error de representación 
        //  de base numérica se debe de mostrar como un error en pantalla.
        //  La dirección inicial se abrevia como DIR_INIC, y es una variable
        //  de tipo entero, que puede tomar valores del 0 al 65535, en caso
        //  de que el valor asociado no esté en este rango de valores se
        //  debe de mostrar un error en pantalla.
        
        for (int i = 0; i < linea.length; i++ ) 
        {   
            String res = "";  
            if(Validador.isComentario(linea[i]))
                continue;
            else if(linea[i].contains("ERROR"))
               return s.append(" se dentendra el programa\t").toString();
            else
            {   
                String[] tokens = linea[i].split("\\t");
                
                if(res.contains("ERROR"))
                    return s.append(res+" Terminando el programa").toString();
                if(CONT_LOC != null)
                {
                    if(CONT_LOC>65535 || CONT_LOC<0 )
                        return s.append("ERROR el CONT_LOC no debe ser mayor a 65535 o menor a 0 Terminando programa").toString();
                }
                if(tokens[1].toUpperCase().equals("END"))
                {
                    //  END. Indica el final del código en lenguaje ensamblador, el valor
                    //  de la variable OPERANDO debe de ser NULL, la variable ETIQUETA
                    //  puede ser NULL o diferente de NULL cualquiera de los dos casos
                    //  es válido. El END se puede representar con mayúsculas y minúsculas.
                    //  Cualquier otra representación es un error.
                    if(!tokens[2].toUpperCase().equals("NULL"))
                        CONT_LOC = CONT_LOC + Long.parseLong(Procesador.convertirADecimalString(tokens[2]));
                    
                    s.append(tokens[3].split("\\|")[0]+"\t"+Procesador.agregarCeros(CONT_LOC)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
//                    s.append(tokens[3].split("\\|")[0]+"\t"+("0000" + Long.toHexString(CONT_LOC).toUpperCase()).substring(Long.toHexString(CONT_LOC).toUpperCase().length())+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                    s.append("\nLongitud en bytes ").append(CONT_LOC-DIR_INIC).toString();
                    return s.toString();
                }
                else if(tokens[1].toUpperCase().equals("EQU"))
                {
                    String t = Validador.analizaOperando(tokens[2], "Constante de 2 bytes", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Terminando programa").toString();
                    else if(tokens[0].toUpperCase().equals("NULL"))
                        return s.append("ERROR el EQU no debe tener como etiqueta NULL Terminando programa").toString();
                    else if(Long.valueOf(t)>65535 && Long.valueOf(t)<0)
                        return s.append("ERROR el EQU Operando del EQU no debe ser mayor a 65535 o menor a 0 Terminando programa").toString();
//                    s.append(tokens[3].split("\\|")[0]+"\t"+("0000" + Long.toHexString(Long.valueOf(t)).toUpperCase()).substring(Long.toHexString(Long.valueOf(t)).toUpperCase().length())+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                    s.append(tokens[3].split("\\|")[0]+"\t"+Procesador.agregarCeros(Long.valueOf(t))+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                    continue;
                }
                else if(tokens[1].toUpperCase().equals("ORG"))
                {
                    if(alreadyORG)
                        return s.append("ERROR no pueden existir 2 ORG se dentendra el programa \t").toString();
                    alreadyORG = true;
                    //operando
                    String t = Validador.analizaOperando(tokens[2], "ORG", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Teminando programa").toString();
                    else
                    {
                        DIR_INIC = Long.parseLong(t);
                        CONT_LOC = DIR_INIC;
                    }
                    if(DIR_INIC>65535 && DIR_INIC<0)
                        return s.append("ERROR el DIR_INIC no debe ser mayor a 65535 o menor a 0 Terminando programa").toString();
//                    s.append(tokens[3].split("\\|")[0]+"\t"+("0000" + Long.toHexString(DIR_INIC).toUpperCase()).substring(Long.toHexString(DIR_INIC).toUpperCase().length())+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                    s.append(tokens[3].split("\\|")[0]+"\t"+Procesador.agregarCeros(DIR_INIC)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                    continue;
                    //Direccion inicial
                }
               
                
                if(res.contains("ERROR"))
                       return s.append(res+" Terminando el programa").toString();
//                if(tokens[3].contains("|"))
//                    s.append(tokens[3].split("\\|")[0]+"\t"+("0000" + Long.toHexString(CONT_LOC).toUpperCase()).substring(Long.toHexString(CONT_LOC).toUpperCase().length())+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                if(tokens[3].contains("|") && CONT_LOC != null)
                    s.append(tokens[3].split("\\|")[0]+"\t"+Procesador.agregarCeros(CONT_LOC)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                else if(CONT_LOC == null)
                    return s.append("ERROR el ORG solo puede ir despues de un EQU").toString();
                else
                    s.append(tokens[3]+"\t"+("0000" + Long.toHexString(CONT_LOC).toUpperCase()).substring(Long.toHexString(CONT_LOC).toUpperCase().length())+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n").toString();
                
                if(tokens[3].equals("CONS1B"))
                {
                    res = Validador.validaCONSTANTEde1Byte(tokens[2], i);
                    String t = Validador.analizaOperando(tokens[2], "Constante de 2 bytes", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Teminando programa").toString();
                    else
                        CONT_LOC = CONT_LOC+ 1;
                }
                else if(tokens[3].equals("CONS2B"))
                {
                    res = Validador.validaCONSTANTEde2Byte(tokens[2], i);
                    String t = Validador.analizaOperando(tokens[2], "Constante de 2 bytes", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Teminando programa").toString();
                    else
                        CONT_LOC  = CONT_LOC + 2;
                }
                else if(tokens[3].equals("CONSC"))
                {
                    res = Validador.validaCONSTANTEdeCaracteres(tokens[2], i);
                    CONT_LOC = CONT_LOC+ (res.substring(4).length() - 2);
                }
                else if(tokens[3].equals("RES1"))
                {
                    res = Validador.validaCONSTANTEdeReserva1byte(tokens[2], i);
                    String t = Validador.analizaOperando(tokens[2], "Reserva de 1 byte", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Teminando programa").toString();
                    else
                        CONT_LOC = CONT_LOC+ Long.parseLong(t);
                }
                else if(tokens[3].equals("RES2"))
                {
                    res = Validador.validaCONSTANTEdeReserva2byte(tokens[2], i);
                    String t = Validador.analizaOperando(tokens[2], "Reserva de 1 byte", i);
                    if(t.contains("ERROR"))
                        return s.append(t).append(" Teminando programa").toString();
                    else
                        CONT_LOC = CONT_LOC + (Long.parseLong(t)*2);
                }
                else if(tokens[2].charAt(0)== '#')
                    CONT_LOC = CONT_LOC + Long.valueOf(""+tokens[3].split("\\|")[1].charAt(0));
                else if(tokens[2].toUpperCase().equals("NULL"))
                    CONT_LOC = CONT_LOC + Long.valueOf(""+tokens[3].split("\\|")[1].charAt(0));
                else if ((""+tokens[2].charAt(0)).matches("^($|@|%|[0-9])$"))
                    CONT_LOC = CONT_LOC + Long.valueOf(""+tokens[3].split("\\|")[1].charAt(0));
                else if(tokens[3].matches("^(RES2|RES1|CONSC|CONS2B|CONS1B)$"))
                    tokens[3] = "CONTLOC";
                
            }
        }
        return s.append("\nLongitud en bytes "+(CONT_LOC - DIR_INIC)).toString();
        //Calcular e imprimir al final del despligue la LONGITUD EN
        //BYTES del código a ensamblar, de acuerdo a la siguiente 
        //fórmula: Longitud = CONTLOC – DIR_INIC
    }

 


    
    
//  EQU. Su nombre proviene de la palabra EQUATE que significa 
//  IGUALAR. Tanto la variable ETIQUETA como la variable OPERANDO
//  deben de ser diferentes de NULL, si no se cumpliera esta
//  condición se debe de mostrar en pantalla el error. La palabra
//  EQU se puede escribir tanto con mayúsculas como en minúsculas.
//  La variable OPERANDO puede tener valores que van del 0 al 65535
//  en cualquier base numérica. Esta directiva se puede representar
//  indistintamente antes y después de la directiva ORG. 
//  Esta directiva no incrementa el valor de la variable CONTLOC.
    
    
    
    
    
    
    
    
    
}


//  ORG. Indica el inicio de las directivas e instrucciones que
//  afectan a CONTLOC, sólo debe de existir un solo ORG. Siempre 
//  debe de tener asociado un valor en la variable OPERANDO, en
//  cualquier base numérica, con un rango de valores de 0 a 65535,
//  se puede representar con mayúsculas y minúsculas. La variable
//  ETIQUETA debe de ser NULL. Si existiera más de un ORG se debe 
//  de marcar un error en pantalla Cualquier otra representación es
//  un error.
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * autor@Moschino19
 */
package ensambladorhc12;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


//Poner como prioridad el analisis de decimales
//Poner como prioridad el direccionamiento [
//Agregar tipo de base en analisis de rango


public final class EnsambladorHC12Raw {


     private String[] contenidoDeArchivo;
    private String contenidoProcesado;
    private String contenidoDeArchivoTxt;
    private String codop;
    private String operando;
    private String etiqueta;
    private final String FOLDER_ERRORS = "errores";
    private final String TABOP = "/TABOP.TXT";
    private String FILE_NAME;
    private String FOLDER_NAME;
    private String contenidoTABOPtxt;
    
   
    public EnsambladorHC12Raw(String FILE_NAME, String FOLDER_NAME) 
    {
       this.setFILE_NAME(FILE_NAME);
       this.setFOLDER_NAME(FOLDER_NAME);
        new File(getFOLDER_ERRORS()).mkdirs();
    }
    
    public void inicializarVariables(){
        try 
        {
            this.setContenidoTABOPtxt(this.readFile(this.getFOLDER_NAME()+this.getTABOP(),StandardCharsets.UTF_8));
            this.setContenidoDeArchivoTxt(this.readFile(this.getFILE_NAME(),StandardCharsets.UTF_8));
            this.setContenidoDeArchivo(Files.readAllLines(Paths.get(this.getFILE_NAME()),StandardCharsets.UTF_8).toArray(new String[0]));
            this.setContenidoProcesado(this.obtenerResultados());
        } catch (IOException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
    }
    
    private String obtenerResultados() {
        StringBuilder s = new StringBuilder();
        String finDeArchivo = null;
        
        if(this.getContenidoDeArchivoTxt().trim().isEmpty())
            s.append(this.writeError(0, "\n\tERROR El archivo no contiene nada"));
        else
        {
            String[] lineas = this.getContenidoDeArchivo();
            
            for (int i = 0; i < lineas.length; i++) 
            {   
                 String[] palabra = this.separarEnPalabras(lineas[i]);
                 if(i == lineas.length-1 )
                 {
                     if( palabra[0].matches("^(?i)END$"))
                     {
                          s.append("\n\tEND\t\tNo se encontró en el TABOP");
                          break;
                     }
                     else
                         finDeArchivo = this.writeError(i+1, "\nERROR El archivo no termina con END");
                 }
                 if (lineas[i].trim().isEmpty()) 
                      s.append(this.writeError(i+1, "\n\tERROR linea Vacía"));
                 else if(this.isComentario(lineas[i])) 
                     s.append("COMENTARIO=").append(lineas[i]).append("\n");
                 else 
                 {
                     if(this.hasETIQUETA(lineas[i]))
                     {
                         this.setEtiqueta(this.validarETIQUETA(palabra[0])+"\t");
                         if(this.getEtiqueta().contains("ERROR "))
                             s.append("\n").append(this.writeError(i+1, this.getEtiqueta()));
                         else
                             s.append("\n").append(this.getEtiqueta());
                         if(palabra.length>1)
                             s.append(this.analizarLinea(i+1, Arrays.copyOfRange(palabra, 1, palabra.length))).append("\n");
                         else
                             s.append(this.writeError(i+1, "null\n\tERROR Si existe una etiqueta debe existir otro token más")).append("null\n");
                     }
                     else
                         s.append("null\t").append(this.analizarLinea(i+1, palabra)).append("\n");
                 }
                 if(finDeArchivo!=null)
                     s.append(finDeArchivo);
            }
        }
        return s.toString();
    }
    
    public String readFile(String path, Charset encoding) throws IOException{ 
         byte[] encoded = Files.readAllBytes(Paths.get(path));
         return new String(encoded, encoding);
    }
    
    public String[] separarEnPalabras(String contenido) {
        return contenido.trim().split("\\s++");
    }
    
    public String analizarLinea(int LINE_NUMBER, String[] palabras) {
        StringBuilder z = new StringBuilder();
        this.setCodop(this.validarCODOP(palabras[0]));
        if(this.getCodop().contains("\tERROR "))
            z.append(this.writeError(LINE_NUMBER, this.getCodop()));
        else
            z.append(this.getCodop());
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append("");
            this.setOperando(this.validarOPERANDO(s.toString()));
            z.append(this.getOperando());
            z.append("\t").append(this.buscarEnTABOP(palabras[0], this.getOperando(), LINE_NUMBER));
        }
        else
        {
            this.setOperando("null");
            z.append(this.getOperando());
            z.append("\t").append(this.buscarEnTABOP(palabras[0], this.getOperando(), LINE_NUMBER));
        }
        return z.toString();
    }
    
      public String buscarEnTABOP(String palabra, String hasOperando, int LINE_NUMBER){
          
        for (String linea : contenidoTABOPtxt.split("\n")) 
        {
            if (linea.contains(palabra.toUpperCase())) 
            {
                String[] tokens = linea.split("\\|");
                if((tokens[1].contains("SI") && hasOperando.equals("null")) ||
                   (tokens[1].contains("NO") && !hasOperando.equals("null")))
                    return "No se encontró en el TABOP";
                else
                {
                    //Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n"
                    String s = this.validaTipoOPERANDO(hasOperando, LINE_NUMBER, Integer.parseInt(tokens[5]));
                    if(hasOperando.equals("null"))
                    //  Modo Inherente: No tiene operando. En caso de haber un operando, entonces, se debe de marcar un error en pantalla.		
                    //  ORG	$FFF
                    //	INX
                    //	END
                        return "Inherente de "+tokens[6]+" bytes\n";          
                    else if(tokens[2].equals("REL"))
                        return this.validaRelativoDe8y16Bits(hasOperando, Integer.parseInt(tokens[5]), tokens[6], LINE_NUMBER)+"\n";
                    else if(s.contains("ERROR") && s.contains("|"))
                        return this.writeError(LINE_NUMBER, (s.split("\\|")[1]));
                    else if(s.contains("ERROR"))
                        return this.writeError(LINE_NUMBER, s);
                    else if(linea.contains(s.split("\\|")[0]))
                        return (s.split("\\|")[1])+" de "+tokens[6]+" bytes\n";
                }
            }
        }
        return this.writeError(LINE_NUMBER, "\nERROR NO SE ENCONTRO EL CODOP DE OPERACIÓN en el archivo TABOP.txt\n");
    }
    private String validaTipoOPERANDO(String palabra, int LINE_NUMBER, int bytes_pendientes) { 
        String s;
        if(palabra.startsWith("#"))
            return "IMM|"+this.validaInmediato(palabra, LINE_NUMBER, bytes_pendientes);
        else if(palabra.startsWith("["))
        {
            if(palabra.contains(","))
            {
                if(palabra.length()>2)
                {
                    if(palabra.substring(1).split(",")[0].matches("^[0-9]*$"))
                        return "[IDX2]|"+this.validaIndizadoIndirectoDe16Bits(palabra, LINE_NUMBER);
                    else
                        return "[D,IDX]|"+this.validaIndizadoDeAcumuladorIndirecto(palabra, LINE_NUMBER);
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
                    return "IDX|"+this.validaIndizadoDeAcumulador(palabra, LINE_NUMBER);
                    /*s = this.validaIndizadoDeAcumulador(palabra, LINE_NUMBER);
                    if(s.contains("ERROR"))
                        return s;*/
                else if(palabra.contains("+") || palabra.endsWith("-") || palabra.matches("^.*,-.*$"))
                    return "IDX|"+this.validaIndizadoDeAutoPrePostDecrementoIncremento(palabra, LINE_NUMBER);
                else if(this.isDECIMAL(g[0]))
                {
                    long num = Long.parseLong(s);
                    if((num>= -256 && num <= -17) || (num>= 16 && num<=255))
                        return "IDX1|"+this.validaIndizadoDe9Bits(palabra, LINE_NUMBER);
                    else if((num>= 256 && num <= 65535))
                        return  "IDX2|"+this.validaIndizadoDe16Bits(palabra, LINE_NUMBER);
                    else
                        return "IDX|"+this.validaIndizadoDe5Bits(palabra, LINE_NUMBER);
                }
                return "ERROR algun registro del operando Indizado es invalido";
            }
            else
            {
                return "IDX|"+this.validaIndizadoDe5Bits(palabra, LINE_NUMBER);
                //indizado de 5 bits
            }
        }
        else
        {
            if((""+palabra.charAt(0)).matches("^[a-zA-Z]*$"))
            {
                s = this.validarETIQUETA(palabra);
                if(s.contains("ERROR"))
                    return s;
                return "EXT|"+this.validaExtendido(palabra, LINE_NUMBER);
            }
            s = this.convertirADecimalString(palabra);
            if(s.contains("ERROR"))
                return s;
            long l = Long.parseLong(s);
            if(l >= 0 && l <= 255)
                return "DIR|"+this.validaDirecto(palabra, LINE_NUMBER);
            else if(l >= 0 && l <= 65535)
                return "EXT|"+this.validaExtendido(palabra, LINE_NUMBER);
            else if(l < 0)
                return "DIR|ERROR el valor decimal es menor minimo para el modo de direccionamiento Decimal/Extendido";
            return "DIR|ERROR el valor decimal es mayor al rango maximo para el modo de direccionamiento Decimal/Extendido";        
        }
        //return this.writeError(LINE_NUMBER, "ERROR el Operando no tiene posible modo direccionamiento valido");
    }

    private String convertirADecimalString(String palabra) {
        if(this.isDECIMAL(palabra))
            return palabra;
        else if(palabra.startsWith("$"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo hexadecimal debe tener minimo 2 caracteres";
            if(this.isHEX(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 16));
            return "ERROR Base hexadecimal invalida, Caracteres validos:  A-F 0-9";
        }
        else if(palabra.startsWith("%"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo binaria debe tener minimo 2 caracteres";
            if(this.isBINARY(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 2));
            return "ERROR Base binaria invalida, Caracteres validos:  0-1";
        }
        else if(palabra.startsWith("@"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo octal debe tener minimo 2 caracteres";
            if(this.isOCTAL(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 8));
            return "ERROR Base octal invalida, Caracteres validos:  0-8";
        }
        return "ERROR Base decimal invalida, Caracteres validos:  0-9";
    }

    private String validaBase(String palabra, int LINE_NUMBER, int min, int max, String tipo_direccionamiento) {
        String s = this.quitaCeros(palabra);
        if(this.isDECIMAL(palabra))
            return this.validaDECIMAL(s, LINE_NUMBER, min, max, tipo_direccionamiento);
        else if(palabra.length() >1)
        {
            if(palabra.startsWith("$"))
                return this.validaHEX(s, LINE_NUMBER, min, max, tipo_direccionamiento);
            else if(palabra.startsWith("%"))
                return this.validaBINARY(s, LINE_NUMBER, min, max, tipo_direccionamiento);
            else if(palabra.startsWith("@"))
                return this.validaOCTAL(s, LINE_NUMBER, min, max, tipo_direccionamiento);
        }
        return "ERROR el Operando "+ tipo_direccionamiento+" no tiene caracteres despues del caracter de base";
    }
    
    private String quitaCeros(String substring) {
        if(substring.matches("^-?[0-9]*$"))
            return substring.replaceFirst("^0+(?!$)", "");
        else if(substring.length()>1)
            return substring.substring(1).replaceFirst("^0+(?!$)", "");
        return "";
    }

    public boolean  isComentario(String linea) {
         return this.separarEnPalabras(linea)[0].startsWith(";");
    }
    
    public boolean hasETIQUETA(String linea ) {
        return !(""+linea.charAt(0)).matches("\\s++");
    }
    
    public String validarOPERANDO(String palabra ) {
        StringBuilder s = new StringBuilder(palabra);
        
        return  palabra;
    }
    
    public String validarCODOP(String palabra ) {
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
     
   public String validarETIQUETA(String palabra ) {
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
    
    public String writeError(int LINE_NUMBER, String FILE_CONTENT){
        try(PrintWriter out = new PrintWriter(this.getFOLDER_ERRORS()+"/"+LINE_NUMBER+"ERROR"+".txt"))
        {
            out.println(FILE_CONTENT+":Linea "+LINE_NUMBER);
            out.close();
        } 
        catch (FileNotFoundException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
        return FILE_CONTENT+":Linea "+LINE_NUMBER+"\n";
    }
    
   
    
     public String getCodop() {
        return codop;
    }

    public void setCodop(String aCodop) {
        codop = aCodop;
    }

    public String getOperando() {
        return operando;
    }

    public void setOperando(String aOperando) {
        operando = aOperando;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String aEtiqueta) {
        etiqueta = aEtiqueta;
    }
   
    public String[] getContenidoDeArchivo() {
        return contenidoDeArchivo;
    }

    public void setContenidoDeArchivo(String[] aContenidoDeArchivo) {
        contenidoDeArchivo = aContenidoDeArchivo;
    }
    
    public String getContenidoDeArchivoTxt() {
        return contenidoDeArchivoTxt;
    }

    public void setContenidoDeArchivoTxt(String contenidoDeArchivoTxt) {
        this.contenidoDeArchivoTxt = contenidoDeArchivoTxt;
    }
    
     public String getContenidoProcesado() {
        return contenidoProcesado;
    }


    public void setContenidoProcesado(String contenidoProcesado) {
        this.contenidoProcesado = contenidoProcesado;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public String getFOLDER_NAME() {
        return FOLDER_NAME;
    }

    public void setFOLDER_NAME(String FOLDER_NAME) {
        this.FOLDER_NAME = FOLDER_NAME;
    }

    public String getFOLDER_ERRORS() {
        return FOLDER_ERRORS;
    }

    public String getTABOP() {
        return TABOP;
    }

    public void setContenidoTABOPtxt(String contenidoTABOPtxt) {
        this.contenidoTABOPtxt = contenidoTABOPtxt;
    }
    public  String getContenidoTABOPtxt() {
        return contenidoTABOPtxt;
    }


    private String validaInmediato(String palabra, int LINE_NUMBER, int bytes_pendientes) {
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
            String s =  this.validaBase(palabra.substring(1), LINE_NUMBER, 0, max, "Inmediato");
            if(s.contains("ERROR"))
                return s;
            return "Inmediato de "+bytes_pendientes*8+" bits, ";
        }
        else
            return "ERROR el Operando Inmediato debe empezar con un caracter de base despues del #";
    }

    private String validaDirecto(String palabra, int LINE_NUMBER) {
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
        String s =  this.validaBase(palabra, LINE_NUMBER, 0, 255, "Directo");
        if(s.contains("ERROR"))
            return s;
        return "Directo,";
    }

    private String validaExtendido(String palabra, int LINE_NUMBER) {
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
                String s = this.validarETIQUETA(palabra);
                if(s.contains("ERROR"))
                    return s;
                return "Extendido,";
                //return palabra;
        }
        else
        {
            String s = this.validaBase(palabra, LINE_NUMBER, 256, 65535, "Extendido");
            if(s.contains("ERROR"))
                return s;
            return "Extendido,"; 
        }
    }

    private String validaIndizadoDe5Bits(String palabra, int LINE_NUMBER) {
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
            String s =  this.validaRegistro(palabra.substring(1), "Indizado De 5Bits");
            if(s.contains("ERROR"))
                return s;
            return "Indizado de 5 bits, ";
        }
        else if(palabra.matches("^-?[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = this.validaDECIMAL(palabra.split(",")[0], LINE_NUMBER, -16, 15, "Indizado de 5 bits");
            if(s.contains("ERROR"))
                return s.toString();
            return "Indizado de 5 bits,";
        }        
        return this.validaIndizado(palabra, LINE_NUMBER, -16, 15, 0, 0, "Indizado De 5 Bits");
    }

    private String validaIndizadoDe9Bits(String palabra, int LINE_NUMBER) {
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
        
//        if(palabra.startsWith(","))
//        {
//            String s = this.validaRegistro(palabra, LINE_NUMBER);
//            if(s.contains("ERROR"))
//                return s;
//            return "Indizado de 9 bits ";
//        }
//        else
        if(palabra.matches("^-?[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = this.validaRangoDobleDECIMAL(palabra.split(",")[0], -256, -17, 16, 255,"Indizado de 9 bits");
            if(s.toString().contains("ERROR"))
                return s.toString();
            return "Indizado de 9 bits, ";
            //return s.toString();
        }        
        else
            return this.validaIndizado(palabra, LINE_NUMBER, -256, -17, 16, 255,"Indizado De 9 Bits");
    }
    
    private String validaIndizadoDe16Bits(String palabra, int LINE_NUMBER) {
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
            String s = this.validaRegistro(palabra, "Indizado de 16 bits");
            if(s.contains("ERROR"))
                return s;
            return "Indizado de 16 bits ";
        }
        else if(palabra.matches("^-*[0-9]*,(?i)(X|SP|PC|Y)$"))
        {
            String s = this.validaDECIMAL(palabra.split(",")[0].substring(1), LINE_NUMBER, 256, 65535, "Indizado de 16 bits");
            if(s.contains("ERROR"))
                return s.toString();
            return "Indizado de 16 bits,";
        }        
        else
            return this.validaIndizado(palabra, LINE_NUMBER, 256, 65535, 0, 0, "Indizado De 16 Bits");
    }

    private String validaIndizadoIndirectoDe16Bits(String palabra, int LINE_NUMBER) {
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
//        if(palabra.startsWith(","))
//        {
//            String s = this.validaRegistro(palabra, LINE_NUMBER);
//            if(s.contains("ERROR"))
//                return s;
//            return "Indizado Indirecto de 16 bits ";
//        }

//        else 
        if(palabra.matches("^\\[[0-9]*,(X|SP|PC|Y)\\]$"))
        {
            String s = this.validaDECIMAL(palabra.split(",")[0].substring(1), LINE_NUMBER, 0, 65535, "Indizado Indirecto de 16 bits,");
            if(s.contains("ERROR"))
                return s.toString();
            return "Indizado Indirecto de 16 bits,";
            //return s.toString();
        }        
        else
            return this.validaIndizado(palabra.substring(1, palabra.length()-1), LINE_NUMBER, 0, 65535, 0, 0, "Indizado Indirecto De 16 Bits");
    }

    private String validaIndizadoDeAutoPrePostDecrementoIncremento(String palabra, int LINE_NUMBER) {
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
        //Decimal de 1 a 8 , (+|-)?(?i)(X | Y | SP)(+|-)?
        if(palabra.matches("^[1-8],(((\\+|-)(?i)(X|SP|Y))|((?i)(X|SP|Y)(\\+|-)))$"))
        {
            String s = this.validaDECIMAL(palabra, LINE_NUMBER, 1, 8, "Indizado De Auto Pre Post Decremento Incremento");
            if(s.contains("ERROR"))
                return s;
            String g[] = palabra.split(",");
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
                    String s = this.validaDECIMAL(tokens[0], LINE_NUMBER, 1, 8, "Indizado De Auto Pre Post Decremento Incremento");
                    if(s.contains("ERROR"))
                        return s;
                }
                else
                    return "ERROR el primer registro del Operando de Auto Pre  Post no es valido";
                if(!tokens[1].matches("^(((\\+|-)(?i)(X|SP|Y))|((?i)(X|SP|Y)(\\+|-)))$"))
                    return "ERROR el segundo registro del Operando de Auto Pre Post no es valido";
            }     
            return "ERROR el primer registro del Operando de Auto Pre Post no es valido";
        }
    }
    

    private String validaIndizadoDeAcumulador(String palabra, int LINE_NUMBER) {
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
        
        //(?i)(A | B | D),(?i)(X | Y | SP)
        if(palabra.matches("^(?i)(B|A|D),(?i)(X|PC|Y|SP)$"))
            return "Indizado de Acumulador,";
            //return palabra;
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

    private String validaIndizadoDeAcumuladorIndirecto(String palabra, int LINE_NUMBER) {
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
            return "Indizado Indirecto de Acumulador,";
            //return palabra;
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
    
     private String validaIndizado(String palabra, int LINE_NUMBER, int min, int max, int min2, int max2, String tipo_direccionamiento) {
        String tokens[] = palabra.split(",");
        if(tokens.length == 1)
            return "ERROR el primer registro del Operando"+ tipo_direccionamiento+" no es valido";
        else if(tokens.length == 2)
        {
            
            if(tokens[0].matches("^-?[0-9]*$"))
            {
                if(min2 == max2)
                {
                    String s = this.validaDECIMAL(tokens[0], LINE_NUMBER, min, max, tipo_direccionamiento);
                    if(s.contains("ERROR"))
                        return s;
                }
                else
                {
                    String s = this.validaRangoDobleDECIMAL(tokens[0], min, max, min2, max2, tipo_direccionamiento);
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

    private String validaRelativoDe8y16Bits(String palabra, int bytes_pendientes, String bytes_sumados, int LINE_NUMBER) {
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
        //Etiqueta si es 8 o 16 bits lo determina tabop
        String s = this.validarETIQUETA(palabra);
        if(s.contains("ERROR"))
          return this.writeError(LINE_NUMBER, s);
        return "Relativo de "+ bytes_pendientes*8 +" bits, de "+bytes_sumados+" bytes";
        //return s;
    }
    
    private String validaHEX(String palabra, int LINE_NUMBER,int min, int max, String tipo_direccionamiento) {
        //Carácter de pesos ($) y le pueden seguir las letras, minúsculas y /o mayúsculas, 
        //A a F y los dígitos del 0 al 9.
        if(!this.isHEX(palabra))
           return this.writeError(LINE_NUMBER, "ERROR el operando es un hexadecimal pero tiene algún carácter inválido");
        return this.validaRango(palabra, min, max, 16, LINE_NUMBER,  tipo_direccionamiento);
    }

    private String validaOCTAL(String palabra, int LINE_NUMBER,int min, int max,  String tipo_direccionamiento) {
        //Octal, se representa con el carácter de @ y le pueden seguir los dígitos del 0 al 7.
        //^@([1-9]|1[0-8])$  del 1 al 18
        if(!this.isOCTAL(palabra))
           return this.writeError(LINE_NUMBER, "ERROR el operando es un octal pero tiene algún carácter inválido");
        return this.validaRango(palabra, min, max, 8, LINE_NUMBER,  tipo_direccionamiento);
    }

    private String validaBINARY(String palabra, int LINE_NUMBER,int min, int max, String  tipo_direccionamiento) {
        //Binario, se representa con el carácter de % y le pueden seguir los dígitos 0 y 1.        
        if(!this.isBINARY(palabra))
           return this.writeError(LINE_NUMBER, "ERROR el operando es un binario pero tiene algún carácter inválido");
        return this.validaRango(palabra, min, max, 2, LINE_NUMBER,  tipo_direccionamiento);
    }

    private String validaDECIMAL(String palabra, int LINE_NUMBER,int min, int max, String  tipo_direccionamiento) {
        //Decimal inicia con cualquiera de los dígitos de 0 al 9.
        if(!this.isDECIMAL(palabra))
           return this.writeError(LINE_NUMBER, "ERROR el operando es un decimal pero tiene algún carácter inválido");
        return this.validaRango(palabra, min, max, 10, LINE_NUMBER,  tipo_direccionamiento);
    }

    private String validaRango(String palabra, int min, int max, int base, int LINE_NUMBER, String  tipo_direccionamiento) {
        long l = Long.parseLong(palabra, base);
        if(l < min)
            return "ERROR el rango es menor que el minimo valido para el modo de direccionamiento "+ tipo_direccionamiento;
        else if( l > max)
            return "ERROR el rango es mayor que el maximo valido para el modo de direccionamiento "+ tipo_direccionamiento; 
        return palabra;
    }
    
    private String validaRangoDobleDECIMAL(String palabra, int min1, int max1, int min2, int max2, String tipo_direccionamiento) {
        if(!this.isDECIMAL(palabra))
            return "ERROR el operando es un decimal pero tiene algún carácter inválido "+tipo_direccionamiento;
        long l = Long.parseLong(palabra);
        if( (l >= min1 && l <= max1) || (l >= min2 && l <= max2))
            return palabra;
        else
            return "ERROR el rango es menor que el minimo valido para el modo de direccionamiento "+ tipo_direccionamiento;
    }

    
    
    private String validaRegistro(String palabra, String tipo_direccionamiento) {
        if(!palabra.matches("^(?i)(X|SP|PC|Y)$"))
            return "ERROR el Registro del Operando "+ tipo_direccionamiento+ " es invalido";
        return palabra;
    }
    
    private boolean isHEX(String palabra) {
        return palabra.matches("^[A-Fa-f0-9]*$");
    }

    private boolean isBINARY(String palabra) {
        return palabra.matches("^[0-1]*$");
    }

    private boolean isOCTAL(String palabra) {
        return palabra.matches("^[0-7]*$");
    }

    private boolean isDECIMAL(String palabra) {
        return palabra.matches("^-?[0-9]*$");
    }

    //return this.writeError(LINE_NUMBER, "ERROR despues del identificador de base no existe ningun caracter ");
    /*s = this.getCantidadDeBytes(palabra, LINE_NUMBER);
                if(s.contains("ERROR"))
                    return s;
                cantidad_de_bytes = Integer.valueOf(s);
                if(cantidad_de_bytes == 2)
                    return "DIR";
                else if(cantidad_de_bytes > 2)
                    return "EXT";
                    */
    
    /*private String getCantidadDeBytes(String palabra, int LINE_NUMBER) {
        String s;
        if(palabra.startsWith("$")&& palabra.length()>1)
        {
            s = this.conviertoHEXaBytes(palabra.substring(1), LINE_NUMBER);
            if(s.contains("ERROR"))
                return s;
            if(s.length()%8!=0)
                return String.valueOf((s.length()/8)+1);
            else
                return String.valueOf(s.length()/8);
        }
        else if(palabra.startsWith("@")&& palabra.length()>1)
        {
            s = this.conviertoOCTaBytes(palabra.substring(1), LINE_NUMBER);
            if(s.contains("ERROR"))
                return s;
            if(s.length()%8!=0)
                return String.valueOf((s.length()/8)+1);
            else
                return String.valueOf(s.length()/8);
        }
        else if(palabra.startsWith("%")&& palabra.length()>1)
        {
            s = this.conviertoBINaBytes(palabra.substring(1), LINE_NUMBER);
            if(s.contains("ERROR"))
                return s;
            if(s.length()%8!=0)
                return String.valueOf((s.length()/8)+1);
            else
                return String.valueOf(s.length()/8);
        }
        else
        {
            s = this.conviertoDECaBytes(palabra, LINE_NUMBER);
            if(s.contains("ERROR"))
                return s;
            if(s.length()%8!=0)
                return String.valueOf((s.length()/8)+1);
            else
                return String.valueOf(s.length()/8);
        }
    }*/
    
    /*
     private String conviertoHEXaBytes(String palabra, int LINE_NUMBER) {
        //Carácter de pesos ($) y le pueden seguir las letras, minúsculas y /o mayúsculas, 
        //A a F y los dígitos del 0 al 9.
        if(palabra.length() >1)
        {
            String s  = palabra.replaceFirst("^0+(?!$)", "");
            if(!s.matches("^[A-Fa-f0-9]*$"))
               return this.writeError(LINE_NUMBER, "ERROR el operando es un hexadecimal pero tiene algún carácter inválido");            
            return Long.toString(Long.parseLong(palabra.substring(1), 16),2);
        }
        return this.writeError(LINE_NUMBER, "ERROR despues del identificador de base no existe ningun caracter ");
    }
     */
    
    /*
     private String conviertoBINaBytes(String palabra, int LINE_NUMBER) {
        if(palabra.length() >1)
        {
            String s  = palabra.replaceFirst("^0+(?!$)", "");
            if(!s.matches("^[0-1]*$"))
               return this.writeError(LINE_NUMBER, "ERROR el operando es un binario pero tiene algún carácter inválido");
            return palabra;
        }
        return this.writeError(LINE_NUMBER, "ERROR despues del identificador de base no existe ningun caracter ");
    }
     */
    
    /*
      private String conviertoDECaBytes(String palabra, int LINE_NUMBER) {
        String s  = palabra.replaceFirst("^0+(?!$)", "");
        if(!s.matches("^[0-9]*$"))
           return this.writeError(LINE_NUMBER, "ERROR el operando es un decimal pero tiene algún carácter inválido");
        return Long.toString(Long.parseLong(palabra),2);
      }
     */
    
    /*
     private String conviertoOCTaBytes(String palabra, int LINE_NUMBER) {
        if(palabra.length() >1)
        {
            String s  = palabra.replaceFirst("^0+(?!$)", "");
            if(!s.matches("^[0-7]*$"))
               return this.writeError(LINE_NUMBER, "ERROR el operando es un octal pero tiene algún carácter inválido");
            return Long.toString(Long.parseLong(palabra.substring(1), 8),2);
        }
        return this.writeError(LINE_NUMBER, "ERROR despues del identificador de base no existe ningun caracter ");
    }
     */
    
    /*
    private String validaTipoCODOP(String palabra, String tipoCODOP, int LINE_NUMBER, int bytes_pendientes) {
       if(tipoCODOP.equals("IMM"))
           return this.validaInmediato(palabra, LINE_NUMBER, bytes_pendientes);
       else if(tipoCODOP.equals("IDX"))
       {
           if(palabra.contains("B")|| palabra.contains("b") || palabra.contains("D") || palabra.contains("d") || palabra.contains("A") || palabra.contains("a"))
               return this.validaIndizadoDeAcumulador(palabra, LINE_NUMBER);
           else if(palabra.contains("+") || palabra.endsWith("-") || palabra.matches("^.*,-.*$"))
               return this.validaIndizadoDeAutoPrePostDecrementoIncremento(palabra, LINE_NUMBER);
           else
               return this.validaIndizadoDe5Bits(palabra, LINE_NUMBER);
       }
       else if(tipoCODOP.equals("IDX1"))
           return this.validaIndizadoDe9Bits(palabra, LINE_NUMBER);
       else if(tipoCODOP.equals("IDX2"))
           return this.validaIndizadoDe16Bits(palabra, LINE_NUMBER);
       else if(tipoCODOP.equals("[IDX2]"))
           return this.validaIndizadoIndirectoDe16Bits(palabra, LINE_NUMBER);
       else if(tipoCODOP.equals("[D,IDX]"))
           return this.validaIndizadoDeAcumuladorIndirecto(palabra, LINE_NUMBER);
       else if(tipoCODOP.equals("DIR"))
           return this.validaDirecto(palabra, LINE_NUMBER);
       else if(tipoCODOP.equals("EXT"))
           return this.validaExtendido(palabra, LINE_NUMBER);
       return palabra;
   }
     */
}        

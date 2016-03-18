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


public final class EnsambladorHC12Raw {


    private String[] contenidoDeArchivo;
    private String contenidoProcesado;
    private String contenidoDeArchivoTxt;
    private String codop;
    private String operando;
    private String etiqueta;
    private final String FOLDER_ERRORS = "/errores";
    private final String TABOP = "/TABOP.TXT";
    private String FILE_NAME;
    private String FOLDER_NAME;
    private String contenidoTABOPtxt;
    
   
    public EnsambladorHC12Raw(String FILE_NAME, String FOLDER_NAME) 
    {
       this.setFILE_NAME(FILE_NAME);
       this.setFOLDER_NAME(FOLDER_NAME);
        new File(FOLDER_NAME+getFOLDER_ERRORS()).mkdirs();
    }

    
    public static void main(String[] args) {
        String FILE_NAME = "P1ASM.TXT";
        String FOLDER_NAME ="src/ensambladorhc12/";
        
        EnsambladorHC12Raw ensamblador = new EnsambladorHC12Raw(FILE_NAME,FOLDER_NAME);
       
        if(!Files.exists(Paths.get(FOLDER_NAME+FILE_NAME)))
            ensamblador.setContenidoProcesado(ensamblador.writeError(0, "\n\tERROR: El archivo "+FILE_NAME+" no existe junto al .jar"));
        else
            ensamblador.inicializarVariables();
        System.out.println(ensamblador.getContenidoProcesado());
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
            s.append(this.writeError(0, "\n\tERROR: El archivo no contiene nada"));
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
                          s.append("\nEND");
                          break;
                     }
                     else
                         finDeArchivo = this.writeError(i+1, "\nERROR: El archivo no termina con END");
                 }
                 if (lineas[i].trim().isEmpty()) 
                      s.append(this.writeError(i+1, "\n\tERROR: linea Vacía"));
                 else if(this.isComentario(lineas[i])) 
                     s.append("COMENTARIO=").append(lineas[i]).append("\n");
                 else 
                 {
                     if(this.hasETIQUETA(lineas[i]))
                     {
                         this.setEtiqueta(this.validarETIQUETA(palabra[0]));
                         if(this.getEtiqueta().contains("\tERROR: "))
                             s.append("\n").append(this.writeError(i+1, this.getEtiqueta()));
                         else
                             s.append("\n").append(this.getEtiqueta()).append("\n");
                         if(palabra.length>1)
                             s.append(this.analizarLinea(i+1, Arrays.copyOfRange(palabra, 1, palabra.length)));
                         else
                         {
                             s.append(this.writeError(i+1, "CODOP = null\n\tERROR: Si existe una etiqueta debe existir otro token más"));
                             s.append("OPERANDO = null\n");
                         }
                     }
                     else
                     {
                         s.append("\nETIQUETA = null\n");
                         s.append(this.analizarLinea(i+1, palabra)).append("\n");
                     }
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
        if(this.getCodop().contains("\tERROR: "))
            z.append(this.writeError(LINE_NUMBER, this.getCodop()));
        else
            z.append(this.getCodop()).append("\n");
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append(" ");
            this.setOperando(this.validarOPERANDO(s.toString()));
            z.append(this.buscarEnTABOP(palabras[0], true,LINE_NUMBER));
            z.append(this.getOperando());
        }
        else
        {
            z.append(this.buscarEnTABOP(palabras[0], false, LINE_NUMBER));
            this.setOperando("OPERANDO = null\n");
            z.append(this.getOperando());
        }
        return z.toString();
    }

    public boolean  isComentario(String linea) {
         return this.separarEnPalabras(linea)[0].startsWith(";");
    }
    
    public boolean hasETIQUETA(String linea ) {
        return !(""+linea.charAt(0)).matches("\\s++");
    }
    
    public String validarOPERANDO(String palabra ) {
        return  "OPERANDO = "+palabra+"\n";
    }
    
    public String validarCODOP(String palabra ) {
        StringBuilder s = new StringBuilder("CODOP = "+palabra+"");
        if(palabra.length()>5)
            s.append("\n\tERROR: Tamaño de CODOP mayor a 5").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
            s.append("\n\tERROR: Los CODOPS no pueden empezar con carácteres que no sean alfabéticos").toString();
        else if(!palabra.matches("^[^.]*.[^.]*$"))
            s.append("\n\tERROR: No se puede usar más de 2 veces el caracter \".\" en los CODOPS").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z\\.]+$"))
               ) 
            s.append("\n\tERROR: Existe algún carácter inválido en el CODOP").toString();
        return  s.toString();
    }
     
   public String validarETIQUETA(String palabra ) {
        StringBuilder s = new StringBuilder("ETIQUETA = "+palabra+"");
        if(palabra.length()>8)
            return s.append("\n\tERROR: Tamaño de ETIQUETA mayor a 8").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
            return s.append("\n\tERROR: Las ETIQUETAS no pueden empezar con carácteres que no sean alfabéticos").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z0-9_]{0,7}$"))
               )
             return s.append("\n\tERROR: Existe algún carácter inválido en la ETIQUETA").toString();
        else
            return s.toString();
    }
    
    public String writeError(int LINE_NUMBER, String FILE_CONTENT){
        try(PrintWriter out = new PrintWriter(this.getFOLDER_NAME()+this.getFOLDER_ERRORS()+"/"+LINE_NUMBER+"ERROR"+FILE_CONTENT.replaceAll("\\s","") +".txt"))
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
    
    public String getContenidoTABOPtxt() {
        return this.contenidoTABOPtxt;
    }
    
    public String buscarEnTABOP(String palabra, boolean hasOperando, int LINE_NUMBER){
        for (String linea : contenidoTABOPtxt.split("\n")) 
        {
            if (linea.contains(palabra.toUpperCase())) 
            {
                String[] tokens = linea.split("\\|");
                if(tokens[1].contains("SI") && !hasOperando)
                    return this.writeError(LINE_NUMBER, "EL CODOP DEBE DE TENER OPERANDO\n");
                else if (tokens[1].contains("NO") && hasOperando)
                    return this.writeError(LINE_NUMBER, "EL CODOP NO DEBE DE TENER OPERANDO\n");
                else
                    return "Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n";       
            }
        }
        return "\nNO SE ENCONTRO EL CODOP DE OPERACIÓN en el archivo TABOP.txt\n";
    }
}        

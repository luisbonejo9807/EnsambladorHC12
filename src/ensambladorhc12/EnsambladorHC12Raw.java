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


public final class EnsambladorHC12Raw {

    private String contenidoDeArchivo;
    private String codop;
    private String operando;
    private String etiqueta;
    private String contenidoTextField;
   
    public EnsambladorHC12Raw(String FILE_NAME) 
    {
        try{this.setContenidoDeArchivo(this.readFile(FILE_NAME,StandardCharsets.UTF_8));}
        catch (IOException ex){Logger.getLogger(EnsambladorHC12.class.getName()).log(Level.SEVERE, null, ex);}
    }
    

    
    public static void main(String[] args) {
        String FILE_NAME = "src/ensambladorhc12/P1ASM2.TXT";
        new File("src/ensambladorhc12/errores").mkdirs();
        
        
        if(!Files.exists(Paths.get(FILE_NAME)))
            EnsambladorHC12Raw.writeError(0, "\n\tERROR: El archivo no existe");
        else
        {
            EnsambladorHC12Raw ensamblador = new EnsambladorHC12Raw(FILE_NAME);
            if(ensamblador.getContenidoDeArchivo().trim().isEmpty())
                EnsambladorHC12Raw.writeError(0, "\n\tERROR: El archivo no contiene nada");
            else
            {
                String[] lineas = ensamblador.separarEnLineas(ensamblador.getContenidoDeArchivo());

                for (int i = 0; i < lineas.length; i++) 
                {   
                     String[] palabra = ensamblador.separarEnPalabras(lineas[i]);

                    if (lineas[i].trim().isEmpty()) 
                          System.out.print(EnsambladorHC12Raw.writeError(i+1, "\n\tERROR: linea Vacía"));
                    else if(ensamblador.isComentario(lineas[i])) 
                         System.out.print("COMENTARIO="+lineas[i]);
                    else 
                    {
                         if(ensamblador.hasETIQUETA(lineas[i]))
                         {
                             ensamblador.setEtiqueta(ensamblador.validarETIQUETA(palabra[0]));
                             if(ensamblador.getEtiqueta().contains("\tERROR: "))
                                 System.out.print("\n"+EnsambladorHC12Raw.writeError(i+1, ensamblador.getEtiqueta()));
                             else
                                 System.out.print("\n"+ensamblador.getEtiqueta()+"\n");
                             if(palabra.length>1)
                                 System.out.print(ensamblador.analizarLinea(i+1, Arrays.copyOfRange(palabra, 1, palabra.length)));
                             else
                             {
                                 System.out.print(EnsambladorHC12Raw.writeError(i+1, "CODOP = null\n\tERROR: Si existe una etiqueta debe existir otro token más"));
                                 System.out.print("OPERANDO = null\n");
                             }
                         }
                         else
                         {
                             System.out.print("\nETIQUETA = null\n");
                             System.out.print(ensamblador.analizarLinea(i+1, palabra)+"\n");
                         }
                    }
                    if(i == lineas.length-1 )
                    {
                        if( palabra[0].matches("/^END$/i"))
                             System.out.println("END");
                        else
                            System.out.print(EnsambladorHC12Raw.writeError(i+1, "\n\tERROR: El archivo no termina con END"));
                    }
                 }
            }
        }
    }
    
    public String readFile(String path, Charset encoding) throws IOException{ 
         byte[] encoded = Files.readAllBytes(Paths.get(path));
         return new String(encoded, encoding);
    }
    
    public String[] separarEnLineas(String contenido) {
        return contenido.split("\n");
    }
    
    public String[] separarEnPalabras(String contenido) {
        return contenido.trim().split("\\s++");
    }
    
    public String analizarLinea(int LINE_NUMBER, String[] palabras) {
        StringBuilder z = new StringBuilder();
        this.setCodop(this.validarCODOP(palabras[0]));
        if(this.getCodop().contains("\tERROR: "))
            z.append(EnsambladorHC12Raw.writeError(LINE_NUMBER, this.getCodop()));
        else
            z.append(this.getCodop()+"\n");
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append(" ");
            this.setOperando(this.validarOPERANDO(s.toString()));
            z.append(this.getOperando());
        }
        else
        {
            this.setOperando("OPERANDO = null");
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
            return s.append("\n\tERROR: Tamaño de CODOP mayor a 5").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
                return s.append("\n\tERROR: Los CODOPS no pueden empezar con carácteres que no sean alfabéticos").toString();
        else if(!palabra.matches("^[^.]*.[^.]*$"))
            return  s.append("\n\tERROR: No se puede usar más de 2 veces el caracter \".\" en los CODOPS").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z\\.]+$"))
               ) 
            return s.append("\n\tERROR: Existe algún carácter inválido en el CODOP").toString();
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
   
    public String getContenidoDeArchivo() {
        return contenidoDeArchivo;
    }

    public void setContenidoDeArchivo(String aContenidoDeArchivo) {
        contenidoDeArchivo = aContenidoDeArchivo;
    }
    
    public static String writeError(int LINE_NUMBER, String FILE_CONTENT){
        try(  PrintWriter out = new PrintWriter( "src/ensambladorhc12/"+LINE_NUMBER+"ERROR"+".txt" )  )
        {
            out.println(FILE_CONTENT+":Linea "+LINE_NUMBER);
            out.close();
            return FILE_CONTENT+":Linea "+LINE_NUMBER+"\n";
        } 
        catch (FileNotFoundException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
        return null;
    }
}        

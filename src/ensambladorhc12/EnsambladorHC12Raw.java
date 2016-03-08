/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * autor@Moschino19
 */
package ensambladorhc12;

import java.io.IOException;
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
   
    public EnsambladorHC12Raw(String FILE_NAME) 
    {
        try{this.setContenidoDeArchivo(this.readFile(FILE_NAME,StandardCharsets.UTF_8));}
        catch (IOException ex){Logger.getLogger(EnsambladorHC12.class.getName()).log(Level.SEVERE, null, ex);}
    }
    

    
    public static void main(String[] args) {
        final String FILE_NAME = "C:\\Users\\hp\\Documents\\NetBeansProjects\\EnsambladorHC12\\src\\ensambladorhc12\\P1ASM2.TXT";
        if(!Files.exists(Paths.get(FILE_NAME)))
            System.out.println("\tERROR: El archivo no existe");
        else
        {
            EnsambladorHC12Raw ensamblador = new EnsambladorHC12Raw(FILE_NAME);
             if(ensamblador.getContenidoDeArchivo().trim().isEmpty())
                System.out.println("\tERROR: El archivo no contiene nada");
            else
            {
                String[] lineas = ensamblador.separarEnLineas(ensamblador.getContenidoDeArchivo());

                for (int i = 0; i < lineas.length; i++) 
                {   
                     String[] palabra = ensamblador.separarEnPalabras(lineas[i]);

                     if(i == lineas.length-1 )
                     {
                         if( palabra[0].matches("/^END$/i"))
                         {
                              System.out.println("END");
                              break;
                         }
                         else
                             System.out.println("\tERROR: El archivo no termina con END");
                     }
                     else if (lineas[i].trim().isEmpty()) 
                         System.out.println("\tERROR: linea Vacía");
                     else if(ensamblador.isComentario(lineas[i])) 
                         System.out.println("COMENTARIO="+lineas[i]);
                     else 
                     {
                         System.out.println("");
                         if(ensamblador.hasETIQUETA(lineas[i]))
                         {
                             System.out.print(ensamblador.validarETIQUETA(palabra[0]));
                             if(palabra.length>1)
                                 ensamblador.analizarLinea(Arrays.copyOfRange(palabra, 1, palabra.length));
                             else
                             {
                                 System.out.println("CODOP = null");
                                 System.out.println("\tERROR: Si existe una etiqueta debe existir otro token más");
                                 System.out.println("OPERANDO = null\n");
                             }
                         }
                         else
                         {
                             System.out.println("ETIQUETA = null");
                             ensamblador.analizarLinea(palabra);
                         }
                     }
                 }
            }
        }
    }
    
    public String readFile(String path, Charset encoding) throws IOException{ 
         byte[] encoded = Files.readAllBytes(Paths.get(path));
         return new String(encoded, encoding);
    }
    
    private String[] separarEnLineas(String contenido) {
        return contenido.split("\n");
    }
    
    private String[] separarEnPalabras(String contenido) {
        return contenido.trim().split("\\s++");
    }
    
    private void analizarLinea(String[] palabras) {
        
        System.out.print(this.validarCODOP(palabras[0]));
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append(" ");
            System.out.println(this.validarOPERANDO(s.toString()));
        }
        else
             System.out.println("OPERANDO = null\n");
    }

    public boolean  isComentario(String linea) {
         return this.separarEnPalabras(linea)[0].startsWith(";");
    }
    
    private boolean hasETIQUETA(String linea ) {
        return !(""+linea.charAt(0)).matches("\\s++");
    }
    
    public String validarOPERANDO(String palabra ) {
        return  "OPERANDO = "+palabra+"\n";
    }
    
    public String validarCODOP(String palabra ) {
        StringBuilder s = new StringBuilder("CODOP = "+palabra+"\n");
        if(palabra.length()>5)
            return s.append("\tERROR: Tamaño de CODOP mayor a 5\n").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
                return s.append("\tERROR: Los CODOPS no pueden empezar con carácteres que no sean alfabéticos\n").toString();
        else if(!palabra.matches("^[^.]*.[^.]*$"))
            return  s.append("\tERROR: No se puede usar más de 2 veces el caracter \".\" en los CODOPS\n").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z\\.]+$"))
               ) 
            return s.append("\tERROR: Existe algún carácter inválido en el CODOP\n").toString();
        return  s.toString();
    }
    
    private String validarETIQUETA(String palabra ) {
        StringBuilder s = new StringBuilder("ETIQUETA = "+palabra+"\n");
        if(palabra.length()>8)
            return s.append("\tERROR: Tamaño de ETIQUETA mayor a 8\n").toString();
        else if(!(""+palabra.charAt(0)).matches("^[a-zA-Z]$"))
            return s.append("\tERROR: Las ETIQUETAS no pueden empezar con carácteres que no sean alfabéticos\n").toString();
        else if(palabra.length()> 1 &&
                !(palabra.substring(1).matches("^[a-zA-Z0-9_]{0,7}$"))
               )
             return s.append("\tERROR: Existe algún carácter inválido en la ETIQUETA\n").toString();
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
}
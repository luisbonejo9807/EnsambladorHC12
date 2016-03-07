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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public final class EnsambladorHC12Raw {

    final static String FILE_NAME ="C:\\Users\\hp\\Documents\\NetBeansProjects\\EnsambladorHC12\\src\\ensambladorhc12\\P1ASM2.TXT";
    private static String contenidoDeArchivo;
    private static ArrayList<String>  comentarios;
    private static ArrayList<String>  codops;
    private static ArrayList<String>  operandos;
    private static ArrayList<String>  etiquetas;
    private static ArrayList<String>  lineasErroneas;

    

    public EnsambladorHC12Raw() 
    {
        this.setComentarios(new ArrayList<>());
        this.setCodops(new ArrayList<>());
        this.setOperandos(new ArrayList<>());
        this.setEtiquetas(new ArrayList<>());
    }
    
    
    
    
    public static void main(String[] args) {
        EnsambladorHC12Raw ensamblador = new EnsambladorHC12Raw();
        try 
        {
          String contenido = ensamblador.readFile(FILE_NAME,StandardCharsets.UTF_8);
           for (String linea : ensamblador.separarEnLineas(contenido))
           {
//               validacion para linea vacia
//               if(linea.length())
//               {
//                   System.out.println("ERROR linea vacia");
//               }
               String[] palabra = ensamblador.separarEnPalabras(linea);
              
               if(linea.charAt(linea.length()-1) == palabra[palabra.length-1].charAt(palabra[palabra.length-1].length()-1))
                        System.out.println("END");
               else if(ensamblador.isComentario(linea))
               {
                   ensamblador.getComentarios().add(linea);
                   System.out.println("COMENTARIO="+ensamblador.getComentarios().get(ensamblador.getComentarios().size()-1));
               }
               else 
               {
                   System.out.println("");
                   if(ensamblador.hasETIQUETA(linea))
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
        } catch (IOException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
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
        
        this.getCodops().add("null");
        this.getOperandos().add("null");
        
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

    public ArrayList<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<String> aComentarios) {
        comentarios = aComentarios;
    }

    public String getContenidoDeArchivo() {
        return contenidoDeArchivo;
    }

    public void setContenidoDeArchivo(String aContenidoDeArchivo) {
        contenidoDeArchivo = aContenidoDeArchivo;
    }
        
    public ArrayList<String> getCodops() {
        return codops;
    }

    public void setCodops(ArrayList<String> aCodops) {
        codops = aCodops;
    }

    public ArrayList<String> getOperandos() {
        return operandos;
    }

    public void setOperandos(ArrayList<String> aOperandos) {
        operandos = aOperandos;
    }
    
    public ArrayList<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(ArrayList<String> aEtiquetas) {
        etiquetas = aEtiquetas;
    }
    
    public ArrayList<String> getLineasErroneas() {
        return lineasErroneas;
    }

    public void setLineasErroneas(ArrayList<String> aLineasErroneas) {
        lineasErroneas = aLineasErroneas;
    }
}
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public final class EnsambladorHC12Raw {

    final static String FILE_NAME ="src\\ensambladorhc12\\P1ASM.TXT";
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
               if(ensamblador.isComentario(linea))
               {
                   ensamblador.getComentarios().add(linea);
                   System.out.println("COMENTARIO="+ensamblador.getComentarios().get(ensamblador.getComentarios().size()-1));
               }
               else if((""+linea.charAt(0)).matches("^[a-zA-Z]")&& ensamblador.isETIQUETA(ensamblador.separarEnPalabras(linea)[0]))
               {
                   ensamblador.getEtiquetas().add(ensamblador.separarEnPalabras(linea)[0]);
                   ensamblador.analizarLinea(ensamblador.separarEnPalabras(linea.substring(ensamblador.separarEnPalabras(linea)[0].length(),linea.length())));
                   
                   System.out.println("");
                   System.out.println("ETIQUETA="+ensamblador.getEtiquetas().get(ensamblador.getEtiquetas().size()-1));
                   System.out.println("CODOP="+ensamblador.getCodops().get(ensamblador.getCodops().size()-1));
                   System.out.println("OPERANDO="+ensamblador.getOperandos().get(ensamblador.getOperandos().size()-1));
               }
               else
               {
                   ensamblador.analizarLinea(ensamblador.separarEnPalabras(linea));
                   ensamblador.getEtiquetas().add("null");
                   
                   System.out.println("");
                    System.out.println("ETIQUETA="+ensamblador.getEtiquetas().get(ensamblador.getEtiquetas().size()-1));
                   System.out.println("CODOP="+ensamblador.getCodops().get(ensamblador.getCodops().size()-1));
                   System.out.println("OPERANDO="+ensamblador.getOperandos().get(ensamblador.getOperandos().size()-1));
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
        
        this.getCodops().add(palabras[0]);
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]).append(" ");
            this.getOperandos().add(s.toString());
        }
    }

    public boolean  isComentario(String linea) {
         return this.separarEnPalabras(linea)[0].startsWith(";");
    }
    
     private boolean isETIQUETA(String palabra ) {
        return  palabra.matches("^[a-zA-Z0-9_]{0,7}$");
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
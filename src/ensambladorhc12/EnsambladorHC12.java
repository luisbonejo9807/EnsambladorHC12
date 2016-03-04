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
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/**
 *
 * @author hp
 */
public class EnsambladorHC12 {

    final static String FILE_NAME ="src\\ensambladorhc12\\P1ASM.TXT";
    private static String contenidoDeArchivo;
    private static ArrayList<String>  comentarios;
    private static ArrayList<String>  codops;
    private static ArrayList<String>  operandos;
    private static ArrayList<String>  etiquetas;
    private static ArrayList<String>  lineasErroneas;

    

    public EnsambladorHC12() 
    {
        this.setComentarios(new ArrayList<>());
        this.setCodops(new ArrayList<>());
        this.setOperandos(new ArrayList<>());
        this.setEtiquetas(new ArrayList<>());
    }
    
    
    
    
    public static void main(String[] args) {
        EnsambladorHC12 ensamblador = new EnsambladorHC12();
        try 
        {
 //        1. Capturar el siguiente ejemplo con un editor de texto puro
//        (por ejemplo, el “bloc de notas”) y grabar el archivo
//        con el nombre P1ASM.TXT. Cada una de las líneas capturadas es
//        un ejemplo ficticio de un código en lenguaje ensamblador para 
//        la arquitectura HC12.
//          System.out.println("1: CONTENIDO ARCHIVO:");
          String contenido = ensamblador.readFile(FILE_NAME,StandardCharsets.UTF_8);
          
//        2.Al capturar el ejemplo utilizar espacios en blanco y 
//        tabuladores de manera alternada para crear varias opciones de 
//        separación de palabras. 
          
//          System.out.println("------------------------------");
//          System.out.println("2: PALABRAS SEPARADAS EN BLANCO:");
//          
//          for (String palabra : ensamblador.separarEnPalabras(contenido))
//                System.out.println(palabra);
          
          
        //3.	Cada una de estas líneas puede ser de dos formas:
        //a.	Línea de comentario.
        //b.	Línea de ETIQUETA, CODIGO DE OPERACIÓN (CODOP) y OPERANDO.
//          System.out.println("------------------------------");
//          System.out.println("3-6: ETIQUETA, CODIGO DE OPERACIÓN (CODOP) y OPERANDO:");
           for (String linea : ensamblador.separarEnLineas(contenido))
           {
               
        //4.   Las líneas de comentario tienen las siguientes reglas de escritura.
               if(ensamblador.isComentario(linea))
               {
                   ensamblador.getComentarios().add(linea);
                   System.out.println("COMENTARIO="+ensamblador.getComentarios().get(ensamblador.getComentarios().size()-1));
               }
               //5.   Las líneas que están formadas por ETIQUETA,
               //a.	ETIQUETAS:
               else if(new String(""+linea.charAt(0)).matches("^[a-zA-Z]")&& ensamblador.isETIQUETA(ensamblador.separarEnPalabras(linea)[0]))
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
        } catch (IOException ex) {Logger.getLogger(EnsambladorHC12.class.getName()).log(Level.SEVERE, null, ex);}
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
        //    CODOP 
        //b.  CODOPS (códigos de operación):
        this.getCodops().add(palabras[0]);
        //y    OPERANDO tienen las siguientes reglas de escritura:
        //c.	OPERANDOS:
        if(palabras.length>1)
        {
            StringBuilder s = new StringBuilder("");
            for (int i = 1; i < palabras.length; i++) 
               s.append(palabras[i]+" ");
            this.getOperandos().add(s.toString());
        }
//        for (int i = 0; i < palabras.length; i++) 
//        {
//            if(this.isCODOP(palabras[i]))
//                this.getCodops().add(palabras[i]);
//         
//            else
//            {
//                StringBuilder s = new StringBuilder("");
//                for (int j = i; j < palabras.length; j++) 
//                   s.append(palabras[j]+" ");
//                this.getOperandos().add(s.toString());
//                break;
//            }
//        }
    }

    public boolean  isComentario(String linea) {
    //a.	Comienzan con el carácter de “ ; ”.
    //b.	Este carácter de “ ; “ solo puede estar en la primera posición de la línea.
    //c.	Después del carácter de “ ; “ pueden seguirle cualquier carácter. En caso de letras pueden ser indistintamente mayúsculas y minúsculas.
    //d.	No existe un límite de caracteres por cada comentario.
    //e.	El delimitador de la línea de comentario es el retorno de carro (“enter”).
         return this.separarEnPalabras(linea)[0].startsWith(";");
    }
    
     private boolean isETIQUETA(String palabra ) {
    //i.	Comienzan con letra mayúsculas o minúsculas (son válidos los dos casos).
    //ii.	Esta primera letra se representa en la primera posición de la línea.
    //iii.	Después de la primera letra le pueden seguir más letras, números (0 .. 9 ) o guiones bajos “ _ “.
    //iv.	Su longitud es de 8 caracteres máximo.
    //v.	Cualquier otro carácter representado es un error.
        return  palabra.matches("^[a-zA-Z0-9_]{0,7}$");
    }

    private boolean isOPERANDO(String linea) {
    //i.	Pueden comenzar con cualquier carácter (en las siguientes prácticas se revisaran las excepciones).
    //ii.	Pueden tener cualquier longitud
        
        return true;
    }

    private boolean isCODOP(String palabra) {
    //i.	Comienzan con letra mayúscula o minúscula (son válidos los dos casos).
    //ii.	Después de la primera letra le pueden seguir más letras y el “ . “. El carácter de “.” solo puede ser representado una única vez.
    //iii.	Su longitud es de 5 caracteres máximo.
    //iv.	Cualquier otro carácter es un error.

        if(palabra.length()>5)
            return false;
        if(palabra.matches("^[a-zA-Z][a-zA-Z\\\\.]+$"))
        {
            if(palabra.length()==1)
                return true;
            if(palabra.split(".").length>2)
                return false;
            else if(palabra.substring(1,palabra.length()).matches("^[a-zA-Z\\.]+$"))
                return true;
            else 
                return false;
        }
        else
            return false;
    }
    
     private void imprimirVariables() {
        System.out.println("------------------------------");
        System.out.println("COMENTARIOS");
        for (String comentario : this.getComentarios())
            System.out.println(comentario);
        System.out.println("------------------------------");
        System.out.println("ETIQUETAS");
        for (String etiqueta : this.getEtiquetas())
            System.out.println(etiqueta);
        System.out.println("------------------------------");
        System.out.println("CODOPS");
        for (String codops : this.getCodops())
            System.out.println(codops);
        System.out.println("------------------------------");
        System.out.println("OPERANDOS");
        for (String operando : this.getOperandos())
            System.out.println(operando);
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
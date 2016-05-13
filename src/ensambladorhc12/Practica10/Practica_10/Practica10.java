/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

import java.util.ArrayList;

/**
 *
 * @author hp
 */
public class Practica10 {

    public static String procesarPasoDos(String contenidoPasoDos, String FileName) {
        StringBuilder codigo = new StringBuilder();
        
        S1 s1 = null;
        String[] lineas = contenidoPasoDos.split("\n");
        for (int i = 0; i < lineas.length; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            if(tokens[2].toUpperCase().equals("EQU")&& s1 != null )
            {
                continue;
            }
            else if(tokens[2].toUpperCase().equals("ORG"))
            {
                //codigo.append("INICIO\n");
                codigo.append(Practica10.procesarS0(FileName));
                codigo.append("\n");
            }
            else if(tokens[2].toString().equals("END"))
            {
                if(s1!= null)
                {
                    codigo.append(s1.getContenido());
                    codigo.append("\n\n");
                }
                s1 = null;
                //codigo.append("\nFIN\n");
                codigo.append("S903000FC");
            }
            else if(tokens.length == 5)
            {
                if(s1 == null)
                {
                    s1 = new S1("1");
                    s1.setDireccion(tokens[0]);
                     s1.setLongitud(s1.getLongitud()+tokens[4].length());
                    s1.setCodigoMaquina(tokens[4]);
                }   
                else if((s1.getLongitud()+tokens[4].length()) <=32  )
                {
                    s1.setLongitud(s1.getLongitud()+tokens[4].length());
                    s1.setCodigoMaquina(s1.getCodigoMaquina()+tokens[4]);
                }
                else
                {
                    int longTmp = 32- s1.getCodigoMaquina().length();
                    s1.setLongitud(s1.getLongitud()+32- s1.getCodigoMaquina().length());
                    s1.setCodigoMaquina(s1.getCodigoMaquina()+tokens[4].substring(0, 32- s1.getCodigoMaquina().length() ));
                    codigo.append(s1.getContenido());
                    codigo.append("\n\n");
                    s1 = new S1("1");
                    s1.setDireccion(Long.toHexString(Long.parseLong(tokens[0],16)+Long.parseLong(Long.toHexString(longTmp/2),16)));
                    s1.setLongitud(((longTmp/2)/2));
                    s1.setCodigoMaquina(tokens[4].substring(longTmp));
                }
            }
            else if(s1!= null)
            {
                    codigo.append(s1.getContenido());
                    codigo.append("\n\n");
                    s1 = null;
            }
        }
        return codigo.toString();
    }

    private static String procesarS0(String FileName) {
        S1 s0 = new S1("0");
//        if(FileName.length()<9)
//            codigo.append("0");
//        codigo.append(FileName.length()+1);
        s0.setDireccion("0000");
        
        StringBuilder codigo = new StringBuilder();
        codigo.append(Long.toHexString(FileName.charAt(0)).toUpperCase());
        codigo.append("3A");
        codigo.append("20");
        
        for (char caracter : FileName.substring(FileName.lastIndexOf("\\")+1).toCharArray())
        {
           codigo.append(Long.toHexString(caracter).toUpperCase());
        }
        codigo.append("0A");        
        s0.setCodigoMaquina(codigo.toString());
        
        s0.setLongitud(s0.getCodigoMaquina().length());
        
        return s0.getContenido();
    }
    
}

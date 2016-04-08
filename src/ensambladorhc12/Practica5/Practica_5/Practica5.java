/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

class Practica5 {
    
    //Práctica no. 5. Obtener el código máquina de los direccionamientos simples inherente,
    //inmediato, directo y extendido utilizando valores numéricos. Modificar la práctica número 4 
    //para obtener estos modos de direccionamiento.

    static String procesarTemporal(String ASMTXT_FOLDER_NAME, String contenidoArchivoTemporalTxt) {
        StringBuilder g = new StringBuilder("");
        String[] lineas = contenidoArchivoTemporalTxt.split("\n");
        for (int i = 0; i < lineas.length-3; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            String valor = Practica5.buscarEnTABOP(ASMTXT_FOLDER_NAME, tokens[3],tokens[4],i);
            if(valor.contains("INH"))
            {
                valor = valor.replaceAll("\\s","");
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(valor.substring(4))).append("\n");
            }
            else if(valor.contains("DIR"))
            {
//                    a.	Revisar el valor del OPERANDO (de acuerdo a las reglas presentadas en las prácticas anteriores).
//                    b.	Transformar el valor en formato hexadecimal.
//                    c.	Concatenar el código máquina del TABOP con el código máquina recién calculado.
//                    d.	Complementar con ceros a la izquierda el código máquina del byte que se calcule.
                
                String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                if(v.length()== 1)
                    v = "0"+v;
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(valor.substring(4,6)+v)).append("\n");
            }
            else if (valor.contains("EXT"))
            {
                String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                if(v.length()== 1)
                    v = "0"+v;
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(4,6)).append(Procesador.agregarCeros4(v)).append("\n");
            }
            else if(valor.contains("IMM"))
            {
                String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4].substring(1))));
                int bytes_pendientes = Integer.valueOf(valor.split("\\|")[1]);
                if(bytes_pendientes == 1)
                {
                    if(v.length()== 1)
                        v = "0"+v;
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(valor.substring(6,8)+v)).append("\n");
                }                
                else
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(6,8)).append(Procesador.agregarCeros4(v)).append("\n");
            }
            else
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor).append("\n");
        }
        String[] tokens = lineas[lineas.length-3].split("\t");
        g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\n");
        return g.toString();
    }
    
    public static String buscarEnTABOP(String contenidoTABOPtxt, String codop, String operando, int LINE_NUMBER){
        
        for (String linea : contenidoTABOPtxt.split("\n")) 
        {
            if (linea.contains(codop.toUpperCase())) 
            {
               
               String[] tokens = linea.split("\\|");
               if((tokens[1].contains("SI") && operando.toUpperCase().equals("NULL")) ||
                   (tokens[1].contains("NO") && !operando.toUpperCase().equals("NULL")))
                    return "No se encontró en el TABOP";
               else if(operando.toUpperCase().equals("NULL"))
                    return "INH|"+tokens[3];
               else
               {
                    String s = Validador.validaTipoOPERANDO(operando, LINE_NUMBER, Integer.parseInt(tokens[5]));
                    String tipoOperando = s.split("\\|")[0];
                    if(tipoOperando.equals("DIR") && linea.contains("DIR"))
                        return "DIR|"+tokens[3];
                     else if(tipoOperando.equals("EXT") && linea.contains("EXT"))
                        return "EXT|"+tokens[3];
                     else if(tipoOperando.equals("IMM") && linea.contains("IMM"))
                        return "IMM|"+tokens[5]+"|"+tokens[3];
                     
               }
                
//                if(s.contains("|"))
//                {
//                  
//                    
//                    if(tipoOperando.equals("DIR") && linea.contains("DIR"))
//                        return "DIR|"+tokens[3];
//                    else if(tipoOperando.equals("ETX"))
//                    {
//                    1.	Buscar en el TABOP el valor de la variable CODOP.
//                    2.	Recuperar del TABOP el código máquina en formato hexadecimal.
//                    3.	Calcular el código máquina faltante:
//                    a.	Revisar el valor del OPERANDO (de acuerdo a las reglas presentadas en las prácticas anteriores).
//                    b.	Transformar el valor en formato hexadecimal.
//                    c.	Concatenar el código máquina del TABOP con el código máquina recién calculado.
//                    d.	Complementar con ceros a la izquierda el código máquina de los 2 bytes que se calcule.
//                    4.	Imprimir en pantalla por cada línea del archivo TEMPORAL el código máquina encontrado.
//                        return "EXT|"+tokens[3];
//                    }
//                    else if(tipoOperando.equals("IMM"))
//                    {
//                    1.	Buscar en el TABOP el valor de la variable CODOP.
//                    2.	Recuperar del TABOP el código máquina en formato hexadecimal.
//                    3.	Calcular el código máquina faltante:
//                    a.	Revisar el valor del OPERANDO (de acuerdo a las reglas presentadas en las prácticas anteriores).
//                    b.	Determinar si es de 1 o de 2 bytes, y generar un error en caso de que no proceda.
//                    c.	Transformar el valor en formato hexadecimal.
//                    d.	Concatenar el código máquina del TABOP con el código máquina recién calculado.
//                    e.	Complementar con ceros a la izquierda el código máquina del byte o bytes que se calculen.
//                    4.	Imprimir en pantalla por cada línea del archivo TEMPORAL el código máquina encontrado.

//                        return "IMM|"+tokens[3];
//                    }
//                }
                    //return (s.split("\\|")[1])+" de "+tokens[6]+" bytes\n";
            }
        }
        return "No encontrado";
    }
}
//Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n"

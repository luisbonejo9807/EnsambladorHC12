/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

import java.nio.charset.StandardCharsets;


class Practica6 {
    
    //Práctica no. 5. Obtener el código máquina de los direccionamientos simples inherente,
    //inmediato, directo y extendido utilizando valores numéricos. Modificar la práctica número 4 
    //para obtener estos modos de direccionamiento.

    static String procesarTemporal(String contenidoTabop, String contenidoArchivoTemporalTxt) {
        
        String valor_anterior = null;
        StringBuilder g = new StringBuilder("");
        String[] lineas = contenidoArchivoTemporalTxt.split("\n");
        for (int i = 0; i < lineas.length-3; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            
            String valor  = Validador.validaTipoDirectivaConstante(tokens[3],tokens[4], i);
            if(!valor.contains("ERROR"))
            {
                //FCC
                if(valor.contains("CONSC"))
                {
                    StringBuilder s = new StringBuilder("");
                    for (int j = 1; j < tokens[4].length()-1; j++) 
                    {
                        char token = tokens[4].charAt(j);    
                        s.append(Long.toHexString(0+token));
                    }
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(s.toString().toUpperCase()).append("\n");
                }
                //DW|DC.W|FDB
                else if(valor.contains("CONS2B"))
                {
                    String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                    if(v.length()== 1)
                        v = "0"+v;
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(v)).append("\n");
                }
                //DB|DC.B|FCB
                else if(valor.contains("CONS1B"))
                {
                    String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                    if(v.length()== 1)
                        v = "0"+v;
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(v).append("\n");
                }
                continue;
            }
            
            valor = Practica6.buscarEnTABOP(contenidoTabop, tokens[3],tokens[4],i);
           
            if(valor.contains("INH"))
            {
                valor = valor.replaceAll("\\s","");
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(valor.substring(4))).append("\n");
            }
            else if(valor.contains("DIR"))
            {
                String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                if(v.length()== 1)
                    v = "0"+v;
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(Procesador.agregarCeros4(valor.substring(4,6)+v)).append("\n");
            }
            else if (valor.contains("EXT"))
            {
                if(!Validador.validarETIQUETA(tokens[4]).contains("ERROR"))
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(4,6)).append(Procesador.agregarCeros4(valor_anterior)).append("\n");
                else
                {
                    String v = Long.toHexString(Long.valueOf(Procesador.convertirADecimalString(tokens[4])));
                    if(v.length()== 1)
                        v = "0"+v;
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(4,6)).append(Procesador.agregarCeros4(v)).append("\n");
                }
                
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
            
            valor_anterior = tokens[1];
            
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
                    String tipoOperando = Validador.validaTipoOPERANDO(operando, LINE_NUMBER, Integer.parseInt(tokens[5])).split("\\|")[0];
                    if(tipoOperando.equals("DIR") && linea.contains("DIR"))
                        return "DIR|"+tokens[3];
                     else if(tipoOperando.equals("EXT") && linea.contains("EXT"))
                        return "EXT|"+tokens[3];
                     else if(tipoOperando.equals("IMM") && linea.contains("IMM"))
                        return "IMM|"+tokens[5]+"|"+tokens[3];
                     
               }
            }
        }
        return "No encontrado";
    }
}
//Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n"

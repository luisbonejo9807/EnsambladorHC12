/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;


public class Practica9 {
    
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
                else if(valor.contains("RES1") || valor.contains("RES2"))
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\n");
                continue;
            }
            
            valor = Practica9.buscarEnTABOP(contenidoTabop, tokens[3],tokens[4],i);
           
            if(valor.contains("INH"))
            {
                valor = valor.replaceAll("\\s","");
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(4)).append("\n");
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
                {
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor.substring(4,6)).append(Procesador.agregarCeros4(Practica9.buscarEnCOntenidoDeArchivo(tokens[4], contenidoArchivoTemporalTxt))).append("\n");
                }
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
            else if(valor.contains("[IDX2]"))
            {
                String valor1 = valor.split("\\|")[1];
                valor1 = valor1.substring(0,valor1.length()-9);
                String valor2 = tokens[4].split(",")[1];
                valor2 = Procesador.convertirADecimalString("111"+Procesador.convertirRRaBinario(valor2.substring(0, valor2.length()-1))+"011");
                valor2 = Long.toHexString(Long.parseLong(valor2,2));
                String valor3  = tokens[4].split(",")[0].substring(1);
                valor3  = Long.toHexString(Long.parseLong(valor3));
                valor3  = Procesador.agregarCeros4(valor3);
                valor2 = valor2+valor3;
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2.toUpperCase()).append("\n");
            }
            else if(valor.contains("[D,IDX]"))
            {
               String valor1 = valor.split("\\|")[1];
               valor1 = valor1.substring(0,valor1.length()-3);
               String valor2 = tokens[4].split(",")[1];
               valor2 = Procesador.convertirADecimalString("111"+Procesador.convertirRRaBinario(valor2.substring(0, valor2.length()-1))+"111");
               valor2 = Long.toHexString(Long.parseLong(valor2,2));
               g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2.toUpperCase()).append("\n");
            }
            else if(valor.contains("IDX1"))
            {
                String registros[] = tokens[4].split(",");
                String valor1 = valor.split("x")[0].replaceAll("\\s+","").substring(5);

                String valor2 = "111";
                valor2 = valor2+Procesador.convertirRRaBinario(registros[1].toUpperCase());
                valor2 = valor2+"00";
                if(registros[0].contains("-"))
                    valor2 = valor2+"1";
                else
                    valor2 = valor2+"0";
                
                valor2 = Long.toHexString(Long.parseLong(valor2,2)).toUpperCase();
                
                String valor3 = Long.toHexString(Long.valueOf(registros[0])).toUpperCase();
                valor3 = valor3.substring(valor3.length()-2, valor3.length());
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2).append(valor3).append("\n");
            }
            else if(valor.contains("IDX2"))
            {
                String registros[] = tokens[4].split(",");
                String valor1 = valor.split("x")[0].replaceAll("\\s+","").substring(5);

                String valor2 = "111";
                valor2 = valor2+Procesador.convertirRRaBinario(registros[1]);
                valor2 = valor2+"010";
                valor2 = Long.toHexString(Long.parseLong(valor2,2)).toUpperCase();
                
                String valor3 = Long.toHexString(Long.valueOf(registros[0])).toUpperCase();
                valor3 = valor3.substring(valor3.length()-4, valor3.length());
                g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2).append(valor3).append("\n");
                
            }
            else if(valor.contains("IDX"))
            {
                String registros[] = tokens[4].split(",");
                String valor1 = valor.split("x")[0].replaceAll("\\s+","").substring(4);
                if(!registros[0].equals(""))
                {
                    if(tokens[4].contains("B")|| tokens[4].contains("b") || tokens[4].contains("D") || tokens[4].contains("d") || tokens[4].contains("A") || tokens[4].contains("a"))
                    {
                        //Indizado de acumulador
                        String rr = Procesador.convertirRRaBinario(registros[1]);
                        String aa;
                        switch(registros[0].toUpperCase().charAt(0))
                        {
                            case 'A':
                                aa = "00";
                            break;
                            case 'B':
                                aa = "01";
                            break;
                            case 'D':
                                aa = "10";
                            break;
                            default:
                                return "ERROR el registro es incorrecto para indizado de acumulador";
                                        
                        }
                        String valor2 = "111"+rr+"1"+aa;
                        valor2 = Long.toHexString(Long.parseLong(valor2,2)).toUpperCase();
                        g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2).append("\n");
                    }
                    else if(tokens[4].contains("+") || tokens[4].endsWith("-") || tokens[4].matches("^.*,-.*$"))
                    {
                        //Indizado de prepost
                        String p;
                        String rr;
                        if(registros[1].startsWith("+")||registros[1].startsWith("-"))
                        {
                            p = "0";
                            rr = Procesador.convertirRRaBinario(registros[1].substring(1));
                        }
                            
                        else
                        {
                            p = "1";
                            rr = Procesador.convertirRRaBinario(registros[1].substring(0,registros[1].length()-1));
                        }
                        
                        String valor2;
                        if(registros[1].contains("+"))
                            valor2 = Long.toBinaryString(Long.parseLong(registros[0])-1);
                        else
                            valor2 = Long.toBinaryString(Long.parseLong(registros[0])*-1);
                        if(valor2.length()==64)
                            valor2 = valor2.substring(valor2.length()-4, valor2.length());
                        else
                            valor2 = Procesador.agregarCeros4(valor2);
                        
                        valor2 = rr+"1"+p+valor2;
                        valor2 = Long.toHexString(Long.parseLong(valor2,2));
                        
                        valor2 = valor2.substring(valor2.length()-2, valor2.length()).toUpperCase();
                        
                        
                        g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2).append("\n");
                    }
                    else
                    {
                        //Indizado de 5 bits
                        String valor2 = Procesador.convertirRRaBinario(registros[1]);
                        valor2 = valor2.substring(valor2.length()-2, valor2.length());
                        valor2 = valor2+"0";
                        String valor3 = Long.toBinaryString(Long.parseLong(registros[0]));

                        if(valor3.length()== 64)
                            valor3 = valor3.substring(valor3.length()-5,valor3.length());
                        if(Long.parseLong(registros[0])<0)
                            valor2 = valor2+valor3.substring(valor3.length()-5, valor3.length());
                        else
                            valor2 = valor2+valor3;

                        valor2 = Long.toHexString(Long.parseLong(valor2,2));
                        if(valor2.length() == 1)
                            valor2 = "0"+valor2;
                        g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2.toUpperCase()).append("\n");
                    }
                }
                else
                {
                    //Indizado de 5 bits
                    String valor2 = Long.toHexString(Long.parseLong(Procesador.convertirRRaBinario(registros[1]),2));
                    
                    valor2 = valor2+"000000";
                    
                    valor2 = Long.toHexString(Long.parseLong(valor2,2));
                    if(valor2.length() == 1)
                        valor2 = "0"+valor2;
                    g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2.toUpperCase()).append("\n");
                }
            }
            else if(!tokens[3].toUpperCase().equals("EQU") && !Validador.validarETIQUETA(tokens[4]).contains("ERROR"))
            {
               String valor1 = valor.split("\\|")[1].replaceAll("\\s+","");
               String valor2 = Practica9.buscarValorEnTabSIM(contenidoArchivoTemporalTxt, tokens[4]);
               long suma;
               
               if(valor1.substring(0, 4).toUpperCase().matches("^[A-F0-9]+$"))
               {
                   valor1 = valor1.substring(0,4);
                     suma =Long.valueOf(Procesador.convertirADecimalString("$"+valor2)) -
                           Long.valueOf(Procesador.convertirADecimalString("$"+lineas[i+1].split("\t")[1]));
                   if(suma< -32768  || suma > 32767)
                       return "ERROR: RANGO DEL DESPLAZAMIENTO NO Valido";
                   valor2 = Long.toHexString(suma);
                   if(valor2.length()==16)
                        valor2 = valor2.substring(12);
                   Procesador.agregarCeros4(valor2);
               }
                    
               else
               {
                    valor1 = valor1.substring(0,2);
                    suma  =Long.valueOf(Procesador.convertirADecimalString("$"+valor2)) -
                           Long.valueOf(Procesador.convertirADecimalString("$"+lineas[i+1].split("\t")[1]));
                    if(suma< -128 || suma > 127)
                       return "ERROR: RANGO DEL DESPLAZAMIENTO NO Valido";
                    valor2 = Long.toHexString(suma);
                    if(valor2.length()==16)
                         valor2 = valor2.substring(14);
               }
               g.append(tokens[1]).append("\t").append(tokens[2]).append("\t").append(tokens[3]).append("\t").append(tokens[4]).append("\t").append(valor1).append(valor2.toUpperCase()).append("\n");
           
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
        if(codop.toUpperCase().equals("EQU"))
            return "";
        for (String linea : contenidoTABOPtxt.split("\n")) 
        {
            
            if (linea.contains(codop.toUpperCase())) 
            {
               String[] tokens = linea.split("\\|");
               if((tokens[1].contains("SI") && operando.toUpperCase().equals("NULL")) ||
                   (tokens[1].contains("NO") && !operando.toUpperCase().equals("NULL")))
                    return "";
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
                    else if(tipoOperando.equals("[IDX2]") && linea.contains("[IDX2]"))
                        return "[IDX2]|"+tokens[3];
                    else if(tipoOperando.equals("[D,IDX]") && linea.contains("[D,IDX]"))
                        return "[D,IDX]|"+tokens[3];
                    else if(tipoOperando.equals("IDX") && linea.contains("IDX|"))
                        return "IDX|"+tokens[3];
                    else if(tipoOperando.equals("IDX1") && linea.contains("IDX1"))
                        return "IDX1|"+tokens[3];
                    else if(tipoOperando.equals("IDX2") && linea.contains("IDX2|"))
                        return "IDX2|"+tokens[3];
                    else if(!Validador.validarETIQUETA(operando).contains("ERROR")&& !tipoOperando.equals("EXT"))
                        return "REL|"+tokens[3];
               }
            }
        }
        return "No encontrado";
    }
    
    public static String buscarValorEnTabSIM(String contenidoArchivoTxt, String etiqueta){
        String[] lineas = contenidoArchivoTxt.split("\n");
        for (int i = 0; i < lineas.length-3; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            if(tokens[2].contains(etiqueta))
                return Procesador.quitaCeros(tokens[1]);
        }
        
        return "ERROR No encontrado";
        
    }
    
    public static String buscarEnCOntenidoDeArchivo(String etiqueta, String contenidoArchivoTemporalTxt){
        String[] lineas = contenidoArchivoTemporalTxt.split("\n");
        for (int i = 0; i < lineas.length-3; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            if(tokens[2].toUpperCase().equals(etiqueta.toUpperCase()))
                return tokens[1];
        }
        return "ERROR No encontrado";
    }
}
//Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n"

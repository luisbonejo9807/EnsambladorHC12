package ensambladorhc12;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tabop {
    
    public static String buscarEnTABOP(String ASMTXT_FOLDER_NAME, String palabra, String hasOperando, int LINE_NUMBER){
        String contenidoTABOPtxt = null;
        try
        {
            contenidoTABOPtxt = new String(Files.readAllBytes(Paths.get(ASMTXT_FOLDER_NAME+"/TABOP.txt")), StandardCharsets.UTF_8);
        }catch (IOException ex) {Logger.getLogger(Tabop.class.getName()).log(Level.SEVERE, null, ex);}
        if(palabra.toUpperCase().equals("ORG"))
            return "DIR_INIC";
        else if(palabra.toUpperCase().equals("EQU"))
            return "VALOR_EQU";
        //if(un chingo de palabras) se tiene que quitar por otras validaciones
        
        for (String linea : contenidoTABOPtxt.split("\n")) 
        {
            if (linea.contains(palabra.toUpperCase())) 
            {
                String[] tokens = linea.split("\\|");
                if((tokens[1].contains("SI") && hasOperando.equals("null")) ||
                   (tokens[1].contains("NO") && !hasOperando.equals("null")))
                    return "No se encontró en el TABOP";
                else
                {
//Modo de direccionamiento:"+tokens[2]+"\tCódigo Máquina:"+tokens[3]+"\tTotal de bytes calculados:"+tokens[4]+"\tTotal de bytes por calcular:"+tokens[5]+"\tSuma total de bytes:"+tokens[6]+"\n"
                    String s = Validador.validaTipoDirectivaConstante(palabra, hasOperando, LINE_NUMBER);
                    if(s.contains("ERROR"))
                        s = Validador.validaTipoOPERANDO(hasOperando, LINE_NUMBER, Integer.parseInt(tokens[5]));
                    else 
                        return s;
                    if(hasOperando.toUpperCase().equals("NULL"))
                        //return "Inherente\t"+tokens[6]+" bytes\n";          
                        return "CONTLOC|"+tokens[6];
                    else if(tokens[2].equals("REL"))
                        return Validador.validaRelativoDe8y16Bits(hasOperando, Integer.parseInt(tokens[5]), tokens[6], LINE_NUMBER)+"|"+tokens[6];
                    else if(s.contains("ERROR") && s.contains("|"))
                        return s.split("\\|")[1];
                    else if(s.contains("ERROR"))
                        return s;
                    else if(linea.contains(s.split("\\|")[0]))
                        return (s.split("\\|")[1])+"|"+tokens[6];
                        //return (s.split("\\|")[1])+" de "+tokens[6]+" bytes\n";
                }
            }
        }
        return Validador.validaTipoDirectivaConstante(palabra, hasOperando, LINE_NUMBER);
    }
}

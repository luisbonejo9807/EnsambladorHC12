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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


//Poner como prioridad el analisis de decimales
//Poner como prioridad el direccionamiento [
//Agregar tipo de base en analisis de rango


public final class EnsambladorHC12Raw {
    
    private final String TEMPORAL_FILE_NAME = "/P4TMP.txt";    
    private final String FOLDER_ERRORS = "/errores";
    private final String TABOP_FILE_NAME = "/TABOP.TXT";
    private final String TABSIM_FILE_NAME = "/TABSIM.TXT";
    
    private String ASMTXT_FILE_NAME;
    private String ASMTXT_FOLDER_NAME;
    private String ENSAMBLADOR_ACREAR_FILE_NAME;
    
    private String contenidoDeArchivoASMProcesadotxt;
    private String contenidoDeArchivoASMTxt;
    private String contenidoTABOPtxt;
    private String contenidoArchivoTemporalTxt;
    private String contenidoTabsim;
    

   
    public EnsambladorHC12Raw(String ASMTXT_FILE_NAME, String ASMTXT_FOLDER_NAME, String ENSAMBLADOR_ACREAR_FILE_NAME) 
    {
        this.setASMTXT_FILE_NAME(ASMTXT_FILE_NAME);
        this.setASMTXT_FOLDER_NAME(ASMTXT_FOLDER_NAME);
        this.setENSAMBLADOR_ACREAR_FILE_NAME("/"+ENSAMBLADOR_ACREAR_FILE_NAME);
        new File(ASMTXT_FOLDER_NAME+getFOLDER_ERRORS()).mkdirs();
    }
    
    public void inicializarVariables(){
        try 
        {            
            this.setContenidoTABOPtxt(this.readFile(this.getASMTXT_FOLDER_NAME()+this.getTABOP_FILE_NAME(),StandardCharsets.UTF_8));
            this.setContenidoDeArchivoASMTxt(this.readFile(this.getASMTXT_FILE_NAME(),StandardCharsets.ISO_8859_1));
            this.setContenidoDeArchivoASMProcesadotxt(this.obtenerResultados(this.getContenidoDeArchivoASMTxt()));
            this.crearArchivosDeErrores(this.getContenidoDeArchivoASMProcesadotxt());
            if(this.getContenidoDeArchivoASMProcesadotxt().contains("ERROR"))
            {
                this.setContenidoArchivoTemporalTxt("El Contenido del archivo ASM tiene errores terminando el programa");
                return;
            }
            
            this.setContenidoArchivoTemporalTxt(Temporal.procesarTemporal(this.getContenidoDeArchivoASMProcesadotxt()));
            this.crearArchivosDeErrores(this.getContenidoArchivoTemporalTxt());
            if(this.getContenidoArchivoTemporalTxt().contains("ERROR"))
            {
                this.setContenidoTabsim("El Contenido del archivo Temporal tiene errores terminando el programa");
                return;
            }
            this.setContenidoTabsim(Tabsim.procesarTabsim(this.getContenidoArchivoTemporalTxt()));
            //Ensamblador a crear
            Files.write(Paths.get(this.getASMTXT_FOLDER_NAME()+this.getENSAMBLADOR_ACREAR_FILE_NAME()+".txt"),new ArrayList<String>(Arrays.asList(this.getContenidoDeArchivoASMProcesadotxt().split("\n"))),Charset.forName("UTF-8"));
            //Temporal a crear
            Files.write(Paths.get(this.getASMTXT_FOLDER_NAME()+this.getTEMPORAL_FILE_NAME()+".txt"),new ArrayList<String>(Arrays.asList(this.getContenidoArchivoTemporalTxt().split("\n"))),Charset.forName("UTF-8"));
            //Tabsim a crear
            if(!this.getContenidoTabsim().equals(""))
                Files.write(Paths.get(this.getASMTXT_FOLDER_NAME()+this.getTABSIM_FILE_NAME()+".txt"),new ArrayList<String>(Arrays.asList(this.getContenidoTabsim().split("\n"))),Charset.forName("UTF-8"));
            else
                this.setContenidoTabsim("No es necesario crear el TABSIM");
        } catch (IOException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
        //this.setContenidoDeArchivoASM(Files.readAllLines(Paths.get(this.getASMTXT_FILE_NAME()),StandardCharsets.ISO_8859_1).toArray(new String[0]));
        //this.getContenidoTabSIM()
    }
    
    private String obtenerResultados(String contenidoDeArchivoASMTxt) {
        StringBuilder s = new StringBuilder();
        String finDeArchivoASM = null;
        String etiqueta;
        
        if(contenidoDeArchivoASMTxt.trim().isEmpty())
            s.append("\n\tERROR El archivo no contiene nada");
        else
        {
            String[] lineas = contenidoDeArchivoASMTxt.split("\n");
            
            for (int i = 0; i < lineas.length; i++) 
            {   
                String[] palabra = Procesador.separarEnPalabras(lineas[i]);
                 if(i == lineas.length-1 )
                 {
                     if(palabra.length == 1)
                     {
                         if( palabra[0].toUpperCase().matches("^END$"))
                             s.append("null\tEND\tNULL\tCONTLOC");
                         else
                             s.append("ERROR no existe END");
                     }
                     else if(palabra.length == 2)
                     {
                        String endEtiqueta = Validador.validarETIQUETA(palabra[0]);
                        if(endEtiqueta.contains("ERROR"))
                            finDeArchivoASM = "ERROR La etiqueta del END no es valida";
                        else if( palabra[1].toUpperCase().matches("^END$") )
                             s.append(palabra[0]).append("\t").append("END\tNULL\tCONTLOC\t");
                        else
                             s.append("ERROR no existe END");
                     }
                     else if(palabra.length == 3)
                     {
                        if(!palabra[2].toUpperCase().equals("NULL"))
                            finDeArchivoASM = "ERROR el operando del END debe ser nulo";
                        String endEtiqueta = Validador.validarETIQUETA(palabra[0]);
                        if(endEtiqueta.contains("ERROR"))
                            finDeArchivoASM = "ERROR La etiqueta del END no es valida";
                        else if( palabra[1].toUpperCase().matches("^END$") )
                             s.append(palabra[0]).append("\t").append("END\tNULL\tCONTLOC\t");
                        else
                             s.append("ERROR no existe END");
                     }
                     else if(palabra.length > 3)
                        s.append("ERROR el END tiene mas de 3 tokens");
                     else
                         finDeArchivoASM = "\nERROR El archivo no termina con END";
                     break;
                 }
                 if (lineas[i].trim().isEmpty()) 
                      s.append("\n\tERROR linea Vacía");
                 else if(Validador.isComentario(lineas[i])) 
                     s.append(lineas[i]).append("\n");
                 else 
                 {
                     if(Validador.hasETIQUETA(lineas[i]))
                     {
                         etiqueta = Validador.validarETIQUETA(palabra[0])+"\t";
                         if(etiqueta.contains("ERROR "))
                             s.append(etiqueta);
                         else
                             s.append(etiqueta);
                         if(palabra.length>1)
                             s.append(Validador.validarLinea(this.getASMTXT_FOLDER_NAME(),i+1, Arrays.copyOfRange(palabra, 1, palabra.length))).append("\n");
                         else
                             s.append("null\tERROR Si existe una etiqueta debe existir otro token más").append("null\n");
                     }
                     else
                         s.append("null\t").append(Validador.validarLinea(this.getASMTXT_FOLDER_NAME(),i+1, palabra)).append("\n");
                 }
                 if(finDeArchivoASM!=null)
                     s.append(finDeArchivoASM);
            }
        }
        return s.toString();
    }
    
    public String readFile(String path, Charset encoding) throws IOException{ 
         byte[] encoded = Files.readAllBytes(Paths.get(path));
         return new String(encoded, encoding);
    }
    
    public String getENSAMBLADOR_ACREAR_FILE_NAME() {
        return ENSAMBLADOR_ACREAR_FILE_NAME;
    }

    public void setENSAMBLADOR_ACREAR_FILE_NAME(String ENSAMBLADOR_ACREAR_FILE_NAME) {
        this.ENSAMBLADOR_ACREAR_FILE_NAME = ENSAMBLADOR_ACREAR_FILE_NAME;
    }
    
    public String getASMTXT_FILE_NAME() {
        return ASMTXT_FILE_NAME;
    }

    public void setASMTXT_FILE_NAME(String ASMTXT_FILE_NAME) {
        this.ASMTXT_FILE_NAME = ASMTXT_FILE_NAME;
    }

    public String getASMTXT_FOLDER_NAME() {
        return ASMTXT_FOLDER_NAME;
    }

    public void setASMTXT_FOLDER_NAME(String ASMTXT_FOLDER_NAME) {
        this.ASMTXT_FOLDER_NAME = ASMTXT_FOLDER_NAME;
    }

    public String getFOLDER_ERRORS() {
        return FOLDER_ERRORS;
    }    

    public String getContenidoDeArchivoASMProcesadotxt() {
        return contenidoDeArchivoASMProcesadotxt;
    }
    
     public void setContenidoDeArchivoASMProcesadotxt(String contenidoDeArchivoASMProcesadotxt) {
        this.contenidoDeArchivoASMProcesadotxt = contenidoDeArchivoASMProcesadotxt;
    }
    
    

    public String getContenidoDeArchivoASMTxt() {
        return contenidoDeArchivoASMTxt;
    }
    
    public void setContenidoDeArchivoASMTxt(String contenidoDeArchivoASMASMTxt) {
        this.contenidoDeArchivoASMTxt = contenidoDeArchivoASMASMTxt;
    }

   
    public String getContenidoTABOPtxt() {
        return contenidoTABOPtxt;
    }
    
    public void setContenidoTABOPtxt(String contenidoTABOPtxt) {
        this.contenidoTABOPtxt = contenidoTABOPtxt;
    }
    
    public String getTEMPORAL_FILE_NAME() {
        return TEMPORAL_FILE_NAME;
    }

    public String getTABOP_FILE_NAME() {
        return TABOP_FILE_NAME;
    }

    public String getContenidoArchivoTemporalTxt() {
        return contenidoArchivoTemporalTxt;
    }
    
    public void setContenidoArchivoTemporalTxt(String contenidoArchivoTemporalTxt) {
        this.contenidoArchivoTemporalTxt = contenidoArchivoTemporalTxt;
    }

    public String getTABSIM_FILE_NAME() {
        return TABSIM_FILE_NAME;
    }

   public void crearArchivosDeErrores(String FILE_CONTENT){
       if(FILE_CONTENT.contains("ERROR"))
       {
           String lineas[] = FILE_CONTENT.split(",");
           for (int i = 0; i < lineas.length; i++) 
           {
               String linea = lineas[i];
               
               if(linea.contains("ERROR"))
               {
                   try(PrintWriter out = new PrintWriter(this.getASMTXT_FOLDER_NAME()+this.getFOLDER_ERRORS()+"/"+i+".txt"))
                   {
                       out.println(linea+":Linea "+i);
                       out.close();
                   } 
                   catch (FileNotFoundException ex) {Logger.getLogger(EnsambladorHC12Raw.class.getName()).log(Level.SEVERE, null, ex);}
               }
               
           }
           
       }
    }

    /**
     * @return the contenidoTabsim
     */
    public String getContenidoTabsim() {
        return contenidoTabsim;
    }

    /**
     * @param contenidoTabsim the contenidoTabsim to set
     */
    public void setContenidoTabsim(String contenidoTabsim) {
        this.contenidoTabsim = contenidoTabsim;
    }
}        

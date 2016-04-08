/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author oscar
 */
public final class Tabsim {

    static String procesarTabsim(String contenidoArchivoTemporalTxt) {
        StringBuilder g = new StringBuilder("");
        String[] lineas = contenidoArchivoTemporalTxt.split("\n");
        for (int i = 0; i < lineas.length-2; i++) 
        {
            String[] tokens = lineas[i].split("\t");
            if(tokens[0].contains("EQU"))
                tokens[0] = "EQU (ETIQUETA ABSOLUTA)\t";
            else
                tokens[0] = "CONTLOC (ETIQUETA RELATIVA)";
            if(!tokens[2].toUpperCase().equals("NULL") && (contenidoArchivoTemporalTxt.split(tokens[2], -1).length-1)<=1)
                g.append(tokens[0]).append("\t").append(tokens[2]).append("\t").append(tokens[1]).append("\n");
        }
        return g.toString();
    }
    private final String TABSIM = "/TABSIM.txt";    
    
    public String getTABSIM() {
        return TABSIM;
    }
    
    private Tabsim(){
    }
    
}

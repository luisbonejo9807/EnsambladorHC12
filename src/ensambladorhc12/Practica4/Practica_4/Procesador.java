/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author oscar
 */
public final class Procesador {

    private Procesador() {
    }
    
    public static String[] separarEnPalabras(String contenido) {
        return contenido.trim().split("\\s++");
    }
    
    
    public static String convertirADecimalString(String palabra) {
        if(Validador.isDECIMAL(palabra))
            return palabra;
        else if(palabra.startsWith("$"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo hexadecimal debe tener minimo 2 caracteres";
            if(Validador.isHEX(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 16));
            return "ERROR Base hexadecimal invalida, Caracteres validos  A-F 0-9";
        }
        else if(palabra.startsWith("%"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo binaria debe tener minimo 2 caracteres";
            if(Validador.isBINARY(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 2));
            return "ERROR Base binaria invalida, Caracteres validos  0-1";
        }
        else if(palabra.startsWith("@"))
        {
            if(palabra.length()==1)
                return "ERROR Las bases del tipo octal debe tener minimo 2 caracteres";
            if(Validador.isOCTAL(palabra.substring(1)))
                return Long.toString(Long.parseLong(palabra.substring(1), 8));
            return "ERROR Base octal invalida, Caracteres validos  0-8";
        }
        return "ERROR Base decimal invalida, Caracteres validos  0-9";
    }
    
    public static String quitaCeros(String substring) {
        if(substring.matches("^-?[0-9]*$"))
            return substring.replaceFirst("^0+(?!$)", "");
        else if(substring.length()>1)
            return substring.substring(1).replaceFirst("^0+(?!$)", "");
        return "";
    }
    
    public static String agregarCeros(Long CONT_LOC) {
        return ("0000" + Long.toHexString(CONT_LOC).toUpperCase()).substring(Long.toHexString(CONT_LOC).toUpperCase().length());
    }
    
    public static String agregarCeros(String CONT_LOC) {
        return ("0000" + CONT_LOC.toUpperCase()).substring(CONT_LOC.toUpperCase().length());
    }
}

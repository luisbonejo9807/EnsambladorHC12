/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author hp
 */
public class S1 {
    
    private long longitud = 0;
    private String direccion;
    private String codigoMaquina;
    private String tipo;
    
    public S1(String tipo){
        this.setTipo("S"+tipo);
    }
    
    public String getContenido() {
        StringBuilder s = new StringBuilder();
        s.append(this.getTipo());
        if(this.getLongitud()== 0)
            this.setLongitud(2);
        String longHex = Long.toHexString((this.getLongitud()/2)+3);
        if(longHex.length()==1)
            s.append("0");
        s.append(longHex);
        s.append(this.getDireccion());
        s.append(this.getCodigoMaquina());
        
        s.append(this.calcularChecksum(longHex));
        
        return s.toString();
    }
    
    public String calcularChecksum(String longitud){
        StringBuilder s = new StringBuilder();
        long suma= 0;
        suma += Long.parseLong(longitud,16); 
        suma += Long.parseLong(this.getDireccion().substring(0,2),16); 
        suma += Long.parseLong(this.getDireccion().substring(2,4),16); 
        char[] caracteres = this.getCodigoMaquina().toCharArray();
        for (int i = 0; i < this.getCodigoMaquina().length(); i+=2) 
        {
            suma += Long.parseLong(""+caracteres[i]+caracteres[i+1],16);
        }
        
         suma= ~suma;
        String checkem = Long.toHexString(suma);
        if(suma<0)
            s.append(checkem.substring(checkem.length()-2, checkem.length()));
        else
            s.append(Long.toHexString(suma));
        s.append("\n");
        return s.toString();
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    /**
     * @return the codigoMaquina
     */
    public String getCodigoMaquina() {
        return codigoMaquina;
    }

    /**
     * @param codigoMaquina the codigoMaquina to set
     */
    public void setCodigoMaquina(String codigoMaquina) {
        this.codigoMaquina = codigoMaquina;
    }
    
    /**
     * @return the longitud
     */
    public long getLongitud() {
        return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(long longitud) {
        this.longitud = longitud;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

package it.toscana.regione.medici.common.utils.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.toscana.regione.medici.dto.CalcoloTotaleServizioDto;

public class CalcoloTotaleServizioDtoSerializer extends StdSerializer<CalcoloTotaleServizioDto> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3698643752306724936L;

	public CalcoloTotaleServizioDtoSerializer() {
        this(null);
    }
   
    public CalcoloTotaleServizioDtoSerializer(Class<CalcoloTotaleServizioDto> t) {
        super(t);
    }
    
    @Override
    public void serialize(
    		CalcoloTotaleServizioDto value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        
        jgen.writeObjectField("codServizio", value.getCodServizio());
        jgen.writeObjectField("periodi", value.getPeriodi() );
        jgen.writeNumberField("numeroGiorniAttivitaUltimiDieciAnni", value.getNumeroGiorniAttivitaConsiderataUltimiDieciAnni() );
        
        jgen.writeNumberField("punteggioTotale", value.getPunteggioTotale());
        
        boolean calcoloOre = value.hasServiziCalcoloOreDaConsiderare();
        
        jgen.writeStringField("calcoloGiorniOre", calcoloOre ? "ORE" : "GIORNI");
        
        if( calcoloOre )
        {
        	jgen.writeNumberField("numeroOreUtilizzatePerCalcolo", value.getNumeroOreUtilizzatePerCalcolo());	
        }
        else
        {
        	jgen.writeNumberField("numeroGiorniUtilizzatiPerCalcolo", value.getNumeroGiorniUtilizzatiPerCalcolo());	
        }
        jgen.writeEndObject();
    }
}

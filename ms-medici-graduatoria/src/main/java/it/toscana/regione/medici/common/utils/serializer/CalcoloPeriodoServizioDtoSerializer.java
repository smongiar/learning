package it.toscana.regione.medici.common.utils.serializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.toscana.regione.medici.dto.CalcoloPeriodoServizioDto;

public class CalcoloPeriodoServizioDtoSerializer extends StdSerializer<CalcoloPeriodoServizioDto> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3698643752306724936L;

	public CalcoloPeriodoServizioDtoSerializer() {
        this(null);
    }
   
    public CalcoloPeriodoServizioDtoSerializer(Class<CalcoloPeriodoServizioDto> t) {
        super(t);
    }
    
    @Override
    public void serialize(
    		CalcoloPeriodoServizioDto value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("numeroGiorniAttivitaSeUltimiDieciAnni", value.getNumeroGiorniAttivitaSeUltimiDieciAnni() );
        jgen.writeStringField("intervalloDataInizio", value.getIntervallo().getStartAsLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        jgen.writeStringField("intervalloDataFine", value.getIntervallo().getEndAsLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        jgen.writeObjectField("servizioOriginale", value.getServizioOriginale());
        if( ! value.isCalcoloOre() )
        {
        	jgen.writeNumberField("giorniDaUtilizzareSeCalcoloAGiorni", value.getGiorniDaUtilizzareSeCalcoloAGiorni() );	
        }
        jgen.writeEndObject();
    }
}

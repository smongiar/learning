package it.toscana.regione.medici.common.utils.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalDateSerializer extends StdSerializer<LocalDate>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9070225489433428359L;

	public LocalDateSerializer() {
        this(null);
    }
   
    public LocalDateSerializer(Class<LocalDate> t) {
        super(t);
    }
    
    @Override
    public void serialize(
    		LocalDate value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException 
    {
        jgen.writeString( value.format(DateTimeFormatter.ISO_LOCAL_DATE) );
    }
}

package it.toscana.regione.medici.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import it.toscana.regione.medici.common.utils.serializer.LocalDateSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectUtility 
{
	public static String toString(Object object, boolean recursive) 
	{
        if (object == null) return "null";

        Class<?> clazz = object.getClass();

        String sbObject = new String();
        while (clazz != null && !clazz.equals(Object.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    try {
                        f.setAccessible(true);
                        //sb.append(f.getName()).append(" = ").append(f.get(object)).append(",");
                        if (object!=null && f.get(object)!=null) {
                        	sbObject = (f.get(object)).toString();
                        }
                        
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!recursive) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return sbObject;
    }

	public static <T> String writeAsString(T object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try 
		{
			SimpleModule module = new SimpleModule();
			module.addSerializer(LocalDate.class, new LocalDateSerializer());
			mapper.registerModule(module);
			return mapper.writeValueAsString(object);
		} 
		catch (Exception e) 
		{
			log.error("Errore nel tentativo di generare la stringa JSON " + object , e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
}

package com.lsm.utils.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;



/**
 * https://stackoverflow.com/questions/29736660/jersey-parsing-java-8-date-time
 * @author olgaso
 *
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{

	@Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jg, 
            SerializerProvider sp) throws IOException, JsonProcessingException {
		if (dateTime != null){
			ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
			 Date dt = Date.from(zdt.toInstant());
			 jg.writeObject(dt);
		}
     } 
}

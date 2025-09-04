package com.lsm.utils.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
/**
 * Deserializing date into LocalDateTime
 * also may be replaced by XMLLocalDateConverter.class 
 * @author olgaso
 *
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize( JsonParser jp,
	                                  DeserializationContext dc) throws IOException, JsonProcessingException {

		ObjectCodec codec = jp.getCodec();
		TreeNode node = codec.readTree(jp);
		if (node != null){
			if (node instanceof TextNode){
				return LocalDateTime.parse(((TextNode) node).textValue(),DateTimeFormatter.ISO_ZONED_DATE_TIME);
			}else{
				return (new Date(((LongNode) node).longValue())).toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
			}
		}
		return null;
	}
}
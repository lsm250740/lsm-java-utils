package com.lsm.utils.adapters;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Deserializing date into TimeUnit deserialize from string and form
 * integer(order)
 * 
 * @author olgaso
 *
 */
public class TimeUnitDeserializer extends JsonDeserializer<TimeUnit> {

	@Override
	public TimeUnit deserialize(JsonParser jp,
								DeserializationContext dc) throws IOException, JsonProcessingException {

		ObjectCodec codec = jp.getCodec();
		TreeNode node = codec.readTree(jp);
		if (node != null) {
			try {
				if (node instanceof TextNode) {
					return deserialize((TextNode) node);
				} else {
					return deserialize(node.toString().toUpperCase());
				}

			} catch (Exception e) {
				throw new IllegalArgumentException(jp.getCurrentName() + " illegal value " + String.valueOf(node), e);
			}
		}
		return null;
	}

	private TimeUnit deserialize(TextNode node) {
		if (node.isInt()) {
			deserialize(node.intValue());
		}

		return deserialize(node.asText().toUpperCase());
	}

	private TimeUnit deserialize(int order) {
		return TimeUnit.values()[order];
	}

	private TimeUnit deserialize(String name) {
		if (!name.endsWith("S")) {
			name = name + "S";
		}

		return TimeUnit.valueOf(name);
	}

}
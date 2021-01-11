package name.qd.sbbet.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String parseObjectToString(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}
}

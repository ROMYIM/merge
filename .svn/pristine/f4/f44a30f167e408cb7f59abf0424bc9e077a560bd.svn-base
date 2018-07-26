package merge.aaa.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static Logger logger = Logger.getLogger(JsonUtil.class);

	public String toJson(Object object) {

		String value = null;
		try {
		    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm")); 
			value = objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.info("JsonProcessingException: " + e.toString());
		}
		return value;
	}

	public  <T> T toObject(String jsonString, Class<T> classType) {
		if (null == jsonString)
			return null;
		try {
		    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			return (T) objectMapper.readValue(jsonString, classType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		}

		return null;
	}

}

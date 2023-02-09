package in.co.bel.ims.initial.service.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringReader {
	public static String getValue(String json, String event, String key) {
		if (json != null && !json.isEmpty() && key != null & !key.isEmpty()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode actualObj = mapper.readTree(json);

				if (actualObj.isArray()) {
					for (final JsonNode objNode : actualObj) {
						JsonNode eventObj = objNode.get(ExternalPassConfigEnum.PUBLIC_PASS_EVENT_NAME.key);
						JsonNode nodeForGivenKey = objNode.get(key);
						if (eventObj != null && eventObj.asText().equals(event) && nodeForGivenKey != null)
							return nodeForGivenKey.asText();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return null;
	}

	public static String getFileContent(Resource resource) {
		if (resource != null && resource.exists()) {
			try {
				InputStream in = resource.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder stringBuilder = new StringBuilder();
				while (true) {
					String line = reader.readLine();
					if (line == null)
						break;
					stringBuilder.append(line);
				}
				reader.close();

				if (!stringBuilder.toString().isEmpty())
					return stringBuilder.toString();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return null;
	}
}

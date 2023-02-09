package in.co.bel.ims.initial.service.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * External File Reader 
 * @author BSTC
 *
 */
@ConfigurationProperties(prefix = "external")
public class ExternalPassConfigReader {
	private Resource file;

	public Resource getFile() {
		return file;
	}

	public void setFile(Resource file) {
		this.file = file;
	}
	
	
}

package in.co.bel.ims.initial.service.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImsRestTemplateGenerator {

	
	/*
	 * public static void main(String[] argv) throws IOException {
	 * 
	 * System.out.println(fileRename()); }
	 */

	protected static String fileRename() throws IOException {
		String path_to_folder = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-data\\src\\main\\java\\in\\co\\bel\\ims\\service\\controller";
		File my_folder = new File(path_to_folder);
		File[] array_file = my_folder.listFiles();
		for (int i = 0; i < array_file.length; i++) {
			if (array_file[i].isFile()) {
				File my_file = new File(path_to_folder + "\\" + array_file[i].getName());
				String long_file_name = array_file[i].getName();
				String[] my_token = long_file_name.split("[.]");
				String entityName = my_token[0];
				String repoClassName = entityName + "Repository";
				String serviceJavaFileName = entityName + "Controller.java";
				my_file.renameTo(new File(path_to_folder + "\\" + serviceJavaFileName));

				String content = "package in.co.bel.ims.service.controller;\r\n"
						+ "\r\n"
						+ "import org.springframework.web.bind.annotation.RequestMapping;\r\n"
						+ "\r\n"
						+ "import in.co.bel.ims.data.entity."+entityName+";\r\n"
						+ "import in.co.bel.ims.service.fwk.ImsServiceTemplate;\r\n"
						+ "import in.co.bel.ims.data.repository."+repoClassName+";\r\n"
						+ "\r\n"
						+"@RestController"
						+ "\r\n"
						+ "@RequestMapping(\"/"+formatServiceName(entityName)+"\")\r\n"
						+ "public class "+entityName+"Controller extends ImsServiceTemplate<"+entityName+", "+repoClassName+">{\r\n"
						+ "\r\n"
						+ "}\r\n"
						+ "";
				FileWriter myWriter = new FileWriter(path_to_folder + "\\" +serviceJavaFileName);
				myWriter.write(content);
				myWriter.close();

			}

		}

		return "Spring Services Created";
	}
	
	
	private static String formatServiceName(String entityName) {
		
		StringBuilder sb = new StringBuilder(entityName);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
		
	}


}

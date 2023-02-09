package in.co.bel.ims.initial.service.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImsJpaTemplateGenerator {

	
	/*
	 * public static void main(String[] argv) throws IOException {
	 * 
	 * System.out.println(fileRename()); }
	 */

	protected static String fileRename() throws IOException {
		String path_to_folder = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-data\\src\\main\\java\\in\\co\\bel\\ims\\data\\repository";
		File my_folder = new File(path_to_folder);
		File[] array_file = my_folder.listFiles();
		for (int i = 0; i < array_file.length; i++) {
			if (array_file[i].isFile()) {
				File my_file = new File(path_to_folder + "\\" + array_file[i].getName());
				String long_file_name = array_file[i].getName();
				String[] my_token = long_file_name.split("[.]");
				String entityName = my_token[0];
				String repoClassName = entityName + "Repository";
				String repoJavaFileName = entityName + "Repository.java";
				my_file.renameTo(new File(path_to_folder + "\\" + repoJavaFileName));

				String content = "package in.co.bel.ims.data.repository;\r\n" + "\r\n"
						+ "import org.springframework.data.jpa.repository.JpaRepository;\r\n" + "\r\n"
						+ "public interface " + repoClassName + " extends JpaRepository<" + entityName
						+ ", Integer> {\r\n" + "   \r\n" + "}\r\n" + "\r\n" + "";
				FileWriter myWriter = new FileWriter(path_to_folder + "\\" + repoJavaFileName);
				myWriter.write(content);
				myWriter.close();

			}

		}

		return "Spring JPA Repository Files Created";
	}

}


package in.co.bel.ims.initial.service.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class ImsPDFMerger {
	
	public static void mergePDF(File passMain,File pass,String passFileName) {

		try {
			PDFMergerUtility obj = new PDFMergerUtility();
			obj.setDestinationFileName(passFileName);
			obj.addSource(passMain);
			obj.addSource(pass);
			//obj.addSource(instructions);
			obj.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public static void mergePDF(List<File> srcFileList, String destPassFileName) {
		try {
			PDFMergerUtility obj = new PDFMergerUtility();
			obj.setDestinationFileName(destPassFileName);
			for(File tempFile : srcFileList) {
				if(tempFile.exists()) {
					obj.addSource(tempFile);
				}
			}
			obj.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

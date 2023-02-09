package in.co.bel.ims.initial.infra.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.IdentityProofRepository;
import in.co.bel.ims.initial.data.repository.MaritalStatusRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.data.repository.PayLevelRepository;
import in.co.bel.ims.initial.data.repository.SalutationRepository;

@Service
public class GenerateExcelTemplate {
	@Autowired
	OrganizationRepository organizationRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	SalutationRepository salutationRepository;
	
	@Autowired
	PayLevelRepository payLevelRepository;
	
	@Autowired
	IdentityProofRepository identityProofRepository;
	
	@Autowired
	MaritalStatusRepository maritalStatusRepository;
	
	public void generateTemplate(InputStream fileStream, String filename) throws IOException {
//		List<Organization> organizations = organizationRepository.findAll();
//		List<Department> departments = departmentRepository.findAll();
//		List<Salutation> salutations = salutationRepository.findAll();
//		List<PayLevel> payLevels = payLevelRepository.findAll();
//		List<IdentityProof> idProofTypes = identityProofRepository.findAll();
//		List<MaritalStatus> maritalStatus = maritalStatusRepository.findAll();

	    XSSFWorkbook workbook = new XSSFWorkbook(fileStream); 
	    
//	    Sheet organizationsSheet;
//	    Sheet departmentsSheet;
//	    Sheet salutationsSheet;
//	    Sheet payLevelsSheet;
//	    Sheet idProofTypesSheet;
//	    Sheet maritalStatusSheet;
	    
//	    if (workbook.getSheetIndex("Organizations") != -1) {
//	    	organizationsSheet = workbook.getSheet("Organizations");
//        } else organizationsSheet = workbook.createSheet("Organizations");
//	    
//	    if (workbook.getSheetIndex("Departments") != -1) {
//	    	departmentsSheet = workbook.getSheet("Departments");
//        } else departmentsSheet = workbook.createSheet("Departments");
	    
//	    if (workbook.getSheetIndex("Salutations") != -1) {
//	    	salutationsSheet = workbook.getSheet("Salutations");
//        } else salutationsSheet = workbook.createSheet("Salutations");
//	    
//	    if (workbook.getSheetIndex("Paylevels") != -1) {
//	    	payLevelsSheet = workbook.getSheet("Paylevels");
//        } else payLevelsSheet = workbook.createSheet("Paylevels");
	    
//	    if (workbook.getSheetIndex("IdProofTypes") != -1) {
//	    	idProofTypesSheet = workbook.getSheet("IdProofTypes");
//        } else idProofTypesSheet = workbook.createSheet("IdProofTypes");
//	    
//	    if (workbook.getSheetIndex("MaritalStatus") != -1) {
//	    	maritalStatusSheet = workbook.getSheet("MaritalStatus");
//        } else maritalStatusSheet = workbook.createSheet("MaritalStatus");
	    
		
//		organizationsSheet.setColumnWidth(0, 6000);
//		departmentsSheet.setColumnWidth(0, 6000);
//		salutationsSheet.setColumnWidth(0, 6000);
//		payLevelsSheet.setColumnWidth(0, 6000);
//		idProofTypesSheet.setColumnWidth(0, 6000);
//		maritalStatusSheet.setColumnWidth(0, 6000);
		
//		Row organizationsHeader = organizationsSheet.createRow(0);
//		Row departmentsHeader = departmentsSheet.createRow(0);
//		Row salutatiosnHeader = salutationsSheet.createRow(0);
//		Row payLevelsHeader = payLevelsSheet.createRow(0);
//		Row idProofTypesHeader = idProofTypesSheet.createRow(0);
//		Row maritalStatusHeader = maritalStatusSheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

//		Cell organizationsHeaderCell = organizationsHeader.createCell(0);
//		organizationsHeaderCell.setCellValue("Organization");
//		organizationsHeaderCell.setCellStyle(headerStyle);
//
//		Cell departmentsHeaderCell = departmentsHeader.createCell(0);
//		departmentsHeaderCell.setCellValue("Department");
//		departmentsHeaderCell.setCellStyle(headerStyle);

//		Cell salutatiosnHeaderCell = salutatiosnHeader.createCell(0);
//		salutatiosnHeaderCell.setCellValue("Salutation");
//		salutatiosnHeaderCell.setCellStyle(headerStyle);
//		
//		Cell payLevelsHeaderCell = payLevelsHeader.createCell(0);
//		payLevelsHeaderCell.setCellValue("Pay Level");
//		payLevelsHeaderCell.setCellStyle(headerStyle);
		
//		Cell idProofTypesHeaderCell = idProofTypesHeader.createCell(0);
//		idProofTypesHeaderCell.setCellValue("Id Proof Type");
//		idProofTypesHeaderCell.setCellStyle(headerStyle);
//		
//		Cell maritalStatusTypesHeaderCell = maritalStatusHeader.createCell(0);
//		maritalStatusTypesHeaderCell.setCellValue("Marital Status");
//		maritalStatusTypesHeaderCell.setCellStyle(headerStyle);
		
		

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		
//		List<Row> organizationsRows = new ArrayList<Row>(organizations.size());
//		List<Row> salutationsRows = new ArrayList<Row>(salutations.size());
//		List<Row> payLevelsRows = new ArrayList<Row>(payLevels.size());
//		List<Row> departmentsRows = new ArrayList<Row>(departments.size());
//		List<Row> idProofTypesRows = new ArrayList<Row>(idProofTypes.size());
//		List<Row> maritalStatusTypesRows = new ArrayList<Row>(maritalStatus.size());
        
//		for (int i=1; i < (organizations.size() + 1); i++) {
//			organizationsRows.add(organizationsSheet.createRow(i));
//		}
		 
//		for (int i=1; i < (salutations.size() + 1); i++) {
//			salutationsRows.add(salutationsSheet.createRow(i));
//		}
//		
//		for (int i=1; i < (payLevels.size() + 1); i++) {
//			payLevelsRows.add(payLevelsSheet.createRow(i));
//		}
		
//		for (int i=1; i < (departments.size() + 1); i++) {
//			departmentsRows.add(departmentsSheet.createRow(i));
//		}
//		
//		for (int i=1; i < (idProofTypes.size() + 1); i++) {
//			idProofTypesRows.add(idProofTypesSheet.createRow(i));
//		}
//		
//		for (int i=1; i < (maritalStatus.size() + 1); i++) {
//			maritalStatusTypesRows.add(maritalStatusSheet.createRow(i));
//		}

//		for (int i=0; i < organizations.size(); i++) {
//		     String value = organizations.get(i).getName();
//		     int organizationId = organizations.get(i).getId();
//		     Cell cell = organizationsRows.get(i).createCell(0);
//		     cell.setCellValue(organizationId+"-"+value);
//		}
//		for (int i=0; i < departments.size(); i++) {
//			 String value = departments.get(i).getName();
//			 int departmentId = departments.get(i).getId();
//		     Cell cell = departmentsRows.get(i).createCell(0);
//		     cell.setCellValue(departmentId+"-"+value);
//		}
//		for (int i=0; i < salutations.size(); i++) {
//			 String value = salutations.get(i).getName();
////			 int salutationId = salutations.get(i).getId();
//		     Cell cell = salutationsRows.get(i).createCell(0);
//		     cell.setCellValue( value);
//		}
//		for (int i=0; i < payLevels.size(); i++) {
//			 String value = payLevels.get(i).getName();
////			 int payLevelId = payLevels.get(i).getId();
//		     Cell cell = payLevelsRows.get(i).createCell(0);
//		     cell.setCellValue(value);
//		}
//		for (int i=0; i < idProofTypes.size(); i++) {
//			 String value = idProofTypes.get(i).getName();
//			 int proofId = idProofTypes.get(i).getId();
//		     Cell cell = idProofTypesRows.get(i).createCell(0);
//		     cell.setCellValue(proofId+"-"+value);
//		}
//		
//		for (int i=0; i < maritalStatus.size(); i++) {
//			 String value = maritalStatus.get(i).getStatus();
//			 int statusId = maritalStatus.get(i).getId();
//		     Cell cell = maritalStatusTypesRows.get(i).createCell(0);
//		     cell.setCellValue(statusId+"-"+value);
//		}
		fileStream.close();
		OutputStream outputStream;
		try {
			File file = new File(filename);
			outputStream = new FileOutputStream(file, false);
			workbook.write(outputStream);
			workbook.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

package in.co.bel.ims.initial.infra.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.IdentityProofRepository;
import in.co.bel.ims.initial.data.repository.MaritalStatusRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.data.repository.PayLevelRepository;
import in.co.bel.ims.initial.data.repository.PrecedenceRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.data.repository.SalutationRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.MaritalStatus;
import in.co.bel.ims.initial.entity.PayLevel;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.entity.Salutation;
import in.co.bel.ims.initial.entity.UserType;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@Component
public class ExcelTemplateParser {

	@Autowired
	SalutationRepository salutationRepository;
	@Autowired
	PayLevelRepository payLevelRepository;
	@Autowired
	MaritalStatusRepository maritalStatusRepository;
	@Autowired
	PrecedenceRepository precedenceRepository;
	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	OrganizationRepository organizationRepository;
	@Autowired
	DepartmentRepository departmentRepository;
	@Autowired
	IdentityProofRepository identityProofRepository;
	
	//Template A - As per customers draft
	public List<ImsUser> parseTemplateA(InputStream fileStream) {

		List<ImsUser> imsUserList = new ArrayList<>();
		try {
			int templateARowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_A.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateARowsCount = templateARowsCount + 1;
				if (row.getRowNum() != 0) {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateARowsCount) {
						if (row != null) {
							row.cellIterator().forEachRemaining(cell -> {
								if (cell != null && cell.getCellType() != CellType.BLANK
										&& StringUtils.isNotBlank(cell.toString())) {
									switch (cell.getColumnIndex()) {
									case ExcelTemplateConstants.APDX_A_EMP_ID_COLUMN_INDEX:
										if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
											imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
													? String.valueOf((long) cell.getNumericCellValue())
													: cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_SALUTAION_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
											imsUser.setSalutation(salutation);
										}
										break;
									case ExcelTemplateConstants.APDX_A_NAME_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setName(cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_DESIGNATION_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setDesignation(cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_PAY_LEVEL_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											
											PayLevel payLevel = payLevelRepository.findByName(cell.getStringCellValue());
											imsUser.setPayLevel(payLevel);
										}
										break;
									case ExcelTemplateConstants.APDX_A_BASIC_PAY_COLUMN_INDEX:
										if (String.valueOf(cell.getNumericCellValue()) != null
												&& String.valueOf(cell.getNumericCellValue()) != "") {
											imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
													? String.valueOf(cell.getNumericCellValue())
													: cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_MOBILE_NO_COLUMN_INDEX:
										if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
											imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
													? String.valueOf((long) cell.getNumericCellValue())
													: cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_EMAIL_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setEmail(cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_MARITAL_STATUS_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											MaritalStatus maritalStatus = maritalStatusRepository.findByStatus(cell.getStringCellValue());
											imsUser.setMaritalStatus(maritalStatus);
										}
										break;
									case ExcelTemplateConstants.APDX_A_OFFICIAL_ADDRESS_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setOfficeAddress(cell.getStringCellValue());
										}
										break;
									case ExcelTemplateConstants.APDX_A_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setResidentialAddress(cell.getStringCellValue());
										}
										break;
//									case ExcelTemplateConstants.APDX_A_SEATING_PREFERENCE_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											int id = Integer.parseInt(
//													cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
//															.trim());
//											imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
//										}
//										break;
									case ExcelTemplateConstants.APDX_A_REMARKS_COLUMN_INDEX:
										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
											imsUser.setRemarks(cell.getStringCellValue());
										}
										break;
									}

								}

							});
						}
					}
					if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
							&& imsUser.getName() != "" && imsUser.getMobileNo() != null
							&& imsUser.getMobileNo() != "") {
						imsUserList.add(imsUser);
					}

				}
			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return imsUserList;
	}
	
	//Appendix B - As per customers draft
	public List<ImsUser> parseTemplateB(InputStream fileStream) {

		List<ImsUser> imsUserList = new ArrayList<>();
		try {
			int templateBRowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateBRowsCount = templateBRowsCount + 1;
				ImsUser imsUser = new ImsUser();
				imsUser.setUserType(userType);
				imsUser.setRole(role);
				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateBRowsCount) {
					if (row != null) {
						row.cellIterator().forEachRemaining(cell -> {
							switch (cell.getColumnIndex()) {
							
							case ExcelTemplateConstants.APDX_B_EMP_ID_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_SALUTAION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
									imsUser.setSalutation(salutation);
								}
								break;
							case ExcelTemplateConstants.APDX_B_NAME_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setName(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_DESIGNATION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setDesignation(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_EQUI_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEquivalentStatus(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_PAY_LEVEL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									PayLevel payLevel = payLevelRepository.findByName(cell.getStringCellValue());
									imsUser.setPayLevel(payLevel);
								}
								break;
							case ExcelTemplateConstants.APDX_B_BASIC_PAY_COLUMN_INDEX:
								if (String.valueOf(cell.getNumericCellValue()) != null
										&& String.valueOf(cell.getNumericCellValue()) != "") {
									imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf(cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_MOBILE_NO_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_EMAIL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEmail(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_MARITAL_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									MaritalStatus maritalStatus = maritalStatusRepository.findByStatus(cell.getStringCellValue());
									imsUser.setMaritalStatus(maritalStatus);
								}
								break;
							case ExcelTemplateConstants.APDX_B_OFFICIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setOfficeAddress(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_B_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setResidentialAddress(cell.getStringCellValue());
								}
								break;
//							case ExcelTemplateConstants.APDX_B_SEATING_PREFERENCE_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int id = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
//								}
//								break;
							case ExcelTemplateConstants.APDX_B_REMARKS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setRemarks(cell.getStringCellValue());
								}
								break;
							}

						});
					}
				}

				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
					imsUserList.add(imsUser);
				}

			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return imsUserList;
	}
	
	//Appendix C - As per customers draft
	public List<ImsUser> parseTemplateC(InputStream fileStream) {

		List<ImsUser> imsUserList = new ArrayList<>();
		try {
			int templateCRowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateCRowsCount = templateCRowsCount + 1;
				ImsUser imsUser = new ImsUser();
				imsUser.setUserType(userType);
				imsUser.setRole(role);
				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateCRowsCount) {
					if (row != null) {
						row.cellIterator().forEachRemaining(cell -> {
							switch (cell.getColumnIndex()) {
							
							case ExcelTemplateConstants.APDX_C_EMP_ID_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_SALUTAION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
									imsUser.setSalutation(salutation);
								}
								break;
							case ExcelTemplateConstants.APDX_C_NAME_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setName(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_DESIGNATION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setDesignation(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_EQUI_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEquivalentStatus(cell.getStringCellValue());
								}
								break;
//							case ExcelTemplateConstants.APDX_C_TAB_OF_PRECEDENCE_COLUMN_INDEX:
//								if (String.valueOf(cell.getNumericCellValue()) != null && String.valueOf(cell.getNumericCellValue()) != "") {
//									imsUser.setPrecedence(new Precedence(Integer.valueOf(String.valueOf(cell.getNumericCellValue()))));
//								}
//								break;
							case ExcelTemplateConstants.APDX_C_PAY_LEVEL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									PayLevel payLevel = payLevelRepository.findByName(cell.getStringCellValue());
									imsUser.setPayLevel(payLevel);
								}
								break;
							case ExcelTemplateConstants.APDX_C_BASIC_PAY_COLUMN_INDEX:
								if (String.valueOf(cell.getNumericCellValue()) != null
										&& String.valueOf(cell.getNumericCellValue()) != "") {
									imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf(cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_MOBILE_NO_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_EMAIL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEmail(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_MARITAL_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									MaritalStatus maritalStatus = maritalStatusRepository.findByStatus(cell.getStringCellValue());
									imsUser.setMaritalStatus(maritalStatus);
								}
								break;
							case ExcelTemplateConstants.APDX_C_OFFICIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setOfficeAddress(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_C_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setResidentialAddress(cell.getStringCellValue());
								}
								break;
//							case ExcelTemplateConstants.APDX_C_SEATING_PREFERENCE_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int id = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
//								}
//								break;
							case ExcelTemplateConstants.APDX_C_REMARKS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setRemarks(cell.getStringCellValue());
								}
								break;
							}

						});
					}
				}
				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
					imsUserList.add(imsUser);
				}

			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return imsUserList;
	}
	
	
	//Appendix D - As per customers draft
	public List<ImsUser> parseTemplateD(InputStream fileStream) {

		List<ImsUser> guestUserDataList = new ArrayList<>();
		try {
			int templateDRowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateDRowsCount = templateDRowsCount + 1;
				ImsUser imsUser = new ImsUser();
				imsUser.setUserType(userType);
				imsUser.setRole(role);
				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateDRowsCount) {
					if (row != null) {
						row.cellIterator().forEachRemaining(cell -> {

							switch (cell.getColumnIndex()) {
							
							case ExcelTemplateConstants.APDX_D_SALUTAION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
									imsUser.setSalutation(salutation);
								}
								break;
							case ExcelTemplateConstants.APDX_D_NAME_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setName(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_D_MOBILE_NO_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_D_EMAIL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEmail(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_D_NATIONALITY_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setNationality(cell.getStringCellValue());
								}
								break;
							
							case ExcelTemplateConstants.APDX_D_ID_NUMBER_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setIdProofNo(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_D_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setResidentialAddress(cell.getStringCellValue());
								}
								break;
							
							case ExcelTemplateConstants.APDX_D_REMARKS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setRemarks(cell.getStringCellValue());
								}
								break;
							}

						});
					}

				}
				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
					guestUserDataList.add(imsUser);
				}

			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return guestUserDataList;
	}

	//Appendix E - As per customers draft
	public List<ImsUser> parseTemplateE(InputStream fileStream) {

		List<ImsUser> delegatesUserDataList = new ArrayList<>();
		try {
			int templateERowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateERowsCount = templateERowsCount + 1;
				ImsUser imsUser = new ImsUser();
				imsUser.setUserType(userType);
				imsUser.setRole(role);
				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateERowsCount) {
					if (row != null) {
						row.cellIterator().forEachRemaining(cell -> {
							switch (cell.getColumnIndex()) {
							
							case ExcelTemplateConstants.APDX_E_EMP_ID_COLUMN_INDEX:
								if (String.valueOf(cell.getNumericCellValue()) != null && String.valueOf(cell.getNumericCellValue()) != "") {
									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_COUNTRY_ORGANIZATION:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setNationality(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_SALUTAION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
									imsUser.setSalutation(salutation);
								}
								break;
							case ExcelTemplateConstants.APDX_E_NAME_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setName(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_POSITION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setPositionMissionConsulate(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_MOBILE_NO_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_EMAIL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEmail(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_MARITAL_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									MaritalStatus maritalStatus = maritalStatusRepository.findByStatus(cell.getStringCellValue());
									imsUser.setMaritalStatus(maritalStatus);
								}
								break;
							case ExcelTemplateConstants.APDX_E_OFFICIAL_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setOfficeAddress(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_E_REMARKS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setRemarks(cell.getStringCellValue());
								}
								break;
							}

						});
					}
				}

				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
					delegatesUserDataList.add(imsUser);
				}

			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return delegatesUserDataList;
	}
	
	//Appendix F - As per customers draft
	public List<ImsUser> parseTemplateF(InputStream fileStream) {

		List<ImsUser> mlaMpUserDataList = new ArrayList<>();
		try {
			int templateFRowsCount = 0;
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheetAt(0);
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			for (Row row : sheet) {
				templateFRowsCount = templateFRowsCount + 1;
				ImsUser imsUser = new ImsUser();
				imsUser.setUserType(userType);
				imsUser.setRole(role);
				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateFRowsCount) {
					if (row != null) {
						row.cellIterator().forEachRemaining(cell -> {
							switch (cell.getColumnIndex()) {
							case ExcelTemplateConstants.APDX_F_EMP_ID_COLUMN_INDEX:
								if (String.valueOf(cell.getNumericCellValue()) != null && String.valueOf(cell.getNumericCellValue()) != "") {
									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_CONSTITUENCY_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setConstituency(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_SALUTAION_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									Salutation salutation = salutationRepository.findByName(cell.getStringCellValue());
									imsUser.setSalutation(salutation);
								}
								break;
							case ExcelTemplateConstants.APDX_F_NAME_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setName(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_MOBILE_NO_COLUMN_INDEX:
								if (String.valueOf((long) cell.getNumericCellValue()) != null && String.valueOf((long) cell.getNumericCellValue()) != "") {
									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
											? String.valueOf((long) cell.getNumericCellValue())
											: cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_EMAIL_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setEmail(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_MARITAL_STATUS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									MaritalStatus maritalStatus = maritalStatusRepository.findByStatus(cell.getStringCellValue());
									imsUser.setMaritalStatus(maritalStatus);
								}
								break;
							case ExcelTemplateConstants.APDX_F_ADDRESS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setOfficeAddress(cell.getStringCellValue());
								}
								break;
							case ExcelTemplateConstants.APDX_F_REMARKS_COLUMN_INDEX:
								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
									imsUser.setRemarks(cell.getStringCellValue());
								}
								break;
							}

						});
					}
				}

				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
					mlaMpUserDataList.add(imsUser);
				}

			}
			workbook.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return mlaMpUserDataList;
	}

	// Appendix A - Ministry Users excel template parsing.
//	public List<ImsUser> parseTemplateA(InputStream fileStream) {
//
//		List<ImsUser> imsUserList = new ArrayList<>();
//		try {
//			int templateARowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_A.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateARowsCount = templateARowsCount + 1;
//				if (row.getRowNum() != 0) {
//					ImsUser imsUser = new ImsUser();
//					imsUser.setUserType(userType);
//					imsUser.setRole(role);
//					if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateARowsCount) {
//						if (row != null) {
//							row.cellIterator().forEachRemaining(cell -> {
//								if (cell != null && cell.getCellType() != CellType.BLANK
//										&& StringUtils.isNotBlank(cell.toString())) {
//									switch (cell.getColumnIndex()) {
//									case ExcelTemplateConstants.APDX_A_ORGANIZATION_COLUMN_INDEX:
////										 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//										break;
//									case ExcelTemplateConstants.APDX_A_DEPARTMENT_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											int deptId = Integer.parseInt(
//													cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
//															.trim());
//											Department department = departmentRepository.findById(deptId).get();
//											imsUser.setDepartment(department);
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_EMP_ID_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
//													? String.valueOf((long) cell.getNumericCellValue())
//													: cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_SALUTAION_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											int salId = Integer.parseInt(
//													cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
//															.trim());
//											Salutation salutation = salutationRepository.findById(salId).get();
//											imsUser.setSalutation(salutation);
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_NAME_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setName(cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_DESIGNATION_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setDesignation(cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_PAY_LEVEL_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											int payId = Integer.parseInt((cell.getCellType() == CellType.NUMERIC)
//													? String.valueOf(cell.getNumericCellValue())
//													: cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
//															.trim());
//											PayLevel payLevel = payLevelRepository.findById(payId).get();
//											imsUser.setPayLevel(payLevel);
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_BASIC_PAY_COLUMN_INDEX:
//										if (String.valueOf(cell.getNumericCellValue()) != null
//												&& String.valueOf(cell.getNumericCellValue()) != "") {
//											imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
//													? Double.valueOf(cell.getNumericCellValue())
//													: Double.valueOf(cell.getStringCellValue()));
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_MOBILE_NO_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//													? String.valueOf((long) cell.getNumericCellValue())
//													: cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_EMAIL_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setEmail(cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_MARITAL_STATUS_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											int msId = Integer.parseInt(
//													cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
//															.trim());
//											imsUser.setMaritalStatus(maritalStatusRepository.findById(msId).get());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_OFFICIAL_ADDRESS_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setOfficeAddress(cell.getStringCellValue());
//										}
//										break;
//									case ExcelTemplateConstants.APDX_A_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setResidentialAddress(cell.getStringCellValue());
//										}
//										break;
////									case ExcelTemplateConstants.APDX_A_SEATING_PREFERENCE_COLUMN_INDEX:
////										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
////											int id = Integer.parseInt(
////													cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0]
////															.trim());
////											imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
////										}
////										break;
//									case ExcelTemplateConstants.APDX_A_REMARKS_COLUMN_INDEX:
//										if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//											imsUser.setRemarks(cell.getStringCellValue());
//										}
//										break;
//									}
//
//								}
//
//							});
//						}
//					}
//					if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//							&& imsUser.getName() != "" && imsUser.getMobileNo() != null
//							&& imsUser.getMobileNo() != "") {
//						imsUserList.add(imsUser);
//					}
//
//				}
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return imsUserList;
//	}

	// Appendix B - Autonomous/PSU Users excel template parsing.
//	public List<ImsUser> parseTemplateB(InputStream fileStream) {
//
//		List<ImsUser> imsUserList = new ArrayList<>();
//		try {
//			int templateBRowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateBRowsCount = templateBRowsCount + 1;
//				ImsUser imsUser = new ImsUser();
//				imsUser.setUserType(userType);
//				imsUser.setRole(role);
//				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateBRowsCount) {
//					if (row != null) {
//						row.cellIterator().forEachRemaining(cell -> {
//							switch (cell.getColumnIndex()) {
//							case ExcelTemplateConstants.APDX_B_ORGANIZATION_COLUMN_INDEX:
////								 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//								break;
//							case ExcelTemplateConstants.APDX_B_DEPARTMENT_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int deptId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Department department = departmentRepository.findById(deptId).get();
//									imsUser.setDepartment(department);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_EMP_ID_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_SALUTAION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int salId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Salutation salutation = salutationRepository.findById(salId).get();
//									imsUser.setSalutation(salutation);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_DESIGNATION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setDesignation(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_EQUI_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEquivalentStatus(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_PAY_LEVEL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int payId = Integer.parseInt((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf(cell.getNumericCellValue())
//											: cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									PayLevel payLevel = payLevelRepository.findById(payId).get();
//									imsUser.setPayLevel(payLevel);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_BASIC_PAY_COLUMN_INDEX:
//								if (String.valueOf(cell.getNumericCellValue()) != null
//										&& String.valueOf(cell.getNumericCellValue()) != "") {
//									imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
//											? Double.valueOf(cell.getNumericCellValue())
//											: Double.valueOf(cell.getStringCellValue()));
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_MOBILE_NO_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_EMAIL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmail(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_MARITAL_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int msId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setMaritalStatus(maritalStatusRepository.findById(msId).get());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_OFFICIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setOfficeAddress(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_B_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setResidentialAddress(cell.getStringCellValue());
//								}
//								break;
////							case ExcelTemplateConstants.APDX_B_SEATING_PREFERENCE_COLUMN_INDEX:
////								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
////									int id = Integer.parseInt(
////											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
////									imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
////								}
////								break;
//							case ExcelTemplateConstants.APDX_B_REMARKS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRemarks(cell.getStringCellValue());
//								}
//								break;
//							}
//
//						});
//					}
//				}
//
//				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
//					imsUserList.add(imsUser);
//				}
//
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return imsUserList;
//	}

	// Appendix C - Commission/Committees Users excel template parsing.
//	public List<ImsUser> parseTemplateC(InputStream fileStream) {
//
//		List<ImsUser> imsUserList = new ArrayList<>();
//		try {
//			int templateCRowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateCRowsCount = templateCRowsCount + 1;
//				ImsUser imsUser = new ImsUser();
//				imsUser.setUserType(userType);
//				imsUser.setRole(role);
//				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateCRowsCount) {
//					if (row != null) {
//						row.cellIterator().forEachRemaining(cell -> {
//							switch (cell.getColumnIndex()) {
//							case ExcelTemplateConstants.APDX_C_ORGANIZATION_COLUMN_INDEX:
////								 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//								break;
//							case ExcelTemplateConstants.APDX_C_DEPARTMENT_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int deptId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Department department = departmentRepository.findById(deptId).get();
//									imsUser.setDepartment(department);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_EMP_ID_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_SALUTAION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int salId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Salutation salutation = salutationRepository.findById(salId).get();
//									imsUser.setSalutation(salutation);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_DESIGNATION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setDesignation(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_EQUI_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEquivalentStatus(cell.getStringCellValue());
//								}
//								break;
////							case ExcelTemplateConstants.APDX_C_TAB_OF_PRECEDENCE_COLUMN_INDEX:
////								if (String.valueOf(cell.getNumericCellValue()) != null && String.valueOf(cell.getNumericCellValue()) != "") {
////									imsUser.setPrecedence(new Precedence(Integer.valueOf(String.valueOf(cell.getNumericCellValue()))));
////								}
////								break;
//							case ExcelTemplateConstants.APDX_C_PAY_LEVEL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int payId = Integer.parseInt((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf(cell.getNumericCellValue())
//											: cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									PayLevel payLevel = payLevelRepository.findById(payId).get();
//									imsUser.setPayLevel(payLevel);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_BASIC_PAY_COLUMN_INDEX:
//								if (String.valueOf(cell.getNumericCellValue()) != null
//										&& String.valueOf(cell.getNumericCellValue()) != "") {
//									imsUser.setBasicPay((cell.getCellType() == CellType.NUMERIC)
//											? Double.valueOf(cell.getNumericCellValue())
//											: Double.valueOf(cell.getStringCellValue()));
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_MOBILE_NO_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_EMAIL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmail(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_MARITAL_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int msId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setMaritalStatus(maritalStatusRepository.findById(msId).get());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_OFFICIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setOfficeAddress(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_C_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setResidentialAddress(cell.getStringCellValue());
//								}
//								break;
////							case ExcelTemplateConstants.APDX_C_SEATING_PREFERENCE_COLUMN_INDEX:
////								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
////									int id = Integer.parseInt(
////											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
////									imsUser.setEnclosureGroup(enclosureGroupRepository.findById(id).get());
////								}
////								break;
//							case ExcelTemplateConstants.APDX_C_REMARKS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRemarks(cell.getStringCellValue());
//								}
//								break;
//							}
//
//						});
//					}
//				}
//				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
//					imsUserList.add(imsUser);
//				}
//
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return imsUserList;
//	}

	// Appendix D - Guest Users excel template parsing.
//	public List<ImsUser> parseTemplateD(InputStream fileStream) {
//
//		List<ImsUser> guestUserDataList = new ArrayList<>();
//		try {
//			int templateDRowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateDRowsCount = templateDRowsCount + 1;
//				ImsUser imsUser = new ImsUser();
//				imsUser.setUserType(userType);
//				imsUser.setRole(role);
//				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateDRowsCount) {
//					if (row != null) {
//						row.cellIterator().forEachRemaining(cell -> {
//
//							switch (cell.getColumnIndex()) {
//							case ExcelTemplateConstants.APDX_D_ORGANIZATION_COLUMN_INDEX:
////							 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//								break;
//							case ExcelTemplateConstants.APDX_D_DEPARTMENT_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int deptId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Department department = departmentRepository.findById(deptId).get();
//									imsUser.setDepartment(department);
//								}
//								break;
//
//							case ExcelTemplateConstants.APDX_D_SALUTAION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int salId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Salutation salutation = salutationRepository.findById(salId).get();
//									imsUser.setSalutation(salutation);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_MOBILE_NO_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_EMAIL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmail(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_NATIONALITY_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setNationality(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_ID_TYPE_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int proofId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									IdentityProof identityProof = identityProofRepository.findById(proofId).get();
//									imsUser.setIdentityProof(identityProof);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_ID_NUMBER_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setIdProofNo(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_RESIDENTIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setResidentialAddress(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_REC_OFFICER_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRecommendingOfficerName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_REC_OFFICER_DESIGNATION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRecommendingOfficerDesignation(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_D_REMARKS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRemarks(cell.getStringCellValue());
//								}
//								break;
//							}
//
//						});
//					}
//
//				}
//				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
//					guestUserDataList.add(imsUser);
//				}
//
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return guestUserDataList;
//	}

	// Appendix E - Delegates/Consulate Users excel template parsing.
//	public List<ImsUser> parseTemplateE(InputStream fileStream) {
//
//		List<ImsUser> delegatesUserDataList = new ArrayList<>();
//		try {
//			int templateERowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateERowsCount = templateERowsCount + 1;
//				ImsUser imsUser = new ImsUser();
//				imsUser.setUserType(userType);
//				imsUser.setRole(role);
//				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateERowsCount) {
//					if (row != null) {
//						row.cellIterator().forEachRemaining(cell -> {
//							switch (cell.getColumnIndex()) {
//							case ExcelTemplateConstants.APDX_E_ORGANIZATION_COLUMN_INDEX:
////								 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//								break;
//							case ExcelTemplateConstants.APDX_E_DEPARTMENT_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int deptId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Department department = departmentRepository.findById(deptId).get();
//									imsUser.setDepartment(department);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_EMP_ID_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_COUNTRY_ORGANIZATION:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setNationality(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_SALUTAION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int id = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Salutation salutation = salutationRepository.findById(id).get();
//									imsUser.setSalutation(salutation);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_POSITION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setPositionMissionConsulate(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_MOBILE_NO_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_EMAIL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmail(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_MARITAL_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int msId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setMaritalStatus(maritalStatusRepository.findById(msId).get());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_OFFICIAL_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setOfficeAddress(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_E_REMARKS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRemarks(cell.getStringCellValue());
//								}
//								break;
//							}
//
//						});
//					}
//				}
//
//				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
//					delegatesUserDataList.add(imsUser);
//				}
//
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return delegatesUserDataList;
//	}

	// Appendix F - MLA/MP Users excel template parsing.
//	public List<ImsUser> parseTemplateF(InputStream fileStream) {
//
//		List<ImsUser> mlaMpUserDataList = new ArrayList<>();
//		try {
//			int templateFRowsCount = 0;
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
//			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
//			for (Row row : sheet) {
//				templateFRowsCount = templateFRowsCount + 1;
//				ImsUser imsUser = new ImsUser();
//				imsUser.setUserType(userType);
//				imsUser.setRole(role);
//				if (row.getRowNum() != 0 && sheet.getPhysicalNumberOfRows() > templateFRowsCount) {
//					if (row != null) {
//						row.cellIterator().forEachRemaining(cell -> {
//							switch (cell.getColumnIndex()) {
//							case ExcelTemplateConstants.APDX_F_ORGANIZATION_COLUMN_INDEX:
////								 Organization organization =  organizationRepository.findById(cell.getStringCellValue());
//								break;
//							case ExcelTemplateConstants.APDX_F_DEPARTMENT_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int id = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Department department = departmentRepository.findById(id).get();
//									imsUser.setDepartment(department);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_EMP_ID_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmpNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_CONSTITUENCY_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setConstituency(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_SALUTAION_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int salId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									Salutation salutation = salutationRepository.findById(salId).get();
//									imsUser.setSalutation(salutation);
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_NAME_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setName(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_MOBILE_NO_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setMobileNo((cell.getCellType() == CellType.NUMERIC)
//											? String.valueOf((long) cell.getNumericCellValue())
//											: cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_EMAIL_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setEmail(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_MARITAL_STATUS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									int msId = Integer.parseInt(
//											cell.getStringCellValue().split(ExcelTemplateConstants.HYPHEN)[0].trim());
//									imsUser.setMaritalStatus(maritalStatusRepository.findById(msId).get());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_ADDRESS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setOfficeAddress(cell.getStringCellValue());
//								}
//								break;
//							case ExcelTemplateConstants.APDX_F_REMARKS_COLUMN_INDEX:
//								if (cell.getStringCellValue() != null && cell.getStringCellValue() != "") {
//									imsUser.setRemarks(cell.getStringCellValue());
//								}
//								break;
//							}
//
//						});
//					}
//				}
//
//				if (imsUser.getEmail() != "" && imsUser.getEmail() != null && imsUser.getName() != null
//						&& imsUser.getName() != "" && imsUser.getMobileNo() != null && imsUser.getMobileNo() != "") {
//					mlaMpUserDataList.add(imsUser);
//				}
//
//			}
//			workbook.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return mlaMpUserDataList;
//	}

}

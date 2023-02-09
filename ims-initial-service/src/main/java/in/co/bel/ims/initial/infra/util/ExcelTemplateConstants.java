package in.co.bel.ims.initial.infra.util;

public class ExcelTemplateConstants {
	
	public static final String template1 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template A- Official-Ministry-Dept.xlsx";
	public static final String template2 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template B- PSU-Autonomous.xlsx";
	public static final String template3 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template C- Comission-Committees-Courts.xlsx";
	public static final String template4 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template D- Others-Guest.xlsx";
	public static final String template5 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template E- Delegates-Consulate.xlsx";
	public static final String template6 = "C:\\Users\\HP\\Desktop\\IMS\\workspace1\\ims-infra\\src\\main\\resources\\templates\\Template F- MP.xlsx";
	
	public static final String HYPHEN = "-";
	public static final String UNDERSCORE = "_";
	
	//Regular Exp
	public static final String EMP_NO_REGEX = "[a-zA-Z0-9\\s]*";
	public static final String MOBILE_NO_REGEX = "[0-9\\s]*";
	public static final String EMAIL_REGEX = "[a-zA-Z0-9\\s_@.]*";
	public static final String DESIGNATION_REGEX = "[a-zA-Z0-9\\s-.,()/&]*";
	public static final String OFFICE_REGEX = "[a-zA-Z0-9\\s.,()&]*";
	public static final String CONSTITUENCY_REGEX = "[a-zA-Z0-9\\s-.,()&]*";
	public static final String REMARKS_REGEX = "[a-zA-Z0-9\\s+@#:,().&']*";
	public static final String EQUIVALENT_STATUS_REGEX = "[a-zA-Z0-9\\s]*";
	public static final String NATIONALITY_REGEX = "[A-za-z\\s]*";
	public static final String ID_PROOF_NO_REGEX = "[a-zA-Z0-9\\s]*";
	public static final String OFFICE_ADDR_REGEX = "[a-zA-Z0-9\\s .,()&]*";
	public static final String RESIDENTIAL_ADDR_REGEX = "[a-zA-Z0-9\\s .,()&]*";
	public static final String RECOMMENDING_OFFR_NAME_REGEX = "[a-zA-Z\\s .,()&]*";
	public static final String RECOMMENDING_OFFR_DESG_REGEX = "[a-zA-Z\\s-+.,()&/\\[\\]]*";
	
	//Appendix A - As per customers draft
	public static final int APDX_A_EMP_ID_COLUMN_INDEX = 1;
	public static final int APDX_A_SALUTAION_COLUMN_INDEX = 2;
	public static final int APDX_A_NAME_COLUMN_INDEX = 3;
	public static final int APDX_A_DESIGNATION_COLUMN_INDEX = 4;
	public static final int APDX_A_PAY_LEVEL_COLUMN_INDEX = 5;
	public static final int APDX_A_BASIC_PAY_COLUMN_INDEX = 6;
	public static final int APDX_A_MOBILE_NO_COLUMN_INDEX = 7;
	public static final int APDX_A_EMAIL_COLUMN_INDEX = 8;
	public static final int APDX_A_MARITAL_STATUS_COLUMN_INDEX = 9;
	public static final int APDX_A_OFFICIAL_ADDRESS_COLUMN_INDEX = 10;
	public static final int APDX_A_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 11;
	public static final int APDX_A_SEATING_PREFERENCE_COLUMN_INDEX = 12;
	public static final int APDX_A_REMARKS_COLUMN_INDEX = 13;
	
	//Appendix B  - As per customers draft
	public static final int APDX_B_EMP_ID_COLUMN_INDEX = 1;
	public static final int APDX_B_SALUTAION_COLUMN_INDEX = 2;
	public static final int APDX_B_NAME_COLUMN_INDEX = 3;
	public static final int APDX_B_DESIGNATION_COLUMN_INDEX = 4;
	public static final int APDX_B_EQUI_STATUS_COLUMN_INDEX = 5;
	public static final int APDX_B_PAY_LEVEL_COLUMN_INDEX = 6;
	public static final int APDX_B_BASIC_PAY_COLUMN_INDEX = 7;
	public static final int APDX_B_MOBILE_NO_COLUMN_INDEX = 8;
	public static final int APDX_B_EMAIL_COLUMN_INDEX = 9;
	public static final int APDX_B_MARITAL_STATUS_COLUMN_INDEX = 10;
	public static final int APDX_B_OFFICIAL_ADDRESS_COLUMN_INDEX = 11;
	public static final int APDX_B_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 12;
	public static final int APDX_B_SEATING_PREFERENCE_COLUMN_INDEX = 13;
	public static final int APDX_B_REMARKS_COLUMN_INDEX = 14;
	
	//Appendix C - As per customers draft
	public static final int APDX_C_EMP_ID_COLUMN_INDEX = 1;
	public static final int APDX_C_SALUTAION_COLUMN_INDEX = 2;
	public static final int APDX_C_NAME_COLUMN_INDEX = 3;
	public static final int APDX_C_DESIGNATION_COLUMN_INDEX = 4;
	public static final int APDX_C_EQUI_STATUS_COLUMN_INDEX = 5;
	public static final int APDX_C_TAB_OF_PRECEDENCE_COLUMN_INDEX = 6;
	public static final int APDX_C_PAY_LEVEL_COLUMN_INDEX = 7;
	public static final int APDX_C_BASIC_PAY_COLUMN_INDEX = 8;
	public static final int APDX_C_MOBILE_NO_COLUMN_INDEX = 9;
	public static final int APDX_C_EMAIL_COLUMN_INDEX = 10;
	public static final int APDX_C_MARITAL_STATUS_COLUMN_INDEX = 11;
	public static final int APDX_C_OFFICIAL_ADDRESS_COLUMN_INDEX = 12;
	public static final int APDX_C_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 13;
	public static final int APDX_C_SEATING_PREFERENCE_COLUMN_INDEX = 14;
	public static final int APDX_C_REMARKS_COLUMN_INDEX = 15;
	
	//Appendix D - As per customers draft
	public static final int APDX_D_SALUTAION_COLUMN_INDEX = 1;
	public static final int APDX_D_NAME_COLUMN_INDEX = 2;
	public static final int APDX_D_MOBILE_NO_COLUMN_INDEX = 3;
	public static final int APDX_D_EMAIL_COLUMN_INDEX = 4;
	public static final int APDX_D_NATIONALITY_COLUMN_INDEX = 5;
	public static final int APDX_D_ID_NUMBER_COLUMN_INDEX = 6;
	public static final int APDX_D_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 7;
	public static final int APDX_D_REMARKS_COLUMN_INDEX = 8;
	
	//Appendix E - As per customers draft
	public static final int APDX_E_EMP_ID_COLUMN_INDEX = 1;
	public static final int APDX_E_COUNTRY_ORGANIZATION = 2;
	public static final int APDX_E_SALUTAION_COLUMN_INDEX = 3;
	public static final int APDX_E_NAME_COLUMN_INDEX = 4;
	public static final int APDX_E_POSITION_COLUMN_INDEX = 5;
	public static final int APDX_E_MOBILE_NO_COLUMN_INDEX = 6;
	public static final int APDX_E_EMAIL_COLUMN_INDEX = 7;
	public static final int APDX_E_MARITAL_STATUS_COLUMN_INDEX = 8;
	public static final int APDX_E_OFFICIAL_ADDRESS_COLUMN_INDEX = 9;
	public static final int APDX_E_REMARKS_COLUMN_INDEX = 10;
	
	//Appendix F- As per customers draft
	public static final int APDX_F_EMP_ID_COLUMN_INDEX = 1;
	public static final int APDX_F_CONSTITUENCY_COLUMN_INDEX = 2;
	public static final int APDX_F_SALUTAION_COLUMN_INDEX = 3;
	public static final int APDX_F_NAME_COLUMN_INDEX = 4;
	public static final int APDX_F_MOBILE_NO_COLUMN_INDEX = 5;
	public static final int APDX_F_EMAIL_COLUMN_INDEX = 6;
	public static final int APDX_F_MARITAL_STATUS_COLUMN_INDEX = 7;
	public static final int APDX_F_ADDRESS_COLUMN_INDEX = 8;
	public static final int APDX_F_REMARKS_COLUMN_INDEX = 9;
	
	// Appendix A - Ministry Users Cell Indices in excel template.
//	public static final int APDX_A_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_A_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_A_EMP_ID_COLUMN_INDEX = 3;
//	public static final int APDX_A_SALUTAION_COLUMN_INDEX = 4;
//	public static final int APDX_A_NAME_COLUMN_INDEX = 5;
//	public static final int APDX_A_DESIGNATION_COLUMN_INDEX = 6;
//	public static final int APDX_A_PAY_LEVEL_COLUMN_INDEX = 7;
//	public static final int APDX_A_BASIC_PAY_COLUMN_INDEX = 8;
//	public static final int APDX_A_MOBILE_NO_COLUMN_INDEX = 9;
//	public static final int APDX_A_EMAIL_COLUMN_INDEX = 10;
//	public static final int APDX_A_MARITAL_STATUS_COLUMN_INDEX = 11;
//	public static final int APDX_A_OFFICIAL_ADDRESS_COLUMN_INDEX = 12;
//	public static final int APDX_A_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 13;
//	public static final int APDX_A_SEATING_PREFERENCE_COLUMN_INDEX = 14;
//	public static final int APDX_A_REMARKS_COLUMN_INDEX = 15;
	
	
	// Appendix B - Autonomous/PSU User Cell Indices in excel template.
//	public static final int APDX_B_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_B_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_B_EMP_ID_COLUMN_INDEX = 3;
//	public static final int APDX_B_SALUTAION_COLUMN_INDEX = 4;
//	public static final int APDX_B_NAME_COLUMN_INDEX = 5;
//	public static final int APDX_B_DESIGNATION_COLUMN_INDEX = 6;
//	public static final int APDX_B_EQUI_STATUS_COLUMN_INDEX = 7;
//	public static final int APDX_B_PAY_LEVEL_COLUMN_INDEX = 8;
//	public static final int APDX_B_BASIC_PAY_COLUMN_INDEX = 9;
//	public static final int APDX_B_MOBILE_NO_COLUMN_INDEX = 10;
//	public static final int APDX_B_EMAIL_COLUMN_INDEX = 11;
//	public static final int APDX_B_MARITAL_STATUS_COLUMN_INDEX = 12;
//	public static final int APDX_B_OFFICIAL_ADDRESS_COLUMN_INDEX = 13;
//	public static final int APDX_B_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 14;
//	public static final int APDX_B_SEATING_PREFERENCE_COLUMN_INDEX = 15;
//	public static final int APDX_B_REMARKS_COLUMN_INDEX = 16;
	
	// Appendix C - Commission/Committees User Cell Indices in excel template.
//	public static final int APDX_C_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_C_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_C_EMP_ID_COLUMN_INDEX = 3;
//	public static final int APDX_C_SALUTAION_COLUMN_INDEX = 4;
//	public static final int APDX_C_NAME_COLUMN_INDEX = 5;
//	public static final int APDX_C_DESIGNATION_COLUMN_INDEX = 6;
//	public static final int APDX_C_EQUI_STATUS_COLUMN_INDEX = 7;
//	public static final int APDX_C_TAB_OF_PRECEDENCE_COLUMN_INDEX = 8;
//	public static final int APDX_C_PAY_LEVEL_COLUMN_INDEX = 9;
//	public static final int APDX_C_BASIC_PAY_COLUMN_INDEX = 10;
//	public static final int APDX_C_MOBILE_NO_COLUMN_INDEX = 11;
//	public static final int APDX_C_EMAIL_COLUMN_INDEX = 12;
//	public static final int APDX_C_MARITAL_STATUS_COLUMN_INDEX = 13;
//	public static final int APDX_C_OFFICIAL_ADDRESS_COLUMN_INDEX = 14;
//	public static final int APDX_C_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 15;
//	public static final int APDX_C_SEATING_PREFERENCE_COLUMN_INDEX = 16;
//	public static final int APDX_C_REMARKS_COLUMN_INDEX = 17;
	
	// Appendix D - Guest Users Cell Indices in excel template.
//	public static final int APDX_D_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_D_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_D_SALUTAION_COLUMN_INDEX = 3;
//	public static final int APDX_D_NAME_COLUMN_INDEX = 4;
//	public static final int APDX_D_MOBILE_NO_COLUMN_INDEX = 5;
//	public static final int APDX_D_EMAIL_COLUMN_INDEX = 6;
//	public static final int APDX_D_NATIONALITY_COLUMN_INDEX = 7;
//	public static final int APDX_D_ID_TYPE_COLUMN_INDEX = 8;
//	public static final int APDX_D_ID_NUMBER_COLUMN_INDEX = 9;
//	public static final int APDX_D_RESIDENTIAL_ADDRESS_COLUMN_INDEX = 10;
//	public static final int APDX_D_REC_OFFICER_NAME_COLUMN_INDEX = 11;
//	public static final int APDX_D_REC_OFFICER_DESIGNATION_COLUMN_INDEX = 12;
//	public static final int APDX_D_REMARKS_COLUMN_INDEX = 13;
	
	// Appendix E - Delegates/Consulate Users Cell Indices in excel template.
//	public static final int APDX_E_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_E_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_E_EMP_ID_COLUMN_INDEX = 3;
//	public static final int APDX_E_COUNTRY_ORGANIZATION = 4;
//	public static final int APDX_E_SALUTAION_COLUMN_INDEX = 5;
//	public static final int APDX_E_NAME_COLUMN_INDEX = 6;
//	public static final int APDX_E_POSITION_COLUMN_INDEX = 7;
//	public static final int APDX_E_MOBILE_NO_COLUMN_INDEX = 8;
//	public static final int APDX_E_EMAIL_COLUMN_INDEX = 9;
//	public static final int APDX_E_MARITAL_STATUS_COLUMN_INDEX = 10;
//	public static final int APDX_E_OFFICIAL_ADDRESS_COLUMN_INDEX = 11;
//	public static final int APDX_E_REMARKS_COLUMN_INDEX = 12;
	
	// Appendix F - MLA/MP Users Cell Indices in excel template.
//	public static final int APDX_F_ORGANIZATION_COLUMN_INDEX = 1;
//	public static final int APDX_F_DEPARTMENT_COLUMN_INDEX = 2;
//	public static final int APDX_F_EMP_ID_COLUMN_INDEX = 3;
//	public static final int APDX_F_CONSTITUENCY_COLUMN_INDEX = 4;
//	public static final int APDX_F_SALUTAION_COLUMN_INDEX = 5;
//	public static final int APDX_F_NAME_COLUMN_INDEX = 6;
//	public static final int APDX_F_MOBILE_NO_COLUMN_INDEX = 10;
//	public static final int APDX_F_EMAIL_COLUMN_INDEX = 11;
//	public static final int APDX_F_MARITAL_STATUS_COLUMN_INDEX = 12;
//	public static final int APDX_F_ADDRESS_COLUMN_INDEX = 13;
//	public static final int APDX_F_REMARKS_COLUMN_INDEX = 14;
	
	
	

}

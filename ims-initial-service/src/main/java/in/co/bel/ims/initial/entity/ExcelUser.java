package in.co.bel.ims.initial.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.poiji.annotation.ExcelCellName;

@Entity
public class ExcelUser {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ExcelCellName("Organization")
	private String organization;

	@ExcelCellName("Department")
	private String department;
	
	@ExcelCellName("Emp Id")
	private String empId;
	
	@ExcelCellName("Salutation")
	private String salutation;
	
	@ExcelCellName("Name")
	private String name;

	@ExcelCellName("Mobile No")
	private String mobileNo;
	
	@ExcelCellName("Email")
	private String email;
	
	@ExcelCellName("Designation")
	private String designation;
	
	@ExcelCellName("Pay Level")
	private String payLevel;
	
	@ExcelCellName("Basic Pay")
	private String basicPay;
	
	@ExcelCellName("Marital Status")
	private String maritalStatus;
	
	@ExcelCellName("Office Address")
	private String officeAddress;
	
	@ExcelCellName("Residential Address")
	private String residentialAddress;
	
	@ExcelCellName("Enclosure Group")
	private String enclosureGroup;
	
	@ExcelCellName("Remarks")
	private String remarks;
	
	@ExcelCellName("Equivalent Status")
	private String equivalentStatus;
	
	@ExcelCellName("Nationality")
	private String nationality;
	
	@ExcelCellName("Id Proof No")
	private String idProofNo;
	
	@ExcelCellName("Position Mission Consulate")
	private String positionMissionConsulate;
	
	@ExcelCellName("Constituency")
	private String constituency;
	
	@ExcelCellName("Precedence")
	private String precedence;
	
	@ExcelCellName("Recommending Officer Name")
	private String recommendingOfficerName;
	
	@ExcelCellName("Recommending Officer Designation")
	private String recommendingOfficerDesignation;
	
	@ExcelCellName("Id Proof Type")
	private String IdentityProof;
	

	public ExcelUser() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ExcelUser(Integer id, String organization, String department, String empId, String salutation, String name,
			String mobileNo, String email, String designation, String payLevel, String basicPay, String maritalStatus,
			String officeAddress, String residentialAddress, String enclosureGroup, String remarks,
			String equivalentStatus, String nationality, String idProofNo, String positionMissionConsulate,
			String constituency, String precedence, String recommendingOfficerName,
			String recommendingOfficerDesignation, String identityProof) {
		super();
		this.id = id;
		this.organization = organization;
		this.department = department;
		this.empId = empId;
		this.salutation = salutation;
		this.name = name;
		this.mobileNo = mobileNo;
		this.email = email;
		this.designation = designation;
		this.payLevel = payLevel;
		this.basicPay = basicPay;
		this.maritalStatus = maritalStatus;
		this.officeAddress = officeAddress;
		this.residentialAddress = residentialAddress;
		this.enclosureGroup = enclosureGroup;
		this.remarks = remarks;
		this.equivalentStatus = equivalentStatus;
		this.nationality = nationality;
		this.idProofNo = idProofNo;
		this.positionMissionConsulate = positionMissionConsulate;
		this.constituency = constituency;
		this.precedence = precedence;
		this.recommendingOfficerName = recommendingOfficerName;
		this.recommendingOfficerDesignation = recommendingOfficerDesignation;
		IdentityProof = identityProof;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getOrganization() {
		return organization;
	}


	public void setOrganization(String organization) {
		this.organization = organization;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public String getSalutation() {
		return salutation;
	}


	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMobileNo() {
		return mobileNo;
	}


	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getDesignation() {
		return designation;
	}


	public void setDesignation(String designation) {
		this.designation = designation;
	}


	public String getPayLevel() {
		return payLevel;
	}


	public void setPayLevel(String payLevel) {
		this.payLevel = payLevel;
	}


	public String getBasicPay() {
		return basicPay;
	}


	public void setBasicPay(String basicPay) {
		this.basicPay = basicPay;
	}


	public String getMaritalStatus() {
		return maritalStatus;
	}


	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}


	public String getOfficeAddress() {
		return officeAddress;
	}


	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}


	public String getResidentialAddress() {
		return residentialAddress;
	}


	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}


	public String getEnclosureGroup() {
		return enclosureGroup;
	}


	public void setEnclosureGroup(String enclosureGroup) {
		this.enclosureGroup = enclosureGroup;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public String getEquivalentStatus() {
		return equivalentStatus;
	}


	public void setEquivalentStatus(String equivalentStatus) {
		this.equivalentStatus = equivalentStatus;
	}


	public String getNationality() {
		return nationality;
	}


	public void setNationality(String nationality) {
		this.nationality = nationality;
	}


	public String getIdProofNo() {
		return idProofNo;
	}


	public void setIdProofNo(String idProofNo) {
		this.idProofNo = idProofNo;
	}


	public String getPositionMissionConsulate() {
		return positionMissionConsulate;
	}


	public void setPositionMissionConsulate(String positionMissionConsulate) {
		this.positionMissionConsulate = positionMissionConsulate;
	}


	public String getConstituency() {
		return constituency;
	}


	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}


	public String getPrecedence() {
		return precedence;
	}


	public void setPrecedence(String precedence) {
		this.precedence = precedence;
	}


	public String getRecommendingOfficerName() {
		return recommendingOfficerName;
	}


	public void setRecommendingOfficerName(String recommendingOfficerName) {
		this.recommendingOfficerName = recommendingOfficerName;
	}


	public String getRecommendingOfficerDesignation() {
		return recommendingOfficerDesignation;
	}


	public void setRecommendingOfficerDesignation(String recommendingOfficerDesignation) {
		this.recommendingOfficerDesignation = recommendingOfficerDesignation;
	}


	public String getIdentityProof() {
		return IdentityProof;
	}


	public void setIdentityProof(String identityProof) {
		IdentityProof = identityProof;
	}


	@Override
	public String toString() {
		return "ExcelUser [id=" + id + ", organization=" + organization + ", department=" + department + ", empId="
				+ empId + ", salutation=" + salutation + ", name=" + name + ", mobileNo=" + mobileNo + ", email="
				+ email + ", designation=" + designation + ", payLevel=" + payLevel + ", basicPay=" + basicPay
				+ ", maritalStatus=" + maritalStatus + ", officeAddress=" + officeAddress + ", residentialAddress="
				+ residentialAddress + ", enclosureGroup=" + enclosureGroup + ", remarks=" + remarks
				+ ", equivalentStatus=" + equivalentStatus + ", nationality=" + nationality + ", idProofNo=" + idProofNo
				+ ", positionMissionConsulate=" + positionMissionConsulate + ", constituency=" + constituency
				+ ", precedence=" + precedence + ", recommendingOfficerName=" + recommendingOfficerName
				+ ", recommendingOfficerDesignation=" + recommendingOfficerDesignation + ", IdentityProof="
				+ IdentityProof + "]";
	}
	
	

	
}

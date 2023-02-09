package in.co.bel.ims.initial.service.controller;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.PaidPassHoldersRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.PaidPassHolders;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.PaidPassHolderRequest;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsJpaUpdateUtil;
import in.co.bel.ims.initial.service.util.ImsPDFMerger;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/paidPassHolders")
public class PaidPassHoldersController extends ImsServiceTemplate<PaidPassHolders, PaidPassHoldersRepository> {
	
	@Autowired
	PaidPassHoldersRepository paidPassHoldersRepository;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	private PassRepository passRepository;
	@Autowired
	private ImsUserRepository imsUserRepository;
	
	private static String PNG_TYPE="image/png";
	private static String JPEG_TYPE="image/jpeg";
//	private static String PDF_TYPE="application/pdf";
	
	@PostMapping("/uploadIdProof")
	@PreAuthorize("hasRole('COUNTEREMP') or hasRole('CITIZEN')")
	public ImsResponse uploadIdProof(@RequestParam("file") MultipartFile file, @RequestParam("id")  int id, @RequestParam("side")  int side, @RequestHeader (name="Authorization") String token) {
		ImsResponse imsResponse = new ImsResponse();
		try {
			//Check for image file content not malicious, if image content was edited following line will through exception
			BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
			
			String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
			ImsUser imsUser = imsUserRepository.findByMobileNo(mobileNo);
			List<PaidPassHolders> paidPassHoldersFromDB = paidPassHoldersRepository.findByImsUserId(imsUser.getId());
			
			int emptyRecordCount = 0;
			for (PaidPassHolders paidPassHolders : paidPassHoldersFromDB) {
				if(paidPassHolders.getIdentityProofNumber() == null && paidPassHolders.getMobileNo() == null) {
					emptyRecordCount++;
				}
			}
			
			if(imsUser.getRole().getId() == RoleEnum.ROLE_CITIZEN.role && emptyRecordCount >= 10) {
				imsResponse.setSuccess(false);
				imsResponse.setMessage("File Upload limit exceeds, Try again after 24 Hrs!");
				return imsResponse;
			}
			
			PaidPassHolders paidPassHolders = new PaidPassHolders();
			try {
				if(file.getBytes().length > 0) {
					//Detect Mime Type based on content
					Tika tika = new Tika();
					String detectedType = tika.detect(file.getBytes());

					if (detectedType != null && !detectedType.isEmpty()
							&& (detectedType.equals(PNG_TYPE) || detectedType.equals(JPEG_TYPE))) {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						String extension = "";
						if(detectedType.equals(PNG_TYPE))
							extension = "png";
						else if(detectedType.equals(JPEG_TYPE))
							extension = "jpeg";
						// Write input file to new ByteArrayOutputStream to reset input file MetaData
						// This will avoid malicious code saving to db
						ImageIO.write(bufferedImage, extension, byteArrayOutputStream);
						
						System.out.println("PaidPassHoldersController.uploadIdProof() "+id);
						if(id != 0) {
							paidPassHolders = paidPassHoldersRepository.findById(id).get();
						} 
						
						if(side == 0) {
							paidPassHolders.setIdentityProofDocument(byteArrayOutputStream.toByteArray());
						} else {
							paidPassHolders.setIdentityProofDocument2(byteArrayOutputStream.toByteArray());
						}
							
						
						paidPassHolders.setImsUser(imsUser);
						imsResponse.setSuccess(true);
						paidPassHolders.setCreatedTimestamp(LocalDateTime.now());
						imsResponse.setData(super.create(paidPassHolders));
						imsResponse.setMessage("Id Proof upload successfully");
						System.out.println("***************Id Proof uploaded successfully ******************");
						byteArrayOutputStream.close();
						byteArrayOutputStream = null;
						bufferedImage = null;
					} else {
						imsResponse.setSuccess(false);
						imsResponse.setMessage("Upload only PNG/JPEG file");
					}
				} else {
					imsResponse.setSuccess(false);
					imsResponse.setMessage("Please upload Id Proof document");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Upload valid PNG/JPEG file");
		}
		
		return imsResponse;
	}
	

	@PostMapping("/downloadPaidPassHolderDocx")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Object> downloadPaidPassHolderDocx(@RequestBody PaidPassHolderRequest paidPassHolderRequest) throws IOException, DocumentException{
		if(paidPassHolderRequest.getStartDate() != null && paidPassHolderRequest.getEndDate() != null && paidPassHolderRequest.getEventId() > 0) {
			List<PaidPassHolders> paidPassHolders = paidPassHoldersRepository.getAllBetweenDates(
					paidPassHolderRequest.getStartDate().with(LocalTime.of(0, 0, 0)),
					paidPassHolderRequest.getEndDate().with(LocalTime.of(23, 59, 59)),
					paidPassHolderRequest.getEventId());
			List<PaidPassHolders> paidPassHoldersFiltered = new ArrayList<>();
			for(PaidPassHolders paidPassHolder : paidPassHolders) {
				if(paidPassHolder.getImsUser().getRole().getId() == RoleEnum.ROLE_CITIZEN.role ) {
					Pass pass = passRepository.findByPaidPassHoldersIdAndPassStatusIdAndEventIdAndDeleted(paidPassHolder.getId(),PassStatusEnum.ALLOCATED.type, paidPassHolderRequest.getEventId(), false);
					if(pass != null && pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type) {
						paidPassHoldersFiltered.add(paidPassHolder);
					}
				}
			}
			String mergedFileName = "IDProofDocx.pdf";
			File finalFile = new File(mergedFileName);
			if(!finalFile.exists())
			   finalFile.createNewFile();
			String desFileName = "DescDocx.pdf";
			Path path = Paths.get(mergedFileName);
		    int j = 0 ;
		   	
			for (PaidPassHolders passHolder : paidPassHoldersFiltered) {
				
				if(!passHolder.getPasses().isEmpty()) {
					if (passHolder.getIdentityProofDocument() != null) {
						InputStream is = new ByteArrayInputStream(passHolder.getIdentityProofDocument());
						String fileType = URLConnection.guessContentTypeFromStream(is);
						if (fileType != null && fileType.toUpperCase().contains("IMAGE")) {
							Document document = new Document();
							// document.setPageSize(new Rectangle(buf.getWidth(), buf.getHeight()));
							try {
								FileOutputStream fos = new FileOutputStream(passHolder.getName() + "_" + passHolder.getIdentityProofNumber() + "_" + String.valueOf(paidPassHolderRequest.getEventId())+ "_" + String.valueOf(passHolder.getId()));
								PdfWriter writer = PdfWriter.getInstance(document, fos);
								writer.open();
								document.open();
								Image image = Image.getInstance(passHolder.getIdentityProofDocument());
								float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
										- document.rightMargin() - 10) / image.getWidth()) * 100;
								image.scalePercent(scaler);
								document.add(image);
								document.close();
								
								writer.close();
								fos.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						is.close();

						if (j == 0) {
							File file1 = new File(passHolder.getName() + "_" + passHolder.getIdentityProofNumber() + "_" + String.valueOf(paidPassHolderRequest.getEventId())+ "_" + String.valueOf(passHolder.getId()));
							if (!file1.exists())
								file1.createNewFile();
							Files.write(finalFile.toPath(), passHolder.getIdentityProofDocument(), StandardOpenOption.APPEND);
							Files.write(file1.toPath(), passHolder.getIdentityProofDocument(), StandardOpenOption.APPEND);
							addDocDetails(passHolder, desFileName, paidPassHolderRequest.getEventId());
							FileUtils.copyFile(new File(desFileName), finalFile, StandardCopyOption.REPLACE_EXISTING);
						} else {
							File file2 = new File(passHolder.getName() + "_" + passHolder.getIdentityProofNumber() + "_" + String.valueOf(paidPassHolderRequest.getEventId())+ "_" + String.valueOf(passHolder.getId()));
							if (!file2.exists())
								file2.createNewFile();
							Files.write(file2.toPath(), passHolder.getIdentityProofDocument(), StandardOpenOption.APPEND);
							addDocDetails(passHolder, desFileName, paidPassHolderRequest.getEventId());
							ImsPDFMerger.mergePDF(finalFile, new File(desFileName), mergedFileName);
						}
						++j;
					}
				}
			}
		    Resource resource = new UrlResource(path.toUri());
		    if(paidPassHoldersFiltered.size() > 0) {
		    	  return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\"")
							.contentType(MediaType.APPLICATION_PDF).body(resource);
		    }else {
		    	return ResponseEntity.badRequest().body("");
		    }
		  
		}else {
			return ResponseEntity.badRequest().body("");
		}
	}
	
	private void addDocDetails(PaidPassHolders passHolder, String desFileName, int eventId) throws IOException, DocumentException {
		PdfReader pdfReader = new PdfReader(passHolder.getName() + "_" + passHolder.getIdentityProofNumber() + "_" + String.valueOf(eventId)+ "_" + String.valueOf(passHolder.getId()));	
		FileOutputStream out = new FileOutputStream(desFileName);
    	PdfStamper pdfStamper = new PdfStamper(pdfReader,out);
        BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        int pages = pdfReader.getNumberOfPages(); 
		 for(int i=1; i<=pages; i++) { 
 	    		PdfContentByte pageContentByte = 
 	    					pdfStamper.getOverContent(i);
 	    		pageContentByte.beginText();
 	    		pageContentByte.setFontAndSize(baseFont, 18);
 	    		Pass pass = passRepository.findByPaidPassHoldersIdAndPassStatusIdAndEventIdAndDeleted(passHolder.getId(),PassStatusEnum.ALLOCATED.type, eventId, false);
 	    		pageContentByte.setColorFill(BaseColor.BLUE);
 	    		pageContentByte.setTextMatrix(15, 30);
 	    		pageContentByte.showText("Name : " + passHolder.getName());
 	    		pageContentByte.setTextMatrix(15, 15);
 	    		pageContentByte.showText("Mobile No : " + passHolder.getMobileNo());
 	    		pageContentByte.setTextMatrix(250, 30);
 	    		pageContentByte.newlineShowText("ID Proof Number : " + passHolder.getIdentityProofNumber());
 	    		pageContentByte.setTextMatrix(250, 15);
 	    		pageContentByte.showText("Control No : " + pass.getControlNo());
 	    	
 	    		pageContentByte.endText();
 	         }
 	         pdfStamper.close();
 	        pdfReader.close();
 	        out.close();
	}
// 
//	 @PostMapping("/upload")
//	  public void uploadFile(@RequestParam("file") MultipartFile file) {
//	    try {
//	    	PaidPassHolders FileDB = new PaidPassHolders();
//	    	FileDB.setIdentityProofDocument(file.getBytes());	
//	    	FileDB.setName("Sarath");
//	    	FileDB.setIdentityProofNumber("657564555555");
//
//	        paidPassHoldersRepository.save(FileDB);
//	    } catch (Exception e) {
//	    }
//	  }
	

	@PutMapping("/updateAll")
	@PreAuthorize("hasRole('COUNTEREMP')")
	public ImsResponse updateAll(@RequestBody List<PaidPassHolders> paidPassHoldersList) {
		ImsResponse imsResponse = new ImsResponse();
		List<PaidPassHolders> paidPassHoldersToSave = new ArrayList<>();
		paidPassHoldersList.forEach( paidPassHolderReq -> {
			PaidPassHolders entityToUpdate = paidPassHoldersRepository.findById(paidPassHolderReq.getId()).get();
			ImsJpaUpdateUtil.copyEntityProperties(paidPassHolderReq, entityToUpdate);
			paidPassHoldersToSave.add(entityToUpdate);
			
		});
		imsResponse.setSuccess(true);
		imsResponse.setData(paidPassHoldersRepository.saveAll(paidPassHoldersToSave));
		System.out.println("***************Updated all the PaidPassHolders ******************");
		return imsResponse;
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public ImsResponse getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public ImsResponse getById(int id) {
		// TODO Auto-generated method stub
		return super.getById(id);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public PaidPassHolders create(@Valid PaidPassHolders t) {
		// TODO Auto-generated method stub
		System.out.println("**************Saved PaidPassHolder ******************");
		return super.create(t);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public List<PaidPassHolders> createAll(@Valid List<PaidPassHolders> t) {
		// TODO Auto-generated method stub
		System.out.println("**************Created list of PaidPassHolders ******************");
		return super.createAll(t);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public void delete(int id) {
		// TODO Auto-generated method stub
		System.out.println("***************Deleted PaidPassHolder with the id " +id +" ******************");
		super.delete(id);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('COUNTEREMP')")
	public ImsResponse update(@Valid PaidPassHolders t) {
		// TODO Auto-generated method stub
		System.out.println("***************Updated PaidPassHolder with the id " + t.getId() +" ******************");
		return super.update(t);
	}
	
}

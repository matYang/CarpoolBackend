package carpool.resources.userResource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.imgscalr.Scalr;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

import carpool.common.DebugLog;
import carpool.configurations.ImageConfig;
import carpool.configurations.ServerConfig;
import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.PassengerVerificationOrigin;
import carpool.dbservice.FileService;
import carpool.dbservice.UserDaoService;
import carpool.exception.PseudoException;
import carpool.exception.validation.ValidationException;
import carpool.model.identityVerification.PassengerVerification;
import carpool.resources.PseudoResource;

public class UserPassengerVerificationResource extends PseudoResource{

	@Post
	public Representation startVerification(Representation entity) {
		int id = -1;
		File imgFile = null;
		Map<String, String> props = new HashMap<String, String>();
		try {
			
			this.checkFileEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
			if (!MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)){
				throw new ValidationException("上传数据类型错误");
			}
			
            // 1/ Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(ImageConfig.img_FactorySize);

            // 2/ Create a new file upload handler
            RestletFileUpload upload = new RestletFileUpload(factory);
            List<FileItem> items;

            // 3/ Request is parsed by the handler which generates a list of FileItems
            items = upload.parseRepresentation(entity); 

            

            for (final Iterator<FileItem> it = items.iterator(); it.hasNext(); ) {
                FileItem fi = it.next();

                String name = fi.getName();
                if (name == null) {
                    props.put(fi.getFieldName(), new String(fi.get(), "UTF-8"));
                } else {
                    BufferedImage bufferedImage = ImageIO.read(fi.getInputStream());
                    bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 200, 200, Scalr.OP_ANTIALIAS);
                    String imgName;
                    String path;
                    if (fi.getFieldName().equals("image_personalId_1")){
                    	imgName = ImageConfig.passengerVerificationFrontImgPrefix + ImageConfig.imgSize_m + id;
                    	imgFile = new File(ServerConfig.pathToSearchHistoryFolder + imgName + ".png");
                        ImageIO.write(bufferedImage, "png", imgFile);
                        //warning: can only call this upload once, as it will delete the image file before it exits
                        path = FileService.uploadPassengerVerificationLicenseFrontImg(id, imgFile, imgName);
                    }
                    else{
                    	imgName = ImageConfig.passengerVerificationBackImgPrefix  + ImageConfig.imgSize_m + id;
                    	imgFile = new File(ServerConfig.pathToSearchHistoryFolder + imgName + ".png");
                        ImageIO.write(bufferedImage, "png", imgFile);
                        //warning: can only call this upload once, as it will delete the image file before it exits
                        path = FileService.uploadPassengerVerificationLicenseBackImg(id, imgFile, imgName);
                    }
                    
                    
                    props.put(fi.getFieldName(), path);
                }
            }
            
            //process props to make it into passengerVerification obj
            String realName = props.get("name");
            String licenseNumber = props.get("id_number");
            String frontImgLink = props.get("image_personalId_1");
            String backImgLink = props.get("image_personalId_2");
            
            if (realName == null || licenseNumber == null || frontImgLink == null || backImgLink == null || realName.length() == 0 || licenseNumber.length() == 0){
            	throw new ValidationException("验证信息输入不完整");
            }
            
            
            PassengerVerification passengerVerification = new PassengerVerification(id, realName, licenseNumber, frontImgLink, backImgLink, PassengerVerificationOrigin.passenger);
            UserDaoService.addPassengerVerification(id, passengerVerification);
            		
		} catch (PseudoException e){
        	DebugLog.d(e);
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
        	DebugLog.d(e);
            return this.doException(e);
        }

		setStatus(Status.SUCCESS_OK);
		Representation result = new StringRepresentation("SUCCESS", MediaType.TEXT_PLAIN);

		this.addCORSHeader();
        return result;
	}
}

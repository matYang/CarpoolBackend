package carpool.resources.userResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.imgscalr.Scalr;

import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.configurations.CarpoolConfig;
import carpool.configurations.EnumConfig;
import carpool.dbservice.*;
import carpool.encryption.ImgCrypto;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class ImgResource extends PseudoResource{
	
	
	@Put
	public String store(Representation entity) throws Exception{ 
        if(entity == null){ 
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST); 
                return ""; 
        } 
        
        if(!MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)){ 
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST); 
                return ""; 
        } 
        
        DiskFileItemFactory factory = new DiskFileItemFactory(); 
        factory.setSizeThreshold(1024000); 
        
        RestletFileUpload upload = new RestletFileUpload(factory); 
        
        List<FileItem> items = upload.parseRepresentation(entity); 
        
        for(FileItem fi : items){ 
                File file = new File(fi.getName()); 
                fi.write(file); 
                DebugLog.d(fi.getName()); 
        } 
        setStatus(Status.SUCCESS_OK); 
        return ""; 
}
		
	@Post
	//TODO check for FileRepresentation
	public Representation postImage(Representation entity){
		JSONObject jsonObject = new JSONObject();
		int id = -1;
		File imgFile = null;	
		try {
			
			this.checkFileEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
			DebugLog.d("initial validation passed");
			DiskFileItemFactory factory = new DiskFileItemFactory(); 
	        factory.setSizeThreshold(1024000); 
	        RestletFileUpload upload = new RestletFileUpload(factory); 
	        
	        DebugLog.d("Waning: creating file items");
	        List<FileItem> items = upload.parseRepresentation(entity); 
	        DebugLog.d("Temp files created, fildItem list generated");
	        /*
	        for(FileItem fi : items){ 
	                File file = new File(fi.getName()); 
	                fi.write(file); 
	                DebugLog.d(fi.getName()); 
	        } 
			*/
//			InputStream inputStream = entity.getStream();
            BufferedImage bufferedImage = ImageIO.read(items.get(0).getInputStream());
            
            bufferedImage = cropImageToRatio(bufferedImage);
            DebugLog.d("stream connected, starting to rescale");	
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 200, 200, Scalr.OP_ANTIALIAS);
            DebugLog.d("img rescale completed into buffer");
            
    		String userProfile = CarpoolConfig.profileImgPrefix;
    		String imgSize = CarpoolConfig.imgSize_m;
    		String imgName = userProfile+imgSize+id;
    		DebugLog.d("creating new file on EC2");
            imgFile = new File(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png");
            DebugLog.d("dumping img into buffer");
            
            ImageIO.write(bufferedImage, "png", imgFile);
            DebugLog.d("img file write completed, starting to upload to EC2");
            
            //warning: can only call this upload once, as it will delete the image file before it exits
            String path = FileService.uploadUserProfileImg(id, imgFile, imgName);
            DebugLog.d("img uploaded, retriving user");
            User user = UserDaoService.getUserById(id);
            user.setImgPath(path);
            DebugLog.d("updating user");
            UserDaoService.updateUser(user);
            
            jsonObject = JSONFactory.toJSON(user);
			setStatus(Status.SUCCESS_OK);
			DebugLog.d("success response ready");

        } catch (PseudoException e){
        	DebugLog.d(e);
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
        	DebugLog.d(e);
            return this.doException(e);
        }

		DebugLog.d("return");
		Representation result = new JsonRepresentation(jsonObject);

		this.addCORSHeader();
        return result;
	}

	private BufferedImage cropImageToRatio(BufferedImage src){
		int height = src.getHeight();
		int width = src.getWidth();
		BufferedImage dest;
		if (height<width){
			dest = src.getSubimage((width-height)/2, 0, height, height);
		} else {
			dest = src.getSubimage(0, (height-width)/2, width, width);
		}
		return dest;
	}
	
}


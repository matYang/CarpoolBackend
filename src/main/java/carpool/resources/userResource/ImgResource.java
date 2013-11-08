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
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.encryption.ImgCrypto;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
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
                System.out.println(fi.getName()); 
        } 
        setStatus(Status.SUCCESS_OK); 
        return ""; 
}
		
	@Post
	//TODO check for FileRepresentation
	public Representation postImage(Representation entity){
		JSONObject jsonObject = new JSONObject();
		int id = -1;
			
		try {
			this.checkFileEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
			DiskFileItemFactory factory = new DiskFileItemFactory(); 
	        factory.setSizeThreshold(1024000); 
	        RestletFileUpload upload = new RestletFileUpload(factory); 
	        List<FileItem> items = upload.parseRepresentation(entity); 
	        
	        /*
	        for(FileItem fi : items){ 
	                File file = new File(fi.getName()); 
	                fi.write(file); 
	                System.out.println(fi.getName()); 
	        } 
			*/
//			InputStream inputStream = entity.getStream();
            BufferedImage bufferedImage = ImageIO.read(items.get(0).getInputStream());
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 128, 128, Scalr.OP_ANTIALIAS);

            
    		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
    		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
    		String imgName = userProfile+imgSize+id;
            File imgFile = new File(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png");
            
            ImageIO.write(bufferedImage, "png", imgFile);
            
            
            String path = FileService.uploadUserProfileImg(id, imgFile, imgName);
            
            User user = UserDaoService.getUserById(id);
            user.setImgPath(path);
            UserDaoService.updateUser(user);

            jsonObject = JSONFactory.toJSON(user);
			setStatus(Status.SUCCESS_OK);


        } catch (PseudoException e){
        	this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch (Exception e) {
            this.doException(e);
        }

		
		Representation result = new JsonRepresentation(jsonObject);

		this.addCORSHeader();
		this.printResult(result);
        return result;
	}

	
}


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

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
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

import org.imgscalr.Scalr;

import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.encryption.ImgCrypto;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class ImgResource extends PseudoResource{

		
	@Put
	//check for FileRepresentation
	public Representation postImage(Representation entity){
		JSONObject jsonObject = new JSONObject();
		int id = -1;
		String imgPath = "";
		boolean previousImgRemoved = false;
			
		try {
			this.checkEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			String imageNameId = id + "" + Calendar.getInstance().getTimeInMillis();
			
			//give a god damn encrypted image name
			String encryptedString = ImgCrypto.encrypt(imageNameId);
			
        	
			this.validateAuthentication(id);
			imgPath = Constants.imagePathPrefix + encryptedString +  Constants.imagePathSufix;
			
			MediaType mediaType = entity.getMediaType();
			if (mediaType.equals(MediaType.IMAGE_BMP) || mediaType.equals(MediaType.IMAGE_PNG) || mediaType.equals(MediaType.IMAGE_JPEG)){
				InputStream inputStream = entity.getStream(); 
				
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                
                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 150, 100, Scalr.OP_ANTIALIAS);
                
                ImageIO.write(bufferedImage, "png", new File(imgPath));
                
                User user = UserDaoService.getUserById(id);
                String previousPath = user.getImgPath();
                user.setImgPath(imgPath);
                UserDaoService.updateUser(user);
                previousImgRemoved = HelperOperator.removePreviousImg(previousPath);
                
                if (!previousImgRemoved){
                	DebugLog.d("image not removed, path: " + previousPath);
                }
                
                //sending a user obj to front end
				jsonObject = JSONFactory.toJSON(user);
				setStatus(Status.SUCCESS_OK);

			}
			else{
				setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
			}

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


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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.encryption.ImgCrypto;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;




public class ImgResource extends ServerResource{

	@Get 
	public Representation getImage(Representation entity){
		
        int id = -1;
        boolean goOn = false;
        JSONObject jsonObject = new JSONObject();
        String imgPath = "";
        
        try {
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			Common.d("API::GetImage:: " + id);
			
			if (goOn){
	        	imgPath = UserDaoService.getImagePath(id);
	        	if (imgPath != null){
	        		setStatus(Status.SUCCESS_OK);
	        		jsonObject = JSONFactory.toJSON(imgPath);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
	        	}
	        	
	        }
			
		} catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        Representation result = new JsonRepresentation(jsonObject);
        
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 
        return result;
		
	}
		
	@Put
	//check for FileRepresentation
	public Representation postImage(Representation entity){
		JSONObject jsonObject = new JSONObject();
		int id = -1;
		String imgPath = "";
		boolean goOn = false;
		boolean imgPathSet = false;
		boolean previousImgRemoved = false;
		
		if (entity != null && entity.getSize() < Constants.max_imageSize){
			
			try {
				id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				String imageNameId = id + "" + Calendar.getInstance().getTimeInMillis();
				
				//give a god damn encrypted image name
				String encryptedString = ImgCrypto.encrypt(imageNameId);
				
	        	
				if (encryptedString != null && UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
					goOn = true;
				}
				else if (encryptedString == null){
					goOn = false;
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				}
				else{
					goOn = false;
					setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				}
				
				if (goOn){
					imgPath = Constants.imagePathPrefix + encryptedString +  Constants.imagePathSufix;
					
					MediaType mediaType = entity.getMediaType();
					if (mediaType.equals(MediaType.IMAGE_BMP) || mediaType.equals(MediaType.IMAGE_PNG) || mediaType.equals(MediaType.IMAGE_JPEG)){
						InputStream inputStream = entity.getStream(); 
						
	                    BufferedImage bufferedImage = ImageIO.read(inputStream);
	                    
	                    bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 150, 100, Scalr.OP_ANTIALIAS);
	                    
	                    ImageIO.write(bufferedImage, "png", new File(imgPath));
	                    
	                    String previousPath = UserDaoService.getImagePath(id);
	                    imgPathSet = UserDaoService.setImagePath(id, imgPath);
	                    previousImgRemoved = Common.removePreviousImg(previousPath);
	                    
	                    if (!previousImgRemoved){
	                    	//TODO should change to log, set log4j
	                    	Common.d("image not removed, path: " + previousPath);
	                    }
	                    
	                    //once new image path is set, operation successfully, have an image cleaner later on
						if (imgPathSet){
							jsonObject = JSONFactory.toJSON(imgPath);
							setStatus(Status.SUCCESS_OK);
						}
						else{
							setStatus(Status.CLIENT_ERROR_CONFLICT);
						}
					}
					else{
						setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
					}
				}
	        } catch (UserNotFoundException e){
	        	e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
	        } catch (DuplicateSessionCookieException e1){
				//TODO clear cookies, set name and value
				e1.printStackTrace();
				this.getResponse().getCookieSettings().clear();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (SessionEncodingException e){
				//TODO modify session where needed
				e.printStackTrace();
				this.getResponse().getCookieSettings().clear();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (FileNotFoundException e) { 
				e.printStackTrace(); 
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        } catch (IOException e) { 
	        	e.printStackTrace(); 
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        } catch (Exception e1) {
	            e1.printStackTrace();
	            setStatus(Status.SERVER_ERROR_INTERNAL);
	        }

		}
		else if (entity == null){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		else{
        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
        }
		
		
		Representation result = new JsonRepresentation(jsonObject);

        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        try {
            Common.d(result.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
	}

    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        //send anything back will be fine, browser just expects a response
        DMMessage message = new DMMessage();
        Representation result = new JsonRepresentation(message);
        Common.d("exiting Options request");
        setStatus(Status.SUCCESS_OK);
        return result;
    }
	
}


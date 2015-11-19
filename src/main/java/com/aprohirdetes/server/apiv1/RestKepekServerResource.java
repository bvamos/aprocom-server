package com.aprohirdetes.server.apiv1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.server.AproConfig;
import com.aprohirdetes.utils.MongoUtils;

public class RestKepekServerResource extends ServerResource  {
	
	private Hirdetes hirdetes = null;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		String hirdetesId = (String) getRequestAttributes().get("hirdetesId");
		if(hirdetesId!=null) {
			hirdetes = HirdetesHelper.load(hirdetesId);
		}
	}

	@Get("json")
	public RestResponse representJson() throws Exception {
		RestResponse response = new RestResponse();
		
		if(hirdetes==null) {
			response.addError(1031, "Nem letezik a megadott hirdetes");
			response.setSuccess(false);
		} else {
			response.addData("kepek", hirdetes.getKepek());
			response.setSuccess(true);
		}
		
		return response;
	}
	
	@Post
	public RestResponse acceptJson(Representation entity) throws Exception {
		RestResponse response = new RestResponse();
		
		if(hirdetes==null) {
			response.addError(1031, "A megadott hirdetes nem letezik");
			response.setSuccess(false);
			return response;
		}
		
		if(hirdetes.getStatusz()!=Hirdetes.Statusz.JOVAHAGYVA.value()) {
			response.addError(1032, "A megadott hirdetes mar vagy meg nem aktiv, nem modosithato");
			response.setSuccess(false);
			return response;
		}
		
		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				ObjectId kepId = new ObjectId();
				String fileName = hirdetes.getId().toString() + "_" + kepId.toString();
				String fileNamePath = AproConfig.APP_CONFIG.getProperty("WORKDIR")
						+ "/images/" + fileName;

				// The Apache FileUpload project parses HTTP requests which
				// conform to RFC 1867, "Form-based File Upload in HTML". That
				// is, if an HTTP request is submitted using the POST method,
				// and with a content type of "multipart/form-data", then
				// FileUpload can parse that request, and get all uploaded files
				// as FileItem.

				// 1/ Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(10002400);

				// 2/ Create a new file upload handler based on the Restlet
				// FileUpload extension that will parse Restlet requests and
				// generates FileItems.
				RestletFileUpload upload = new RestletFileUpload(factory);
				List<FileItem> items;

				// 3/ Request is parsed by the handler which generates a
				// list of FileItems
				items = upload.parseRequest(getRequest());

				ArrayList<Map<String, String>> kepekJson = new ArrayList<Map<String, String>>();
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// Letezo kepek lekerdezese
						Datastore datastore = MongoUtils.getDatastore();
						Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
						
						query.criteria("hirdetesId").equal(hirdetes.getId());
						
						long sorszam = query.countAll();
						if(sorszam>=6) {
							response.addError(1029, "Maximum 6 kepet lehet feltolteni");
						} else {
							
							// Kep file mentese
							File file = new File(fileNamePath + getSuffix(item.getName()));
							item.write(file);
							
							// Thumbnail keszitese
							Thumbnails.of(file)
					        	.size(140, 140)
					        	.toFile(new File(fileNamePath + ".thumbnail" + getSuffix(item.getName())));
							
							// Adatok adatbazisba mentese
							HirdetesKep kep = new HirdetesKep();
							kep.setId(kepId);
							kep.setSorszam((int)sorszam+1);
							kep.setFileNev(fileName + getSuffix(item.getName()));
							kep.setThumbFileNev(fileName + ".thumbnail" + getSuffix(item.getName()));
							kep.setMeret(item.getSize());
							kep.setContentType(item.getContentType());
							kep.setHirdetesId(hirdetes.getId());
							
							Key<HirdetesKep> id = datastore.save(kep);
							
							// Valasz
							HashMap<String, String> kepJson = new HashMap<String, String>();
							kepJson.put("fileNev0", item.getName());
							if(id != null) {
								kepJson.put("fileNev1", kep.getFileNev());
								kepJson.put("fileNev2", kep.getThumbFileNev());
								kepJson.put("fileMeret", String.valueOf(item.getSize()));
								kepJson.put("sorszam", String.valueOf(kep.getSorszam()));
							} else {
								kepJson.put("hibaUzenet", "Hiba történt a kép mentése közben.");
							}
							kepekJson.add(kepJson);
						}
					}
				}
				// Response data es status beallitasa
				if(kepekJson.size()>0) {
					response.addData("kepek", kepekJson);
				}
				response.setSuccess(true);
			}
			
		} else {
			// POST request with no entity.
			response.addError(1030, "A feltoltott objektum nem lehet ures");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return response;
	}

	/**
	 * Visszaadja a filenevbol a kiterjesztest, azaz az utolso pont utani reszt a ponttal egyutt.
	 * 
	 * @param filename File neve
	 * @return Kiterjesztes ponttal egyutt, vagy ures string
	 */
	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos).toLowerCase();
		}
		return suffix;
	}
}

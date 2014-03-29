package com.aprohirdetes.server.apiv1;

import java.io.File;
import java.util.List;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIKepFeltoltesResource;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;

public class KepFeltoltesServerResource extends ServerResource implements
		APIKepFeltoltesResource {

	private ObjectId hirdetesId = null;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		String hirdetesIdString = (String) this.getRequestAttributes().get(
				"hirdetesId");
		if (hirdetesIdString != null) {
			hirdetesId = new ObjectId(hirdetesIdString);
		}
	}

	public Representation accept(Representation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				ObjectId kepId = new ObjectId();
				String fileName = hirdetesId.toString() + "_" + kepId.toString();
				String fileNamePath = AproApplication.APP_CONFIG
						.getProperty("WORKDIR")
						+ "/"
						+ "images_upload" + "/" + fileName;

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

				JSONObject response = new JSONObject();
				JSONArray filesJson = new JSONArray();
				for (FileItem item : items) {
					if (!item.isFormField()) {
						try {
							// Letezo kepek lekerdezese
							Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
							Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
							
							query.criteria("hirdetesId").equal(hirdetesId);
							
							long sorszam = query.countAll();
							if(sorszam>=6) {
								throw new Exception("Maximum 6 kepet lehet feltolteni.");
							}
							
							// Kep file mentese
							File file = new File(fileNamePath + getSuffix(item.getName()));
							item.write(file);
							
							// Thumbnail keszitese
							Thumbnails.of(file)
					        	.size(160, 160)
					        	.toFile(new File(fileNamePath + "_thumb" + getSuffix(item.getName())));
							
							// Adatok adatbazisba mentese
							HirdetesKep kep = new HirdetesKep();
							kep.setId(kepId);
							kep.setSorszam((int)sorszam+1);
							kep.setFileNev(fileName + getSuffix(item.getName()));
							kep.setThumbFileNev(fileName + "_thumb" + getSuffix(item.getName()));
							kep.setMeret(item.getSize());
							kep.setHirdetesId(hirdetesId);
							
							Key<HirdetesKep> id = datastore.save(kep);
							
							// Valasz
							JSONObject fileJson = new JSONObject();
							fileJson.put("fileNev", kep.getFileNev());
							fileJson.put("thumbFileNev", kep.getThumbFileNev());
							fileJson.put("eredetiFileNev", item.getName());
							if(id != null) {
								fileJson.put("fileMeret", item.getSize());
								fileJson.put("url", getRequest().getRootRef().toString() + "/static/images_upload/" + kep.getFileNev());
								fileJson.put("sorszam", kep.getSorszam());
							} else {
								fileJson.put("hibaUzenet", "Hiba történt a kép mentése közben.");
							}
							filesJson.put(fileJson);
						} catch (Exception e) {
							JSONObject fileJson = new JSONObject();
							fileJson.put("fileNev", fileName + getSuffix(item.getName()));
							fileJson.put("eredetiFileNev", item.getName());
							fileJson.put("hibaUzenet", e.getMessage());
							filesJson.put(fileJson);
						}
					}
				}
				response.put("files", filesJson);
				rep = new JsonRepresentation(response);
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
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

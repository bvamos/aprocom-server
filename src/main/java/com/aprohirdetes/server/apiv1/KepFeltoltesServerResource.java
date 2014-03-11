package com.aprohirdetes.server.apiv1;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIKepFeltoltesResource;
import com.aprohirdetes.server.AproApplication;

public class KepFeltoltesServerResource extends ServerResource implements
		APIKepFeltoltesResource {
	
	private ObjectId hirdetesId = null;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		String hirdetesIdString = (String) this.getRequestAttributes().get("hirdetesId");
		if(hirdetesIdString != null) {
			hirdetesId = new ObjectId(hirdetesIdString);
		}
	}
	
	public Representation accept(Representation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				String fileName = hirdetesId + "_" + new ObjectId().toString();
				String fileNamePath = AproApplication.APP_CONFIG.getProperty("WORKDIR") 
						+ File.separator + "images_upload" 
						+ File.separator + fileName;

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

				// Process only the uploaded item called "files[]" and
				// save it on disk
				boolean found = false;
				for (final Iterator<FileItem> it = items.iterator(); it
						.hasNext() && !found;) {
					FileItem fi = it.next();
					if (fi.getFieldName().equals("files[]")) {
						found = true;
						File file = new File(fileNamePath);
						fi.write(file);
					}
				}

				// Once handled, the content of the uploaded file is sent
				// back to the client.
				JSONObject response = new JSONObject();
				if (found) {
					// Create a new representation
					JSONArray files = new JSONArray();
					JSONObject file = new JSONObject();
					file.put("name", fileName);
					file.put("size", 11);
					files.put(file);
					response.put("files", files);
					rep = new JsonRepresentation(response);
				} else {
					// Some problem occurs, sent back a simple line of text.
					JSONArray files = new JSONArray();
					JSONObject file = new JSONObject();
					file.put("name", fileName);
					file.put("error", "No file uploaded");
					files.put(file);
					response.put("files", files);
					rep = new JsonRepresentation(response);
				}
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
	}

}

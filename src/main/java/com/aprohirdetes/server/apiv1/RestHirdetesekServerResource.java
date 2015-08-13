package com.aprohirdetes.server.apiv1;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.exception.HirdetesValidationException;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesForras;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.utils.MongoUtils;

public class RestHirdetesekServerResource extends ServerResource  {

	@Post("json")
	public RestResponse acceptJson(JsonRepresentation entity) throws Exception {
		RestResponse response = new RestResponse();
		
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			
			// Hirdetes objektum letrehozasa
			Hirdetes hirdetes = new Hirdetes();
			
			// Felado azonositoja. AproApplication.apiAuthFilterben kerul beallitasra
			// Nem feltetlenul azonos a Hirdeto.id-val
			hirdetes.setFeladoId((ObjectId) getRequest().getAttributes().get("feladoId"));
			
			// Hirdetes tipusa. Alapertelmezett: KINAL
			if(requestJson.has("tipus")) {
				String tipus = requestJson.getString("tipus");
				if(!"keres".equalsIgnoreCase(tipus) && !"kinal".equalsIgnoreCase(tipus)) {
					response.setSuccess(false);
					response.addError(1018, "A hirdetes tipusa csak keres vagy kinal lehet");
				} else {
					hirdetes.setTipus(tipus);
				}
			}
			
			// Hirdetes cime (rovid leiras)
			if(!requestJson.has("cim")) {
				response.setSuccess(false);
				response.addError(1017, "A hirdetes cim mezoje nem lehet üres");
			} else {
				hirdetes.setCim(requestJson.getString("cim"));
				response.addData("cim", hirdetes.getCim());
			}
			
			// Hirdetes tartalma: szoveg
			if(!requestJson.has("szoveg")) {
				response.setSuccess(false);
				response.addError(1019, "A hirdetes szoveg mezoje nem lehet üres");
			} else {
				String szoveg = requestJson.getString("szoveg");
				if(szoveg.length()>Hirdetes.MAX_SZOVEG_LENGTH) {
					response.addError(1101, "A hirdetes szovege tul hosszu, ezert a veget levagtuk");
				}
				hirdetes.setSzoveg(szoveg);
				response.addData("szoveg", hirdetes.getSzoveg());
			}
			
			// egyebInfo
			if(requestJson.has("egyebInfo")) {
				String egyebInfo = requestJson.getString("egyebInfo");
				if(egyebInfo.length()>Hirdetes.MAX_EGYEBINFO_LENGTH) {
					response.addError(1102, "A hirdetes egyebInfo mezoje tul hosszu, ezert a veget levagtuk");
				}
				hirdetes.setEgyebInfo(egyebInfo);
				response.addData("egyebInfo", hirdetes.getEgyebInfo());
			}
			
			// ar
			if(requestJson.has("ar")) {
				try {
					int ar = requestJson.getInt("ar");
					if(ar<0) {
						response.setSuccess(false);
						response.addError(1014, "A hirdetes ara nem lehet negativ szam");
					} else {
						hirdetes.setAr(ar);
						response.addData("ar", hirdetes.getAr());
					}
				} catch (JSONException je) {
					response.setSuccess(false);
					response.addError(1020, "A hirdetes ara legyen szam");
				}
			}
			
			// Kategoria
			if(!requestJson.has("kategoria")) {
				response.setSuccess(false);
				response.addError(1021, "A kategoria mezo kotelezo");
			} else {
				String kategoriaUrlNev = requestJson.getString("kategoria");
				Kategoria kategoria = KategoriaCache.getCacheByUrlNev().get(kategoriaUrlNev);
				if(kategoria==null) {
					response.setSuccess(false);
					response.addError(1022, "A megadott kategoria nem letezik: " + kategoriaUrlNev);
				} else {
					hirdetes.setKategoriaId(kategoria.getId());
					response.addData("kategoria", kategoriaUrlNev);
					
					// Egyeb mezok
					if(requestJson.has("egyebMezok")) {
						JSONObject egyebMezokJson = requestJson.getJSONObject("egyebMezok");
						for(String key : JSONObject.getNames(egyebMezokJson)) {
							Attributum attributum = AttributumCache.getAttributum(kategoriaUrlNev, key);
							
							if(attributum != null) {
								hirdetes.getAttributumok().put(key, egyebMezokJson.get(key));
							} else {
								// Kivesszuk a nem letezo attributumot
								egyebMezokJson.remove(key);
							}
						}
						response.addData("egyebMezok", hirdetes.getAttributumok());
					}
				}
			}
			
			// Helyseg
			if(!requestJson.has("helyseg")) {
				response.setSuccess(false);
				response.addError(1023, "A helyseg mezo kotelezo");
			} else {
				String helysegUrlNev = requestJson.getString("helyseg");
				Helyseg helyseg = HelysegCache.getCacheByUrlNev().get(helysegUrlNev);
				if(helyseg==null) {
					response.setSuccess(false);
					response.addError(1024, "A megadott helyseg nem letezik: " + helysegUrlNev);
				} else {
					hirdetes.setHelysegId(helyseg.getId());
					response.addData("helyseg", helysegUrlNev);
				}
			}
			
			
			// Hirdeto
			if(!requestJson.has("hirdeto")) {
				response.setSuccess(false);
				response.addError(1016, "A hirdeto megadasa kotelezo");
			} else {
				JSONObject hirdetoJson = requestJson.getJSONObject("hirdeto");
				
				Hirdeto hirdeto = null;
				// Kell egy uj objektum, hogy a null erteku es a felesleges mezok ne legyenek a valaszban
				HashMap<String, Object> hirdetoData = new HashMap<String, Object>();
				
				if(hirdetoJson.has("id")) {
					// Van Id, megprobaljuk betolteni a Hirdetot Id alapjan
					String hirdetoId = hirdetoJson.getString("id");
					try {
						ObjectId hirdetoObjectId = new ObjectId(hirdetoId);
						hirdeto = HirdetoHelper.load(hirdetoObjectId);
						
						if(hirdeto==null) {
							response.setSuccess(false);
							response.addError(1025, "A megadott hirdeto nem letezik: " + hirdetoId);
						} else {
							hirdetes.setHirdetoId(hirdetoObjectId);
							hirdetoData.put("id", hirdetoId);
						}
					} catch(Exception e) {
						response.setSuccess(false);
						response.addError(1026, "A megadott hirdeto id mezo erteke hibas: " + hirdetoId);
					}
					
				} else {
					// Nincs Id, letrehozunk egy uj Hirdetot
					hirdeto = new Hirdeto();
				}
				
				if(hirdeto!=null) {
					// Van Id alapjan betoltott, vagy uj hirdeto objektum, lehet felulirni a mezoit
					
					if(hirdetoJson.has("email")) {
						hirdeto.setEmail(hirdetoJson.getString("email"));
						hirdetoData.put("email", hirdeto.getEmail());
					} else if(hirdeto.getEmail()==null) {
						response.addError(1015, "A hirdeto email cime nem lehet ures");
					}
					
					if(hirdetoJson.has("nev")) {
						hirdeto.setNev(hirdetoJson.getString("nev"));
						hirdetoData.put("nev", hirdeto.getNev());
					}
					
					if(hirdetoJson.has("iranyitoszam")) {
						hirdeto.setIranyitoSzam(hirdetoJson.getString("iranyitoszam"));
					}
					
					if(hirdetoJson.has("telepules")) {
						hirdeto.setTelepules(hirdetoJson.getString("telepules"));
					}
					
					if(hirdetoJson.has("cim")) {
						hirdeto.setCim(hirdetoJson.getString("cim"));
					}
					
					if(hirdetoJson.has("orszag")) {
						hirdeto.setOrszag(hirdetoJson.getString("orszag"));
					}
					
					hirdetes.setHirdeto(hirdeto);
					response.addData("hirdeto", hirdetoData);
				}
				
				hirdetes.setHirdeto(hirdeto);
			} // Hirdeto vege
			
			// Hirdetes validacio
			try {
				hirdetes.validate();
				HirdetesHelper.validate(hirdetes);
			} catch(HirdetesValidationException hve) {
				response.addError(hve.getEsemenyId(), hve.getMessage());
			}
			
			// Hirdetes mentese, ha nincs hiba
			if(!response.hasError()) {
				// Uj id
				hirdetes.setId(new ObjectId());
				// API-n feladott hirdetest nem kell hitelesiteni
				hirdetes.setHitelesitve(true);
				// Forras beallitasa
				hirdetes.setForras(HirdetesForras.API);
				
				Datastore datastore = MongoUtils.getDatastore();
				Key<Hirdetes> hirdetesKey = datastore.save(hirdetes);
				
				if(hirdetesKey!=null) {
					response.setSuccess(true);
					response.addData("id", hirdetes.getId().toString());
				} else {
					// Hiba a mentes kozben
					response.addError(1027, "Hiba a hirdetes mentese kozben");
				}
			}
			
		} else {
			// POST request with no entity.
			response.addError(1028, "A hirdetes objektum nem lehet ures");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return response;
	}

	
}

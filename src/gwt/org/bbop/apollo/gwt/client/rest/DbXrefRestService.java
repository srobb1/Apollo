package org.bbop.apollo.gwt.client.rest;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Anchor;
import org.bbop.apollo.gwt.client.dto.AnnotationInfo;
import org.bbop.apollo.gwt.client.dto.DbXRefInfoConverter;
import org.bbop.apollo.gwt.client.dto.DbXrefInfo;
import org.bbop.apollo.gwt.shared.FeatureStringEnum;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;

/**
 * Created by ndunn on 1/14/15.
 */
public class DbXrefRestService {

    static String TERM_LOOKUP_SERVER = "http://api.geneontology.org/api/ontology/term/"; // ECO%3A0000315

    public static void saveDbXref(RequestCallback requestCallback, DbXrefInfo dbXrefInfo) {
        RestService.sendRequest(requestCallback, "dbXrefInfo/save", "data=" + DbXRefInfoConverter.convertToJson(dbXrefInfo).toString());
    }

    public static void updateDbXref(RequestCallback requestCallback, AnnotationInfo annotationInfo,DbXrefInfo oldDbXrefInfo,DbXrefInfo newDbXrefInfo) {

        JSONArray featuresArray = new JSONArray();
        JSONObject featureObject = new JSONObject();
        String featureUniqueName = annotationInfo.getUniqueName();
        featureObject.put(FeatureStringEnum.UNIQUENAME.getValue(), new JSONString(featureUniqueName));
        JSONArray oldDbXrefJsonArray = new JSONArray();
        JSONObject oldDbXrefJsonObject = new JSONObject();
        oldDbXrefJsonObject.put(FeatureStringEnum.TAG.getValue(), new JSONString(oldDbXrefInfo.getTag()));
        oldDbXrefJsonObject.put(FeatureStringEnum.VALUE.getValue(), new JSONString(oldDbXrefInfo.getValue()));
        oldDbXrefJsonArray.set(0, oldDbXrefJsonObject);
        featureObject.put(FeatureStringEnum.OLD_DBXREFS.getValue(), oldDbXrefJsonArray);

        JSONArray newDbXrefJsonArray = new JSONArray();
        JSONObject newDbXrefJsonObject = new JSONObject();
        newDbXrefJsonObject.put(FeatureStringEnum.TAG.getValue(), new JSONString(newDbXrefInfo.getTag()));
        newDbXrefJsonObject.put(FeatureStringEnum.VALUE.getValue(), new JSONString(newDbXrefInfo.getValue()));
        newDbXrefJsonArray.set(0, newDbXrefJsonObject);
        featureObject.put(FeatureStringEnum.NEW_DBXREFS.getValue(), newDbXrefJsonArray);
        featuresArray.set(0, featureObject);

        JSONObject requestObject = new JSONObject();
//        requestObject.put("operation", new JSONString("update_variant_info"));
        requestObject.put(FeatureStringEnum.TRACK.getValue(), new JSONString(annotationInfo.getSequence()));
//            requestObject.put(FeatureStringEnum.CLIENT_TOKEN.getValue(), new JSONString(Annotator.getClientToken()));
        requestObject.put(FeatureStringEnum.FEATURES.getValue(), featuresArray);

        RestService.sendRequest(requestCallback, "annotationEditor/updateDbxref", "data=" + requestObject.toString());
    }

    public static void deleteDbXref(RequestCallback requestCallback, DbXrefInfo dbXrefInfo) {
        RestService.sendRequest(requestCallback, "dbXrefInfo/delete", "data=" + DbXRefInfoConverter.convertToJson(dbXrefInfo).toString());
    }

    public static void getDbXref(RequestCallback requestCallback, String featureUniqueName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uniqueName",new JSONString(featureUniqueName));
        RestService.sendRequest(requestCallback, "dbXrefInfo/", "data=" + jsonObject.toString());
    }

    private static void lookupTerm(RequestCallback requestCallback, String url) {
        RestService.generateBuilder(requestCallback,RequestBuilder.GET,url);
    }

    public static void lookupTerm(final Anchor anchor, String evidenceCurie) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                JSONObject returnObject = JSONParser.parseStrict(response.getText()).isObject();
                anchor.setHTML(returnObject.get("label").isString().stringValue());
                if(returnObject.containsKey("definition")){
                    anchor.setTitle(returnObject.get("definition").isString().stringValue());
                }
                else{
                    anchor.setTitle(returnObject.get("label").isString().stringValue());
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                Bootbox.alert("Failed to do lookup: "+exception.getMessage());
            }
        };

        DbXrefRestService.lookupTerm(requestCallback,TERM_LOOKUP_SERVER + evidenceCurie);
    }
}
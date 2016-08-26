package org.csi.yucca.datamanagementapi.test.unit;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.csi.yucca.datamanagementapi.test.RestBase;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HttpDeleteDatasetIT extends RestBase {

	@BeforeClass
	public void setUpSecretObject() throws IOException {
		super.setUpSecretObject("/testSecret.json");
	}
	
	@DataProvider(name = "ValidationDeleteDatasetTest")
	public Iterator<Object[]> getFromJson() {
		return super.getFromJson("/ValidationDeleteDatasetTest.json");
	}
	
	@Test(dataProvider = "ValidationDeleteDatasetTest")
	public void deleteDatasetTesting(JSONObject dato) {
		/*
			if (dato.optBoolean("rt.toskip") || dato.optBoolean("rt.httptoskip"))
				throw new SkipException("TODO in future version");
			//RequestSpecification rs = given().body(dato.get("dmapi.message")).contentType(ContentType.JSON);
		*/
		
		RequestSpecification rs = given().contentType(ContentType.JSON);

		Response rsp = rs.log().all().when().delete(dato.getString("dmapi.url") + "metadata/clearDataset/" + dato.get("dmapi.tenant") + "/" + dato.get("dmapi.idDataset") + (dato.opt("dmapi.datasetVersion") == null ? "" : dato.get("dmapi.datasetVersion")));
		rsp.then().statusCode(HttpStatus.SC_OK);
		
		rsp.then().log().all().body(dato.optString("dmapi.ko_ok"), Matchers.equalTo(1));

		/*
		 if (StringUtils.isNotEmpty(dato.optString("rt.errorCode")))
			rsp.then().body("error_code", Matchers.equalTo(dato.optString("rt.errorCode")));
		*/
	}
}
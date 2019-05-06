package client.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;

public class RestHttpClientProductApp {

	// main Method
	public static void main(String[] args) throws Exception {

		////////////////////////////////////////////////////////////////////////////////////////////
		// 주석을 하나씩 처리해가며 실습
		////////////////////////////////////////////////////////////////////////////////////////////
//		System.out.println("\n====================================\n");
//		// 상품 추가 - Http Post 방식 Request : CodeHaus lib 사용		
//		RestHttpClientProductApp.getProductTest();
		
//		System.out.println("\n====================================\n");
//		// 상품 추가 - Http Post 방식 Request : CodeHaus lib 사용		
//		RestHttpClientProductApp.addProductTest();

//		System.out.println("\n====================================\n");
//		// 유저 리스트 가져오기 - Http Get 방식 Request : CodeHaus lib 사용
//		RestHttpClientProductApp.getProductListTest();

//		System.out.println("\n====================================\n");
//		// 유저 수정 - Http Post 방식 Request : CodeHaus lib 사용		
		RestHttpClientProductApp.updateProductTest();
	}
	
//================================================================//
	public static void getProductTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/product/json/getProduct";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		int prodNo = 10000;

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonValue = objectMapper.writeValueAsString(prodNo);
		HttpEntity httpEntity = new StringEntity(jsonValue, "utf-8");

		httpPost.setEntity(httpEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);

		// ==> Response 확인
		System.out.println(httpResponse);
		System.out.println();

		// ==> Response 중 entity(DATA) 확인
		httpEntity = httpResponse.getEntity();

		// ==> InputStream 생성
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// ==> API 확인 : Stream 객체를 직접 전달
		JSONObject jsonobj = (JSONObject) JSONValue.parse(br);
		System.out.println(jsonobj);

		Product product = objectMapper.readValue(jsonobj.toString(), Product.class);
		System.out.println(product);
	}
	
	public static void addProductTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/product/json/addProduct";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		// [ 방법 3 : codehaus 사용]
		Product product = new Product();
		product.setProdName("추가확인");
		product.setProdDetail("post");
		product.setManuDate("2019-05-06");
		product.setPrice(1234567);
		
		ObjectMapper objectMapper01 = new ObjectMapper();
		// Object ==> JSON Value 로 변환
		String jsonValue = objectMapper01.writeValueAsString(product);
		HttpEntity httpEntity01 = new StringEntity(jsonValue, "utf-8");

		httpPost.setEntity(httpEntity01);
		HttpResponse httpResponse = httpClient.execute(httpPost);

		// ==> Response 확인
		System.out.println(httpResponse);
		System.out.println();

		// ==> Response 중 entity(DATA) 확인
		HttpEntity httpEntity = httpResponse.getEntity();

		// ==> InputStream 생성
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// ==> API 확인 : Stream 객체를 직접 전달
		JSONObject jsonObject = (JSONObject) JSONValue.parse(br);
		System.out.println(jsonObject);
		
		product = objectMapper01.readValue(jsonObject.toString(), Product.class);
		System.out.println(product);
	}

	public static void getProductListTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/product/json/listProduct";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		Search search = new Search();
		search.setCurrentPage(3);
		search.setPageSize(3);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonValue = objectMapper.writeValueAsString(search);
		HttpEntity httpEntity = new StringEntity(jsonValue, "utf-8");

		httpPost.setEntity(httpEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);

		// ==> Response 확인
		System.out.println(httpResponse);
		System.out.println();

		// ==> Response 중 entity(DATA) 확인
		httpEntity = httpResponse.getEntity();

		// ==> InputStream 생성
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// ==> API 확인 : Stream 객체를 직접 전달
		JSONObject jsonobj = (JSONObject) JSONValue.parse(br);
		System.out.println(jsonobj);

		List<Product> list = objectMapper.readValue(jsonobj.get("list").toString(), new TypeReference<List<Product>>() {
		});
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Product[" + i + "] : " + list.get(i));
		}
		System.out.println("totalCount : " + jsonobj.get("totalCount"));
	}

	public static void updateProductTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/product/json/updateProduct";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		Product product = new Product();
		product.setProdName("update");
		product.setProdDetail("prodDetail");
		product.setManuDate("2019-05-07");
		product.setPrice(7654321);
		product.setProdNo(10008);

		ObjectMapper objectMapper = new ObjectMapper();
		// Object ==> JSON Value 로 변환
		String jsonValue = objectMapper.writeValueAsString(product);
		HttpEntity httpEntity01 = new StringEntity(jsonValue, "utf-8");

		httpPost.setEntity(httpEntity01);
		HttpResponse httpResponse = httpClient.execute(httpPost);

		// ==> Response 확인
		System.out.println(httpResponse);
		System.out.println();

		// ==> Response 중 entity(DATA) 확인
		HttpEntity httpEntity = httpResponse.getEntity();

		// ==> InputStream 생성
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// ==> API 확인 : Stream 객체를 직접 전달
		boolean result = (boolean) JSONValue.parse(br);
		System.out.println(result);

		
	}
}
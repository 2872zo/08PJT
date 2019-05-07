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
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;

public class RestHttpClientPurchaseApp {

	// main Method
	public static void main(String[] args) throws Exception {

		////////////////////////////////////////////////////////////////////////////////////////////
		// 주석을 하나씩 처리해가며 실습
		////////////////////////////////////////////////////////////////////////////////////////////
//		System.out.println("\n====================================\n");
//		// 구매 추가 - Http Post 방식 Request : CodeHaus lib 사용		
//		RestHttpClientPurchaseApp.getPurchaseTest();
		
//		System.out.println("\n====================================\n");
//		// 구매 추가 - Http Post 방식 Request : CodeHaus lib 사용		
//		RestHttpClientPurchaseApp.addPurchaseTest();

//		System.out.println("\n====================================\n");
//		// 구매 리스트 가져오기 - Http Get 방식 Request : CodeHaus lib 사용
//		RestHttpClientPurchaseApp.getPurchaseListTest();

//		System.out.println("\n====================================\n");
//		// 구매 수정 - Http Post 방식 Request : CodeHaus lib 사용		
//		RestHttpClientPurchaseApp.updatePurchaseTest();
		
//		System.out.println("\n====================================\n");
//		// 구매 상태 수정 - Http Post 방식 Request : CodeHaus lib 사용		
		RestHttpClientPurchaseApp.updateTranCodeTest();
	}
	
//================================================================//
	public static void getPurchaseTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/purchase/json/getPurchase";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		int tranNo = 10054;

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonValue = objectMapper.writeValueAsString(tranNo);
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

		Purchase purchase = objectMapper.readValue(jsonobj.toString(), Purchase.class);
		System.out.println(purchase);
	}
	
	public static void addPurchaseTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/purchase/json/addPurchase";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		// [ 방법 3 : codehaus 사용]
		User user = new User();
		user.setUserId("user01");
		
		Product product = new Product();
		product.setProdNo(10000);
		
		Purchase purchase = new Purchase();
		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		purchase.setPaymentOption("0");
		purchase.setReceiverName("user01");
		purchase.setReceiverPhone("01098245555");
		purchase.setDlvyAddr("비트 강남");
		purchase.setDlvyRequest("add확인용");
		purchase.setTranCode("1");
		purchase.setDlvyDate("2019-05-07");
		
		
		ObjectMapper objectMapper01 = new ObjectMapper();
		// Object ==> JSON Value 로 변환
		String jsonValue = objectMapper01.writeValueAsString(purchase);
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

	public static void getPurchaseListTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/purchase/json/listPurchase";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(3);
		search.setUserId("user01");

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

		List<Product> list = objectMapper.readValue(jsonobj.get("list").toString(), new TypeReference<List<Purchase>>() {
		});
		for (int i = 0; i < list.size(); i++) {
			System.out.println("purchase[" + i + "] : " + list.get(i));
		}
		System.out.println("totalCount : " + jsonobj.get("totalCount"));
	}

	public static void updatePurchaseTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/purchase/json/updatePurchase";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		User user = new User();
		user.setUserId("user01");
		
		Product product = new Product();
		product.setProdNo(10000);
		
		Purchase purchase = new Purchase();
		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		purchase.setPaymentOption("1");
		purchase.setReceiverName("user01");
		purchase.setReceiverPhone("66655551111");
		purchase.setDlvyAddr("강남 비트");
		purchase.setDlvyRequest("update확인용");
		purchase.setTranCode("1");
		purchase.setDlvyDate("2019-05-15");
		purchase.setTranNo(10056);

		ObjectMapper objectMapper = new ObjectMapper();
		// Object ==> JSON Value 로 변환
		String jsonValue = objectMapper.writeValueAsString(purchase);
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
	
	public static void updateTranCodeTest() throws Exception {
		// HttpClient : Http Protocol 의 client 추상화
		HttpClient httpClient = new DefaultHttpClient();

		String url = "http://127.0.0.1:8080/purchase/json/updateTranCode";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		Purchase purchase = new Purchase();
		purchase.setTranCode("1");
		purchase.setTranNo(10056);

		ObjectMapper objectMapper = new ObjectMapper();
		// Object ==> JSON Value 로 변환
		String jsonValue = objectMapper.writeValueAsString(purchase);
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
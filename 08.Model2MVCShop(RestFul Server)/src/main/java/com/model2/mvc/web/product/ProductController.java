package com.model2.mvc.web.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.CommonUtil;
import com.model2.mvc.common.util.HttpUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@Controller
@RequestMapping("/product/*")
public class ProductController {
	@Autowired
	@Qualifier("productService")
	ProductService productService;

	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;

	@Value("#{commonProperties['pageSize']}")
	int pageSize;

	public ProductController() {
		System.out.println(this.getClass());
	}

	@RequestMapping("addProduct")
	public String addProduct(@ModelAttribute("product") Product product) throws Exception {

		productService.addProduct(product);

		return "forward:/product/confirmProduct.jsp";
	}

	@RequestMapping("getProduct")
	public String getProduct(@RequestParam("prodNo") int prodNo, 
			@RequestParam(value="menu", required=false) String menu,
			@CookieValue(value = "history", required = false) Cookie cookie,
			Map<String,Object> map,
			HttpServletResponse response) throws Exception {
		System.out.println("\n==>getProudct Start.........");
		
		String targetURI = null;

		Product product = productService.getProduct(prodNo);

		// list 형태로 product 전달
		// domain에 toList() 추가
		map.put("list", product.toList());
		map.put("product", product);
		
		if (menu == null || menu.equals("search")) {
			targetURI = "forward:/product/getProduct.jsp";
		} else if (menu.equals("manage")) {
			targetURI = "forward:/product/updateProductView";
		}

		if (cookie == null) {
			cookie = new Cookie("history", String.valueOf(prodNo));
		} else if(cookie.getValue().indexOf(String.valueOf(prodNo)) == -1){
			cookie = new Cookie("history", cookie.getValue() +","+prodNo);
		}
		
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		
		System.out.println("\n==>getProudct End.........");

		return targetURI;
	}

	@RequestMapping(value="listProduct")
	public String getProductList(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,	Search search,
									@RequestParam("menu") String menu, Map<String, Object> resultMap,
									@RequestParam(value="pageSize", defaultValue="0") int pageSize
									,HttpServletRequest request) throws Exception {
		
		System.out.println("\n==>listProudct-GET Start.........");
		if(request.getMethod().equals("GET")) {
			currentPage = Integer.parseInt(request.getParameter("page")!=null?request.getParameter("page"):"1");
			if(!(CommonUtil.null2str(search.getSearchKeyword()).equals(""))){
				search.setSearchKeyword(HttpUtil.convertKo(search.getSearchKeyword()));
			}
		}

		// currentPage
		search.setCurrentPage(currentPage);
		// pageSize
		if(pageSize == 0) {
			System.out.println("pageSize : 0");
			search.setPageSize(this.pageSize);
		}else {
			search.setPageSize(pageSize);
		}
		

		
		System.out.println("getProductList - search : " + search);
		
		/// 4.DB에 접속하여 결과값을 Map으로 가져옴
		Map<String,Object> map = productService.getProductList(search);

		/// 5.pageView를 위한 객체
		Page resultPage = new Page(currentPage, ((Integer) map.get("totalCount")).intValue(), pageUnit, search.getPageSize());

		System.out.println("ListProductAction-resultPage : " + resultPage);
		System.out.println("ListProductAction-list.size() : " + ((List) map.get("list")).size());

		/// 6.JSP에 출력을 하기위한 설정들
		// searchOptionList 설정
		List<String> searchOptionList = new Vector<String>();
		searchOptionList.add("상품번호");
		searchOptionList.add("상품명");
		searchOptionList.add("상품가격");

		// title 설정
		String title = null;
		if (menu.equals("search")) {
			title = "상품 목록 조회";
		} else if(menu.equals("manage")) {
			title = "판매 목록 관리";
		}

		// colum 설정
		List<String> columList = new ArrayList<String>();
		columList.add("No");
		columList.add("상품명");
		columList.add("가격");
		columList.add("등록일");
		columList.add("현재상태");

		// UnitList 설정
		List unitList = makeProductList(menu, (List<Product>) map.get("list"), currentPage);

		// 출력을 위한 Obejct들
		
		resultMap.put("title", title);
		resultMap.put("columList", columList);
		resultMap.put("unitList", unitList);
		resultMap.put("searchOptionList", searchOptionList);
		resultMap.put("resultPage", resultPage);
		
		System.out.println("\n==>listProudct-GET End.........");
		
		return "forward:/product/listProduct.jsp";
	}
	

	@RequestMapping("updateProduct")
	public String updateProduct(@RequestParam("prodNo") int prodNo, @ModelAttribute Product product,
			Map<String,String> map,	HttpServletRequest request) throws Exception {

		productService.updateProduct(product);

		map.put("prodNo", String.valueOf(prodNo));
		map.put("menu", "search");
		
		return "redirect:/product/getProduct";
	}

	@RequestMapping("updateProductView")
	public String updateProductView(@RequestParam("prodNo") int prodNo, Map<String, Object> map) throws Exception {
		map.put("product", productService.getProduct(prodNo));
		
		return "forward:/product/updateProductView.jsp";
	}
	
	private List makeProductList(String menu, List<Product> productList, int currentPage) {
		List<List> unitList = new Vector<List>();
		List<String> UnitDetail = null;
		String aTagEnd = "</a>";
		
		for (int i = 0; i < productList.size(); i++) {
			System.out.println(productList.get(i));
			UnitDetail = new Vector<String>();
			//1.제품 순서 번호
			UnitDetail.add(String.valueOf(i + 1));
			//2.제품 이름
			String aTagGetProductStart = "<a href='/product/getProduct?prodNo=" + productList.get(i).getProdNo() + "\'>";
			UnitDetail.add(aTagGetProductStart + productList.get(i).getProdName() + aTagEnd);
			//3.제품 가격
			UnitDetail.add(String.valueOf(productList.get(i).getPrice()));
			//4.제품 등록 날짜
			UnitDetail.add(String.valueOf(productList.get(i).getRegDate()));
			//5.제품 상태
			if (menu.equals("manage")) {
				if(productList.get(i).getTranCount() != 0) {
					String aTagSaleListStart = "<a href='/purchase/listSale?prodNo=" + productList.get(i).getProdNo() + "\'>";
					UnitDetail.add(aTagSaleListStart + "거래 건수 : " + productList.get(i).getTranCount() + aTagEnd);
				}
			} else {
				if (productList.get(i).getStock() > 0) {
					UnitDetail.add("판매중");
				} else {
					UnitDetail.add("재고없음");
				}
			}

			
			
			
			//1~5과정을 통해 만들어진 리스트를 삽입
			unitList.add(UnitDetail);
		}
		return unitList;
	}
}

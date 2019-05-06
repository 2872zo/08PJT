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
	public String addProduct(@ModelAttribute("product") Product product, HttpServletRequest request) throws Exception {

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

		// list ���·� product ����
		// domain�� toList() �߰�
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

	@RequestMapping(value="listProduct",method=RequestMethod.GET)
	public String getProductList(@RequestParam(value = "page", defaultValue = "1") int currentPage,	Search search,
									@RequestParam("menu") String menu, Map<String, Object> resultMap,
									@RequestParam(value="pageSize", defaultValue="0") int pageSize) throws Exception {
		
		System.out.println("\n==>listProudct-GET Start.........");
		if(!(CommonUtil.null2str(search.getSearchKeyword()).equals(""))){
			search.setSearchKeyword(HttpUtil.convertKo(search.getSearchKeyword()));
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
		
		/// 4.DB�� �����Ͽ� ������� Map���� ������
		Map<String,Object> map = productService.getProductList(search);

		/// 5.pageView�� ���� ��ü
		Page resultPage = new Page(currentPage, ((Integer) map.get("totalCount")).intValue(), pageUnit, pageSize);

		System.out.println("ListProductAction-resultPage : " + resultPage);
		System.out.println("ListProductAction-list.size() : " + ((List) map.get("list")).size());

		/// 6.JSP�� ����� �ϱ����� ������
		// searchOptionList ����
		List<String> searchOptionList = new Vector<String>();
		searchOptionList.add("��ǰ��ȣ");
		searchOptionList.add("��ǰ��");
		searchOptionList.add("��ǰ����");

		// title ����
		String title = null;
		if (menu.equals("search")) {
			title = "��ǰ ��� ��ȸ";
		} else {
			title = "�Ǹ� ��� ����";
		}

		// colum ����
		List<String> columList = new ArrayList<String>();
		columList.add("No");
		columList.add("��ǰ��");
		columList.add("����");
		columList.add("�����");
		columList.add("�������");

		// UnitList ����
		List unitList = makeProductList(menu, (List<Product>) map.get("list"), currentPage);

		// ����� ���� Obejct��
		
		resultMap.put("title", title);
		resultMap.put("columList", columList);
		resultMap.put("unitList", unitList);
		resultMap.put("searchOptionList", searchOptionList);
		resultMap.put("resultPage", resultPage);
		
		System.out.println("\n==>listProudct-GET End.........");
		
		return "forward:/product/listProduct.jsp";
	}
	
	@RequestMapping(value="listProduct",method=RequestMethod.POST)
	public String getProductList(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,	Search search,
								Map<String, Object> resultMap,	@RequestParam("menu") String menu,
								@RequestParam(value="pageSize", defaultValue="0") int pageSize) throws Exception {
		
		System.out.println("\n==>listProudct-POST Start.........");
		// currentPage
		search.setCurrentPage(currentPage);
		// pageSize
		if(pageSize == 0) {
			System.out.println("pageSize : 0");
			search.setPageSize(this.pageSize);
		}else {
			search.setPageSize(pageSize);
		}
		

		/// 4.DB�� �����Ͽ� ������� Map���� ������
		Map<String, Object> map = productService.getProductList(search);

		/// 5.pageView�� ���� ��ü
		Page resultPage = new Page(currentPage, ((Integer) map.get("totalCount")).intValue(), pageUnit, pageSize);

		System.out.println("ListProductAction-resultPage : " + resultPage);
		System.out.println("ListProductAction-list.size() : " + ((List) map.get("list")).size());

		/// 6.JSP�� ����� �ϱ����� ������
		// searchOptionList ����
		List<String> searchOptionList = new Vector<String>();
		searchOptionList.add("��ǰ��ȣ");
		searchOptionList.add("��ǰ��");
		searchOptionList.add("��ǰ����");

		// title ����
		String title = null;
		if (menu.equals("search")) {
			title = "��ǰ ��� ��ȸ";
		} else {
			title = "�Ǹ� ��� ����";
		}

		// colum ����
		List<String> columList = new ArrayList<String>();
		columList.add("No");
		columList.add("��ǰ��");
		columList.add("����");
		columList.add("�����");
		columList.add("�������");

		// UnitList ����
		List unitList = makeProductList(menu, (List<Product>) map.get("list"), currentPage);

		// ����� ���� Object
		resultMap.put("title", title);
		resultMap.put("columList", columList);
		resultMap.put("unitList", unitList);
		resultMap.put("searchOptionList", searchOptionList);
		resultMap.put("resultPage", resultPage);

		System.out.println("\n==>listProudct-POST End.........");
		
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
			//1.��ǰ ���� ��ȣ
			UnitDetail.add(String.valueOf(i + 1));
			//2.��ǰ �̸�
			//a�±� ���� Ȯ��
			if (!(menu.equals("manage") && productList.get(i).getProTranCode() != null)) {
				String aTagGetProductStart 
					= "<a href='/product/getProduct?prodNo=" + productList.get(i).getProdNo() + "&menu=" + menu + "'>";
				
				UnitDetail.add(aTagGetProductStart + productList.get(i).getProdName() + aTagEnd);
			} else {
				UnitDetail.add(productList.get(i).getProdName());
			}
			//3.��ǰ ����
			UnitDetail.add(String.valueOf(productList.get(i).getPrice()));
			//4.��ǰ ��� ��¥
			UnitDetail.add(String.valueOf(productList.get(i).getRegDate()));
			//5.��ǰ ����
			if (productList.get(i).getProTranCode() != null) {
				if (menu.equals("manage")) {
					switch (productList.get(i).getProTranCode()) {
					case "1":
						String aTagUpdateTranCodeStart = "<a href=\"javascript:fncUpdateTranCodeByProd("
								+ currentPage + "," + productList.get(i).getProdNo() + ");\">";
						UnitDetail.add("����غ���&nbsp;" + aTagUpdateTranCodeStart + "��� ���" + aTagEnd);
						break;
					case "2":
						UnitDetail.add("�����");
						break;
					case "3":
						UnitDetail.add("�ŷ��Ϸ�");
						break;
					}
				} else {
					UnitDetail.add("������");
				}
			} else {
				UnitDetail.add("�Ǹ���");
			}
			//1~5������ ���� ������� ����Ʈ�� ����
			unitList.add(UnitDetail);
		}
		return unitList;
	}
}

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
<title>구매 목록조회</title>

<link rel="stylesheet" href="/css/admin.css" type="text/css">

<script type="text/javascript">
	function fncGetPurchaseList(currentPage) {
		document.detailForm.currentPage.value = currentPage;
		document.detailForm.menu.value =  "${param.menu}";
		
		document.detailForm.submit();
	}
	
	function fncUpdatePurchaseCode(currentPage,tranNo,tranCode){		
		var URI = "/purchase/updateTranCode?page="+currentPage+"&tranNo="+tranNo+"&tranCode="+tranCode;
		
		console.log(URI);
		
		location.href = URI;
	}
	
	function fncSortList(currentPage, sortCode){
		document.detailForm.currentPage.value = currentPage;
		document.detailForm.menu.value = "${param.menu}";
		document.detailForm.sortCode.value = sortCode;

		document.detailForm.submit();
	}

	function fncHiddingEmptyStock(currentPage, hiddingEmptyStock){
		document.detailForm.currentPage.value = currentPage;
		document.detailForm.hiddingEmptyStock.value = hiddingEmptyStock;

		document.detailForm.submit();
		
	}


	function fncResetSearchCondition(){
		location.href = "/purchase/listPurchase";
	}
</script>
</head>

<body bgcolor="#ffffff" text="#000000">

<div style="width: 98%; margin-left: 10px;">

<form name="detailForm" action="/purchase/listPurchase" method="post">

<%-- list출력 부분 --%>
<c:import url="../common/listPrinter.jsp">
	<c:param name="domainName" value="Purchase"/>
</c:import>

<table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 10px;">
	<tr>
		<td align="center">
		 <input type="hidden" id="currentPage" name="currentPage" value=""/>
		 <input type="hidden" id="menu" name="menu" value=""/>
		 <input type="hidden" id="sortCode" name="sortCode" value="${search.sortCode}"/>
		 <input type="hidden" id="hiddingEmptyStock" name="hiddingEmptyStock" value="${search.hiddingEmptyStock}"/>
		
		 <c:import url="../common/pageNavigator.jsp">
			<c:param name="domainName" value="Purchase"/>
		</c:import>	
			
		</td>
	</tr>
	<tr>
		<td align="center">
			<a href="javascript:fncSortList(${resultPage.currentPage},4)">구매 취소된 상품</a>
			&nbsp;
			<a href="javascript:fncSortList(${resultPage.currentPage},1)">배송 준비중인 상품</a>
			&nbsp;
			<a href="javascript:fncSortList(${resultPage.currentPage},2)">배송중인 상품</a>
			&nbsp;
			<a href="javascript:fncSortList(${resultPage.currentPage},3)">거래 완료된 상품</a>
		</td>
	</tr>
	<tr>
		<td align="center">
			<a href="javascript:fncHiddingEmptyStock(${resultPage.currentPage},true)">거래중인 상품만 보기</a>
			&nbsp;
			<a href="javascript:fncHiddingEmptyStock(${resultPage.currentPage},false)">모든 상품 보기</a>
		</td>
	</tr>
	<tr>
		<td align="center">
			<a href="javascript:fncResetSearchCondition();">검색 조건 초기화</a>
		</td>
	</tr>
</table>

<!--  페이지 Navigator 끝 -->
</form>

</div>

</body>
</html>
package vvs_assignment_htmlunit;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static vvs_assignment_htmlunit.HtmlUnitVariables.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

// For the test to run properly run the TestSuiteHtmlUnit

public class HtmlAddingCustomerWithSaleAndDelivery {

	@After
	public void removeAddedCustomer() throws IOException {
		WebRequest webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"RemoveCustomerPageController"), HttpMethod.POST);
		String formData = String.format("vat=%s", VAT_5);
        webRequest.setRequestBody(formData);
        HtmlPage reportPage;
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        assumeFalse(reportPage.asText().contains(VAT_5));
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		HtmlPage operatedPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		assumeFalse(operatedPage.asText().contains(VAT_5));
	}
	
	@Test
	public void addingCustomerWithSaleAndDeliveryTest() throws IOException {
		// Adding a new customer
		WebRequest webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddCustomerPageController"), HttpMethod.POST);
		String formData = String.format("vat=%s&designation=%s&phone=%s", VAT_5, DESIGNATION_5, PHONE_5);
        webRequest.setRequestBody(formData);
        HtmlPage reportPage;
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        String textReportPage = reportPage.asText();
        assertTrue(textReportPage.contains(DESIGNATION_5));
        assertTrue(textReportPage.contains(PHONE_5));
        
        // Adding an address for him
        boolean addressAlreadyAdded = false;
        
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetCustomerPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		HtmlTable table = reportPage.getFirstByXPath("//table");
		if (table != null && table.asText().contains(ADDRESS_5_3)) addressAlreadyAdded = true;
		
		if (!addressAlreadyAdded) {
			HtmlAnchor addAddressLink = page.getAnchorByHref("addAddressToCustomer.html");
			HtmlPage operatedPage = (HtmlPage) addAddressLink.openLinkInNewWindow();
			
			HtmlForm addAddressForm = operatedPage.getForms().get(0);
			HtmlInput vatInput = addAddressForm.getInputByName("vat");
			HtmlInput addressInput = addAddressForm.getInputByName("address");
			HtmlInput doorInput = addAddressForm.getInputByName("door");
			HtmlInput postalCodeInput = addAddressForm.getInputByName("postalCode");
			HtmlInput localityInput = addAddressForm.getInputByName("locality");
			HtmlInput submitButton = addAddressForm.getInputByValue("Insert");
			
			vatInput.setValueAttribute(VAT_5);
			addressInput.setValueAttribute(ADDRESS_5_3);
			doorInput.setValueAttribute(DOOR_5_3);
			postalCodeInput.setValueAttribute(POSTAL_CODE_5_3);
			localityInput.setValueAttribute(LOCALITY_5_3);
			submitButton.click();
		}
        
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetCustomerPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		table = reportPage.getFirstByXPath("//table");
		assertTrue(table.asText().contains(ADDRESS_5_3));
		
		/*// Opening a new sale for him
        HtmlPage reportPage;
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetSalePageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("customerVat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Sales"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		HtmlTable table = reportPage.getFirstByXPath("//table");
		int numberOfRowsBefore;
		List<String> idBeforeList = new ArrayList<String>();
		if (table == null) {
			numberOfRowsBefore = 1;
			idBeforeList = null;
		}
		else {
			numberOfRowsBefore = table.getRowCount();
			idBeforeList = new ArrayList<String>();
	        for (HtmlTableRow row : table.getRows()) {
	        	idBeforeList.add(row.getCell(0).asText());
	        }
		}*/
		
		// Opening a new sale for him
		// Checking the initial sales
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetSalePageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("customerVat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Sales"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		table = reportPage.getFirstByXPath("//table");
		int numberOfRowsBefore;
		List<String> idBeforeList = new ArrayList<String>();
		if (table == null) {
			numberOfRowsBefore = 1;
			idBeforeList = null;
		}
		else {
			numberOfRowsBefore = table.getRowCount();
			idBeforeList = new ArrayList<String>();
	        for (HtmlTableRow row : table.getRows()) {
	        	idBeforeList.add(row.getCell(0).asText());
	        }
		}
		
		// Adding a sale
		webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddSalePageController"), HttpMethod.POST);
		formData = String.format("customerVat=%s", VAT_5);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        table = reportPage.getFirstByXPath("//table");
        assertTrue(table != null);
        assertEquals(numberOfRowsBefore + 1, table.getRowCount());
        
        String openedSaleId = null;
        int openedSaleRowIndex = 0;
        
        if (idBeforeList == null) {
        	openedSaleId = table.getRow(1).getCell(0).asText();
        	openedSaleRowIndex = 1;
        }
        else {
        	int rowIndex = 0;
            for (HtmlTableRow row : table.getRows()) {
            	if (rowIndex < idBeforeList.size() && row.getCell(0).asText() != idBeforeList.get(rowIndex)) {
            		openedSaleId = row.getCell(0).asText();
            		openedSaleRowIndex = rowIndex;
            	}
            	++rowIndex;
            }
            if (openedSaleId == null) openedSaleId = table.getRow(table.getRowCount()-1).getCell(0).asText();
            assertTrue(openedSaleId != null);
        }
        
        // Checking the added sale
        HtmlTableRow newSaleRow = table.getRow(openedSaleRowIndex);
        assertTrue(newSaleRow.asText().contains(VAT_5));
        assertTrue(newSaleRow.asText().contains("O"));
		
		// Inserting a new sale delivery
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"AddSaleDeliveryPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		
		List<HtmlTable> tables = reportPage.getByXPath("//table");
		assertTrue(tables.size() == 2);
		HtmlTable addressesTable = tables.get(0);
		HtmlTable salesTable = tables.get(1);
		assertTrue(addressesTable.asText().contains(ADDRESS_5_3));
		assertTrue(salesTable.asText().contains(openedSaleId));
		
		String addressId = null;
		for (HtmlTableRow row : addressesTable.getRows()) {
			if (row.asText().contains(ADDRESS_5_3)) addressId = row.getCell(0).asText();
		}
		assertTrue(addressId != null);
        
		webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddSaleDeliveryPageController"), HttpMethod.POST);
		formData = String.format("addr_id=%s&sale_id=%s", addressId, openedSaleId);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        
        table = reportPage.getFirstByXPath("//table");
        assertTrue(table != null);
        HtmlTableRow newDeliveryRow = table.getRow(table.getRowCount()-1);
        assertEquals(openedSaleId, newDeliveryRow.getCell(1).asText());
        assertEquals(addressId, newDeliveryRow.getCell(2).asText());
        
        // Checking the added delivery
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetSaleDeliveryPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", VAT_5));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));
			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());
		}
		table = (HtmlTable) reportPage.getFirstByXPath("//table");
		assertTrue(table != null);
		newDeliveryRow = table.getRow(table.getRowCount()-1);
		assertEquals(openedSaleId, newDeliveryRow.getCell(1).asText());
        assertEquals(addressId, newDeliveryRow.getCell(2).asText());
	}

}

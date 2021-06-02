package vvs_assignment_htmlunit;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static vvs_assignment_htmlunit.HtmlUnitVariables.APPLICATION_URL;
import static vvs_assignment_htmlunit.HtmlUnitVariables.DESIGNATION_5;
import static vvs_assignment_htmlunit.HtmlUnitVariables.PHONE_5;
import static vvs_assignment_htmlunit.HtmlUnitVariables.VAT_5;
import static vvs_assignment_htmlunit.HtmlUnitVariables.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
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

public class HtmlClosingOpenSale {

	String openedSaleId = null;
	int openedSaleRowIndex = 0;
	
	@Before
	public void settingUpTheDatabase() throws IOException {
		// Adding a new customer
		WebRequest webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddCustomerPageController"), HttpMethod.POST);
		String formData = String.format("vat=%s&designation=%s&phone=%s", VAT_5, DESIGNATION_5, PHONE_5);
        webRequest.setRequestBody(formData);
        HtmlPage reportPage;
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        String textReportPage = reportPage.asText();
        assumeTrue(textReportPage.contains(DESIGNATION_5));
        assumeTrue(textReportPage.contains(PHONE_5));
        
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
		}
		
		// Adding a sale
		webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddSalePageController"), HttpMethod.POST);
		formData = String.format("customerVat=%s", VAT_5);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        table = reportPage.getFirstByXPath("//table");
        assumeTrue(table != null);
        assumeTrue(numberOfRowsBefore + 1 == table.getRowCount());
        
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
            assumeTrue(openedSaleId != null);
        }
        
        // Checking the added sale
        HtmlTableRow newSaleRow = table.getRow(openedSaleRowIndex);
        assumeTrue(newSaleRow.asText().contains(VAT_5));
        assumeTrue(newSaleRow.asText().contains("O"));
        assumeTrue(newSaleRow.getCell(0).asText().contains(openedSaleId));
	}

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
	public void closingOpenSaleTest() throws IOException {
		// Checking the initial sales
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
		assumeTrue(table != null);
		HtmlTableRow newSaleRow = table.getRow(openedSaleRowIndex);
        assumeTrue(newSaleRow.asText().contains(VAT_5));
        assumeTrue(newSaleRow.asText().contains("O"));
        assumeTrue(newSaleRow.getCell(0).asText().contains(openedSaleId));
		
		// Closing the sale
		HtmlAnchor closeSaleLink = page.getAnchorByHref("UpdateSaleStatusPageControler");
		HtmlPage operatedPage = (HtmlPage) closeSaleLink.openLinkInNewWindow();
		HtmlForm closeSaleForm = operatedPage.getForms().get(0);
		HtmlInput vatInput = closeSaleForm.getInputByName("id");
		HtmlInput submitButton = closeSaleForm.getInputByValue("Close Sale");
		vatInput.setValueAttribute(openedSaleId);
		submitButton.click();
        
		// Checking the final sales
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
		assertTrue(table != null);
		newSaleRow = table.getRow(openedSaleRowIndex);
		assertTrue(newSaleRow.asText().contains(VAT_5));
		assertTrue(newSaleRow.asText().contains("C"));
		assertTrue(newSaleRow.getCell(0).asText().contains(openedSaleId));
	}

}

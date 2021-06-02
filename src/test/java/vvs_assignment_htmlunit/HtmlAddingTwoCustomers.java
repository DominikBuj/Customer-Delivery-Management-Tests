package vvs_assignment_htmlunit;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static vvs_assignment_htmlunit.HtmlUnitVariables.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

// For the test to run properly run the TestSuiteHtmlUnit

public class HtmlAddingTwoCustomers {

	@After
	public void removeAddedCustomers() throws IOException {
		HtmlPage reportPage;
		String formData;
		WebRequest webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"RemoveCustomerPageController"), HttpMethod.POST);
		String textReportPage;
		
		// Removing the first customer
		formData = String.format("vat=%s", VAT_3);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        textReportPage = reportPage.asText();
        assumeFalse(textReportPage.contains(DESIGNATION_3));
        assumeFalse(textReportPage.contains(PHONE_3));
        
        // Removing the second customer
        formData = String.format("vat=%s", VAT_4);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        textReportPage = reportPage.asText();
        assumeFalse(textReportPage.contains(DESIGNATION_4));
        assumeFalse(textReportPage.contains(PHONE_4));
		
        // Checking the customers
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		HtmlPage operatedPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		assumeFalse(operatedPage.asText().contains(VAT_3));
		assumeFalse(operatedPage.asText().contains(VAT_4));
	}

	@Test
	public void addingTwoCustomersTest() throws IOException {
		HtmlPage reportPage;
		String formData;
		WebRequest webRequest = new WebRequest(new java.net.URL(APPLICATION_URL+"AddCustomerPageController"), HttpMethod.POST);
		String textReportPage;
		
		// Adding the first customer
		formData = String.format("vat=%s&designation=%s&phone=%s", VAT_3, DESIGNATION_3, PHONE_3);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        textReportPage = reportPage.asText();
        assertTrue(textReportPage.contains(DESIGNATION_3));
        assertTrue(textReportPage.contains(PHONE_3));
        
        // Adding the second customer
        formData = String.format("vat=%s&designation=%s&phone=%s", VAT_4, DESIGNATION_4, PHONE_4);
        webRequest.setRequestBody(formData);
        try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) {
            reportPage = (HtmlPage) webClient.getPage(webRequest);
        }
        textReportPage = reportPage.asText();
        assertTrue(textReportPage.contains(DESIGNATION_4));
        assertTrue(textReportPage.contains(PHONE_4));
        
        // Checking the customers
        HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
        HtmlPage listAllCustomersPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
        final HtmlTable table = listAllCustomersPage.getHtmlElementById("clients");
        boolean tableContainsFirstCustomer = false;
        boolean tableContainsSecondCustomer = false;
        for (final HtmlTableRow row : table.getRows()) {
        	if (tableContainsFirstCustomer && tableContainsSecondCustomer) break;
        	String customerInformation = row.asText();
        	if (!tableContainsFirstCustomer && customerInformation.contains(VAT_3)) {
        		tableContainsFirstCustomer = true;
        		assertTrue(customerInformation.contains(DESIGNATION_3));
        		assertTrue(customerInformation.contains(PHONE_3));
        	}
        	else if (!tableContainsSecondCustomer && customerInformation.contains(VAT_4)) {
        		tableContainsSecondCustomer = true;
        		assertTrue(customerInformation.contains(DESIGNATION_4));
        		assertTrue(customerInformation.contains(PHONE_4));
        	}
        }
        assertTrue(tableContainsFirstCustomer && tableContainsSecondCustomer);
	}

}

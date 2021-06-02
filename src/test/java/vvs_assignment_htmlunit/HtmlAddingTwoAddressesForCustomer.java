package vvs_assignment_htmlunit;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assume.assumeFalse;
import static vvs_assignment_htmlunit.HtmlUnitVariables.*;

import java.io.IOException;
import java.util.ArrayList;

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
import com.gargoylesoftware.htmlunit.util.NameValuePair;

// For the test to run properly run the TestSuiteHtmlUnit

public class HtmlAddingTwoAddressesForCustomer {

	@Before
	public void addOneCustomer() throws IOException {
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
	public void addingTwoAddressesForCustomerTest() throws IOException {
		// Checking the addresses before
		HtmlPage reportPage;
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
		int numberOfRowsBefore;
		if (table == null) numberOfRowsBefore = 1;
		else numberOfRowsBefore = table.getRowCount();
		
		// Adding the addresses
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
		addressInput.setValueAttribute(ADDRESS_5_1);
		doorInput.setValueAttribute(DOOR_5_1);
		postalCodeInput.setValueAttribute(POSTAL_CODE_5_1);
		localityInput.setValueAttribute(LOCALITY_5_1);
		submitButton.click();
		
		vatInput.setValueAttribute(VAT_5);
		addressInput.setValueAttribute(ADDRESS_5_2);
		doorInput.setValueAttribute(DOOR_5_2);
		postalCodeInput.setValueAttribute(POSTAL_CODE_5_2);
		localityInput.setValueAttribute(LOCALITY_5_2);
		operatedPage = submitButton.click();
		
		// Checking the addresses after
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
		int numberOfRowsAfter = table.getRowCount();
		assertEquals(numberOfRowsBefore + 2, numberOfRowsAfter);
		
		String tableAsText = table.asText();
		assertTrue(tableAsText.contains(ADDRESS_5_1));
		assertTrue(tableAsText.contains(DOOR_5_1));
		assertTrue(tableAsText.contains(POSTAL_CODE_5_1));
		assertTrue(tableAsText.contains(LOCALITY_5_1));
		assertTrue(tableAsText.contains(ADDRESS_5_2));
		assertTrue(tableAsText.contains(DOOR_5_2));
		assertTrue(tableAsText.contains(POSTAL_CODE_5_2));
		assertTrue(tableAsText.contains(LOCALITY_5_2));
	}

}

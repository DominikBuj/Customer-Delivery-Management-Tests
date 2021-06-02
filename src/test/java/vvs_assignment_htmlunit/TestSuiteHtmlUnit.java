package vvs_assignment_htmlunit;

import static org.junit.Assert.assertEquals;
import static vvs_assignment_htmlunit.HtmlUnitVariables.page;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

@RunWith(Suite.class)
@SuiteClasses({ HtmlAddingTwoCustomers.class, HtmlAddingTwoAddressesForCustomer.class, OpeningSaleForExistingCustomer.class,
	HtmlClosingOpenSale.class, HtmlAddingCustomerWithSaleAndDelivery.class })
public class TestSuiteHtmlUnit {

	@BeforeClass
	public static void setUpSite() throws Exception {
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			// possible configurations needed to prevent JUnit tests to fail for complex HTML pages
            webClient.setJavaScriptTimeout(15000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
		    
            page = webClient.getPage(HtmlUnitVariables.APPLICATION_URL);
			assertEquals(200, page.getWebResponse().getStatusCode()); // OK status
		}
	}
	
}

package vvs_assignment_mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import webapp.persistence.CustomerFinder;
import webapp.persistence.CustomerRowDataGateway;
import webapp.persistence.PersistenceException;
import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;

public class TestCustomerService {

	private static final int ID_1 = 1;
	private static final int VAT_1 = 514101709;
	private static final String DESIGNATION_1 = "REINALDO REIS";
	private static final int PHONE_1 = 289291494;
	
	private static final int VAT_2 = 504194739;
	
	private static final int INCORRECT_VAT = 21;
	
	@Mock CustomerFinder customerFinder;
	@Spy CustomerRowDataGateway customerOne = new CustomerRowDataGateway(ID_1, VAT_1, DESIGNATION_1, PHONE_1);
	
	CustomerService customerService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(customerFinder.getCustomerByVATNumber(VAT_1)).
			thenReturn(new CustomerRowDataGateway(ID_1, VAT_1, DESIGNATION_1, PHONE_1));
		when(customerFinder.getCustomerByVATNumber(VAT_2)).
			thenThrow(new PersistenceException("Internal error getting a customer by its VAT number", 
					new SQLException("Customer with vat number " + VAT_2 + " not found.")));
		
		customerService = CustomerService.INSTANCE;
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void getExistingCustomerTest() throws ApplicationException, PersistenceException {
		CustomerDTO customer = customerService.getCustomerByVat(VAT_1, customerFinder);
		
		assertEquals(ID_1, customer.id);
		assertEquals(VAT_1, customer.vat);
		assertEquals(DESIGNATION_1, customer.designation);
		assertEquals(PHONE_1, customer.phoneNumber);
		
		verify(customerFinder).getCustomerByVATNumber(VAT_1);
	}
	
	@Test
	public void getNonExistentCustomerTest() throws ApplicationException {
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Customer with vat number " + VAT_2 + " not found.");
		customerService.getCustomerByVat(VAT_2, customerFinder);
	}

	@Test
	public void getNonExistentCustomerIncorrectVat() throws ApplicationException {
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Invalid VAT number: " + INCORRECT_VAT);
		customerService.getCustomerByVat(INCORRECT_VAT, customerFinder);
		//verify(customerFinder).isValidVAT(INCORRECT_VAT);
	}
	
	/*@Test
	public void removingExistingCustomerTest() throws ApplicationException, PersistenceException {
		customerService.removeCustomer(VAT_1, customerFinder);
		
		verify(customerFinder).getCustomerByVATNumber(VAT_1);
	}*/
	
	@Test
	public void removingNonExistentCustomerTest() throws ApplicationException {
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Customer with vat number " + VAT_2 + " doesn't exist.");
		customerService.removeCustomer(VAT_2, customerFinder);
	}

}

package vvs_assignment_dbsetup;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static vvs_dbsetup.DBSetupUtils.DELETE_ALL;
import static vvs_dbsetup.DBSetupUtils.INSERT_CUSTOMER_ADDRESS_DATA;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_CUSTOMERS;
import static vvs_dbsetup.DBSetupUtils.customerExists;
import static vvs_dbsetup.DBSetupUtils.dataSource;
import static vvs_dbsetup.DBSetupUtils.dbSetupTracker;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.persistence.CustomerFinder;
import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;
import webapp.services.CustomersDTO;

// For the test to run properly run the TestSuiteDbSetup

public class DbCustomersOperations {

	@Before
	public void rebuildData() throws SQLException {
		Operation initDBOperations = Operations.sequenceOf(
				DELETE_ALL,
				INSERT_CUSTOMER_ADDRESS_DATA
		);
		
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		
        // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
	}

	@Test
	public void removeAllCustomersTest() throws ApplicationException {
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		
		CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		for(CustomerDTO customer : customersDTO.customers) {
			CustomerService.INSTANCE.removeCustomer(customer.vat, new CustomerFinder());
		}
		
		int finalNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals(0, finalNumberOfCustomers);
	}

	
	@Test
	public void updateCustomersPhoneTest() throws ApplicationException {
		assumeTrue(customerExists(197672337));
		int initialPhoneNumber = CustomerService.INSTANCE.getCustomerByVat(197672337, new CustomerFinder()).phoneNumber;
		assumeTrue(914276732 == initialPhoneNumber);
		
		CustomerService.INSTANCE.updateCustomerPhone(197672337, 289815986, new CustomerFinder());
		
		int finalPhoneNumber = CustomerService.INSTANCE.getCustomerByVat(197672337, new CustomerFinder()).phoneNumber;
		assertEquals(289815986, finalPhoneNumber);
	}
	
}

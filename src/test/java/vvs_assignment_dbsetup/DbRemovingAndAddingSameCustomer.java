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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.persistence.CustomerFinder;
import webapp.persistence.CustomerRowDataGateway;
import webapp.services.ApplicationException;
import webapp.services.CustomerService;

// For the test to run properly run the TestSuiteDbSetup

public class DbRemovingAndAddingSameCustomer {

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

	@After
	public void makeSureCustomerWasAdded() throws ApplicationException {
		assertTrue(customerExists(168027852));
		int finalNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals(NUM_INIT_CUSTOMERS, finalNumberOfCustomers);
	}

	@Test
	public void removeAndAddTheSameCustomerTest() throws ApplicationException {
		assumeTrue(customerExists(168027852));
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		
		CustomerService.INSTANCE.removeCustomer(168027852, new CustomerFinder());
		int middleNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals(initialNumberOfCustomers-1, middleNumberOfCustomers);
		
		try {
			CustomerService.INSTANCE.addCustomer(new CustomerRowDataGateway(168027852, "LUIS SANTOS", 964294317));
		} catch (Exception e) {
			fail("Expected to not throw an exception");
		}
	}

}

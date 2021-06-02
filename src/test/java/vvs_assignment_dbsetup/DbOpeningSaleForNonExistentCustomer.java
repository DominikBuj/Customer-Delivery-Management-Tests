package vvs_assignment_dbsetup;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assume.assumeFalse;
import static vvs_dbsetup.DBSetupUtils.DELETE_ALL;
import static vvs_dbsetup.DBSetupUtils.INSERT_CUSTOMER_SALE_DATA;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_CUSTOMERS;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_SALES;
import static vvs_dbsetup.DBSetupUtils.customerExists;
import static vvs_dbsetup.DBSetupUtils.dataSource;
import static vvs_dbsetup.DBSetupUtils.dbSetupTracker;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.services.ApplicationException;
import webapp.services.CustomerService;
import webapp.services.SaleService;

// For the test to run properly run the TestSuiteDbSetup

public class DbOpeningSaleForNonExistentCustomer {

	@Before
	public void rebuildData() throws SQLException {
		Operation initDBOperations = Operations.sequenceOf(
				DELETE_ALL,
				INSERT_CUSTOMER_SALE_DATA
		);
		
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		
        // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
	}

	@After
	public void makeSureSalesDidntChange() throws ApplicationException {
		int finalNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assertEquals("Number of sales changed when adding an incorrect one.",
				NUM_INIT_SALES, finalNumberOfSales);
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void openingSaleForNonExistentCustomerTest() throws ApplicationException {
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		assumeFalse(customerExists(514101709));
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Can't add customer with vat number 514101709.");
		SaleService.INSTANCE.addSale(514101709);
	}

}

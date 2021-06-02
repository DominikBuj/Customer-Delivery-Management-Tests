package vvs_assignment_dbsetup;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static vvs_dbsetup.DBSetupUtils.DELETE_ALL;
import static vvs_dbsetup.DBSetupUtils.INSERT_CUSTOMER_SALE_DATA;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_CUSTOMERS;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_SALES;
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
import webapp.services.CustomerService;
import webapp.services.SaleDTO;
import webapp.services.SaleService;

// For the test to run properly run the TestSuiteDbSetup

public class DbSalesOperations {

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

	@Test
	public void removingCustomerCheckingSalesTest() throws ApplicationException {
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		assumeTrue(customerExists(197672337));
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		initialNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		
		CustomerService.INSTANCE.removeCustomer(197672337, new CustomerFinder());
		
		int finalNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals("Number of customer din't change after removing one.",
				NUM_INIT_CUSTOMERS-1, finalNumberOfCustomers);
		int finalNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assertEquals("Number of sales of a removed customer is not zero.",
				0, finalNumberOfSales);
		finalNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assertEquals("Number of sales of a removed customer is not zero.",
				0, finalNumberOfSales);
	}
	
	@Test
	public void addingSaleCheckingSalesNubmerTest() throws ApplicationException {
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		assumeTrue(customerExists(197672337));
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		initialNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		
		SaleService.INSTANCE.addSale(197672337);
		
		int finalNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assertEquals("Number of sales didn't inrease by one.",
				NUM_INIT_SALES+1, finalNumberOfSales);
		finalNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assertEquals("Number of sales of the customer didn't increase by one.",
				NUM_INIT_SALES+1, finalNumberOfSales);
	}
	
	@Test
	public void closingSalesCheckingSales() throws ApplicationException {
		int initialNumberOfCustomers = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assumeTrue(NUM_INIT_CUSTOMERS == initialNumberOfCustomers);
		assumeTrue(customerExists(197672337));
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		initialNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		
		SaleService.INSTANCE.updateSale(1);
		SaleService.INSTANCE.updateSale(2);
		
		int finalNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assertEquals("Number of sales changed when closing them.",
				NUM_INIT_SALES, finalNumberOfSales);
		finalNumberOfSales = SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales.size();
		assertEquals("Number of sales changed when closing them.",
				NUM_INIT_SALES, finalNumberOfSales);
		for (SaleDTO sale : SaleService.INSTANCE.getSaleByCustomerVat(197672337).sales) {
			assertEquals("Sale didn't close.",
					sale.statusId, "C");
		}
	}

}

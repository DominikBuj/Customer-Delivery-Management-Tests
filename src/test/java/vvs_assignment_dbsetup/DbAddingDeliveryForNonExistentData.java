package vvs_assignment_dbsetup;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static vvs_dbsetup.DBSetupUtils.DELETE_ALL;
import static vvs_dbsetup.DBSetupUtils.INSERT_CUSTOMER_DELIVERIES_DATA;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_SALES;
import static vvs_dbsetup.DBSetupUtils.NUM_INIT_DELIVERIES;
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
import webapp.services.SaleService;

// For the test to run properly run the TestSuiteDbSetup

public class DbAddingDeliveryForNonExistentData {

	@Before
	public void rebuildData() throws SQLException {
		Operation initDBOperations = Operations.sequenceOf(
				DELETE_ALL,
				INSERT_CUSTOMER_DELIVERIES_DATA
		);
		
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		
        // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
	}

	@After
	public void makeSureDeliveriesDidntChange() throws ApplicationException {
		int finalNumberOfDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(197672337).sales_delivery.size();
		assertEquals("Number of deliveries changed.",
				NUM_INIT_DELIVERIES, finalNumberOfDeliveries);
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void addingDeliveryForNonExistentSaleAndAddressTest() throws ApplicationException {
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		assumeTrue(customerExists(197672337));
		int initialNumberOfDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(197672337).sales_delivery.size();
		assumeTrue(NUM_INIT_DELIVERIES == initialNumberOfDeliveries);
		
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Can't add address to cutomer.");
		SaleService.INSTANCE.addSaleDelivery(66, 6);
	}

	@Test
	public void addingDeliveryForNonExistentSaleTest() throws ApplicationException {
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		assumeTrue(customerExists(197672337));
		int initialNumberOfDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(197672337).sales_delivery.size();
		assumeTrue(NUM_INIT_DELIVERIES == initialNumberOfDeliveries);
		
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Can't add address to cutomer.");
		SaleService.INSTANCE.addSaleDelivery(66, 100);
	}
	
	@Test
	public void addingDeliveryForNonExistentAddressTest() throws ApplicationException {
		int initialNumberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		assumeTrue(NUM_INIT_SALES == initialNumberOfSales);
		assumeTrue(customerExists(197672337));
		int initialNumberOfDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(197672337).sales_delivery.size();
		assumeTrue(NUM_INIT_DELIVERIES == initialNumberOfDeliveries);
		
		exceptionRule.expect(ApplicationException.class);
		exceptionRule.expectMessage("Can't add address to cutomer.");
		SaleService.INSTANCE.addSaleDelivery(1, 66);
	}
}

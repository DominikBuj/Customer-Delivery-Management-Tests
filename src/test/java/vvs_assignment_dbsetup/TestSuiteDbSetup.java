package vvs_assignment_dbsetup;

import static vvs_dbsetup.DBSetupUtils.DB_PASSWORD;
import static vvs_dbsetup.DBSetupUtils.DB_URL;
import static vvs_dbsetup.DBSetupUtils.DB_USERNAME;
import static vvs_dbsetup.DBSetupUtils.DELETE_ALL;
import static vvs_dbsetup.DBSetupUtils.INSERT_CUSTOMER_ADDRESS_DATA;
import static vvs_dbsetup.DBSetupUtils.dataSource;
import static vvs_dbsetup.DBSetupUtils.dbSetupTracker;
import static vvs_dbsetup.DBSetupUtils.startApplicationDatabaseForTesting;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@RunWith(Suite.class)
@SuiteClasses({ DbAddingExistingCustomer.class, DbRemovingAndAddingSameCustomer.class, DbCustomersOperations.class, DbSalesOperations.class, DbOpeningSaleForNonExistentCustomer.class,
	DbAddingDeliveryForNonExistentData.class })
public class TestSuiteDbSetup {
	
	@BeforeClass
	public static void setUpDatabase() throws Exception {
		startApplicationDatabaseForTesting();
		dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
	}
	
	@AfterClass
	public static void rebuildData() {
		Operation initDBOperations = Operations.sequenceOf(
				DELETE_ALL,
				INSERT_CUSTOMER_ADDRESS_DATA
		);
			
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
			
	    // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
	    dbSetupTracker.launchIfNecessary(dbSetup);
	}
	
}

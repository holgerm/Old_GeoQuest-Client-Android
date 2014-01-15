package com.qeevee.gq.tests.inventory;

import static com.qeevee.gq.tests.util.TestUtils.startApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.inventory.Inventory;
import com.qeevee.gq.tests.robolectric.GQTestRunner;

@RunWith(GQTestRunner.class)
public class InventoryTests {

	@Before
	public void setup() {
		startApp();
	}

	@Test
	public void name() {
		// GIVEN:

		// WHEN:
		Inventory inventory = Inventory.getStandardInventory();

		// THEN:
		shouldBeEmpty(inventory);
	}

	@Test
	public void checkCleanUp() {
		// GIVEN:
		Inventory inventory = Inventory.getStandardInventory();
		inventory.addItems("TestItem1", 3);
		inventory.addItems("TestItem2", 5);

		// WHEN:
		inventory.deleteAll();

		shouldBeEmpty(inventory);
	}

	// //// HELPERS

	private void shouldBeEmpty(Inventory inventory) {
		assertNotNull(inventory.getItems());
		assertEquals(0, inventory.getItems().size());
	}

}

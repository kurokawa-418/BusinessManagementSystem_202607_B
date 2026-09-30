package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.nexus.whc.form.EmployeeForm;
import com.nexus.whc.repository.EmployeeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		TransactionDbUnitTestExecutionListener.class
})
@ContextConfiguration(classes = EmployeeRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional
public class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository target;

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void searchEmployeeList() {

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(1, result.size());
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case02/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case02/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void searchEmployeeList2() {

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(0, result.size());
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case03/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case03/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void getClient() {

		List<Map<String, Object>> result = target.getClient();

		assertEquals(2, result.size());
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void getClient2() {

		List<Map<String, Object>> result = target.getClient();

		assertEquals(1, result.size());
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case04/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case04/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteEmployee() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("1001");
		employeeForm.setUpdatedUser("test_user");

		int result = target.deleteEmployee(employeeForm);

		assertEquals(1, result);
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case04/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case04/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteEmployee2() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("9999");
		employeeForm.setUpdatedUser("test_user");

		int result = target.deleteEmployee(employeeForm);

		assertEquals(0, result);
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case05/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case05/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deletePaidVacation() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("1001");
		employeeForm.setUpdatedUser("test_user");

		int result = target.deletePaidVacation(employeeForm);

		assertEquals(1, result);
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case06/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case06/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void existsEmployee() {

		boolean result = target.existsEmployee("1001");

		assertTrue(result);
	}

	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case06/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case06/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void existsEmployee2() {

		boolean result = target.existsEmployee("9999");

		assertFalse(result);
	}
}
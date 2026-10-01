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

	/**
	 * searchEmployeeList(社員一覧を取得できる場合)
	 * 正常系テストケース
	 * EmployeeRepository.searchEmployeeListは社員一覧を返す
	 * 期待値
	 * EmployeeRepository.searchEmployeeListが社員一覧を返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void searchEmployeeList() {

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(1, result.size());
	}

	/**
	 * searchEmployeeList(社員一覧が存在しない場合)
	 * 正常系テストケース
	 * EmployeeRepository.searchEmployeeListは空の社員一覧を返す
	 * 期待値
	 * EmployeeRepository.searchEmployeeListが0件の社員一覧を返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case02/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case02/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void searchEmployeeList2() {

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(0, result.size());
	}

	/**
	 * getClient(顧客情報が2件存在する場合)
	 * 正常系テストケース
	 * EmployeeRepository.getClientは顧客一覧を返す
	 * 期待値
	 * EmployeeRepository.getClientが2件の顧客一覧を返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case03/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case03/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void getClient() {

		List<Map<String, Object>> result = target.getClient();

		assertEquals(2, result.size());
	}

	/**
	 * getClient(顧客情報が1件存在する場合)
	 * 正常系テストケース
	 * EmployeeRepository.getClientは顧客一覧を返す
	 * 期待値
	 * EmployeeRepository.getClientが1件の顧客一覧を返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void getClient2() {

		List<Map<String, Object>> result = target.getClient();

		assertEquals(1, result.size());
	}

	/**
	 * deleteEmployee(社員が存在する場合)
	 * 正常系テストケース
	 * EmployeeRepository.deleteEmployeeは指定した社員を論理削除する
	 * 期待値
	 * EmployeeRepository.deleteEmployeeが1件更新すること
	 */
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

	/**
	 * deleteEmployee(社員が存在しない場合)
	 * 異常系テストケース
	 * EmployeeRepository.deleteEmployeeは指定した社員を更新できない
	 * 期待値
	 * EmployeeRepository.deleteEmployeeの更新件数が0件であること
	 */
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

	/**
	 * deletePaidVacation(社員が存在する場合)
	 * 正常系テストケース
	 * EmployeeRepository.deletePaidVacationは指定した社員の有給休暇情報を論理削除する
	 * 期待値
	 * EmployeeRepository.deletePaidVacationが1件更新すること
	 */
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

	/**
	 * existsEmployee(社員が存在する場合)
	 * 正常系テストケース
	 * EmployeeRepository.existsEmployeeは指定した社員の存在を確認する
	 * 期待値
	 * EmployeeRepository.existsEmployeeがtrueを返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case06/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case06/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void existsEmployee() {

		boolean result = target.existsEmployee("1001");

		assertTrue(result);
	}

	/**
	 * existsEmployee(社員が存在しない場合)
	 * 異常系テストケース
	 * EmployeeRepository.existsEmployeeは指定した社員の存在を確認する
	 * 期待値
	 * EmployeeRepository.existsEmployeeがfalseを返すこと
	 */
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case06/init-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case06/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void existsEmployee2() {

		boolean result = target.existsEmployee("9999");

		assertFalse(result);
	}
}
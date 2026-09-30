package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.nexus.whc.form.EmployeeForm;
import com.nexus.whc.repository.EmployeeRepository;
import com.nexus.whc.services.EmployeeService;
import com.nexus.whc.services.LockService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = EmployeeService.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
public class EmployeeServiceTest {

	@MockBean
	private EmployeeRepository mockEmployeeRepository;

	@MockBean
	private LockService mockLockService;

	@Autowired
	private EmployeeService target;

	/**
	 * searchEmployeeList(社員一覧を取得できる場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.searchEmployeeListは社員一覧を返す
	 * 期待値
	 * EmployeeService.searchEmployeeListがその社員一覧を返すこと
	 */
	@Test
	public void searchEmployeeList() {
		List<Map<String, Object>> mockList = new ArrayList<>();
		Map<String, Object> mockMap = new HashMap<>();

		/*Mapにデータを入れていく*/
		mockMap.put("employee_id", "001");
		mockMap.put("employee_name", "田中太郎");
		mockMap.put("client_id", "001");
		mockMap.put("hourly_wage", 0);
		mockMap.put("paid_holiday_std", null);
		mockMap.put("delete_flg", false);
		mockMap.put("created_at", null);
		mockMap.put("created_user", null);
		mockMap.put("updated_at", null);
		mockMap.put("updated_user", "test_user");

		/*ListにMapを格納*/
		mockList.add(mockMap);
		/*実際にServiceを呼ぶ*/
		Mockito.when(mockEmployeeRepository.searchEmployeeList())
				.thenReturn(mockList);

		List<Map<String, Object>> result = target.searchEmployeeList();
		/*結果を期待値と比較*/
		assertEquals(mockList, result);

	}

	/**
	 * searchEmployeeList（社員一覧が0件の場合）
	 * 正常系テストケース
	 * mockEmployeeRepository.searchEmployeeListは0件のListを返す
	 * 期待値
	 * ・0件のListが返却されること
	 */
	@Test
	public void searchEmployeeList2() {
		List<Map<String, Object>> mockList = new ArrayList<>();

		Mockito.when(mockEmployeeRepository.searchEmployeeList())
				.thenReturn(mockList);

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(mockList, result);
	}

	/**
	 * isEmployeeLockedByOtherUser(他のユーザが社員情報をロックしている場合(排他チェック編集中))
	 * 正常系テストケース
	 * mockLockService.isLockedByOtherUserはtrueを返す
	 * 期待値
	 * EmployeeService.isEmployeeLockedByOtherUserがtrueを返すこと
	 */

	@Test
	public void isEmployeeLockedByOtherUser() {
		String employeeId = "001";
		String userId = "testUser";
		Mockito.when(mockLockService.isLockedByOtherUser(
				"m_employee",
				Integer.valueOf(employeeId),
				userId))
				.thenReturn(true);
		boolean result = target.isEmployeeLockedByOtherUser(employeeId, userId);
		assertEquals(true, result);
	}

	/**
	 * isEmployeeLockedByOtherUser(他のユーザが社員情報をロックしていない場合(排他チェック編集中))
	 * 正常系テストケース
	 * mockLockService.isLockedByOtherUserはfalseを返す
	 * 期待値
	 * EmployeeService.isEmployeeLockedByOtherUserがfalseを返すこと
	 */
	@Test
	public void isEmployeeLockedByOtherUser2() {
		String employeeId = "001";
		String userId = "testUser";

		Mockito.when(mockLockService.isLockedByOtherUser(
				"m_employee",
				Integer.valueOf(employeeId),
				userId))
				.thenReturn(false);

		boolean result = target.isEmployeeLockedByOtherUser(employeeId, userId);

		assertEquals(false, result);
	}

	/**deleteEmployee（社員情報を削除できる場合）
	 * 正常系テストケース
	 * mockEmployeeRepository.deleteEmployeeは1を返す
	 * 期待値
	 * EmployeeService.deleteEmployeeが1を返すこと
	 */

	@Test
	public void deleteEmployee() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeForm))
				.thenReturn(1);

		int result = target.deleteEmployee(employeeForm);

		assertEquals(1, result);
	}

	/**
	 * deleteEmployee(社員情報を削除できない場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.deleteEmployeeは0を返す
	 * 期待値
	 * EmployeeService.deleteEmployeeが0を返すこと
	 */
	@Test
	public void deleteEmployee2() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeForm))
				.thenReturn(0);

		int result = target.deleteEmployee(employeeForm);

		assertEquals(0, result);
	}

	/**
	 * deleteEmployee(Repositoryから2が返却された場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.deleteEmployeeは2を返す
	 * 期待値
	 * EmployeeService.deleteEmployeeが2を返すこと
	 */
	@Test
	public void deleteEmployee3() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeForm))
				.thenReturn(2);

		int result = target.deleteEmployee(employeeForm);

		assertEquals(2, result);
	}

	/**
	 * deletePaidVacation(有給情報を削除できる場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.deletePaidVacationは1を返す
	 * 期待値
	 * EmployeeService.deletePaidVacationが1を返すこと
	 */
	@Test
	public void deletePaidVacation() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deletePaidVacation(employeeForm))
				.thenReturn(1);

		int result = target.deletePaidVacation(employeeForm);

		assertEquals(1, result);
	}

	/**
	 * deletePaidVacation(削除対象の有給情報が存在しない場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.deletePaidVacationは0を返す
	 * 期待値
	 * EmployeeService.deletePaidVacationが0を返すこと
	 */
	@Test
	public void deletePaidVacation2() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deletePaidVacation(employeeForm))
				.thenReturn(0);

		int result = target.deletePaidVacation(employeeForm);

		assertEquals(0, result);
	}

	/**
	 * deletePaidVacation(Repositoryから2が返却された場合)
	 * 正常系テストケース
	 * mockEmployeeRepository.deletePaidVacationは2を返す
	 * 期待値
	 * EmployeeService.deletePaidVacationが2を返すこと
	 */
	@Test
	public void deletePaidVacation3() {
		EmployeeForm employeeForm = new EmployeeForm();

		Mockito.when(mockEmployeeRepository.deletePaidVacation(employeeForm))
				.thenReturn(2);

		int result = target.deletePaidVacation(employeeForm);

		assertEquals(2, result);
	}

}

package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.controller.EmployeeController;
import com.nexus.whc.form.EmployeeForm;
import com.nexus.whc.services.EmployeeService;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {

	@InjectMocks
	private EmployeeController employeeController;

	@Mock
	private EmployeeService employeeService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	@Mock
	private HttpSession session;

	/**
	 * clientList(社員一覧を取得できる場合)
	 * 正常系テストケース
	 * mockEmployeeService.getClientは顧客一覧を返す
	 * mockEmployeeService.searchEmployeeListは社員一覧を返す
	 * 期待値
	 * EmployeeController.clientListが"SMSEM001"を返すこと
	 * Modelに顧客一覧と社員一覧が設定されること
	 */
	@Test
	public void clientList() {

		List<Map<String, Object>> clientList = new ArrayList<Map<String, Object>>();

		Map<String, Object> client = new HashMap<String, Object>();

		client.put("client_id", 101);
		client.put("client_name", "株式会社アクサス");

		clientList.add(client);

		List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();

		Map<String, Object> employee = new HashMap<String, Object>();

		employee.put("employee_id", 1001);
		employee.put("employee_name", "テスト社員001");

		employeeList.add(employee);

		Mockito.when(employeeService.getClient())
				.thenReturn(clientList);

		Mockito.when(employeeService.searchEmployeeList())
				.thenReturn(employeeList);

		String result = employeeController.clientList(model);

		assertEquals("SMSEM001", result);

		verify(employeeService, times(1))
				.getClient();

		verify(employeeService, times(1))
				.searchEmployeeList();

		verify(model, times(1))
				.addAttribute("client_list", clientList);

		verify(model, times(1))
				.addAttribute("employeeList", employeeList);
	}

	/**
	 * clientList(複数件の社員一覧を取得できる場合)
	 * 正常系テストケース
	 * mockEmployeeService.getClientは顧客一覧を返す
	 * mockEmployeeService.searchEmployeeListは複数件の社員一覧を返す
	 * 期待値
	 * EmployeeController.clientListが"SMSEM001"を返すこと
	 * Modelに顧客一覧と複数件の社員一覧が設定されること
	 */
	@Test
	public void clientList2() {

		List<Map<String, Object>> clientList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();

		Map<String, Object> employee1 = new HashMap<String, Object>();
		employee1.put("employee_id", 1001);
		employee1.put("employee_name", "テスト社員001");

		Map<String, Object> employee2 = new HashMap<String, Object>();
		employee2.put("employee_id", 1002);
		employee2.put("employee_name", "テスト社員002");

		employeeList.add(employee1);
		employeeList.add(employee2);

		when(employeeService.getClient())
				.thenReturn(clientList);

		when(employeeService.searchEmployeeList())
				.thenReturn(employeeList);

		String result = employeeController.clientList(model);

		assertEquals("SMSEM001", result);

		verify(employeeService, times(1))
				.getClient();

		verify(employeeService, times(1))
				.searchEmployeeList();

		verify(model, times(1))
				.addAttribute("client_list", clientList);

		verify(model, times(1))
				.addAttribute("employeeList", employeeList);
	}

	/**
	 * clientList(社員一覧が0件の場合)
	 * 正常系テストケース
	 * mockEmployeeService.getClientは顧客一覧を返す
	 * mockEmployeeService.searchEmployeeListは空の社員一覧を返す
	 * 期待値
	 * EmployeeController.clientListが"SMSEM001"を返すこと
	 * Modelに顧客一覧と空の社員一覧が設定されること
	 */

	@Test
	public void clientList3() {

		List<Map<String, Object>> clientList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();

		when(employeeService.getClient())
				.thenReturn(clientList);

		when(employeeService.searchEmployeeList())
				.thenReturn(employeeList);

		String result = employeeController.clientList(model);

		assertEquals("SMSEM001", result);

		verify(employeeService, times(1))
				.getClient();

		verify(employeeService, times(1))
				.searchEmployeeList();

		verify(model, times(1))
				.addAttribute("client_list", clientList);

		verify(model, times(1))
				.addAttribute("employeeList", employeeList);
	}

	/**
	 * deleteEmployee(社員を正常に削除できる場合)
	 * 正常系テストケース
	 * mockEmployeeService.existsEmployeeはtrueを返す
	 * mockEmployeeService.isEmployeeLockedByOtherUserはfalseを返す
	 * 期待値
	 * EmployeeController.deleteEmployeeが"redirect:/employee/list"を返すこと
	 * EmployeeServiceの削除処理が実行されること
	 */
	@Test
	public void deleteEmployee() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("1001");

		when(session.getAttribute("userId"))
				.thenReturn("nexus001");

		when(employeeService.existsEmployee("1001"))
				.thenReturn(true);

		when(employeeService.isEmployeeLockedByOtherUser(
				"1001", "nexus001"))
						.thenReturn(false);

		String result = employeeController.deleteEmployee(
				employeeForm,
				session,
				model,
				redirectAttributes);

		assertEquals("redirect:/employee/list", result);

		verify(session, times(1))
				.getAttribute("userId");

		verify(session, times(1))
				.setAttribute("employeeForm", employeeForm);

		verify(employeeService, times(1))
				.existsEmployee("1001");

		verify(employeeService, times(1))
				.isEmployeeLockedByOtherUser(
						"1001", "nexus001");

		verify(employeeService, times(1))
				.unlockEmployee("1001", "nexus001");

		verify(employeeService, times(1))
				.deleteEmployee(employeeForm);

		verify(employeeService, times(1))
				.deletePaidVacation(employeeForm);
	}

	/**
	 * deleteEmployee(セッションにユーザーIDが設定されていない場合)
	 * 正常系テストケース
	 * mockEmployeeService.existsEmployeeはtrueを返す
	 * mockEmployeeService.isEmployeeLockedByOtherUserはfalseを返す
	 * 期待値
	 * EmployeeController.deleteEmployeeが"redirect:/employee/list"を返すこと
	 * セッションにデフォルトユーザーID"nexus001"が設定されること
	 * EmployeeServiceの削除処理が実行されること
	 */

	@Test
	public void deleteEmployee2() {
		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("1001");

		when(session.getAttribute("userId"))
				.thenReturn(null);

		when(employeeService.existsEmployee("1001"))
				.thenReturn(true);

		when(employeeService.isEmployeeLockedByOtherUser(
				"1001", "nexus001"))
						.thenReturn(false);

		String result = employeeController.deleteEmployee(
				employeeForm,
				session,
				model,
				redirectAttributes);

		assertEquals("redirect:/employee/list", result);

		verify(session, times(1))
				.getAttribute("userId");

		verify(session, times(1))
				.setAttribute("userId", "nexus001");

		verify(session, times(1))
				.setAttribute("employeeForm", employeeForm);

		verify(employeeService, times(1))
				.existsEmployee("1001");

		verify(employeeService, times(1))
				.isEmployeeLockedByOtherUser(
						"1001", "nexus001");

		verify(employeeService, times(1))
				.unlockEmployee("1001", "nexus001");

		verify(employeeService, times(1))
				.deleteEmployee(employeeForm);

		verify(employeeService, times(1))
				.deletePaidVacation(employeeForm);
	}

	/**
	 * deleteEmployee(対象社員が存在しない場合)
	 * 異常系テストケース
	 * mockEmployeeService.existsEmployeeはfalseを返す
	 * 期待値
	 * EmployeeController.deleteEmployeeが"redirect:/employee/list"を返すこと
	 * COM01E005のメッセージが設定されること
	 * EmployeeServiceの削除処理が実行されないこと
	 */
	@Test
	public void deleteEmployee3() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("9999");

		when(session.getAttribute("userId"))
				.thenReturn("nexus001");

		when(employeeService.existsEmployee("9999"))
				.thenReturn(false);

		when(messageSource.getMessage(
				eq("COM01E005"),
				any(Object[].class),
				eq(Locale.JAPANESE)))
						.thenReturn("該当データが存在しません。");

		String result = employeeController.deleteEmployee(
				employeeForm,
				session,
				model,
				redirectAttributes);

		assertEquals("redirect:/employee/list", result);

		verify(employeeService, times(1))
				.existsEmployee("9999");

		verify(messageSource, times(1))
				.getMessage(
						eq("COM01E005"),
						any(Object[].class),
						eq(Locale.JAPANESE));

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						eq("message"),
						eq("該当データが存在しません。"));

		verify(employeeService, never())
				.deleteEmployee(any(EmployeeForm.class));

		verify(employeeService, never())
				.deletePaidVacation(any(EmployeeForm.class));
	}

	/**
	 * deleteEmployee(他ユーザーが編集中の場合)
	 * 異常系テストケース
	 * mockEmployeeService.existsEmployeeはtrueを返す
	 * mockEmployeeService.isEmployeeLockedByOtherUserはtrueを返す
	 * mockEmployeeService.getEmployeeLockingUserIdはロックしているユーザーIDを返す
	 * 期待値
	 * EmployeeController.deleteEmployeeが"redirect:/employee/list"を返すこと
	 * COM01E006のメッセージが設定されること
	 * EmployeeServiceの削除処理が実行されないこと
	 */
	@Test
	public void deleteEmployee4() {

		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setEmployeeId("1001");

		when(session.getAttribute("userId"))
				.thenReturn("nexus001");

		when(employeeService.existsEmployee("1001"))
				.thenReturn(true);

		when(employeeService.isEmployeeLockedByOtherUser(
				"1001", "nexus001"))
						.thenReturn(true);

		when(employeeService.getEmployeeLockingUserId(
				"1001", "nexus001"))
						.thenReturn("nexus002");

		when(messageSource.getMessage(
				eq("COM01E006"),
				any(Object[].class),
				eq(Locale.JAPANESE)))
						.thenReturn("該当データは他のユーザ「nexus002」が編集中です。");

		String result = employeeController.deleteEmployee(
				employeeForm,
				session,
				model,
				redirectAttributes);

		assertEquals("redirect:/employee/list", result);

		verify(employeeService, times(1))
				.existsEmployee("1001");

		verify(employeeService, times(1))
				.isEmployeeLockedByOtherUser(
						"1001", "nexus001");

		verify(employeeService, times(1))
				.getEmployeeLockingUserId(
						"1001", "nexus001");

		verify(messageSource, times(1))
				.getMessage(
						eq("COM01E006"),
						any(Object[].class),
						eq(Locale.JAPANESE));

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						eq("message"),
						eq("該当データは他のユーザ「nexus002」が編集中です。"));

		verify(employeeService, never())
				.unlockEmployee(anyString(), anyString());

		verify(employeeService, never())
				.deleteEmployee(any(EmployeeForm.class));

		verify(employeeService, never())
				.deletePaidVacation(any(EmployeeForm.class));
	}

}
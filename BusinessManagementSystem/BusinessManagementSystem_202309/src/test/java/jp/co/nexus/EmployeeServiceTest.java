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
	 * mockEmployeeRepository.searchEmployeeは社員一覧を返す
	 * 期待値
	 * EmployeeService.searchEmployeeListがその社員一覧を返すこと
	 */
	@Test
	public void searchEmployee() {
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

		Mockito.when(mockEmployeeRepository.searchEmployeeList())
				.thenReturn(mockList);

		List<Map<String, Object>> result = target.searchEmployeeList();

		assertEquals(mockList, result);

	}

}
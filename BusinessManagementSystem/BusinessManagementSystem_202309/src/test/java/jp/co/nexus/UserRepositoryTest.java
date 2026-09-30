package jp.co.nexus;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
import com.nexus.whc.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		TransactionDbUnitTestExecutionListener.class
})
@ContextConfiguration(classes = UserRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository target;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * deleteUser
	 * 存在しない連番IDを指定した場合、DBの内容が変更されないこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteUser_001() {
		target.deleteUser(999);
	}

	/**
	 * deleteUser
	 * 存在する連番IDを指定した場合、対象ユーザが論理削除されること。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/init-data")
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case02/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteUser_002() {
		target.deleteUser(1);
	}

	/**
	 * existsActiveUser
	 * 有効なユーザが存在する場合、trueを返すこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsActiveUser_001() {
		boolean actual = target.existsActiveUser(1);

		Assert.assertTrue(actual);
	}

	/**
	 * existsActiveUser
	 * 指定した連番IDのユーザが存在しない場合、falseを返すこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsActiveUser_002() {
		boolean actual = target.existsActiveUser(999);

		Assert.assertFalse(actual);
	}

	/**
	 * existsActiveUser
	 * 指定したユーザが削除済みの場合、falseを返すこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void existsActiveUser_003() {
		boolean actual = target.existsActiveUser(1);

		Assert.assertFalse(actual);
	}

	/**
	 * countUser
	 * 条件を指定しない場合、有効なユーザの件数を返すこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_001() {
		int actual = target.countUser("", "", "", "");

		Assert.assertEquals(1, actual);
	}

	/**
	 * countUser
	 * 削除済みユーザを件数に含めないこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void countUser_007() {
		int actual = target.countUser("", "", "", "");

		Assert.assertEquals(0, actual);
	}

	/**
	 * searchList
	 * 条件を指定しない場合、有効なユーザを一覧で取得できること。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void searchList_001() {
		List<Map<String, Object>> actual = target.searchList("", "", "", "", 1, 10);

		Assert.assertEquals(1, actual.size());
		Assert.assertEquals("test001", actual.get(0).get("user_id"));
	}

	/**
	 * searchList
	 * 削除済みユーザしか存在しない場合、一覧に表示されないこと。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void searchList_006() {
		List<Map<String, Object>> actual = target.searchList("", "", "", "", 1, 20);

		Assert.assertTrue(actual.isEmpty());
	}

	/**
	 * searchList
	 * 2ページ目を指定した場合、該当ページのユーザを取得できること。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void searchList_007() {
		jdbcTemplate.update(
				"INSERT INTO m_user "
						+ "(seq_id, user_id, user_name, password, auth_id, "
						+ "mail_address, delete_flg) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)",
				2, "test002", "テストユーザ2", "password", 1,
				"test002@example.com", 0);

		List<Map<String, Object>> actual = target.searchList("", "", "", "", 2, 1);

		Assert.assertEquals(1, actual.size());
		Assert.assertEquals("test002", actual.get(0).get("user_id"));
	}
}
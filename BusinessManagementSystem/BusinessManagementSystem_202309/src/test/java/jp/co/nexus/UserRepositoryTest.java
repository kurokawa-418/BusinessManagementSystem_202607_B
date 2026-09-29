package jp.co.nexus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
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
import com.nexus.whc.form.UserForm;
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
	 * 存在しない連番IDを指定した場合、
	 * DBの内容が変更されないことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteUser_001() {

		int seqId = 999;

		target.deleteUser(seqId);
	}

	/**
	 * deleteUser
	 * 存在する連番IDを指定した場合、
	 * 対象ユーザが論理削除されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/init-data")
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case02/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteUser_002() {
		int seqId = 1;
		target.deleteUser(seqId);
	}

	/**
	 * 有効なユーザが存在する場合、trueを返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsActiveUser_001() {
		boolean actual = target.existsActiveUser(1);

		org.junit.Assert.assertTrue(actual);
	}

	/**
	 * 指定した連番IDのユーザが存在しない場合、falseを返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsActiveUser_002() {
		boolean actual = target.existsActiveUser(999);

		org.junit.Assert.assertFalse(actual);
	}

	/**
	 * ユーザが0件の場合、0を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void getMaxSeqId_002() {
		jdbcTemplate.update("DELETE FROM m_user");

		int actual = target.getMaxSeqId();

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * ユーザが存在する場合、最大の連番IDを返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void getMaxSeqId_001() {
		int actual = target.getMaxSeqId();

		org.junit.Assert.assertEquals(1, actual);
	}

	/**
	 * 削除済みのユーザは、存在していてもfalseを返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void existsActiveUser_003() {
		boolean actual = target.existsActiveUser(1);

		org.junit.Assert.assertFalse(actual);
	}

	/**
	 * 検索条件がない場合、有効なユーザの件数を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_001() {
		int actual = target.countUser("", "", "", "");

		org.junit.Assert.assertEquals(1, actual);
	}

	/**
	 * ユーザIDが一致しない場合、0件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_002() {
		int actual = target.countUser("該当なし", "", "", "");

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * ユーザ名が一致しない場合、0件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_003() {
		int actual = target.countUser("", "該当なし", "", "");

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * 権限IDが一致しない場合、0件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_004() {
		int actual = target.countUser("", "", "999", "");

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * メールアドレスが一致しない場合、0件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_005() {
		int actual = target.countUser("", "", "", "not-found@example.com");

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * 4つの検索条件すべてが一致する場合、1件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void countUser_006() {
		int actual = target.countUser("test", "テスト", "1", "@example.com");

		org.junit.Assert.assertEquals(1, actual);
	}

	/**
	 * 削除済みユーザは件数を含めず、0件を返す。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void countUser_007() {
		int actual = target.countUser("", "", "", "");

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * 有効なユーザの連番IDを指定した場合、該当するユーザIDとユーザ名を取得できること。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findUserBySeqId_001() {
		Map<String, Object> actual = target.findUserBySeqId(1);

		org.junit.Assert.assertEquals("test001", actual.get("user_id"));
		org.junit.Assert.assertEquals("テストユーザ", actual.get("user_name"));
	}

	/**
	 * findUserBySeqId
	 * 存在しない連番IDを指定した場合、
	 * 該当データがないため例外が発生することを確認する。
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findUserBySeqId_002() {
		target.findUserBySeqId(999);
	}

	/**
	 * findUserBySeqId
	 * 削除済みユーザの連番IDを指定した場合、
	 * 取得対象から除外され、例外が発生することを確認する。
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void findUserBySeqId_003() {
		target.findUserBySeqId(1);
	}

	/**
	 * existsUser
	 * 登録済みのユーザIDを指定した場合、
	 * trueを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsUser_001() {
		boolean actual = target.existsUser(
				"test001", "別の名前", "other@example.com");

		org.junit.Assert.assertTrue(actual);
	}

	/**
	 * existsUser
	 * ユーザID・ユーザ名・メールアドレスの
	 * どれも一致しない場合、falseを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsUser_002() {
		boolean actual = target.existsUser(
				"other001", "別の名前", "other@example.com");

		org.junit.Assert.assertFalse(actual);
	}

	/**
	 * existsUser
	 * 登録済みのユーザ名を指定した場合、
	 * trueを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsUser_003() {
		boolean actual = target.existsUser(
				"other001", "テストユーザ", "other@example.com");

		org.junit.Assert.assertTrue(actual);
	}

	/**
	 * existsUser
	 * 登録済みのメールアドレスを指定した場合、
	 * trueを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void existsUser_004() {
		boolean actual = target.existsUser(
				"other001", "別の名前", "test001@example.com");

		org.junit.Assert.assertTrue(actual);
	}

	/**
	 * findDuplicateUser
	 * ユーザIDだけが重複する場合、
	 * 重複項目として「ユーザID」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUser_001() {
		List<String> actual = target.findDuplicateUser(
				"test001", "別の名前", "other@example.com");

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザID"), actual);
	}

	/**
	 * findDuplicateUser
	 * ユーザ名だけが重複する場合、
	 * 重複項目として「ユーザ名」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUser_002() {
		List<String> actual = target.findDuplicateUser(
				"other001", "テストユーザ", "other@example.com");

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザ名"), actual);
	}

	/**
	 * findDuplicateUser
	 * メールアドレスだけが重複する場合、
	 * 重複項目として「メールアドレス」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUser_003() {
		List<String> actual = target.findDuplicateUser(
				"other001", "別の名前", "test001@example.com");

		org.junit.Assert.assertEquals(
				Arrays.asList("メールアドレス"), actual);
	}

	/**
	 * findDuplicateUser
	 * どの項目も重複しない場合、
	 * 空のリストを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUser_004() {
		List<String> actual = target.findDuplicateUser(
				"other001", "別の名前", "other@example.com");

		org.junit.Assert.assertTrue(actual.isEmpty());
	}

	/**
	 * findDuplicateUser
	 * 3項目すべてが重複する場合、
	 * 3つの項目名を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUser_005() {
		List<String> actual = target.findDuplicateUser(
				"test001", "テストユーザ", "test001@example.com");

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザID", "ユーザ名", "メールアドレス"),
				actual);
	}

	/**
	 * findDuplicateUserForUpdate
	 * 更新対象自身と同じ値を指定した場合、
	 * 重複項目はないことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_001() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"test001", "テストユーザ",
				"test001@example.com", 1);

		org.junit.Assert.assertTrue(actual.isEmpty());
	}

	/**
	 * findDuplicateUserForUpdate
	 * 別ユーザのユーザIDと一致する場合、
	 * 「ユーザID」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_002() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"test001", "別の名前",
				"other@example.com", 2);

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザID"), actual);
	}

	/**
	 * findDuplicateUserForUpdate
	 * 別ユーザのユーザ名と一致する場合、
	 * 「ユーザ名」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_003() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"other001", "テストユーザ",
				"other@example.com", 2);

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザ名"), actual);
	}

	/**
	 * findDuplicateUserForUpdate
	 * 別ユーザのメールアドレスと一致する場合、
	 * 「メールアドレス」を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_004() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"other001", "別の名前",
				"test001@example.com", 2);

		org.junit.Assert.assertEquals(
				Arrays.asList("メールアドレス"), actual);
	}

	/**
	 * findDuplicateUserForUpdate
	 * 別ユーザとどの項目も一致しない場合、
	 * 空のリストを返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_005() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"other001", "別の名前",
				"other@example.com", 2);

		org.junit.Assert.assertTrue(actual.isEmpty());
	}

	/**
	 * findDuplicateUserForUpdate
	 * 別ユーザと3項目すべて一致する場合、
	 * 3つの項目名を返すことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void findDuplicateUserForUpdate_006() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"test001", "テストユーザ",
				"test001@example.com", 2);

		org.junit.Assert.assertEquals(
				Arrays.asList("ユーザID", "ユーザ名", "メールアドレス"),
				actual);
	}

	/**
	 * findDuplicateUserForUpdate
	 * 一致するユーザが削除済みの場合、
	 * 重複項目に含めないことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/after-update-data")
	public void findDuplicateUserForUpdate_007() {
		List<String> actual = target.findDuplicateUserForUpdate(
				"test001", "テストユーザ",
				"test001@example.com", 2);

		org.junit.Assert.assertTrue(actual.isEmpty());
	}

	/**
	 * updateUser
	 * 存在する連番IDを指定した場合、
	 * ユーザ名・権限ID・メールアドレスが更新されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void updateUser_001() {
		UserForm form = new UserForm();
		form.setSeqId(1);
		form.setUserName("更新後ユーザ");
		form.setAuthId("2");
		form.setMailAddress("updated@example.com");

		int updated = target.updateUser(form);

		Map<String, Object> actual = jdbcTemplate.queryForMap(
				"SELECT user_name, auth_id, mail_address FROM m_user WHERE seq_id = ?",
				1);

		org.junit.Assert.assertEquals(1, updated);
		org.junit.Assert.assertEquals("更新後ユーザ", actual.get("user_name"));
		org.junit.Assert.assertEquals(2, ((Number) actual.get("auth_id")).intValue());
		org.junit.Assert.assertEquals("updated@example.com", actual.get("mail_address"));
	}

	/**
	 * updateUser
	 * 存在しない連番IDを指定した場合、
	 * 更新件数が0でDBの内容が変わらないことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void updateUser_002() {
		UserForm form = new UserForm();
		form.setSeqId(999);
		form.setUserName("更新後ユーザ");
		form.setAuthId("2");
		form.setMailAddress("updated@example.com");

		int actual = target.updateUser(form);

		org.junit.Assert.assertEquals(0, actual);
	}

	/**
	 * registUser
	 * 新しいユーザ情報を指定した場合、
	 * 1件登録され、DBに入力値が保存されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-data")
	public void registUser_001() {
		UserForm form = new UserForm();
		form.setSeqId(2);
		form.setUserId("test002");
		form.setUserName("新規ユーザ");
		form.setAuthId("1");
		form.setMailAddress("test002@example.com");
		form.setPassword("password2");

		int inserted = target.registUser(form);

		Map<String, Object> actual = jdbcTemplate.queryForMap(
				"SELECT user_id, user_name, auth_id, mail_address, password, delete_flg "
						+ "FROM m_user WHERE seq_id = ?",
				2);

		org.junit.Assert.assertEquals(1, inserted);
		org.junit.Assert.assertEquals("test002", actual.get("user_id"));
		org.junit.Assert.assertEquals("新規ユーザ", actual.get("user_name"));
		org.junit.Assert.assertEquals(1, ((Number) actual.get("auth_id")).intValue());
		org.junit.Assert.assertEquals("test002@example.com", actual.get("mail_address"));
		org.junit.Assert.assertEquals("password2", actual.get("password"));
		org.junit.Assert.assertEquals(Boolean.FALSE, actual.get("delete_flg"));
	}

}
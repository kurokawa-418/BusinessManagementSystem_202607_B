package jp.co.nexus;

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

	@Test
	@DatabaseSetup("/testdata/UserDaoTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/UserDaoTest/case01/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void userDelete001() {

		int seqId = 1;

		target.deleteUser(seqId);
	}

	/**
	 * deleteUser
	 * 存在しない連番IDを指定した場合、
	 * DBの内容が変更されないことを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/UserDaoTest/case01/init-data")
	@ExpectedDatabase(value = "/testdata/UserDaoTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
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
	@DatabaseSetup("/testdata/UserDaoTest/case02/init-data")
	@ExpectedDatabase(value = "/testdata/UserDaoTest/case02/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteUser_002() {

		int seqId = 1;

		target.deleteUser(seqId);
	}

}
package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.nexus.whc.repository.ClientRepository;

@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
	TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@SpringBootApplication
@SpringBootTest(classes = ClientRepositoryTest.TestConfig.class)
@Transactional
public class ClientRepositoryTest {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = ClientRepository.class)
	public static class TestConfig {
	}

	/**
	 * findAllClient
	 * 一覧取得の正常系。
	 *
	 * 有効な顧客が2件存在する場合、
	 * 2件の顧客情報が取得できることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case01/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case01/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void findAllClient_001() {

		List<Map<String, Object>> result = clientRepository.findAllClient();

		// 2件取得されること
		assertEquals(2, result.size());

		// 1件目の確認
		assertEquals(
				101,
				((Number) result.get(0).get("client_id")).intValue());

		assertEquals(
				"株式会社アクサス",
				result.get(0).get("client_name"));

		// 2件目の確認
		assertEquals(
				102,
				((Number) result.get(1).get("client_id")).intValue());

		assertEquals(
				"株式会社イクサス",
				result.get(1).get("client_name"));
	}

	/**
	 * findAllClient
	 * 一覧取得の異常系。
	 *
	 * 有効な顧客が0件の場合、
	 * 空のListが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case02/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case02/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void findAllClient_002() {

		List<Map<String, Object>> result = clientRepository.findAllClient();

		// 0件であること
		assertEquals(0, result.size());
	}

	/**
	 * findAllClient
	 * 一覧取得の正常系。
	 *
	 * 顧客IDの登録順がSQL上の並び順と異なっていても、
	 * client_idの昇順で取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case03/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case03/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void findAllClient_003() {

		List<Map<String, Object>> result = clientRepository.findAllClient();

		// 5件取得されること
		assertEquals(5, result.size());

		// client_idの昇順になっていること
		assertEquals(
				101,
				((Number) result.get(0).get("client_id")).intValue());

		assertEquals(
				102,
				((Number) result.get(1).get("client_id")).intValue());

		assertEquals(
				103,
				((Number) result.get(2).get("client_id")).intValue());

		assertEquals(
				104,
				((Number) result.get(3).get("client_id")).intValue());

		assertEquals(
				105,
				((Number) result.get(4).get("client_id")).intValue());
	}

	/**
	 * deleteClients
	 * 顧客削除の正常系。
	 *
	 * 1件の顧客IDを指定した場合、
	 * 指定した顧客のdelete_flgが1になり、
	 * 更新件数が1件になることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case04/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case04/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（変更あり）
	public void deleteClients_001() {

		List<Integer> clientIds = Arrays.asList(101);

		int result = clientRepository.deleteClients(clientIds);

		// 更新件数が1件であること
		assertEquals(1, result);

		// 削除後の一覧を取得
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		// 102だけ残っていること
		assertEquals(1, clients.size());

		assertEquals(
				102,
				((Number) clients.get(0).get("client_id")).intValue());
		
	}

	/**
	 * deleteClients
	 * 顧客削除の正常系。
	 *
	 * 複数の顧客IDを指定した場合、
	 * 指定した全顧客が削除され、
	 * 更新件数が指定件数になることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case05/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case05/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（変更あり）
	public void deleteClients_002() {

		List<Integer> clientIds = Arrays.asList(103, 104);

		int result = clientRepository.deleteClients(clientIds);

		// 更新件数が2件であること
		assertEquals(2, result);

		// 削除後の一覧を取得
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		// 有効な顧客が0件であること
		assertEquals(0, clients.size());
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * 存在しない顧客IDを指定した場合、
	 * 更新件数が0件になることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case06/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case06/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClients_003() {

		List<Integer> clientIds = Arrays.asList(999);

		int result = clientRepository.deleteClients(clientIds);

		// 更新対象が存在しないため0件
		assertEquals(0, result);

		// 元の2件が残っていること
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		assertEquals(2, clients.size());
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * 存在する顧客IDと存在しない顧客IDを
	 * 同時に指定した場合、
	 * 存在する顧客だけが削除されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case07/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case07/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（変更あり）
	public void deleteClients_004() {

		List<Integer> clientIds = Arrays.asList(106, 999);

		int result = clientRepository.deleteClients(clientIds);

		// 存在する106だけが更新される
		assertEquals(1, result);

		// 101,102だけが有効データとして残る
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		assertEquals(2, clients.size());

		assertEquals(
				101,
				((Number) clients.get(0).get("client_id")).intValue());
		
		assertEquals(
				102,
				((Number) clients.get(1).get("client_id")).intValue());
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * 既に削除済みの顧客IDを指定した場合、
	 * 現在のSQLではdelete_flgの状態を条件にしていないため、
	 * 更新件数が1件になることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case08/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case08/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClients_005() {

		List<Integer> clientIds = Arrays.asList(107);

		int result = clientRepository.deleteClients(clientIds);

		/*
		 * 現在のdeleteClients()は
		 * WHERE client_id IN (...)
		 * のみでdelete_flg=0を条件にしていないため、
		 * 既にdelete_flg=1でも更新件数は1になる。
		 */
		assertEquals(1, result);
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * 同一の顧客IDを重複して指定した場合、
	 * 同じ顧客が二重に更新されることはなく、
	 * 更新件数が1件になることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case09/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case09/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（変更あり）
	public void deleteClients_006() {

		List<Integer> clientIds = Arrays.asList(108, 108);

		int result = clientRepository.deleteClients(clientIds);

		// 同じレコードは1件として更新される
		assertEquals(1, result);

		// 108が一覧から除外されること
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		assertEquals(2, clients.size());

		assertEquals(
				101,
				((Number) clients.get(0).get("client_id")).intValue());
		
		assertEquals(
				102,
				((Number) clients.get(1).get("client_id")).intValue());
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * 空のListを指定した場合、
	 * 現在の実装ではIN句のプレースホルダが生成されないため、
	 * SQLエラーになることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case10/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case10/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClients_007() {

		List<Integer> clientIds = Arrays.asList();

		try {

			clientRepository.deleteClients(clientIds);

			// SQLエラーが発生しなかった場合はテスト失敗
			fail("空のListを指定した場合にSQLエラーが発生しませんでした。");

		} catch (BadSqlGrammarException e) {

			// SQLエラーが発生すればOK
		}
	}

	/**
	 * deleteClients
	 * 顧客削除の異常系。
	 *
	 * nullを指定した場合、
	 * 現在の実装ではclientIds.size()を呼び出すため、
	 * NullPointerExceptionが発生することを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case11/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case11/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClients_008() {

		List<Integer> clientIds = null;

		try {

			clientRepository.deleteClients(clientIds);

			// 例外が発生しなかった場合はテスト失敗
			fail("nullを指定した場合にNullPointerExceptionが発生しませんでした。");

		} catch (NullPointerException e) {

			// NullPointerExceptionが発生すればOK
		}
	}
}
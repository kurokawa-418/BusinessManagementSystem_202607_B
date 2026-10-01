package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
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
import com.nexus.whc.repository.ClientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@ContextConfiguration(classes = ClientRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional
public class ClientRepositoryTest {

	@Autowired
	private ClientRepository clientRepository;

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

		List<Integer> clientIds = Arrays.asList(100);

		int result = clientRepository.deleteClients(clientIds);

		// 更新件数が1件であること
		assertEquals(1, result);

		// 削除後の一覧を取得
		List<Map<String, Object>> clients = clientRepository.findAllClient();

		// 101,102だけ残っていること
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

	/**
	 * searchClients
	 * 一覧検索の正常系。
	 *
	 * 顧客ID、顧客名を指定しない場合、
	 * 有効な顧客がclient_id昇順で取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_001() {

		List<Map<String, Object>> result = clientRepository.searchClients("", "", 1);

		assertEquals(5, result.size());

		assertEquals(
				109,
				((Number) result.get(0).get("client_id")).intValue());

		assertEquals(
				113,
				((Number) result.get(4).get("client_id")).intValue());
	}
	
	/**
	 * searchClients
	 * 顧客ID検索の正常系。
	 *
	 * 顧客IDを指定した場合、
	 * 指定した顧客だけが取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_002() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients("109", "", 1);

	    assertEquals(1, result.size());

	    assertEquals(
	            109,
	            ((Number) result.get(0).get("client_id")).intValue());

	    assertEquals(
	            "株式会社ケクサス",
	            result.get(0).get("client_name"));
	}
	
	/**
	 * searchClients
	 * 顧客名検索の正常系。
	 *
	 * 顧客名の一部を指定した場合、
	 * 指定文字列を含む顧客が取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_003() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients("", "ケクサス", 1);

	    assertEquals(1, result.size());

	    assertEquals(
	            "株式会社ケクサス",
	            result.get(0).get("client_name"));
	}
	
	/**
	 * searchClients
	 * 顧客ID、顧客名検索の正常系。
	 *
	 * 顧客IDと顧客名の両方を指定した場合、
	 * 両方の条件を満たす顧客だけが取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_004() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients(
	                    "109",
	                    "ケクサス",
	                    1);

	    assertEquals(1, result.size());

	    assertEquals(
	            109,
	            ((Number) result.get(0).get("client_id")).intValue());
	}
	
	/**
	 * searchClients
	 * 一覧検索の異常系。
	 *
	 * 該当する顧客が存在しない場合、
	 * 空のListが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_005() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients("999", "", 1);

	    assertEquals(0, result.size());
	}
	
	/**
	 * searchClients
	 * 一覧検索の正常系。
	 *
	 * 顧客IDと顧客名がnullの場合、
	 * 検索条件なしとして取得されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_006() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients(null, null, 1);

	    assertEquals(5, result.size());
	}
	
	/**
	 * searchClients
	 * 一覧検索の異常系。
	 *
	 * 顧客IDと顧客名の両方を指定し、
	 * 両方の条件を満たす顧客が存在しない場合、
	 * 空のListが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchClients_007() {

	    List<Map<String, Object>> result =
	            clientRepository.searchClients(
	                    "101",
	                    "存在しない顧客",
	                    1);

	    assertEquals(0, result.size());
	}
	
	/**
	 * countClients
	 * 顧客件数取得の正常系。
	 *
	 * 検索条件を指定しない場合、
	 * 有効な顧客の総件数が取得できることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_001() {

	    int result =
	            clientRepository.countClients("", "");

	    assertEquals(5, result);
	}
	
	/**
	 * countClients
	 * 顧客ID検索の正常系。
	 *
	 * 顧客IDを指定した場合、
	 * 該当する顧客の件数が取得できることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_002() {

	    int result =
	            clientRepository.countClients("110", "");

	    assertEquals(1, result);
	}
	
	/**
	 * countClients
	 * 顧客名検索の正常系。
	 *
	 * 顧客名を指定した場合、
	 * 指定文字列を含む顧客の件数が取得できることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_003() {

	    int result =
	            clientRepository.countClients("", "コクサス");

	    assertEquals(1, result);
	}
	
	/**
	 * countClients
	 * 顧客ID、顧客名検索の正常系。
	 *
	 * 顧客IDと顧客名の両方を指定した場合、
	 * 両方の条件を満たす顧客の件数が取得できることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_004() {

	    int result =
	            clientRepository.countClients(
	                    "110",
	                    "コクサス");

	    assertEquals(1, result);
	}
	
	/**
	 * countClients
	 * 顧客件数取得の異常系。
	 *
	 * 該当する顧客が存在しない場合、
	 * 0件が返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_005() {

	    int result =
	            clientRepository.countClients("999", "");

	    assertEquals(0, result);
	}
	
	/**
	 * countClients
	 * 顧客件数取得の正常系。
	 *
	 * 顧客IDと顧客名がnullの場合、
	 * 検索条件なしとして件数を取得することを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_006() {

	    int result =
	            clientRepository.countClients(null, null);

	    assertEquals(5, result);
	}
	
	/**
	 * countClients
	 * 顧客件数取得の異常系。
	 *
	 * 顧客IDと顧客名の両方を指定し、
	 * 両方の条件を満たす顧客が存在しない場合、
	 * 0件が返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClients_007() {

	    int result =
	            clientRepository.countClients(
	                    "999",
	                    "存在しない顧客");

	    assertEquals(0, result);
	}
	
	/**
	 * existsActiveClient
	 * 有効顧客存在チェックの正常系。
	 *
	 * 存在する有効な顧客IDを指定した場合、
	 * trueが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void existsActiveClient_001() {

	    boolean result =
	            clientRepository.existsActiveClient(111);

	    assertTrue(result);
	}
	
	/**
	 * existsActiveClient
	 * 有効顧客存在チェックの異常系。
	 *
	 * 存在する顧客でも削除済みの場合、
	 * falseが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case13/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case13/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void existsActiveClient_002() {

	    boolean result =
	            clientRepository.existsActiveClient(111);

	    assertFalse(result);
	}
	
	/**
	 * existsActiveClient
	 * 有効顧客存在チェックの異常系。
	 *
	 * 存在しない顧客IDを指定した場合、
	 * falseが返却されることを確認する。
	 */
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case12/init-data")
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case12/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void existsActiveClient_003() {

	    boolean result =
	            clientRepository.existsActiveClient(999);

	    assertFalse(result);
	}
	
	
}
package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.nexus.whc.repository.ClientRepository;
import com.nexus.whc.services.ClientService;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    /**
     * findAllClient
     * 一覧取得の正常系。
     *
     * ClientRepositoryから顧客情報が取得された場合、
     * ClientServiceがその結果をそのまま返却することを確認する。
     */
    @Test
    public void findAllClient_001() {

        // Repositoryから返却されるテストデータを作成
        List<Map<String, Object>> expected =
                new ArrayList<Map<String, Object>>();

        Map<String, Object> client = new HashMap<String, Object>();

        client.put("client_id", 101);
        client.put("client_name", "株式会社アクサス");

        expected.add(client);

        // Repositoryの戻り値を設定
        when(clientRepository.findAllClient())
                .thenReturn(expected);

        // Serviceを実行
        List<Map<String, Object>> result =
                clientService.findAllClient();

        // Repositoryから返された結果と同じであること
        assertEquals(expected, result);

        // Repositoryが1回呼び出されたこと
        verify(clientRepository, times(1))
                .findAllClient();
    }

    /**
     * findAllClient。
     * 一覧取得の正常系。
     *
     * ClientRepositoryから0件のListが返却された場合、
     * ClientServiceも0件のListを返却することを確認する。
     */
    @Test
    public void findAllClient_002() {

        // Repositoryから空のListが返却されるように設定
        List<Map<String, Object>> expected =
                new ArrayList<Map<String, Object>>();

        when(clientRepository.findAllClient())
                .thenReturn(expected);

        // Serviceを実行
        List<Map<String, Object>> result =
                clientService.findAllClient();

        // 0件であること
        assertEquals(0, result.size());

        // Repositoryが1回呼び出されたこと
        verify(clientRepository, times(1))
                .findAllClient();
    }

    /**
     * deleteClients
     * 顧客削除の正常系。
     *
     * 1件の顧客IDを指定した場合、
     * ClientRepositoryの戻り値がServiceからそのまま返却されることを確認する。
     */
    @Test
    public void deleteClients_001() {

        // 削除対象の顧客ID
        List<Integer> clientIds =
                Arrays.asList(101);

        // Repositoryが1件更新したと設定
        when(clientRepository.deleteClients(clientIds))
                .thenReturn(1);

        // Serviceを実行
        int result =
                clientService.deleteClients(clientIds);

        // 更新件数が1件であること
        assertEquals(1, result);

        // Repositoryが指定したIDで1回呼び出されたこと
        verify(clientRepository, times(1))
                .deleteClients(clientIds);
    }

    /**
     * deleteClients
     * 顧客削除の正常系。
     *
     * 複数の顧客IDを指定した場合、
     * ClientRepositoryの戻り値がServiceからそのまま返却されることを確認する。
     */
    @Test
    public void deleteClients_002() {

        // 削除対象の顧客ID
        List<Integer> clientIds =
                Arrays.asList(101, 102);

        // Repositoryが2件更新したと設定
        when(clientRepository.deleteClients(clientIds))
                .thenReturn(2);

        // Serviceを実行
        int result =
                clientService.deleteClients(clientIds);

        // 更新件数が2件であること
        assertEquals(2, result);

        // Repositoryが指定したIDで1回呼び出されたこと
        verify(clientRepository, times(1))
                .deleteClients(clientIds);
    }

    /**
     * deleteClients
     * 顧客削除の正常系。
     *
     * 存在しない顧客IDを指定した場合、
     * ClientRepositoryが0件を返却し、
     * ClientServiceも0件を返却することを確認する。
     */
    @Test
    public void deleteClients_003() {

        // 存在しない顧客ID
        List<Integer> clientIds =
                Arrays.asList(999);

        // Repositoryが0件更新したと設定
        when(clientRepository.deleteClients(clientIds))
                .thenReturn(0);

        // Serviceを実行
        int result =
                clientService.deleteClients(clientIds);

        // 更新件数が0件であること
        assertEquals(0, result);

        // Repositoryが指定したIDで1回呼び出されたこと
        verify(clientRepository, times(1))
                .deleteClients(clientIds);
    }

    /**
     * deleteClients
     * 顧客削除の異常系。
     *
     * nullを指定した場合、
     * ClientServiceからClientRepositoryにnullがそのまま渡されることを確認する。
     */
    @Test
    public void deleteClients_004() {

        // nullを設定
        List<Integer> clientIds = null;

        // Repositoryが0件を返却するように設定
        when(clientRepository.deleteClients(clientIds))
                .thenReturn(0);

        // Serviceを実行
        int result =
                clientService.deleteClients(clientIds);

        // Repositoryの戻り値がそのまま返却されること
        assertEquals(0, result);

        // nullがそのままRepositoryに渡されていること
        verify(clientRepository, times(1))
                .deleteClients(clientIds);
    }
    
    /**
     * searchClients
     * 顧客一覧検索の正常系。
     *
     * Repositoryから取得した顧客一覧が、
     * そのまま返却されることを確認する。
     */
    @Test
    public void searchClients_001() {

        List<Map<String, Object>> expected =
                new ArrayList<Map<String, Object>>();

        Map<String, Object> client = new HashMap<String, Object>();
        client.put("client_id", 101);
        client.put("client_name", "株式会社アクサス");

        expected.add(client);

        when(clientRepository.searchClients("101", "", 1))
                .thenReturn(expected);

        List<Map<String, Object>> result =
                clientService.searchClients("101", "", 1);

        assertEquals(expected, result);

        verify(clientRepository)
                .searchClients("101", "", 1);
    }
    
    /**
     * searchClients
     * 顧客一覧検索の正常系。
     *
     * 検索条件が空文字の場合でも、
     * Repositoryにそのまま渡され、
     * Repositoryの結果が返却されることを確認する。
     */
    @Test
    public void searchClients_002() {

        List<Map<String, Object>> expected =
                new ArrayList<Map<String, Object>>();

        when(clientRepository.searchClients("", "", 1))
                .thenReturn(expected);

        List<Map<String, Object>> result =
                clientService.searchClients("", "", 1);

        assertEquals(expected, result);

        verify(clientRepository)
                .searchClients("", "", 1);
    }
    
    /**
     * searchClients
     * 顧客一覧検索の正常系。
     *
     * 顧客IDと顧客名がnullの場合でも、
     * Repositoryにそのまま渡され、
     * Repositoryの結果が返却されることを確認する。
     */
    @Test
    public void searchClients_003() {

        List<Map<String, Object>> expected =
                new ArrayList<Map<String, Object>>();

        when(clientRepository.searchClients(null, null, 1))
                .thenReturn(expected);

        List<Map<String, Object>> result =
                clientService.searchClients(null, null, 1);

        assertEquals(expected, result);

        verify(clientRepository)
                .searchClients(null, null, 1);
    }
    
    /**
     * countClients
     * 顧客件数取得の正常系。
     *
     * Repositoryから取得した件数が、
     * そのまま返却されることを確認する。
     */
    @Test
    public void countClients_001() {

        when(clientRepository.countClients("101", "アクサス"))
                .thenReturn(1);

        int result =
                clientService.countClients("101", "アクサス");

        assertEquals(1, result);

        verify(clientRepository)
                .countClients("101", "アクサス");
    }
    
    /**
     * countClients
     * 顧客件数取得の正常系。
     *
     * 検索条件を指定しない場合、
     * Repositoryから取得した件数が返却されることを確認する。
     */
    @Test
    public void countClients_002() {

        when(clientRepository.countClients("", ""))
                .thenReturn(5);

        int result =
                clientService.countClients("", "");

        assertEquals(5, result);

        verify(clientRepository)
                .countClients("", "");
    }
    
    /**
     * countClients
     * 顧客件数取得の正常系。
     *
     * 該当する顧客が存在しない場合、
     * Repositoryから返された0件がそのまま返却されることを確認する。
     */
    @Test
    public void countClients_003() {

        when(clientRepository.countClients("999", ""))
                .thenReturn(0);

        int result =
                clientService.countClients("999", "");

        assertEquals(0, result);

        verify(clientRepository)
                .countClients("999", "");
    }
    
    /**
     * countClients
     * 顧客件数取得の正常系。
     *
     * 顧客IDと顧客名がnullの場合でも、
     * Repositoryにそのまま渡されることを確認する。
     */
    @Test
    public void countClients_004() {

        when(clientRepository.countClients(null, null))
                .thenReturn(5);

        int result =
                clientService.countClients(null, null);

        assertEquals(5, result);

        verify(clientRepository)
                .countClients(null, null);
    }
    
    /**
     * existsActiveClient
     * 有効顧客存在チェックの正常系。
     *
     * Repositoryがtrueを返した場合、
     * Serviceもtrueを返すことを確認する。
     */
    @Test
    public void existsActiveClient_001() {

        when(clientRepository.existsActiveClient(101))
                .thenReturn(true);

        boolean result =
                clientService.existsActiveClient(101);

        assertTrue(result);

        verify(clientRepository)
                .existsActiveClient(101);
    }
    
    /**
     * existsActiveClient
     * 有効顧客存在チェックの正常系。
     *
     * Repositoryがfalseを返した場合、
     * Serviceもfalseを返すことを確認する。
     */
    @Test
    public void existsActiveClient_002() {

        when(clientRepository.existsActiveClient(102))
                .thenReturn(false);

        boolean result =
                clientService.existsActiveClient(102);

        assertFalse(result);

        verify(clientRepository)
                .existsActiveClient(102);
    }
    
    /**
     * existsActiveClient
     * 有効顧客存在チェックの異常系。
     *
     * 存在しない顧客IDを指定した場合、
     * Repositoryから返されたfalseがそのまま返却されることを確認する。
     */
    @Test
    public void existsActiveClient_003() {

        when(clientRepository.existsActiveClient(999))
                .thenReturn(false);

        boolean result =
                clientService.existsActiveClient(999);

        assertFalse(result);

        verify(clientRepository)
                .existsActiveClient(999);
    }
    
    /**
     * existsActiveClient
     * 有効顧客存在チェックの異常系。
     *
     * 顧客IDがnullの場合でも、
     * Repositoryにnullがそのまま渡されることを確認する。
     */
    @Test
    public void existsActiveClient_004() {

        when(clientRepository.existsActiveClient(null))
                .thenReturn(false);

        boolean result =
                clientService.existsActiveClient(null);

        assertFalse(result);

        verify(clientRepository)
                .existsActiveClient(null);
    }
}
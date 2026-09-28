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
}
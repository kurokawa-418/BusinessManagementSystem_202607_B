package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.controller.ClientController;
import com.nexus.whc.services.ClientService;
import com.nexus.whc.services.LockService;

@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

	@InjectMocks
	private ClientController clientController;

	@Mock
	private ClientService clientService;

	@Mock
	private LockService lockService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	@Mock
	private HttpSession session;

	/**
	 * clientList
	 * 顧客一覧を正常に取得できることを確認する。
	 */
	@Test
	public void clientList_001() {

		List<Map<String, Object>> clientList =
				new ArrayList<Map<String, Object>>();

		Map<String, Object> client =
				new HashMap<String, Object>();

		client.put("client_id", 101);
		client.put("client_name", "株式会社アクサス");

		clientList.add(client);

		when(clientService.searchClients("", "", 1))
				.thenReturn(clientList);

		when(clientService.countClients("", ""))
				.thenReturn(1);

		String result =
				clientController.clientList(
						"",
						"",
						false,
						1,
						model);

		assertEquals("SMSCL001", result);

		verify(clientService, times(1))
				.searchClients("", "", 1);

		verify(clientService, times(1))
				.countClients("", "");

		verify(model, times(1))
				.addAttribute("clientList", clientList);

		verify(model, times(1))
				.addAttribute("clientId", "");

		verify(model, times(1))
				.addAttribute("clientName", "");

		verify(model, times(1))
				.addAttribute("page", 1);

		verify(model, times(1))
				.addAttribute("totalPages", 1);
	}

	/**
	 * clientList
	 * 検索結果が0件の場合、
	 * 検索結果なしのメッセージが設定されることを確認する。
	 */
	@Test
	public void clientList_002() {

		List<Map<String, Object>> clientList =
				new ArrayList<Map<String, Object>>();

		when(clientService.searchClients(
				"999",
				"",
				1))
				.thenReturn(clientList);

		when(clientService.countClients(
				"999",
				""))
				.thenReturn(0);

		when(messageSource.getMessage(
				"COM01W001",
				new Object[] { null, "顧客" },
				null))
				.thenReturn("顧客一覧の検索結果は0件です。条件を変更し、再度検索してください。");

		String result =
				clientController.clientList(
						"999",
						"",
						true,
						1,
						model);

		assertEquals("SMSCL001", result);

		verify(model, times(1))
				.addAttribute(
						"message",
						"顧客一覧の検索結果は0件です。条件を変更し、再度検索してください。");

		verify(model, times(1))
				.addAttribute(
						"clientList",
						clientList);
	}

	/**
	 * clientList
	 * 顧客件数が21件以上の場合、
	 * 2ページになることを確認する。
	 */
	@Test
	public void clientList_003() {

		List<Map<String, Object>> clientList =
				new ArrayList<Map<String, Object>>();

		when(clientService.searchClients(
				"",
				"",
				1))
				.thenReturn(clientList);

		when(clientService.countClients(
				"",
				""))
				.thenReturn(21);

		String result =
				clientController.clientList(
						"",
						"",
						false,
						1,
						model);

		assertEquals("SMSCL001", result);

		verify(model, times(1))
				.addAttribute(
						"totalPages",
						2);
	}

	/**
	 * clientList
	 * 指定されたページ番号が
	 * Modelに設定されることを確認する。
	 */
	@Test
	public void clientList_004() {

		List<Map<String, Object>> clientList =
				new ArrayList<Map<String, Object>>();

		when(clientService.searchClients(
				"",
				"",
				2))
				.thenReturn(clientList);

		when(clientService.countClients(
				"",
				""))
				.thenReturn(21);

		String result =
				clientController.clientList(
						"",
						"",
						false,
						2,
						model);

		assertEquals("SMSCL001", result);

		verify(clientService, times(1))
				.searchClients("", "", 2);

		verify(model, times(1))
				.addAttribute("page", 2);
	}
	
	/**
	 * createPageNumbers
	 * 総ページ数が5ページ以下の場合
	 * [1, 2, 3, 4, 5]
	 */
	@Test
	public void createPageNumbers_001() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						1,
						5);

		assertEquals(
				Arrays.asList(1, 2, 3, 4, 5),
				result);
	}

	/**
	 * createPageNumbers
	 * 現在ページが1～2ページ目の場合
	 * [1, 2, 3, -1, 10]
	 */
	@Test
	public void createPageNumbers_002() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						1,
						10);

		assertEquals(
				Arrays.asList(1, 2, 3, -1, 10),
				result);
	}

	/**
	 * createPageNumbers
	 * 現在ページが3ページ目の場合
	 * [1, 2, 3, 4, -1, 10]
	 */
	@Test
	public void createPageNumbers_003() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						3,
						10);

		assertEquals(
				Arrays.asList(1, 2, 3, 4, -1, 10),
				result);
	}

	/**
	 * createPageNumbers
	 * 現在ページが最終ページの2つ前の場合
	 * [1, -1, 7, 8, 9, 10]
	 */
	@Test
	public void createPageNumbers_004() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						8,
						10);

		assertEquals(
				Arrays.asList(1, -1, 7, 8, 9, 10),
				result);
	}

	/**
	 * createPageNumbers
	 * 現在ページが最終ページの1つ前の場合
	 * [1, -1, 8, 9, 10]
	 */
	@Test
	public void createPageNumbers_005() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						9,
						10);

		assertEquals(
				Arrays.asList(1, -1, 8, 9, 10),
				result);
	}

	/**
	 * createPageNumbers
	 * 現在ページが中間ページの場合
	 * [1, -1, 4, 5, 6, -1, 10]
	 */
	@Test
	public void createPageNumbers_006() {

		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>) ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						5,
						10);

		assertEquals(
				Arrays.asList(1, -1, 4, 5, 6, -1, 10),
				result);
	}
	
	/**
	 * createPageNumbers
	 * 検索結果が0件の場合
	 * ページ番号が表示されないこと
	 */
	@Test
	public void createPageNumbers_007() {
		
		@SuppressWarnings("unchecked")
		List<Integer> result =
				(List<Integer>)ReflectionTestUtils.invokeMethod(
						clientController,
						"createPageNumbers",
						1,
						0);
		assertEquals(
				Arrays.asList(),
				result);
	}
	
	/**
	 * postDelete
	 * 顧客を1件正常に削除できることを確認する。
	 */
	@Test
	public void postDelete_001() {

		List<Integer> clientIds =
				Arrays.asList(101);

		when(clientService.existsActiveClient(101))
				.thenReturn(true);

		when(lockService.isLocked(
				"m_client",
				101))
				.thenReturn(false);

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(clientService, times(1))
				.deleteClients(clientIds);
	}

	/**
	 * postDelete
	 * 顧客を複数件正常に削除できることを確認する。
	 */
	@Test
	public void postDelete_002() {

		List<Integer> clientIds =
				Arrays.asList(101, 102);

		when(clientService.existsActiveClient(101))
				.thenReturn(true);

		when(clientService.existsActiveClient(102))
				.thenReturn(true);

		when(lockService.isLocked(
				"m_client",
				101))
				.thenReturn(false);

		when(lockService.isLocked(
				"m_client",
				102))
				.thenReturn(false);

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(clientService, times(1))
				.deleteClients(clientIds);
	}

	/**
	 * postDelete
	 * 顧客が未選択(null)の場合、
	 * 削除せず警告メッセージを設定することを確認する。
	 */
	@Test
	public void postDelete_003() {

		List<Integer> clientIds = null;

		when(messageSource.getMessage(
				"COM01W003",
				new Object[] {},
				null))
				.thenReturn("対象が選択されていません。対象を選択してください。");

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						"message",
						"対象が選択されていません。対象を選択してください。");

		verify(clientService, never())
				.deleteClients(
						org.mockito.ArgumentMatchers.anyList());
	}

	/**
	 * postDelete
	 * 顧客が1件も選択されていない場合、
	 * 削除せず警告メッセージを設定することを確認する。
	 */
	@Test
	public void postDelete_004() {

		List<Integer> clientIds =
				new ArrayList<Integer>();

		when(messageSource.getMessage(
				"COM01W003",
				new Object[] {},
				null))
				.thenReturn("対象が選択されていません。対象を選択してください。");

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						"message",
						"対象が選択されていません。対象を選択してください。");

		verify(clientService, never())
				.deleteClients(
						org.mockito.ArgumentMatchers.anyList());
	}

	/**
	 * postDelete
	 * 選択された顧客が削除済みの場合、
	 * 削除せずエラーメッセージを設定することを確認する。
	 */
	@Test
	public void postDelete_005() {

		List<Integer> clientIds =
				Arrays.asList(101);

		when(clientService.existsActiveClient(101))
				.thenReturn(false);

		when(messageSource.getMessage(
				"COM01E005",
				new Object[] {},
				null))
				.thenReturn("該当データはすでに削除されています。");

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						"message",
						"該当データはすでに削除されています。");

		verify(lockService, never())
				.isLocked(
						"m_client",
						101);

		verify(clientService, never())
				.deleteClients(
						org.mockito.ArgumentMatchers.anyList());
	}

	/**
	 * postDelete
	 * 選択された顧客が他ユーザーによって
	 * 編集中の場合、削除しないことを確認する。
	 */
	@Test
	public void postDelete_006() {

		List<Integer> clientIds =
				Arrays.asList(101);

		when(clientService.existsActiveClient(101))
				.thenReturn(true);

		when(lockService.isLocked(
				"m_client",
				101))
				.thenReturn(true);

		when(lockService.getLockingUserId(
				"m_client",
				101))
				.thenReturn("nexus002");

		when(messageSource.getMessage(
				"COM01E006",
				new Object[] { null, "nexus002" },
				null))
				.thenReturn(
						"該当データは他のユーザ「nexus002」が編集中です。");

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(redirectAttributes, times(1))
				.addFlashAttribute(
						"message",
						"該当データは他のユーザ「nexus002」が編集中です。");

		verify(clientService, never())
				.deleteClients(
						org.mockito.ArgumentMatchers.anyList());
	}

	/**
	 * postDelete
	 * 複数件削除する際、途中の顧客が削除済みの場合、
	 * それ以降の削除処理を行わず全件削除しないことを確認する。
	 */
	@Test
	public void postDelete_007() {

		List<Integer> clientIds =
				Arrays.asList(101, 102);

		when(clientService.existsActiveClient(101))
				.thenReturn(true);

		when(lockService.isLocked(
				"m_client",
				101))
				.thenReturn(false);

		when(clientService.existsActiveClient(102))
				.thenReturn(false);

		when(messageSource.getMessage(
				"COM01E005",
				new Object[] {},
				null))
				.thenReturn("該当データはすでに削除されています。");

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(clientService, never())
				.deleteClients(
						org.mockito.ArgumentMatchers.anyList());

		verify(lockService, never())
				.isLocked(
						"m_client",
						102);
	}

	/**
	 * postDelete
	 * 複数件の顧客がすべて正常な場合、
	 * 全件削除処理が実行されることを確認する。
	 */
	@Test
	public void postDelete_008() {

		List<Integer> clientIds =
				Arrays.asList(101, 102, 103);

		when(clientService.existsActiveClient(101))
				.thenReturn(true);

		when(clientService.existsActiveClient(102))
				.thenReturn(true);

		when(clientService.existsActiveClient(103))
				.thenReturn(true);

		when(lockService.isLocked(
				"m_client",
				101))
				.thenReturn(false);

		when(lockService.isLocked(
				"m_client",
				102))
				.thenReturn(false);

		when(lockService.isLocked(
				"m_client",
				103))
				.thenReturn(false);

		String result =
				clientController.postDelete(
						clientIds,
						redirectAttributes);

		assertEquals(
				"redirect:/client/list",
				result);

		verify(clientService, times(1))
				.deleteClients(clientIds);

		verify(clientService, times(1))
				.existsActiveClient(101);

		verify(clientService, times(1))
				.existsActiveClient(102);

		verify(clientService, times(1))
				.existsActiveClient(103);
	}
}
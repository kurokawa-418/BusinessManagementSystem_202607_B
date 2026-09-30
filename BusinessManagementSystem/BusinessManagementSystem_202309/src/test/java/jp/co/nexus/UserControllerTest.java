package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.nexus.whc.controller.UserController;
import com.nexus.whc.services.LockService;
import com.nexus.whc.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private LockService lockService;

	@InjectMocks
	private UserController target;

	/**
	 * userList
	 * 検索条件なしで一覧を開いた場合、ユーザ一覧と件数を画面に渡すこと。
	 */
	@Test
	public void userList_001() {
		List<Map<String, Object>> users = Collections.emptyList();
		when(userService.countUser("", "", "", "")).thenReturn(0);
		when(userService.searchList("", "", "", "", 1, 20)).thenReturn(users);
		ExtendedModelMap model = new ExtendedModelMap();

		String view = target.userList("", "", "", "", false, 1, model);

		assertEquals("SMSUS001", view);
		assertEquals(users, model.get("userList"));
		assertEquals(0, model.get("totalCount"));
		assertEquals(1, model.get("page"));
		verify(userService).countUser("", "", "", "");
		verify(userService).searchList("", "", "", "", 1, 20);
	}

	/**
	 * userList
	 * 21件ある場合、2ページ目を取得し、全2ページとすること。
	 */
	@Test
	public void userList_002() {
		List<Map<String, Object>> users = Collections.emptyList();
		when(userService.countUser("", "", "", "")).thenReturn(21);
		when(userService.searchList("", "", "", "", 2, 20)).thenReturn(users);
		ExtendedModelMap model = new ExtendedModelMap();

		String view = target.userList("", "", "", "", false, 2, model);

		assertEquals("SMSUS001", view);
		assertEquals(2, model.get("page"));
		assertEquals(2, model.get("totalPages"));
		assertEquals(21, model.get("totalCount"));
		verify(userService).searchList("", "", "", "", 2, 20);
	}

	/**
	 * userList
	 * 最終ページを超えるページ番号を指定した場合、最終ページを表示すること。
	 */
	@Test
	public void userList_003() {
		List<Map<String, Object>> users = Collections.emptyList();
		when(userService.countUser("", "", "", "")).thenReturn(21);
		when(userService.searchList("", "", "", "", 2, 20)).thenReturn(users);
		ExtendedModelMap model = new ExtendedModelMap();

		String view = target.userList("", "", "", "", false, 99, model);

		assertEquals("SMSUS001", view);
		assertEquals(2, model.get("page"));
		verify(userService).searchList("", "", "", "", 2, 20);
	}

	/**
	 * deleteUser
	 * 削除対象が未選択の場合、削除せず一覧へ戻り、メッセージを表示すること。
	 */
	@Test
	public void deleteUser_001() {
		when(messageSource.getMessage("COM01W003", null, Locale.JAPAN))
				.thenReturn("削除対象を選択してください");

		RedirectAttributesModelMap attr = new RedirectAttributesModelMap();
		MockHttpSession session = new MockHttpSession();

		String view = target.deleteUser(null, attr, session);

		assertEquals("redirect:/user/list", view);
		assertEquals("削除対象を選択してください",
				attr.getFlashAttributes().get("message"));
		verify(userService, never()).deleteUser(anyInt());
	}

	/**
	 * deleteUser
	 * 対象ユーザが有効で、他のユーザが編集中でない場合、削除すること。
	 */
	@Test
	public void deleteUser_002() {
		when(userService.existsActiveUser(1)).thenReturn(true);

		RedirectAttributesModelMap attr = new RedirectAttributesModelMap();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("userId", "testUser");

		String view = target.deleteUser(
				Collections.singletonList(1), attr, session);

		assertEquals("redirect:/user/list", view);
		verify(userService).existsActiveUser(1);
		verify(lockService).isLockedByOtherUser("m_user", 1, "testUser");
		verify(userService).deleteUser(1);
	}

	/**
	 * deleteUser
	 * 対象ユーザが削除済みの場合、削除せず一覧へ戻ること。
	 */
	@Test
	public void deleteUser_003() {
		when(messageSource.getMessage("COM01E005", null, Locale.JAPAN))
				.thenReturn("対象のデータは削除されています");

		RedirectAttributesModelMap attr = new RedirectAttributesModelMap();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("userId", "testUser");

		String view = target.deleteUser(
				Collections.singletonList(1), attr, session);

		assertEquals("redirect:/user/list", view);
		assertEquals("対象のデータは削除されています",
				attr.getFlashAttributes().get("message"));
		verify(userService).existsActiveUser(1);
		verify(userService, never()).deleteUser(anyInt());
	}

	/**
	 * deleteUser
	 * 他のユーザが編集中の場合、削除せず一覧へ戻ること。
	 */
	@Test
	public void deleteUser_004() {
		when(userService.existsActiveUser(1)).thenReturn(true);
		when(lockService.isLockedByOtherUser("m_user", 1, "testUser"))
				.thenReturn(true);
		when(lockService.getLockingUserId("m_user", 1, "testUser"))
				.thenReturn("otherUser");
		when(messageSource.getMessage(
				eq("COM01E006"), any(Object[].class), eq(Locale.JAPAN)))
						.thenReturn("他のユーザが編集中です");

		RedirectAttributesModelMap attr = new RedirectAttributesModelMap();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("userId", "testUser");

		String view = target.deleteUser(
				Collections.singletonList(1), attr, session);

		assertEquals("redirect:/user/list", view);
		assertEquals("他のユーザが編集中です",
				attr.getFlashAttributes().get("message"));
		verify(lockService).getLockingUserId("m_user", 1, "testUser");
		verify(userService, never()).deleteUser(anyInt());
	}

	/**
	 * deleteUser
	 * 複数の有効なユーザを選択した場合、それぞれ削除すること。
	 */
	@Test
	public void deleteUser_005() {
		when(userService.existsActiveUser(1)).thenReturn(true);
		when(userService.existsActiveUser(2)).thenReturn(true);

		RedirectAttributesModelMap attr = new RedirectAttributesModelMap();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("userId", "testUser");

		String view = target.deleteUser(Arrays.asList(1, 2), attr, session);

		assertEquals("redirect:/user/list", view);
		verify(lockService).isLockedByOtherUser("m_user", 1, "testUser");
		verify(lockService).isLockedByOtherUser("m_user", 2, "testUser");
		verify(userService).deleteUser(1);
		verify(userService).deleteUser(2);
	}
}
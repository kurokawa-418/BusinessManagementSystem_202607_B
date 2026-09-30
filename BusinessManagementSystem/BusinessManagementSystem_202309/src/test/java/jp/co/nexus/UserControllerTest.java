package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

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
}
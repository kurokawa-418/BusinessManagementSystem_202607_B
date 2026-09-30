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

import com.nexus.whc.repository.UserRepository;
import com.nexus.whc.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService target;

	/**
	 * searchList
	 * 条件なしの一覧取得をRepositoryに依頼し、その結果を返すこと。
	 */
	@Test
	public void searchList_001() {
		List<Map<String, Object>> expected = Collections.emptyList();
		when(userRepository.searchList("", "", "", "", 1, 20))
				.thenReturn(expected);

		List<Map<String, Object>> actual = target.searchList("", "", "", "", 1, 20);

		assertEquals(expected, actual);
		verify(userRepository).searchList("", "", "", "", 1, 20);
	}

	/**
	 * countUser
	 * 条件なしのユーザ件数をRepositoryから取得して返すこと。
	 */
	@Test
	public void countUser_001() {
		when(userRepository.countUser("", "", "", ""))
				.thenReturn(2);

		int actual = target.countUser("", "", "", "");

		assertEquals(2, actual);
		verify(userRepository).countUser("", "", "", "");
	}

	/**
	 * existsActiveUser
	 * Repositoryで有効なユーザが存在すると判定された場合、trueを返すこと。
	 */
	@Test
	public void existsActiveUser_001() {
		when(userRepository.existsActiveUser(1)).thenReturn(true);

		boolean actual = target.existsActiveUser(1);

		assertEquals(true, actual);
		verify(userRepository).existsActiveUser(1);
	}

	/**
	 * existsActiveUser
	 * Repositoryで有効なユーザが存在しないと判定された場合、falseを返すこと。
	 */
	@Test
	public void existsActiveUser_002() {
		when(userRepository.existsActiveUser(1)).thenReturn(false);

		boolean actual = target.existsActiveUser(1);

		assertEquals(false, actual);
		verify(userRepository).existsActiveUser(1);
	}

	/**
	 * deleteUser
	 * 指定した連番IDをRepositoryの削除処理に渡すこと。
	 */
	@Test
	public void deleteUser_001() {
		target.deleteUser(1);

		verify(userRepository).deleteUser(1);
	}
}
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
}
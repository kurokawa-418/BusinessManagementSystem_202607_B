package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.repository.UserRepository;

/* UserService.java
* 
* UserServiceクラス
*/
@Service
public class UserService {
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/* ユーザー一覧検索*/
	public List<Map<String, Object>> searchList(
			String userId,
			String userName,
			String authId,
			String mailAddress) {

		return userRepository.searchList(
				userId,
				userName,
				authId,
				mailAddress);
	}

	/*ユーザー削除*/
	public void deleteUser(int seqId) {
		userRepository.deleteUser(seqId);
	}
}

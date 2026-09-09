package com.nexus.whc.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.form.UserForm;
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

	/*ユーザー登録*/
	public int registUser(UserForm userForm) {
		// パスワードを自動生成
		String password = generatePassword();

		// UserFormにパスワードを設定
		userForm.setPassword(password);

		return userRepository.registUser(userForm);
	}

	/*パスワード自動生成メソッド*/
	private String generatePassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz"
				+ "0123456789";

		StringBuilder password = new StringBuilder();
		SecureRandom random = new SecureRandom();

		for (int i = 0; i < 20; i++) {
			int index = random.nextInt(chars.length());
			password.append(chars.charAt(index));
		}
		return password.toString();
	}

	/*更新するユーザ情報を取得*/
	public Map<String, Object> findUserBySeqId(int seqId) {
		return userRepository.findUserBySeqId(seqId);
	}

	/*ユーザ情報を更新*/
	public int updateUser(UserForm userForm) {
		return userRepository.updateUser(userForm);
	}

	/*ユーザー削除*/
	public void deleteUser(int seqId) {
		userRepository.deleteUser(seqId);
	}
}

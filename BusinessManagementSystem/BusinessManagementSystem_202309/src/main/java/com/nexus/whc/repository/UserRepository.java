package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/*
*UserRepository.java
*
**UserRepositoryクラス*/

/*
* Repositoryクラス
*/
public class UserRepository {
	/* JdbcTemplate */
	private final JdbcTemplate jdbcTemplate;

	/* UserRepositoryクラス */
	@Autowired
	public UserRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*ユーザ一覧検索*/
	/*一部の情報でも検索できるようにしてる*/
	public List<Map<String, Object>> searchList(
			String userId,
			String userName,
			String authId,
			String mailAddress) {
		StringBuilder sql = new StringBuilder("SELECT * "
				+ "FROM m_user "
				+ "WHERE delete_flg = 0");
		List<Object> param = new ArrayList<>();

		if (!userId.isEmpty()) {
			sql.append(" AND user_id LIKE ?");
			param.add("%" + userId + "%");
		}
		if (!userName.isEmpty()) {
			sql.append(" AND user_name LIKE ?");
			param.add("%" + userName + "%");
		}
		if (!authId.isEmpty()) {
			sql.append(" AND auth_id = ?");
			param.add(authId);
		}
		if (!mailAddress.isEmpty()) {
			sql.append(" AND mail_address LIKE ?");
			param.add("%" + mailAddress + "%");
		}
		/*SQLを実行して、複数行の検索結果を取得する*/
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), param.toArray());
		/*Serviceに返す*/
		return list;
	}
	
	/*一覧から削除*/
	public void deleteUser(int seqId) {

	    String sql = "DELETE FROM m_user WHERE seq_id = ?";
	    Object[] param = { seqId };

	    jdbcTemplate.update(sql, param);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

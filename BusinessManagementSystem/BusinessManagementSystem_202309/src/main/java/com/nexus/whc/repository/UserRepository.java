package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexus.whc.form.UserForm;

/*
*UserRepository.java
*
**UserRepositoryクラス*/

/*
* Repositoryクラス
*/
@Repository
public class UserRepository {
	/* JdbcTemplate */
	private final JdbcTemplate jdbcTemplate;

	/* UserRepositoryクラス */
	@Autowired
	public UserRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int getMaxSeqId() {

		String sql = "SELECT MAX(seq_id) FROM m_user";

		Integer maxSeqId = jdbcTemplate.queryForObject(sql, Integer.class);

		if (maxSeqId == null) {
			return 0;
		}

		return maxSeqId;
	}

	/*ユーザ一覧検索*/
	/*一部の情報でも検索可*/
	public List<Map<String, Object>> searchList(
			String userId,
			String userName,
			String authId,
			String mailAddress,
			int page,
			int pageSize) {
		StringBuilder sql = new StringBuilder("SELECT m_user.*, m_authority.auth_status "
				+ "FROM m_user "
				+ "LEFT JOIN m_authority "
				+ "ON m_user.auth_id = m_authority.auth_id "
				+ "WHERE m_user.delete_flg = 0");
		List<Object> param = new ArrayList<>();

		if (!userId.isEmpty()) {
			sql.append(" AND m_user.user_id LIKE ?");
			param.add("%" + userId + "%");
		}
		if (!userName.isEmpty()) {
			sql.append(" AND m_user.user_name LIKE ?");
			param.add("%" + userName + "%");
		}
		if (!authId.isEmpty()) {
			sql.append(" AND m_user.auth_id = ?");
			param.add(authId);
		}
		if (!mailAddress.isEmpty()) {
			sql.append(" AND m_user.mail_address LIKE ?");
			param.add("%" + mailAddress + "%");
		}

		int offset = (page - 1) * pageSize;

		sql.append(" ORDER BY m_user.seq_id");
		sql.append(" LIMIT ? OFFSET ?");

		param.add(pageSize);
		param.add(offset);

		/*SQLを実行して、複数行の検索結果を取得する*/
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), param.toArray());
		/*Serviceに返す*/
		return list;
	}

	/*ユーザー件数取得*/
	public int countUser(
			String userId,
			String userName,
			String authId,
			String mailAddress) {

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) "
				+ "FROM m_user "
				+ "WHERE m_user.delete_flg = 0");

		List<Object> param = new ArrayList<>();

		if (!userId.isEmpty()) {
			sql.append(" AND m_user.user_id LIKE ?");
			param.add("%" + userId + "%");
		}

		if (!userName.isEmpty()) {
			sql.append(" AND m_user.user_name LIKE ?");
			param.add("%" + userName + "%");
		}

		if (!authId.isEmpty()) {
			sql.append(" AND m_user.auth_id = ?");
			param.add(authId);
		}

		if (!mailAddress.isEmpty()) {
			sql.append(" AND m_user.mail_address LIKE ?");
			param.add("%" + mailAddress + "%");
		}

		return jdbcTemplate.queryForObject(
				sql.toString(),
				Integer.class,
				param.toArray());
	}

	/*登録*/
	public int registUser(UserForm userForm) {

		String sql = "INSERT INTO m_user "
				+ "(seq_id, user_id, user_name, auth_id, mail_address, password, delete_flg)"
				+ " VALUES(?,?,?,?,?,?,?)";

		Object[] param = {
				userForm.getSeqId(),
				userForm.getUserId(),
				userForm.getUserName(),
				userForm.getAuthId(),
				userForm.getMailAddress(),
				userForm.getPassword(),
				0 };

		return jdbcTemplate.update(sql, param);
	}

	/*更新するユーザ情報を取得*/
	public Map<String, Object> findUserBySeqId(int seqId) {

		String sql = "SELECT * "
				+ "FROM m_user "
				+ "WHERE seq_id = ? "
				+ "AND delete_flg = 0";

		Object[] param = { seqId };

		return jdbcTemplate.queryForMap(sql, param);
	}

	/*更新*/
	public int updateUser(UserForm userForm) {
		String sql = "UPDATE m_user SET "
				+ "user_name = ?, "
				+ "auth_id = ?, "
				+ "mail_address = ? "
				+ "WHERE seq_id = ?";
		Object[] param = {
				userForm.getUserName(),
				userForm.getAuthId(),
				userForm.getMailAddress(),
				userForm.getSeqId(),
		};
		return jdbcTemplate.update(sql, param);
	}

	/*一覧から削除*/
	public void deleteUser(int seqId) {

		String sql = "DELETE FROM m_user WHERE seq_id = ?";
		Object[] param = { seqId };

		jdbcTemplate.update(sql, param);
	}

	/*マスタ存在チェック*/
	public boolean existsUser(
			String userId,
			String userName,
			String mailAddress) {

		String sql = "SELECT COUNT(*) "
				+ "FROM m_user "
				+ "WHERE user_id = ? "
				+ "OR user_name = ? "
				+ "OR mail_address = ? ";

		Object[] param = {
				userId,
				userName,
				mailAddress
		};

		int count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				param);

		return count > 0;
	}

}

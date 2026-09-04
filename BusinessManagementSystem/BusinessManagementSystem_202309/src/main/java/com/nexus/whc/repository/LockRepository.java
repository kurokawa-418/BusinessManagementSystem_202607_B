package com.nexus.whc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LockRepository {

	private final JdbcTemplate jdbcTemplate;

	public LockRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isLocked(String tableName, Integer recordId) {

		String sql = "SELECT COUNT(*) "
				+ "FROM s_lock "
				+ "WHERE locking_table_name = ? "
				+ "AND locking_record_id = ?";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				tableName,
				recordId);

		return count != null && count > 0;
	}

	public int insertLock(
			String tableName,
			Integer recordId,
			String userId) {

		String sql = "INSERT INTO s_lock "
				+ "(locking_table_name, locking_record_id, locking_user_id) "
				+ "VALUES (?, ?, ?)";

		return jdbcTemplate.update(sql, tableName, recordId, userId);
	}

	public int deleteLock(
			String tableName,
			Integer recordId,
			String userId) {

		String sql = "DELETE FROM s_lock "
				+ "WHERE locking_table_name = ? "
				+ "AND locking_record_id = ? "
				+ "AND locking_user_id = ?";

		return jdbcTemplate.update(sql, tableName, recordId, userId);
	}

	public boolean isLockedByOtherUser(
			String tableName,
			Integer recordId,
			String userId) {

		String sql = "SELECT COUNT(*) "
				+ "FROM s_lock "
				+ "WHERE locking_table_name = ? "
				+ "AND locking_record_id = ? "
				+ "AND locking_user_id <> ?";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				tableName,
				recordId,
				userId);

		return count != null && count > 0;
	}
}
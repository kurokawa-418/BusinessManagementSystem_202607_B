package com.nexus.whc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/*
 * TopRepository.java
 *
 * TopRepositoryクラス
 */

/*
 * Repositoryクラス
 */
@Repository
public class TopRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public TopRepository(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;

	}

	/*
	 * ファイル・リンク一覧取得
	 */
	public List<Map<String, Object>> searchFileLink() {

		String sql = "SELECT "
				+ "seq_id, "
				+ "publication_date, "
				+ "title, "
				+ "content "
				+ "FROM s_filelink "
				+ "WHERE delete_flg = 0 "
				+ "ORDER BY publication_date ASC";

		return jdbcTemplate.queryForList(sql);
	}

}
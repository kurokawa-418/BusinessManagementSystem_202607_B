package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexus.whc.form.ClientForm;

@Repository
public class ClientRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ClientRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> findAllClient() {

		String sql = "SELECT * "
				+ "FROM m_client "
				+ "WHERE delete_flg = 0 "
				+ "ORDER BY client_id";

		return jdbcTemplate.queryForList(sql);
	}

	public int insertClient(ClientForm clientForm) {

		String sql = "INSERT INTO m_client ("
				+ "client_id, "
				+ "client_name, "
				+ "open_time, "
				+ "close_time, "
				+ "working_time, "
				+ "rest1_start, rest1_end, "
				+ "rest2_start, rest2_end, "
				+ "rest3_start, rest3_end, "
				+ "rest4_start, rest4_end, "
				+ "rest5_start, rest5_end, "
				+ "rest6_start, rest6_end, "
				+ "adjust_rest_time_start, "
				+ "adjust_rest_time_end, "
				+ "comment, "
				+ "delete_flg"
				+ ") VALUES ("
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0"
				+ ")";

		Object[] param = {
				clientForm.getClientId(),
				clientForm.getClientName(),
				clientForm.getOpenTime(),
				clientForm.getCloseTime(),
				clientForm.getWorkingTime(),
				clientForm.getRest1Start(),
				clientForm.getRest1End(),
				clientForm.getRest2Start(),
				clientForm.getRest2End(),
				clientForm.getRest3Start(),
				clientForm.getRest3End(),
				clientForm.getRest4Start(),
				clientForm.getRest4End(),
				clientForm.getRest5Start(),
				clientForm.getRest5End(),
				clientForm.getRest6Start(),
				clientForm.getRest6End(),
				clientForm.getAdjustRestTimeStart(),
				clientForm.getAdjustRestTimeEnd(),
				clientForm.getComment()
		};

		return jdbcTemplate.update(sql, param);
	}

	public Map<String, Object> findClientById(Integer clientId) {

		String sql = "SELECT * "
				+ "FROM m_client "
				+ "WHERE client_id = ? "
				+ "AND delete_flg = 0";

		return jdbcTemplate.queryForMap(sql, clientId);
	}

	public int updateClient(ClientForm clientForm) {

		String sql = "UPDATE m_client SET "
				+ "client_name = ?, "
				+ "open_time = ?, "
				+ "close_time = ?, "
				+ "working_time = ?, "
				+ "rest1_start = ?, rest1_end = ?, "
				+ "rest2_start = ?, rest2_end = ?, "
				+ "rest3_start = ?, rest3_end = ?, "
				+ "rest4_start = ?, rest4_end = ?, "
				+ "rest5_start = ?, rest5_end = ?, "
				+ "rest6_start = ?, rest6_end = ?, "
				+ "adjust_rest_time_start = ?, "
				+ "adjust_rest_time_end = ?, "
				+ "comment = ? "
				+ "WHERE client_id = ? "
				+ "AND delete_flg = 0";

		Object[] param = {
				clientForm.getClientName(),
				clientForm.getOpenTime(),
				clientForm.getCloseTime(),
				clientForm.getWorkingTime(),
				clientForm.getRest1Start(),
				clientForm.getRest1End(),
				clientForm.getRest2Start(),
				clientForm.getRest2End(),
				clientForm.getRest3Start(),
				clientForm.getRest3End(),
				clientForm.getRest4Start(),
				clientForm.getRest4End(),
				clientForm.getRest5Start(),
				clientForm.getRest5End(),
				clientForm.getRest6Start(),
				clientForm.getRest6End(),
				clientForm.getAdjustRestTimeStart(),
				clientForm.getAdjustRestTimeEnd(),
				clientForm.getComment(),
				clientForm.getClientId()
		};

		return jdbcTemplate.update(sql, param);
	}

	public int deleteClients(List<Integer> clientIds) {

		String placeholder = String.join(
				",", Collections.nCopies(clientIds.size(), "?"));

		String sql = "UPDATE m_client "
				+ "SET delete_flg = 1 "
				+ "WHERE client_id IN (" + placeholder + ")";

		return jdbcTemplate.update(sql, clientIds.toArray());
	}

	public List<Map<String, Object>> searchClients(
			String clientId, String clientName, int page) {

		StringBuilder sql = new StringBuilder(
				"SELECT * FROM m_client WHERE delete_flg = 0 ");

		List<Object> param = new ArrayList<>();

		if (clientId != null && !clientId.isEmpty()) {
			sql.append("AND client_id = ? ");
			param.add(clientId);
		}

		if (clientName != null && !clientName.isEmpty()) {
			sql.append("AND client_name LIKE ? ");
			param.add("%" + clientName + "%");
		}

		sql.append("ORDER BY client_id LIMIT 20 OFFSET ?");
		param.add((page - 1) * 20);

		return jdbcTemplate.queryForList(sql.toString(), param.toArray());
	}

	public int countClients(String clientId, String clientName) {

		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM m_client WHERE delete_flg = 0 ");

		List<Object> param = new ArrayList<>();

		if (clientId != null && !clientId.isEmpty()) {
			sql.append("AND client_id = ? ");
			param.add(clientId);
		}

		if (clientName != null && !clientName.isEmpty()) {
			sql.append("AND client_name LIKE ? ");
			param.add("%" + clientName + "%");
		}

		return jdbcTemplate.queryForObject(
				sql.toString(), param.toArray(), Integer.class);
	}

	public boolean existsClient(Integer clientId, String clientName) {

		String sql = "SELECT COUNT(*) FROM m_client "
				+ "WHERE client_id = ? OR client_name = ?";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				clientId,
				clientName);

		return count != null && count > 0;
	}

	public boolean existsActiveClient(Integer clientId) {

		String sql = "SELECT COUNT(*) "
				+ "FROM m_client "
				+ "WHERE client_id = ? "
				+ "AND delete_flg = 0";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				clientId);

		return count != null && count > 0;
	}
}
package com.nexus.whc.repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexus.whc.form.EmployeeForm;

/*
 * EmployeeRepository.java
 * 
 * EmployeeRepositoryクラス
 */

/*
 * Repositoryクラス
 */
@Repository
public class EmployeeRepository {

	/* JdbcTemplate */
	private final JdbcTemplate jdbcTemplate;

	/* CalendarRepositoryクラス */
	@Autowired
	public EmployeeRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 顧客選択ダイアログ
	 */
	public List<Map<String, Object>> getClient() {

		// SQL文作成
		String sql = "SELECT client_id,client_name "
				+ "FROM m_client "
				+ "WHERE delete_flg = 0";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return list;
	}

	public int registEmployee(EmployeeForm employeeForm) {

		String sql = "INSERT INTO m_employee ("
				+ "employee_id, "
				+ "employee_name, "
				+ "client_id, "
				+ "hourly_wage, "
				+ "paid_holiday_std, "
				+ "delete_flg, "
				+ "created_at, "
				+ "created_user, "
				+ "updated_at, "
				+ "updated_user"
				+ ") "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.update(
				sql,
				Integer.valueOf(employeeForm.getEmployeeId()),
				employeeForm.getEmployeeName(),
				Integer.valueOf(employeeForm.getClientId()),
				employeeForm.getHourlyWage(),
				Date.valueOf(employeeForm.getPaidHolidayStd()),
				Integer.valueOf(employeeForm.getDeleteFlg()),
				LocalDateTime.now(),
				employeeForm.getCreatedUser(),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser());
	}

	public List<Map<String, Object>> searchEmployeeList() {

		String sql = "SELECT "
				+ "employee_id, "
				+ "employee_name, "
				+ "client_id, "
				+ "client_name, "
				+ "hourly_wage, "
				+ "paid_holiday_std, "
				+ "delete_flg, "
				+ "created_at, "
				+ "created_user, "
				+ "updated_at, "
				+ "updated_user "
				+ "FROM m_employee "
				+ "WHERE delete_flg = 0 "
				+ "ORDER BY employee_id";

		return jdbcTemplate.queryForList(sql);
	}

	public Map<String, Object> searchEmployee(String employeeId) {

		String sql = "SELECT "
				+ "employee_id, "
				+ "employee_name, "
				+ "client_id, "
				+ "client_name, "
				+ "hourly_wage, "
				+ "paid_holiday_std, "
				+ "delete_flg, "
				+ "created_at, "
				+ "created_user, "
				+ "updated_at, "
				+ "updated_user "
				+ "FROM m_employee "
				+ "WHERE employee_id = ? "
				+ "AND delete_flg = 0";

		return jdbcTemplate.queryForMap(
				sql,
				Integer.valueOf(employeeId));
	}

	public int updateEmployee(EmployeeForm employeeForm) {

		String sql = "UPDATE m_employee SET "
				+ "employee_name = ?, "
				+ "client_id = ?, "
				+ "client_name = ?, "
				+ "hourly_wage = ?, "
				+ "paid_holiday_std = ?, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ?";

		return jdbcTemplate.update(
				sql,
				employeeForm.getEmployeeName(),
				Integer.valueOf(employeeForm.getClientId()),
				employeeForm.getClientName(),
				employeeForm.getHourlyWage(),
				Date.valueOf(employeeForm.getPaidHolidayStd()),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId()));
	}

	public int deleteEmployee(String employeeId, String updatedUser) {

		String sql = "UPDATE m_employee SET "
				+ "delete_flg = 1, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ?";

		return jdbcTemplate.update(
				sql,
				LocalDateTime.now(),
				updatedUser,
				Integer.valueOf(employeeId));
	}
}

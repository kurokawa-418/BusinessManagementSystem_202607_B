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

		Object[] param = {
				Integer.valueOf(employeeForm.getEmployeeId()),
				employeeForm.getEmployeeName(),
				Integer.valueOf(employeeForm.getClientId()),
				employeeForm.getHourlyWage(),
				Date.valueOf(employeeForm.getPaidHolidayStd()),
				Integer.valueOf(employeeForm.getDeleteFlg()),
				LocalDateTime.now(),
				employeeForm.getCreatedUser(),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser() };

		return jdbcTemplate.update(sql, param);
	}

	public int registPaidVacation(String employeeId) {

		String sql = "INSERT INTO m_employee_paid_vacation ("
				+ "employee_id"
				+ ") "
				+ "VALUES (?)";

		return jdbcTemplate.update(
				sql,
				Integer.valueOf(employeeId));
	}

	public List<Map<String, Object>> checkEmployeeDuplicate(
			EmployeeForm employeeForm) {

		String sql = "SELECT "
				+ "employee_id "
				+ "FROM m_employee "
				+ "WHERE employee_id = ? "
				+ "AND employee_name = ? "
				+ "AND delete_flg = 0";

		Object[] param = {
				Integer.valueOf(employeeForm.getEmployeeId()),
				employeeForm.getEmployeeName() };

		return jdbcTemplate.queryForList(
				sql, param);
	}

	public Map<String, Object> searchEmployeeById(String employeeId) {

		String sql = "SELECT "
				+ "m_employee.employee_id, "
				+ "m_employee.employee_name, "
				+ "m_employee.client_id, "
				+ "m_client.client_name, "
				+ "m_employee.hourly_wage, "
				+ "m_employee.paid_holiday_std, "
				+ "m_employee_paid_vacation.remaind_this_year, "
				+ "m_employee_paid_vacation.remaind_last_year "
				+ "FROM m_employee "
				+ "LEFT JOIN m_client "
				+ "ON m_employee.client_id = m_client.client_id "
				+ "LEFT JOIN m_employee_paid_vacation "
				+ "ON m_employee.employee_id = m_employee_paid_vacation.employee_id "
				+ "WHERE m_employee.employee_id = ? "
				+ "AND m_employee.delete_flg = 0";

		Object[] param = { Integer.valueOf(employeeId) };

		return jdbcTemplate.queryForMap(sql, param);
	}

	public List<Map<String, Object>> searchEmployeeList() {

		String sql = "SELECT "
				+ "m_employee.employee_id, "
				+ "m_employee.employee_name, "
				+ "m_employee.client_id, "
				+ "m_client.client_name, "
				+ "m_employee.hourly_wage, "
				+ "m_employee.paid_holiday_std, "
				+ "m_employee_paid_vacation.remaind_this_year, "
				+ "m_employee_paid_vacation.remaind_last_year "
				+ "FROM m_employee "
				+ "LEFT JOIN m_client "
				+ "ON m_employee.client_id = m_client.client_id "
				+ "LEFT JOIN m_employee_paid_vacation "
				+ "ON m_employee.employee_id = m_employee_paid_vacation.employee_id "
				+ "AND m_employee.delete_flg = 0";

		return jdbcTemplate.queryForList(sql);
	}

	public int updateEmployee(EmployeeForm employeeForm) {

		String sql = "UPDATE m_employee SET "
				+ "client_id = ?, "
				+ "hourly_wage = ?, "
				+ "paid_holiday_std = ?, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ?"
				+ "AND delete_flg = false";

		Object[] param = {
				employeeForm.getEmployeeName(),
				Integer.valueOf(employeeForm.getClientId()),
				employeeForm.getHourlyWage(),
				Date.valueOf(employeeForm.getPaidHolidayStd()),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId()) };
		return jdbcTemplate.update(sql, param);
	}

	public int deleteEmployee(String employeeId, String updatedUser) {

		String sql = "UPDATE m_employee SET "
				+ "delete_flg = 1, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ?"
				+ "AND delete_flg = false";

		Object[] param = {
				LocalDateTime.now(),
				updatedUser,
				Integer.valueOf(employeeId) };
		return jdbcTemplate.update(sql, param);
	}
}

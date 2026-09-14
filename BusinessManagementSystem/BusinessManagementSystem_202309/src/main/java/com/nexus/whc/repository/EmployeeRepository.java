package com.nexus.whc.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
				Integer.valueOf(employeeForm.getHourlyWage()),
				Date.valueOf(employeeForm.getPaidHolidayStd().replace("/", "-")),
				Integer.valueOf(employeeForm.getDeleteFlg()),
				LocalDateTime.now(),
				employeeForm.getCreatedUser(),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser()
		};

		return jdbcTemplate.update(sql, param);
	}

	public int registPaidVacation(EmployeeForm employeeForm) {

		String sql = "INSERT INTO m_employee_paid_vacation ("
				+ "employee_id, "
				+ "year, "
				+ "remaind_this_year, "
				+ "remaind_last_year, "
				+ "delete_flg, "
				+ "created_at, "
				+ "created_user, "
				+ "updated_at, "
				+ "updated_user"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Object[] param = {
				Integer.valueOf(employeeForm.getEmployeeId()),
				LocalDate.of(LocalDate.now().getYear(), 4, 1),
				Double.valueOf(employeeForm.getRemaindThisYear()),
				Double.valueOf(employeeForm.getRemaindLastYear()),
				0,
				new java.sql.Timestamp(System.currentTimeMillis()),
				employeeForm.getCreatedUser(),
				new java.sql.Timestamp(System.currentTimeMillis()),
				employeeForm.getUpdatedUser()
		};
		return jdbcTemplate.update(sql, param);
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

	public List<Map<String, Object>> searchEmployee(
			String employeeId,
			String employeeName,
			String clientId,
			String clientName) {

		String sql = "SELECT "
				+ "m_employee.employee_id, "
				+ "m_employee.employee_name, "
				+ "m_employee.client_id, "
				+ "m_client.client_name, "
				+ "m_employee.hourly_wage, "
				+ "m_employee.paid_holiday_std, "
				+ "m_employee_paid_vacation.remaind_this_year, "
				+ "m_employee_paid_vacation.remaind_last_year, "
				+ "t_work_leave_application.holiday_date, "
				+ "t_work_leave_application.application_class "
				+ "FROM m_employee "
				+ "LEFT JOIN m_client "
				+ "ON m_employee.client_id = m_client.client_id "
				+ "LEFT JOIN m_employee_paid_vacation "
				+ "ON m_employee.employee_id = m_employee_paid_vacation.employee_id "
				+ "AND m_employee_paid_vacation.year = '2026-04-01' "
				+ "LEFT JOIN t_work_leave_application "
				+ "ON m_employee.employee_id = t_work_leave_application.employee_id "
				+ "AND t_work_leave_application.holiday_date "
				+ "BETWEEN DATE_SUB(m_employee.paid_holiday_std, INTERVAL 1 YEAR) "
				+ "AND DATE_ADD(m_employee.paid_holiday_std, INTERVAL 1 YEAR) "
				+ "WHERE m_employee.delete_flg = 0 ";
		List<Object> params = new ArrayList<>();

		if (employeeId != null && !employeeId.isEmpty()) {
			sql += "AND m_employee.employee_id = ? ";
			params.add(Integer.valueOf(employeeId));
		}

		if (employeeName != null && !employeeName.isEmpty()) {
			sql += "AND m_employee.employee_name LIKE ? ";
			params.add("%" + employeeName + "%");
		}

		if (clientId != null && !clientId.isEmpty()) {
			sql += "AND m_employee.client_id = ? ";
			params.add(Integer.valueOf(clientId));
		}

		if (clientName != null && !clientName.isEmpty()) {
			sql += "AND m_client.client_name LIKE ? ";
			params.add("%" + clientName + "%");
		}

		sql += "ORDER BY m_employee.employee_id ASC";

		return jdbcTemplate.queryForList(sql, params.toArray());
	}

	public Map<String, Object> searchEmployeeById(String employeeId) {

		String sql = "SELECT "
				+ "m_employee.employee_id, "
				+ "m_employee.employee_name, "
				+ "m_employee.client_id, "
				+ "m_client.client_name, "
				+ "CAST(m_employee.hourly_wage AS UNSIGNED) AS hourly_wage, "
				+ "m_employee.paid_holiday_std, "
				+ "m_employee_paid_vacation.remaind_this_year, "
				+ "m_employee_paid_vacation.remaind_last_year "
				+ "FROM m_employee "
				+ "LEFT JOIN m_client "
				+ "ON m_employee.client_id = m_client.client_id "
				+ "LEFT JOIN m_employee_paid_vacation "
				+ "ON m_employee.employee_id = m_employee_paid_vacation.employee_id "
				+ "WHERE m_employee.employee_id = ? "
				+ "AND m_employee.delete_flg = 0 "
				+ "ORDER BY m_employee.employee_id ASC";

		Object[] param = { Integer.valueOf(employeeId) };

		return jdbcTemplate.queryForMap(sql, param);
	}

	public boolean existsEmployee(String employeeId) {

		String sql = "SELECT COUNT(*) "
				+ "FROM m_employee "
				+ "WHERE employee_id = ? "
				+ "AND delete_flg = 0";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				Integer.valueOf(employeeId));

		return count != null && count > 0;
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
				+ "WHERE m_employee.delete_flg = 0 "
				+ "ORDER BY m_employee.employee_id ASC";

		return jdbcTemplate.queryForList(sql);
	}

	public boolean existsClient(String clientId) {

		String sql = "SELECT COUNT(*) "
				+ "FROM m_client "
				+ "WHERE client_id = ? "
				+ "AND delete_flg = 0";

		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, clientId);

		return count != null && count > 0;
	}

	public Map<String, Object> searchClientById(String clientId) {

		String sql = "SELECT client_id AS clientId, "
				+ "client_name AS clientName "
				+ "FROM m_client "
				+ "WHERE client_id = ? "
				+ "AND delete_flg = 0";

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, clientId);

		if (result.isEmpty()) {
			return new HashMap<String, Object>();
		}

		return result.get(0);
	}

	public Map<String, Object> searchClientByName(String clientName) {

		String sql = "SELECT client_id AS clientId, "
				+ "client_name AS clientName "
				+ "FROM m_client "
				+ "WHERE client_name = ? "
				+ "AND delete_flg = 0";

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, clientName);

		if (result.isEmpty()) {
			return new HashMap<String, Object>();
		}

		return result.get(0);
	}

	public int updateEmployee(EmployeeForm employeeForm) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate date = LocalDate.parse(employeeForm.getPaidHolidayStd(), formatter);

		String sql = "UPDATE m_employee SET "
				+ "client_id = ?, "
				+ "hourly_wage = ?, "
				+ "paid_holiday_std = ?, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ? "
				+ "AND delete_flg = false";

		boolean hourlyWage = "1".equals(employeeForm.getHourlyWage());

		Object[] param = {
				Integer.valueOf(employeeForm.getClientId()),
				hourlyWage,
				java.sql.Date.valueOf(date),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId()) };
		return jdbcTemplate.update(sql, param);
	}

	public int updatePaidVacation(EmployeeForm employeeForm) {

		String sql = "UPDATE m_employee_paid_vacation SET "
				+ "remaind_this_year = ?, "
				+ "remaind_last_year = ?, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ? "
				+ "AND delete_flg = 0";

		Object[] param = {
				employeeForm.getRemaindThisYear(),
				employeeForm.getRemaindLastYear(),
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId())
		};
		return jdbcTemplate.update(sql, param);
	}

	public int deleteEmployee(EmployeeForm employeeForm) {

		String sql = "UPDATE m_employee SET "
				+ "delete_flg = 1, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ? "
				+ "AND delete_flg = 0";

		Object[] param = {
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId())
		};

		return jdbcTemplate.update(sql, param);
	}

	public int deletePaidVacation(EmployeeForm employeeForm) {

		String sql = "UPDATE m_employee_paid_vacation SET "
				+ "delete_flg = 1, "
				+ "updated_at = ?, "
				+ "updated_user = ? "
				+ "WHERE employee_id = ? "
				+ "AND year = ? "
				+ "AND delete_flg = 0";

		Object[] param = {
				LocalDateTime.now(),
				employeeForm.getUpdatedUser(),
				Integer.valueOf(employeeForm.getEmployeeId()),
				Date.valueOf("2026-04-01")
		};

		return jdbcTemplate.update(sql, param);
	}
}

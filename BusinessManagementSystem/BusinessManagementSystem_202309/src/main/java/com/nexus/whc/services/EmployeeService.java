package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.form.EmployeeForm;
import com.nexus.whc.repository.EmployeeRepository;

/*
 * Serviceクラス
 */
@Service
public class EmployeeService {
	/* EmployeeRepositoryクラス */
	private final EmployeeRepository employeeRepository;
	private final LockService lockService;

	/* EmployeeServiceクラス */
	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository,
			LockService lockService) {
		this.employeeRepository = employeeRepository;
		this.lockService = lockService;
	}

	/**
	 * 顧客選択ダイアログ
	 */
	public List<Map<String, Object>> getClient() {

		List<Map<String, Object>> list = employeeRepository.getClient();

		return list;
	}

	public int registEmployee(EmployeeForm employeeForm) {
		return employeeRepository.registEmployee(employeeForm);
	}

	public int registPaidVacation(EmployeeForm employeeForm) {
		return employeeRepository.registPaidVacation(employeeForm);
	}

	public boolean checkEmployeeDuplicate(EmployeeForm employeeForm) {

		List<Map<String, Object>> result = employeeRepository.checkEmployeeDuplicate(employeeForm);

		return !result.isEmpty();
	}

	public List<Map<String, Object>> searchEmployee(
			String employeeId,
			String employeeName,
			String clientId,
			String clientName) {
		return employeeRepository.searchEmployee(employeeId,
				employeeName,
				clientId,
				clientName);
	}

	public Map<String, Object> searchEmployeeById(String employeeId) {
		return employeeRepository.searchEmployeeById(employeeId);
	}

	public boolean isEmployeeLocked(
			String employeeId,
			String userId) {

		return lockService.isLocked(
				"m_employee",
				Integer.valueOf(employeeId));
	}

	public List<Map<String, Object>> searchEmployeeList() {
		return employeeRepository.searchEmployeeList();
	}

	public boolean existsClient(String clientId) {
		return employeeRepository.existsClient(clientId);
	}

	public Map<String, Object> searchClientById(String clientId) {
		return employeeRepository.searchClientById(clientId);
	}

	public Map<String, Object> searchClientByName(String clientName) {
		return employeeRepository.searchClientByName(clientName);
	}

	// 更新
	public int updateEmployee(EmployeeForm employeeForm) {
		return employeeRepository.updateEmployee(employeeForm);
	}

	public int updatePaidVacation(EmployeeForm employeeForm) {
		return employeeRepository.updatePaidVacation(employeeForm);
	}

	// 削除
	public int deleteEmployee(EmployeeForm employeeForm) {
		return employeeRepository.deleteEmployee(employeeForm);
	}

	public int deletePaidVacation(EmployeeForm employeeForm) {
		return employeeRepository.deletePaidVacation(employeeForm);
	}
	
	public boolean existsEmployee(String employeeId) {
		return employeeRepository.existsEmployee(employeeId);
	}

	public boolean isEmployeeLockedByOtherUser(
			String employeeId,
			String userId) {

		return lockService.isLockedByOtherUser(
				"m_employee",
				Integer.valueOf(employeeId),
				userId);
	}

	public int lockEmployee(
			String employeeId,
			String userId) {

		return lockService.insertLock(
				"m_employee",
				Integer.valueOf(employeeId),
				userId);
	}

	public int unlockEmployee(
			String employeeId,
			String userId) {

		return lockService.deleteLock(
				"m_employee",
				Integer.valueOf(employeeId),
				userId);
	}
}

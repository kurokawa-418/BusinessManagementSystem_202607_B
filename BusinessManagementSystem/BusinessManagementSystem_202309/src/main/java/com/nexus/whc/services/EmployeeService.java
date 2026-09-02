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

	/* EmployeeServiceクラス */
	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
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

	public int registPaidVacation(String employeeId) {
		return employeeRepository.registPaidVacation(employeeId);
	}

	public List<Map<String, Object>> checkEmployeeDuplicate(EmployeeForm employeeForm) {
		return employeeRepository.checkEmployeeDuplicate(employeeForm);
	}

	public Map<String, Object> searchEmployeeById(String employeeId) {
		return employeeRepository.searchEmployeeById(employeeId);
	}
	public List<Map<String, Object>> searchEmployeeList() {
		return employeeRepository.searchEmployeeList();
	}

	// 更新
	public int updateEmployee(EmployeeForm employeeForm) {
		return employeeRepository.updateEmployee(employeeForm);
	}

	// 削除
	public int deleteEmployee(String employeeId, String updatedUser) {
		return employeeRepository.deleteEmployee(employeeId, updatedUser);
	}
}

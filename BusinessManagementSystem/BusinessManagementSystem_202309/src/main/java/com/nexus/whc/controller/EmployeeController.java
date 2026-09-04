package com.nexus.whc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexus.whc.form.EmployeeForm;
import com.nexus.whc.services.EmployeeService;

/*
 * EmployeeController.java
 * 
 * EmployeeControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	/* EmployeeServiceクラス*/
	@Autowired
	EmployeeService employeeService;

	@GetMapping("/list")
	public String clientList(Model model) {

		//顧客選択ダイアログ用
		List<Map<String, Object>> clientList = employeeService.getClient();
		model.addAttribute("client_list", clientList);

		List<Map<String, Object>> employeeList = employeeService.searchEmployeeList();
		model.addAttribute("employeeList", employeeList);

		return "SMSEM001";
	}

	//社員検索メソッド
	@PostMapping("/search")
	public String searchEmployee(
			@RequestParam(name = "employeeId", defaultValue = "") String employeeId,
			@RequestParam(name = "employeeName", defaultValue = "") String employeeName,
			@RequestParam(name = "clientId", defaultValue = "") String clientId,
			@RequestParam(name = "clientName", defaultValue = "") String clientName,
			Model model) {

		// 顧客選択ダイアログ用
		List<Map<String, Object>> clientList = employeeService.getClient();
		model.addAttribute("client_list", clientList);

		// 社員検索
		List<Map<String, Object>> employeeList = employeeService.searchEmployee(
				employeeId,
				employeeName,
				clientId,
				clientName);

		model.addAttribute("employeeList", employeeList);

		return "SMSEM001";
	}

	// 社員マスタ登録画面
	@GetMapping("/input")
	public String employeeInput(Model model) {

		EmployeeForm employeeForm = new EmployeeForm();

		employeeForm.setDeleteFlg("0");

		model.addAttribute("employeeForm", employeeForm);

		List<Map<String, Object>> clientList = employeeService.getClient();

		model.addAttribute("client_list", clientList);

		return "SMSEM002";
	}

	//社員マスタ登録処理
	@PostMapping("/regist")
	public String registEmployee(
			EmployeeForm employeeForm) {

		employeeForm.setDeleteFlg("0");

		int result = employeeService.registEmployee(employeeForm);

		if (result > 0) {

			employeeService.registPaidVacation(employeeForm);
		}

		return "redirect:/employee/list";
	}

	//社員マスタ閲覧画面
	@GetMapping("/detail")
	public String employeeDetail(
			@RequestParam("employeeId") String employeeId,
			Model model) {

		Map<String, Object> employee = employeeService.searchEmployeeById(employeeId);

		model.addAttribute("employee", employee);

		// 顧客選択ダイアログ用
		List<Map<String, Object>> clientList = employeeService.getClient();

		model.addAttribute("client_list", clientList);

		return "SMSEM002";
	}

	//社員マスタ更新処理
	@PostMapping("/update")
	public String updateEmployee(
			EmployeeForm employeeForm) {

		employeeService.updateEmployee(employeeForm);
		employeeService.updatePaidVacation(employeeForm);

		return "redirect:/employee/list";
	}

	//社員マスタ削除処理
	@PostMapping("/delete")
	public String deleteEmployee(
			@RequestParam("employeeId") String employeeId,
			@RequestParam("updatedUser") String updatedUser) {

		employeeService.deleteEmployee(
				employeeId,
				updatedUser);
		employeeService.deletePaidVacation(
				employeeId,
				updatedUser);

		return "redirect:/employee/list";
	}
}

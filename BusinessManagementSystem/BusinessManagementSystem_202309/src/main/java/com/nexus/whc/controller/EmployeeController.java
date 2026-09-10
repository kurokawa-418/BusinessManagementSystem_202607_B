package com.nexus.whc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

	private void checkRequired(
			String employeeId,
			String employeeName,
			String paidHolidayStd,
			String remaindThisYear,
			String remaindLastYear,
			BindingResult bindingResult) {

		if (employeeId == null || employeeId.trim().isEmpty()) {
			bindingResult.rejectValue(
					"employeeId",
					"COM01E001",
					new Object[] { null, "社員番号" },
					null);
		}

		if (employeeName == null || employeeName.trim().isEmpty()) {
			bindingResult.rejectValue(
					"employeeName",
					"COM01E001",
					new Object[] { null, "社員氏名" },
					null);
		}

		if (paidHolidayStd == null || paidHolidayStd.trim().isEmpty()) {
			bindingResult.rejectValue(
					"paidHolidayStd",
					"COM01E001",
					new Object[] { null, "有休基準日" },
					null);
		}

		if (remaindThisYear == null || remaindThisYear.trim().isEmpty()) {
			bindingResult.rejectValue(
					"remaindThisYear",
					"COM01E001",
					new Object[] { null, "有休残日数-当年度分" },
					null);
		}

		if (remaindLastYear == null || remaindLastYear.trim().isEmpty()) {
			bindingResult.rejectValue(
					"remaindLastYear",
					"COM01E001",
					new Object[] { null, "有休残日数-前年度分" },
					null);
		}
	}

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

		// 検索結果が0件の場合
		if (employeeList.isEmpty()) {
			model.addAttribute(
					"message",
					"社員一覧の検索結果は0件です。条件を変更し、再度検索してください。");
		}

		return "SMSEM001";
	}

	// 社員マスタ登録画面
	@GetMapping("/input")
	public String employeeInput(Model model, HttpSession session) {

		// 以前のフォーム情報を削除
		session.removeAttribute("employeeForm");

		// 新しい空のフォームを作成
		EmployeeForm employeeForm = new EmployeeForm();

		employeeForm.setDeleteFlg("0");

		model.addAttribute("employeeForm", employeeForm);

		List<Map<String, Object>> clientList = employeeService.getClient();

		model.addAttribute("client_list", clientList);

		session.setAttribute("employeeMode", "new");

		return "SMSEM002";
	}

	//社員マスタ登録処理
	@PostMapping("/regist")
	public String registEmployee(
	        @ModelAttribute EmployeeForm employeeForm,
	        BindingResult bindingResult,
	        HttpSession session) {

	    // 必須チェック
	    checkRequired(
	            employeeForm.getEmployeeId(),
	            employeeForm.getEmployeeName(),
	            employeeForm.getPaidHolidayStd(),
	            employeeForm.getRemaindThisYear(),
	            employeeForm.getRemaindLastYear(),
	            bindingResult);

	    // 必須エラーがなければチェック
	    if (!bindingResult.hasErrors()) {

	        // 顧客番号存在チェック
	        boolean clientExists =
	                employeeService.existsClient(employeeForm.getClientId());

	        if (!clientExists) {
	            bindingResult.rejectValue(
	                    "clientId",
	                    "",
	                    "存在しない顧客情報です");
	        }
	    }

	    // 顧客番号エラーがなければ重複チェック
	    if (!bindingResult.hasErrors()) {
	        boolean duplicate =
	                employeeService.checkEmployeeDuplicate(employeeForm);

	        if (duplicate) {
	            bindingResult.rejectValue(
	                    "employeeId",
	                    "COM01E011",
	                    new Object[] {
	                            null,
	                            "社員番号",
	                            employeeForm.getEmployeeId(),
	                            "社員マスタ"
	                    },
	                    null);
	        }
	    }

	    // エラーがあれば登録しない
	    if (bindingResult.hasErrors()) {
	        return "SMSEM002";
	    }

	    employeeForm.setDeleteFlg("0");

	    if (employeeForm.getHourlyWage() == null) {
	        employeeForm.setHourlyWage("0");
	    }

	    // DB登録
	    int result = employeeService.registEmployee(employeeForm);

	    if (result > 0) {
	        employeeService.registPaidVacation(employeeForm);
	    }

	    return "redirect:/employee/list";
	}

	@PostMapping("/registNext")
	public String registNext(
	        @ModelAttribute EmployeeForm employeeForm,
	        BindingResult bindingResult,
	        HttpSession session) {

	    // 必須チェック
	    checkRequired(
	            employeeForm.getEmployeeId(),
	            employeeForm.getEmployeeName(),
	            employeeForm.getPaidHolidayStd(),
	            employeeForm.getRemaindThisYear(),
	            employeeForm.getRemaindLastYear(),
	            bindingResult);

	    // 必須エラーがあれば以降のチェックをしない
	    if (bindingResult.hasErrors()) {
	        return "SMSEM002";
	    }

	    // 顧客番号存在チェック
	    boolean clientExists =
	            employeeService.existsClient(employeeForm.getClientId());

	    if (!clientExists) {
	        bindingResult.rejectValue(
	                "clientId",
	                "",
	                "存在しない顧客情報です");
	    }

	    // 顧客番号エラーがあれば重複チェック・登録をしない
	    if (bindingResult.hasErrors()) {
	        return "SMSEM002";
	    }

	    // 社員重複チェック
	    boolean duplicate =
	            employeeService.checkEmployeeDuplicate(employeeForm);

	    if (duplicate) {
	        bindingResult.rejectValue(
	                "employeeId",
	                "COM01E011",
	                new Object[] {
	                        null,
	                        "社員番号",
	                        employeeForm.getEmployeeId(),
	                        "社員マスタ"
	                },
	                null);

	        return "SMSEM002";
	    }

	    // 新規登録用の値
	    employeeForm.setDeleteFlg("0");

	    if (employeeForm.getHourlyWage() == null) {
	        employeeForm.setHourlyWage("0");
	    }

	    // DB登録
	    int result = employeeService.registEmployee(employeeForm);

	    if (result > 0) {
	        employeeService.registPaidVacation(employeeForm);
	    }

	    // セッションに保存していた入力内容を削除
	    session.removeAttribute("employeeForm");

	    // 入力欄を空にしてSMSEM002を再表示
	    return "redirect:/employee/input";
	}

	//社員マスタ閲覧画面
	@GetMapping("/detail")
	public String employeeDetail(
			@RequestParam("employeeId") String employeeId,
			Model model,
			HttpSession session) {

		Map<String, Object> employee = employeeService.searchEmployeeById(employeeId);

		EmployeeForm employeeForm = new EmployeeForm();

		employeeForm.setEmployeeId(
				String.format("%04d",
						Integer.valueOf(
								String.valueOf(employee.get("employee_id")))));

		employeeForm.setEmployeeName(
				String.valueOf(employee.get("employee_name")));

		employeeForm.setClientId(
				String.format("%03d",
						Integer.valueOf(
								String.valueOf(employee.get("client_id")))));

		employeeForm.setClientName(
				String.valueOf(employee.get("client_name")));

		// hourly_wage（BIT(1)）
		Object hourlyWageValue = employee.get("hourly_wage");

		if (hourlyWageValue instanceof byte[]) {

			byte[] bytes = (byte[]) hourlyWageValue;

			if (bytes.length > 0 && bytes[0] == 1) {
				employeeForm.setHourlyWage("1");
			} else {
				employeeForm.setHourlyWage("0");
			}

		} else {

			employeeForm.setHourlyWage(
					String.valueOf(hourlyWageValue));
		}

		// 有給基準日 yyyy-MM-dd → yyyy/MM/dd
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		LocalDate paidHolidayStd = ((java.sql.Date) employee.get("paid_holiday_std"))
				.toLocalDate();

		employeeForm.setPaidHolidayStd(
				paidHolidayStd.format(dateFormatter));

		// 有給残日数：小数1桁で表示
		employeeForm.setRemaindThisYear(
				String.format("%.1f",
						Double.valueOf(
								String.valueOf(
										employee.get("remaind_this_year")))));

		employeeForm.setRemaindLastYear(
				String.format("%.1f",
						Double.valueOf(
								String.valueOf(
										employee.get("remaind_last_year")))));

		model.addAttribute("employeeForm", employeeForm);

		// 顧客選択ダイアログ用
		List<Map<String, Object>> clientList = employeeService.getClient();

		model.addAttribute("client_list", clientList);

		session.setAttribute("employeeMode", "update");

		return "SMSEM002";
	}

	@GetMapping("/searchClientById")
	@ResponseBody
	public Map<String, Object> searchClientById(
			@RequestParam("clientId") String clientId) {

		return employeeService.searchClientById(clientId);
	}

	@GetMapping("/searchClientByName")
	@ResponseBody
	public Map<String, Object> searchClientByName(
			@RequestParam("clientName") String clientName) {

		return employeeService.searchClientByName(clientName);
	}

	//社員マスタ更新処理
	@PostMapping("/update")
	public String updateEmployee(
	        EmployeeForm employeeForm,
	        BindingResult bindingResult) {

	    // 必須チェック
	    checkRequired(
	            employeeForm.getEmployeeId(),
	            employeeForm.getEmployeeName(),
	            employeeForm.getPaidHolidayStd(),
	            employeeForm.getRemaindThisYear(),
	            employeeForm.getRemaindLastYear(),
	            bindingResult);

	    // 必須エラーがあれば更新しない
	    if (bindingResult.hasErrors()) {
	        return "SMSEM002";
	    }

	    // 顧客番号存在チェック
	    boolean clientExists =
	            employeeService.existsClient(employeeForm.getClientId());

	    if (!clientExists) {
	        bindingResult.rejectValue(
	                "clientId",
	                "",
	                "存在しない顧客情報です");
	    }

	    // 顧客番号エラーがあれば更新しない
	    if (bindingResult.hasErrors()) {
	        return "SMSEM002";
	    }

	    // エラーがなければ更新
	    employeeService.updateEmployee(employeeForm);
	    employeeService.updatePaidVacation(employeeForm);

	    return "redirect:/employee/list";
	}

	//社員マスタ削除処理
	@PostMapping("/delete")
	public String deleteEmployee(
			@ModelAttribute EmployeeForm employeeForm,
			HttpSession session) {

		// セッション管理
		session.setAttribute("employeeForm", employeeForm);

		// 社員マスタ削除
		employeeService.deleteEmployee(employeeForm);

		// 有給残日数のレコードを論理削除
		employeeService.deletePaidVacation(employeeForm);

		return "redirect:/employee/list";
	}

	//キャンセルボタン押下処理
	@PostMapping("/cancel")
	public String cancelEmployee(HttpSession session) {

		// 入力内容をセッションから破棄
		session.removeAttribute("employeeForm");

		// 共通排他チェック（編集済み）は
		// まだ未実装なので現時点では何もしない

		// 社員マスタ一覧画面へ
		return "redirect:/employee/list";
	}
}
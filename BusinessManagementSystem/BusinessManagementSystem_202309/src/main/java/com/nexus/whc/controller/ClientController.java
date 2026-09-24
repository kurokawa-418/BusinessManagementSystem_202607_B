package com.nexus.whc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.form.ClientForm;
import com.nexus.whc.services.ClientService;
import com.nexus.whc.services.LockService;

/*
 * ClientController.java
 * 
 * ClientControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/client")
public class ClientController {

	private final ClientService clientService;

	private final LockService lockService;

	private final MessageSource messageSource;

	@Autowired
	public ClientController(
			ClientService clientService,
			LockService lockService,
			MessageSource messageSource) {

		this.clientService = clientService;
		this.lockService = lockService;
		this.messageSource = messageSource;
	}

	private static final String LOCK_TABLE_NAME = "m_client";

	private static final String SESSION_USER_ID = "userId";

	private String getUserId(HttpSession session) {

		String userId = (String) session.getAttribute(SESSION_USER_ID);

		if (userId == null) {
			userId = "nexus001";
			session.setAttribute(SESSION_USER_ID, userId);
		}

		return userId;
	}

	@GetMapping("/list")
	public String clientList(
			@RequestParam(name = "clientId", defaultValue = "") String clientId,
			@RequestParam(name = "clientName", defaultValue = "") String clientName,
			@RequestParam(name = "search", defaultValue = "false") boolean search,
			@RequestParam(name = "page", defaultValue = "1") int page,
			Model model) {

		List<Map<String, Object>> clientList = clientService.searchClients(clientId, clientName, page);

		int totalCount = clientService.countClients(clientId, clientName);

		int totalPages = (int) Math.ceil(totalCount / 20.0);

		List<Integer> pageNumbers = createPageNumbers(page, totalPages);

		if (search && clientList.isEmpty()) {
			model.addAttribute(
					"message",
					getMessage("COM01W001", null, "顧客"));
		}

		model.addAttribute("pageNumbers", pageNumbers);
		model.addAttribute("clientList", clientList);
		model.addAttribute("clientId", clientId);
		model.addAttribute("clientName", clientName);
		model.addAttribute("page", page);
		model.addAttribute("totalPages", totalPages);

		return "SMSCL001";
	}

	@GetMapping("/regist")
	public String getRegist(
			@RequestParam(name = "clientId", required = false) Integer clientId,
			HttpSession session,
			Model model,
			RedirectAttributes attr) {

		ClientForm clientForm;

		if (clientId == null) {
			clientForm = new ClientForm();

		} else {
			// 削除済みチェック
			if (!clientService.existsActiveClient(clientId)) {
				attr.addFlashAttribute(
						"message",
						getMessage("COM01E005"));
				return "redirect:/client/list";
			}

			// 編集中チェック
			if (lockService.isLocked(LOCK_TABLE_NAME, clientId)) {

				String lockingUserId = lockService.getLockingUserId(
						LOCK_TABLE_NAME,
						clientId);

				attr.addFlashAttribute(
						"message",
						getMessage(
								"COM01E006",
								null,
								lockingUserId));

				return "redirect:/client/list";
			}

			// 編集ロック登録
			lockService.insertLock(
					LOCK_TABLE_NAME,
					clientId,
					getUserId(session));

			clientForm = clientService.getClientFormById(clientId);
		}

		boolean isUpdateMode = clientId != null;

		model.addAttribute("isUpdateMode", isUpdateMode);
		model.addAttribute("clientForm", clientForm);

		return "SMSCL002";
	}

	@PostMapping("/regist")
	public String postRegist(
			@Validated @ModelAttribute ClientForm clientForm,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {

			String fieldName = getRequiredFieldName(bindingResult);

			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E001",
							null,
							fieldName));

			return "SMSCL002";
		}

		if (clientService.existsClientId(clientForm.getClientId())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E011",
							null,
							"顧客番号",
							String.valueOf(clientForm.getClientId()),
							"顧客マスタ"));

			return "SMSCL002";
		}

		if (clientService.existsClientName(clientForm.getClientName())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E011",
							null,
							"顧客名",
							clientForm.getClientName(),
							"顧客マスタ"));

			return "SMSCL002";
		}

		clientService.insertClient(clientForm);

		return "redirect:/client/list";
	}

	@PostMapping("/regist-next")
	public String postRegistNext(
			@Validated @ModelAttribute ClientForm clientForm,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage("COM01E001", null, "必須項目"));
			return "SMSCL002";
		}

		if (clientService.existsClientId(clientForm.getClientId())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E011",
							null,
							"顧客番号",
							String.valueOf(clientForm.getClientId()),
							"顧客マスタ"));

			return "SMSCL002";
		}

		if (clientService.existsClientName(clientForm.getClientName())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E011",
							null,
							"顧客名",
							clientForm.getClientName(),
							"顧客マスタ"));

			return "SMSCL002";
		}

		clientService.insertClient(clientForm);

		return "redirect:/client/regist";
	}

	@PostMapping("/update")
	public String postUpdate(
			@Validated @ModelAttribute ClientForm clientForm,
			BindingResult bindingResult,
			Model model,
			HttpSession session) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("isUpdateMode", true);
			model.addAttribute(
					"message",
					getMessage("COM01E001", null, "必須項目"));
			return "SMSCL002";
		}

		String userId = getUserId(session);

		// 削除済みチェック
		if (!clientService.existsActiveClient(clientForm.getClientId())) {

			model.addAttribute("isUpdateMode", true);
			model.addAttribute(
					"message",
					getMessage("COM01E005"));

			return "SMSCL002";
		}

		// 他ユーザーが編集中かチェック
		if (lockService.isLockedByOtherUser(
				LOCK_TABLE_NAME,
				clientForm.getClientId(),
				userId)) {

			String lockingUserId = lockService.getLockingUserId(
					LOCK_TABLE_NAME,
					clientForm.getClientId(),
					userId);

			model.addAttribute("isUpdateMode", true);
			model.addAttribute(
					"message",
					getMessage(
							"COM01E006",
							null,
							lockingUserId));

			return "SMSCL002";
		}

		clientService.updateClient(clientForm);

		lockService.deleteLock(
				LOCK_TABLE_NAME,
				clientForm.getClientId(),
				userId);

		return "redirect:/client/list";
	}

	@PostMapping("/delete")
	public String postDelete(
			@RequestParam(name = "clientIds", required = false) List<Integer> clientIds,
			RedirectAttributes attr) {

		// 未選択チェック
		if (clientIds == null || clientIds.isEmpty()) {

			attr.addFlashAttribute(
					"message",
					getMessage("COM01W003"));

			return "redirect:/client/list";
		}

		// 選択された顧客を1件ずつチェック
		for (Integer clientId : clientIds) {

			if (!clientService.existsActiveClient(clientId)) {

				attr.addFlashAttribute(
						"message",
						getMessage("COM01E005"));

				return "redirect:/client/list";
			}

			boolean locked = lockService.isLocked(
					LOCK_TABLE_NAME,
					clientId);

			if (locked) {

				String lockingUserId = lockService.getLockingUserId(
						LOCK_TABLE_NAME,
						clientId);

				attr.addFlashAttribute(
						"message",
						getMessage(
								"COM01E006",
								null,
								lockingUserId));

				return "redirect:/client/list";
			}

		}

		// 全件問題なければ削除
		clientService.deleteClients(clientIds);

		return "redirect:/client/list";

	}

	private List<Integer> createPageNumbers(
			int page, int totalPages) {

		List<Integer> pageNumbers = new ArrayList<>();

		if (totalPages <= 5) {
			for (int i = 1; i <= totalPages; i++) {
				pageNumbers.add(i);
			}
			return pageNumbers;
		}

		List<Integer> baseNumbers = new ArrayList<>();

		if (page <= 2) {
			baseNumbers.add(1);
			baseNumbers.add(2);
			baseNumbers.add(3);
			baseNumbers.add(totalPages);

		} else if (page == 3) {
			baseNumbers.add(1);
			baseNumbers.add(2);
			baseNumbers.add(3);
			baseNumbers.add(4);
			baseNumbers.add(totalPages);

		} else if (page == totalPages - 2) {
			baseNumbers.add(1);
			baseNumbers.add(totalPages - 3);
			baseNumbers.add(totalPages - 2);
			baseNumbers.add(totalPages - 1);
			baseNumbers.add(totalPages);

		} else if (page >= totalPages - 1) {
			baseNumbers.add(1);
			baseNumbers.add(totalPages - 2);
			baseNumbers.add(totalPages - 1);
			baseNumbers.add(totalPages);

		} else {
			baseNumbers.add(1);
			baseNumbers.add(page - 1);
			baseNumbers.add(page);
			baseNumbers.add(page + 1);
			baseNumbers.add(totalPages);
		}

		Integer before = null;

		for (Integer pageNumber : baseNumbers) {

			if (before != null && pageNumber - before > 1) {
				pageNumbers.add(-1);
			}

			pageNumbers.add(pageNumber);
			before = pageNumber;
		}

		return pageNumbers;
	}

	@PostMapping("/cancel")
	public String postCancel(
			@RequestParam Integer clientId,
			HttpSession session) {

		lockService.deleteLock(
				LOCK_TABLE_NAME,
				clientId,
				getUserId(session));

		return "redirect:/client/list";
	}

	/**
	 * propertiesからメッセージを取得する
	 *
	 * @param code メッセージコード
	 * @param args メッセージへ埋め込む値
	 * @return メッセージ
	 */
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	private String getRequiredFieldName(BindingResult bindingResult) {

		for (FieldError error : bindingResult.getFieldErrors()) {

			if ("NotNull".equals(error.getCode())
					|| "NotBlank".equals(error.getCode())) {

				switch (error.getField()) {
				case "clientId":
					return "顧客番号";
				case "clientName":
					return "顧客名";
				case "openTime":
					return "始業時刻";
				case "closeTime":
					return "終業時刻";
				case "workingTime":
					return "作業時間";
				case "rest1Start":
					return "休憩1開始時刻";
				case "rest1End":
					return "休憩1終了時刻";
				default:
					return "必須項目";
				}
			}
		}

		return "必須項目";
	}
}

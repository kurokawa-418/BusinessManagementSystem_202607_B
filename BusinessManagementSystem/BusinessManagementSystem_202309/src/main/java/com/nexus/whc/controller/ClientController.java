package com.nexus.whc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.form.ClientForm;
import com.nexus.whc.services.ClientService;

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

	@Autowired
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
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
					"顧客の検索結果は0件です。条件を変更し、再度検索してください。");
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
			Model model) {

		ClientForm clientForm;

		if (clientId == null) {
			clientForm = new ClientForm();
		} else {
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
			model.addAttribute("isUpdateMode", false);
			model.addAttribute("message", "必須項目を入力してください。");
			return "SMSCL002";
		}

		if (clientService.existsClient(
				clientForm.getClientId(),
				clientForm.getClientName())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute("message", "顧客番号または顧客名はすでに登録されています。");
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
			model.addAttribute("message", "必須項目を入力してください。");
			return "SMSCL002";
		}

		if (clientService.existsClient(
				clientForm.getClientId(),
				clientForm.getClientName())) {

			model.addAttribute("isUpdateMode", false);
			model.addAttribute("message", "顧客番号または顧客名はすでに登録されています。");
			return "SMSCL002";
		}

		clientService.insertClient(clientForm);

		return "redirect:/client/regist";
	}

	@PostMapping("/update")
	public String postUpdate(
			@Validated @ModelAttribute ClientForm clientForm,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("isUpdateMode", true);
			model.addAttribute("message", "必須項目を入力してください。");
			return "SMSCL002";
		}

		clientService.updateClient(clientForm);

		return "redirect:/client/list";
	}

	@PostMapping("/delete")
	public String postDelete(
			@RequestParam(name = "clientIds", required = false) List<Integer> clientIds,
			RedirectAttributes attr) {

		if (clientIds == null || clientIds.isEmpty()) {

			attr.addFlashAttribute(
					"message",
					"対象が選択されていません。対象を選択してください。");

			return "redirect:/client/list";
		}

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
}

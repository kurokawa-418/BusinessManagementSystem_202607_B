package com.nexus.whc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
			Model model) {

		List<Map<String, Object>> clientList = clientService.searchClients(clientId, clientName);

		model.addAttribute("clientList", clientList);
		model.addAttribute("clientId", clientId);
		model.addAttribute("clientName", clientName);

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

		model.addAttribute("clientForm", clientForm);

		return "SMSCL002";
	}

	@PostMapping("/regist")
	public String postRegist(@ModelAttribute ClientForm clientForm) {

		clientService.insertClient(clientForm);

		return "redirect:/client/list";
	}

	@PostMapping("/update")
	public String postUpdate(@ModelAttribute ClientForm clientForm) {

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
}

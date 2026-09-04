package com.nexus.whc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexus.whc.services.UserService;

/*
 * UserController.java
 * 
 * UserControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/user")
public class UserController {
	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	//ユーザ情報入力画面Get用
	@GetMapping("/input")
	public String getUser() {

		//ユーザー情報入力画面に遷移
		return "charaInput";
	}

	//ユーザー一覧
	@GetMapping("/list")
	public String userList(
			@RequestParam(name = "user_id", defaultValue = "") String userId,
			@RequestParam(name = "user_name", defaultValue = "") String userName,
			@RequestParam(name = "permission", defaultValue = "") String authId,
			@RequestParam(name = "mail_address", defaultValue = "") String mailAddress,
			Model model) {

		//DB検索
		List<Map<String, Object>> userlist = userService.searchList(userId, userName, authId, mailAddress);
		if (userlist.isEmpty()) {
			model.addAttribute("message", "{COM01W001}");
		}
		//リクエストスコープに保存
		model.addAttribute("userList", userlist);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("authId", authId);
		model.addAttribute("mailAddress", mailAddress);
		//ユーザーマスタ一覧画面に遷移
		return "SMSUS001";
	}
}

 
package com.nexus.whc.controller;
 
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

import com.nexus.whc.form.UserForm;
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
 
	/*ユーザ情報入力画面Get用*/
	@GetMapping("/input")
	public String getUser() {
 
		/*ユーザー情報入力画面に遷移*/
		return "userInput";
	}
 
	/*ユーザー情報入力画面Post用*/
	@PostMapping("/input")
	public String postUser() {
		return "userInput";
	}
 
	/*ユーザー一覧*/
	@GetMapping("/list")
	public String userList(
			@RequestParam(name = "user_id", defaultValue = "") String userId,
			@RequestParam(name = "user_name", defaultValue = "") String userName,
			@RequestParam(name = "permission", defaultValue = "") String authId,
			@RequestParam(name = "mail_address", defaultValue = "") String mailAddress,
			Model model) {
 
		/*DB検索*/
		List<Map<String, Object>> userlist = userService.searchList(userId, userName, authId, mailAddress);
		if (userlist.isEmpty()) {
			model.addAttribute("message", "{COM01W001}");
		}
		/*リクエストスコープに保存*/
		model.addAttribute("userList", userlist);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("authId", authId);
		model.addAttribute("mailAddress", mailAddress);
		/*ユーザーマスタ一覧画面に遷移*/
		return "SMSUS001";
	}
 
	/*ユーザー登録(新規追加モード）*/
	@PostMapping("/regist")
	public String userRegist(@Validated @ModelAttribute UserForm userForm,
			BindingResult bindingResult,
			RedirectAttributes attr) {
		//未入力チェック
		if (bindingResult.hasErrors()) {
			//入力画面に遷移する
			return "SMSUS002";
		}
		// 登録結果
		/*宣言＋初期値の設定＋Serviceの呼び出し*/
		int result = userService.registUser(userForm);
 
		if (0 == result) {
			// エラーメッセージをフラッシュスコープに保存
			attr.addFlashAttribute("message", "登録エラーが発生しました");
			// エラー画面に遷移
			return "redirect:/user/error";
		} else {
			//ユーザー一覧画面に遷移
			return "redirect:/user/list";
		}
	}
 
	/*削除*/
	@PostMapping("/delete")
	public String deleteUser(@RequestParam(required = false) List<Integer> sequenceId, RedirectAttributes attr) {
		if (sequenceId == null || sequenceId.isEmpty()) {
			attr.addFlashAttribute("message", "COM01W003");
			return "redirect:/user/list";
		}
 
		for (Integer seqId : sequenceId) {
			/*排他チェックメソッド呼び出し（削除、編集中）*/
			userService.deleteUser(seqId);
		}
 
		return "redirect:/user/list";
 
	}
}
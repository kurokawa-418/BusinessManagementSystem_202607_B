package com.nexus.whc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.nexus.whc.services.LockService;
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
	private final LockService lockService;
	private UserService userService;
	private MessageSource messageSource;

	@Autowired
	public UserController(UserService userService, MessageSource messageSource, LockService lockService) {
		this.userService = userService;
		this.messageSource = messageSource;
		this.lockService = lockService;

	}

	/*ユーザ情報入力画面(新規登録)*/
	@GetMapping("/input")
	public String inputUser(@RequestParam("seq_id") Integer seqId,
			Model model,
			HttpSession session) {
		UserForm userForm = new UserForm();
		userForm.setSeqId(seqId);

		model.addAttribute("userForm", userForm);
		session.setAttribute("userMode", "new");
		return "SMSUS002";
	}

	/*ユーザー登録(新規登録）*/
	@PostMapping("/regist")
	public String userRegist(@Validated @ModelAttribute UserForm userForm,
			BindingResult bindingResult,
			RedirectAttributes attr,
			HttpSession session) {
		//未入力チェック
		if (bindingResult.hasErrors()) {
			return "SMSUS002";
		}
		/*マスタ存在チェック*/
		if (userService.existsUser(
				userForm.getUserId(),
				userForm.getUserName(),
				userForm.getMailAddress())) {

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

	/*ユーザー登録して次へ*/
	@PostMapping("/registNext")
	public String userRegistNext(@Validated @ModelAttribute UserForm userForm,
			BindingResult bindingResult,
			RedirectAttributes attr,
			HttpSession session,
			Model model) {

		/* 未入力チェック*/
		if (bindingResult.hasErrors()) {
			return "SMSUS002";
		}

		/*マスタ存在チェック*/
		if (userService.existsUser(
				userForm.getUserId(),
				userForm.getUserName(),
				userForm.getMailAddress())) {
			return "SMSUS002";
		}

		/* 登録*/
		int result = userService.registUser(userForm);

		if (0 == result) {
			attr.addFlashAttribute("message", "登録エラーが発生しました");
			return "redirect:/user/error";
		} else {
			/*登録後、もう一度新規登録画面を表示*/
			UserForm newUserForm = new UserForm();
			newUserForm.setSeqId(0);
			model.addAttribute("userForm", newUserForm);

			session.setAttribute("userMode", "new");

			return "SMSUS002";
		}
	}

	/*ユーザ情報入力画面(更新)*/
	@GetMapping("/update")
	public String updateUser(
			@RequestParam("seq_id") Integer seqId,
			Model model,
			HttpSession session) {
		/*DBから取り出した値をMapのuserに格納*/
		Map<String, Object> user = userService.findUserBySeqId(seqId);

		UserForm userForm = new UserForm();
		/*DBから取得したユーザ情報の内、user_idをString型にしてからUserFormのuserIdに設定*/
		/*UserFormにコピーしてる*/
		userForm.setUserId((String) user.get("user_id"));

		userForm.setUserName((String) user.get("user_name"));
		userForm.setAuthId(String.valueOf(user.get("auth_id")));
		userForm.setMailAddress((String) user.get("mail_address"));
		userForm.setSeqId(seqId);
		/*コピーした値を画面に渡す*/
		model.addAttribute("userForm", userForm);
		session.setAttribute("userMode", "update");
		return "SMSUS002";
	}

	@PostMapping("/update")
	public String updateUser(@Validated @ModelAttribute UserForm userForm,
			BindingResult bindingResult,
			RedirectAttributes attr,
			HttpSession session) {
		//未入力チェック
		if (bindingResult.hasErrors()) {
			return "SMSUS002";
		}
		// 登録結果
		/*宣言＋初期値の設定＋Serviceの呼び出し*/
		int result = userService.updateUser(userForm);

		if (0 == result) {
			// エラーメッセージをフラッシュスコープに保存
			attr.addFlashAttribute("message", "更新エラーが発生しました");
			// エラー画面に遷移
			return "redirect:/user/error";
		} else {
			//ユーザー一覧画面に遷移
			return "redirect:/user/list";
		}
	}

	/*ユーザー一覧*/
	@GetMapping("/list")
	public String userList(
			@RequestParam(name = "user_id", defaultValue = "") String userId,
			@RequestParam(name = "user_name", defaultValue = "") String userName,
			@RequestParam(name = "permission", defaultValue = "") String authId,
			@RequestParam(name = "mail_address", defaultValue = "") String mailAddress,
			@RequestParam(name = "search", defaultValue = "false") boolean search,
			@RequestParam(name = "page", defaultValue = "1") int page,
			Model model) {

		/*表示する件数*/
		int pageSize = 20;
		if (page < 1) {
			page = 1;
		}
		// 全件数を取得
		int totalCount = userService.countUser(
				userId,
				userName,
				authId,
				mailAddress);

		// 全ページ数を計算
		int totalPages = (totalCount + pageSize - 1) / pageSize;

		// ページが存在しない場合
		if (totalPages > 0 && page > totalPages) {
			page = totalPages;
		}

		// ページ番号を作成
		List<Integer> pageNumbers = new ArrayList<>();

		if (totalPages <= 5) {

			for (int i = 1; i <= totalPages; i++) {
				pageNumbers.add(i);
			}

		} else if (page <= 2) {

			pageNumbers.add(1);
			pageNumbers.add(2);
			pageNumbers.add(3);
			pageNumbers.add(-1);
			pageNumbers.add(totalPages);

		} else if (page == 3) {

			pageNumbers.add(1);
			pageNumbers.add(2);
			pageNumbers.add(3);
			pageNumbers.add(4);
			pageNumbers.add(-1);
			pageNumbers.add(totalPages);

		} else if (page <= totalPages - 3) {

			pageNumbers.add(1);
			pageNumbers.add(-1);
			pageNumbers.add(page - 1);
			pageNumbers.add(page);
			pageNumbers.add(page + 1);
			pageNumbers.add(-1);
			pageNumbers.add(totalPages);

		} else {

			pageNumbers.add(1);
			pageNumbers.add(-1);
			pageNumbers.add(totalPages - 2);
			pageNumbers.add(totalPages - 1);
			pageNumbers.add(totalPages);
		}

		/*DB検索*/
		List<Map<String, Object>> userlist = userService.searchList(
				userId,
				userName,
				authId,
				mailAddress,
				page,
				pageSize);

		if (search && userlist.isEmpty()) {
			String message = messageSource.getMessage("COM01W001",
					new Object[] { "", "ユーザ" },
					Locale.JAPAN);
			model.addAttribute("message", message);
		}
		/*リクエストスコープに保存（画面に渡す）*/
		model.addAttribute("userList", userlist);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("authId", authId);
		model.addAttribute("mailAddress", mailAddress);

		// ページネーション用
		model.addAttribute("page", page);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageNumbers", pageNumbers);
		/*ユーザーマスタ一覧画面に遷移*/
		return "SMSUS001";
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

	/*キャンセル処理*/
	@PostMapping("/cancel")
	public String cancelUser(
			@RequestParam("seqId") Integer seqId) {
		return "redirect:/user/list";
	}

}
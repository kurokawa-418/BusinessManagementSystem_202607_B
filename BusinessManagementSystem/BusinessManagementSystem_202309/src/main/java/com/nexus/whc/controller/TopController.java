package com.nexus.whc.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nexus.whc.services.TopService;

/*
 * TopController.java
 *
 * TopControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
public class TopController {

	private final TopService topService;

	@Autowired
	public TopController(TopService topService) {

		this.topService = topService;

	}

	/*
	 * TOP画面初期表示
	 */
	@GetMapping("/SCMCM001")
	public String topList(Model model) {

		// ファイル・リンク一覧を取得
		List<Map<String, Object>> fileLinkList = topService.searchFileLink();

		// HTMLへ渡す
		model.addAttribute("fileLinkList", fileLinkList);

		return "SCMCM001";
	}

	/*
	 * ファイル参照
	 */
	@GetMapping("/file/{fileName}")
	public ResponseEntity<Resource> viewFile(
			@PathVariable String fileName) throws IOException {

		Resource resource = new ClassPathResource("static/files/" + fileName);

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(resource);
	}

	/*
	 * ファイルダウンロード
	 */
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> download(
			@PathVariable String fileName) throws IOException {

		Resource resource = new ClassPathResource("static/files/" + fileName);

		HttpHeaders headers = new HttpHeaders();

		headers.set(
				HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileName + "\"");

		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

}
package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.repository.TopRepository;

/*
 * TopService.java
 *
 * TopServiceクラス
 */

/*
 * Serviceクラス
 */
@Service
public class TopService {

	private final TopRepository topRepository;

	@Autowired
	public TopService(TopRepository topDao) {

		this.topRepository = topDao;

	}

	/*
	 * ファイル・リンク一覧取得
	 */
	public List<Map<String, Object>> searchFileLink() {

		return topRepository.searchFileLink();

	}

}
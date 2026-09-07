package com.nexus.whc.services;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.nexus.whc.repository.LockRepository;

@Service
public class LockService {

	private final LockRepository lockRepository;

	private static final String LOCK_TABLE_NAME = "m_client";

	private static final String SESSION_USER_ID = "userId";

	public LockService(LockRepository lockRepository) {
		this.lockRepository = lockRepository;
	}

	private String getUserId(HttpSession session) {

		String userId = (String) session.getAttribute(SESSION_USER_ID);

		if (userId == null) {
			userId = "nexus@001";
			session.setAttribute(SESSION_USER_ID, userId);
		}

		return userId;
	}

	public boolean isLocked(String tableName, Integer recordId) {
		return lockRepository.isLocked(tableName, recordId);
	}

	public int insertLock(
			String tableName,
			Integer recordId,
			String userId) {

		return lockRepository.insertLock(tableName, recordId, userId);
	}

	public int deleteLock(
			String tableName,
			Integer recordId,
			String userId) {

		return lockRepository.deleteLock(tableName, recordId, userId);
	}

	public boolean isLockedByOtherUser(
			String tableName,
			Integer recordId,
			String userId) {

		return lockRepository.isLockedByOtherUser(
				tableName,
				recordId,
				userId);
	}
}
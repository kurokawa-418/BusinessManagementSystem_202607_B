package com.nexus.whc.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarDetail;
import com.nexus.whc.repository.CalendarRepository;

/*
 * CalendarService.java
 * 
 * CalendarServiceクラス
 */

/*
 * Serviceクラス
 */
@Service
public class CalendarService {

	/* CalendarRepositoryクラス */
	private final CalendarRepository calendarRepository;

	/* CalendarServiceクラス */
	@Autowired
	public CalendarService(CalendarRepository calendarRepository) {
		this.calendarRepository = calendarRepository;
	}

	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー情報を抽出する
	 * @param seq_id
	 * @return 抽出結果のlist
	 */
	public CalendarData searchCalendarData(String seq_id) {

		Map<String, Object> result = calendarRepository.searchCalendarData(seq_id);

		CalendarData calendarData = new CalendarData();

		calendarData.setClientName(result.get("client_name").toString());
		calendarData.setEmployeeId((Integer) result.get("employee_id"));
		calendarData.setEmployeeName(result.get("employee_name").toString());
		calendarData.setYearMonth(LocalDate.parse(result.get("year_month").toString()));
		calendarData.setAllYearRoundComment(result.get("comment").toString());

		return calendarData;
	}
	
	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー詳細情報を抽出する
	 * @param seq_id シーケンスID
	 * @return 抽出結果のlist
	 */
	public List<CalendarDetail> searchCalendarDetails(String seq_id) {

		List<Map<String, Object>> list = calendarRepository.searchCalendarDetails(seq_id);

		List<CalendarDetail> result = new ArrayList<>();

		for (Map<String, Object> map : list) {

			CalendarDetail calendarDetail = new CalendarDetail();

			//日付を表示する形に変換
			LocalDate date = LocalDate.parse(map.get("date").toString());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
			String formattedDay = date.format(formatter);

			//曜日を取得
			int dayOfWeek = date.getDayOfWeek().getValue();

			//フォームに追加
			calendarDetail.setDate(formattedDay);
			calendarDetail.setHolidayFlg((boolean) map.get("holiday_Flg"));
			calendarDetail.setComment(map.get("comment").toString());
			calendarDetail.setDayOfWeek(dayOfWeek);
			
			result.add(calendarDetail);
		}

		return result;
	}

	/**
	 * 顧客選択ダイアログ
	 */
	public List<Map<String, Object>> getClient() {

		List<Map<String, Object>> list = calendarRepository.getClient();

		return list;
	}

	/**
	 * 社員選択ダイアログ用
	 */
	public List<Map<String, Object>> getEmployee() {

		List<Map<String, Object>> list = calendarRepository.getEmployee();

		return list;
	}

}
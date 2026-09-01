package com.nexus.whc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarDetail;
import com.nexus.whc.services.CalendarService;

/*
 * CalendarController.java
 * 
 * CalendarControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/calendar")
public class CalendarController {

	/* CalendarServiceクラス */
	@Autowired
	CalendarService calendarService;

	@GetMapping("/list")
	public String calendarList(Model model) {

		/*顧客,社員選択ダイアログ用処理ここから*/
		/*ダイアログを実装している他の箇所にもこれをコピーしてください*/
		List<Map<String, Object>> clientList = calendarService.getClient();
		model.addAttribute("client_list", clientList);
		List<Map<String, Object>> employeeList = calendarService.getEmployee();
		model.addAttribute("employee_list", employeeList);
		/*顧客,社員選択ダイアログ処理ここまで*/

		return "SMSCD001";
	}

	

	/**
	 * 閲覧処理
	 * @param calendarSeqIdIndex チェックボックス
	 * @param eventStatus ステータス
	 * @param seqId シーケンスID
	 * @param model modelデータ
	 * @return SMSCD002画面
	 */
	@GetMapping("/browse")
	public String browseCalender(@RequestParam(name = "seqId", required = false) String seqId, Model model) {

		String mode = "view";

		// 顧客、社員、年月情報取得
		CalendarData calendarData = calendarService.searchCalendarData(seqId);

		// カレンダー詳細情報取得
		List<CalendarDetail> calendarList = calendarService.searchCalendarDetails(seqId);

		// カレンダーリストを整形して表示用のリストを取得
		List<List<CalendarDetail>> formatLists = formatCalendarList(calendarList, calendarData.getYearMonth());

		// 年月のフォーマット
		DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");

		model.addAttribute("year", LocalDate.parse(calendarData.getYearMonth().toString()).format(yearFormatter));
		model.addAttribute("month", LocalDate.parse(calendarData.getYearMonth().toString()).format(monthFormatter));

		model.addAttribute("calendarData", calendarData);
		model.addAttribute("calendarDatail", formatLists);

		model.addAttribute("seqId", seqId);
		model.addAttribute("mode", mode);

		return "SMSCD002";
	}

	/**
	 * カレンダーリスト表示用に整形するメソッド
	 * @param calendarList シーケンスidから取得したカレンダーリスト
	 * @return List<List<Map<String, Object>>> 表示用に整形したカレンダーリスト
	 */
	private List<List<CalendarDetail>> formatCalendarList(List<CalendarDetail> calendarList, LocalDate yearMonth) {

		List<List<CalendarDetail>> formatLists = new ArrayList<>();

		List<CalendarDetail> list1 = new ArrayList<>();
		List<CalendarDetail> list2 = new ArrayList<>();
		List<CalendarDetail> list3 = new ArrayList<>();
		List<CalendarDetail> list4 = new ArrayList<>();
		List<CalendarDetail> list5 = new ArrayList<>();
		List<CalendarDetail> list6 = new ArrayList<>();

		//1日目の曜日を取得
		int firstDayOfWeek = yearMonth.getDayOfWeek().getValue();

		// カレンダー表示時に先頭に入れる空白の数
		int topBlank = firstDayOfWeek;
		if (topBlank == 7) {
			topBlank = 0;
		}

		// カレンダー表示時に末尾にいれる空白の数
		int bottomBlank = 0;
		if (calendarList.size() + topBlank <= 35) {
			bottomBlank = 35 - (calendarList.size() + topBlank);
		} else {
			bottomBlank = 42 - (calendarList.size() + topBlank);
		}

		//表示用に整形
		int count = topBlank;
		for (int i = 0; i < calendarList.size() + topBlank + bottomBlank; i++) {

			int input = i - topBlank;

			if (i < 7) {

				if (count >= 1) {
					list1.add(null);
					count--;
				} else {
					list1.add(calendarList.get(input));
				}
			} else if (i >= 7 && i < 14) {
				list2.add(calendarList.get(input));
			} else if (i >= 14 && i < 21) {
				list3.add(calendarList.get(input));
			} else if (i >= 21 && i < 28) {
				list4.add(calendarList.get(input));
			} else if ((i >= 28 && i < 35)) {

				if (i >= calendarList.size() + topBlank) {
					list5.add(null);
				} else {
					list5.add(calendarList.get(input));
				}

			} else {
				if (i >= calendarList.size() + topBlank) {
					list6.add(null);
				} else {
					list6.add(calendarList.get(input));
				}
			}

		}

		formatLists.add(list1);
		formatLists.add(list2);
		formatLists.add(list3);
		formatLists.add(list4);
		formatLists.add(list5);
		formatLists.add(list6);

		return formatLists;
	}

}

// 担当顧客番号から顧客名を取得
function searchClientById() {

	var clientId = document.getElementById("customerNumberField").value.trim();

	// 空欄なら何もしない
	if (clientId === "") {
		return;
	}

	fetch("/employee/searchClientById?clientId=" + encodeURIComponent(clientId))
		.then(response => response.json())
		.then(data => {

			if (data !== null) {
				document.getElementById("customerNameField").value = data.clientName;
			} else {
				document.getElementById("customerNameField").value = "";
			}

		})
		.catch(error => {
			console.error("顧客検索エラー:", error);
		});
}


// 担当顧客名から顧客番号を取得
function searchClientByName() {

	var clientName = document.getElementById("customerNameField").value.trim();

	// 空欄なら何もしない
	if (clientName === "") {
		return;
	}

	fetch("/employee/searchClientByName?clientName=" + encodeURIComponent(clientName))
		.then(response => response.json())
		.then(data => {

			if (data !== null) {
				document.getElementById("customerNumberField").value = data.clientId;
			} else {
				document.getElementById("customerNumberField").value = "";
			}

		})
		.catch(error => {
			console.error("顧客検索エラー:", error);
		});
}

function checkPaidHolidayStd() {

	var input = document.getElementById("paidHolidayStd");
	var error = document.getElementById("paidHolidayStdError");
	var value = input.value.trim();

	if (value === "") {
		error.hidden = true;
		return true;
	}

	var pattern = /^\d{4}\/\d{1,2}\/\d{1,2}$/;

	if (!pattern.test(value)) {
		error.hidden = false;
		return false;
	}

	var parts = value.split("/");
	var year = Number(parts[0]);
	var month = Number(parts[1]);
	var day = Number(parts[2]);

	var date = new Date(year, month - 1, day);

	if (
		date.getFullYear() !== year ||
		date.getMonth() !== month - 1 ||
		date.getDate() !== day
	) {
		error.hidden = false;
		return false;
	}

	error.hidden = true;
	return true;
}
// 有休残日数（当年度分）のフォーマットチェック
function checkRemaindThisYear() {

	var input = document.getElementById("remaindThisYear");
	var error = document.getElementById("remaindThisYearError");

	var value = input.value.trim();

	// 空欄の場合は必須チェック側で処理する
	if (value === "") {
		error.hidden = true;
		return true;
	}

	// DECIMAL用
	var pattern = /^\d{1,2}\.\d$/;

	if (!pattern.test(value)) {
		error.hidden = false;
		return false;
	}

	error.hidden = true;
	return true;
}


// 有休残日数（前年度分）のフォーマットチェック
function checkRemaindLastYear() {

	var input = document.getElementById("remaindLastYear");
	var error = document.getElementById("remaindLastYearError");

	var value = input.value.trim();

	// 空欄の場合は必須チェック側で処理する
	if (value === "") {
		error.hidden = true;
		return true;
	}

	// DECIMAL用
	var pattern = /^\d{1,2}\.\d$/;

	if (!pattern.test(value)) {
		error.hidden = false;
		return false;
	}

	error.hidden = true;
	return true;
}
function calculatePaidLeave() {
	// 基準日の入力値を取得
	var paidHolidayStd = new Date(document.getElementById("paidHolidayStd").value);

	// 現在日付を取得
	var currentDate = new Date();

	// 基準日からの年数を計算
	var yearsSinceStd = calculateYearsSinceStd(paidHolidayStd, currentDate);

	// 年数に応じて有休日数を計算
	var paidLeaveDays = calculatePaidLeaveDays(yearsSinceStd);

	// 有休日数をフォームに表示
	document.getElementById("paidLeaveDays").value = paidLeaveDays;
}

function calculateYearsSinceStd(paidHolidayStd, currentDate) {
	var timeDiff = currentDate - paidHolidayStd;
	var daysSinceStd = timeDiff / (1000 * 60 * 60 * 24);
	var yearsSinceStd = Math.floor(daysSinceStd / 365);
	return yearsSinceStd;
}

function calculatePaidLeaveDays(yearsSinceStd) {
	if (yearsSinceStd <= 0) {
		return 0.0;
	} else if (yearsSinceStd == 1) {
		return 10.0;
	} else if (yearsSinceStd <= 6) {
		return 10.0 + (yearsSinceStd - 1) * 1.0;
	} else {
		return 20.0;
	}
}
//警告
function determinePaidLeaveStatus(paidHolidayStd, currentDate, usedPaidLeave) {
	var status = "";

	// 有休残日数(当年度分) + 有休残日数(前年度分) が 0 以下の場合
	if (usedPaidLeave <= 0) {
		status = "有休残日数なし";
	}
	// 有休基準日から6ヶ月以内で、有休取得日が 0 の場合
	else if (calculateMonthsToStd(paidHolidayStd, currentDate) <= 6 && usedPaidLeave === 0) {
		status = "有休取得日数不足(通知)";
	}
	// 有休基準日から9ヶ月以内で、有休取得日が 5 より少ない場合
	else if (calculateMonthsToStd(paidHolidayStd, currentDate) <= 9 && usedPaidLeave < 5) {
		status = "有休取得日数不足(注意)";
	}
	// 有休基準日から10ヶ月以内で、有休取得日が 5 より少ない場合
	else if (calculateMonthsToStd(paidHolidayStd, currentDate) <= 10 && usedPaidLeave < 5) {
		status = "有休取得日数不足(警告)";
	}
	// どの条件にも該当しない場合
	else {
		status = "ブランク";
	}

	return status;
}

function checkAllFormat() {

	var paidHolidayStdResult = checkPaidHolidayStd();
	var remaindThisYearResult = checkRemaindThisYear();
	var remaindLastYearResult = checkRemaindLastYear();

	return paidHolidayStdResult
		&& remaindThisYearResult
		&& remaindLastYearResult;
}
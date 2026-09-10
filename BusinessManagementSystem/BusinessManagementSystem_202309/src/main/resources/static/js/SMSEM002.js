// 担当顧客番号から顧客名を取得
function searchClientById() {

	var clientId =
		document.getElementById("customerNumberField").value.trim();

	if (clientId === "") {
		document.getElementById("customerNameField").value = "";
		return;
	}

	fetch("/employee/searchClientById?clientId="
		+ encodeURIComponent(clientId))
		.then(response => {

			if (!response.ok) {
				throw new Error("顧客検索に失敗しました");
			}

			return response.json();

		})
		.then(data => {

			console.log("顧客番号検索結果:", data);

			if (data && data.clientName != null) {

				document.getElementById("customerNameField").value =
					data.clientName;

			} else {

				document.getElementById("customerNameField").value = "";

			}

		})
		.catch(error => {

			console.error(error);

			document.getElementById("customerNameField").value = "";

		});
}

// 担当顧客名から顧客番号を取得
function searchClientByName() {

	var clientName =
		document.getElementById("customerNameField").value.trim();

	if (clientName === "") {
		document.getElementById("customerNumberField").value = "";
		return;
	}

	fetch("/employee/searchClientByName?clientName="
		+ encodeURIComponent(clientName))
		.then(response => {

			if (!response.ok) {
				throw new Error("顧客検索に失敗しました");
			}

			return response.json();

		})
		.then(data => {

			console.log("顧客名検索結果:", data);

			if (data && data.clientId != null) {

				document.getElementById("customerNumberField").value =
					String(data.clientId).padStart(3, "0");

			} else {

				document.getElementById("customerNumberField").value = "";

			}

		})
		.catch(error => {

			console.error(error);

			document.getElementById("customerNumberField").value = "";

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

    var value = document.getElementById("paidHolidayStd").value.trim();

    // 未入力なら計算しない
    if (value === "") {
        document.getElementById("remaindThisYear").value = "";
        return;
    }

    // yyyy/MM/dd の形式以外なら計算しない
    var pattern = /^\d{4}\/\d{1,2}\/\d{1,2}$/;

    if (!pattern.test(value)) {
        document.getElementById("remaindThisYear").value = "";
        return;
    }

    // yyyy/MM/dd を分解
    var parts = value.split("/");

    var year = Number(parts[0]);
    var month = Number(parts[1]);
    var day = Number(parts[2]);

    var paidHolidayStd = new Date(year, month - 1, day);

    // 実在する日付かチェック
    if (
        paidHolidayStd.getFullYear() !== year ||
        paidHolidayStd.getMonth() !== month - 1 ||
        paidHolidayStd.getDate() !== day
    ) {
        document.getElementById("remaindThisYear").value = "";
        return;
    }

    var currentDate = new Date();

    var yearsSinceStd =
        calculateYearsSinceStd(paidHolidayStd, currentDate);

    var paidLeaveDays =
        calculatePaidLeaveDays(yearsSinceStd);

    document.getElementById("remaindThisYear").value =
        paidLeaveDays.toFixed(1);
}

function calculateYearsSinceStd(paidHolidayStd, currentDate) {
	
    // 基準日より前
    if (currentDate < paidHolidayStd) {
        return -1;
    }

    var years =
        currentDate.getFullYear()
        - paidHolidayStd.getFullYear();

    // 今年の基準日をまだ迎えていない場合は1年減らす
    var anniversary =
        new Date(
            currentDate.getFullYear(),
            paidHolidayStd.getMonth(),
            paidHolidayStd.getDate()
        );

    if (currentDate < anniversary) {
        years--;
    }

    return years;
}

function calculatePaidLeaveDays(yearsSinceStd) {
	
    if (yearsSinceStd < 0) {
        return 0.0;

    } else if (yearsSinceStd === 0) {
        return 10.0;

    } else if (yearsSinceStd === 1) {
        return 11.0;

    } else if (yearsSinceStd === 2) {
        return 12.0;

    } else if (yearsSinceStd === 3) {
        return 14.0;

    } else if (yearsSinceStd === 4) {
        return 16.0;

    } else if (yearsSinceStd === 5) {
        return 18.0;

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
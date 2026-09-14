// dialog.js
// ページの読み込みが完了したら実行
document.addEventListener("DOMContentLoaded", function() {
	// ダイアログを非表示に初期化
	document.getElementById("customerDialog").style.display = "none";

	// ボタンクリック時にダイアログを表示
	document.getElementById("showDialogButton").addEventListener("click", function() {
		// ダイアログを表示
		document.getElementById("customerDialog").style.display = "block";
	});

	// ダイアログ内の顧客を選択したときの処理
	var customerRows = document.querySelectorAll("#customerDialog tbody tr");
	customerRows.forEach(function(row) {
		row.addEventListener("click", function() {
			// 顧客番号と顧客名を取得
			var customerId = row.querySelector("td:first-child").textContent;
			var customerName = row.querySelector("td:last-child").textContent;

			// 顧客番号をフィールドに自動入力
			document.getElementById("customerNumberField").value = customerId;

			// 担当顧客名をフィールドに自動入力
			document.getElementById("customerNameField").value = customerName;

			// ダイアログを非表示にする
			document.getElementById("customerDialog").style.display = "none";
		});
	});
})

document.addEventListener("DOMContentLoaded", function() {

	// ==============================
	// ページング
	// ==============================

	var rows = document.querySelectorAll(".employee-row");
	var pagination = document.getElementById("pagination");

	var currentPage = 1;
	var rowsPerPage = 20;

	function displayPage(page) {

		var totalPages = Math.ceil(rows.length / rowsPerPage);

		if (totalPages === 0) {
			pagination.innerHTML = "";
			return;
		}

		if (page < 1) {
			page = 1;
		}

		if (page > totalPages) {
			page = totalPages;
		}

		currentPage = page;

		var start = (currentPage - 1) * rowsPerPage;
		var end = start + rowsPerPage;

		rows.forEach(function(row, index) {

			if (index >= start && index < end) {
				row.style.display = "";
			} else {
				row.style.display = "none";
			}

		});

		createPagination(totalPages);
	}


	function createPagination(totalPages) {

		pagination.innerHTML = "";

		// 前のページ
		if (currentPage > 1) {

			var prev = document.createElement("a");

			prev.href = "#";
			prev.className = "mx-3";
			prev.textContent = "←　前のページ";

			prev.addEventListener("click", function(event) {

				event.preventDefault();

				displayPage(currentPage - 1);

			});

			pagination.appendChild(prev);
		}


		// ページ番号
		for (var i = 1; i <= totalPages; i++) {

			if (shouldDisplayPage(i, totalPages)) {

				if (i > 1 && !shouldDisplayPage(i - 1, totalPages)) {

					var dots = document.createElement("span");

					dots.className = "mx-1";
					dots.textContent = "...";

					pagination.appendChild(dots);
				}


				var pageLink = document.createElement("a");

				pageLink.href = "#";
				pageLink.className = "mx-1";
				pageLink.textContent = i;

				if (i === currentPage) {
					pageLink.style.fontWeight = "bold";
				}

				pageLink.addEventListener("click", function(event) {

					event.preventDefault();

					var page = Number(event.target.textContent);

					displayPage(page);

				});

				pagination.appendChild(pageLink);
			}
		}


		// 次のページ
		if (currentPage < totalPages) {

			var next = document.createElement("a");

			next.href = "#";
			next.className = "mx-3";
			next.textContent = "次のページ　→";

			next.addEventListener("click", function(event) {

				event.preventDefault();

				displayPage(currentPage + 1);

			});

			pagination.appendChild(next);
		}
	}


	function shouldDisplayPage(page, totalPages) {

		if (totalPages <= 7) {
			return true;
		}

		if (page === 1) {
			return true;
		}

		if (page === totalPages) {
			return true;
		}

		if (page >= currentPage - 1 &&
			page <= currentPage + 1) {

			return true;
		}

		if (currentPage <= 2 && page <= 3) {
			return true;
		}

		if (currentPage >= totalPages - 1 &&
			page >= totalPages - 2) {

			return true;
		}

		return false;
	}


	// 1ページ目を表示
	displayPage(1);

});

document.addEventListener("DOMContentLoaded", function() {

	var accordion = document.querySelector(".accordion");
	var listLimit = document.getElementById("listLimit");

	if (!accordion || !listLimit) {
		return;
	}

	function changeListHeight() {

		var accordionStyle = window.getComputedStyle(accordion);

		// 検索条件が閉じている場合
		if (accordionStyle.display === "none"
			|| accordion.offsetHeight === 0) {

			listLimit.style.maxHeight = "none";
			listLimit.style.overflowY = "visible";

		} else {

			// 検索条件が開いている場合
			listLimit.style.maxHeight = "calc(33px * 12)";
			listLimit.style.overflowY = "scroll";
		}
	}

	// 初期状態
	changeListHeight();

	// index.jsによる開閉を監視
	var observer = new MutationObserver(function() {
		changeListHeight();
	});

	observer.observe(accordion, {
		attributes: true,
		attributeFilter: ["style", "class"]
	});
});






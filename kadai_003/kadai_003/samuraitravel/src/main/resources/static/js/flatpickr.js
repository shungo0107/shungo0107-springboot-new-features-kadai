 let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 flatpickr('#fromCheckinDateToCheckoutDate', {
   mode: "range",
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate
 });
 
 /*このファイルでは、以下の設定をおこなっています。
     1. 変数maxDateに3か月後の日付を代入する
     1. 以下の設定内容をもとにFlatpickrのインスタンスを生成する
          1. 対象となるセレクタ：#fromCheckinDateToCheckoutDate（id="fromCheckinDateToCheckoutDate"が設定されたHTML要素）
          2. mode: "range"：カレンダーの範囲選択モードを有効にする（チェックイン日とチェックアウト日を同時に指定できるようになる）
          3. locale: 'ja'：カレンダーの言語を日本語にする
          4. minDate: 'today'：カレンダーで選択できる最小の日付を当日にする
          5. maxDate: maxDate：カレンダーで選択できる最大の日付を3か月後にする
 */
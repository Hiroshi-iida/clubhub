const clubType = 
      {
  "運動部": ["野球", "サッカー", "テニス", "陸上", "バレーボール", "バドミントン", "バスケットボール", "柔道", "卓球", "ハンドボール", "水泳", "体操","その他"],
  "文化部": ["吹奏楽", "書道", "軽音", "囲碁", "茶道", "料理", "商業/IT", "手芸", "数学", "華道", "漫画", "その他"]
        
      };


function createMenu(selectType){
  
  let type = document.getElementById('type');
  type.disabled = false;
  type.innerHTML = '';
  let option = document.createElement('option');
  option.innerHTML = '部活動を選択';
  option.defaultSelected = true;
  option.disabled = true;
  type.appendChild(option);  
  
  clubType[selectType].forEach( menu => {
    let option = document.createElement('option');
    option.innerHTML = menu;
    type.appendChild(option);  
  });    
}
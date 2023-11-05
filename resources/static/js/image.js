 var cot = 0;
 function nex(size) {
      if (cot < size-1) {
        $('.imgs img').eq(cot).animate({ 'margin-left': '-700px' }, 700);
        cot++;
      }
    }
 function pre() {
      if (cot > 0) {
        cot--;
        $('.imgs img').eq(cot).animate({ 'margin-left': '0' }, 700);
      }
 }
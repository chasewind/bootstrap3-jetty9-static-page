
$(function(){
	//自定义弹幕组件
	//第三方js：zepto.js 和zepto的动画 fx.js
	Barrage = {};
	Barrage.elems = null;
	Barrage.w = $("#commentArea").width();
	Barrage.dataLen = 0;
	//data格式
//	var data = [
//	        	{imgUrl: "http://images11.app.happyjuzi.com/usericonurl/14331496184735.jpg", name:"", text: "哈哈哈哈逗死了吧"},
//	        	{imgUrl: "http://images11.app.happyjuzi.com/usericonurl/14372808138692.jpg", name:"", text: "胖胖们"},
//	        ];
	Barrage.setData=function(data){
		if(data &&data.length>0){
			Barrage.data=data;
			//清空DIV
			$(".commentScroll").empty();
			var html = [];
			var len = data.length;
			Barrage.dataLen =len;
			for(var i=0;i<len;i++){
				html.push('<a class="cItem" id="citem_'+i+'">');
				//填充图片
				if(!data[i].imgUrl) {
					html.push('<div class="tcImg"><img src="http://images11.app.happyjuzi.com/usericonurl/14372694871940.jpg"></div>');
				}else {
					html.push('<div class="tcImg"><img src="'+data[i].imgUrl+'"></div>');
				}
				//填充name
				if(!data[i].name) {
					html.push('<div class="tcText">');
				}else {
					html.push('<div class="tcText"><span class="itemName">'+data[i].name+': </span>');
				}
				//填充text
				if(!data[i].text) {
					html.push('</div></a>');
				}else {
					html.push('<span class="itemText">'+data[i].text+'</span></div></a>');
				}
			}
			$(".commentScroll").append(html.join(" "));
		}
	}
	Barrage.getData=function(){
		return Barrage.data;
	};
	Barrage.getElems=function(){
		Barrage.elems = $(".cItem");
		return Barrage.elems;
	}
	Barrage.setTop=function(){
		if(!Barrage.elems){
			Barrage.getElems();
		}
		Barrage.elems.each(function(index, item){
			var e = $(item);
			if((index+1)%3 == 1){
				e.css({"top":Barrage.getRandomNum(5, 25)});	
			}else if((index+1)%3 == 2){
				e.css({"top":Barrage.getRandomNum(40, 60)});	
			}else if((index+1)%3 == 0){
				e.css({"top":Barrage.getRandomNum(75, 90)});	
			}
					
		});
	}
	
	Barrage.doAnimate=function(){
		var j= 0;
		if(!Barrage.elems){
			Barrage.getElems();
		}
		var len = Barrage.dataLen;
		if(len >0){
			Barrage.elems.css({"left":Barrage.w, "width": Barrage.w});
			Barrage.setTop();
			setInterval(function(){
				var r = Math.floor(Math.random()*2)+1; //[1,3]   每次出现几个弹幕
				if(j == len || j > len) {j = 0;}
				if(j < len){
					for(var k = 0;k < r;k++){
						$(Barrage.elems[j]).show().animate({"left": -Barrage.w},Barrage.getRandomNum(5,11)*1000, "ease-in", function(){
							$(Barrage.elems[j]).css({"left": Barrage.w});
						});				
						j++;
					}			
				}
			}, 1500);
			
		}
	}
	Barrage.getRandomNum=function (Min, Max) {
	    var Range = Max - Min;
	    var Rand = Math.random();
	    return (Min + Math.round(Rand * Range));
	}
	
	
});
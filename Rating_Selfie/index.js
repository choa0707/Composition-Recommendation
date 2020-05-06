const express = require("express");
const app = express();
var mysql = require("mysql");
app.engine('html',require('ejs').renderFile);
app.set('view engine','ejs');
app.use(express.static(__dirname+'/'));
var connection = mysql.createConnection({
	host : "chw9603.cafe24.com",
	port : 3306,
	user : "chw9603",
	password : "qlraor3500dnjs",
	database : "chw9603",
	typeCast: function(field, next){
		if(field.type=='VAR_STRING'){
			return field.string();
		}
		return next();
	}
})

app.get("/", function(req,res){
	console.log("테스트");

	res.render('simpleweb.html');
})
app.get("/test",function(req,res){
	res.render('test.html');
})
app.get("/rating",function(req,res){
	var insertquery = "insert into Image (image_name, rate)VALUES('"+req.query.img+"','"+req.query.rating+"');";
	console.log(insertquery);
	connection.query(insertquery, function(error,result,fields){
		if (error){
			console.log(error);
		}else{
			console.log("insertion success");	
		}
	
	});


	res.send("successs");

})
app.listen(3000, function(){
	console.log("실행중..");
});

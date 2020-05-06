/*jslint devel: true */ 
/* eslint-disable no-console */ 
/*eslint no-undef: "error"*/ 
/*eslint-env node*/
//172.19.83.109
//IP주소가 변화하면 안드로이드 앱 내에 있는 url 주소도 바꿔주어야 정상 동작하기시작함!


var express = require('express');
var http = require('http');
var bodyParser= require('body-parser');
var app = express();
var pythonShell = require("python-shell")
var options;
var options2;
app.set('port',process.env.PORT || 4000);
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());
var router = express.Router();

//첫 번째 미들웨어
app.use(function(req, res, next) {

    //var approve ={'approve_id':'NO','approve_pw':'NO'};

    var score = req.body.score;
    var lex = req.body.lex;
    var ley = req.body.ley;
    var rex = req.body.rex;
    var rey = req.body.rey;
    var nox = req.body.nox;
    var returnscore;
    var noy = req.body.noy;
    var lmx = req.body.lmx;
    var lmy = req.body.lmy;
    var temp;
    var rmx = req.body.rmx;
    var minus = 0;
    var rmy = req.body.rmy;
    var chx = req.body.chx;
    var chy = req.body.chy;
    var roll = 0;
    var pitch = 0;
    var yaw = 0;
    var resultt;
    var ratio = req.body.ratio;
    var returnV = {'roll':'0','yaw':'0','pitch':'0','score':'-1', 'help':'0'}
    var result = (score+","+lex+", "+ley+", "+rex+", "+rey+", "+nox+", "+noy+", "+lmx+", "+lmy+", "+rmx+", "+rmy+", "+chx+", "+chy);
    options = {
		mode: 'text',
		pythonPath : '',
		pythonOptions : [],
		scriptPath: '',
		args: [lex,ley,rex,rey,nox,noy,lmx,lmy,rmx,rmy,chx,chy,ratio]
	};
	pythonShell.PythonShell.run('pose_estimation.py',options,function(err,result){
		if(err)
		{
			console.log(err);
			res.send(err);
		}
		else
		{
			roll = pitch=yaw = 0;
			resultt = result[0].split(' ');
			ratio = resultt[0];
			roll = resultt[1];
			pitch = resultt[2];
			yaw = resultt[3];
			options2 = {
		            mode: 'text',
	     		   pythonPath : '',
			    pythonOptions : [],
			    scriptPath: '',
	    			args: [ratio, roll, pitch, yaw]
		        };
			returnV.roll=roll;
			returnV.pitch = pitch;
			returnV.yaw = yaw;
			//console.log(ratio+", "+roll+", "+pitch+", "+yaw);
			//
			if (score == 1){
				if ((parseInt(pitch) >= 40)||(parseInt(roll) >= 70)||(parseInt(yaw) >= 70)) {
					returnV.score = '1';
					console.log(returnV.score);
					res.send(returnV);
				}
				else{
					pythonShell.PythonShell.run('./learning/model.cpython-36.pyc', options2, function(err,result2){
						if(err)
						{
							console.log(err);
							res.send(err);
						}
						else{
							temp = result2[0].split(' ');
							returnscore = parseInt(temp[1]);
							returnscore -= minus;
							console.log(returnscore);
							if (returnscore < 0 ) returnscore = 1;
							returnV.score=String(returnscore);
							res.send(returnV);
						}
					});
				}
			}
			else {
				console.log(resultt);
				res.send(returnV);	
			}
		}
	});

    //아이디 일치여부 flag json 데이터입니다.
    //if(paramId == 'test01') approve.approve_id = 'OK';
    //if(paramPassword == '123') approve.approve_pw = 'OK';
    //console.log(result);

});

var server = http.createServer(app).listen(app.get('port'),function(){
   console.log("익스프레스로 웹 서버를 실행함 : "+ app.get('port')); 
});

const pythonshell = require('python-shell')
const fs = require('fs')
var multipart = require('multiparty');
var path = require('path');

var findUser = function (req, res) {
	console.log(req.route)
	console.log(req.body);

	var database = req.app.get('database'); //app.set으로 설정해주기
	var user_id = req.body.user_id
	var user_password = req.body.user_password

	if (database.db) {

		database.UserModel.find({ user_id: user_id, user_password: user_password }, function (err, results) {
			if (err) {
				console.log("findUser : error ")
				return;
			}
			if (results.length > 0) {
				console.log('findUser : user found');
				res.send({ "result": true })
			} else {
				console.log("findUser : user not found");
				res.send({ "result": false })
			}
		});

	}
	else {
		console.log("database disconnected")
		res.send({ "result": false })
	}

};

var findCar = function (req, res) {
	var car_id = req.body.car_id
	var database = req.app.get('database');

	console.log(req.route)
	console.log(req.body);

	if (database.db) {


		database.CarModel.find({ car_id: car_id, on_rent: false }, function (err, results) {
			if (err) {
				console.log("findCar : error")
				return;
			}

			if (results.length > 0) {

				console.log('findCar : car found');
				res.send({ "result": true, "car_type": results[0].car_type })
			} else {
				console.log("findCar : car not found");
				res.send({ "result": false })
			}
		})

	} else {
		console.log("database disconected")
		res.send({ "result": false })
	}

};


var findRentHistory = function (req, res) {

	var user_id = req.body.user_id
	var database = req.app.get('database');

	console.log(req.route)
	console.log(req.body);

	if (database.db) {

		database.RentHistoryModel.find({ "user_id": user_id }, 'rent_id car_id rent_date return_date returned', function (err, results) {

			if (err) {
				console.log("findRentHistory : error")
				return;
			}

			if (results.length > 0) {
				console.log('findRentHistory :rent history found');
				res.send({ "result": true, "history": results })

			} else {
				console.log('findRentHistory :no rent history');
				res.send({ "result": false })
			}
		})

	} else {
		console.log("database disconected")
		res.send({ "result": false })
	}

}

var addRentHistory = function (req, res) {

	console.log("addRentHistory called")
	var database = req.app.get('database')

	database.RentHistoryModel.find(function (err, results) {

		var user_id = req.body.user_id;
		var car_id = req.body.car_id
		var rent_id = results.length;
		var rent_date = (new Date()).format('yyyy-MM-dd(ES) HH:mm:ss')

		console.log(req.route)
		console.log(req.body);


		if (database.db) {

			database.UserModel.find({ "user_id": user_id }, 'renting', function (error, results) {
				if (results.length > 0) {
					if (results[0].renting == false) {
						var newRent = new database.RentHistoryModel({ "rent_id": rent_id, "user_id": user_id, "car_id": car_id, "rent_date": rent_date, "return_date": null, "returned": false })
						newRent.save(function (err) {

							if (err) {
								console.log("addRentHistory :error");
								console.log(err)
								res.send({ "result": false })
								return;
							}

							if (newRent) {
								console.dir("rent history added : " + rent_id);
								res.send({ "result": true, "rent_id": rent_id })
								database.UserModel.findOneAndUpdate({ "user_id": user_id },
									{ $set: { 'renting': true, "current_rent_id": rent_id, "current_car_id": car_id } },
									() => {
										console.log("addRentHistory : user " + user_id + " starts renting a car")
									})

								database.CarModel.findOneAndUpdate({ "car_id": car_id },
									{ $set: { 'on_rent': true } },
									() => {
										console.log("addRentHistory : car " + car_id + " on rent")
									})
									
							} else {
								console.dir("addRentHistory :rent history not added")
								res.send({ "result": false, "renting": false })
							}
						})
					} else {
						console.dir("addRentHistory : already renting a car")
						res.send({ "result": false, "renting": true })
					}
				} else {
					console.dir("addRentHistory : user not found")
					res.send({ "result": false })
				}
			})

		} else {
			console.log("addRentHistory : database disconected")
			res.send({ "result": false })
		}
	})

}

var runYoloPy = function (req, res) {

	var database = req.app.get('database');
	console.log(req.route)

	if (database.db) {
		var form = new multipart.Form();
		form.parse(req, function (err, fields, files) {
			console.log(files)
			console.log(fields)

			var rent_id = fields.rent_id[0]
			var before_after = fields.before_after[0]
			const pythonshell = require('python-shell')
			const fs = require('fs')

			database.PhotoModel.find({ "rent_id": rent_id, "before_after": before_after }, function (err, results) {

				if (err) {
					console.log("runYoloPy : " + err)
					res.send({ result: false });
				} else if (results.length > 0) {

					function sleep(log, sec) {
						return new Promise(function (resolve, reject) {
							setTimeout(function () {
								console.log(log);
								resolve();
							}, sec);
						});
					}

					function runyolo(part, timeout) {
						return new Promise(resolve =>
							setTimeout(() => {

								var extention = "jpg";
								var filename = rent_id + "_" + part + '_' + before_after + '.' + extention
								var options = { mode: 'text', pythonPath: "", pythonOptions: ['-W ignore'], scriptPath: '', args: [before_after, filename] };

								pythonshell.PythonShell.run('yolo.py', options, function (err, results) {
									if (err) {
										console.log("runYoloPy : " + err);
									}

									console.log("runYoloPy : python process running ...")

									var resultpath = './results/yolo/' + filename.split('.').slice(0)[0] + '.json';

									let rawdata = fs.readFileSync(resultpath);
									let yoloresult = JSON.parse(rawdata);

									database.PhotoModel.findOneAndUpdate({ "rent_id": rent_id, "before_after": before_after, "part": part },
										{ $set: { 'defects': yoloresult.predictions } },
										() => {
											console.log("runYoloPy : yolo result saved in database")
										})

								});
								resolve();
							}, timeout));
					}

					async function loop() {

						for (part of ['ff', 'ft', 'bf', 'bt', 'lf', 'lb', 'rf', 'rb']) {
							if (part == 'ff') {
								await runyolo(part, 10);
							} else {
								await (runyolo(part, 18000))
							}
						}

						await sleep("runYoloPy : yolo process completed : "+new Date().toString(), 50000)
						await res.send({ "result": true })

					}
					loop();

				} else {
					console.log("runYoloPy : photos not found")
					res.send({ result: false });
				}
			})
		}) 
	}else {
		console.log("database disconected")
		res.send({ result: false });
	}


}

var runComparePy = function (req, res) {

	var database = req.app.get('database');
	var rent_id = req.body.rent_id;

	console.log(req.route)
	console.log(req.body);

	if (database.db) {

		database.RentHistoryModel.find({ "rent_id": rent_id }, function (err, results) {

			if (err) {
				console.log("runComparePy : error")
				console.log(err);
				res.send({ result: false });
			}

			else if (results.length > 0) {

				

				function runcompare(part) {
					return new Promise(resolve =>
						setTimeout(() => {
							var options = { mode: 'text', pythonPath: '', pythonOptions: ['-W ignore'], scriptPath: '', args: [rent_id, part] };
							//console.log(rent_id)
							//console.log(part)
							pythonshell.PythonShell.run('compare.py', options, function (err, results) {
								if (err) {
									console.log("runComparePy : " + err);
								}
								console.log("runComparePy : " + part + " : python process running ...")
								var resultpath = './results/compare/' + rent_id + "_" + part + '.json';
								let rawdata = fs.readFileSync(resultpath);
								let compareresult = JSON.parse(rawdata);

								database.RentHistoryModel.findOneAndUpdate({ "rent_id": rent_id },
									{ $push: { 'new_defects': { "part": part, "defects": compareresult.new_defects } } },
									{ new: true }, (err, result) => {
										console.log("runComparePy : compared result saved in database")
									})

							});
							resolve();
						}, 100));
				}


				async function loop() {

					for (part of ['ff', 'ft', 'bf', 'bt', 'lf', 'lb', 'rf', 'rb']) {
						await runcompare(part);
					}
					await sleep("runComparePy : compare process completed", 2000)
					await res.send({ "result": true })

				}

				loop();

			} else {
				console.log("runComparePy : rentHisory not found")
				res.send({ result: false });
			}

		})
	} else {
		console.log("database disconected")
		res.send({ result: false });
	}

}

var showDetectionResult = function (req, res) {

	var rent_id = req.body.rent_id
	var before_after = req.body.before_after
	var database = req.app.get('database')

	console.log(req.route)
	console.log(req.body);

	if (database.db) {
		database.PhotoModel.find({ "rent_id": rent_id, "before_after": before_after }, 'part defects', function (err, results) {
			if (err) {
				console.log("showDetectionResult : " + err);
				res.send({ result: false })

			} else if (results.length > 0) {
				res.send({ result: true, detections: results });

			} else {
				console.log("showDetectionResult : result not found");
				res.send({ result: false })
			}

		})

	} else {
		console.log("database disconected")
		res.send({ result: false })
	}

}

var showCompareResult = function (req, res) {
	var rent_id = req.body.rent_id
	var database = req.app.get('database')

	console.log(req.route)
	console.log(req.body);

	if (database.db) {
		database.RentHistoryModel.find({ "rent_id": rent_id }, function (err, results) {
			if (err) {
				console.log("showCompareResult : " + err);
				res.send({ result: false })

			} else if (results.length > 0) {
				res.send({ result: true, new_defects: results[0].new_defects });

			} else {
				console.log("showCompareResult : result not found");
				res.send({ result: false })
			}

		})

	} else {
		console.log("database disconected")
		res.send({ result: false })
	}

}

var updateRentHistory = function (req, res) {

	var rent_id = req.body.rent_id;
	var database = req.app.get('database')

	console.log(req.route)
	console.log(req.body);

	if (database.db) {
		database.RentHistoryModel.find({ "rent_id": rent_id }, function (err, results) {
			var user_id = results[0].user_id;
			var car_id = results[0].car_id;

			database.RentHistoryModel.updateOne({ "rent_id": rent_id },
				{ $set: { "return_date": (new Date()).format('yyyy-MM-dd(ES) HH:mm:ss'), "returned": true } }, function () {
					res.send({ result: true })
				})
			database.UserModel.findOneAndUpdate({ "user_id": user_id },
				{ $set: { 'renting': false, "current_rent_id": null, "current_car_id": null, "photos_state": { "before": false, "after": false } } },
				() => {
					console.log("updateRentHistory : user " + user_id + " returnes a car")
				})
			database.CarModel.findOneAndUpdate({ "car_id": car_id },
				{ $set: { 'on_rent': false } },
				() => {
					console.log("updateRentHistory : car " + car_id + " returned")
				})
		});


	} else {
		console.log("database disconected")
		res.send({ result: false })
	}


}

var findUserInfo = function (req, res) {
	var database = req.app.get('database');
	var user_id = req.body.user_id

	console.log(req.route)
	console.log(req.body);

	if (database.db) {
		database.UserModel.find({ "user_id": user_id },'renting current_rent_id current_car_id photos_state', function (err, results) {
			if (results.length == 1) {
				console.log("findUserInfo : user found")
				res.send({ "result": true, "user_info": results[0] })
			} else {
				console.log("findUserInfo : user not found")
				res.send({ "result": false })
			}
		})
	}
}


var upload = function (req, res) {

	var database = req.app.get('database')
	console.log(req.route)

	if (database.db) {
		var form = new multipart.Form();
		form.parse(req, function (err, fields, files) {
			console.log(files)

			function saveimg(file) {
				return new Promise(resolve =>
					setTimeout(() => {

						fs.readFile(file.path, function (err, data) {
							if (err) { console.log("upload : " + err) }
							fs.writeFile('./photos/' + file.originalFilename, data, function (err) {
								if (err) throw err;
								console.log('upload : File saved. ' + file.originalFilename)
							});
						});

						var extension = path.extname(file.originalFilename);
						var basename = path.basename(file.originalFilename, extension);

						var rent_id = basename.split('_')[0]
						var part = basename.split('_')[1]
						var before_after = basename.split('_')[2]
						var photo_id = rent_id + part + before_after

						var newPhoto = new database.PhotoModel({
							"photo_id": photo_id,
							"rent_id": rent_id,
							"path": "./photos/" + file.originalFilename,
							"part": part,
							"before_after": before_after
						})

						newPhoto.save(function (err) {
							if (err) {
								console.log("upload : " + err);
							} else {
								console.log("upload : photo saved : ./photos/" + file.originalFilename)
							}
						})

						if (part == "ff") {

							if (before_after == 'b') {
								database.UserModel.findOneAndUpdate({ "current_rent_id": rent_id },
									{ $set: { "photos_state.before": true} },()=>{
										console.log("upload : before photos uploaded,user info changed")
									})
							}
							if (before_after == 'a') {
								database.UserModel.findOneAndUpdate({ "current_rent_id": rent_id },
									{ $set: { "photos_state.after": true } },()=>{
										console.log("upload : after photos uploaded, user info changed")
									})
							}
						}

						resolve();
					}, 100));
			}

			async function loop() {
				for (key of ['image0', 'image1', 'image2', 'image3', 'image4', 'image5', 'image6', 'image7']) {
					await saveimg(files[key][0])
				}
				await sleep("upload : upload process completed", 100)
				await res.send({ "result": true })
			}

			loop();



		});


	} else {
		res.send({ "result:": false })
		console.log("database disconnected")
	}



}

function sleep(log, sec) {
	return new Promise(function (resolve, reject) {
		setTimeout(function () {
			console.log(log);
			resolve();
		}, sec);
	});
}
Date.prototype.format = function (f) {
	String.prototype.string = function (len) { var s = '', i = 0; while (i++ < len) { s += this; } return s; };
	String.prototype.zf = function (len) { return "0".string(len - this.length) + this; };
	Number.prototype.zf = function (len) { return this.toString().zf(len); };

	if (!this.valueOf()) return " ";
	var weekKorName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
	var weekKorShortName = ["일", "월", "화", "수", "목", "금", "토"];
	var weekEngName = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	var weekEngShortName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
	var d = this;

	return f.replace(/(yyyy|yy|MM|dd|KS|KL|ES|EL|HH|hh|mm|ss|a\/p)/gi, function ($1) {

		switch ($1) {

			case "yyyy": return d.getFullYear(); // 년 (4자리)
			case "yy": return (d.getFullYear() % 1000).zf(2); // 년 (2자리)
			case "MM": return (d.getMonth() + 1).zf(2); // 월 (2자리)
			case "dd": return d.getDate().zf(2); // 일 (2자리)
			case "KS": return weekKorShortName[d.getDay()]; // 요일 (짧은 한글)
			case "KL": return weekKorName[d.getDay()]; // 요일 (긴 한글)
			case "ES": return weekEngShortName[d.getDay()]; // 요일 (짧은 영어)
			case "EL": return weekEngName[d.getDay()]; // 요일 (긴 영어)
			case "HH": return d.getHours().zf(2); // 시간 (24시간 기준, 2자리)
			case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2); // 시간 (12시간 기준, 2자리)
			case "mm": return d.getMinutes().zf(2); // 분 (2자리)
			case "ss": return d.getSeconds().zf(2); // 초 (2자리)
			case "a/p": return d.getHours() < 12 ? "오전" : "오후"; // 오전/오후 구분
			default: return $1;
		}

	});

};



module.exports.findUser = findUser
module.exports.findCar = findCar
module.exports.addRentHistory = addRentHistory
module.exports.findRentHistory = findRentHistory
module.exports.runYoloPy = runYoloPy
module.exports.runComparePy = runComparePy
module.exports.showDetectionResult = showDetectionResult;
module.exports.showCompareResult = showCompareResult
module.exports.updateRentHistory = updateRentHistory
module.exports.findUserInfo = findUserInfo
module.exports.upload = upload
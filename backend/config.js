module.exports = {
	server_port: 3000,
	db_url: 'mongodb://localhost:27017/database',
	db_schemas: [
		{file:'./schema', collection:'users', schemaName:'UserSchema', modelName:'UserModel'},
		{file:'./schema',collection:'cars',schemaName:'CarSchema',modelName:'CarModel'},
		{file:'./schema',collection:'photos',schemaName:'PhotoSchema',modelName:'PhotoModel'},
		{file:'./schema',collection:'rent_history',schemaName:'RentHistorySchema',modelName:'RentHistoryModel'},

	],
	route_info: [
	    {file:'./router', path:'/login', method:'findUser', type:'post'}
		,{file:'./router', path:'/findCar', method:'findCar', type:'post'}
		,{file:'./router', path:'/startRent', method:'addRentHistory', type:'post'}
		,{file:'./router', path:'/completeReturn', method:'updateRentHistory', type:'post'}
		,{file:'./router', path:'/viewHistory', method:'findRentHistory', type:'post'}
		,{file:'./router', path:'/python/yolo', method:'runYoloPy', type:'post'}
		,{file:'./router', path:'/python/compare', method:'runComparePy', type:'post'}
		,{file:'./router', path:'/showResults/yolo', method:'showDetectionResult', type:'post'}
		,{file:'./router', path:'/showResults/compare', method:'showCompareResult', type:'post'}
		,{file:'./router', path:'/checkUserInfo', method:'findUserInfo', type:'post'}
		,{file:'./router', path:'/upload', method:'upload', type:'post'}
		
		
	]
}
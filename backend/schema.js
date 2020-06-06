var mongoose=require('mongoose')

var UserSchema = new mongoose.Schema({
    user_id: {type: String, required: true, unique: true},
    user_password: {type: String, required: true},
    renting:{type:Boolean,default:false},
    current_rent_id:{type:String},
    current_car_id:{type:String},
    photos_state:{before:{type:Boolean,default:false}, after:{type:Boolean,default:false,_id:false,}}
});

var CarSchema=new mongoose.Schema({
    car_id:{type: String, required: true, unique: true},
    car_type:{type: String, required: true},
    on_rent:{type:Boolean,default:false}
});

var RentHistorySchema=new mongoose.Schema({
    rent_id:{type: String, required: true, unique: true},
    user_id:{type: String, required: true,},
    car_id:{type: String, required: true},
    rent_date:{type: String},
    return_date :{type: Object},
    returned:{type:Boolean},
    new_defects:[{part:{type:String}, defects:[{_id:false,"label":String,"topx":Number,"topy":Number,"btmx":Number,"btmy":Number}]}]
});

var PhotoSchema=new mongoose.Schema({
    photo_id:{type: String, required: true, unique: true},
    rent_id:{type:String},
    path: {type: String, required: true, unique: true},
    part:{type:String},
    before_after : {type:String},
    defects:[{_id:false, "label":String,"topx":Number,"topy":Number,"btmx":Number,"btmy":Number}]
});


module.exports.UserSchema=UserSchema
module.exports.CarSchema=CarSchema
module.exports.RentHistorySchema=RentHistorySchema
module.exports.PhotoSchema=PhotoSchema
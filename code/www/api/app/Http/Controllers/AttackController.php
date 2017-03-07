<?php namespace App\Http\Controllers;

use Laravel\Lumen\Routing\Controller as BaseController;
use Illuminate\Support\Facades\DB;
use Illuminate\Http\Request;

class AttackController extends BaseController
{
    public function __construct(){
        header("Content-type: text/html;charset=utf-8"); 
    }
	/**
	 * 搜索目标
	 * 
	 * */
	public function searchTarget(Request $request){
		$userId=$request->input("userId");
		$lat=$request->input("lat");
		$lot=$request->input("lot");
		if($userId==null || $lat==null || $lot==null){
			abort(404);
			return ;
		}
//		$lat=39.993604;
//		$lot=116.484719;
		$lot_min=$lot-0.001;
		$lot_max=$lot+0.001;
		$lat_min=$lat-0.001;
		$lat_max=$lat+0.001;
		
		$data=array();
		
		$num=DB::table("attack_location")->where("user_id",'=',$userId)->count();
		$time=date('Y-m-d H:i:s',time());
		if($num>0){
			$result=DB::table("attack_location")->where("user_id",'=',$userId)->update(['lat'=>$lat,'lot'=>$lot,'flash_time'=>$time]);
		}else{
			$result=DB::table("attack_location")->insert(['user_id'=>$userId,'lat'=>$lat,'lot'=>$lot,'flash_time'=>$time]);
		}
		
		$result=DB::select("select * from attack_location where user_id <> ".$userId." and ".$lot_max.">lot and lot>".$lot_min." and ".$lat_max.">lat and lat>".$lat_min." order by flash_time desc limit 0,5");
		
		$data['code']=0;
		$data['msg']="";
		$data['result']=$result;
		
		return response()->json($data);
	}
    /**
	 * 消灭目标
	 * 
	 * */
	public function boomTarget(Request $request){
		$userId=$request->input("userId");
		$lat=$request->input("lat");
		$lot=$request->input("lot");
		$killerId=$request->input("killerId");
		
		$data=array();
		$time=date('Y-m-d H:i:s',time());
		
		$result=DB::table("attack_event")->insert(['user_id'=>$userId,'lat'=>$lat,'lot'=>$lot,'failure_time'=>$time,'killer_id'=>$killerId]);
		
		$result=DB::table('user_info')->where("id",'=',$killerId)->increment('score');
		$result=DB::table('user_info')->where("id",'=',$userId)->increment('failure');
		$result=DB::table('user_info')->where("id",'=',$userId)->update(['status' => 2]);
		
		$data['code']=0;
		$data['msg']="操作成功";
		$data['result']=$result;
		
		return response()->json($data);
		
		
	}
}

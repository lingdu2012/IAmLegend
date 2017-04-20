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
	 */
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
		
		$result=DB::select("select a.*,b.status from attack_location as a INNER JOIN user_info as b on a.user_id=b.id and b.status=0 and a.user_id <> ".$userId." and ".$lot_max.">a.lot and a.lot>".$lot_min." and ".$lat_max.">a.lat and a.lat>".$lat_min." order by a.flash_time desc limit 0,5");
		
		$data['code']=0;
		$data['msg']="";
		$data['result']=$result;
		
		return response()->json($data);
	}
    /**
	 * 消灭目标
	 * 
	 */
	public function boomTarget(Request $request){
		//获取参数	
		$userId=$request->input("userId");
		$lat=$request->input("lat");
		$lot=$request->input("lot");
		$killerId=$request->input("killerId");
		//检查当前状态
		$result=DB::table("user_info")->where("id",'=',$killerId)->get();
		$status=$result[0]->status;
		if($status == 2){
			abort(404);
			return ;
		}
		//检查是否道具充足
		$result=DB::table("user_info")->where("id",'=',$killerId)->get();
		$tools_num=$result[0]->tools;
		if($tools_num <= 0){
			abort(404);
			return ;
		}
		//进行操作处理
		$data=array();
		$time=date('Y-m-d H:i:s',time());
		//写入攻击事件
		$result=DB::table("attack_event")->insert(['user_id'=>$userId,'lat'=>$lat,'lot'=>$lot,'failure_time'=>$time,'killer_id'=>$killerId]);
		//进行成绩处理
		//攻击者
		$result=DB::table('user_info')->where("id",'=',$killerId)->increment('score');
		$result=DB::table('user_info')->where("id",'=',$killerId)->decrement('tools');
		//被攻击者
		$result=DB::table('user_info')->where("id",'=',$userId)->increment('failure');
		$result=DB::table('user_info')->where("id",'=',$userId)->update(['status' => 2]);
		
		$data['code']=0;
		$data['msg']="操作成功";
		$data['result']=$result;
		
		return response()->json($data);
		
	}
}

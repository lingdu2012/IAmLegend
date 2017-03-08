<?php namespace App\Http\Controllers;

use Laravel\Lumen\Routing\Controller as BaseController;
use Illuminate\Support\Facades\DB;
use Illuminate\Http\Request;

class UserController extends BaseController
{
	
	public function __construct(){
        header("Content-type: text/html;charset=utf-8"); 
    }
    public function register(){
    	$result=array("0"=>'my',"1"=>"time");
		return response()->json($result);

    }
	/**
	 * 初始化用户
	 * 没有则新建
	 * 有则直接返回
	 */
	public function userInit(Request $request){
       
	    $markId=$request->input("markId");
	    $lat=$request->input("lat");
	    $lot=$request->input("lot");
			
		if(!$markId){
			abort(404);
			return ;
		}
		$result=DB::table("user_info")->where("mark_id",'=',$markId)->select('id', 'mark_id','user_name','score','tools','flash_time')->get();
		if(count($result)>0){
			//判断是否赠送道具
			$date=date('Y-m-d',time());
			$prev_date=date('Y-m-d',$result[0]['flash_time']);
			if($date != $prev_date){
				$result[0]['tools']=$result[0]['tools']+1;
				$result[0]['flash_time']=date('Y-m-d H:i:s',time());
				$update_result=DB::table('user_info')->where("id",'=',$result[0]['id'])->update(['tools' => $result[0]['tools'],'flash_time'=>$result[0]['flash_time']]);
			}
			//直接返回信息
			$data['result']=$result;
		}else{
			//注册时间
			$time=date('Y-m-d H:i:s',time());
			$result_id=DB::table("user_info")->insertGetId(['mark_id'=>$markId,'register_time'=>$time,'register_lat'=>$lat,'register_lot'=>$lot,'status'=>0,'tools'=>1,'flash_time'=>$time]);
			
			$data['result']=$result=DB::table("user_info")->where("id",'=',$result_id)->select('id', 'mark_id','user_name','score','tools','flash_time')->get();
		}
		$data['code']=0;
		$data['msg']='';
		
		return response()->json($data);
		
	}
}

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
        //获取参数
		$markId=$request->input("markId");
		$lat=$request->input("lat");
		$lot=$request->input("lot");
			
		if(!$markId){
			abort(404);
			return ;
		}

		//检查用户是否已存在
		$result=DB::table("user_info")->where("mark_id",'=',$markId)->select('id', 'mark_id','user_name','score','tools','flash_time','status')->get();
		//用户存在
		if(count($result)>0){
			//判断当前是否复活
			$istatus=$result[0]->status;
			if($istatus==2){
				$id=$result[0]->id;
				//获取上次被击毙时间
				$attack_log=DB::table("attack_event")->where('user_id','=',$id)->get();
				$failure_time=$attack_log[0]->failure_time;
				$time=time();
				//计算间隔时间
				$deadtime=$time-strtotime($failure_time);
				//如果死亡时间超过1小时，复活！
				if($deadtime>3600){
					$rest=DB::table("user_info")->where("mark_id",'=',$markId)->update(['status'=>0]);
				}
			}
			
			//判断是否赠送道具，每天加1
			$date=date('Y-m-d',time());
			$flash_date=$result[0]->flash_time;
			$prev_date=date('Y-m-d',strtotime($flash_date));
			
			if($date != $prev_date){
				$tools_num=$result[0]->tools;
				$result[0]->tools = $tools_num + 1;
				$result[0]->flash_time = date('Y-m-d H:i:s',time());
				$update_result=DB::table('user_info')->where("id",'=',$result[0]->id)->update(['tools' => $result[0]->tools,'flash_time'=>$result[0]->flash_time]);
			}
			//直接返回信息
			$data['result']=$result;
		}else{//注册新用户
			//注册时间
			$time=date('Y-m-d H:i:s',time());
			$result_id=DB::table("user_info")->insertGetId(['mark_id'=>$markId,'register_time'=>$time,'register_lat'=>$lat,'register_lot'=>$lot,'status'=>0,'tools'=>1,'flash_time'=>$time]);
			
			$data['result']=$result=DB::table("user_info")->where("id",'=',$result_id)->select('id', 'mark_id','user_name','score','tools','flash_time','status')->get();
		}
		$data['code']=0;
		$data['msg']='';
		
		return response()->json($data);
	}
	/**
	 * 获取用户信息
	 * 
	 */
	public function userInfo(Request $request){
		
		$data=array();
		$userId=$request->input("userId");
		//获取用户信息
		$result=DB::table("user_info")->where("id",'=',$userId)->select('id','user_name','score','failure','status','tools','flash_time')->get();
		//判断用户状态，并添加状态展示用语
		$describe=$result[0]->status;

		switch($describe){
			case 0:
				$result[0]->statusDesc ='活跃中';
			break;
			case 1:
				$result[0]->statusDesc ='被锁定';
			break;
			case 2:
				$result[0]->statusDesc ='等待复活';
			break;
			default:
				$result[0]->statusDesc ='活跃中';
				break;
		}
		
		$data['code']=0;
		$data['msg']='';
		$data['result']=$result;
		
		return response()->json($data);
	}
}

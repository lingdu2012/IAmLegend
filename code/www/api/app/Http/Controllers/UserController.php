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
		if(!$markId){
			abort(404);
			return ;
		}
		$result=DB::table("user_info")->where("mark_id",'=',$markId)->select('id', 'mark_id','user_name')->get();
		if(count($result)>0){
			//直接返回信息
			$data['result']=$result;
		}else{
			//注册时间
			$time=date('Y-m-d H:i:s',time());
			$result_id=DB::table("user_info")->insertGetId(['mark_id'=>$markId,'register_time'=>$time]);
			$data['result']=array('id'=>$result_id);
		}
		$data['code']=0;
		$data['msg']='';
		
		return response()->json($data);
		
	}
}

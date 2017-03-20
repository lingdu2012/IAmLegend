<?php namespace App\Http\Controllers;

use Laravel\Lumen\Routing\Controller as BaseController;
use Illuminate\Support\Facades\DB;
use Illuminate\Http\Request;

class TipController extends BaseController
{
		
	public function __construct(){
        header("Content-type: text/html;charset=utf-8"); 
    }
    /**
	 * 提示信息生成器
	 * 根据现有数据生成可读性信息
	 * 包括成绩，及公告等。
	 */
	public function tipInfo(){
		
		$result=array("0"=>"欢迎来到这里！");
		
		
		$data['code']=0;
		$data['msg']="";
		$data['result']=$result;
		
		
		return response()->json($data);
	}	
		
}
	
<?php namespace App\Http\Controllers;

use Laravel\Lumen\Routing\Controller as BaseController;

class UserController extends BaseController
{
	
	public function __construct(){
        header("Content-type: text/html;charset=utf-8"); 
    }
    public function register(){
    	$result=array("0"=>'my',"1"=>"time");
		return response()->json($result);

    }
}

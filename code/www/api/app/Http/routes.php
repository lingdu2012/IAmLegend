<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

$app->get('/', function() use ($app) {
    return $app->welcome();
});
/****用户相关api*****/
$app->get('user/register','UserController@register');
//初始化用户信息
$app->get('user/userInit','UserController@userInit');
$app->post('user/userInit','UserController@userInit');
//获取用户信息
$app->post('user/userInfo','UserController@userInfo');
$app->get('user/userInfo','UserController@userInfo');

/****攻击相关api*****/
//搜索目标
$app->post('attack/searchTarget','AttackController@searchTarget');
$app->get('attack/searchTarget','AttackController@searchTarget');
//攻击目标
$app->post('attack/boomTarget','AttackController@boomTarget');
$app->get('attack/boomTarget','AttackController@boomTarget');
/****提示信息相关api*****/
//提示信息
$app->post('tip/tipInfo','TipController@tipInfo');
$app->get('tip/tipInfo','TipController@tipInfo');

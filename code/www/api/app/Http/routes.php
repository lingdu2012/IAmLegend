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
//用户相关api
$app->get('user/register','UserController@register');

$app->get('user/userInit','UserController@userInit');
$app->post('user/userInit','UserController@userInit');
//攻击相关api
$app->post('attack/searchTarget','AttackController@searchTarget');
//攻击相关api
$app->get('attack/searchTarget','AttackController@searchTarget');
//攻击相关api
$app->post('attack/boomTarget','AttackController@boomTarget');
//攻击相关api
$app->get('attack/boomTarget','AttackController@boomTarget');

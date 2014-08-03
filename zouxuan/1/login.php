<?php
$name=$_POST['username'];
$pass=$_POST['password'];
session_start();
require("dbconn.php");

    $arr=mysql_query("select * from user");
    while($result=mysql_fetch_array($arr)){
       if($result['name']===$name&&$result['password']===$pass){
           $_SESSION['username']=$name;
           $_SESSION['password']=$pass;
           $return['result']="success";

           mysql_free_result($arr);
           exit(json_encode($return));
       }
    }

$return['result']="fail";
exit(json_encode($return));

?>
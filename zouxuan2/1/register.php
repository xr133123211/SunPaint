<?php

require("dbconn.php");

$name=$_POST['username'];
$pass=$_POST['password'];

$arr=mysql_query("select * from user");      
while($result=mysql_fetch_array($arr)){
    if($result['name']===$name){
        $return['result']="invalid";
        exit(json_encode($return));
    }
}
mysql_query("insert into user values ('$name','$pass')");
$return['result']="success";
exit(json_encode($return));

?>
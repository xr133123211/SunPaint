<?php
$name=$_POST['username'];
$pass=$_POST['password'];

require("dbconn.php");

    $arr=mysql_query("select * from user");
    while($result=mysql_fetch_array($arr)){
       if($result['name']===$name&&$result['password']===$pass){
           $return['result']="success";
           $_SESSION['username']=$name;
           mysql_free_result($arr);
           exit(json_encode($return));
       }
    }

$return['result']="fail";
exit(json_encode($return));

?>
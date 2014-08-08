<?php
session_start();

require('dbconn.php');

$name=$_SESSION['username'];
$data=$_POST['data'];
$owner=$_POST['owner'];
$owner_time_s=$_POST['ownertime'];
$owner_time=strtotime($owner_time_s);
$reviewer=$name;

$reviewer_time=time();

mysql_query("insert into review values('$owner','$owner_time','$reviewer','$reviewer_time','$data')");

$result['time']=date('Y-m-d H:i:s',$reviewer_time);
$result['name']=$name;

exit (json_encode($result));



?>

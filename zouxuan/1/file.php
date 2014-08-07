<?php
session_start();

require("dbconn.php");

$name=$_SESSION['username'];

$user=$name;
$storage = new SaeStorage();
$domain = 'zouxuan';
$destFileName = $_FILES['file']['name'];
$srcFileName = $_FILES['file']['tmp_name'];
$s= $storage->upload($domain,$destFileName, $srcFileName);

$time=time();

mysql_query("insert into image values('$name','$time','$destFileName')");

$data=$_POST['data'];

mysql_query("insert into record values ('$name','$time','$data')");

if($s===false){
    $result['result'] ='fail';
}
else{
    $result['result']='success';
}
exit (json_encode($result));

?>
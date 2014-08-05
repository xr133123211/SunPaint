<?php
require("dbconn.php");

$name=$_SESSION['username'];
$storage = new SaeStorage();
$domain = 'zouxuan';
$destFileName = $_FILES['file']['name'];
$srcFileName = $_FILES['file']['tmp_name'];
$s= $storage->upload($domain,$destFileName, $srcFileName);

$time=time();

mysql_query("insert into image values('$name','$time','$destFileName')");

$data=$_POST['data'];

mysql_query("insert into record values ('$name','$time','$data')");

//$_SESSION['username']

if($s===false){
    $result['result'] ='fail';
}
else{
    $result['result']='success';
}

exit (json_encode($result));

?>  
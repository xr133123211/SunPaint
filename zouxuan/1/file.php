<?php
require("dbconn.php");

$storage = new SaeStorage();
$domain = 'zouxuan';
$destFileName = $_FILES['file']['name'];
$srcFileName = $_FILES['file']['tmp_name'];
$s= $storage->upload($domain,$destFileName, $srcFileName);

$time=time();

mysql_query("insert into image values ('zouxuan','$time','$destFileName')");

$data=$_POST['data'];

mysql_query("insert into record values ('zouxuan','$time','$data')");

//$_SESSION['username']

if($s===false){
    $result['result'] ='fail';
}
else{
    $result['result']='success';
}

exit (json_encode($result));

?>  
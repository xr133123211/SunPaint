<?php
session_start();
if(!isset($_SESSION['username'])) {
    $result['result']='unlogin';
}
else{
    $result['result']=$_SESSION['username'];
}
exit (json_encode($result));

?>
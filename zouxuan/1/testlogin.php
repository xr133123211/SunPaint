<?php

if(!isset($_SESSION['username'])) {
    $result['result']='unlogin';
}
else{
    $result['result']='login';
}
exit (json_encode($result));

?>
<?php

$owner=$_GET['owner'];
$owner_time_s=$_GET['ownertime'];

require('dbconn.php');
$sql=mysql_query("select * from review");
$i=0;

$owner_time=strtotime($owner_time_s);



while($arr=mysql_fetch_array($sql)){

    if($owner==$arr['owner']&$owner_time==$arr['owner_time']){

        $result[$i]['reviewer']=$arr['reviewer'];
        $result[$i]['reviewer_time']=$arr['reviewer_time'];
        $result[$i]['data']=$arr['data'];
        $i++;

    }
}
exit (json_encode($result));

?>
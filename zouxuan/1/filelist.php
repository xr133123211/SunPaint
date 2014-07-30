<?php

require('dbconn.php');

$stor = new SaeStorage();

$sql = mysql_query("select * from record");
$sql_2=mysql_query("select * from image");

$i=0;

while($arr=mysql_fetch_array($sql)){
    $result[$i]['name']=$arr['username'];
    $result[$i]['time']=date('Y-m-d H:i:s',$arr['time']);
    $result[$i]['data']=$arr['text'];
    mysql_data_seek($sql_2,0);
    while($arr_2=mysql_fetch_array($sql_2)){
        if($arr_2['username']===$arr['username']&$arr_2['time']===$arr['time']){
            $result[$i]['imagepath']=$stor->getUrl("zouxuan",$arr_2['imagepath']);
        }
    }
    $i++;
}

exit (json_encode($result));
?>

<?php
function example(){
    static $a=10;
    $a+=1;
    echo "a: ".$a."<br>";

}
example();
?>


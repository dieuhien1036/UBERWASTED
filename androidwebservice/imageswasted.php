<?php
    $connect = mysqli_connect("localhost","root","","volunteer");
    mysqli_query($connect,"SET NAMES 'utf8'");

    $query ="Select * from imageswasted";
    $data= mysqli_query($connect,$query);

    class wasted{
        function wasted($id, $image_name, $image_url,$wasteLocation_latitude,$wasteLocation_longtitude,$wasteLocation_address,$people,$size,$material,$mota,$caption){
            $this->id = $id;
            $this->image_name = $image_name ;
            $this->image_url = $image_url ;
            $this->wasteLocation_latitude = $wasteLocation_latitude ;
            $this->wasteLocation_longtitude = $wasteLocation_longtitude ;
            $this->wasteLocation_address = $wasteLocation_address ;
            $this->People = $people ;
            $this->size = $size ;
            $this->mota = $mota ;
            $this->caption = $caption ;
        }
    }
    $arrayWasted = array();
    while($row = mysqli_fetch_assoc($data)){
        array_push($arrayWasted, new wasted($row['id'] , $row['image_name'], $row['image_url'], $row['wasteLocation_latitude'], $row['wasteLocation_longtitude']
        , $row['wasteLocation_address'] ,$row['people'], $row['size'],$row['material'] , $row['mota'] ,$row['caption'] ));
    }
    echo json_encode($arrayWasted);
?>

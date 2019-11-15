<?php
	
	$connect = mysqli_connect("localhost","root","","volunteer");
	mysqli_query($connect,"SET NAMES 'utf8'");

	$user_ID = $_POST['user_ID'];
	$feedback_content = $_POST['feedback_content'];
	$feedback_status = $_POST['feedback_status'];
	$feedback_date = $_POST['feedback_date'];


	$query = "INSERT INTO feedback VALUES(null,'$user_ID', '$feedback_content', 
					'$feedback_status', '$feedback_date')";

	mysqli_query($connect,$query);
	if(mysqli_query($connect,$query)){
		echo "success";	
	}else{
		echo "fail";
	}
?>
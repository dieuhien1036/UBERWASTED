<?php  

	class volunteers{
		function volunteers($user_ID, $user_email, $user_password, $user_firstname, 
			$user_lastname, $user_dateOfBirth, $user_phoneNumber, $user_job, 
			$user_imageID, $user_gender){
			$this->user_ID = $user_ID;
			$this->user_email = $user_email;
			$this->user_password = $user_password;
			$this->user_firstname = $user_firstname;
			$this->user_lastname = $user_lastname;
			$this->user_dateOfBirth = $user_dateOfBirth;
			$this->user_phoneNumber = $user_phoneNumber;
			$this->user_job = $user_job;
			$this->user_imageID = $user_imageID;
			$this->user_gender = $user_gender;
		}
	}

	$connect = mysqli_connect("localhost","root","","volunteer");
	mysqli_query($connect,"SET NAMES 'utf8'");
	$query = "SELECT * FROM volunteers";
	$data = mysqli_query($connect,$query);

	$array_volunteers = array();
	
	while($row = mysqli_fetch_assoc($data)){
		array_push($array_volunteers,new volunteers($row['user_ID'],$row['user_email'],$row['user_password'],$row['user_firstname'],$row['user_lastname'],$row['user_dateOfBirth'],$row['user_phoneNumber'],$row['user_job'],$row['user_imageID'],$row['user_gender']));
	}
 	

   	echo json_encode($array_volunteers);


?>

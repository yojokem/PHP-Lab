<?php
	public class DB
	{
		//Made By Minsung Kim.
		private $conn_type = strtolower("mysql");
		private $conn = "";

		public function __construct($host, $user, $pass, $name, $sql_type)
		{
			$sql_type1 = strtolower($sql_type);
			switch($sql_type1)
			{
				case "mysql":
					$this->conn_type = $sql_type1;
					$this->conn = mysql_connect($host, $user, $pass, $name);
					return $this->conn;
				case "mysqli":
					$this->conn_type = $sql_type1;
					$this->conn = mysqli_connect($host, $user, $pass, $name);
					return $this->conn;
				default:
					return false;
			}

			return false;
		}

		public function connect($host, $user, $pass, $name, $sql_type)
		{
			$sql_type1 = strtolower($sql_type);
			switch($sql_type1)
			{
				case "mysql":
					$conn = mysql_connect($host, $user, $pass, $name);
					return $conn;
				case "mysqli":
					$conn = mysqli_connect($host, $user, $pass, $name);
					return $conn;
				default:
					return false;
			}

			return false;
		}

		public function disconnect($sql_type, $conn)
		{
			$sql_type1 = strtolower($sql_type);
			switch($sql_type1)
			{
				case "mysql":
					$close = mysql_close($conn);
					return $close;
				case "mysqli":
					$close = mysqli_close($conn);
					return $close;
				default:
					return false;
			}

			return false
		}

		public function cmd($sql)
		{
			switch($this->conn_type)
			{
				case "mysql":
					$result = mysql_query($conn, $sql);
					return $result;
				case "mysqli":
					$result = mysqli_query($conn, $sql);
					return $result;
				default:
					return false;
			}

			return true;
		}
	}
?>

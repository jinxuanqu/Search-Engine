<?php
	include 'info.php';
	$connection = ssh2_connect($host, $port);
	ssh2_auth_password($connection, $username, $password);
	$tmp = explode(" ",$input);
	$input = "";
	foreach($tmp as $each){
		$input .=$each."-";
	}
	$input = substr($input,0,strlen($input)-1);	
	$stream = ssh2_exec($connection, "cp \$HADOOP_HOME/copy_search_region.sh \$HADOOP_HOME/search.sh");
	$stream = ssh2_exec($connection, "perl -pi -e 's/&&&search_word&&&/$input/g' \$HADOOP_HOME/search.sh");
	$stream = ssh2_exec($connection, "perl -pi -e 's/&&&search_page&&&/$page/g' \$HADOOP_HOME/search.sh");
	
	//$stream = ssh2_exec($connection, "\$HADOOP_HOME/search.sh");

	//sleep(5);//it seems necessary to do this

	$stream = ssh2_exec($connection, "cp \$HADOOP_HOME/input/doc_detail.xml /home/chaoh/Documents/881/website/data/first.xml");

	//sleep(30);

	//return result
	/*
		$sftp = ssh2_sftp($connection);
		$remote = fopen("ssh2.sftp://$sftp/home/chaoh/Documents/hadoop/hadoop-1.2.1/input/doc_detail.xml", 'rb');
		$local = fopen('/home/chaoh/Documents/881/website/data/first.xml', 'w');
		while(!feof($remote)){
		    fwrite($local, fread($remote, 8192));
		}
		fclose($local);
		fclose($remote);
	*/
	include 'xmlParse.php';
	
	/*$sentence = "hello world";
	$each = "hello";
	$replacement = "hahaha";

	$abstract = str_replace($each, $replacement, $sentence);
	echo $abstract;*/

	sleep(5);
?>
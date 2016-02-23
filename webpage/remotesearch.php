<?php
	//include the basic information
	include 'info.php';
	
	//login authrized, change your password and test if you want
	$connection = ssh2_connect($host, $port);
	ssh2_auth_password($connection, $username, $password);

	//get the host node's name
	$stream = ssh2_exec($connection, "cat \$HADOOP_CONF_DIR/masters");
	stream_set_blocking( $stream, true );
	$hostNode = stream_get_contents($stream);
	//echo $hostNode;
	

	// login to the host node
	$stream = ssh2_exec($connection, "ssh $hostNode");
	
	//copy the old file
	$stream = ssh2_exec($connection, "cp \$HADOOP_HOME/demo/copy_search.sh \$HADOOP_HOME/demo/search.sh");
	$stream = ssh2_exec($connection, "perl -pi -e 's/&&&search_word&&&/$input/g' \$HADOOP_HOME/demo/search.sh");
	$stream = ssh2_exec($connection, "perl -pi -e 's/&&&search_page&&&/$page/g' \$HADOOP_HOME/demo/search.sh");

	// do the search
	$stream = ssh2_exec($connection, "\$HADOOP_HOME/demo/search.sh");
	
	// it shoudl wait for some time
	sleep(10);

	
	$sftp = ssh2_sftp($connection);

	$remote = fopen("ssh2.sftp://$sftp/home/chaoh/software/hadoop-1.2.1/final_output/doc_detail.xml", 'rb');
	
	$local = fopen('/home/chaoh/Documents/881/website/data/first.xml', 'w');

	while(!feof($remote)){
	    fwrite($local, fread($remote, 8192));
	}

	fclose($local);

	fclose($remote);
	include 'xmlParse.php';
?>
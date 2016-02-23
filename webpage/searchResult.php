<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Search Result</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="style.css" rel="stylesheet" type="text/css" media="screen" />
</head>
<?php 
	$input = $_GET['Ori_String'];
	$page = 1;
	// run the hadoop in localhost
	include 'localsearch.php';

	// run the hadoop in palmetto 
	//include 'remotesearch.php';
?>
<body>
<div id="wrapper">
	<div id="header">
		<div id="menu">
			<ul>
				<li class="current_page_item"><a href="#">881 Final Project</a></li>
			</ul>
		</div>
		<!-- end #menu -->
		<div id="search">
			<form method="get" action="searchResult.php">
				<fieldset>
				<input type="text" name="Ori_String" id="inputword" size="15" />
				<input type="submit" id="btn1" value="do search" />
				</fieldset>
			</form>
		</div>
		<!-- end #search -->
	</div>
	<!-- end #header -->
	
<!-- end #header-wrapper -->


<!-- <form action = "nextpage.php" method="GET">
	<input type="hidden" name="Ori_String"  value="<?php echo $input;  ?>"></input>
	<input type="hidden" name="PageNumber"  value="<?php echo $page+1; ?>"></input>
	<input type = "submit" value ="next"></input>
</form> -->


<div id="page">
	<div id="content">
		<div class="post">
			<div class="entry">
				<p class="links"><a href="#" class="comments"><?php echo $titleArray[0];?></a> &nbsp;&nbsp;&nbsp;</p>
				<p><?php echo $abstractArray[1]; ?></p>
			</div>
			<div class="entry">
				<p class="links"><a href="#" class="comments"><?php echo $titleArray[1];?></a> &nbsp;&nbsp;&nbsp; </p>
				<p><?php echo $abstractArray[2]; ?></p>
			</div>
			<div class="entry">
				<p class="links"><a href="#" class="comments"><?php echo $titleArray[2];?></a> &nbsp;&nbsp;&nbsp;</p>
				<p><?php echo $abstractArray[3]; ?></p>
			</div>
			<div class="entry">
				<p class="links"><a href="#" class="comments"><?php echo $titleArray[3];?></a> &nbsp;&nbsp;&nbsp;</p>
				<p><?php echo $abstractArray[4]; ?></p>
			</div>
			<div class="entry">
				<p class="links"><a href="#" class="comments"><?php echo $titleArray[4];?></a> &nbsp;&nbsp;&nbsp;</p>
				<p><?php echo $abstractArray[5]; ?></p>
			</div>
		￼</div>
		</div>
	
	
	
	<div style="clear: both;">&nbsp;</div>
</div>
<!-- end #page -->

<div id="footer">
	<p>Chao Huang, Di Zhang and Jinxuan Qu</p>
</div>
<!-- end #footer -->
</div>
</body>
</html>

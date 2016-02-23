<?php
ini_set ('display_errors', true);
error_reporting (E_ALL | E_STRICT);
$dom = new DOMDocument();
$dom->load("data/first.xml");
$titleArray = array();
$abstractArray = array();
$count = 0;

$xpath = new DOMXPath($dom);
$elements = $xpath->query("/docs/pp");

$pattern = array();
//$input .= "-CANADA";
$words = explode("-",$input);
//print_r($words);
$count = 0;
foreach($words as $each){
    $pattern[$count] = '/'.$each.'/';
    $count ++;
}
$count = 0;
if(!is_null($elements)) {
  foreach($elements as $element) {
    $nodes = $element->childNodes;
    foreach ($nodes as $node) {
      if ($node->nodeName == "title"){
          $titleArray[$count] = $node->nodeValue;
          $count = $count +1;
      }
      if($node->nodeName == "detail"){
        $abstract = $dom->saveXML($node);
        $sentences = explode(".", $abstract);
        $abstractArray[$count] = '';
        foreach ($sentences as $sentence) {
          foreach($words as $each){
            $replacement = '<font color="red" style="strong" size="3pt">'.$each.'</font>';
            if((strpos($sentence, $each) !== false) && (strlen($abstractArray[$count]) < 500)){
              $abstract = str_replace($each, $replacement, $sentence);
              $abstractArray[$count] .= $abstract;
            }
          }
        }
      }
    }
  }
}
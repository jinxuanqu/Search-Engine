/**
 * Process:
 * 1.Map: 
 * 			match with search words and key(word), 
 * 			if match
 * 			output: DocNo.	PositionList
 * 2.Reduce:
 * 			combine the key with same DocNo.	
 * 			output:	DocNo.	PositionList
 * 
 * @author	Jinxuan Qu
 * @email: jinxuaq@g.clemson.edu
 * 
 */
package hadoop.jinxuaq.biSearch;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Search {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, Text> {
		// Input: word1 word2 Sum-[DocNo1:Position1,][DocNo2:Postion1]
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			Configuration conf = context.getConfiguration();
			String searchString = conf.get("searchString");
			String[] indexString = value.toString().split("\t");
			String[] searchSplit = searchString.split(" ");
			for (String val : searchSplit) {
				if (indexString[0].indexOf(val) >= 0) {
					String[] sumDocNoPositionSplit = indexString[1].split("-");
					// System.out.println("@@DocNoPositionList:"+sumDocNoPositionSplit[1]);
					String[] docNoPositionListSplit = sumDocNoPositionSplit[1]
							.split("]");
					// System.out.println("** DocLength: "+docNoPositionListSplit.length);
					int i = docNoPositionListSplit.length - 1;
					while (i >= 0) {
						String[] docNoPositionSplit = docNoPositionListSplit[i]
								.split(":");
						String docName = docNoPositionSplit[0];
						String position = docNoPositionSplit[1];
						// Output: DocName PositionList
						context.write(new Text(docName.replace("[", "")),
								new Text(position));
						i--;
					}
				}
			}

		}
	}

	public static class DocReducer extends Reducer<Text, Text, Text, Text> {
		// Combine the same Document No.
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			StringBuffer positionList = new StringBuffer();
			int sum = 0;
			for (Text val : values) {
				sum++;
				positionList.append(val);
			}
			// Output: DocName PositionList
			context.write(key, new Text(positionList.toString()));
			// System.out.println("@@Reduce: "+"key: "+key+"sum: "+sum+"positionList: "+positionList);

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("searchString", args[0]);
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: Search <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "Search");
		job.setJarByClass(Search.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setCombinerClass(DocReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

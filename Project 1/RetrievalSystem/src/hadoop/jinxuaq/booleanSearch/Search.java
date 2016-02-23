/**
 * Process:
 * 1.Map: 
 * 			match with search words and key(word), 
 * 			if match
 * 			output: DocNo. 1
 * 2.Reduce:
 * 			combine the key with same DocNo.	
 * 			output:	DocNo. sum
 * 
 * @author	Jinxuan Qu
 * @email: jinxuaq@g.clemson.edu
 * 
 */
package hadoop.jinxuaq.booleanSearch;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
			Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			Configuration conf = context.getConfiguration();
			String searchString = conf.get("searchString");
			String[] indexString = value.toString().split("\t");
			String[] searchSplit = searchString.split(" ");
			for (String val : searchSplit) {
				if (indexString[0].contains(val)) {
					// Search match successfully.
					String[] sumDocSplit = indexString[1].split(",");
					String[] docSplit = sumDocSplit[1].split("]");
					int i = docSplit.length - 1;
					while (i >= 0) {
						context.write(new Text(docSplit[i].replace("[", "")),
								one);
						// Output: DocNo. 1
						i--;
					}
				}
			}

		}
	}

	public static class DocReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
			// Output: Document No. sum

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
		job.setMapOutputValueClass(IntWritable.class);

		job.setCombinerClass(DocReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setInputFormatClass(TextInputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

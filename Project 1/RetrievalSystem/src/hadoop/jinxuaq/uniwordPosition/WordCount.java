/**
 * Process: 
 * 1.Read Zip format file by ZipFileInputFormat and ZipFileRecordReader
 * 2.Map:
 * 		XML parse
 * 		Output: word-DocNo.			positionList
 * 3.Combine:
 * 		we want to combine the key with the same word and DocNo.
 *		Output:	word 				sum-[DocNo:positionList]
 * 4.Reduce:
 * 		we want to reduce the key with the same word.
 * 		Output: word 			    sum-[DocNo:positionList][DocNo:positionList][DocNo:positionList]
 * 
 * @author	Jinxuan Qu
 * @email: jinxuaq@g.clemson.edu
 */
package hadoop.jinxuaq.uniwordPosition;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.jdom.JDOMException;

public class WordCount {

	enum Counter {
		LINESKIP,
	}

	public static class TokenizerMapper extends
			Mapper<Text, BytesWritable, Text, Text> {
		Text positionList = new Text();

		public void map(Text key, BytesWritable value, Context context)
				throws IOException, InterruptedException {
			XMLParse parser = new XMLParse();
			Text wordDoc = new Text();
			String word = new String();
			try {
				String content = new String(value.getBytes(), "UTF-8");
				String XMLContent = parser.getText(content);
				content = XMLContent.replaceAll("[^A-Za-z \n]", "")
						.toLowerCase();
				StringTokenizer itr = new StringTokenizer(content);
				int postion = 0;
				String docName = key.toString().replace("newsML.xml", "XML");
				while (itr.hasMoreTokens()) {
					word = itr.nextToken();
					wordDoc.set(word + "-" + docName);
					positionList.set(postion + ",");
					context.write(wordDoc, positionList);
					//Output: word-DocNo.	positionList
					postion += word.length() + 1;

				}
			} catch (IOException | InterruptedException | JDOMException e) {
				context.getCounter(Counter.LINESKIP).increment(1);
				e.printStackTrace();
			}
		}
	}

	public static class DocSumCombiner extends Reducer<Text, Text, Text, Text> {
		Text sumDocNoList = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String string = key.toString();
			String[] parts = string.split("-");
			String word = parts[0];
			String docNo = parts[1];
			int sum = 0;
			StringBuffer docList = new StringBuffer();
			for (Text val : values) {
				sum++;
				docList.append(val.toString());
			}
			sumDocNoList.set(sum + "-" + "[" + docNo + ":" + docList + "]");
			context.write(new Text(word), sumDocNoList);
			//Output:	word 	sum-[DocNo:positionList]
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			System.out.println("begin reduce");
			int count = 0;
			StringBuffer fileList = new StringBuffer();
			for (Text val : values) {
				String string = val.toString();
				String[] parts = string.split("-");
				String docSum = parts[0];
				String docList = parts[1];
				count += Integer.parseInt(docSum);
				fileList.append(docList);
			}
			result.set(count + "-" + fileList);
			context.write(key, result);
			////Output:	word 	sum-[DocNo:positionList][DocNo:positionList][DocNo:positionList]
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setCombinerClass(DocSumCombiner.class);
		job.setReducerClass(IntSumReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(ZipFileInputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

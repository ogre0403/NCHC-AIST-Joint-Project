package puf.job;

import puf.combiner.CorrectIDCombiner;
import puf.combiner.HDCombiner;
import puf.mapper.CorrectIDMapper;
import puf.mapper.FlattenMapper;
import puf.mapper.HDwithCorrectIDMapper;
import puf.reducer.AverageHDReducer;
import puf.reducer.CorrectIDReducer;
import puf.reducer.FlattenReducer;
import puf.type.ArrayIntPair;
import puf.type.BytesArrayWritable;
import puf.type.IntPair;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Driver class to launch Intra HD calculation using correct IDs.
 */
public class IntraHD_2 extends Configured implements Tool {

    private Path FLATTEN_INPUT_FILE;
    private Path FLATTEN_OUTPUT_DIR;
    private Path CORRECTID_INPUT_FILE;
    private Path CORRECTID_OUTPUT_DIR;
    private Path HD_INPUT_FILE;
    private Path HD_OUTPUT_DIR;
    private URI CACHE;

    public IntraHD_2(Path flatten_input_file,
                     String flatten_output_dir,
                     String correctid_output_dir,
                     String HD_output_dir) throws URISyntaxException {

        FLATTEN_INPUT_FILE = flatten_input_file;
        FLATTEN_OUTPUT_DIR = new Path(flatten_output_dir);
        CORRECTID_INPUT_FILE = new Path(flatten_output_dir + "/part-r-00000");
        CORRECTID_OUTPUT_DIR = new Path(correctid_output_dir);
        HD_INPUT_FILE = new Path(flatten_output_dir + "/part-r-00000");
        HD_OUTPUT_DIR = new Path(HD_output_dir);
        CACHE = new URI(correctid_output_dir + "/part-r-00000");

    }


    @Override
    public int run(String[] args) throws Exception {
        String job_id = FLATTEN_OUTPUT_DIR.getName();

        // Flatten MR job first
        Job flattenJob = Job.getInstance(getConf(), "Flatten input job ["+job_id+"]");
        flattenJob.setJarByClass(IntraHD_2.class);
        flattenJob.setMapperClass(FlattenMapper.class);
        flattenJob.setReducerClass(FlattenReducer.class);

        flattenJob.setInputFormatClass(KeyValueTextInputFormat.class);
        flattenJob.setMapOutputKeyClass(Text.class);
        flattenJob.setMapOutputValueClass(BytesWritable.class);
        flattenJob.setOutputKeyClass(Text.class);
        flattenJob.setOutputValueClass(BytesArrayWritable.class);
        flattenJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        flattenJob.setNumReduceTasks(1);
        FileInputFormat.addInputPath(flattenJob, FLATTEN_INPUT_FILE);
        FileOutputFormat.setOutputPath(flattenJob, FLATTEN_OUTPUT_DIR);

        if (flattenJob.waitForCompletion(true) == false)
            return 1;

        // Calculat each correct ID
        Job correctIDJob = Job.getInstance(getConf(),"CorrectIDs Job [" + job_id+ "]");
        correctIDJob.setJarByClass(IntraHD_2.class);
        correctIDJob.setMapperClass(CorrectIDMapper.class);
        correctIDJob.setReducerClass(CorrectIDReducer.class);
        correctIDJob.setCombinerClass(CorrectIDCombiner.class);

        correctIDJob.setInputFormatClass(SequenceFileInputFormat.class);
        correctIDJob.setMapOutputKeyClass(Text.class);
        correctIDJob.setMapOutputValueClass(ArrayIntPair.class);

        correctIDJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        correctIDJob.setOutputKeyClass(Text.class);
        correctIDJob.setOutputValueClass(BytesWritable.class);
        correctIDJob.setNumReduceTasks(1);

        FileInputFormat.addInputPath(correctIDJob, CORRECTID_INPUT_FILE);
        FileOutputFormat.setOutputPath(correctIDJob, CORRECTID_OUTPUT_DIR);

        if (correctIDJob.waitForCompletion(true) == false)
            return 1;

        // calculate Hamming distance using correct IDs derived in previous step
        Job HDJob = Job.getInstance(getConf(), "Hamming distance Job with CorrectIDs [" + job_id+"]");
        HDJob.setJarByClass(IntraHD_2.class);
        HDJob.setMapperClass(HDwithCorrectIDMapper.class);
        HDJob.setReducerClass(AverageHDReducer.class);
        HDJob.setCombinerClass(HDCombiner.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(IntWritable.class);
        HDJob.setMapOutputValueClass(IntPair.class);

        HDJob.setOutputKeyClass(IntWritable.class);
        HDJob.setOutputValueClass(DoubleWritable.class);
        HDJob.setNumReduceTasks(1);
        HDJob.addCacheFile(CACHE);

        FileInputFormat.addInputPath(HDJob, HD_INPUT_FILE);
        FileOutputFormat.setOutputPath(HDJob, HD_OUTPUT_DIR);

        return HDJob.waitForCompletion(true) ? 0 : 1;

    }
}

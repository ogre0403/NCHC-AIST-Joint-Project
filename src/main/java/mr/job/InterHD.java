package mr.job;

import mr.combiner.HDCombiner;
import mr.mapper.HDMapper;
import mr.mapper.IdentityMapper;
import mr.reducer.AverageHDReducer;
import mr.reducer.FlattenReducer;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;

/**
 * Driver class to launch Inter HD calculation
 */
public class InterHD extends Configured implements Tool {

    private static Logger logger = Logger.getLogger(InterHD.class);

    private Path MERGE_INPUT_DIR;
    private Path MERGE_OUTPUT_DIR;
    private Path HD_INPUT_FILE;
    private Path HD_OUTPUT_DIR;

    public InterHD(String merge_input_dir,
                   String merge_output_dir,
                   String hd_output_dir){
        MERGE_INPUT_DIR = new Path(merge_input_dir);
        MERGE_OUTPUT_DIR = new Path(merge_output_dir);
        HD_INPUT_FILE = new Path(merge_output_dir + "/part-r-00000");
        HD_OUTPUT_DIR = new Path(hd_output_dir);
    }

    @Override
    public int run(String[] args) throws Exception {

        FileSystem fs = FileSystem.get(getConf());
        FileStatus[] fileStat = fs.listStatus(MERGE_INPUT_DIR);

        // Read all pre-calculated correct IDs files
        // , and merge to single file
        Job mergeJob = Job.getInstance(getConf(), "Merge CorrectIDs Job");
        mergeJob.setJarByClass(InterHD.class);
        mergeJob.setReducerClass(FlattenReducer.class);

        mergeJob.setMapOutputKeyClass(Text.class);
        mergeJob.setMapOutputValueClass(BytesWritable.class);

        mergeJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        mergeJob.setOutputKeyClass(Text.class);
        mergeJob.setOutputValueClass(BytesArrayWritable.class);
        mergeJob.setNumReduceTasks(1);

        for(FileStatus ff : fileStat) {
            if(ff.isDirectory()) {

                FileStatus[] ss = fs.listStatus(ff.getPath());

                for(FileStatus fff : ss){
                    if(fff.isFile() && fff.getPath().getName().equals("part-r-00000")){
                        logger.info(fff.getPath());
                        MultipleInputs.addInputPath(mergeJob, fff.getPath(),
                                SequenceFileInputFormat.class, IdentityMapper.class);
                    }
                }
            }
        }



        FileOutputFormat.setOutputPath(mergeJob, MERGE_OUTPUT_DIR);
        if (mergeJob.waitForCompletion(true) == false)
            return 1;

        // calculate Hamming distance using merged correct IDs file
        Job HDJob = Job.getInstance(getConf(), "Hamming distance job");
        HDJob.setJarByClass(InterHD.class);
        HDJob.setMapperClass(HDMapper.class);
        HDJob.setCombinerClass(HDCombiner.class);
        HDJob.setReducerClass(AverageHDReducer.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(IntWritable.class);
        HDJob.setMapOutputValueClass(IntPair.class);
        HDJob.setOutputKeyClass(IntWritable.class);
        HDJob.setOutputValueClass(DoubleWritable.class);
        HDJob.setNumReduceTasks(1);


        FileInputFormat.addInputPath(HDJob, HD_INPUT_FILE);
        FileOutputFormat.setOutputPath(HDJob, HD_OUTPUT_DIR);

        return HDJob.waitForCompletion(true) ? 0 : 1;

    }
}

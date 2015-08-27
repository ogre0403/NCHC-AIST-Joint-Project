package mr;

import mr.mapper.FlattenMapper;
import mr.reducer.FlattenReducer;
import mr.type.BytesArrayWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.TestTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class FlattenTest {

    private Mapper<Text, Text, Text, BytesWritable> mapper;
    private MapDriver<Text, Text, Text, BytesWritable> mapdriver;
    private Reducer<Text,BytesWritable,Text,BytesArrayWritable> reducer;
    private ReduceDriver<Text,BytesWritable,Text,BytesArrayWritable> reducerdriver;
    private MapReduceDriver<Text, Text, Text, BytesWritable, Text, BytesArrayWritable> mapReduceDriver;

    @Before
    public void init() {
        mapper = new FlattenMapper();
        mapdriver = new MapDriver<Text, Text, Text, BytesWritable>(mapper);

        reducer = new FlattenReducer();
        reducerdriver = new ReduceDriver<Text,BytesWritable,Text,BytesArrayWritable>(reducer);

        mapReduceDriver = new MapReduceDriver<Text, Text, Text, BytesWritable, Text, BytesArrayWritable>(mapper,reducer);

    }

    @Test
    public void testType(){
        String line1 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line2 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";

        byte[] ba1 = new byte[16];
        byte[] ba2 = new byte[16];

        StringTokenizer itr1 = new StringTokenizer(line1,",");
        StringTokenizer itr2 = new StringTokenizer(line2,",");
        Integer I;
        int index = 0;
        while (itr1.hasMoreTokens()) {
            I = Integer.parseInt(itr1.nextToken());
            ba1[index] = I.byteValue();
            index+=1;
        }

        index = 0;
        while (itr2.hasMoreTokens()) {
            I = Integer.parseInt(itr2.nextToken());
            ba2[index] = I.byteValue();
            index+=1;
        }
        BytesWritable bw1 = new BytesWritable(ba1);
        BytesWritable bw2 = new BytesWritable(ba2);

        BytesArrayWritable bwa = new BytesArrayWritable(new BytesWritable[]{bw1,bw2});
        Assert.assertTrue(bw1.equals(bw2));
    }

    @Test
    public void testMapper() throws IOException {

        String line1 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        byte[] ba = new byte[16];

        StringTokenizer itr = new StringTokenizer(line1,",");
        int index = 0;
        while (itr.hasMoreTokens()) {
            Integer I = Integer.parseInt(itr.nextToken());
            ba[index] = I.byteValue();
            index+=1;
        }

        BytesWritable bw = new BytesWritable(ba);
        mapdriver.withInput(new Text("1"), new Text(line1))
                .withOutput(new Text("1"), bw)
                .runTest();
    }

    @Test
    public void testReducer() throws IOException {
        String[] data = new String[]{
                "1,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
                "2,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216"
        };


        reducerdriver
                .withInput(new Text("1"), TestTool.toBytesWritableArray(data))
                .withOutput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .runTest();
    }

    @Test
    public void testMR() throws IOException, InterruptedException {

        String[] data = new String[]{
                "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
                "93,135,159,134,173,13,62,255,0,247,166,137,145,250,192,41"
        };

        String[] result1 = new String[]{
                "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
                "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216"
        };

        String[] result2 = new String[] {
                "93,135,159,134,173,13,62,255,0,247,166,137,145,250,192,41",
                "93,135,159,134,173,13,62,255,0,247,166,137,145,250,192,41"
        };


        mapReduceDriver
                .withInput(new Text("1"), new Text(data[0]))
                .withInput(new Text("1"), new Text(data[0]))
                .withInput(new Text("2"), new Text(data[1]))
                .withInput(new Text("2"), new Text(data[1]))
                .withOutput(new Text("1"),TestTool.toBytesArrayWritable(result1))
                .withOutput(new Text("2"),TestTool.toBytesArrayWritable(result2))
                .runTest();

        }

    private static KeyValueLineRecordReader getBytesRecordReader()throws IOException, InterruptedException {
        Configuration conf = new Configuration(false);
        conf.set("fs.defaultFS", "file:///");

        File testFile = new File("src/test/resources/puf_sample.txt");
        Path path = new Path(testFile.getAbsoluteFile().toURI());
        FileSplit split = new FileSplit(path, 0, testFile.length(), null);
        TaskAttemptContext context = new TaskAttemptContextImpl(conf,new TaskAttemptID());
        KeyValueLineRecordReader reader = new KeyValueLineRecordReader(context.getConfiguration());
        reader.initialize(split, context);

        return reader;
    }



}

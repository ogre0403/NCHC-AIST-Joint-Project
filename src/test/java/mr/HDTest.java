package mr;

import mr.HD.HDMapper;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HDTest {

    private Mapper<Text,BytesArrayWritable,Text, IntPair> mapper;
    private MapDriver<Text,BytesArrayWritable,Text, IntPair> mapdriver;

    @Before
    public void init() {
        mapper = new HDMapper();
        mapdriver = new MapDriver<Text,BytesArrayWritable,Text, IntPair>(mapper);

    }

    @Test
    public void testMapper() throws IOException {
        String line1 = "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line2 = "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216";

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

        IntPair ip = new IntPair();
        ip.set(0,2);
        mapdriver.withInput(new Text("1"), bwa)
                .withOutput(new Text("1"), ip)
                .runTest();
    }

}

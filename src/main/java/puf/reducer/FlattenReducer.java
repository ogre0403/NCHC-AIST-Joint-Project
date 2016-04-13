package puf.reducer;

import puf.type.BytesArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Vector;

public class FlattenReducer extends Reducer<Text,BytesWritable,Text, BytesArrayWritable> {

    /**
     * reduce() save each ByteWritable object within a collection
     * into one BytesArrayWritable object.
     */
    @Override
    protected void reduce(Text key, Iterable<BytesWritable> values, Context context)
            throws IOException, InterruptedException {
        Vector<BytesWritable> v = new Vector<BytesWritable>();

        for (BytesWritable val : values) {
            v.add(new BytesWritable(val.copyBytes()));
        }
        BytesWritable[] bwa = v.toArray(new BytesWritable[v.size()]);
        BytesArrayWritable result = new BytesArrayWritable(bwa);
        context.write(key, result);
    }
}

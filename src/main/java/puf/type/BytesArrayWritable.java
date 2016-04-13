package puf.type;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.BytesWritable;

import java.util.Arrays;

/**
 * A ArrayWritable wrapper of BytesWritable[]
 */
public class BytesArrayWritable extends ArrayWritable {

    public BytesArrayWritable() {
        super(BytesWritable.class);
    }

    public BytesArrayWritable(BytesWritable[] data) {
        super(BytesWritable.class, data);
    }

    @Override
    public String toString() {
        BytesWritable[] aa = (BytesWritable[])this.toArray();
        StringBuilder b = new StringBuilder();
        for(int i =0;i<aa.length;i++){
            b.append(aa[i].toString());
            if (i == aa.length -1)
                break;
            b.append(":");
        }
        return b.toString();
    }

    @Override
    public int hashCode() {
        return 123456;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BytesArrayWritable){
            BytesArrayWritable aa = (BytesArrayWritable)obj;
            BytesWritable[] a = (BytesWritable[])aa.toArray();
            BytesWritable[] b = (BytesWritable[])this.toArray();
            return Arrays.equals(a, b);
        }

        return false;
    }

}

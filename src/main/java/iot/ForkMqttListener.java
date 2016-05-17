package iot;

import org.apache.commons.io.input.TailerListenerAdapter;

import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ForkMqttListener extends TailerListenerAdapter {

    boolean isLocal = true;
    String ip ;
    String broker;
    String topic ;

    public ForkMqttListener(String ip, String topic, boolean isLocal){
        this.ip = ip;
        this.topic = topic;
        this.isLocal = isLocal;
        this.broker = "tcp://" + ip +":1883" ;
    }

    //TODO: distinguish many session keys among many spark streaming app
    @Override
    public void handle(String line) {
        String key = line.split(":")[1].trim();
        System.out.println(key);
        Mqtt mqtt =new Mqtt(key, broker,topic);

        try {
            mqtt.run(false);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

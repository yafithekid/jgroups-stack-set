import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReplSet<T> extends ReceiverAdapter {
    final HashSet<T> set;
    JChannel channel;
    static final String CHANNEL_NAME = "ReplSet";
    Logger logger;

    public ReplSet() throws Exception {
        set = new HashSet<T>();
        start();
        logger = Logger.getLogger(this.getClass().getName());
    }

    public boolean add(T t){
        boolean ret = contains(t);

        try {
            ReplObject<T> obj = new ReplObject<>(ReplObject.SET_ADD);
            obj.setValue(t);
            channel.send(new Message(null, null, obj));
        }
        catch (Exception e) {
            logger.log(Level.SEVERE,e.getMessage());
            ret = false;
        }

        return ret;
    }

    public boolean contains(T t){
        boolean ret;
        synchronized (set) {
            ret = set.contains(t);
        }
        return ret;
    }

    public boolean remove(T t) {
        boolean ret = contains(t);

        try {
            ReplObject<T> obj = new ReplObject<>(ReplObject.SET_REMOVE);
            obj.setValue(t);
            channel.send(new Message(null, null, obj));
        }
        catch (Exception e) {
            logger.log(Level.SEVERE,e.getMessage());
            ret = false;
        }

        return ret;
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect(CHANNEL_NAME);
        channel.getState(null, 10000);
    }

    public void receive(Message msg) {
        if (msg.getObject() instanceof ReplObject) {
            ReplObject<T> obj = (ReplObject<T>) msg.getObject();
            if (obj.getType() == ReplObject.SET_ADD) {
                synchronized (set) {
                    set.add(obj.getValue());
                }
            } else if (obj.getType() == ReplObject.SET_REMOVE) {
                synchronized (set) {
                    set.remove(obj.getValue());
                }
            }
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (set) {
            Util.objectToStream(set, new DataOutputStream(output));
        }
    }

    @SuppressWarnings("unchecked")
    public void setState(InputStream input) throws Exception {
        HashSet<T> remoteStack = (HashSet<T>) Util.objectFromStream(new DataInputStream(input));
        synchronized (set) {
            set.clear();
            set.addAll(remoteStack);
        }
    }

    public static void main(String[] args) {
        boolean stop = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = System.out;


        try {
            ReplSet<String> set = new ReplSet<>();
            do {
                System.out.print("> ");
                String line = in.readLine().toLowerCase();
                if (line.startsWith("add")) {
                    String[] splited = line.split("\\s+");
                    if (splited.length != 2) {
                        out.println("Usage: add <some_value>");
                    } else {
                        System.out.println(set.add(splited[1]));
                    }
                } else if (line.startsWith("contains")) {
                    String[] splited = line.split("\\s+");
                    if (splited.length != 2) {
                        out.println("Usage: contains <some_value>");
                    } else {
                        System.out.println(set.contains(splited[1]));
                    }
                } else if (line.startsWith("remove")) {
                    String[] splited = line.split("\\s+");
                    if (splited.length != 2) {
                        out.println("Usage: remove <some_value>");
                    } else {
                        System.out.println(set.remove(splited[1]));
                    }
                } else if (line.startsWith("exit")) {
                    stop = true;
                }
            } while (!stop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("bye");
    }
}
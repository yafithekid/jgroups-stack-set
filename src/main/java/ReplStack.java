import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ReplStack<T> extends ReceiverAdapter {
    final Stack<T> stack;
    JChannel channel;
    static final String CHANNEL_NAME = "ReplStack";

    public ReplStack() throws Exception {
        stack = new Stack<>();
        start();
    }

    public void push(T t) throws Exception {
        ReplObject<T> obj = new ReplObject<>(ReplObject.STACK_PUSH);
        obj.setValue(t);
        channel.send(new Message(null, null, obj));
    }

    public T pop() throws Exception {
        T top = this.top();
        ReplObject<T> obj = new ReplObject<>(ReplObject.STACK_POP);
        channel.send(new Message(null, null, obj));
        return top;
    }

    public T top() {
        T t;
        synchronized (stack){
            t = stack.peek();
        }
        return t;
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
            if (obj.getType() == ReplObject.STACK_PUSH) {
                synchronized (stack) {
                    stack.push(obj.getValue());
                }
            } else if (obj.getType() == ReplObject.STACK_POP) {
                synchronized (stack) {
                    stack.pop();
                }

            } else if (obj.getType() == ReplObject.STACK_TOP) {
                //do nothing
            }
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (stack) {
            Util.objectToStream(stack, new DataOutputStream(output));
        }
    }

    @SuppressWarnings("unchecked")
    public void setState(InputStream input) throws Exception {
        Stack<T> remoteStack = (Stack<T>) Util.objectFromStream(new DataInputStream(input));
        synchronized (stack) {
            stack.clear();
            stack.addAll(remoteStack);
        }
    }

    public static void main(String[] args) {
        boolean stop = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = System.out;


        try {
            ReplStack<String> stack = new ReplStack<>();
            do {
                System.out.print("> ");
                String line = in.readLine().toLowerCase();
                if (line.startsWith("push")) {
                    String[] splited = line.split("\\s+");
                    if (splited.length != 2) {
                        out.println("Usage: push <some_value>");
                    } else {
                        stack.push(splited[1]);
                    }
                } else if (line.startsWith("pop")) {
                    out.println(stack.pop());
                } else if (line.startsWith("top")) {
                    out.println(stack.top());
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

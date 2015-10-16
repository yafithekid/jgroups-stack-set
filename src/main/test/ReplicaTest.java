import org.junit.Test;

import static org.junit.Assert.*;

public class ReplicaTest {
    @Test
    public void testStack(){
        try {
            ReplStack<String> yafi = new ReplStack<>();
            ReplStack<String> calvin = new ReplStack<>();

            yafi.push("a");
            assertEquals(calvin.top(),"a");
            ReplStack<String> akua = new ReplStack<>();
            assertEquals(akua.top(), "a");
            calvin.push("b");
            assertEquals(yafi.top(),"b");
            assertEquals(yafi.pop(), "b");
            assertEquals(calvin.top(),"a");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSet(){
        try {
            ReplSet<String> yafi = new ReplSet<>();
            ReplSet<String> calvin = new ReplSet<>();

            yafi.remove("a");
            Thread.sleep(1000);
            assertEquals(yafi.contains("a"), false);
            Thread.sleep(1000);
            assertEquals(yafi.add("a"), true);
            Thread.sleep(1000);
            assertEquals(calvin.contains("a"),true);
            Thread.sleep(1000);
            ReplSet<String> akua = new ReplSet<>();
            Thread.sleep(1000);
            assertEquals(akua.add("a"), false);
            Thread.sleep(1000);
            assertEquals(calvin.remove("a"),true);
            Thread.sleep(1000);
            assertEquals(calvin.remove("a"), false);
        } catch (Exception e){

        }
    }
}

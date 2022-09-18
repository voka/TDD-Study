package e2e;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class SingleMessageListener {
    private final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(1);

    public void processMessage(String channel, String message) {
        messages.add(message);
    }

    public void receivesAMessage() throws InterruptedException {
        assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
    }
    // 검사하는 함수를 매개변수로 받아서 테스트 함
    public void receivesAMessageMatcher(Matcher<? super String> matcher ) throws InterruptedException{
        assertThat(messages.poll(5,TimeUnit.SECONDS),matcher);
    }

}

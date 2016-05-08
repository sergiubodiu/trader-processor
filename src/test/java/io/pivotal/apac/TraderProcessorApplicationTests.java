package io.pivotal.apac;

import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TraderProcessorApplicationTests.TestApplication.class)
@WebAppConfiguration
public class TraderProcessorApplicationTests {
	@Autowired
	private Processor processor;

	@Autowired
	private MessageCollector collector;

    Money amount = Money.parse("GBP1000");
    String msg = "foo";
    String id = "bar";

	@Test
	public void contextLoads() throws Exception {

        processor.input().send(MessageBuilder
				.withPayload(new Payment(msg, id, amount)).build());

        Message<?> message = collector.forChannel(processor.output()).poll(1000, TimeUnit.MILLISECONDS);
        assertThat(message, is(notNullValue()));
        FastPayment payment = (FastPayment) message.getPayload();
        assertEquals(payment.getAmount(), amount);
        assertEquals(payment.getMsg(), msg);
        assertEquals(payment.getId(), id);
    }

	@Configuration
	@Import({TestSupportBinderAutoConfiguration.class, TraderProcessorApplication.class})
	protected static class TestApplication {
	}
}

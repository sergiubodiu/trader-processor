package io.pivotal.apac;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class TraderProcessorApplicationTests {
	@Autowired
	private Processor processor;

	@Autowired
	private MessageCollector collector;

	@Test
	public void contextLoads() throws Exception {
		processor.input().send(MessageBuilder
				.withPayload(new Payment("foo", "bar", Money.parse("GBP1000"))).build());
		assertThat(collector.forChannel(processor.output()).poll(1000,
				TimeUnit.MILLISECONDS), is(notNullValue()));
	}

	@Configuration
	@Import({TestSupportBinderAutoConfiguration.class, ProcessorApplication.class})
	protected static class TestApplication {
	}
}

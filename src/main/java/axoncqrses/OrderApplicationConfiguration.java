package axoncqrses;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

@Configuration
public class OrderApplicationConfiguration {

    @Bean
    public SnapshotTriggerDefinition orderAggregateSnapshotTriggerDefinition(Snapshotter snapshotter, @Value("${axon.aggregate.order.snapshot-threshold:250}") int threshold) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, threshold);
    }

    @Bean
    InitializingBean initializingBean(ObjectMapper objectMapper) {
        return () -> objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(),
            DefaultTyping.JAVA_LANG_OBJECT
        );
    }
}

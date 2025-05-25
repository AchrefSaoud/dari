package utm.tn.dari.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class KafkaEnableConfig implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // Read the kafka.enabled property from environment
        String kafkaEnabled = context.getEnvironment().getProperty("kafka.enabled", "false");
        System.out.println("KafkaEnableConfig matches called with kafka.enabled = " + kafkaEnabled);
        return "true".equalsIgnoreCase(kafkaEnabled);
    }
}


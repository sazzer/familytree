package uk.co.grahamcox.familytree.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Clock

/**
 * The root configuration to use for the core webapp Spring context
 */
@Configuration
open class CoreContext {
    /**
     * Configure the clock to use
     * @return the clock
     */
    @Bean(name = arrayOf("clock"))
    @Profile("!test")
    open fun clock() = Clock.systemUTC()

    /**
     * Construct the Jackson Object Mapper to use
     * @return the Jackson Object Mapper
     */
    @Bean
    open fun objectMapper() : ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(Jdk8Module())
        objectMapper.registerModule(KotlinModule())

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        return objectMapper;
    }

}

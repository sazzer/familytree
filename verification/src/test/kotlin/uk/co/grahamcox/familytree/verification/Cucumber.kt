package uk.co.grahamcox.familytree.verification

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

/**
 * Cucumber Test Runner to run all of the tests
 */
@RunWith(Cucumber::class)
@CucumberOptions(tags = arrayOf("~@wip", "~@ignore"),
        format = arrayOf("pretty"),
        strict = true)
class CucumberIT {

}

/**
 * Cucumber Test Runner to run all of the tests that are still being developed
 */
@RunWith(Cucumber::class)
@CucumberOptions(tags = arrayOf("@wip", "~@ignore"),
        format = arrayOf("pretty"),
        strict = false)
class CucumberWipIT {

}

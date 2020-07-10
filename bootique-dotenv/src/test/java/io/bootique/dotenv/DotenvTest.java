package io.bootique.dotenv;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.command.CommandOutcome;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.junit5.BQModuleProviderChecker;
import io.bootique.junit5.BQTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class DotenvTest {
    final String value1 = "abc";
    final String value2 = "def";

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    @AfterEach
    void cleanup() {
        //do some complex stuff
        System.clearProperty("value1");
        System.clearProperty("value2");
    }
    
    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable(DotenvModule.class);
    }

    @Test
    public void testCommandNoResource() {
        CommandOutcome result = testFactory
            .app("--dotenv")
            .autoLoadModules()
            .createRuntime()
            .run();
            
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCommandUnknownResource() {
        CommandOutcome result = testFactory
            .app("--dotenv", "--resource", "unknown")
            .autoLoadModules()
            .createRuntime()
            .run();
            
        assertFalse(result.isSuccess());
    }

    @Test
    public void testCommandResourceSrc() {
        CommandOutcome result = testFactory
            .app("--dotenv", "--resource", "src/test/resources/env.properties")
            .autoLoadModules()
            .createRuntime()
            .run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value1"), value1);
    }

    @Test
    public void testCommandResourceClasspath() {
        CommandOutcome result = testFactory
            .app("--dotenv", "--resource", "classpath:env.properties")
            .autoLoadModules()
            .createRuntime()
            .run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value2"), value2);
    }

    @Test
    public void testCommandResourceNoOverwrite() {
        final String value1 = value2;
        
        System.setProperty("value1", value1);
            
        CommandOutcome result = testFactory
            .app("--dotenv", "--resource", "src/test/resources/env.properties")
            .autoLoadModules()
            .createRuntime()
            .run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value1"), value1);
    }
}

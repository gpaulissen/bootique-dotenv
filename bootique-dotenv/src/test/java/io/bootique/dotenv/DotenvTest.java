package io.bootique.dotenv;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.command.CommandOutcome;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQModuleProviderChecker;
import io.bootique.junit5.BQTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class DotenvTest {
    final String value1 = "abc"; // in the .env
    final String value2 = "def"; // in the .env

    @BQApp
    final static BQRuntime appFileClasspath = Bootique
        .app("--dotenv", "--file", "classpath:.env")
        .autoLoadModules()
        .createRuntime();
    
    @BQApp
    final static BQRuntime appFileSrc = Bootique
        .app("--dotenv", "--file", "src/test/resources/.env")
        .autoLoadModules()
        .createRuntime();

    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable(DotenvModule.class);
    }

    @Test
    public void testCommandNoFile() {
        BQRuntime appNoFile = Bootique
            .app("--dotenv")
            .autoLoadModules()
            .createRuntime();
    
        CommandOutcome result = appNoFile.run();
        
        assertFalse(result.isSuccess());
    }

    @Test
    public void testCommandFileSrc() {
        CommandOutcome result = appFileSrc.run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value1"), value1);
    }

    @Test
    public void testCommandFileClasspath() {
        CommandOutcome result = appFileClasspath.run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value2"), value2);
    }

    @Test
    public void testCommandFileNoOverwrite() {
        final String value1 = value2;
        
        System.setProperty("value1", value1);
            
        CommandOutcome result = appFileSrc.run();
        
        assertTrue(result.isSuccess());
        assertEquals(System.getProperty("value1"), value1);
    }
}

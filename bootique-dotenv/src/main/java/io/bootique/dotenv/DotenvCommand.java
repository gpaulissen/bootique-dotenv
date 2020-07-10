/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.dotenv;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.resource.ResourceFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;

public class DotenvCommand extends CommandWithMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(DotenvCommand.class);

    private static final String RESOURCE_OPTION = "resource";
    private static final String RESOURCE_SYSTEM_PROPERTY = "dotenv." + RESOURCE_OPTION;
    private static final String RESOURCE_DEFAULT = "classpath:env.properties";
    private static final String RESOURCE_ACTUAL = System.getProperty(RESOURCE_SYSTEM_PROPERTY, RESOURCE_DEFAULT);
    private static final String COMMAND_DESCRIPTION =
        "Reads one (optional) property resource that is specified with a '--%s' option. " +
        "The default is the value of the system property '%s' or '%s'.";
    private static final String OPTION_DESCRIPTION =
        "Set system properties by reading from a resource " +
        "(optionally) specified with a '--%s' option. The default is the value of the system property '%s' or '%s'.";

    private static OptionMetadata.Builder createResourceOption() {
        return OptionMetadata.builder(RESOURCE_OPTION)
            .description(String.format(OPTION_DESCRIPTION, RESOURCE_OPTION, RESOURCE_SYSTEM_PROPERTY, RESOURCE_DEFAULT))
            .valueOptionalWithDefault(RESOURCE_ACTUAL);
    }

    private static CommandMetadata createMetadata() {
        return CommandMetadata.builder(DotenvCommand.class)
            .description(String.format(COMMAND_DESCRIPTION, RESOURCE_OPTION, RESOURCE_SYSTEM_PROPERTY, RESOURCE_DEFAULT))
            .addOption(createResourceOption()).build();
    }

    public DotenvCommand() {
        super(createMetadata());
    }

    // @Override
    public CommandOutcome run(Cli cli) {
        List<String> resourceNames = cli.optionStrings(RESOURCE_OPTION);
        final CommandOutcome outcome;
        final String errorMsg = "Exactly one resource should be specified. Use '--%s' option to provide a resource name";
        
        if (resourceNames == null || resourceNames.size() != 1) {
            outcome = CommandOutcome.failed(1, String.format(errorMsg, RESOURCE_OPTION));
        } else {
            outcome = setSystemProperties(resourceNames.get(0));
        }
        
        LOGGER.info("Command outcome: " + outcome);

        return outcome;
    }

    public static CommandOutcome setSystemProperties(String resourceName) {
        LOGGER.info("Will read resource: " + resourceName);

        URL url = new ResourceFactory(resourceName).getUrl(); // resource may have classpath: as a prefix                
        Properties properties = new Properties();
        CommandOutcome outcome = CommandOutcome.succeeded();

        try {
            Reader reader = new InputStreamReader(url.openStream());
        
            properties.load(reader);

            properties.forEach((key, value) -> setSystemProperty(key.toString(), value.toString()));
        } catch (Exception e) {
            outcome = CommandOutcome.failed(1, e);
        }

        return outcome;
    }

    public static void setSystemProperty(String name, String value) {
        // take care not to overwrite a system property
        if (System.getProperty(name) != null) {
            LOGGER.debug("Skip setting system property '{}' since it is already set", name);
        } else if (value == null) {
            LOGGER.debug("Skip setting system property '{}' since the new value is null", name);
        } else {
            LOGGER.debug("Setting system property '{}' to '{}'", name, value);
            System.setProperty(name, value);
        }
    }
}

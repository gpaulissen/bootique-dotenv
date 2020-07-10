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

import io.bootique.BQCoreModule;
import io.bootique.BaseModule;
import io.bootique.di.Binder;

public class DotenvModule extends BaseModule {

    /*
    public static final String FILE_OPTION = "file";
    */

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(DotenvCommand.class);
        /*
            .addOption(OptionMetadata.builder(FILE_OPTION).description("Specifies the name of the properties file to read. "
                                                                       + "Used in conjunction with '--dotenv' commands. ")
                       .valueRequired("file_name")
                       .build());
        */
    }
}

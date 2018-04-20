/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.server;

import azakban.utils.Props;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.Logger;

import java.util.Arrays;

public abstract class AzkabanServer {
   public static final Logger logger = Logger.getLogger(AzkabanServer.class);
   private static Props azkabanProperties = null;

   public static Props loadProps(final String[] args){
       azkabanProperties = loadProps(args, new OptionParser());
       return azkabanProperties;
   }

   public static Props getAzkabanProperties(){
       return azkabanProperties;
   }

   public static Props loadProps(final String[] args, final OptionParser parser){
       final OptionSpec<String> configDirectory = parser.acceptsAll(
               Arrays.asList("c", "conf"), "The conf directory for Azkaban.")
               .withRequiredArg()
               .describedAs("conf")
               .ofType(String.class);

       Props azkabanSettings = null;
       final OptionSet options = parser.parse(args);
       if (options.has(configDirectory)) {
           final String path = options.valueOf(configDirectory);

       }
       return azkabanProperties;

   }

}

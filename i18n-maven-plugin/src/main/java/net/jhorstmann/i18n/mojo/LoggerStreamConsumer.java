package net.jhorstmann.i18n.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.StreamConsumer;

class LoggerStreamConsumer implements StreamConsumer {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;

    private final Log logger;
    private final int loglevel;

    public LoggerStreamConsumer(Log logger, int loglevel) {
        this.logger = logger;
        this.loglevel = loglevel;
    }

    @Override
    public void consumeLine(String line) {
        if (loglevel == DEBUG) {
            logger.debug(line);
        } else if (loglevel == INFO) {
            logger.info(line);
        } else if (loglevel == WARN) {
            logger.warn(line);
        } else if (loglevel == ERROR) {
            logger.error(line);
        }
    }
}

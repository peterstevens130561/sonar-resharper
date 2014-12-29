/*
 * SonarQube ReSharper Plugin
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.resharper.customseverities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.resharper.ReSharperPlugin;
import org.xml.sax.InputSource;



public class FileCustomSeverities extends BaseCustomSeverities {

    private static final Logger LOG = LoggerFactory.getLogger(FileCustomSeverities.class);

    /**
     * Create inputsource for a file
     * Creates inputstream, removes BOM
     * @return null in case file could not be opened, otherwise inputsource for the file
     */
    @Override
    InputSource createInputSource(String path) {
        try {
            LOG.trace("Using " + path);
            InputStream fileReader = new FileInputStream(path);
            InputStream inputStream = new BOMInputStream(fileReader);
            return new InputSource(inputStream);           
        } catch (FileNotFoundException e) {
            LOG.error("could not open " + path + "defined in " + ReSharperPlugin.CUSTOM_SEVERITIES_PATH_PROPERTY_KEY + " reason:", e);
        }
        return null;
    }

    @Override
    String getDefinitionKey() {
        return ReSharperPlugin.CUSTOM_SEVERITIES_PATH_PROPERTY_KEY;
    }

}

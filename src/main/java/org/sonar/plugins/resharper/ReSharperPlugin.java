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
package org.sonar.plugins.resharper;

import com.google.common.collect.ImmutableList;

import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public class ReSharperPlugin extends SonarPlugin {

  public static final String PROJECT_NAME_PROPERTY_KEY = "sonar.resharper.projectName";
  public static final String SOLUTION_FILE_PROPERTY_KEY = "sonar.resharper.solutionFile";
  public static final String INSPECTCODE_PATH_PROPERTY_KEY = "sonar.resharper.inspectCodePath";
  public static final String TIMEOUT_MINUTES_PROPERTY_KEY = "sonar.resharper.timeoutMinutes";
  public static final String CACHES_HOME_PROPERTY_KEY = "sonar.resharper.cacheshome";
  public static final String PROFILE_PROPERTY_KEY = "sonar.resharper.profile";
  public static final String RULES_PROFILE_PROPERTY_KEY = "sonar.resharper.rulerepository";
  public static final String CUSTOM_SEVERITIES_DEFINITON_PROPERTY_KEY = "sonar.resharper.customSeverities.definition";
  public static final String CUSTOM_SEVERITIES_PATH_PROPERTY_KEY = "sonar.resharper.customSeverities.path";


  public static final String OLD_INSTALL_DIRECTORY_KEY = "sonar.resharper.installDirectory";

  private static final String CATEGORY = "ReSharper";
  public static final String PROFILE_DEFAULT="Sonar way";

  /**
   * {@inheritDoc}
   */
  @Override
  public List getExtensions() {
    ImmutableList.Builder builder = ImmutableList.builder();

    builder.addAll(CSharpReSharperProvider.extensions());
    builder.addAll(VBNetReSharperProvider.extensions());

    builder.addAll(pluginProperties());

    return builder.build();
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(
      PropertyDefinition.builder(PROJECT_NAME_PROPERTY_KEY)
        .name("Visual Studio project name")
        .description("Example: MyLibrary")
        .category(CATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),

      PropertyDefinition.builder(SOLUTION_FILE_PROPERTY_KEY)
        .name("Solution file")
        .description("Example: C:/Projects/MyProject/MySolution.sln")
        .category(CATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),

      PropertyDefinition.builder(INSPECTCODE_PATH_PROPERTY_KEY)
        .name("Path to inspectcode.exe")
        .description("Example: C:/jetbrains-commandline-tools/inspectcode.exe")
        .defaultValue("C:/jetbrains-commandline-tools/inspectcode.exe")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .deprecatedKey(OLD_INSTALL_DIRECTORY_KEY)
        .build(),

      PropertyDefinition.builder(TIMEOUT_MINUTES_PROPERTY_KEY)
        .name("ReSharper execution timeout")
        .description("Time in minutes after which ReSharper's execution should be interrupted if not finished")
        .defaultValue("60")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.INTEGER)
        .build(),
       PropertyDefinition.builder(CACHES_HOME_PROPERTY_KEY)
        .name("caches home")
        .description("inspectcode caches")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.STRING)
        .build(),
        PropertyDefinition.builder(PROFILE_PROPERTY_KEY)
        .name("settings file to use by inspectcode")
        .description("inspectcode profile")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.STRING)
        .build(),
        PropertyDefinition.builder(CUSTOM_SEVERITIES_PATH_PROPERTY_KEY)
        .name("file with custom severities")
        .description("Absolute path to file with exported ReSharper settings: RESHARPER, Manage Options...,Import/Export Settiings, Export to file,CodeInspection")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.STRING)
        .build(),
        PropertyDefinition.builder(CUSTOM_SEVERITIES_DEFINITON_PROPERTY_KEY)
        .name("custom severities")
        .description("Add &lt;String&gt; vales from ReSharper's custom definitions (including &lt:wpf:ResourceDictionary&gt;) A restart is required to take affect.")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.STRING)
        .build(),
        PropertyDefinition.builder(RULES_PROFILE_PROPERTY_KEY)
        .name("R# profile")
        .description("Profile to which rules will be saved on restart, if profile does not exist")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.STRING)
        .build(),

      deprecatedPropertyDefinition(OLD_INSTALL_DIRECTORY_KEY));
  }

  private static PropertyDefinition deprecatedPropertyDefinition(String oldKey) {
    return PropertyDefinition
      .builder(oldKey)
      .name(oldKey)
      .description("This property is deprecated and will be removed in a future version.<br />"
        + "You should stop using it as soon as possible.<br />"
        + "Consult the migration guide for guidance.")
      .category(CATEGORY)
      .subCategory("Deprecated")
      .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
      .build();
  }

}

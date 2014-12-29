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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ReSharperRuleRepository extends RuleRepository {
  private static final Logger LOG = LoggerFactory.getLogger(ReSharperFileParser.class);
  private static final String REPOSITORY_NAME = "Resharper";

  private final XMLRuleParser xmlRuleParser;
  private final Settings settings;

  public ReSharperRuleRepository(ReSharperConfiguration fxCopConf, Settings settings,XMLRuleParser xmlRuleParser) {
    super(fxCopConf.repositoryKey(), fxCopConf.languageKey());
    setName(REPOSITORY_NAME);
    this.xmlRuleParser = xmlRuleParser;
    this.settings = settings;
  }


  public List<Rule> createRulesOld() {
    return xmlRuleParser.parse(getClass().getResourceAsStream("/org/sonar/plugins/resharper/rules.xml"));
  }
  @Override
  public List<Rule> createRules() {
      List<Rule> rules = new ArrayList<Rule>();

      // ReSharper rules
      InputStream rulesFileStream = ReSharperRuleRepository.class.getResourceAsStream("/org/sonar/plugins/resharper/DefaultRules.ReSharper");
      Reader reader = new InputStreamReader(rulesFileStream);
      ReSharperFileParser parser = new ReSharperFileParser();
      List<ReSharperRule> reSharperRules = parser.parseRules(reader);
      for(ReSharperRule rRule: reSharperRules) {
          rules.add(rRule.toSonarRule());
      }

      // Custom rules through the Web interface
      String customRules = settings.getString(ReSharperPlugin.CUSTOM_SEVERITIES_DEFINITON_PROPERTY_KEY);
      if (StringUtils.isNotBlank(customRules)) {
          try {
              String customRulesXml = "<Report><IssueTypes>" + customRules + "</IssueTypes></Report>";

              Reader customRulesReader = new StringReader(customRulesXml);
              List<ReSharperRule> customReSharperRules = parser.parseRules(customRulesReader);
              for(ReSharperRule rRule: customReSharperRules) {
                  //TODO: do i need to check if the rule has already been added?
                  rules.add(rRule.toSonarRule());
              }
          } catch (Exception ex)
          {
              LOG.warn("Error parsing ReSharper Custom Rules: " + ex.getMessage());
          }
      }

      return rules;
  }
}

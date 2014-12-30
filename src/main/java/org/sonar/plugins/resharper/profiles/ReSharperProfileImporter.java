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
package org.sonar.plugins.resharper.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.resharper.ReSharperPlugin;

import java.io.Reader;
import java.util.List;

/**
 * Class that allows to import ReSharper rule definition files into a Sonar Rule Profile
 */
public class ReSharperProfileImporter extends ProfileImporter {

    private static final Logger LOG = LoggerFactory.getLogger(ReSharperProfileImporter.class);

    private RuleFinder ruleFinder;
    private String languageKey;

    public static class CSharpRegularReSharperProfileImporter extends ReSharperProfileImporter {
        public CSharpRegularReSharperProfileImporter(RuleFinder ruleFinder) {
            super("cs", ruleFinder);
        }
    }

    public static class VbNetRegularReSharperProfileImporter extends ReSharperProfileImporter {
        public VbNetRegularReSharperProfileImporter(RuleFinder ruleFinder) {
            super("vbnet", ruleFinder);
        }
    }

    protected ReSharperProfileImporter(String languageKey, RuleFinder ruleFinder) {
        super(ReSharperPlugin.REPOSITORY_KEY + "-" + languageKey, ReSharperPlugin.REPOSITORY_NAME);
        setSupportedLanguages(languageKey);
        this.ruleFinder = ruleFinder;
        this.languageKey = languageKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
        RulesProfile profile = RulesProfile.create();
        profile.setLanguage(languageKey);

        //incoming format:
//        <IssueType Id="ClassNeverInstantiated.Global"
//           Enabled="True"
//                   Category="Potential Code Quality Issues"
//                   Description="Class is never instantiated: Non-private accessibility"
//                   Severity="SUGGESTION" />

        ReSharperFileParser parser = new ReSharperFileParser();
        parser.setMessages(messages);
        List<ReSharperRule> rules = parser.parseRules(reader);

        for (ReSharperRule reSharperRule : rules) {
            String ruleName = reSharperRule.getId();
            String ruleKey = reSharperRule.getKey();
            LOG.debug("rule " + ruleName + " key" + ruleKey );

            RuleQuery ruleQuery=RuleQuery.create().withRepositoryKey(getKey()).withKey(ruleKey);
            Rule rule = ruleFinder.find(ruleQuery);

            if (rule != null) {
                RulePriority sonarPriority = reSharperRule.getSonarPriority();
                profile.activateRule(rule, sonarPriority);
                LOG.debug("Activating profile rule " + rule.getKey() + " with priority " + sonarPriority);
            } else {
                messages.addWarningText("Unable to find rule for key '" + ruleName +"' in repository '"+getKey()+"'");
            }
        }

        return profile;
    }
    

}

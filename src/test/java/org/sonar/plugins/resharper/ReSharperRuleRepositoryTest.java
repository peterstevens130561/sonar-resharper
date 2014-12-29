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

import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.XMLRuleParser;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.fest.assertions.Assertions.assertThat;

public class ReSharperRuleRepositoryTest {

  @Test
  public void test() {
	Settings settings = mock(Settings.class);
    ReSharperRuleRepository repo = new ReSharperRuleRepository(new ReSharperConfiguration("cs", "cs-resharper"), settings,new XMLRuleParser());
    assertThat(repo.getLanguage()).isEqualTo("cs");
    assertThat(repo.getKey()).isEqualTo("cs-resharper");

    List<Rule> rules = repo.createRules();
    assertThat(rules.size()).isEqualTo(650);
    for (Rule rule : rules) {
      assertThat(rule.getKey()).isNotNull();
      assertThat(rule.getName()).isNotNull();
      assertThat(rule.getDescription()).isNotNull();
    }
  }

}

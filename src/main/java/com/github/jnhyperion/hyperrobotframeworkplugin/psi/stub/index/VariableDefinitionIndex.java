/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author max
 */
package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.index;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VariableDefinitionIndex extends StringStubIndexExtension<VariableDefinition> {

    public static final StubIndexKey<String, VariableDefinition> VARIABLE_DEFINITION_NAME = StubIndexKey.createIndexKey("robot.variableDefinition");

    private static final VariableDefinitionIndex ourInstance = new VariableDefinitionIndex();

    public static VariableDefinitionIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, VariableDefinition> getKey() {
        return VARIABLE_DEFINITION_NAME;
    }

    @SuppressWarnings("unused")
    public Collection<VariableDefinition> getVariableDefinition(@NotNull String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), name, project, scope, VariableDefinition.class);
    }
}
